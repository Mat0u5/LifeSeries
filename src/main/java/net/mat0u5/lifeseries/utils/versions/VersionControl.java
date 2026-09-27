package net.mat0u5.lifeseries.utils.versions;

import com.google.auto.service.AutoService;
import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.matlib.MatLib;
import net.mat0u5.matlib.services.VersionTrackedMod;
import net.mat0u5.matlib.utils.other.VersionCompatibility;

import static net.mat0u5.lifeseries.LifeSeries.MOD_VERSION;

@AutoService(VersionTrackedMod.class)
public class VersionControl implements VersionTrackedMod {
    public static boolean isDevVersion() {
        return MOD_VERSION.contains("dev") || MOD_VERSION.contains("pre") || LifeSeries.DEBUG || LifeSeries.FORCE_DEV;
    }


    @Override
    public String modId() {
        return LifeSeries.MOD_ID;
    }

    @Override
    public String modReadableName() {
        return LifeSeries.MOD_FRIENDLY_NAME;
    }

    @Override
    public String modVersion() {
        return LifeSeries.MOD_VERSION;
    }

    /*
        *     COMPATIBILITY TABLE
        *   1.3.0
        *   1.3.1       -   1.3.1.2
        *   1.3.1.3     -   1.3.1.4
        *   1.3.2
        *   1.3.2.1     -   1.3.2.2
        *   1.3.2.3
        *   1.3.2.4
        *   1.3.2.5
        *   1.3.2.6
        *   1.3.3       -   1.3.3.2
        *   1.3.4       -   1.3.4.4
        *   1.3.4.5     -   1.3.4.9
        *   1.3.4.10    -   1.3.4.19
        *   1.3.5       -   1.3.5.2
        *   1.3.5.3     -   1.3.5.7
        *   1.3.5.8     -   1.3.5.16
        *   1.3.5.17    -   1.3.5.23
        *   1.3.5.24    -   1.3.5.29
        *   1.3.6       -   1.3.6.7
        *   1.3.6.8     -   1.3.6.26
        *   1.3.6.27    -   1.3.6.37
        *   1.3.7       -   1.3.7.11
        *   1.3.7.12
        *   1.3.7.13    -   1.3.7.26
        *   1.3.7.27    -   1.4.0-pre4
        *   1.4.0       -   1.4.0.3
        *   1.4.0.4     -   1.4.0.5
        *   1.4.0.6     -   1.4.0.13
        *   1.4.0.14    -   1.4.1-pre1
        *   1.4.1       -   1.4.1.1
        *   1.4.1.9     -   1.4.1.16
        *   1.4.1.17
        *   1.4.2       -   1.4.2.12
        *   1.4.3
        *   1.4.3.1     -   1.4.3.8
        *   1.4.3.9     -   1.4.3.22
        *   1.4.3.23    -   1.4.4-pre1
        *   1.4.4       -   1.4.5-pre1
        *   1.4.5       -   1.4.5.4
        *   1.4.5.5     -   1.4.5.42
        *   1.4.5.43    -   1.5.0-pre3
        *   1.5.0
        *   1.5.0.1     -   1.5.0.15
        *   1.5.0.16
        *   1.5.0.17    -   1.5.0.21
        *   1.5.0.22    -   1.5.0.23
        *   1.5.0.24
        *   1.5.0.25    -   1.5.0.29
        *   1.5.0.30    -   1.5.1-pre1
        *   1.5.1       -   1.5.2-pre1
        *   1.5.2       -   1.5.3-pre5
        *   1.5.3       -   1.5.3.6
        *   1.5.3.8     -   1.5.3.14
        *   1.5.3.15    -   1.5.3.25
        *   1.5.3.26    -   1.5.3.30
        *   1.5.3.31    -   1.5.3.33
        *   1.5.3.34    -   1.5.4-pre1
        *   1.5.4       -   1.5.4.2
        *   1.5.4.3     -   1.5.4.13
        *   1.5.5       -   1.5.5.4
        *   1.5.5.5
        *   1.5.5.6
        *   1.5.5.7     -   1.5.5.17
        *   1.5.6-pre1  -   1.5.6-rc1
        *   1.5.6       -   1.5.7-pre1
        *   1.5.7       -   1.5.7.3
        *   1.5.7.4     -   1.5.7.8
        *   1.5.7.9
        *   1.5.7.10
        *   1.5.7.11
        *   1.5.7.12
        *   1.5.7.13    -   1.5.7.16
        *   1.5.7.17    -   1.5.8-pre1
        *   1.5.8       -   1.5.8.10
        *   1.5.8.11    -   1.5.8.18
        *   1.5.8.19    -   1.5.8.20
        *   1.5.8.21    -   1.5.8.23
        *   1.5.9-pre1
        *   1.5.9       -   *
     */

    @Override
    public VersionCompatibility clientCompatibility() {
        // This is the version that the SERVER needs to have for the current client.
        if (LifeSeries.ISOLATED_ENVIRONMENT) return VersionCompatibility.equal(MOD_VERSION);
        return VersionCompatibility.min("1.5.9");
    }

    @Override
    public VersionCompatibility serverCompatibility() {
        // This is the version that the CLIENT needs to have for the current server.
        if (LifeSeries.ISOLATED_ENVIRONMENT) return VersionCompatibility.equal(MOD_VERSION);
        return VersionCompatibility.min("1.5.9");
    }
}
