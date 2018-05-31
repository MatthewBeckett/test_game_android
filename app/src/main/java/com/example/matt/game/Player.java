package com.example.matt.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Created by matt on 17/07/17.
 */

public class Player extends GameObject {

    Rect[]  frames;
    Rect    imgRect;
    int     frameCount = 0;
    public Player(Name name, Bitmap img, int x, int y) {

        super(name, img, x , y);
        rows = 2;
        columns = 4;
        speedX=0;
        speedY=0;

        rotation = -90f;

        width=img.getWidth()/columns;
        height=img.getHeight()/rows;



        frames = new Rect[rows*columns];

        for(int row = 0; row < rows; row++)
            for(int column = 0; column < columns;column++, frameCount++)
                frames[frameCount] = new Rect(width*column,height*row,(width*column)+width,(height*row)+height);

        imgRect =new Rect(x,y,x+width,y+height);

        body = new Rect(imgRect.left+imgRect.width()/4,imgRect.top+imgRect.height()/4,imgRect.right-imgRect.width()/4,imgRect.bottom-imgRect.height()/4);

        frameCount=0;
        acceleration = 1.2f;
        deceleration = 0.8f;

    }

    @Override
    void update() {

        if(targetSpeedX > 0)
            speedX = (speedX  < targetSpeedX) ? speedX+acceleration:targetSpeedX;
        else if(targetSpeedX < 0)
            speedX = (speedX  > targetSpeedX) ? speedX-acceleration:targetSpeedX;
        else
            speedX = (speedX > 0) ? speedX - deceleration: speedX + deceleration;

        if(targetSpeedY > 0)
            speedY = (speedY  < targetSpeedY) ? speedY+acceleration:targetSpeedY;
        else if(targetSpeedY < 0)
            speedY = (speedY  > targetSpeedY) ? speedY-acceleration:targetSpeedY;
        else
            speedY = (speedY > 0) ? speedY-deceleration:speedY+deceleration;


        body = new Rect(imgRect.left+imgRect.width()/4,imgRect.top+imgRect.height()/4,imgRect.right-imgRect.width()/4,imgRect.bottom-imgRect.height()/4);


    }

    @Override
    void draw(Canvas c) {
        c.save();
        c.rotate(rotation,x+(width/2.0f),y+(height/2.0f));
        c.drawBitmap(img, frames[frameCount], imgRect,null);
        c.restore();
    }



}
