package main;

import core.ArcadeMachine;
import ontology.Types;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Michal Dorko on 11/11/15.
 * BSc Final Year project
 * King's College London
 * Map-Reduce library for Genetic Algorithms
 * Licensed under the Academic Free License version 3.0
 */
public class MapReduceBinaryGAMain {

    public static List<Types.ACTIONS> actionSequence;

    public static void main(String[] args) {

        String gamesPath = "examples/gridphysics/";
        String games[] = new String[]{};
        String generateLevelPath = "examples/generatedLevels/";

        //Training Set 1 (2015; CIG 2014)
      //  games = new String[]{"aliens", "boulderdash", "butterflies", "chase", "frogs",
      //          "missilecommand", "portals", "sokoban", "survivezombies", "zelda"}; // aliens, frogs, zombies, zelda have spawning sprites which causes error

        //Training Set 2 (2015; Validation CIG 2014)
        games = new String[]{"camelRace", "digdug", "firestorms", "infection", "firecaster",
              "overload", "pacman", "seaquest", "whackamole", "eggomania"};

        //Training Set 3 (2015)
        //games = new String[]{"bait", "boloadventures", "brainman", "chipschallenge",  "modality",
        //                              "painter", "realportals", "realsokoban", "thecitadel", "zenpuzzle"};

        //Training Set 4 (Validation GECCO 2015, Test CIG 2014)
        //games = new String[]{"roguelike", "surround", "catapults", "plants", "plaqueattack",
        //        "jaws", "labyrinth", "boulderchase", "escape", "lemmings"};


        //Training Set 5 (Validation CIG 2015, Test GECCO 2015)
        //games = new String[]{ "solarfox", "defender", "enemycitadel", "crossfire", "lasers",
        //                               "sheriff", "chopper", "superman", "waitforbreakfast", "cakybaky"};

        //Training Set 6 (Validation CEEC 2015)
        //games = new String[]{"lasers2", "hungrybirds" ,"cookmepasta", "factorymanager", "raceBet2",
        //        "intersection", "blacksmoke", "iceandfire", "gymkhana", "tercio"};

        boolean visuals = false;
        String recordActionsFile = "./actions.txt"; //where to record the actions executed. null if not to save.
        int seed = new Random().nextInt();

        //Game and level to play
        int gameIdx = 6;
        int levelIdx = 0; //level names from 0 to 4 (game_lvlN.txt).
        String game = gamesPath + games[gameIdx] + ".txt";
        String level1 = gamesPath + games[gameIdx] + "_lvl" + levelIdx +".txt";

        String recordLevelFile = generateLevelPath +"geneticLevelGenerator/" + games[gameIdx] + "_lvl0.txt";

        String gameAgent = "gameproblem.GameAgent";
        actionSequence = new ArrayList<>();
        ArcadeMachine.runOneGame(game, level1, visuals, gameAgent, recordActionsFile, seed);
        for(Types.ACTIONS action : actionSequence) {
            System.out.println(action + "\n");
        }
    }

    public static String getStringFromByteArray(Byte[] chromosome) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < chromosome.length; i++) {
            stringBuilder.append(chromosome[i]);
        }
        return stringBuilder.toString();
    }

    public static void addActionSequence(List<Types.ACTIONS> actions) {
        actionSequence.addAll(actions);
    }

}
