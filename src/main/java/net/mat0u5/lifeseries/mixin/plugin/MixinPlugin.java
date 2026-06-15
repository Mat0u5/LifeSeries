package net.mat0u5.lifeseries.mixin.plugin;

import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.lifeseries.compatibilities.CompatibilityManager;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class MixinPlugin implements IMixinConfigPlugin {

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        boolean isClient = LifeSeries.platform().isClient();
        //? if fabric {
        if (mixinClassName.contains("compat.fabricapi")) {
            boolean ret = CompatibilityManager.fabricApiLoaded();
            LifeSeries.LOGGER.info("[Life Series] Compat - Fabric API - " + ret);
            return ret;
        }
        //?}
        if (isClient) {
            if (mixinClassName.contains("client.compat.appleskin")) {
                //? if <= 1.20 {
                /*boolean ret = false;
                 *///?} else {
                boolean ret = CompatibilityManager.appleSkinLoaded();
                //?}
                LifeSeries.LOGGER.info("[Life Series] Compat - Appleskin - " + ret);
                return ret;
            }
        }
        if (mixinClassName.contains(".client.") || mixinClassName.startsWith("client.")) {
            return isClient;
        }
        return true;
    }

    @Override public void onLoad(String mixinPackage) {}
    @Override public String getRefMapperConfig() { return null; }
    @Override public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}
    @Override public List<String> getMixins() { return null; }
    @Override public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
    @Override public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
}