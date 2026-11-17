package net.mat0u5.lifeseries.client.features.inbetween.sound;

import net.minecraft.client.sounds.AudioStream;

import javax.sound.sampled.AudioFormat;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

// Echo + Reverb
public class EchoAudioStream implements AudioStream {
	private static final float SOUND_VOLUME = 0.6f;
	private static final float ECHO_FREQUENCY = 0.42f; // Time between echoes
	private static final float ECHO_FIRST_VOLUME = 0.55f; // Volume of the first echo
	private static final float ECHO_MULTIPLIER = 0.55f; // Volume multiplier for subsequent echoes
	private static final float ECHO_LOW_PASS = 2500;
	private static final float REVERB_VOLUME = 0.08f;
	private static final float REVERB_SECONDS = 3.5f;
	private static final float HIGH_REVERB_SECONDS = 2.5f; // 3+kHz
	private static final float PRE_DELAY_MS = 30;
	private static final float REVERB_LOW_PASS = 2000;
	private static final float DIFUSION = 0.6f;
	private static final float[] DIFUSER_MS = {4.77f, 3.59f, 12.73f, 9.31f}; // JON DATTORRO 142,107,379,277 at 29761Hz
	private static final float[] LINE_MS = {31.1f, 37.3f, 41.9f, 47.7f, 53.3f, 59.9f, 67.1f, 73.3f, 79.9f, 86.3f, 92.7f, 98.9f, 105.1f, 110.3f, 116.9f, 122.3f};

	private final AudioStream sound;
	private final AudioFormat format;
	private final int channels;
	private final boolean supported;

	private final float[] echoLine;
	private int echoPos = 0;
	private final float echoLowPass;
	private float echoMuffled1 = 0;
	private float echoMuffled2 = 0;

	private final float lowPass;
	private float lowPassed = 0;
	private final float[] preDelay;
	private int preDelayPos = 0;
	private final float[][] difusers = new float[DIFUSER_MS.length][];
	private final int[] difussrPos = new int[DIFUSER_MS.length];
	private final float[][] lines = new float[LINE_MS.length][];
	private final int[] linePos = new int[LINE_MS.length];
	private final float[] lineGain = new float[LINE_MS.length];
	private final float[] lineDamping = new float[LINE_MS.length];
	private final float[] lineOut = new float[LINE_MS.length];
	private final float[] mix = new float[LINE_MS.length];

	private boolean soundEnded = false;
	private int tailFrames;
	private final int fadeFrames;

	public EchoAudioStream(AudioStream sound) {
		this.sound = sound;
		this.format = sound.getFormat();
		this.channels = format.getChannels();
		this.supported = format.getSampleSizeInBits() == 16 && channels > 0;
		float sampleRate = format.getSampleRate();
		double highAngle = 2 * Math.PI * Math.min(3000, sampleRate / 4) / sampleRate;

		this.echoLine = new float[toSamples(ECHO_FREQUENCY * 1000, sampleRate)];
		this.echoLowPass = (float) (1 - Math.exp(-2 * Math.PI * ECHO_LOW_PASS / sampleRate));

		this.lowPass = (float) (1 - Math.exp(-2 * Math.PI * REVERB_LOW_PASS / sampleRate));
		this.preDelay = new float[toSamples(PRE_DELAY_MS, sampleRate)];
		for (int i = 0; i < DIFUSER_MS.length; i++) {
			difusers[i] = new float[toSamples(DIFUSER_MS[i], sampleRate)];
		}
		for (int i = 0; i < LINE_MS.length; i++) {
			int length = toSamples(LINE_MS[i], sampleRate);
			lines[i] = new float[length];
			double gain = Math.pow(10, -3.0 * length / (sampleRate * REVERB_SECONDS));
			double highGain = Math.pow(gain, REVERB_SECONDS / HIGH_REVERB_SECONDS - 1);
			lineDamping[i] = (float) getLowPass(highGain, highAngle);
			lineGain[i] = (float) (gain * (1 - lineDamping[i]));
		}
		this.tailFrames = (int) ((2*REVERB_SECONDS) * sampleRate);
		this.fadeFrames = (int) sampleRate;
	}

	private static int toSamples(float ms, float sampleRate) {
		return Math.max(1, Math.round(ms * sampleRate / 1000));
	}

	private static double getLowPass(double gain, double angle) {
		double g2 = gain * gain;
		if (g2 >= 0.9999) return 0;
		double a = 1 - g2 * Math.cos(angle);
		return (a - Math.sqrt(a * a - (1 - g2) * (1 - g2))) / (1 - g2);
	}

	@Override
	public AudioFormat getFormat() {
		return format;
	}

	@Override
	public ByteBuffer read(int size) throws IOException {
		if (!supported) return sound.read(size);
		if (!soundEnded) {
			ByteBuffer buffer = sound.read(size);
			if (buffer != null && buffer.remaining() >= 2 * channels) {
				addEcho(buffer.asShortBuffer(), false);
				return buffer;
			}
			soundEnded = true;
		}
		int frames = Math.max(0, Math.min(size / (2 * channels), tailFrames));
		ByteBuffer buffer = ByteBuffer.allocateDirect(frames * 2 * channels).order(ByteOrder.nativeOrder());
		if (frames > 0) addEcho(buffer.asShortBuffer(), true);
		return buffer;
	}

	private void addEcho(ShortBuffer samples, boolean tail) {
		int frames = samples.remaining() / channels;
		for (int frame = 0; frame < frames; frame++) {
			int index = frame * channels;
			float input = 0;
			for (int channel = 0; channel < channels; channel++) {
				input += samples.get(index + channel);
			}
			input /= channels * 32768f;

			float echo = nextEcho(input);
			float reverb = nextReverb(input + echo) * REVERB_VOLUME;
			float added = echo * SOUND_VOLUME + reverb;
			if (tail) {
				if (tailFrames < fadeFrames) added *= tailFrames / (float) fadeFrames;
				tailFrames--;
			}
			for (int channel = 0; channel < channels; channel++) {
				float output = limit(samples.get(index + channel) / 32768f * SOUND_VOLUME + added);
				samples.put(index + channel, (short) Math.round(output * 32767));
			}
		}
	}

	private static float limit(float value) {
		float size = Math.abs(value);
		if (size <= 0.75f) return value;
		return Math.signum(value) * (0.75f + 0.25f * (float) Math.tanh((size - 0.75f) / 0.25f));
	}

	private float nextEcho(float input) {
		echoMuffled1 += (input - echoMuffled1) * echoLowPass;
		echoMuffled2 += (echoMuffled1 - echoMuffled2) * echoLowPass;

		float delayed = echoLine[echoPos];
		echoLine[echoPos] = echoMuffled2 * ECHO_FIRST_VOLUME + delayed * ECHO_MULTIPLIER;
		echoPos = (echoPos + 1) % echoLine.length;
		return delayed;
	}

	private float nextReverb(float input) {
		lowPassed += (input - lowPassed) * lowPass;
		float sample = preDelay[preDelayPos];
		preDelay[preDelayPos] = lowPassed;
		preDelayPos = (preDelayPos + 1) % preDelay.length;

		for (int i = 0; i < difusers.length; i++) {
			float delayed = difusers[i][difussrPos[i]];
			float stored = sample + DIFUSION * delayed;
			difusers[i][difussrPos[i]] = stored;
			difussrPos[i] = (difussrPos[i] + 1) % difusers[i].length;
			sample = delayed - DIFUSION * stored;
		}

		float reverb = 0;
		for (int i = 0; i < lines.length; i++) {
			float delayed = lines[i][linePos[i]];
			reverb += i % 2 == 0 ? delayed : -delayed;
			lineOut[i] = delayed * lineGain[i] + lineOut[i] * lineDamping[i];
		}
		float[] mixed = mixLines(lineOut);
		for (int i = 0; i < lines.length; i++) {
			lines[i][linePos[i]] = sample + mixed[i];
			linePos[i] = (linePos[i] + 1) % lines[i].length;
		}
		return reverb;
	}

	private float[] mixLines(float[] values) {
		System.arraycopy(values, 0, mix, 0, mix.length);
		for (int step = 1; step < mix.length; step *= 2) {
			for (int start = 0; start < mix.length; start += step * 2) {
				for (int i = start; i < start + step; i++) {
					float a = mix[i];
					float b = mix[i + step];
					mix[i] = a + b;
					mix[i + step] = a - b;
				}
			}
		}
		float scale = (float) (1 / Math.sqrt(mix.length));
		for (int i = 0; i < mix.length; i++) {
			mix[i] *= scale;
		}
		return mix;
	}

	@Override
	public void close() throws IOException {
		sound.close();
	}
}
