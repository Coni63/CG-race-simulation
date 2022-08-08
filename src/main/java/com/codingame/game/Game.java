package com.codingame.game;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.lang.model.util.ElementScanner14;

class SortbyScore implements Comparator<Player>
{
    public int compare(Player a, Player b)
    {
        return a.score() - b.score();
    }
}

class SortbyID implements Comparator<Player>
{
    public int compare(Player a, Player b)
    {
        return a.getIndex() - b.getIndex();
    }
}

public class Game {
    public int totalLaps;
    public int pitStopDuration;
    private int wearMatrix[][];
    private int[] EngineWearPerLap;
    private int[] DischargePerLap;
    private int[] fuelPerLap;
    private int[][] tyreMatrix;

    public Game(int TotalLaps, int numPlayer)
    {
        totalLaps = TotalLaps;

        // game Constants 
        wearMatrix = new int[][]{
            {15, 22, 33},  // SOFT - LOW MID HIGH
            {10, 15, 25},  // MEDIUM - LOW MID HIGH
            { 6,  9, 15},  // HARD - LOW MID HIGH
        };
        fuelPerLap = new int[]{5, 6, 7}; // L/lap based on strat
        EngineWearPerLap = new int[]{10, 15, 20}; // wear per lap based on strat LOW / MID / HIGH

        tyreMatrix = new int[][]{
            {6000, 3},  // config SOFT
            {6200, 2},  // config MEDIUM
            {6500, 1}   // config HARD
        };

    }

    public boolean playTurn(int turn, String inputAction, Player player)
    {
        /*
        * Play an action, 
        *  - PIT SOFT 30 : pit stop to refuel 30L and change tyres for soft
        *  - START MEDIUM 140 MID MID  : Start the race in medium and 140L of fuel. Engine Mode and Tyre Start set to Medium
        *  - PUSH MID MID : stay on the track - adjust just Engine Mode and Tyre Start to Medium
        * The lap time is managed by the game because of overtaking rules
        */
        player.car.lapTime = 0;

        String[] inputs = inputAction.split(" ");
        if (turn > 1 && inputs[0].equals("PIT"))
        {
            player.car.setTyre(inputs[1]);
            player.car.setEngineMode(inputs[2]);
            player.car.setTyreStrat(inputs[3]);
            player.car.setBatteryMode(inputs[4]);

            int refuelTime = player.car.addFuel(Integer.parseInt(inputs[2]));
            player.car.addLapTime(refuelTime);
            player.car.addLapTime(pitStopDuration);
        }
        else if (turn > 1 && inputs[0].equals("PUSH"))
        {
            player.car.setEngineMode(inputs[1]);
            player.car.setTyreStrat(inputs[2]);
            player.car.setBatteryMode(inputs[3]);
        }
        else if (turn == 1 && inputs[0].equals("START"))
        {
            player.car.setTyre(inputs[1]);
            player.car.addFuel(Integer.parseInt(inputs[2])); // no need to save time as it is prior race
            player.car.setEngineMode(inputs[3]);
            player.car.setTyreStrat(inputs[4]);
            player.car.setBatteryMode(inputs[5]);
        }
        else
        {
            throw new IllegalArgumentException("Invalid input");
        }
        
        int lapTime = getLapTime(player.car);
        player.car.addLapTime(lapTime); // compute current's lap time -- before adjusting the wear that is used for the next lap
        wearComponents(player.car);                    // compute wear of the current lap

        player.car.fastestLap = Math.min(player.car.fastestLap, player.car.lapTime);

        return true;
    }

    public String getInitState()
    {
        return "a";
    }

    public void onTurnEnd(List<Player> allPlayers)
    {
        // sort on the score to rank players
        Collections.sort(allPlayers, new SortbyScore());

        // compute rank
        int rank = 0;
        int lastScore = -1;
        int leaderTime = 0;
        int leaderLastLapTime = 0;

        for (Player p: allPlayers)
        {
            if (p.score() != lastScore)
            {
                rank += 1;
            }
            p.rank = rank;

            if (rank == 1)
            {
                leaderTime = p.car.totalTime;
                leaderLastLapTime = p.car.lapTime;
                p.lateness = 0f;
            }

            if (p.isActive() && rank > 1)
            {
                p.lateness = 100*(float)(p.car.totalTime - leaderTime) / leaderLastLapTime;
            }

            lastScore = p.score();
        }

        // reorder in ID to maintain input order
        Collections.sort(allPlayers, new SortbyID());
    }

    private void wearComponents(Car car)
    {
        /*
         * The wear of component is a game based bahavior. That's why it's not implemented in the car
         */
        int fuelPerLap = getFuelBurned(car);
        int tyreWearPerLap = getTyreWear(car);
        int engineWearPerLap = getEngineWear(car);
        int energyUsed = getEnergyUsed(car);
        car.consume(fuelPerLap);
        car.useBattery(energyUsed);
        car.wearTyre(tyreWearPerLap);
        car.wearEngine(engineWearPerLap);
    }

    private int getTyreWear(Car car)
    {
        /* 
        Based on the car tyreStrat and TyreCompound, the wear is different;
        */
        int tyreIndex = car.TyreCompound.ordinal();
        int stratIndex = car.tyreStrat.ordinal();
        return wearMatrix[tyreIndex][stratIndex];
    }

    private int getFuelBurned(Car car)
    {
        /* 
        Based on the engine mode, the wear is different;
        */
        int engineIndex = car.engineMode.ordinal();
        return fuelPerLap[engineIndex];
    }

    private int getEngineWear(Car car)
    {
        /* 
        Based on the engine mode, the wear is different;
        */
        int engineIndex = car.engineMode.ordinal();
        return EngineWearPerLap[engineIndex];
    }

    private int getEnergyUsed(Car car)
    {
        if (car.batteryMode == Modes.HIGH)
        {
            // return the battery level if below the required energy
            return -Math.min(-Constants.DISCHARGE_BATTERY, car.batteryLevel);
        }
        else if (car.batteryMode == Modes.LOW)
        {
            return Constants.RECHARGE_BATTERY;
        }
        else
        {
            return 0;
        }
    }

    private int getLapTime(Car car)
    {
        /* 
        Based on the engine mode, and tyre wear, the lap time is different;
        */

        int[] params = tyreMatrix[car.TyreCompound.ordinal()];
        int steps = (1000-car.tyreLife)/25;
        int baseLapTime = params[0] + params[1] * steps * steps;

        int adjustedLapTime;
        if (car.engineMode == Modes.HIGH)
        {
            adjustedLapTime = baseLapTime + (int)Math.ceil(baseLapTime * Constants.ENGINE_HIGH_BONUS / Constants.MINUTE);
        }
        else if (car.engineMode == Modes.LOW)
        {
            adjustedLapTime = baseLapTime + (int)Math.ceil(baseLapTime * Constants.ENGINE_LOW_MALUS / Constants.MINUTE);
        }
        else
        {
            adjustedLapTime = baseLapTime;
        }

        int fuelPenality = (int)Math.ceil(car.fuelLevel / 10 * Constants.FUEL_PENALITY * adjustedLapTime / Constants.MINUTE);
        
        int energyPenality;
        int energyUsed = getEnergyUsed(car);
        // if we consume the energy, 1 remove 1 time chunk
        // if we recharge, we lose haft the time only
        if (energyUsed < 0) 
        {
            energyPenality = energyUsed;
        }
        else
        {
            energyPenality = energyUsed/2;
        }

        return adjustedLapTime + fuelPenality + energyPenality;
    }
}
