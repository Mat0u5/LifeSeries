package net.mat0u5.lifeseries.client.utils.interfaces;

import net.minecraft.world.entity.Entity;

@Deprecated // Test performance, and make an event that only injects it sometimes.
public interface IEntityRenderState {
    Entity ls$getEntity();
    float ls$getTickProgress();
    void ls$update(Entity entity, float tickProgress);
}
