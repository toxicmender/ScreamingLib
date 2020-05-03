package org.screamingsandals.lib.commands.test;

import org.screamingsandals.lib.commands.common.CommandBuilder;
import org.screamingsandals.lib.commands.common.RegisterCommand;
import org.screamingsandals.lib.commands.common.functions.ScreamingCommand;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;

@RegisterCommand
public class BukkitTestCommand implements ScreamingCommand {

    @Override
    public void register() {
        CommandBuilder.bukkitCommand().create("shitstorm", "my.awesome.plugin", Arrays.asList("dickfest", "shitfest"))
                .setDescription("This is a test shitstorm!")
                .setUsage("/shitstorm")
                .registerSubCommand("biatch", "my.awesome.plugin.biatch")
                .registerSubCommand("kill", "my.awesome.plugin.kill")
                .registerSubCommand("idk", "my.awesome.plugin.idk")
                .registerSubCommand("oi", "my.awesome.plugin.idk")
                .handlePlayerCommand((player, args) -> player.sendMessage("oi"))
                .handleSubPlayerCommand("biatch", (player, args) -> {
                    player.sendMessage("WHAT THE HELLL");
                    player.sendMessage(args.toString());
                })
                .handleSubPlayerCommand("kill", (player, args) -> player.setHealth(0))
                .handlePlayerTab((player, args) -> new LinkedList<>())
                .handleConsoleCommand((console, args) -> console.sendMessage("YOU CANT DO THIS."))
                .handleSubPlayerCommand("oi", ((player, args) -> player.sendMessage("asda")))
                .handleSubPlayerTab("oi", ((player, args) -> Collections.emptyList()))
                .register();
    }
}
