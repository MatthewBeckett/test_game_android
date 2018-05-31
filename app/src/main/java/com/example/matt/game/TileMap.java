package com.example.matt.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Created by matt on 31/07/17.
 */


public class TileMap {

    Bitmap          tileSheet;
    List<Bitmap>    tileImages = new ArrayList<>();
    Tile[][][]      tiles;
    Tile[]          tileArray;
    Sector[][]      sectors;
    Sector[]        sectorArray;
    Integer[][][]   linkTable = new Integer[Settings.MAP_HEIGHT/8][Settings.MAP_WIDTH/8][4];

    int             columns     = Settings.MAP_WIDTH,
                    rows        = Settings.MAP_HEIGHT,
                    tileWidth   = Settings.TILE_WIDTH,
                    tileHeight  = Settings.TILE_WIDTH,
                    anchorX     = 0,
                    anchorY     = 0;

    Paint pBlack = new Paint(Color.BLACK);



    // CollisionMap
    // 0 = n
    // 1 = ne
    // 2 = e
    // 3 = se
    // 4 = s
    // 5 = sw
    // 6 = w
    // 7 = nw
    // 8 = all





    TileMap(Bitmap tileSheet){
        this.tileSheet = tileSheet;
        tiles = new Tile[columns][rows][Settings.TILE_LAYERS];

        tileWidth = tileSheet.getWidth()/4;
        tileHeight = tileSheet.getHeight()/5;
        for(int row = 0 ; row < 5 ; row++) {
            for (int col = 0; col < 4; col++) {
                Bitmap b = Bitmap.createBitmap(tileSheet, col * tileWidth, row * tileHeight, tileWidth, tileHeight);
                tileImages.add(b);
            }
        }

        sectors = new Sector[(Settings.MAP_HEIGHT/8)][(Settings.MAP_HEIGHT/8)];
        sectorArray = new Sector[(Settings.MAP_HEIGHT/8)*(Settings.MAP_HEIGHT/8)];


        map1();
        //setSectorLinks();
        flattenArrays();
    }

    void flattenArrays() {
        // Tiles
        List<Tile> ta = new ArrayList<>();
        for (int row = 0; row < Settings.MAP_HEIGHT; row++)
            for (int col = 0; col < Settings.MAP_WIDTH; col++)
                for (int t = 0; t < Settings.TILE_LAYERS; t++)
                    if (tiles[row][col][t] != null) {
                        ta.add(tiles[row][col][t]);
                    }
        tileArray = new Tile[ta.size()];
        for(Tile t : ta)
            tileArray[ta.indexOf(t)] = t;

        ta.clear();
        tiles = null;

        // Sectors
        List<Sector> sa = new ArrayList<>();
        for(int y = 0 ; y < Settings.MAP_HEIGHT/8;y++)
            for(int x = 0 ; x < Settings.MAP_WIDTH/8; x++)
                sa.add(sectors[y][x]);

        for(Sector s : sa)
            sectorArray[sa.indexOf(s)] = s;

        sa.clear();
        sectors = null;

    }

    void map1(){
        addGrid(0,0,Grid.roomE());
        addGrid(0,0,Grid.ventNW());
        addGrid(0,1,Grid.corridorEW());
        addGrid(0,2,Grid.corridorEW());
        addGrid(0,3,Grid.roomWS());
        addGrid(0,3,Grid.ventNE());
        addGrid(1,3,Grid.roomWN());
        addGrid(1,3,Grid.ventSE());
        addGrid(1,2,Grid.junctionESW());
        addGrid(1,1,Grid.junctionESW());
        addGrid(1,1,Grid.ventC());
        addGrid(1,0,Grid.cornerES());
        addGrid(2,0,Grid.roomN());
        addGrid(2,0,Grid.ventNW());
        addGrid(2,1,Grid.corridorNS());
        addGrid(2,2,Grid.roomEN());
        addGrid(2,2,Grid.ventNE());
        addGrid(2,3,Grid.roomW());
        addGrid(2,3,Grid.ventSW());
        addGrid(3,0,Grid.roomE());
        addGrid(3,0,Grid.ventSW());
        addGrid(3,1,Grid.junctionNEW());
        addGrid(3,2,Grid.roomEW());
        addGrid(3,2,Grid.ventC());
        addGrid(3,3,Grid.roomW());
    }

    void addGrid(int y_location,int x_location, Integer[][][] grid){
        addSector(x_location,y_location);

        int tileLayer = 1;
        if(grid.length > 8){
            tileLayer = 0;
            linkTable[y_location][x_location] = grid[8][0];
        }

        int y = y_location*8;
        int x = x_location*8;
        
        int l = 0;
        
        for(int row = 0 ; row < 8 ; row++) {
            for (int col = 0; col < 8; col++, l++) {
                for (int layer = 0; layer < grid[row][col].length; layer++) {
                    if (grid[row][col][layer] != null) {
                        
                        // Selects by even layers, even number referrers to the image
                        // odd is the direction. This could be improved by splitting by 2
                        // instead of calculating.

                        Name tileName = Name.tile;

                        switch(grid[row][col][layer]){
                            case 1:
                                tileName = Name.metalFloor;
                                break;
                            case 4:
                                tileName = Name.vent;
                                break;
                            case 13:
                                tileName = Name.wall;
                                break;
                            case 16:
                                tileName = Name.cornerWall;
                                break;
                        }

                        if (layer == 0) {
                            tiles[row + y][col + x][tileLayer] = new Tile(
                                    tileName,
                                    tileImages.get(grid[row][col][layer]),
                                    (x + col) * tileWidth,
                                    (y + row) * tileHeight,
                                    grid[row][col][layer + 1]);
                        }
                    }
                }
            }
        }
    }
/*
    private void setSectorLinks(){
        int tc = 0;
        for(int y = 0 ; y < Settings.MAP_HEIGHT/8;y++)
            for(int x = 0 ; x < Settings.MAP_WIDTH/8; x++)
                for(int l = 0 ; l < 4 ; l++){
                    switch(l){
                        case 0:
                            if(linkTable[y][x][l] == 1){
                                sectors[y][x].link[0] = ((y-1)*10)+(x);
                                Log.d("sectorLink","ID."+sectors[y][x].ID+" x."+x+" y."+y+" l."+l+" id."+sectors[y][x].link[0]);
                            }
                            else
                                sectors[y][x].link[0] = null;
                            break;
                        case 1:
                            if(linkTable[y][x][l] == 1){
                                sectors[y][x].link[1] = (y*10)+(x+1);
                                Log.d("sectorLink","ID."+sectors[y][x].ID+" x."+x+" y."+y+" l."+l+" id."+sectors[y][x].link[1]);
                            }
                            else
                                sectors[y][x].link[1] = null;
                            break;
                        case 2:
                            if(linkTable[y][x][l] == 1){
                                sectors[y][x].link[2] = ((y+1)*10)+(x);
                                Log.d("sectorLink","ID."+sectors[y][x].ID+" x."+x+" y."+y+" l."+l+" id."+sectors[y][x].link[2]);
                            }
                            else
                                sectors[y][x].link[2] = null;
                            break;
                        case 3:
                            if(linkTable[y][x][l] == 1) {
                                sectors[y][x].link[3] = (y * 10) + (x - 1);
                                Log.d("sectorLink","ID."+sectors[y][x].ID+" x."+x+" y."+y+" l."+l+" id."+sectors[y][x].link[3]);
                            }
                            else
                                sectors[y][x].link[3] = null;
                    }
                }

    }
*/
    private void addSector(int x_location, int y_location){
        int width   = 8 * 32;
        int height  = 8 * 32;
        int x = x_location * width;
        int y = y_location * height;

        sectors[y_location][x_location] = new Sector(x,y,width,height);

    }

    public void renumberSectors(){

        boolean done;
        do{
            done = true;
            for(Sector s : sectorArray){
                if(s.locationNumber != null){
                    for(Integer id : s.link){
                        if(id != null)
                        for(Sector ss : sectorArray)
                            if(ss.ID == id)
                                if(ss.locationNumber == null || ss.locationNumber > s.locationNumber+1) {
                                    ss.locationNumber = s.locationNumber+1;
                                    Log.d("location numbering","ssid."+ss.ID+" locnum."+ss.locationNumber);
                                    done = false;
                                }
                    }
                }
            }
        }while(!done);
    }

}


class Sector{
    public Rect sectorR;
    public Integer[] link;
    public Integer locationNumber = null;
    final int ID;

    Sector(int x,int y,int width, int height){
            ID = (10*(y/(32*8)))+(x/(32*8));
            sectorR = new Rect(x,y,x+width,y+height);
            link = new Integer[4];
    }
}



// TILE CLASS \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

class Tile {

    Name name;
    Bitmap image;
    int rotation,x,y,width,height;
    Rect body;

    Tile(Name name,Bitmap image,int x,int y,int direction){
        this.name =name;
        this.image = image;
        this.x = x;
        this.y = y;
        width = image.getWidth();
        height = image.getHeight();
        rotation = 90*direction;
        body = new Rect(x,y,x+width,y+height);
    }

    void draw(Canvas c){
        c.save();
        c.rotate(rotation,x+width/2,y+height/2);
        c.drawBitmap(image,x,y,null);
        c.restore();
    }


}



// GRID TEMPLATES \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\


class Grid{

    public static Integer[][][] roomN(){
        return  new Integer[][][]{
                {{14,3},{13,0},{16,3},{01,0},{01,0},{16,0},{13,0},{14,0}},
                {{13,3},{01,0},{01,0},{01,0},{01,0},{01,0},{01,0},{13,1}},
                {{13,3},{01,0},{01,0},{01,0},{01,0},{01,0},{01,0},{13,1}},
                {{13,3},{01,0},{01,0},{01,0},{01,0},{01,0},{01,0},{13,1}},
                {{13,3},{01,0},{01,0},{01,0},{01,0},{01,0},{01,0},{13,1}},
                {{13,3},{01,0},{01,0},{01,0},{01,0},{01,0},{01,0},{13,1}},
                {{13,3},{01,0},{01,0},{01,0},{01,0},{01,0},{01,0},{13,1}},
                {{14,2},{13,2},{13,2},{13,2},{13,2},{13,2},{13,2},{14,1}},
                {{1,0,0,0}}
        };
    }

    public static Integer[][][] roomE(){
        return  new Integer[][][]{
                {{14,3},{13,0},{13,0},{13,0},{13,0},{13,0},{13,0},{14,0}},
                {{13,3},{01,0},{01,0},{01,0},{01,0},{01,0},{01,0},{13,1}},
                {{13,3},{01,0},{01,0},{01,0},{01,0},{01,0},{01,0},{16,0}},
                {{13,3},{01,0},{01,0},{01,0},{01,0},{01,0},{01,0},{01,0}},
                {{13,3},{01,0},{01,0},{01,0},{01,0},{01,0},{01,0},{01,0}},
                {{13,3},{01,0},{01,0},{01,0},{01,0},{01,0},{01,0},{16,1}},
                {{13,3},{01,0},{01,0},{01,0},{01,0},{01,0},{01,0},{13,1}},
                {{14,2},{13,2},{13,2},{13,2},{13,2},{13,2},{13,2},{14,1}},
                {{0,1,0,0}}
        };
    }

    public static Integer[][][] roomEN(){
        return  new Integer[][][]{
                {{14,3},{13,0},{16,3},{01,0},{01,0},{16,0},{13,0},{14,0}},
                {{13,3},{01,0},{01,0},{01,0},{01,0},{01,0},{01,0},{13,1}},
                {{13,3},{01,0},{01,0},{01,0},{01,0},{01,0},{01,0},{16,0}},
                {{13,3},{01,0},{01,0},{01,0},{01,0},{01,0},{01,0},{01,0}},
                {{13,3},{01,0},{01,0},{01,0},{01,0},{01,0},{01,0},{01,0}},
                {{13,3},{01,0},{01,0},{01,0},{01,0},{01,0},{01,0},{16,1}},
                {{13,3},{01,0},{01,0},{01,0},{01,0},{01,0},{01,0},{13,1}},
                {{14,2},{13,2},{13,2},{13,2},{13,2},{13,2},{13,2},{14,1}},
                {{1,1,0,0}}
        };
    }

    public static Integer[][][] roomES(){
        return  new Integer[][][]{
                {{14,3},{13,0},{13,0},{13,0},{13,0},{13,0},{13,0},{14,0}},
                {{13,3},{01,0},{01,0},{01,0},{01,0},{01,0},{01,0},{13,1}},
                {{13,3},{01,0},{01,0},{01,0},{01,0},{01,0},{01,0},{16,0}},
                {{13,3},{01,0},{01,0},{01,0},{01,0},{01,0},{01,0},{01,0}},
                {{13,3},{01,0},{01,0},{01,0},{01,0},{01,0},{01,0},{01,0}},
                {{13,3},{01,0},{01,0},{01,0},{01,0},{01,0},{01,0},{16,1}},
                {{13,3},{01,0},{01,0},{01,0},{01,0},{01,0},{01,0},{13,1}},
                {{14,2},{13,2},{16,2},{01,0},{01,0},{16,1},{13,2},{14,1}},
                {{0,1,1,0}}
        };
    }


    public static Integer[][][] roomS(){
        return  new Integer[][][]{
                {{14,3},{13,0},{13,0},{13,0},{13,0},{13,0},{13,0},{14,0}},
                {{13,3},{01,0},{01,0},{01,0},{01,0},{01,0},{01,0},{13,1}},
                {{13,3},{01,0},{01,0},{01,0},{01,0},{01,0},{01,0},{13,1}},
                {{13,3},{01,0},{01,0},{01,0},{01,0},{01,0},{01,0},{13,1}},
                {{13,3},{01,0},{01,0},{01,0},{01,0},{01,0},{01,0},{13,1}},
                {{13,3},{01,0},{01,0},{01,0},{01,0},{01,0},{01,0},{13,1}},
                {{13,3},{01,0},{01,0},{01,0},{01,0},{01,0},{01,0},{13,1}},
                {{14,2},{13,2},{16,2},{01,0},{01,0},{16,1},{13,2},{14,1}},
                {{0,0,1,0}}
        };
    }

    public static Integer[][][] roomW(){
        return  new Integer[][][]{
                {{14,3},{13,0},{13,0},{13,0},{13,0},{13,0},{13,0},{14,0}},
                {{13,3},{01,0},{01,0},{01,0},{01,0},{01,0},{01,0},{13,1}},
                {{16,3},{01,0},{01,0},{01,0},{01,0},{01,0},{01,0},{13,1}},
                {{01,0},{01,0},{01,0},{01,0},{01,0},{01,0},{01,0},{13,1}},
                {{01,0},{01,0},{01,0},{01,0},{01,0},{01,0},{01,0},{13,1}},
                {{16,2},{01,0},{01,0},{01,0},{01,0},{01,0},{01,0},{13,1}},
                {{13,3},{01,0},{01,0},{01,0},{01,0},{01,0},{01,0},{13,1}},
                {{14,2},{13,2},{13,2},{13,2},{13,2},{13,2},{13,2},{14,1}},
                {{0,0,0,1}}
        };
    }

    public static Integer[][][] roomWN(){
        return  new Integer[][][]{
                {{14,3},{13,0},{16,3},{01,0},{01,0},{16,0},{13,0},{14,0}},
                {{13,3},{01,0},{01,0},{01,0},{01,0},{01,0},{01,0},{13,1}},
                {{16,3},{01,0},{01,0},{01,0},{01,0},{01,0},{01,0},{13,1}},
                {{01,0},{01,0},{01,0},{01,0},{01,0},{01,0},{01,0},{13,1}},
                {{01,0},{01,0},{01,0},{01,0},{01,0},{01,0},{01,0},{13,1}},
                {{16,2},{01,0},{01,0},{01,0},{01,0},{01,0},{01,0},{13,1}},
                {{13,3},{01,0},{01,0},{01,0},{01,0},{01,0},{01,0},{13,1}},
                {{14,2},{13,2},{13,2},{13,2},{13,2},{13,2},{13,2},{14,1}},
                {{1,0,0,1}}
        };
    }

    public static Integer[][][] roomWS(){
        return  new Integer[][][]{
                {{14,3},{13,0},{13,0},{13,0},{13,0},{13,0},{13,0},{14,0}},
                {{13,3},{01,0},{01,0},{01,0},{01,0},{01,0},{01,0},{13,1}},
                {{16,3},{01,0},{01,0},{01,0},{01,0},{01,0},{01,0},{13,1}},
                {{01,0},{01,0},{01,0},{01,0},{01,0},{01,0},{01,0},{13,1}},
                {{01,0},{01,0},{01,0},{01,0},{01,0},{01,0},{01,0},{13,1}},
                {{16,2},{01,0},{01,0},{01,0},{01,0},{01,0},{01,0},{13,1}},
                {{13,3},{01,0},{01,0},{01,0},{01,0},{01,0},{01,0},{13,1}},
                {{14,2},{13,2},{16,2},{01,0},{01,0},{16,1},{13,2},{14,1}},
                {{0,0,1,1}}
        };
    }



    public static Integer[][][] roomEW(){
        return  new Integer[][][]{
                {{14,3},{13,0},{13,0},{13,0},{13,0},{13,0},{13,0},{14,0}},
                {{13,3},{01,0},{01,0},{01,0},{01,0},{01,0},{01,0},{13,1}},
                {{16,3},{01,0},{01,0},{01,0},{01,0},{01,0},{01,0},{16,0}},
                {{01,0},{01,0},{01,0},{01,0},{01,0},{01,0},{01,0},{01,0}},
                {{01,0},{01,0},{01,0},{01,0},{01,0},{01,0},{01,0},{01,0}},
                {{16,2},{01,0},{01,0},{01,0},{01,0},{01,0},{01,0},{16,1}},
                {{13,3},{01,0},{01,0},{01,0},{01,0},{01,0},{01,0},{13,1}},
                {{14,2},{13,2},{13,2},{13,2},{13,2},{13,2},{13,2},{14,1}},
                {{0,1,0,1}}
        };
    }

    public static Integer[][][] roomNS(){
        return  new Integer[][][]{
                {{14,3},{13,0},{16,3},{01,0},{01,0},{16,0},{13,0},{14,0}},
                {{13,3},{01,0},{01,0},{01,0},{01,0},{01,0},{01,0},{13,1}},
                {{13,3},{01,0},{01,0},{01,0},{01,0},{01,0},{01,0},{13,1}},
                {{13,3},{01,0},{01,0},{01,0},{01,0},{01,0},{01,0},{13,1}},
                {{13,3},{01,0},{01,0},{01,0},{01,0},{01,0},{01,0},{13,1}},
                {{13,3},{01,0},{01,0},{01,0},{01,0},{01,0},{01,0},{13,1}},
                {{13,3},{01,0},{01,0},{01,0},{01,0},{01,0},{01,0},{13,1}},
                {{14,2},{13,2},{16,2},{01,0},{01,0},{16,1},{13,2},{14,1}},
                {{1,0,1,0}}
        };
    }

    public static Integer[][][] corridorNS(){
        return  new Integer[][][]{
                {{null},{null},{13,3},{01,0},{01,0},{13,1},{null},{null}},
                {{null},{null},{13,3},{01,0},{01,0},{13,1},{null},{null}},
                {{null},{null},{13,3},{01,0},{01,0},{13,1},{null},{null}},
                {{null},{null},{13,3},{01,0},{01,0},{13,1},{null},{null}},
                {{null},{null},{13,3},{01,0},{01,0},{13,1},{null},{null}},
                {{null},{null},{13,3},{01,0},{01,0},{13,1},{null},{null}},
                {{null},{null},{13,3},{01,0},{01,0},{13,1},{null},{null}},
                {{null},{null},{13,3},{01,0},{01,0},{13,1},{null},{null}},
                {{1,0,1,0}}
        };
    }

    public static Integer[][][] corridorEW(){
        return  new Integer[][][]{
                {{null},{null},{null},{null},{null},{null},{null},{null}},
                {{null},{null},{null},{null},{null},{null},{null},{null}},
                {{13,0},{13,0},{13,0},{13,0},{13,0},{13,0},{13,0},{13,0}},
                {{01,0},{01,0},{01,0},{01,0},{01,0},{01,0},{01,0},{01,0}},
                {{01,0},{01,0},{01,0},{01,0},{01,0},{01,0},{01,0},{01,0}},
                {{13,2},{13,2},{13,2},{13,2},{13,2},{13,2},{13,2},{13,2}},
                {{null},{null},{null},{null},{null},{null},{null},{null}},
                {{null},{null},{null},{null},{null},{null},{null},{null}},
                {{0,1,0,1}}
        };
    }

    public static Integer[][][] cornerWS(){
        return  new Integer[][][]{
                {{null},{null},{null},{null},{null},{null},{null},{null}},
                {{null},{null},{null},{null},{null},{null},{null},{null}},
                {{13,0},{13,0},{13,0},{13,0},{13,0},{14,0},{null},{null}},
                {{01,0},{01,0},{01,0},{01,0},{01,0},{13,1},{null},{null}},
                {{01,0},{01,0},{01,0},{01,0},{01,0},{13,1},{null},{null}},
                {{13,2},{13,2},{16,2},{01,0},{01,0},{13,1},{null},{null}},
                {{null},{null},{13,3},{01,0},{01,0},{13,1},{null},{null}},
                {{null},{null},{13,3},{01,0},{01,0},{13,1},{null},{null}},
                {{0,0,1,1}}
        };
    }

    public static Integer[][][] cornerES(){
        return  new Integer[][][]{
                {{null},{null},{null},{null},{null},{null},{null},{null}},
                {{null},{null},{null},{null},{null},{null},{null},{null}},
                {{null},{null},{14,3},{13,0},{13,0},{13,0},{13,0},{13,0}},
                {{null},{null},{13,3},{01,0},{01,0},{01,0},{01,0},{01,0}},
                {{null},{null},{13,3},{01,0},{01,0},{01,0},{01,0},{01,0}},
                {{null},{null},{13,3},{01,0},{01,0},{16,1},{13,2},{13,2}},
                {{null},{null},{13,3},{01,0},{01,0},{13,1},{null},{null}},
                {{null},{null},{13,3},{01,0},{01,0},{13,1},{null},{null}},
                {{0,1,1,0}}
        };
    }

    public static Integer[][][] junctionESW(){
        return  new Integer[][][]{
                {{null},{null},{null},{null},{null},{null},{null},{null}},
                {{null},{null},{null},{null},{null},{null},{null},{null}},
                {{13,0},{13,0},{13,0},{13,0},{13,0},{13,0},{13,0},{13,0}},
                {{01,0},{01,0},{01,0},{01,0},{01,0},{01,0},{01,0},{01,0}},
                {{01,0},{01,0},{01,0},{01,0},{01,0},{01,0},{01,0},{01,0}},
                {{13,2},{13,2},{16,2},{01,0},{01,0},{16,1},{13,2},{13,2}},
                {{null},{null},{13,3},{01,0},{01,0},{13,1},{null},{null}},
                {{null},{null},{13,3},{01,0},{01,0},{13,1},{null},{null}},
                {{0,1,1,1}}
        };
    }

    public static Integer[][][] junctionNEW(){
        return  new Integer[][][]{
                {{null},{null},{13,3},{01,0},{01,0},{13,1},{null},{null}},
                {{null},{null},{13,3},{01,0},{01,0},{13,1},{null},{null}},
                {{13,0},{13,0},{16,3},{01,0},{01,0},{16,0},{13,0},{13,0}},
                {{01,0},{01,0},{01,0},{01,0},{01,0},{01,0},{01,0},{01,0}},
                {{01,0},{01,0},{01,0},{01,0},{01,0},{01,0},{01,0},{01,0}},
                {{13,2},{13,2},{13,2},{13,2},{13,2},{13,2},{13,2},{13,2}},
                {{null},{null},{null},{null},{null},{null},{null},{null}},
                {{null},{null},{null},{null},{null},{null},{null},{null}},
                {{1,1,0,1}}
        };
    }

    public static Integer[][][] ventC(){
        return new Integer[][][]{
                {{null},{null},{null},{null},{null},{null},{null},{null}},
                {{null},{null},{null},{null},{null},{null},{null},{null}},
                {{null},{null},{null},{null},{null},{null},{null},{null}},
                {{null},{null},{null},{null},{null},{null},{null},{null}},
                {{null},{null},{null},{4, 0},{null},{null},{null},{null}},
                {{null},{null},{null},{null},{null},{null},{null},{null}},
                {{null},{null},{null},{null},{null},{null},{null},{null}},
                {{null},{null},{null},{null},{null},{null},{null},{null}},


        };
    }

    public static Integer[][][] ventNE(){
        return new Integer[][][]{
                {{null},{null},{null},{null},{null},{null},{null},{null}},
                {{null},{null},{null},{null},{null},{null},{4, 0},{null}},
                {{null},{null},{null},{null},{null},{null},{null},{null}},
                {{null},{null},{null},{null},{null},{null},{null},{null}},
                {{null},{null},{null},{null},{null},{null},{null},{null}},
                {{null},{null},{null},{null},{null},{null},{null},{null}},
                {{null},{null},{null},{null},{null},{null},{null},{null}},
                {{null},{null},{null},{null},{null},{null},{null},{null}},


        };
    }

    public static Integer[][][] ventSE(){
        return new Integer[][][]{
                {{null},{null},{null},{null},{null},{null},{null},{null}},
                {{null},{null},{null},{null},{null},{null},{null},{null}},
                {{null},{null},{null},{null},{null},{null},{null},{null}},
                {{null},{null},{null},{null},{null},{null},{null},{null}},
                {{null},{null},{null},{null},{null},{null},{null},{null}},
                {{null},{null},{null},{null},{null},{null},{null},{null}},
                {{null},{null},{null},{null},{null},{null},{4, 0},{null}},
                {{null},{null},{null},{null},{null},{null},{null},{null}},


        };
    }

    public static Integer[][][] ventSW(){
        return new Integer[][][]{
                {{null},{null},{null},{null},{null},{null},{null},{null}},
                {{null},{null},{null},{null},{null},{null},{null},{null}},
                {{null},{null},{null},{null},{null},{null},{null},{null}},
                {{null},{null},{null},{null},{null},{null},{null},{null}},
                {{null},{null},{null},{null},{null},{null},{null},{null}},
                {{null},{null},{null},{null},{null},{null},{null},{null}},
                {{null},{4, 0},{null},{null},{null},{null},{null},{null}},
                {{null},{null},{null},{null},{null},{null},{null},{null}},


        };
    }

    public static Integer[][][] ventNW(){
        return new Integer[][][]{
                {{null},{null},{null},{null},{null},{null},{null},{null}},
                {{null},{4, 0},{null},{null},{null},{null},{null},{null}},
                {{null},{null},{null},{null},{null},{null},{null},{null}},
                {{null},{null},{null},{null},{null},{null},{null},{null}},
                {{null},{null},{null},{null},{null},{null},{null},{null}},
                {{null},{null},{null},{null},{null},{null},{null},{null}},
                {{null},{null},{null},{null},{null},{null},{null},{null}},
                {{null},{null},{null},{null},{null},{null},{null},{null}},


        };
    }

        /*
* Tiles
* 0     = none
* 1     = metal floor
* 2     = wood floor (1)
* 3     = wood floor (2)
* 4     = vent
* 5     = yellow + black tape
* 6     = arrow
* 7     = player spawn
* 8     = straight pipe
* 9     = 90 pipe
* 10    = Tee pipe
* 11    = half pipe
* 12    = desk (1)
* 13    = wall
* 14    = wall corner point
* 15    = desk (2)
* 16    = wall corner inside
* 17    = wall U shape
* 18    = wall pillar
* */

}


