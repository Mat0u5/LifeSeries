package net.mat0u5.lifeseries.client.features.inbetween.sound;

import net.minecraft.client.sounds.AudioStream;

import javax.sound.sampled.AudioFormat;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

//Empty stream - when a memory sound cannot be read/decoded
public class SilentAudioStream implements AudioStream {
	private static final AudioFormat FORMAT = new AudioFormat(44100, 16, 1, true, false);

	@Override
	public AudioFormat getFormat() {
		return FORMAT;
	}

	@Override
	public ByteBuffer read(int size) {
		return ByteBuffer.allocateDirect(0).order(ByteOrder.nativeOrder());
	}

	@Override
	public void close() {
	}
}
