package org.screamingsandals.lib.config;

import lombok.Data;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

@Data
public abstract class SpigotConfigAdapter implements ConfigAdapter {
    private File configFile;
    private InputStream inputStream;
    private YamlConfiguration yamlConfiguration;

    public SpigotConfigAdapter(File configFile) {
        this.configFile = configFile;
    }

    public SpigotConfigAdapter(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public static SpigotConfigAdapter create(File configFile) {
        return new SpigotConfigAdapter(configFile) {
        };
    }

    public static SpigotConfigAdapter create(InputStream inputStream) {
        return new SpigotConfigAdapter(inputStream) {
        };
    }

    @Override
    public void load() {
        yamlConfiguration = new YamlConfiguration();

        try {
            if (inputStream != null) {
                yamlConfiguration.load(new InputStreamReader(inputStream));
                return;
            }

            yamlConfiguration.load(configFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Collection<String> getKeys(String path) {
        return path == null || path.isBlank() ? yamlConfiguration.getKeys(false) : Objects.requireNonNull(yamlConfiguration.getConfigurationSection(path)).getKeys(false);
    }

    @Override
    public void save() {
        try {
            yamlConfiguration.save(configFile);
            load();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object get(String key) {
        return yamlConfiguration.get(key);
    }

    @Override
    public String getString(String key) {
        return yamlConfiguration.getString(key);
    }

    @Override
    public String getString(String key, String def) {
        return yamlConfiguration.getString(key, def);
    }

    @Override
    public boolean getBoolean(String key) {
        return yamlConfiguration.getBoolean(key);
    }

    @Override
    public boolean getBoolean(String key, boolean def) {
        return yamlConfiguration.getBoolean(key, def);
    }

    @Override
    public int getInt(String key) {
        return yamlConfiguration.getInt(key);
    }

    @Override
    public int getInt(String key, int def) {
        return yamlConfiguration.getInt(key, def);
    }

    @Override
    public long getLong(String key) {
        return yamlConfiguration.getLong(key);
    }

    @Override
    public long getLong(String key, long def) {
        return yamlConfiguration.getLong(key, def);
    }

    @Override
    public double getDouble(String key) {
        return yamlConfiguration.getDouble(key);
    }

    @Override
    public double getDouble(String key, double def) {
        return yamlConfiguration.getDouble(key, def);
    }

    @Override
    public List<?> getList(String key) {
        return yamlConfiguration.getList(key);
    }

    @Override
    public List<String> getStringList(String key) {
        return yamlConfiguration.getStringList(key);
    }

    @Override
    public List<Map<?, ?>> getMapList(String key) {
        return yamlConfiguration.getMapList(key);
    }

    @Override
    public boolean isSet(String key) {
        return yamlConfiguration.isSet(key);
    }

    @Override
    public void set(String key, Object obj) {
        yamlConfiguration.set(key, obj);
    }

    public Optional<ItemStack> getStack(String key) {
        if (!isSet(key)) {
            return Optional.empty();
        }

        final ItemStack itemStack;
        final var object = get(key);

        if (object instanceof ItemStack) {
            itemStack = (ItemStack) object;
        } else {
            return Optional.empty();
        }
        return Optional.of(itemStack);
    }

    public ItemStack getStack(String key, Material def) {
        final var toReturn = getStack(key);
        if (toReturn.isEmpty()) {
            return new ItemStack(def);
        }
        return toReturn.get();
    }

    public Optional<Material> getMaterial(String key) {
        if (!isSet(key)) {
            return Optional.empty();
        }
        try {
            return Optional.of(Material.valueOf(getString(key)));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Material getMaterial(String key, Material def) {
        final var toReturn = getMaterial(key);
        if (toReturn.isEmpty()) {
            return def;
        }
        return toReturn.get();
    }
}
