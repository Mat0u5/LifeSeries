package net.mat0u5.lifeseries.config;

import net.mat0u5.lifeseries.LifeSeries;
import net.mat0u5.matlib.config.SimpleConfig;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class MainConfig extends SimpleConfig {
    public MainConfig() {
        super(LifeSeries.MOD_ID+":main", "lifeseries/main", LifeSeries.MOD_ID+".properties");
        getOrCreateProperty("currentSeries", LifeSeries.DEFAULT_SEASON.getId());
    }

    public static void moveOldMainFileIfExists() {
        File newFolder = new File("./config/lifeseries/main/");
        if (!newFolder.exists()) {
            if (!newFolder.mkdirs()) {
                LifeSeries.LOGGER.error("Failed to create folder {}", newFolder);
                return;
            }
        }

        File oldFile = new File("./config/"+ LifeSeries.MOD_ID+".properties");
        if (!oldFile.exists()) return;
        File newFile = new File("./config/lifeseries/main/"+ LifeSeries.MOD_ID+".properties");
        if (newFile.exists()) {
            if (oldFile.delete()) {
                LifeSeries.LOGGER.info("Deleted old config file.");
            }
        }
        else {
            try {
                Files.move(oldFile.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                LifeSeries.LOGGER.info("Moved old config file.");
            } catch (IOException e) {
                LifeSeries.LOGGER.info("Failed to move old config file.");
            }
        }
    }
}
