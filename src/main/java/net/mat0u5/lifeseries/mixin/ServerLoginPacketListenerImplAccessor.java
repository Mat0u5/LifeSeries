package net.mat0u5.lifeseries.mixin;

import com.mojang.authlib.GameProfile;
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.network.Connection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerLoginPacketListenerImpl.class)
@MixinEnvironment(type = MixinEnvironment.Env.MAIN)
public interface ServerLoginPacketListenerImplAccessor {
    //? if <= 1.20 {
    /*@Accessor("gameProfile")
    *///?} else {
    @Accessor("authenticatedProfile")
    //?}
    GameProfile getGameProfile();

    @Accessor
    Connection getConnection();

    @Accessor
    MinecraftServer getServer();
}
