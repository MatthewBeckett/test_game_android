package com.example.matt.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Created by matt on 05/08/17.
 */

public class Projectile extends GameObject {

    Paint green;

    public Projectile(Name name, Bitmap img,GameObject owner) {
        super(name, img, 0, 0);
        this.owner = owner;
        maxSpeed = 12;
        green = new Paint();
        green.setARGB(255,50,150,50);
        body = new Rect(x,y,x+width,y+height);

    }

    @Override
    void update() {
        setSpeed();
        x+=targetSpeedX;
        y+=targetSpeedY;
        body = new Rect(x,y,x+width,y+height);
        if(x > 2000 || x < -2000 || y > 2000 || y < -2000){
            remove = true;
        }
    }

    @Override
    void draw(Canvas c) {
        if(!remove)
            c.drawCircle(x,y,5,green);
    }
}
