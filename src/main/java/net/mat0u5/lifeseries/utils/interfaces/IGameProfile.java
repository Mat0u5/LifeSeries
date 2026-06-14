package net.mat0u5.lifeseries.utils.interfaces;

import com.mojang.authlib.GameProfile;

public interface IGameProfile {
	GameProfile ls$getRealProfile();
	void ls$setRealProfile(GameProfile realProfile);
}
