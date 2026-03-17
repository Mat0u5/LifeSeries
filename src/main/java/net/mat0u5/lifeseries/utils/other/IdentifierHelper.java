package net.mat0u5.lifeseries.utils.other;

import net.mat0u5.lifeseries.Main;

import net.minecraft.resources.Identifier;

public class IdentifierHelper {
    //? if <= 1.20.5 {
    /*public static Identifier of(String namespace, String path) {
        return new Identifier(namespace, path);
    }
    public static Identifier mod(String path) {
        return new Identifier(Main.MOD_ID, path);
    }
    public static Identifier vanilla(String path) {
        return new Identifier("minecraft", path);
    }
    public static Identifier parse(String string) {
        return new Identifier(string);
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
