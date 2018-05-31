package com.example.matt.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by matt on 24/05/18.
 */

public class Score {

    private short score = 0;
    private float[] pos;
    private String text;
    private short textSize = 33;
    private Paint paint;

    Score(float posX,float posY){
        text = Integer.toString(score);
        pos = new float[]{posX,posY};
        paint = new Paint();
        paint.setColor(Color.YELLOW);
        paint.setTextSize(textSize);
    }

    void updateScore(){
        score+=1;
        text = Integer.toString(score);
    }

    void zeroScore(){
        score = 0;
        text = Integer.toString(score);
    }

    void draw(Canvas c){
        c.drawText("Score: "+text,pos[0],pos[1],paint);
    }


}
