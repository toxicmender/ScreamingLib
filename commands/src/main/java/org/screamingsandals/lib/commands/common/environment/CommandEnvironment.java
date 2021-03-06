package org.screamingsandals.lib.commands.common.environment;

import lombok.Data;
import org.bukkit.plugin.Plugin;
import org.screamingsandals.lib.commands.bukkit.BukkitManager;
import org.screamingsandals.lib.commands.bungee.BungeeManager;
import org.screamingsandals.lib.commands.common.RegisterCommand;
import org.screamingsandals.lib.commands.common.interfaces.ScreamingCommand;
import org.screamingsandals.lib.commands.common.language.CommandLanguage;
import org.screamingsandals.lib.commands.common.language.DefaultLanguage;
import org.screamingsandals.lib.commands.common.manager.CommandManager;
import org.screamingsandals.lib.debug.Debug;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarFile;

import static org.screamingsandals.lib.reflection.Reflection.*;

@Data
public abstract class CommandEnvironment {
    private final Object plugin;
    private static CommandEnvironment instance;
    private CommandLanguage commandLanguage;
    private CommandManager commandManager;

    public CommandEnvironment(Object plugin) {
        this.plugin = plugin;
        instance = this;
    }

    public CommandEnvironment(Object plugin, CommandLanguage commandLanguage) {
        this(plugin);
        this.commandLanguage = commandLanguage;
    }

    public void load() {
        try {
            Class.forName("org.bukkit.Server");
            commandManager = new BukkitManager((Plugin) plugin);
        } catch (Throwable ignored) {
            try {
                Class.forName("net.md_5.bungee.api.plugin.PluginManager");
                commandManager = new BungeeManager((net.md_5.bungee.api.plugin.Plugin) plugin);
            } catch (Throwable ignored2) {
                Debug.warn("Your server type is not supported!", true);
            }
        }

        if (commandLanguage == null) {
            commandLanguage = new DefaultLanguage();
        }

        try {
            loadScreamingCommands(plugin.getClass());
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void destroy() {
        commandManager.destroy();
    }

    public void reload() {
        destroy();
        load();
    }

    public void loadScreamingCommands(Class<?> toLoad) throws Throwable {
        final var jarFile = new JarFile(new File(toLoad.getProtectionDomain().getCodeSource().getLocation().toURI()));
        final var packageName = toLoad.getPackage().getName().replaceAll("\\.", "/");
        final var entries = Collections.list(jarFile.entries());
        final List<Object> subCommands = new LinkedList<>();

        entries.forEach(entry -> {
            try {
                if (!entry.getName().endsWith(".class") || !entry.getName().contains(packageName)) {
                    return;
                }

                final Class<?> clazz = Class.forName(entry.getName()
                        .replace("/", ".")
                        .replace(".class", ""));

                if (!ScreamingCommand.class.isAssignableFrom(clazz) || clazz.getDeclaredAnnotation(RegisterCommand.class) == null) {
                    return;
                }

                final Constructor<?> constructor = clazz.getConstructor();
                final Object object = constructor.newInstance();

                if (clazz.getDeclaredAnnotation(RegisterCommand.class).subCommand()) {
                    subCommands.add(object);
                    return;
                }

                invoke(object);
            } catch (Exception ignored) {
            }
        });

        subCommands.forEach(this::invoke);
    }

    public static CommandEnvironment getInstance() {
        return instance;
    }

    private void invoke(Object object) {
        fastInvoke(object, "register");
    }
}
