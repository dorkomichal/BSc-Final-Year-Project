package problemsdesc;

import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import tools.ElapsedCpuTimer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Michal Dorko on 01/02/16.
 * BSc Final Year project
 * King's College London
 * Map-Reduce library for Genetic Algorithms
 * Licensed under the Academic Free License version 3.0
 */
public class GameAgent extends AbstractPlayer {

    List<String> stringEncodedActions;

    public GameAgent(StateObservation stateObs) {
        encodeActions(stateObs.getAvailableActions());
    }

    private void encodeActions(List<Types.ACTIONS> actions) {
        this.stringEncodedActions = new ArrayList<>();
        for (Types.ACTIONS action: actions) {
            switch (action) {
                case ACTION_NIL: stringEncodedActions.add("n");
                    break;
                case ACTION_UP: stringEncodedActions.add("u");
                    break;
                case ACTION_DOWN: stringEncodedActions.add("d");
                    break;
                case ACTION_RIGHT: stringEncodedActions.add("r");
                    break;
                case ACTION_LEFT: stringEncodedActions.add("l");
                    break;
                case ACTION_ESCAPE: stringEncodedActions.add("e");
                    break;
                case ACTION_USE: stringEncodedActions.add("s");
            }
        }
    }

    @Override
    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        return null;
    }
}
