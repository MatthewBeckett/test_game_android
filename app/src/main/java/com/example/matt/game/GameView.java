package com.example.matt.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;

import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Vibrator;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by matt on 17/07/17.
 */

public class GameView extends SurfaceView implements SurfaceHolder.Callback{

    private Bitmap botImg, playerImg;
    private Bitmap tileSheet;
    private Bitmap plasmaRound;
    private Bitmap explosionImg;

    public GameLoop gameLoop;

    private Control control;
    private HelpText helpText;

    private Score score;

    private Random rand = new Random();
    private TileMap tileMap;
    private float moveBtnInputX;
    private float moveBtnInputY;
    private int moveID = 0;
    float[][] distance = new float[][]{{0, 0}, {0, 0}};
    float[] lookDist = new float[]{2048, 0};

    private int movePlayerX = 0,
            movePlayerY = 0;

    private boolean playerAlive = true;
    boolean initialised = false;

    int scrnWdth = 0;
    int scrnHght = 0;
    Rect screenBody;
    GameObject player;

    Context context;
    LevelProgression level;

    private CopyOnWriteArrayList<GameObject> solidObjects = new CopyOnWriteArrayList<>(); 
    private CopyOnWriteArrayList<GameObject> projectiles = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<GameObject> explosions = new CopyOnWriteArrayList<>();

    public GameView(Context context) {
        super(context);

        this.context = context;
        botImg = BitmapFactory.decodeResource(getResources(), R.drawable.bot);
        playerImg = BitmapFactory.decodeResource(getResources(), R.drawable.player);
        tileSheet = BitmapFactory.decodeResource(getResources(), R.drawable.tile_sheet_new);
        plasmaRound = BitmapFactory.decodeResource(getResources(), R.drawable.plasma_round);
        explosionImg = BitmapFactory.decodeResource(getResources(), R.drawable.explosion);

        handleInput();

    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        if (!gameLoop.running) {
            gameLoop = new GameLoop(this);
            gameLoop.start();
        }else gameLoop.setRunning();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
    }


    private void init() {

        level = new LevelProgression();

        tileMap = new TileMap(tileSheet);

        screenBody = new Rect(-128,-128,128+scrnWdth,128+scrnHght);

        player  = new Player(Name.player, playerImg,(scrnWdth / 2) - 64,(scrnHght / 2) - 64);
        solidObjects.add(player);

        control = new Control(170, scrnHght - 170, scrnHght - 170, scrnWdth - 170);
        helpText = new HelpText(300,scrnHght - 170, scrnWdth - 400 , scrnHght - 130, scrnWdth - 320, scrnHght - 170);

        score = new Score(50,50);

        player.rotation = 90 + (float) Math.toDegrees(Math.atan2((0), (30)));

        level.startGame();
        level.playerDead = false;

        initialised = true;
    }
    private void addBot(int x, int y) {
        if (solidObjects.size() < 50) {
            GameObject b = new Bot(Name.bot, botImg);
            b.x = x - 8;
            b.y = y - 8;
            b.maxSpeed = rand.nextInt(6) + 4 + rand.nextFloat();
            solidObjects.add(b);
        }
    }
    public void updateAll() {

        if (initialised) {
            level.update();
            control.update();
            collisionDetection();
            updateSolidObjects();
            updateProjectile();
            updateExplosion();
            updateTile();
            movePlayerX = 0;
            movePlayerY = 0;
        } else {
            if (scrnHght > 0) {
                init();
            }
        }
    }
    private void updateProjectile() {
            for (GameObject p : projectiles) {
                p.x += +movePlayerX - (int) player.speedX;
                p.y += +movePlayerY - (int) player.speedY;
                for(int i=0;i<4;i++) {
                    p.update();
                    projectileCollision(p);
                    if (p.remove) {
                        Log.d("removing", "projectile");
                        projectiles.remove(p);
                    }
                }

            }

    }
    private void updateSolidObjects() {
        for (GameObject o : solidObjects) {
            if(o.name == Name.player) {
                o.update();
                player = o;
            }
            else{
                if(o.name == Name.bot){
                    o.targetX = player.x + (player.width / 2);
                    o.targetY = player.y + (player.height / 2);
                }
                o.x += -movePlayerX - (int) player.speedX;
                o.y += -movePlayerY - (int) player.speedY;
                o.update();
                if (o.remove) {
                    solidObjects.remove(o);
                }
            }
        }
    }
    private void updateExplosion() {
        for (GameObject e : explosions) {
            e.update();
            e.x += -(int) player.speedX + movePlayerX;
            e.y += -(int) player.speedY + movePlayerY;
            if (e.remove) explosions.remove(e);
        }
    }
    private void updateTile() {
        for (Tile t :tileMap.tileArray) {
            if (player != null) {
                t.x += -(int) player.speedX-movePlayerX;
                t.y += -(int) player.speedY-movePlayerY;
                if(t.name == Name.vent)
                    if(rand.nextInt(10) == 5)
                        if(level.spawnBot()) addBot(t.body.centerX(),t.body.centerY());
            }
            t.body.set(t.x, t.y, t.x + t.width, t.y + t.height);
        }

    }
    public void drawAll(Canvas c) {

        if (initialised) {
            c.drawColor(Color.BLACK);

            for(Tile t : tileMap.tileArray)
                if(screenBody.contains(t.body)) {
                        t.draw(c);
                }

            for(GameObject p : projectiles)
                if(screenBody.contains(p.body))
                    p.draw(c);

            for(GameObject o : solidObjects)
                if(screenBody.contains(o.body))
                    o.draw(c);
            

            control.draw(c);
            helpText.draw(c);
            score.draw(c);

        } else {
            scrnWdth = c.getWidth();
            scrnHght = c.getHeight();
        }
        for (GameObject e : explosions)
            e.draw(c);


    }
    private void handleInput() {
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch (motionEvent.getActionMasked()) {

                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_POINTER_DOWN: {
                        movement(motionEvent.getX(), motionEvent.getY(), motionEvent.getPointerId(motionEvent.getActionIndex()));
                        if(control.resetBtnPressed(motionEvent.getX(),motionEvent.getY())) {
                            control.rstActive = false;
                            score.zeroScore();
                            playerAlive = true;
                            init();
                        }
                        break;
                    }
                    case MotionEvent.ACTION_MOVE: {

                        for (int i = 0; i < motionEvent.getPointerCount(); i++) {
                            int pointerID = motionEvent.getPointerId(i);
                            movement(motionEvent.getX(i), motionEvent.getY(i), pointerID);

                        }
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_POINTER_UP: {
                        int pointerIndex = motionEvent.getActionIndex();
                        int pointerID = motionEvent.getPointerId(pointerIndex);

                        if (pointerID == moveID) {
                            moveID = -1;
                            moveBtnInputX = 0;
                            moveBtnInputY = 0;
                            player.targetSpeedX = moveBtnInputX;
                            player.targetSpeedY = moveBtnInputY;
                        }
                        break;
                    }
                }
                return true;
            }
        });
    }
    private void movement(float x, float y, int id) {

        distance = control.getControlValues(x, y);

        if (getDist(x, y, control.cntrMoveBtn[0], control.cntrMoveBtn[1]) < 160) {
            helpText.showMove = false;
            if (moveID == -1) {
                moveID = id;
                moveBtnInputX = distance[0][0];
                moveBtnInputY = distance[0][1];
            }
            if (moveID == id) {
                moveBtnInputX = distance[0][0];
                moveBtnInputY = distance[0][1];
            }
        }
        float ld = getDist(x, y, control.cntrLookBtn[0], control.cntrLookBtn[1]);

        if (ld < 60) {
            shoot(lookDist);
            helpText.showFire = false;
        } else if (ld < 170 && ld > 60) {
            helpText.showTurn = false;
            player.rotation = 90 + (float) Math.toDegrees(Math.atan2((y - control.cntrLookBtn[1]), (x - control.cntrLookBtn[0])));
            lookDist[0] = distance[1][0];
            lookDist[1] = distance[1][1];
        }

        player.targetSpeedX = moveBtnInputX * 0.2f;
        player.targetSpeedY = moveBtnInputY * 0.2f;
    }
    private float getDist(float x1, float y1, float x2, float y2) {
        float x = (x1 > x2) ? x1 - x2 : x2 - x1;
        float y = (y1 > y2) ? y1 - y2 : y2 - y1;
        return (float) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }
    private void shoot(float[] distance) {
        if (level.fire() && playerAlive) {
            Projectile p = new Projectile(Name.projectile, plasmaRound, player);
            p.x = scrnWdth/2 + (int) player.speedX;
            p.y = scrnHght/2 + (int) player.speedY;
            p.targetX = (int) (distance[0] * 1000);
            p.targetY = (int) (distance[1] * 1000);

            this.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            projectiles.add(p);
        }
    }
    private void gameOver(){
        level.playerDead =true;
        solidObjects.clear();
        playerAlive = false;
        GameObject e = new Explosion(Name.explosion, explosionImg);
        e.x = player.body.centerX() - (e.width/2);
        e.y = player.body.centerY() - (e.height/2);
        explosions.add(e);
        control.rstActive = true;
    }
    private void projectileCollision(GameObject p){
        for(GameObject b : solidObjects){
            if(b.name == Name.bot){
                if(b.body.contains(p.body)) {
                    projectiles.remove(p);
                    solidObjects.remove(b);
                    score.updateScore();
                    level.killedBot();
                    GameObject e = new Explosion(Name.explosion, explosionImg);
                    e.x = b.body.centerX() - (e.width / 2);
                    e.y = b.body.centerY() - (e.height / 2);
                    explosions.add(e);
                }
            }
        }
    }
    private void collisionDetection() {
         for (GameObject b : solidObjects ) {
            if(b.name == Name.bot) {
                if(b.body.contains(player.body.centerX(),player.body.centerY())) {
                    gameOver();
                }
            }
        }
        for (Tile t : tileMap.tileArray){
            if (t.name == Name.cornerWall || t.name == Name.wall) {

                for(GameObject p : projectiles)
                    if(t.body.contains(p.body.centerX()-(int)p.speedX,p.body.centerY()-(int)p.speedY)) {
                        Log.d("projectile","wall");
                        p.remove = true;
                    }

                for (GameObject o : solidObjects) {
                    if (t.name == Name.wall) {
                        wallCollisionHandling(o,t);
                    } 
                    else if (t.name == Name.cornerWall) {
                        cornerWallCollisionHandling(o,t);
                    }
                }
            }
        }
    }
    private void wallCollisionHandling(GameObject o, Tile t){
        if (o.body.intersect(t.body)) {
            if(o.name == Name.bot) {
                if (t.rotation == 0) {
                    //north wall
                    o.targetSpeedY = 0;
                    o.y += t.body.bottom - o.body.top;
                } else if (t.rotation == 180) {
                    // south wall
                    o.targetSpeedY = 0;
                    o.y -= (o.body.bottom - t.body.top);
                }
                if (t.rotation == 90) {
                    // east wall
                    o.targetSpeedX = 0;
                    o.x -= (o.body.right - t.body.left);
                } else if (t.rotation == 270) {
                    // west wall
                    o.targetSpeedX = 0;
                    o.x += (t.body.right - o.body.left);
                }
            }
            else if(o.name == Name.player){
                if (t.rotation == 0) {
                    //north wall
                    moveBtnInputY = 0;
                    o.speedY = 1;
                    o.targetSpeedY = 0;
                    movePlayerY = t.body.bottom - o.body.top;
                } else if (t.rotation == 180) {
                    // south wall
                    moveBtnInputY = 0;
                    o.speedY = -1;
                    o.targetSpeedY = 0;
                    movePlayerY = -(o.body.bottom - t.body.top);
                }
                if (t.rotation == 90) {
                    // east wall
                    moveBtnInputX = 0;
                    o.speedX = -1;
                    o.targetSpeedX = 0;
                    movePlayerX = -(o.body.right - t.body.left);
                } else if (t.rotation == 270) {
                    // west wall
                    moveBtnInputX = 0;
                    o.speedX = 1;
                    o.targetSpeedX = 0;
                    movePlayerX = (t.body.right - o.body.left);
                }
            }
            else if(o.name == Name.projectile){
                o.remove = true;
            }
        }
    }

    private void cornerWallCollisionHandling(GameObject o, Tile t){
        if (o.body.intersect(t.body)) {
            if(o.name == Name.bot) {
                if (t.rotation == 0) {
                    // north east
                    if (getDist(o.body.centerX(), o.body.centerY(), t.body.left, t.body.centerY()) <
                            getDist(o.body.centerX(), o.body.centerY(), t.body.centerX(), t.body.bottom)) {
                        o.targetSpeedX = 0;
                        o.x -= (o.body.right - t.body.left);
                    } else {
                        o.targetSpeedY = 0;
                        o.y += (t.body.bottom - o.body.top);
                    }
                } else if (t.rotation == 180) {
                    // south west
                    if (getDist(o.body.centerX(), o.body.centerY(), t.body.right, t.body.centerY()) <
                            getDist(o.body.centerX(), o.body.centerY(), t.body.centerX(), t.body.top)) {
                        o.targetSpeedX = 0;
                        o.x += (t.body.right - o.body.left);
                    } else {
                        o.targetSpeedY = 0;
                        o.y -= (o.body.bottom - t.body.top);
                    }
                }
                if (t.rotation == 90) {
                    // south east
                    if (getDist(o.body.centerX(), o.body.centerY(), t.body.left, t.body.centerY()) <
                            getDist(o.body.centerX(), o.body.centerY(), t.body.centerX(), t.body.top)) {
                        o.targetSpeedX = 0;
                        o.x -= (o.body.right - t.body.left);
                    } else {
                        o.targetSpeedY = 0;
                        o.y -= (o.body.bottom - t.body.top);
                    }
                } else if (t.rotation == 270) {
                    // north west
                    if (getDist(o.body.centerX(), o.body.centerY(), t.body.right, t.body.centerY()) <
                            getDist(o.body.centerX(), o.body.centerY(), t.body.centerX(), t.body.bottom)) {
                        moveBtnInputX = 0;
                        o.x += (t.body.right - o.body.left);
                    } else {
                        o.targetSpeedY = 0;
                        o.y += t.body.bottom - o.body.top;
                    }

                }
            }
            else if(o.name == Name.player){
                if (t.rotation == 0) {
                    // north east
                    if (getDist(o.body.centerX(), o.body.centerY(), t.body.left, t.body.centerY()) <
                            getDist(o.body.centerX(), o.body.centerY(), t.body.centerX(), t.body.bottom)) {
                        moveBtnInputX = 0;
                        o.speedX = 0;
                        movePlayerX = -(o.body.right - t.body.left);
                    } else {
                        moveBtnInputY = 0;
                        o.speedY = 0;
                        movePlayerY = (t.body.bottom - o.body.top);
                    }
                } else if (t.rotation == 180) {
                    // south west
                    if (getDist(o.body.centerX(), o.body.centerY(), t.body.right, t.body.centerY()) <
                            getDist(o.body.centerX(), o.body.centerY(), t.body.centerX(), t.body.top)) {
                        moveBtnInputX = 0;
                        o.speedX = 0;
                        movePlayerX = (t.body.right - o.body.left);
                    } else {
                        moveBtnInputY = 0;
                        o.speedY = 0;
                        movePlayerY = -(o.body.bottom - t.body.top);
                    }
                }
                if (t.rotation == 90) {
                    // south east
                    if (getDist(o.body.centerX(), o.body.centerY(), t.body.left, t.body.centerY()) <
                            getDist(o.body.centerX(), o.body.centerY(), t.body.centerX(), t.body.top)) {
                        moveBtnInputX = 0;
                        o.speedX = 0;
                        movePlayerX = -(o.body.right - t.body.left);
                    } else {
                        moveBtnInputY = 0;
                        o.speedY = 0;
                        movePlayerY = -(o.body.bottom - t.body.top);
                    }
                } else if (t.rotation == 270) {
                    // north west
                    if (getDist(o.body.centerX(), o.body.centerY(), t.body.right, t.body.centerY()) <
                            getDist(o.body.centerX(), o.body.centerY(), t.body.centerX(), t.body.bottom)) {
                        moveBtnInputX = 0;
                        o.speedX = 0;
                        movePlayerX = (t.body.right - o.body.left);
                    } else {
                        moveBtnInputY = 0;
                        o.speedY = 0;
                        movePlayerY = t.body.bottom - o.body.top;
                    }
                }
            }
            else if(o.name == Name.projectile){
                o.remove = true;
            }
        }
    }

    public void resume(){
        gameLoop = new GameLoop(this);
        gameLoop.start();
    }

}

