package net.mat0u5.lifeseries.client.features.inbetween.sound;

import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;

import java.util.function.DoubleSupplier;

// Always plays at the player, volume chages based on the distance to the window.
public class InBetweenWindowSound extends AbstractSoundInstance implements TickableSoundInstance {
	private final DoubleSupplier volumeSupplier;

	public InBetweenWindowSound(Identifier location, SoundSource source, boolean looping, DoubleSupplier volumeSupplier) {
		super(location, source, RandomSource.create());
		this.volumeSupplier = volumeSupplier;
		this.looping = looping;
		this.relative = true;
		this.x = 0;
		this.y = 0;
		this.z = 0;
		this.delay = 0;
		this.pitch = 1;
		this.volume = (float) volumeSupplier.getAsDouble();
		this.attenuation = SoundInstance.Attenuation.NONE;
	}

	@Override
	public void tick() {
		volume = (float) volumeSupplier.getAsDouble();
	}

	@Override
	public boolean isStopped() {
		return false;
	}

	@Override
	public boolean canStartSilent() {
		return true;
	}
}
