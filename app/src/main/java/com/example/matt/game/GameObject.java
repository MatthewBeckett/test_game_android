package com.example.matt.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * Created by matt on 17/07/17.
 */

public abstract class GameObject {

    Name        name;
    Bitmap      img;
    Rect        body;

    GameObject  owner = null;
    Boolean     solid,
                remove=false;

    int         health,
                hp,width,height,x,y,targetX,targetY;

    float       maxSpeed,speedX=0,speedY=0,rotation,targetSpeedX,targetSpeedY,acceleration,deceleration;

    int rows,columns,row=0,column=0;

    public GameObject(Name name, Bitmap img,int x,int y){
        this.name   = name;
        this.img    = img;
        this.x = x;
        this.y = y;
    }

    abstract void update();

    abstract void draw(Canvas c);

    void setSpeed(){
        float xDist, yDist, pX, pY;

        xDist = (x > targetX) ? x - targetX : targetX - x;
        yDist = (y > targetY) ? y - targetY : targetY - y;

        pX = xDist/(xDist+yDist);
        pY = yDist/(xDist+yDist);

        targetSpeedX = (x > targetX) ? -(maxSpeed * pX) : (maxSpeed * pX);
        targetSpeedY = (y > targetY) ? -(maxSpeed * pY) : (maxSpeed * pY);
    }


}
