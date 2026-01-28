package net.mat0u5.lifeseries.utils.other;

import net.mat0u5.lifeseries.Main;

//? if <= 1.21.9 {
/*import net.minecraft.resources.ResourceLocation;
*///?} else {
import net.minecraft.resources.Identifier;
//?}

public class IdentifierHelper {
    //? if <= 1.20.5 {
    /*public static ResourceLocation of(String namespace, String path) {
        return new ResourceLocation(namespace, path);
    }
    public static ResourceLocation mod(String path) {
        return new ResourceLocation(Main.MOD_ID, path);
    }
    public static ResourceLocation vanilla(String path) {
        return new ResourceLocation("minecraft", path);
    }
    public static ResourceLocation parse(String string) {
        return new ResourceLocation(string);
    }
    *///?} else if <= 1.21.9 {
    /*public static ResourceLocation of(String namespace, String path) {
        return ResourceLocation.fromNamespaceAndPath(namespace, path);
    }
    public static ResourceLocation mod(String path) {
        return ResourceLocation.fromNamespaceAndPath(Main.MOD_ID, path);
    }
    public static ResourceLocation vanilla(String path) {
        return ResourceLocation.withDefaultNamespace(path);
    }
    public static ResourceLocation parse(String string) {
        return ResourceLocation.parse(string);
    }
    *///?} else {
    public static Identifier of(String namespace, String path) {
        return Identifier.fromNamespaceAndPath(namespace, path);
    }
    public static Identifier mod(String path) {
        return Identifier.fromNamespaceAndPath(Main.MOD_ID, path);
    }
    public static Identifier vanilla(String path) {
        return Identifier.withDefaultNamespace(path);
    }
    public static Identifier parse(String string) {
        return Identifier.parse(string);
    }
    //?}
}
