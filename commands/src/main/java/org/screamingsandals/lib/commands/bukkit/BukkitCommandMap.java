package org.screamingsandals.lib.commands.bukkit;

import io.papermc.lib.PaperLib;
import lombok.Data;
import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.Plugin;
import org.screamingsandals.lib.debug.Debug;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@Data
public class BukkitCommandMap {
    private final Plugin plugin;
    private final SimpleCommandMap simpleCommandMap;

    public BukkitCommandMap(Object plugin) {
        this.plugin = (Plugin) plugin;
        this.simpleCommandMap = createCommandMapInstance();
    }

    public void registerCommand(Command command) {
        simpleCommandMap.register(command.getName(), command);
    }

    /**
     * checks if the command is already registered
     * in the Bukkit's KnownCommands map
     *
     * @param commandName name of the command
     * @return boolean
     */
    public boolean isCommandRegistered(String commandName) {
        return getRegisteredCommands().containsKey(commandName);
    }

    /**
     * removes the command from the Bukkit
     * KnownCommands map
     *
     * @param commandName name of the command
     */
    public void unregisterCommand(String commandName) {
        getRegisteredCommands().remove(commandName);
    }

    private SimpleCommandMap createCommandMapInstance() {
        final var server = plugin.getServer();
        if (PaperLib.isPaper()) {
            return (SimpleCommandMap) server.getCommandMap();
        } else {
            Debug.info("CommandMapWrapper>> Paper not found, using reflection");
            try {
                final var clazz = server.getClass();
                final var field = clazz.getDeclaredField("commandMap");
                field.setAccessible(true);

                return (SimpleCommandMap) field.get(server);
            } catch (Exception ex) {
                Debug.info("CommandMapWrapper>> Cannot get the CommandMap instance!");
                ex.printStackTrace();
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Command> getRegisteredCommands() {
        Map<String, Command> toReturn = new HashMap<>();

        if (PaperLib.isPaper()) {
            toReturn = simpleCommandMap.getKnownCommands();
        } else {
            Debug.info("CommandMapWrapper>> Paper not found, using reflection", false);
            try {
                if (simpleCommandMap != null) {
                    final Class<?> clazz;
                    Debug.info("CommandMapWrapper>> Trying to get knownCommands");
                    if (PaperLib.isVersion(13, 1)) {
                        clazz = simpleCommandMap.getClass().getSuperclass();
                    } else {
                        clazz = simpleCommandMap.getClass();
                    }

                    final Field knownCommands = clazz.getDeclaredField("knownCommands");
                    knownCommands.setAccessible(true);
                    toReturn = (Map<String, Command>) knownCommands.get(simpleCommandMap);
                }
            } catch (Exception ex) {
                Debug.info("CommandMapWrapper>> Cannot get the knownCommands Map!", false);
                ex.printStackTrace();
            }
        }
        return toReturn;
    }
}