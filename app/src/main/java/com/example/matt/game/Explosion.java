package com.example.matt.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.media.Image;

import java.util.Random;

/**
 * Created by matt on 05/08/17.
 */

public class Explosion extends GameObject {

    int columns,column,rows,row,count=0;
    Rect[] imageFrames;
    Rect location;
    Random random;

    public Explosion(Name name, Bitmap img) {
        super(name, img,0,0);
        columns = 4;
        rows = 3;
        width = img.getWidth()/columns;
        height = img.getHeight()/rows;
        imageFrames = new Rect[columns * rows];
        for(row = 0; row < rows ; row++)
            for(column = 0 ; column < columns; column++,count++){
                imageFrames[count]=new Rect(column*width,row*height,(column*width)+width,(row*height)+height);
            }

        count = 0;
        location = new Rect(0,0,width,height);
        random = new Random();
        rotation = random.nextInt(360);
    }

    @Override
    void update() {
        location.set(x,y,x+width,y+height);
        if(count >= columns*rows) remove = true;
    }

    @Override
    void draw(Canvas c) {
        c.save();
        c.drawBitmap(img,imageFrames[count],location,null);
        count++;
        c.rotate(rotation);
    }
}
