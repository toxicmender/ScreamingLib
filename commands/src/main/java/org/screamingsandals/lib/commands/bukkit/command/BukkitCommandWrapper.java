package org.screamingsandals.lib.commands.bukkit.command;

import lombok.Data;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.screamingsandals.lib.commands.Commands;
import org.screamingsandals.lib.commands.common.commands.SubCommand;
import org.screamingsandals.lib.commands.common.interfaces.Completable;
import org.screamingsandals.lib.commands.common.interfaces.Executable;
import org.screamingsandals.lib.commands.common.language.CommandLanguage;
import org.screamingsandals.lib.commands.common.wrapper.CommandWrapper;

import java.util.*;

//TODO: this needs cleanup and rewrite.. -.-
@Data
public class BukkitCommandWrapper implements CommandWrapper<BukkitCommandBase, Command> {
    private final BukkitCommandBase commandBase;
    private Command commandInstance;

    public BukkitCommandWrapper(BukkitCommandBase commandBase) {
        this.commandBase = commandBase;
        commandInstance = createCommandInstance();
    }

    public Command createCommandInstance() {
        final var commandsLanguage = Commands.getInstance().getCommandLanguage();
        final var commandName = commandBase.getName();
        var aliases = commandBase.getAliases();

        if (aliases == null) {
            aliases = Collections.emptyList();
        }

        final var command = new Command(commandName, commandBase.getDescription(), commandBase.getUsage(), aliases) {
            @Override
            public boolean execute(@NotNull CommandSender commandSender, @NotNull String label, @NotNull String[] args) {
                boolean result;
                try {
                    if (commandSender instanceof Player) {
                        final var player = (Player) commandSender;
                        if (!player.hasPermission(commandBase.getPermission())) {
                            commandsLanguage.sendMessage(player, CommandLanguage.Key.NO_PERMISSIONS);
                        }
                        result = handleCommand((Player) commandSender, args, commandBase.getPlayerExecutable(), commandBase.getPlayerSubExecutors());
                    } else {
                        try {
                            result = handleCommand((ConsoleCommandSender) commandSender, args, commandBase.getConsoleExecutable(), commandBase.getConsoleSubExecutors());
                        } catch (Throwable ignored) {
                            commandsLanguage.sendMessage(commandSender, CommandLanguage.Key.NOT_FOR_CONSOLE);
                            result = true;
                        }
                    }
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                    result = false;
                }

                if (!result) {
                    commandsLanguage.sendMessage(commandSender, CommandLanguage.Key.SOMETHINGS_FUCKED); //lol
                }
                return true;
            }

            @Override
            @NotNull
            public List<String> tabComplete(@NotNull CommandSender commandSender, @NotNull String label, @NotNull String[] args) {
                try {
                    if (commandSender instanceof Player) {
                        return handleTab((Player) commandSender, args, commandBase.getPlayerCompletable(), commandBase.getPlayerSubCompletes());
                    } else {
                        return handleTab((ConsoleCommandSender) commandSender, args, commandBase.getConsoleCompletable(), commandBase.getConsoleSubCompletes());
                    }
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                    return new ArrayList<>();
                }
            }
        };

        command.setPermission(commandBase.getPermission());
        command.setAliases(aliases);
        command.setDescription(commandBase.getDescription());
        command.setUsage(commandBase.getUsage());
        return command;
    }

    private <T> boolean handleCommand(T sender, String[] args, Executable<T> command, Map<SubCommand, Executable<T>> subCommands) {
        final var commandsLanguage = Commands.getInstance().getCommandLanguage();
        final boolean areSubCommandsEmpty = commandBase.getSubCommands().isEmpty();
        final var convertedArgs = Arrays.asList(args);

        if (args.length >= 1) {
            final var subCommandName = args[0].toLowerCase();

            if (!areSubCommandsEmpty && !commandBase.isSubCommandRegistered(subCommandName)) {
                commandsLanguage.sendMessage(sender, CommandLanguage.Key.COMMAND_DOES_NOT_EXISTS);
                return true;
            }

            if (subCommands.size() < 1) {
                return false;
            }

            final var subCommand = commandBase.getSubCommand(subCommandName);
            if (areSubCommandsEmpty) {
                command.execute(sender, convertedArgs);
                return true;
            }

            if (subCommand == null) {
                return false;
            }

            if (sender instanceof Player) {
                final var player = (Player) sender;
                if (!player.hasPermission(subCommand.getPermission())) {
                    commandsLanguage.sendMessage(player, CommandLanguage.Key.NO_PERMISSIONS);
                    return true;
                }
            }

            final var subExecutor = subCommands.get(subCommand);
            if (subExecutor == null) {
                return false;
            }

            subExecutor.execute(sender, convertedArgs);
            return true;
        }

        if (command == null) {
            return false;
        }

        command.execute(sender, convertedArgs);
        return true;
    }

    private <T> List<String> handleTab(T sender, String[] args, Completable<T> complete, Map<SubCommand, Completable<T>> subCompletes) {
        final var convertedArgs = Arrays.asList(args);
        final var areSubCommandsEmpty = commandBase.getSubCommands().isEmpty();
        final var toReturn = new LinkedList<String>();

        if (args.length == 0) {
            if (complete == null) {
                return toReturn;
            }

            toReturn.addAll(complete.complete(sender, convertedArgs));
        }

        if (args.length == 1) {
            if (areSubCommandsEmpty) {
                toReturn.addAll(complete.complete(sender, convertedArgs));
                return toReturn;
            }

            final var typed = args[0].toLowerCase();

            for (var found : commandBase.getSubCommands()) {
                final var permission = found.getPermission();
                final var name = found.getName();

                if (sender instanceof Player) {
                    final var player = (Player) sender;
                    if (name.startsWith(typed)
                            && !permission.equals("")
                            && player.hasPermission(permission)) {
                        toReturn.add(name);
                    }
                } else {
                    toReturn.add(name);
                }
            }

            return toReturn;
        }

        if (args.length > 1) {
            if (commandBase.getSubCommands().isEmpty()) {
                toReturn.addAll(complete.complete(sender, convertedArgs));
                return toReturn;
            }

            final var subCommandName = args[0].toLowerCase();
            final var subCommand = commandBase.getSubCommand(subCommandName);

            if (subCommand == null) {
                return toReturn;
            }

            final var completable = subCompletes.get(subCommand);
            if (completable == null) {
                return toReturn;
            }

            toReturn.addAll(completable.complete(sender, convertedArgs));
        }

        return toReturn;
    }
}
