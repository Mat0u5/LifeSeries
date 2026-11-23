package net.mat0u5.lifeseries.client.features.inbetween.sound;

import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.valueproviders.ConstantFloat;

import java.util.function.DoubleSupplier;

// Plays one sound file instead of a random sound from a sound event.
public class InBetweenFileSound extends InBetweenSound {
	private final Sound fileSound;
	private final WeighedSoundEvents fileEvents;

	public InBetweenFileSound(Identifier file, SoundSource source, boolean stream, int attenuationDistance, DoubleSupplier volume) {
		super(file, source, false, volume); // No statements before super(), older versions compile with Java 17/21
		//? if <= 1.20.5 {
		//this.fileSound = new Sound(file.toString(), ConstantFloat.of(1), ConstantFloat.of(1), 1, Sound.Type.FILE, stream, false, attenuationDistance);
		//?} else {
		this.fileSound = new Sound(file, ConstantFloat.of(1), ConstantFloat.of(1), 1, Sound.Type.FILE, stream, false, attenuationDistance);
		//?}
		this.fileEvents = new WeighedSoundEvents(file, null);
	}

	//? if <= 26.2 {
	/*@Override
	public WeighedSoundEvents resolve(SoundManager soundManager) {
		this.sound = fileSound;
		return fileEvents;
	}
	*///?} else {
	@Override
	public WeighedSoundEvents getOrResolve(SoundManager soundManager) {
		this.sound = fileSound;
		this.soundEvent = fileEvents;
		return fileEvents;
	}
	//?}
}
