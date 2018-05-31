package com.example.matt.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.shapes.RectShape;

/**
 * Created by matt on 31/07/17.
 */

public class Control {

    int[] cntrMoveBtn;
    int[] cntrLookBtn;

    Rect testBtn = new Rect(20,20,120,120);
    Paint red = new Paint();
    Paint blue = new Paint();
    Paint green = new Paint();
    Paint custom = new Paint();


    //--- reset button ---//

    boolean rstActive = false;
    private short rstBtnWdth = 100;
    private short rstBtnHght = 100;
    private short[] rstPos = new short[]{600,300};
    private Rect rstRect = new Rect(rstPos[0],rstPos[1],rstPos[0]+rstBtnWdth,rstPos[1]+rstBtnHght);
    private Paint rPaint = new Paint();


    //--------------------//

    Control(int mx,int my, int lx,int ly){
        cntrMoveBtn = new int[]{mx,my};
        cntrLookBtn = new int[]{ly,lx};
        red.setColor(Color.RED);
        blue.setColor(Color.BLUE);
        green.setColor(Color.GREEN);
        custom.setColor(Color.argb(100,255,50,50));
        rPaint.setTextSize(34);
        rPaint.setColor(Color.YELLOW);
    }

    void update(){

    }

    void draw(Canvas c){
        c.drawCircle(cntrMoveBtn[0],cntrMoveBtn[1],160, custom);
        c.drawCircle(cntrLookBtn[0],cntrLookBtn[1],160, custom);
        c.drawCircle(cntrLookBtn[0],cntrLookBtn[1],60, red);

        if(rstActive){
            c.drawRect(rstRect,red);
            c.drawText("reset", rstPos[0]+10,rstPos[1]+50,rPaint);
        }
    }

    float[][] getControlValues(float x, float y){
        return new float[][]{{x-cntrMoveBtn[0],y-cntrMoveBtn[1]},{x-cntrLookBtn[0],y-cntrLookBtn[1]}};
    }

    boolean resetBtnPressed(float x, float y){
        if(rstActive){
            if(rstRect.contains((int)x,(int)y))
                return true;
            else return false;
        }
        else return false;
    }

    boolean testBtnPressed(float x, float y){
        return testBtn.contains((int)x,(int)y);
    }
}
