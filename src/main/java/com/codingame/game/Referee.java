package com.codingame.game;
import java.util.List;

import com.codingame.gameengine.core.AbstractPlayer.TimeoutException;
import com.codingame.gameengine.core.AbstractReferee;
import com.codingame.gameengine.core.MultiplayerGameManager;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.codingame.gameengine.module.tooltip.TooltipModule;
import com.google.inject.Inject;

public class Referee extends AbstractReferee {
    // Uncomment the line below and comment the line under it to create a Solo Game
    // @Inject private SoloGameManager<Player> gameManager;
    @Inject private MultiplayerGameManager<Player> gameManager;
    @Inject private GraphicEntityModule graphicEntityModule;
    @Inject private TooltipModule tooltipModule;

    private Game game;
    private ViewController viewController;

    @Override
    public void init() {
        int totalLaps = 20;

        gameManager.setFrameDuration(200);
        gameManager.setMaxTurns(totalLaps);
        gameManager.setFirstTurnMaxTime(1000);
        gameManager.setTurnMaxTime(50);

        game = new Game(totalLaps, gameManager.getPlayerCount());

        for (Player player : gameManager.getActivePlayers()) {
            player.init(totalLaps);
        }

        viewController = new ViewController(graphicEntityModule, game, tooltipModule, gameManager);
        viewController.init();
    }

    @Override
    public void gameTurn(int turn) {
        for (Player player : gameManager.getActivePlayers()) {
            if (turn == 1)
            {
                String inputStr = game.getInitState(); // we return full details only if it's me
                System.err.println(inputStr);
                player.sendInputLine(inputStr);
            }

            // return first the number of active players
            player.sendInputLine(String.valueOf(gameManager.getActivePlayers().size()));
            for (Player p : gameManager.getActivePlayers())
            {
                String inputStr = p.getState(player == p); // we return full details only if it's me
                System.err.println(inputStr);
                player.sendInputLine(inputStr);
            }
            player.execute();
        }

        for (Player player : gameManager.getActivePlayers()) {
            try {
                String actionTaken = player.getOutputs().get(0);
                System.err.println(actionTaken);
                game.playTurn(turn, actionTaken, player);
                
            } 
            catch (IllegalArgumentException e)
            {
                player.deactivate(e.getMessage());
            } 
            catch (TimeoutException e) 
            {
                player.deactivate(String.format("$%d timeout!", player.getIndex()));
            }
        }

        game.onTurnEnd(gameManager.getPlayers());

        for (Player player : gameManager.getActivePlayers()) {
            player.describe();
        }

        viewController.updateView(turn);

        // endGame()
    }

    @Override
    public void onEnd() {
        
    }

}
