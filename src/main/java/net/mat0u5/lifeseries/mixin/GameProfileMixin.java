package net.mat0u5.lifeseries.mixin;

import com.mojang.authlib.GameProfile;
import net.mat0u5.lifeseries.utils.interfaces.IGameProfile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = GameProfile.class, priority = 1)
public abstract class GameProfileMixin implements IGameProfile {
	@Unique
	private GameProfile ls$realProfile = null;

	@Override
	public GameProfile ls$getRealProfile() {
		if (ls$realProfile == null)  {
			ls$realProfile = (GameProfile) (Object) this;
		}
		return ls$realProfile;
	}

	@Override
	public void ls$setRealProfile(GameProfile realProfile) {
		ls$realProfile = realProfile;
	}
}
