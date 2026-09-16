package net.mat0u5.lifeseries.utils.other;

import net.mat0u5.lifeseries.LifeSeries;

import net.minecraft.resources.Identifier;

public class IdentifierHelper extends net.mat0u5.matlib.util.other.IdentifierHelper {
    /**
     * Returns an identifier with the {@code lifeseries} namespace.
     */
    public static Identifier lifeseries(String path) {
        return IdentifierHelper.of(LifeSeries.MOD_ID, path);
    }
}
