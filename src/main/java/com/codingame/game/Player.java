package com.codingame.game;
import com.codingame.gameengine.core.AbstractMultiplayerPlayer;

// Uncomment the line below and comment the line under it to create a Solo Game
// public class Player extends AbstractSoloPlayer {
public class Player extends AbstractMultiplayerPlayer {
    public Car car;
    public int lapRemaining;
    public int rank;
    public float lateness;

    public void init(int totalLaps)
    {
        car = new Car();
        lapRemaining = totalLaps;
        rank = 1;
        lateness = 0f;
    }

    @Override
    public int getExpectedOutputLines() {
        return 1;
    }

    public void describe()
    {
        System.err.println(String.format("\nPlayer %s - score %s (rank %s) - late %s", this.getIndex(), this.score(), rank, lateness));
        this.car.describe();
    }

    public int score()
    {
        if (this.isActive())
        {
            return car.totalTime;
        }
        else
        {
            return lapRemaining * 1000000;
        }
    }

    public String getState(boolean fullDetails)
    {
        StringBuilder str = new StringBuilder();
        str.append(String.valueOf(this.index));
        str.append(" " + rank);
        str.append(" " + lateness);
        str.append(" " + car.TyreCompound);

        if (fullDetails)
        {
            str.append(" " + car.tyreLife);
            str.append(" " + car.tyreStrat);
            str.append(" " + car.batteryMode);
            str.append(" " + car.batteryLevel);
            str.append(" " + car.engineMode);
            str.append(" " + car.engineLife);
        } 
        else
        {
            str.append(" -1");
            str.append(" -1");
            str.append(" -1");
            str.append(" -1");
            str.append(" -1");
            str.append(" -1");
        }

        return str.toString();
    }
}
