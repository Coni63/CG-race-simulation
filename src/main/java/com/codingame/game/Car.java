package com.codingame.game;

import javax.lang.model.util.ElementScanner14;

enum Modes
{
    LOW,
    MID,
    HIGH,
}

enum Tyres
{
    SOFT, 
    MEDIUM,
    HARD,
}

public class Car {
    public Modes engineMode;
    public Modes batteryMode;
    public Modes tyreStrat;
    public Tyres TyreCompound;
    public int fuelLevel;
    public int engineLife;
    public int tyreLife;
    public int totalTime;
    public int lapTime;
    public int fastestLap;
    public int batteryLevel;

    public Car()
    {
        fuelLevel = 0;
        engineLife = Constants.INIT_ENGINE_LIFE;
        engineMode = Modes.MID;
        tyreStrat = Modes.MID;
        batteryMode = Modes.MID;
        TyreCompound = Tyres.MEDIUM;
        tyreLife = Constants.INIT_TYRE_LIFE;
        totalTime = 0;
        fastestLap = Integer.MAX_VALUE;
        batteryLevel = 500;
    }

    public void describe()
    {
        System.err.println(String.format("Last lap Time %s s with %s L/%s L", lapTime, fuelLevel, Constants.MAX_FUEL));
        System.err.println(String.format("Tyres %s is %s/1000 and running in %s mode", TyreCompound, tyreLife, tyreStrat));
        System.err.println(String.format("Engine life is %s/1000 and running in %s mode", engineLife, engineMode));
        System.err.println(String.format("Battery level is %s/1000 and running in %s mode", batteryLevel, batteryMode));
    }

    public void addLapTime(int time)
    {
        totalTime += time;
        lapTime += time;
    }

    public void setTyre(String category)
    {
        tyreLife = Constants.INIT_TYRE_LIFE; // as we change tyre, the life goes back to full life
        switch (category)
        {
            case "SOFT": 
                TyreCompound = Tyres.SOFT;
                break;
            case "MEDIUM": 
                TyreCompound = Tyres.MEDIUM;
                break;
            case "HARD": 
                TyreCompound = Tyres.HARD;
                break;
            default: throw new IllegalArgumentException("Invalid tyre input");
        }
    }

    public void setEngineMode(String category)
    {
        switch (category)
        {
            case "HIGH": 
                engineMode = Modes.HIGH;
                break;
            case "MID": 
                engineMode = Modes.MID;
                break;
            case "LOW": 
                engineMode = Modes.LOW;
                break;
            default: throw new IllegalArgumentException("Invalid engine mode input");
        }
    }

    public void setTyreStrat(String category)
    {
        switch (category)
        {
            case "HIGH": 
                tyreStrat = Modes.HIGH;
                break;
            case "MID": 
                tyreStrat = Modes.MID;
                break;
            case "LOW": 
                tyreStrat = Modes.LOW;
                break;
            default: throw new IllegalArgumentException("Invalid engine mode input");
        }
    }

    public void setBatteryMode(String category)
    {
        switch (category)
        {
            case "HIGH": 
                batteryMode = Modes.HIGH;
                break;
            case "MID": 
                batteryMode = Modes.MID;
                break;
            case "LOW": 
                batteryMode = Modes.LOW;
                break;
            default: throw new IllegalArgumentException("Invalid battery mode input");
        }
    }

    public int addFuel(int addedQuantity)
    {
        if (fuelLevel + addedQuantity > Constants.MAX_FUEL)
        {
            throw new IllegalArgumentException("You requested too much fuel");
        }
        if (fuelLevel + addedQuantity <= 0)
        {
            throw new IllegalArgumentException("Not enought fuel to finish the lap");
        }

        fuelLevel += addedQuantity;

        return (int)Math.ceil(addedQuantity / Constants.FUEL_VPS); // time to refuel
    }

    public void consume(int fuelPerLap)
    {
        addFuel(-fuelPerLap); // consume fuel is equivalent to add a negative amout of fuel until 0
    }

    public void useBattery(int quantity)
    {
        /*
         * Handle the energy consumption
         - negative value to empty the battery
         - positive value to recharge
        */
        batteryLevel = Math.max(0, Math.min(1000, batteryLevel + quantity));
    }

    public void wearTyre(int tyreWearPerLap)
    {
        tyreLife -= tyreWearPerLap;
        if (tyreLife <= 0)
        {
            throw new IllegalArgumentException("You destroyed your tyres, you are out of the race");
        }
    }

    public void wearEngine(int engineWearPerLap)
    {
        engineLife -= engineWearPerLap;
        if (engineLife <= 0)
        {
            throw new IllegalArgumentException("You destroyed your engine");
        }
    }
}
