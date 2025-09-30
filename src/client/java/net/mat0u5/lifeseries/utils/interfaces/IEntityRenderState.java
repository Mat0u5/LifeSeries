package net.mat0u5.lifeseries.utils.interfaces;

import net.minecraft.entity.Entity;

public interface IEntityRenderState {
    Entity ls$getEntity();
    float ls$getTickProgress();
    void ls$update(Entity entity, float tickProgress);
}
