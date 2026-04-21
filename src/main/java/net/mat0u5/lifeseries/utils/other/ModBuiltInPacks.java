package net.mat0u5.lifeseries.utils.other;

import net.mat0u5.lifeseries.LifeSeries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URL;
import java.nio.file.*;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

//? if >= 1.20.5 {
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import java.util.Optional;
//?}

public final class ModBuiltInPacks {
    private static final Logger LOGGER = LoggerFactory.getLogger("LifeSeriesPackLoader");

    private record PackDef(String name, Component title, boolean required) {}

    private static final List<PackDef> CLIENT_PACKS = List.of(
            new PackDef("lifeseries",   Component.literal("Main Life Series Resourcepack"), true)
            ,new PackDef("minimal_armor", Component.literal("Minimal Armor Resourcepack"),   false)
            ,new PackDef("nicelife",     Component.literal("Nice Life Resourcepack"),        false)
    );

    private static final List<PackDef> DATA_PACKS = List.of(
            new PackDef("lifeseries_datapack", Component.literal("Life Series Datapack"), true)
            //? if <= 1.20.4 {
            /*,new PackDef("lifeseries_datapack_1.20-1.20.4", Component.literal("Life Series Datapack"), true)
            *///?} else if <= 1.20.5 {
            /*,new PackDef("lifeseries_datapack_1.20.5", Component.literal("Life Series Datapack"), true)
            *///?}
    );

    public static void loadPacks(Consumer<Pack> consumer, PackType packType) {
        List<PackDef> defs = packType == PackType.CLIENT_RESOURCES ? CLIENT_PACKS : DATA_PACKS;

        for (PackDef def : defs) {
            Pack pack = createPack(def, packType);
            if (pack != null) consumer.accept(pack);
        }
    }

    private static @Nullable Pack createPack(PackDef def, PackType packType) {
        String internalPath = "/resourcepacks/" + def.name();
        Path path = getResourceAsPath(internalPath);

        if (path == null || !Files.isDirectory(path)) {
            LOGGER.warn("[LifeSeries] Built-in pack folder not found: {}", internalPath);
            return null;
        }

        String packId = LifeSeries.MOD_ID + ":" + def.name();
        //? if <= 1.20.3 {
        /*return Pack.readMetaAndCreate(
                packId,
                def.title(),
                def.required(),
                //? if <= 1.20 {
                /^(name) -> new PathPackResources(name, path, true),
                ^///?} else {
                new PathPackResources.PathResourcesSupplier(path, true),
                //?}
                packType,
                Pack.Position.TOP,
                PackSource.BUILT_IN
        );
        *///?} else {
        PackLocationInfo locationInfo = new PackLocationInfo(
                packId,
                def.title(),
                PackSource.BUILT_IN,
                Optional.empty() // knownPackInfo
        );

        PackSelectionConfig selectionConfig = new PackSelectionConfig(
                def.required(),
                Pack.Position.TOP,
                false // fixedPosition
        );

        return Pack.readMetaAndCreate(
                locationInfo,
                new PathPackResources.PathResourcesSupplier(path),
                packType,
                selectionConfig
        );
        //?}

    }

    private static @Nullable Path getResourceAsPath(String path) {
        try {
            URL url = ModBuiltInPacks.class.getResource(path);
            if (url == null) return null;

            URI uri = url.toURI();

            if ("jar".equals(uri.getScheme())) {
                try {
                    return FileSystems.getFileSystem(uri).getPath(path);
                } catch (FileSystemNotFoundException e) {
                    FileSystems.newFileSystem(uri, Collections.emptyMap());
                    return FileSystems.getFileSystem(uri).getPath(path);
                }
            }

            return Paths.get(uri);
        } catch (Exception e) {
            LOGGER.error("Failed to resolve path for built-in pack: " + path, e);
            return null;
        }
    }

    private ModBuiltInPacks() {}
}