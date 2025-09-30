package net.mat0u5.lifeseries.mixin.client;
//? if < 1.21.9 {
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = MinecraftServer.class)
public class EntityRenderStateMixin {
    //Empty class to avoid mixin errors
}
//?} else {

/*import net.mat0u5.lifeseries.utils.interfaces.IEntityRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.Entity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = EntityRenderState.class, priority = 1)
public class EntityRenderStateMixin implements IEntityRenderState {
    @Unique
    Entity ls$entity = null;
    @Unique
    float ls$tickProgress = 0;

    @Unique
    @Nullable
    @Override
    public Entity ls$getEntity() {
        return ls$entity;
    }

    @Unique
    @Override
    public float ls$getTickProgress() {
        return ls$tickProgress;
    }

    @Unique
    @Override
    public void ls$update(Entity entity, float tickProgress) {
        ls$entity = entity;
    }
}
*///?}