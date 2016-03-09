package controllers;

import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import tools.ElapsedCpuTimer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Michal Dorko on 09/03/16.
 * BSc Final Year project
 * King's College London
 * Map-Reduce library for Genetic Algorithms
 * Licensed under the Academic Free License version 3.0
 */
public class ReplayFromFile extends AbstractPlayer {

    List<Types.ACTIONS> actions = new ArrayList<>();
    int pointer = 0;
    public ReplayFromFile(StateObservation so, ElapsedCpuTimer elapsedTimer) {
        try
        {
            BufferedReader br = new BufferedReader(new FileReader("./pacmanactions.txt"));
            br.readLine();
            //The rest are the actions:
            String line = br.readLine();
            while(line != null)
            {
                Types.ACTIONS nextAction = Types.ACTIONS.fromString(line);
                actions.add(nextAction);

                //next!
                line = br.readLine();
            }

        }catch(Exception e)
        {
            e.printStackTrace();
            System.exit(1);
        }

    }
    @Override
    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        Types.ACTIONS action = actions.get(pointer);
        pointer ++;
        return action;
    }
}
