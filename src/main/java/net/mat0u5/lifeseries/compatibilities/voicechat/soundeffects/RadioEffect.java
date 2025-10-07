package net.mat0u5.lifeseries.compatibilities.voicechat.soundeffects;

public class RadioEffect {

    private static double SAMPLE_RATE = 16000.0;

    public static short[] applyEffect(short[] audio) {
        short[] modified1 = lowerQuality(audio);
        short[] modified2 = midrangeBoostEQ(modified1, 1500, 7000);
        short[] modified3 = midrangeBoostEQ(modified2, 400, 600);
        short[] modified4 = normalizeGain(modified3, 10);
        return modified4;
    }

    public static short[] lowerQuality(short[] audio) {
        int reduction = 4;

        short[] result = new short[audio.length];
        for (int i = 0; i < audio.length; i += reduction) {
            int sum = 0;
            for (int j = 0; j < reduction; j++) {
                sum += audio[i + j];
            }
            int avg = sum / reduction;
            for (int j = 0; j < reduction; j++) {
                result[i + j] = (short) avg;
            }
        }

        return result;
    }

    private static short[] normalizeGain(short[] input, double gainFactor) {
        short[] output = new short[input.length];
        for (int i = 0; i < input.length; i++) {
            double amplified = input[i] * gainFactor;
            output[i] = (short) Math.max(Math.min(amplified, Short.MAX_VALUE), Short.MIN_VALUE);
        }
        return output;
    }

    private static short[] midrangeBoostEQ(short[] input, int lowCutHz, int highCutHz) {
        short[] highPassed = highPassFilter(input, lowCutHz);
        return lowPassFilter(highPassed, highCutHz);
    }

    private static short[] highPassFilter(short[] input, int cutoff) {
        short[] output = new short[input.length];
        double rc = 1.0 / (2 * Math.PI * cutoff);
        double dt = 1.0 / SAMPLE_RATE;
        double alpha = rc / (rc + dt);
        double prevOut = 0;
        double prevIn = input[0];

        for (int i = 0; i < input.length; i++) {
            double x = input[i];
            double y = alpha * (prevOut + x - prevIn);
            prevOut = y;
            prevIn = x;
            output[i] = (short) Math.max(Math.min(y, Short.MAX_VALUE), Short.MIN_VALUE);
        }
        return output;
    }

    private static short[] lowPassFilter(short[] input, int cutoff) {
        short[] output = new short[input.length];
        double rc = 1.0 / (2 * Math.PI * cutoff);
        double dt = 1.0 / SAMPLE_RATE;
        double alpha = dt / (rc + dt);
        double y = input[0];

        for (int i = 0; i < input.length; i++) {
            y += alpha * (input[i] - y);
            output[i] = (short) Math.max(Math.min(y, Short.MAX_VALUE), Short.MIN_VALUE);
        }
        return output;
    }
}