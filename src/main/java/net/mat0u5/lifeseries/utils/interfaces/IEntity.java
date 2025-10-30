package net.mat0u5.lifeseries.utils.interfaces;

import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public interface IEntity {
    String error = "This method should be overridden in the mixin";

    default World ls$getEntityWorld()      { throw new UnsupportedOperationException(error); }
    default Vec3d ls$getEntityPos()        { throw new UnsupportedOperationException(error); }
}
