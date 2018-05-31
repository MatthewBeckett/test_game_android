package com.example.matt.game;

import java.util.Random;

/**
 * Created by matt on 25/01/18.
 */

public class LevelProgression {

    boolean gameRunning = false,
            canFire = true;

    private final int START_SPAWN_MIN = 100;
    private final int START_SPAWN_MAX = 600;
    private final float START_BOT_MIN_SPEED = 0.1f;
    private final float START_BOT_MAX_SPEED = 1.0f;
    private final int START_FIRE_PAUSE_LENGTH = 30;
    private final int SEED = 123456789;
    private final int MAX_BOTS = 30;
    private final float SPAWN_INCREASE = 0.90f;
    private final float SPEED_INCREASE = 1.10f;

    private int spawnMinTimer = START_SPAWN_MIN,
                spawnMaxTimer = START_SPAWN_MAX,
                nextSpawnFrame = 0,
                firePauselength = START_FIRE_PAUSE_LENGTH,
                nextFireFrame = 0,
                kills   = 0,
                frame   = 0;




    float   botMinSpeed = START_BOT_MIN_SPEED,
            botMaxSpeed = START_BOT_MAX_SPEED,
            totalSeconds= 0f;

    long    startTime,
            finishTime;

    boolean playerDead = false;

    private Random rand;

    LevelProgression(){
        rand = new Random(SEED);
    }

    void update(){
        frame++;

    }

    void    startGame(){
        startTime = System.nanoTime();
    }


    void    finishGame(){
        finishTime = System.nanoTime();
        totalSeconds = (finishTime - startTime) / 1000000000;
    }

    public boolean spawnBot() {
        if (frame >= nextSpawnFrame && !playerDead){
            nextSpawnFrame  = frame + spawnMaxTimer - rand.nextInt(spawnMinTimer);
            return true;
        }
        else
            return false;
    }

    public void killedBot(){
        kills++;
        if(firePauselength > 2){
            firePauselength--;
            nextFireFrame = frame;
            spawnMinTimer *=SPAWN_INCREASE;
            spawnMaxTimer *=SPAWN_INCREASE;
            botMinSpeed*=SPEED_INCREASE;
            botMaxSpeed*=SPEED_INCREASE;
        }
    }

    public boolean fire(){
        if(frame >= nextFireFrame && !playerDead){
            nextFireFrame = frame + firePauselength;
            return true;
        }
        else return false;
    }
}
