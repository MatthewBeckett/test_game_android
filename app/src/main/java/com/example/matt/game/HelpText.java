package com.example.matt.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;

/**
 * Created by matt on 24/05/18.
 */

public class HelpText {

    boolean showMove = true;
    boolean showTurn = true;
    boolean showFire = true;

    String Tmove = "<-- move";
    String Tturn = "turn -->";
    String Tfire = "fire --->";

    float[] Pmove;
    float[] Pturn;
    float[] Pfire;

    float fontSize = 33f;

    Paint paint;


    HelpText(float PmoveX,float PmoveY,float PturnX,float PturnY,float PfireX,float PfireY){

        Pmove = new float[]{PmoveX,PmoveY};
        Pturn = new float[]{PturnX,PturnY};
        Pfire = new float[]{PfireX,PfireY};

        paint = new Paint();

        paint.setTextSize(fontSize);
        paint.setColor(Color.YELLOW);

    }

    void draw(Canvas c){

        if(showMove)
            c.drawText(Tmove,Pmove[0],Pmove[1],paint);
        if(showTurn)
            c.drawText(Tturn,Pturn[0],Pturn[1],paint);
        if(showFire)
            c.drawText(Tfire,Pfire[0],Pfire[1],paint);

    }
}
