package com.example.matt.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * Created by matt on 31/07/17.
 */

public class Bot extends GameObject {

    Rect[]  frames;
    public Rect    imgRect;
    int     frameCount = 0;
    long    animationTime = 200;
    long    last = System.currentTimeMillis();

    public Bot(Name name, Bitmap img) {
        super(name, img, 0, 0);
        rows = 1;
        columns = 4;

        width=img.getWidth()/columns;
        height=img.getHeight()/rows;

        frames = new Rect[rows+columns];
        for(int row = 0; row < rows; row++)
            for(int column = 0; column < columns;column++, frameCount++)
                frames[frameCount] = new Rect(width*column,height*row,(width*column)+width,(height*row)+height);

        imgRect = new Rect(0,0,width,height);
        body = imgRect;
        rotation = 90f;
        frameCount=0;

    }

    @Override
    void update() {
        if(targetX != 0)
            setSpeed();
        else{
            targetSpeedX=0;
            targetSpeedY=0;
        }


        x+=targetSpeedX;
        y+=targetSpeedY;

        imgRect.set(x,y,width+x,height+y);
        body = imgRect;
        if(System.currentTimeMillis() > last + animationTime){
            last = System.currentTimeMillis();
            frameCount++;
            if(frameCount >= frames.length-1)
                frameCount = 0;
        }


    }

    @Override
    void draw(Canvas c) {
        if(targetX != 0)
            rotation = -(float) Math.toDegrees(Math.atan2((x+width/2 - targetX),(y+height/2 - targetY)));
        c.save();
        c.rotate(rotation,x+(width/2.0f),y+(height/2.0f));
        c.drawBitmap(img, frames[frameCount], imgRect,null);
        c.restore();
    }
}
