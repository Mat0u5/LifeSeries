package net.mat0u5.lifeseries.mixin;

import net.mat0u5.lifeseries.Main;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.RecipeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

import static net.mat0u5.lifeseries.Main.blacklist;

//? if <= 1.21 {
/*import net.minecraft.resources.ResourceLocation;
import com.google.gson.JsonElement;
import java.util.ArrayList;
import java.util.Map;
@Mixin(value = RecipeManager.class, priority = 1)
public class RecipeManagerMixin {

    @Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V", at = @At("HEAD"))
    private void applyMixin(Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager, ProfilerFiller profiler, CallbackInfo info) {
        if (!Main.isLogicalSide() || Main.modDisabled()) return;
        if (blacklist == null) return;
        if (blacklist.loadedListItemIdentifier == null)  {
            blacklist.getItemBlacklist();
        }
        if (blacklist.loadedRecipeBlacklist == null)  {
            blacklist.getRecipeBlacklist();
        }
        if (blacklist.loadedListItemIdentifier.isEmpty() && blacklist.loadedRecipeBlacklist.isEmpty()) return;

        List<ResourceLocation> toRemove = new ArrayList<>();

        for (ResourceLocation identifier : map.keySet()) {
            if (blacklist.loadedListItemIdentifier.contains(identifier) || blacklist.loadedRecipeBlacklist.contains(identifier)) {
                toRemove.add(identifier);
            }
        }
        toRemove.forEach(map::remove);
    }

}
*///?} else {
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeMap;
import org.spongepowered.asm.mixin.Shadow;
@Mixin(RecipeManager.class)
public abstract class RecipeManagerMixin {

    @Shadow
    private RecipeMap recipes;

    @Inject(method = "apply(Lnet/minecraft/world/item/crafting/RecipeMap;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V", at = @At("HEAD"), cancellable = true)
    private void applyMixin(RecipeMap preparedRecipes, ResourceManager resourceManager, ProfilerFiller profiler, CallbackInfo ci) {
        if (!Main.isLogicalSide() || Main.modDisabled()) return;
        if (blacklist == null) return;
        if (blacklist.loadedListItemIdentifier == null)  {
            blacklist.getItemBlacklist();
        }
        if (blacklist.loadedRecipeBlacklist == null)  {
            blacklist.getRecipeBlacklist();
        }
        if (blacklist.loadedListItemIdentifier.isEmpty() && blacklist.loadedRecipeBlacklist.isEmpty()) return;

        List<RecipeHolder<?>> filteredRecipes = preparedRecipes.values().stream()
                //? if <= 1.21.9 {
                /*.filter(recipe -> !blacklist.loadedListItemIdentifier.contains(recipe.id().location()) && !blacklist.loadedRecipeBlacklist.contains(recipe.id().location()))
                *///?} else {
                .filter(recipe -> !blacklist.loadedListItemIdentifier.contains(recipe.id().identifier()) && !blacklist.loadedRecipeBlacklist.contains(recipe.id().identifier()))
                //?}
                .toList();

        this.recipes = RecipeMap.create(filteredRecipes);

        // Log the updated recipe count
        Main.LOGGER.info("Loaded {} recipes after filtering", filteredRecipes.size());

        // Cancel further processing of the original method
        ci.cancel();
    }

}
//?}