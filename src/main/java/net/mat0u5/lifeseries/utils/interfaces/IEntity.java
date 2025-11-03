package net.mat0u5.lifeseries.utils.interfaces;

import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public interface IEntity {
    String error = "This method should be overridden in the mixin";

    default Level ls$getEntityWorld()      { throw new UnsupportedOperationException(error); }
    default Vec3 ls$getEntityPos()        { throw new UnsupportedOperationException(error); }
}
