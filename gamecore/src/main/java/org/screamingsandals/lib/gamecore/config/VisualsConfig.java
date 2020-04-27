package org.screamingsandals.lib.gamecore.config;

import org.screamingsandals.lib.config.DefaultConfigBuilder;
import org.screamingsandals.lib.config.SpigotConfigAdapter;

import java.io.File;
import java.util.List;

public abstract class VisualsConfig extends SpigotConfigAdapter {
  /*
    Yes, we do support placeholders. PlaceholderAPI TODO - not needed on HoZ

    Available BedWars placeholders:
    %player%, %team%, %remainingGameTime%, %elapsedGameTime%, %game-state% more to add.
     */

    //SCOREBOARDS
    public static String PATH_SCOREBOARDS_ENABLED = "scoreboards.enabled";
    public static String PATH_SCOREBOARDS_NAME = "scoreboards.name";
    public static String PATH_SCOREBOARDS_CONTENT_LOBBY = "scoreboards.content.lobby";
    public static String PATH_SCOREBOARDS_CONTENT_GAME = "scoreboards.content.game";
    public static String PATH_SCOREBOARDS_CONTENT_DEATHMATCH = "scoreboards.content.deathmatch";
    public static String PATH_SCOREBOARDS_CONTENT_END_GAME = "scoreboards.content.end-game";
    //SCOREBOARDS

    //BOSSBARS
    public static String PATH_BOSSBARS_ENABLED = "bossbars.enabled";
    public static String PATH_BOSSBARS_CONTENT_LOBBY = "bossbars.content.lobby";
    public static String PATH_BOSSBARS_CONTENT_STARTING = "bossbars.content.starting";
    public static String PATH_BOSSBARS_CONTENT_IN_GAME = "bossbars.content.in-game";
    public static String PATH_BOSSBARS_CONTENT_DEATHMATCH = "bossbars.content.deathmatch";
    public static String PATH_BOSSBARS_CONTENT_END_GAME = "bossbars.content.end-game";

    public static String PATH_BOSSBARS_COLOR_LOBBY = "bossbars.color.lobby";
    public static String PATH_BOSSBARS_COLOR_STARTING = "bossbars.color.starting";
    public static String PATH_BOSSBARS_COLOR_IN_GAME = "bossbars.color.in-game";
    public static String PATH_BOSSBARS_COLOR_DEATHMATCH = "bossbars.color.deathmatch";
    public static String PATH_BOSSBARS_COLOR_END_GAME = "bossbars.color.end-game";
    //BOSSBARS

    //TITLES
    public static String PATH_TITLES_LOBBY_FADE_IN = "titles.lobby.fade-in";
    public static String PATH_TITLES_LOBBY_FADE_OUT = "titles.lobby.fade-out";
    public static String PATH_TITLES_LOBBY_STAY = "titles.lobby.stay";
    public static String PATH_TITLES_LOBBY_TITLE = "titles.lobby.title";
    public static String PATH_TITLES_LOBBY_SUBTITLE = "titles.lobby.subtitle";

    public static String PATH_TITLES_IN_GAME_FADE_IN = "titles.in-game.fade-in";
    public static String PATH_TITLES_IN_GAME_FADE_OUT = "titles.in-game.fade-out";
    public static String PATH_TITLES_IN_GAME_STAY = "titles.in-game.stay";
    public static String PATH_TITLES_IN_GAME_TITLE = "titles.in-game.title";
    public static String PATH_TITLES_IN_GAME_SUBTITLE = "titles.in-game.subtitle";

    public static String PATH_TITLES_DEATHMATCH_FADE_IN = "titles.deathmatch.fade-in";
    public static String PATH_TITLES_DEATHMATCH_FADE_OUT = "titles.deathmatch.fade-out";
    public static String PATH_TITLES_DEATHMATCH_STAY = "titles.deathmatch.stay";
    public static String PATH_TITLES_DEATHMATCH_TITLE = "titles.deathmatch.title";
    public static String PATH_TITLES_DEATHMATCH_SUBTITLE = "titles.deathmatch.subtitle";

    public static String PATH_TITLES_END_GAME_FADE_IN = "titles.end-game.fade-in";
    public static String PATH_TITLES_END_GAME_FADE_OUT = "titles.end-game.fade-out";
    public static String PATH_TITLES_END_GAME_STAY = "titles.end-game.stay";
    public static String PATH_TITLES_END_GAME_TITLE = "titles.end-game.title";
    public static String PATH_TITLES_END_GAME_SUBTITLE = "titles.end-game.subtitle";
    //TITLES

    /**
     * @param configFile configuration file
     */
    public VisualsConfig(File configFile) {
        super(configFile);
    }


    public void checkDefaults() {
        DefaultConfigBuilder.start(this)
                .put(PATH_SCOREBOARDS_ENABLED, true)
                .put(PATH_SCOREBOARDS_NAME, "&aYourFuckingGame!")
                //if line contains playersToStart, remove that line if playersToStart == 0 and replace with STARTING!
                .put(PATH_SCOREBOARDS_CONTENT_LOBBY, List.of(" ", "&eMap: &a%game%", "&fPlayers: &2%players%&f/&2%maxplayers%", "&4Need more %playersToStart% players!"))
                //if line contains %teams%, delete it and put teams after it
                .put(PATH_SCOREBOARDS_CONTENT_GAME, List.of(" ", "&eMap: &a%game%", " ", "%teams%", "My ass is amazing!"))
                .put(PATH_SCOREBOARDS_CONTENT_DEATHMATCH, List.of(" ", "&c&lDEATHMATCH", " ", "%teams%", "My ass is amazing!"))
                .put(PATH_SCOREBOARDS_CONTENT_END_GAME, List.of(" ", "%isWinner%", " ", "some ", "content")) //replace %isWinner% with "You won" or "You lost"
                .end();
    }
}