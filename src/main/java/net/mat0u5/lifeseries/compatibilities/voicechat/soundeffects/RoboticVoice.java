package net.mat0u5.lifeseries.compatibilities.voicechat.soundeffects;

import java.util.*;

public class RoboticVoice {
    private static double SAMPLE_RATE = 16000.0;
    private static int HISTORY_SIZE = 5;
    private static long MAX_HISTORY_AGE_MS = 300;

    private static final Map<UUID, Deque<VoiceStats>> speakerHistory = new HashMap<>();
    private static final Map<UUID, Double> speakerPhase = new HashMap<>();

    private static class VoiceStats {
        double frequency;
        double amplitude;
        long timestamp;

        VoiceStats(double frequency, double amplitude, long timestamp) {
            this.frequency = frequency;
            this.amplitude = amplitude;
            this.timestamp = timestamp;
        }
    }

    public static short[] applyEffect(UUID uuid, short[] input) {
        long now = System.currentTimeMillis();
        double amplitude = estimateAmplitude(input);
        double freq = estimateDominantFrequency(input);
        if (amplitude < 0.001)  {
            return new short[input.length];
        }

        Deque<VoiceStats> history = speakerHistory.computeIfAbsent(uuid, k -> new ArrayDeque<>());
        if (history.size() >= HISTORY_SIZE) {
            history.pollFirst();
        }
        history.addLast(new VoiceStats(freq, amplitude, now));

        history.removeIf(stat -> now - stat.timestamp > MAX_HISTORY_AGE_MS);
        if (history.isEmpty()) return new short[input.length]; // Silence if no recent stats

        double weightedFreq = 0.0;
        double weightedAmp = 0.0;
        int totalWeight = 0;
        int weight = 1;

        List<VoiceStats> recentStats = new ArrayList<>(history);
        for (int i = 0; i < recentStats.size(); i++) {
            VoiceStats stat = recentStats.get(i);
            int w = i + 1;
            weightedFreq += stat.frequency * w;
            weightedAmp += stat.amplitude * w;
            totalWeight += w;
        }

        double avgFreq = weightedFreq / totalWeight;
        double avgAmp = weightedAmp / totalWeight;

        return synthHarmonicTone(uuid, input.length, avgFreq, avgAmp * 2);
    }

    private static double estimateAmplitude(short[] input) {
        double sum = 0;
        for (short s : input) sum += Math.abs(s);
        return (sum / input.length) / 32768.0;
    }

    private static double estimateDominantFrequency(short[] input) {
        int zeroCrossings = 0;
        for (int i = 1; i < input.length; i++) {
            if ((input[i - 1] < 0 && input[i] >= 0) || (input[i - 1] > 0 && input[i] <= 0)) {
                zeroCrossings++;
            }
        }
        double durationSec = input.length / SAMPLE_RATE;
        double estFreq = (zeroCrossings / 2.0) / durationSec;
        return Math.max(20, Math.min(estFreq, 400));
    }

    private static short[] synthHarmonicTone(UUID uuid, int length, double baseFreq, double amplitude) {
        short[] output = new short[length];
        double phase = speakerPhase.getOrDefault(uuid, 0.0);
        double modulationDepth = 0.05;
        double lfoFrequency = 5.0;

        for (int i = 0; i < length; i++) {
            double t = i / SAMPLE_RATE;
            double lfo = Math.sin(2 * Math.PI * lfoFrequency * t) * modulationDepth;
            double modulatedFreq = baseFreq * (1 + lfo);
            double phaseIncrement = 2 * Math.PI * modulatedFreq / SAMPLE_RATE;
            phase += phaseIncrement;

            double sample = Math.sin(phase) * 0.6
                    + Math.sin(2 * phase) * 0.3
                    + Math.sin(3 * phase) * 0.1;

            sample *= amplitude;
            sample = Math.max(-1.0, Math.min(1.0, sample));
            output[i] = (short) (sample * 32767);
        }

        speakerPhase.put(uuid, phase % (2 * Math.PI));
        return output;
    }
}
