package org.screamingsandals.lib.commands.bukkit;

import lombok.Data;
import org.bukkit.plugin.Plugin;
import org.screamingsandals.lib.commands.bukkit.command.BukkitCommandWrapper;
import org.screamingsandals.lib.commands.common.manager.CommandManager;
import org.screamingsandals.lib.commands.common.wrapper.CommandWrapper;
import org.screamingsandals.lib.debug.Debug;

import java.util.HashMap;
import java.util.Map;

@Data
public class BukkitManager implements CommandManager {
    private final Plugin plugin;
    private final BukkitCommandMap bukkitCommandMap;
    private final Map<String, BukkitCommandWrapper> commands = new HashMap<>();

    public BukkitManager(Plugin plugin) {
        this.plugin = plugin;
        this.bukkitCommandMap = new BukkitCommandMap(plugin);
    }

    @Override
    public void destroy() {
        commands.keySet().forEach(bukkitCommandMap::unregisterCommand);
        commands.clear();
    }

    @Override
    public void registerCommand(CommandWrapper<?, ?> commandWrapper) {
        final var bukkitCommandWrapper = (BukkitCommandWrapper) commandWrapper;
        final var bukkitCommandBase = bukkitCommandWrapper.getCommandBase();
        final var commandName = bukkitCommandBase.getName();

        if (isCommandRegistered(commandName) || commands.containsKey(commandName)) {
            Debug.info("Command " + commandName + " is already registered!", true);
            return;
        }

        bukkitCommandMap.registerCommand(bukkitCommandWrapper.getCommandInstance());
        commands.put(commandName, bukkitCommandWrapper);
    }

    @Override
    public boolean isCommandRegistered(String commandName) {
        return bukkitCommandMap.isCommandRegistered(commandName);
    }

    @Override
    public void unregisterCommand(String commandName) {
        bukkitCommandMap.unregisterCommand(commandName);
        commands.remove(commandName);
    }

    @Override
    public CommandWrapper<?, ?> getRegisteredCommand(String commandName) {
        return commands.get(commandName);
    }
}
