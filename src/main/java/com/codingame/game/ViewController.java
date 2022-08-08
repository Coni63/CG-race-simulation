package com.codingame.game;

import com.codingame.gameengine.core.MultiplayerGameManager;
import com.codingame.gameengine.module.entities.*;
import com.codingame.gameengine.module.tooltip.TooltipModule;

import java.io.Console;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ViewController {
    private GraphicEntityModule module;
    private MultiplayerGameManager<Player> gameManager;
    private Game game;
    private TooltipModule tooltipModule;
    private Text Timer;
    private Text CheckpointTarget;
    private ArrayList<Circle> checkpoints = new ArrayList<>();
    private int currentCheck = 0;
    private Group car;
    private Group exhaust;
    private Group carPositionGroup;
    private Circle previousLocation;
    private Sprite arrow;
    private Text message;
    private Line targetLine;

    private Sprite[] cars;
    private Sprite[] tyres;
    private Sprite[] avatars;
    private Circle[] engineMaps;
    private Circle[] tyreStrats;
    private RoundedRectangle[] backgroundTank;
    private RoundedRectangle[] frontendTank;
    private Text[] lapTimes;
    private Text[] intervals;
    private Line[] laplines;

    private Sprite startLine;
    private Sprite finishLine;

    private int numPlayer;

    public final int Width = 1920;
    public final int Height = 1080;
    private Random rnd = new Random();


    public ViewController(GraphicEntityModule module, Game game, TooltipModule tooltipModule, MultiplayerGameManager<Player> gameManager){
        this.module = module;
        this.game = game;
        this.tooltipModule = tooltipModule;
        this.gameManager = gameManager;
    }

    public void init(){

        module.createSprite()
        .setImage("back.jpg")
        .setBaseWidth(Width)
        .setBaseHeight(Height*2)
        .setZIndex(-10);
        
        startLine = module.createSprite().setImage("startline.png")
                .setAnchorY(0.5)
                .setAnchorX(0.5)
                .setY(Height/2)
                .setX(Width/2 + 150)
                .setZIndex(99)
                .setAlpha(1.0)
                .setScale(0.15)
                .setVisible(true)
                .setRotation(Math.PI*0.5);

        finishLine = module.createSprite().setImage("startline.png")
                .setAnchorY(0.5)
                .setAnchorX(0.5)
                .setY(Height/2)
                .setX(Width + 150)
                .setZIndex(99)
                .setAlpha(1.0)
                .setScale(0.15)
                .setVisible(true)
                .setRotation(Math.PI*0.5);

        laplines = new Line[game.totalLaps-1];
        for (int i = 0; i < game.totalLaps-1; i++)
        {
            laplines[i] = module.createLine()
                .setX(Width + 150)
                .setY(Height/2 - 300)
                .setX2(Width + 150)
                .setY2(Height/2 + 300)
                .setLineWidth(5.0)
                .setVisible(true);
        }

        
        numPlayer = this.gameManager.getPlayerCount();
        int height = 150 * (numPlayer - 1);

        cars = new Sprite[numPlayer];
        tyres = new Sprite[numPlayer];
        avatars = new Sprite[numPlayer];
        engineMaps = new Circle[numPlayer];
        tyreStrats = new Circle[numPlayer];
        backgroundTank = new RoundedRectangle[numPlayer];
        frontendTank = new RoundedRectangle[numPlayer];
        lapTimes = new Text[numPlayer];
        intervals = new Text[numPlayer];

        for (int i = 0; i < numPlayer; i++)
        {
            int y = (Height - height)/2 + 150 * i;
            cars[i] = module.createSprite().setImage("car.png")
                .setAnchorY(0.5)
                .setAnchorX(0.5)
                .setY(y)
                .setX(Width/2)
                .setZIndex(100)
                .setAlpha(1.0)
                .setScale(0.4)
                .setVisible(true);
        
            // tyre icon
            tyres[i] = module.createSprite()
                .setImage("soft.png")
                .setAnchorY(0.5)
                .setAnchorX(0.5)
                .setY(y)
                .setX(Width/2 + 500)
                .setZIndex(100)
                .setBaseHeight(80)
                .setBaseWidth(80)
                .setVisible(false);

            // player's avatar
            avatars[i] = module.createSprite()
                .setImage(gameManager.getPlayer(i).getAvatarToken())  //getNicknameToken() -> str
                .setX(100)
                .setY(y)
                .setAnchorX(0.5)
                .setAnchorY(0.5)
                .setZIndex(0)
                .setAlpha(0.5)
                .setBaseHeight(100)
                .setBaseWidth(100);

            // engineMaps's circle
            engineMaps[i] = module.createCircle()
                .setRadius(40)
                .setFillAlpha(0.8)
                .setX(Width/2 + 300)
                .setY(y)
                .setLineColor(0x000000)
                .setFillColor(0xff0000, Curve.NONE)
                .setLineAlpha(0.1)
                .setAlpha(1.0)
                .setLineWidth(5)
                .setVisible(true);

            // tyreStrats's circle
            tyreStrats[i] = module.createCircle()
                .setRadius(40)
                .setFillAlpha(0.8)
                .setX(Width/2 + 400)
                .setY(y)
                .setLineColor(0x000000)
                .setFillColor(0xff0000, Curve.NONE)
                .setLineAlpha(0.1)
                .setAlpha(1.0)
                .setLineWidth(5)
                .setVisible(true);

            // Fuel tank background
            backgroundTank[i] = module.createRoundedRectangle()
                .setLineColor(0xff0000)
                .setLineAlpha(1.0)
                .setFillColor(0xffffff)
                .setFillAlpha(1.0)
                .setX(Width/2 + 600)
                .setY(y - 40)
                .setWidth(160)
                .setHeight(80)
                .setLineWidth(6)
                .setRadius(16)
                .setZIndex(100)
                .setVisible(true);
            
            // Fuel tank level
            frontendTank[i] = module.createRoundedRectangle()
                .setLineColor(0xff0000)
                .setLineAlpha(0)
                .setFillColor(0x00ff00)
                .setFillAlpha(1.0)
                .setX(Width/2 + 600 + 3)
                .setY(y - 37)
                .setWidth(0)
                .setHeight(74)
                .setLineWidth(0)
                .setZIndex(100)
                .setRadius(10)
                .setVisible(true);

            // lap time text
            lapTimes[i] = module.createText("")
                .setX(Width/2 - 700)
                .setY(y)
                .setAnchor(0)
                .setFontSize(30)
                .setZIndex(100)
                .setFillColor(0x000000)
                .setVisible(false);

            intervals[i] = module.createText("")
                .setX(Width/2 - 500)
                .setY(y)
                .setAnchor(0)
                .setFontSize(30)
                .setZIndex(100)
                .setFillColor(0x000000)
                .setVisible(false);
        }
    }

    public void updateView(int turn)
    {
        for (int i = 0; i < numPlayer; i++)
        {
            Player p = gameManager.getPlayer(i);
            if (p.isActive())
            {
                cars[i].setX(Width/2 - (int)(10 * p.lateness));
            }
            else
            {
                cars[i].setX(-200);
            }

            int y = cars[i].getY();
            drawTyres(p, i, y);
            drawEngineMap(p, i, y);
            drawTyreStrat(p, i, y);
            drawFuel(p, i, y);
            drawLapTime(p, i, y);
        }

        moveLines(turn);
    }

    private void drawTyres(Player player, int idx, int y)
    {
        String tyreImage = new String[]{"soft.png", "medium.png", "hard.png"}[player.car.TyreCompound.ordinal()];

        tyres[idx]
        .setImage(tyreImage)
        .setVisible(true);
    }


    private void drawEngineMap(Player player, int idx, int y)
    {
        int color = new int[]{0xff0000, 0xFF7F00, 0x00ff00}[player.car.engineMode.ordinal()];

        engineMaps[idx]
        .setFillColor(color, Curve.NONE)
        .setVisible(true);
    }

    private void drawTyreStrat(Player player, int idx, int y)
    {
        int color = new int[]{0xff0000, 0xFF7F00, 0x00ff00}[player.car.tyreStrat.ordinal()];

        tyreStrats[idx]
        .setFillColor(color, Curve.NONE)
        .setVisible(true);
    }

    private void drawFuel(Player player, int idx, int y)
    {
        int w = 160 * player.car.fuelLevel / Constants.MAX_FUEL;
        frontendTank[idx].setWidth(w);
    }

    private void drawLapTime(Player player, int idx, int y)
    {
        lapTimes[idx].setText(String.format("%.2f s", (float)player.car.lapTime/100)).setVisible(true);
        intervals[idx].setText(String.format("%.0f", player.lateness)+"%").setVisible(true);
    }

    private void moveLines(int idx)
    {
        if (idx == 1)
        {
            startLine
            .setX(-150);
        }
        else
        {
            laplines[idx-2]
            .setX(-150)
            .setX2(-150);
        }

        if (idx > laplines.length)
        {
            finishLine
            .setX(Width/2 + 150);
        }
        else
        {
            laplines[idx-1]
                .setX(Width/2+120)
                .setX2(Width/2+120);
        }

    }

}
        