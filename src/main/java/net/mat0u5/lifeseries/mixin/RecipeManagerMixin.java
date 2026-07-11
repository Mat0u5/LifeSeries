package net.mat0u5.lifeseries.mixin;

import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.mat0u5.lifeseries.LifeSeries;
import net.minecraft.world.item.crafting.RecipeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import static net.mat0u5.lifeseries.LifeSeries.blacklist;

//? if <= 1.21 {
/*import net.minecraft.resources.Identifier;
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import com.google.gson.JsonElement;
import java.util.ArrayList;
import java.util.Map;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.List;
@Mixin(value = RecipeManager.class, priority = 1)
@MixinEnvironment(type = MixinEnvironment.Env.MAIN)
public class RecipeManagerMixin {

    @Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V", at = @At("HEAD"))
    private void applyMixin(Map<Identifier, JsonElement> map, ResourceManager resourceManager, ProfilerFiller profiler, CallbackInfo info) {
        if (LifeSeries.isClientOrDisabled()) return;
        if (blacklist == null) return;
        if (blacklist.loadedListItemIdentifier == null)  {
            blacklist.getItemBlacklist();
        }
        if (blacklist.loadedRecipeBlacklist == null)  {
            blacklist.getRecipeBlacklist();
        }
        if (blacklist.loadedListItemIdentifier.isEmpty() && blacklist.loadedRecipeBlacklist.isEmpty()) return;

        List<Identifier> toRemove = new ArrayList<>();

        for (Identifier identifier : map.keySet()) {
            if (blacklist.loadedListItemIdentifier.contains(identifier) || blacklist.loadedRecipeBlacklist.contains(identifier)) {
                toRemove.add(identifier);
            }
        }
        toRemove.forEach(map::remove);
    }

}
*///?} else if <= 26.2 {
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeMap;
import org.spongepowered.asm.mixin.Shadow;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.List;
@Mixin(RecipeManager.class)
@MixinEnvironment(type = MixinEnvironment.Env.MAIN)
public abstract class RecipeManagerMixin {

    @Shadow
    private RecipeMap recipes;

    @Inject(method = "apply(Lnet/minecraft/world/item/crafting/RecipeMap;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V", at = @At("HEAD"), cancellable = true)
    private void applyMixin(RecipeMap preparedRecipes, ResourceManager resourceManager, ProfilerFiller profiler, CallbackInfo ci) {
        if (LifeSeries.isClientOrDisabled()) return;
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
        LifeSeries.LOGGER.info("Loaded {} recipes after filtering", filteredRecipes.size());

        // Cancel further processing of the original method
        ci.cancel();
    }

}
//?} else {
/*import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeMap;
import dev.kikugie.fletching_table.annotation.MixinEnvironment;

import java.util.stream.Stream;

@Mixin(RecipeManager.class)
@MixinEnvironment(type = MixinEnvironment.Env.MAIN)
public abstract class RecipeManagerMixin {

	@SuppressWarnings("unchecked")
	@WrapOperation(method = "<init>(Lnet/minecraft/core/HolderLookup$Provider;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/RecipeMap;create(Lnet/minecraft/core/HolderLookup;)Lnet/minecraft/world/item/crafting/RecipeMap;"))
	private RecipeMap lifeseries$filterRecipes(HolderLookup<Recipe<?>> recipeLookup, Operation<RecipeMap> original) {
		if (LifeSeries.isClientOrDisabled()) return original.call(recipeLookup);
		if (blacklist == null) return original.call(recipeLookup);

		if (blacklist.loadedListItemIdentifier == null) {
			blacklist.getItemBlacklist();
		}
		if (blacklist.loadedRecipeBlacklist == null) {
			blacklist.getRecipeBlacklist();
		}
		if (blacklist.loadedListItemIdentifier.isEmpty() && blacklist.loadedRecipeBlacklist.isEmpty()) {
			return original.call(recipeLookup);
		}

		HolderLookup.RegistryLookup<Recipe<?>> parentLookup = (HolderLookup.RegistryLookup<Recipe<?>>) recipeLookup;
		HolderLookup.RegistryLookup<Recipe<?>> filteredLookup = new HolderLookup.RegistryLookup.Delegate<>() {
			@Override
			public HolderLookup.RegistryLookup<Recipe<?>> parent() {
				return parentLookup;
			}

			@Override
			public Stream<Holder.Reference<Recipe<?>>> listElements() {
				return parentLookup.listElements().filter(holder -> {
					Identifier id = holder.key().identifier();
					return !blacklist.loadedListItemIdentifier.contains(id)
							&& !blacklist.loadedRecipeBlacklist.contains(id);
				});
			}
		};

		RecipeMap filtered = original.call(filteredLookup);
		LifeSeries.LOGGER.info("Loaded {} recipes after filtering", filtered.values().size());
		return filtered;
	}
}
*///?}