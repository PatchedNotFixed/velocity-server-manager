package de.gunnablescum.velocityservermanager.utils;

import org.yaml.snakeyaml.Yaml;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class Config {

    private Map<String, Object> configurationValues;
    private final Path configPath;
    private final String fileName;

    public Config(Path dataDirectory, String fileName) throws IOException {
        this.fileName = fileName;
        this.configPath = dataDirectory.resolve(fileName);
        loadConfiguration();
    }

    private void loadConfiguration() throws IOException {
        if(!Files.exists(configPath)) {
            Files.createDirectories(configPath.getParent());
            try(InputStream in = getClass().getClassLoader().getResourceAsStream(fileName)) {
                Files.copy(in, configPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Yaml yaml = new Yaml();
        try(InputStream in = Files.newInputStream(configPath)) {
            configurationValues = yaml.load(in);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T get(String path, @Nullable T defaultValue) {
        String[] parts = path.split("\\.");
        Object current = configurationValues;

        for (String part : parts) {
            if (current instanceof Map) {
                current = ((Map<String, Object>) current).get(part);
                if (current == null) {
                    return defaultValue;
                }
            } else {
                return defaultValue;
            }
        }

        return (T) current;
    }

    public String getString(String path, String defaultValue) {
        return get(path, defaultValue);
    }

    public int getInt(String path, int defaultValue) {
        Number value = get(path, defaultValue);
        if (value != null) {
            return value.intValue();
        }
        return defaultValue;
    }

}
