package net.mat0u5.lifeseries.mixin;
//? if <= 1.20.5 {
/*import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.mat0u5.lifeseries.LifeSeries;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.stream.Stream;

// Make <= 1.20.6 read from data/<namespace>/structure to make it compatible with later versions.
@Mixin(value = StructureTemplateManager.class)
@MixinEnvironment(type = MixinEnvironment.Env.MAIN)
public class StructureTemplateManagerMixin {
    @Unique
    private static final FileToIdConverter ls$CONVERTER = new FileToIdConverter("structure", ".nbt");

    @Shadow
    private ResourceManager resourceManager;

    @Shadow
    private StructureTemplate readStructure(InputStream inputStream) throws IOException {
        throw new AssertionError();
    }

    @Inject(method = "loadFromResource", at = @At("RETURN"), cancellable = true)
    private void loadFromNewFolder(Identifier id, CallbackInfoReturnable<Optional<StructureTemplate>> cir) {
        if (cir.getReturnValue().isPresent()) return;
        Optional<Resource> resource = resourceManager.getResource(ls$CONVERTER.idToFile(id));
        if (resource.isEmpty()) return;
        try (InputStream stream = resource.get().open()) {
            cir.setReturnValue(Optional.of(readStructure(stream)));
        } catch (Throwable e) {
            LifeSeries.LOGGER.error("Couldn't load structure {}", id, e);
        }
    }

    @Inject(method = "listResources", at = @At("RETURN"), cancellable = true)
    private void listNewFolder(CallbackInfoReturnable<Stream<Identifier>> cir) {
        Stream<Identifier> newFolder = ls$CONVERTER.listMatchingResources(resourceManager).keySet().stream().map(ls$CONVERTER::fileToId);
        cir.setReturnValue(Stream.concat(cir.getReturnValue(), newFolder));
    }
}
*///?} else {
import dev.kikugie.fletching_table.annotation.MixinEnvironment;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = StructureTemplateManager.class)
@MixinEnvironment(type = MixinEnvironment.Env.MAIN)
public class StructureTemplateManagerMixin {
    //Empty class to avoid mixin errors
	//1.21+ reads from data/<namespace>/structure by default
}
//?}
