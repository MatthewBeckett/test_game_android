package com.example.matt.game;

import android.content.Context;
import android.graphics.Canvas;
import android.view.SurfaceHolder;

/**
 * Created by matt on 17/07/17.
 */

public class GameLoop extends Thread {
    private GameView view;
    public boolean running = true;
    int screenWidth = 0,screenHeight = 0;

    byte FPS = 25;
    long frame_time = 1000000000 / FPS;
    long last = 0;

    Context context;
    SurfaceHolder surfaceHolder;

    public GameLoop(GameView view)
    {
        this.view =view;
    }
    public void setRunning() {
        running = true;
    }

    public void pause(){
        running = false;
        while (true){
            try{this.join();}
            catch (InterruptedException e){
                e.printStackTrace();
            }
            break;
        }
    }


    public void run() {
        while (running) {
            Canvas c = null;

            if (System.nanoTime() > last + frame_time) {
                last = System.nanoTime();
                view.updateAll();

                try {
                    c = view.getHolder().lockCanvas();

                    synchronized (surfaceHolder) {
                        view.drawAll(c);
                    }
                } finally {
                    if (c != null) {
                        view.getHolder().unlockCanvasAndPost(c);
                        screenWidth = view.getWidth();
                        screenHeight = view.getHeight();
                    }
                }
            }
        }
    }
}
