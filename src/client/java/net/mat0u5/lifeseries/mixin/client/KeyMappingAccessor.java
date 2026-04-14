package net.mat0u5.lifeseries.mixin.client;

import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;
//? if <= 1.21.6 {
/*import org.spongepowered.asm.mixin.gen.Accessor;
import java.util.Map;
*///?}

@Mixin(KeyMapping.class)
public interface KeyMappingAccessor {
    //? if <= 1.21.6 {
    /*@Accessor("CATEGORY_SORT_ORDER")
    static Map<String, Integer> ls$getCategorySortOrder() {
        throw new AssertionError();
    }
    *///?}
}