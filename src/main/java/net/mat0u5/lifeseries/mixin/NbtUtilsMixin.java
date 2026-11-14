package net.mat0u5.lifeseries.mixin;
//? if <= 26.2 {
/*import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

// <= 26.2 stores structure block pallete as {"id", "properties"} instead of {"Name", "Properties"}
@Mixin(value = NbtUtils.class)
@MixinEnvironment(type = MixinEnvironment.Env.MAIN)
public class NbtUtilsMixin {
    @ModifyVariable(method = "readBlockState", at = @At("HEAD"), argsOnly = true)
    private static CompoundTag readNewBlockStateFormat(CompoundTag tag) {
        if (tag == null || tag.contains("Name") || !tag.contains("id")) return tag;
        CompoundTag converted = tag.copy();
        converted.put("Name", converted.get("id"));
        converted.remove("id");
        if (converted.contains("properties")) {
            converted.put("Properties", converted.get("properties"));
            converted.remove("properties");
        }
        return converted;
    }
}
*///?} else {
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.nbt.NbtUtils;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = NbtUtils.class)
@MixinEnvironment(type = MixinEnvironment.Env.MAIN)
public class NbtUtilsMixin {
    //Empty class to avoid mixin errors
	//26.3+ reads its own format natively
}
//?}
