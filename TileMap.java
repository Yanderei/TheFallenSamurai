import java.awt.Image;
import java.awt.geom.Rectangle2D;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.*;
import java.awt.event.*;		
import java.awt.*;			
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.LinkedList;
import java.util.Iterator;
import javax.swing.JFrame;

/**
    The TileMap class contains the data for a tile-based
    map, including Sprites. Each tile is a reference to an
    Image. Images are used multiple times in the tile map.
    map.
*/

public class TileMap {

    private static final int TILE_SIZE = 64;
    private static final int TILE_SIZE_BITS = 6;

    private Image[][] tiles;
    private int screenWidth, screenHeight;
    private int mapWidth, mapHeight;
    private int offsetY;
    //private Rectangle2D healthBar;

    private LinkedList sprites;
    private Player player;
    private SoundManager soundManager;
    private Image shrine;
    private Enemy1 e1;
    private Enemy1 e2;
    private Enemy1 e3;
    private Enemy1 e4;
    private Enemy1 e5;
    private Enemy1 e6;
    private Obelisk heart;
    private Obelisk heart2;
    //private Heart heart3;
    private Chest chest;
    private Keys keys;
    private Keys keys1;
    private Keys keys2;
    private Keys keys3;
    private Keys keys4;
    private Keys keys5;
    boolean winCon;
    private int time = 0;
    public int level=1; //private static int level
    public boolean levelChange;



    BackgroundManager bgManager;

    private JFrame window;
    private Dimension dimension;

    /**
        Creates a new TileMap with the specified width and
        height (in number of tiles) of the map.
    */
    public TileMap(JFrame window, int width, int height) {

        this.window = window;
        dimension = window.getSize();
        winCon = false;

        screenWidth = dimension.width;
        screenHeight = dimension.height;

        mapWidth = width;
        mapHeight = height;

            // get the y offset to draw all sprites and tiles

        offsetY = screenHeight - tilesToPixels(mapHeight) ;
        System.out.println("offsetY: " + offsetY);

        bgManager = new BackgroundManager (window, 8);

        tiles = new Image[mapWidth][mapHeight];
	    player = new Player (window, this, bgManager);

        //level 1 checkpoint
        heart = new Obelisk (window, player, 4128, 465);
        int heart1Y =  dimension.height - (TILE_SIZE + 90);
        heart.setY(heart1Y+3);
        heart.start();

        //level 2 checkpoint
        heart2 = new Obelisk (window, player, 6375, 465);
        int heart2Y =  dimension.height - (TILE_SIZE + 90);
        heart2.setY(heart2Y+3);
        heart2.start();

        // //level 3 checkpoint
        // heart3 = new Heart (window, player, 500, 465);
        // int heart3Y =  dimension.height - (TILE_SIZE + 90);
        // heart3.setY(heart3Y+3);
        // heart3.start();

        //level 3 checkpoint
        chest = new Chest(window, player, 2000, 600);
        int chestY =dimension.height - (TILE_SIZE + 50);
        chest.setY(chestY);

        shrine = ImageManager.loadImage("images/shrine.png");
   

        sprites = new LinkedList();

        Image playerImage = player.getImage();
        int playerHeight = playerImage.getHeight(null);

        int x, y;
        // x = (dimension.width / 2) + TILE_SIZE;	// position player in middle of screen

        x = 192;					// position player in 'random' location
        y = dimension.height - (TILE_SIZE + playerHeight);

        player.setX(x);
        player.setY(y);


        System.out.println("Player coordinates: " + x + "," + y);
        // System.out.println("Enemy coordinates: " + enemyX + "," + enemyY);

        this.level = getLevel();
        this.levelChange = false;

         //starting animation
         

            //LEVEL 1
            keys = new Keys(window, player, 800, (int)dimension.getHeight()/2 +80);
            keys.start();

            keys2 = new Keys(window, player, 2500, (int)dimension.getHeight()/2 +70);
            keys2.start();

            keys1 = new Keys(window, player, 4128, (int)dimension.getHeight()/2 +70);
            keys1.start();

            //enemy stuff
            e1 = new Enemy1(window, this, bgManager, player);
            e2 = new Enemy1(window, this, bgManager, player);
            e3 = new Enemy1(window, this, bgManager, player);

            Image enemyImage = e1.getImage();
            int enemyHeight = enemyImage.getHeight(null);
    
            int enemyX,  enemyY;
            enemyX = 420;
            enemyY = dimension.height - (TILE_SIZE + enemyHeight); 
    
            e1.setX(enemyX);
            e1.setY(enemyY);
    
            e2.setX(3800);
            e2.setY(enemyY);
    
            e3.setX(2088);
            e3.setY(enemyY);
        
    
            //LEVEL 2
            keys3 = new Keys(window, player, 755, (int)dimension.getHeight()/2 +70);
            keys3.start();

            keys4 = new Keys(window, player, 6375, (int)dimension.getHeight()/2 +60);
            keys4.start();

            keys5 = new Keys(window, player, 4128, (int)dimension.getHeight()/2 +80);
            keys5.start();


            //enemy stuff
            e4 = new Enemy1(window, this, bgManager, player);
            e5 = new Enemy1(window, this, bgManager, player);
            e6 = new Enemy1(window, this, bgManager, player);

            Image enemyImage2 = e4.getImage();
            int enemyHeight2 = enemyImage2.getHeight(null);
    
            int enemyX2,  enemyY2;
            enemyX2 = 460;
            enemyY2 = dimension.height - (TILE_SIZE + enemyHeight2); 
    
            e4.setX(enemyX2);
            e4.setY(enemyY2);
    
            e5.setX(3800);
            e5.setY(enemyY2);
    
            e6.setX(6032);
            e6.setY(enemyY2);
        
            soundManager = SoundManager.getInstance();
    }

    //create enemy entities and call in constructor after level
  

    /**
        Gets the width of this TileMap (number of pixels across).
    */
    public int getWidthPixels() {
	return tilesToPixels(mapWidth);
    }


    /**
        Gets the width of this TileMap (number of tiles across).
    */
    public int getWidth() {
        return mapWidth;
    }


    /**
        Gets the height of this TileMap (number of tiles down).
    */
    public int getHeight() {
        return mapHeight;
    }


    public int getOffsetY() {
	return offsetY;
    }

    
    /**
        Gets the tile at the specified location. Returns null if
        no tile is at the location or if the location is out of
        bounds.
    */
    public Image getTile(int x, int y) {
        if (x < 0 || x >= mapWidth ||
            y < 0 || y >= mapHeight)
        {
            return null;
        }
        else {
            return tiles[x][y];
        }
    }


    /**
        Sets the tile at the specified location.
    */
    public void setTile(int x, int y, Image tile) {
        tiles[x][y] = tile;
    }


    /**
        Gets an Iterator of all the Sprites in this map,
        excluding the player Sprite.
    */

    public Iterator getSprites() {
        return sprites.iterator();
    }

    /**
        Class method to convert a pixel position to a tile position.
    */

    public static int pixelsToTiles(float pixels) {
        return pixelsToTiles(Math.round(pixels));
    }


    /**
        Class method to convert a pixel position to a tile position.
    */

    public static int pixelsToTiles(int pixels) {
        return (int)Math.floor((float)pixels / TILE_SIZE);
    }


    /**
        Class method to convert a tile position to a pixel position.
    */

    public static int tilesToPixels(int numTiles) {
        return numTiles * TILE_SIZE;
    }

    /**
        Draws the specified TileMap.
    */
    public void draw(Graphics2D g2)
    {
        int mapWidthPixels = tilesToPixels(mapWidth);

        // get the scrolling position of the map
        // based on player's position

        int offsetX = screenWidth / 2 -
            Math.round(player.getX()) - TILE_SIZE;
        offsetX = Math.min(offsetX, 0);
        offsetX = Math.max(offsetX, screenWidth - mapWidthPixels);

/*
        // draw black background, if needed
        if (background == null ||
            screenHeight > background.getHeight(null))
        {
            g.setColor(Color.black);
            g.fillRect(0, 0, screenWidth, screenHeight);
        }
*/
	// draw the background first

	bgManager.draw (g2);

        // draw the visible tiles

        int firstTileX = pixelsToTiles(-offsetX);
        int lastTileX = firstTileX + pixelsToTiles(screenWidth) + 1;
        for (int y=0; y<mapHeight; y++) {
            for (int x=firstTileX; x <= lastTileX; x++) {
                Image image = getTile(x, y);
                if (image != null) {
                    g2.drawImage(image,
                        tilesToPixels(x) + offsetX,
                        tilesToPixels(y) + offsetY,
                        null);
                }
            }
        }



        //display panel
        livesDisplay( g2);
        keysDisplay(g2);

        // draw sprites
       
        if(level ==1){
            //heart1
            g2.drawImage(heart.getImage(),
            Math.round(heart.getX()) + offsetX,
            Math.round(heart.getY()), 60, 90, //+ offsetY, 50, 50,
            null);


            //drawing keys
            g2.drawImage(keys.getImage(),
            Math.round(keys.getX()) + offsetX,
            Math.round(keys.getY()), 30, 30, //+ offsetY, 50, 50,
            null);

            g2.drawImage(keys2.getImage(),
            Math.round(keys2.getX()) + offsetX,
            Math.round(keys2.getY()), 30, 30, //+ offsetY, 50, 50,
            null);

            g2.drawImage(keys1.getImage(),
            Math.round(keys1.getX()) + offsetX,
            Math.round(keys1.getY()), 30, 30, //+ offsetY, 50, 50,
            null);
        }

        else if(level ==2){
            g2.drawImage(heart2.getImage(),
            Math.round(heart2.getX()) + offsetX,
            Math.round(heart2.getY()), 60, 90, //+ offsetY, 50, 50,
            null);

             //drawing keys
            g2.drawImage(keys3.getImage(),
            Math.round(keys3.getX()) + offsetX,
            Math.round(keys3.getY()), 30, 30, //+ offsetY, 50, 50,
            null);

            g2.drawImage(keys4.getImage(),
            Math.round(keys4.getX()) + offsetX,
            Math.round(keys4.getY()), 30, 30, //+ offsetY, 50, 50,
            null);

            g2.drawImage(keys5.getImage(),
            Math.round(keys5.getX()) + offsetX,
            Math.round(keys5.getY()), 30, 30, //+ offsetY, 50, 50,
            null);

        }

        else if(level ==3){
            // g2.drawImage(heart3.getImage(),
            // Math.round(heart3.getX()) + offsetX,
            // Math.round(heart3.getY()), 60, 90, //+ offsetY, 50, 50,
            // null);

            g2.drawImage(chest.getImage(),
            Math.round(chest.getX()) + offsetX,
            Math.round(chest.getY()), 70, 50, //+ offsetY, 50, 50,
            null);


            g2.drawImage(shrine,
            Math.round(1825) + offsetX,
            Math.round(dimension.height - (TILE_SIZE + 290)+ 1), 410, 290, //+ offsetY, 50, 50,
            null);

        }


               // draw player

               Image playerImage = player.getImage();

               if(player.getAttack()){
                   if(player.getLookDir()==2){
                       g2.drawImage(player.getImage(),
                       Math.round(player.getX()) + offsetX - 50,
                       Math.round(player.getY()-40),playerImage.getWidth(null)+18, playerImage.getHeight(null) +18,
                       null);
                  
                   }
                   else{
                       g2.drawImage(player.getImage(),
                       Math.round(player.getX()) + offsetX - 120,
                       Math.round(player.getY()-40),playerImage.getWidth(null)+18, playerImage.getHeight(null) +18,
                       null); 
                     
                       
                   }
                   
               }
       
               else if (player.getAttack()== false){
                   if(player.getLookDir() == 1){
                       g2.drawImage(player.getImage(),
                   Math.round(player.getX()) + offsetX - 10,
                   Math.round(player.getY() - 25),playerImage.getWidth(null)+25, playerImage.getHeight(null) + 25,
                   null);
       
                  // player.drawBR(g2);
                   }
       
                   else{
                       g2.drawImage(player.getImage(),
                       Math.round(player.getX()) + offsetX - 25,
                       Math.round(player.getY() - 25),playerImage.getWidth(null)+25, playerImage.getHeight(null) + 25,
                       null);
                       
                    //   player.drawBR(g2);
                   }
                   
                   
               }
   
               //enemies
            if(level == 1){
                if(!e1.getIsDead()){
                    Image enemyImage = e1.getImage();
                //drawing enemy

                //drawing health bar
                if(e1.getIsPunched() && player.getAttack())
                {
                g2.setColor(Color.BLACK);
                g2.drawRect(Math.round(e1.getX()) + offsetX -10,  Math.round(e1.getY())-80, 100, 8);
                Rectangle2D healthBar = new Rectangle2D.Double(e1.getX() + offsetX - 10, e1.getY()-80,e1.getHealth(),8);
                g2.setColor(Color.RED);
                g2.fill(healthBar);
                //e1.drawHealthBar(g2);
                }

                if(e1.getAttack() == true){
                    if(e1.getLookDir() == 1){
                    g2.drawImage(e1.getImage(),
                    Math.round(e1.getX()) + offsetX - 35,
                    Math.round(e1.getY() - 70),enemyImage.getWidth(null)+55, enemyImage.getHeight(null) + 55,
                    null);

                   // e1.drawBR(g2);
                    }

                    else{
                        g2.drawImage(e1.getImage(),
                        Math.round(e1.getX()) + offsetX - 45,
                        Math.round(e1.getY() - 70),enemyImage.getWidth(null)+55, enemyImage.getHeight(null) + 55,
                        null);
                        
                     //   e1.drawBR(g2);
                    }
                }
                else{
                    if(e1.getLookDir() == 1){
                        g2.drawImage(e1.getImage(),
                        Math.round(e1.getX()) + offsetX - 10,
                        Math.round(e1.getY() - 25),enemyImage.getWidth(null)+30, enemyImage.getHeight(null) + 30,
                        null);
                    //    e1.drawBR(g2);
                    }
                    else{
                        g2.drawImage(e1.getImage(),
                        Math.round(e1.getX()) + offsetX - 25,
                        Math.round(e1.getY() - 25),enemyImage.getWidth(null)+30, enemyImage.getHeight(null) + 30,
                        null);
                    //    e1.drawBR(g2);
                    }
                
                }

            }


            if(!e2.getIsDead()){
                Image enemyImage2 = e2.getImage();
                //drawing enemy
        
                //drawing health bar
                if(e2.getIsPunched() && player.getAttack())
                {
                   g2.setColor(Color.BLACK);
                   g2.drawRect(Math.round(e2.getX()) + offsetX -10,  Math.round(e2.getY())-80, 100, 8);
                   Rectangle2D healthBar= new Rectangle2D.Double(e2.getX() + offsetX - 10, e2.getY()-80,e2.getHealth(),8);
                   g2.setColor(Color.RED);
                   g2.fill(healthBar);
                //e2.drawHealthBar(g2);
                }
        
                if(e2.getAttack() == true){
                    if(e2.getLookDir() == 1){
                        g2.drawImage(e2.getImage(),
                    Math.round(e2.getX()) + offsetX - 35,
                    Math.round(e2.getY() - 70),enemyImage2.getWidth(null)+55, enemyImage2.getHeight(null) + 55,
                    null);
        
                    //e2.drawBR(g2);
                    }
        
                    else{
                        g2.drawImage(e2.getImage(),
                        Math.round(e2.getX()) + offsetX - 45,
                        Math.round(e2.getY() - 70),enemyImage2.getWidth(null)+55, enemyImage2.getHeight(null) + 55,
                        null);
                        
                        //e2.drawBR(g2);
                    }
                }
                else{
                    if(e2.getLookDir() == 1){
                        g2.drawImage(e2.getImage(),
                        Math.round(e2.getX()) + offsetX - 10,
                        Math.round(e2.getY() - 25),enemyImage2.getWidth(null)+30, enemyImage2.getHeight(null) + 30,
                        null);
                        //e1.drawBR(g2);
                    }
                    else{
                        g2.drawImage(e2.getImage(),
                        Math.round(e2.getX()) + offsetX - 25,
                        Math.round(e2.getY() - 25),enemyImage2.getWidth(null)+30, enemyImage2.getHeight(null) + 30,
                        null);
                        //e1.drawBR(g2);
                    }
                
                }
            }
            if(!e3.getIsDead()){
                Image enemyImage3 = e3.getImage();
                //drawing enemy
        
                //drawing health bar
                if(e3.getIsPunched() && player.getAttack())
                {
                   g2.setColor(Color.BLACK);
                   g2.drawRect(Math.round(e3.getX()) + offsetX -10,  Math.round(e3.getY())-80, 100, 8);
                   Rectangle2D healthBar= new Rectangle2D.Double(e3.getX() + offsetX - 10, e3.getY()-80,e3.getHealth(),8);
                   g2.setColor(Color.RED);
                   g2.fill(healthBar);
                //e2.drawHealthBar(g2);
                }
        
                if(e3.getAttack() == true){
                    if(e3.getLookDir() == 1){
                        g2.drawImage(e3.getImage(),
                    Math.round(e3.getX()) + offsetX - 35,
                    Math.round(e3.getY() - 70),enemyImage3.getWidth(null)+55, enemyImage3.getHeight(null) + 55,
                    null);
        
                    //e2.drawBR(g2);
                    }
        
                    else{
                        g2.drawImage(e3.getImage(),
                        Math.round(e3.getX()) + offsetX - 45,
                        Math.round(e3.getY() - 70),enemyImage3.getWidth(null)+55, enemyImage3.getHeight(null) + 55,
                        null);
                        
                        //e2.drawBR(g2);
                    }
                }
                else{
                    if(e3.getLookDir() == 1){
                        g2.drawImage(e3.getImage(),
                        Math.round(e3.getX()) + offsetX - 10,
                        Math.round(e3.getY() - 25),enemyImage3.getWidth(null)+30, enemyImage3.getHeight(null) + 30,
                        null);
                        //e1.drawBR(g2);
                    }
                    else{
                        g2.drawImage(e3.getImage(),
                        Math.round(e3.getX()) + offsetX - 25,
                        Math.round(e3.getY() - 25),enemyImage3.getWidth(null)+30, enemyImage3.getHeight(null) + 30,
                        null);
                        //e1.drawBR(g2);
                    }
                
                }
            }

        }
        else if(level == 2){
            if(!e4.getIsDead()){
                    Image enemyImage = e4.getImage();
                //drawing enemy

                //drawing health bar
                if(e4.getIsPunched()  && player.getAttack())
                {
                g2.setColor(Color.BLACK);
                g2.drawRect(Math.round(e4.getX()) + offsetX -10,  Math.round(e4.getY())-80, 100, 8);
                Rectangle2D healthBar = new Rectangle2D.Double(e4.getX() + offsetX - 10, e4.getY()-80,e4.getHealth(),8);
                g2.setColor(Color.RED);
                g2.fill(healthBar);
                //e1.drawHealthBar(g2);
                }

                if(e4.getAttack() == true){
                    if(e4.getLookDir() == 1){
                    g2.drawImage(e4.getImage(),
                    Math.round(e4.getX()) + offsetX - 35,
                    Math.round(e4.getY() - 70),enemyImage.getWidth(null)+55, enemyImage.getHeight(null) + 55,
                    null);

                   // e1.drawBR(g2);
                    }

                    else{
                        g2.drawImage(e4.getImage(),
                        Math.round(e4.getX()) + offsetX - 45,
                        Math.round(e4.getY() - 70),enemyImage.getWidth(null)+55, enemyImage.getHeight(null) + 55,
                        null);
                        
                     //   e1.drawBR(g2);
                    }
                }
                else{
                    if(e4.getLookDir() == 1){
                        g2.drawImage(e4.getImage(),
                        Math.round(e4.getX()) + offsetX - 10,
                        Math.round(e4.getY() - 25),enemyImage.getWidth(null)+30, enemyImage.getHeight(null) + 30,
                        null);
                    //    e1.drawBR(g2);
                    }
                    else{
                        g2.drawImage(e4.getImage(),
                        Math.round(e4.getX()) + offsetX - 25,
                        Math.round(e4.getY() - 25),enemyImage.getWidth(null)+30, enemyImage.getHeight(null) + 30,
                        null);
                    //    e4.drawBR(g2);
                    }
                
                }

            }


            if(!e5.getIsDead()){
                Image enemyImage2 = e5.getImage();
                //drawing enemy
        
                //drawing health bar
                if(e5.getIsPunched() && player.getAttack())
                {
                   g2.setColor(Color.BLACK);
                   g2.drawRect(Math.round(e5.getX()) + offsetX -10,  Math.round(e5.getY())-80, 100, 8);
                   Rectangle2D healthBar= new Rectangle2D.Double(e5.getX() + offsetX - 10, e5.getY()-80,e5.getHealth(),8);
                   g2.setColor(Color.RED);
                   g2.fill(healthBar);
                //e2.drawHealthBar(g2);
                }
        
                if(e5.getAttack() == true){
                    if(e5.getLookDir() == 1){
                        g2.drawImage(e5.getImage(),
                    Math.round(e5.getX()) + offsetX - 35,
                    Math.round(e5.getY() - 70),enemyImage2.getWidth(null)+55, enemyImage2.getHeight(null) + 55,
                    null);
        
                    //e2.drawBR(g2);
                    }
        
                    else{
                        g2.drawImage(e5.getImage(),
                        Math.round(e5.getX()) + offsetX - 45,
                        Math.round(e5.getY() - 70),enemyImage2.getWidth(null)+55, enemyImage2.getHeight(null) + 55,
                        null);
                        
                        //e2.drawBR(g2);
                    }
                }
                else{
                    if(e5.getLookDir() == 1){
                        g2.drawImage(e5.getImage(),
                        Math.round(e5.getX()) + offsetX - 10,
                        Math.round(e5.getY() - 25),enemyImage2.getWidth(null)+30, enemyImage2.getHeight(null) + 30,
                        null);
                        //e1.drawBR(g2);
                    }
                    else{
                        g2.drawImage(e5.getImage(),
                        Math.round(e5.getX()) + offsetX - 25,
                        Math.round(e5.getY() - 25),enemyImage2.getWidth(null)+30, enemyImage2.getHeight(null) + 30,
                        null);
                        //e1.drawBR(g2);
                    }
                
                }
            }
            if(!e6.getIsDead()){
                Image enemyImage3 = e6.getImage();
                //drawing enemy
        
                //drawing health bar
                if(e6.getIsPunched()  && player.getAttack())
                {
                   g2.setColor(Color.BLACK);
                   g2.drawRect(Math.round(e6.getX()) + offsetX -10,  Math.round(e6.getY())-80, 100, 8);
                   Rectangle2D healthBar= new Rectangle2D.Double(e6.getX() + offsetX - 10, e6.getY()-80,e6.getHealth(),8);
                   g2.setColor(Color.RED);
                   g2.fill(healthBar);
                //e2.drawHealthBar(g2);
                }
        
                if(e6.getAttack() == true){
                    if(e6.getLookDir() == 1){
                        g2.drawImage(e6.getImage(),
                    Math.round(e6.getX()) + offsetX - 35,
                    Math.round(e6.getY() - 70),enemyImage3.getWidth(null)+55, enemyImage3.getHeight(null) + 55,
                    null);
        
                    //e2.drawBR(g2);
                    }
        
                    else{
                        g2.drawImage(e6.getImage(),
                        Math.round(e6.getX()) + offsetX - 45,
                        Math.round(e6.getY() - 70),enemyImage3.getWidth(null)+55, enemyImage3.getHeight(null) + 55,
                        null);
                        
                        //e2.drawBR(g2);
                    }
                }
                else{
                    if(e6.getLookDir() == 1){
                        g2.drawImage(e6.getImage(),
                        Math.round(e6.getX()) + offsetX - 10,
                        Math.round(e6.getY() - 25),enemyImage3.getWidth(null)+30, enemyImage3.getHeight(null) + 30,
                        null);
                        //e1.drawBR(g2);
                    }
                    else{
                        g2.drawImage(e6.getImage(),
                        Math.round(e6.getX()) + offsetX - 25,
                        Math.round(e6.getY() - 25),enemyImage3.getWidth(null)+30, enemyImage3.getHeight(null) + 30,
                        null);
                        //e1.drawBR(g2);
                    }
                
                }
            }

        }


        
        


/*
        // draw sprites
        Iterator i = map.getSprites();
        while (i.hasNext()) {
            Sprite sprite = (Sprite)i.next();
            int x = Math.round(sprite.getX()) + offsetX;
            int y = Math.round(sprite.getY()) + offsetY;
            g.drawImage(sprite.getImage(), x, y, null);

            // wake up the creature when it's on screen
            if (sprite instanceof Creature &&
                x >= 0 && x < screenWidth)
            {
                ((Creature)sprite).wakeUp();
            }
        }
*/

    }

    public void livesDisplay(Graphics2D g) {
        Font font = new Font("SansSerif", Font.BOLD, 24);
        g.setFont(font);
    
        String msg = "Life: " + Player.lives + "%";
        int x = window.getWidth()/2 - 180;
        int y = window.getHeight()/16;
    
        if(Player.lives >= 50)
            g.setColor(Color.GREEN);

        else if(Player.lives < 50 && Player.lives >= 25)
            g.setColor(Color.ORANGE);

        else if(Player.lives < 25)
            g.setColor(Color.RED);  
            
            
        g.drawString(msg, x, y);
    }

    public void keysDisplay(Graphics2D g) {
        Font font = new Font("SansSerif", Font.BOLD, 24);
        g.setFont(font);
        String msg = "";
    
        if(level >=3){
            msg = "Total keys collected: " +Player.totalKeys+ "/"+ Player.totalKeys;
        }

        else{
            if(level == 1)
                msg = "Keys collected in level " +this.level + ": " + player.getLevel1Keys() + "/"+ 3;
            else if(level ==2)
                msg = "Keys collected in level " +this.level + ": " + player.getLevel2Keys() + "/"+ 3;
        }
        
        int x = window.getWidth()/2 + 30;
        int y = window.getHeight()/16;
    
        g.setColor(Color.YELLOW);
        g.drawString(msg, x, y);
    }
    




    public void setPlayerAttack(boolean attacking){
        player.setAttack(attacking);
    }

    public void moveLeft() {
        int x, y;
        x = player.getX();
        y = player.getY();

        String mess = "Going left. x = " + x + " y = " + y;
        System.out.println(mess);

        player.move(1);

    }

    public void playerIdleL(){
        player.move(-1);
    }

    
    public void playerIdleR(){
        player.move(0);
    }

    public void playerAttack(){
        player.move(4);
    }

    public void moveRight() {
        int x, y;
        x = player.getX();
        y = player.getY();

        String mess = "Going right. x = " + x + " y = " + y;
        System.out.println(mess);

        player.move(2);

    }


    public void jump() {
        int x, y;
        x = player.getX();
        y = player.getY();

        String mess = "Jumping. x = " + x + " y = " + y;
        System.out.println(mess);

        player.move(3);

    }


    public void update() {
	    player.update();
        // e1.update();
        // e2.update();
        // e3.update();

         
        if(level ==1){
            if (heart.collidesWithPlayer() && player.getLevel1Keys() == 3 && !player.getAttack()) {
                soundManager.playOrResumeClip("warp", false);
                endLevel();
                return;
            }
            keys.update();
            keys2.update();
            keys1.update();

            if(!e1.getIsDead())
                e1.update();
            if(!e2.getIsDead())
                e2.update();
            if(!e3.getIsDead())
                e3.update();
        }
      
        else if (level == 2){
            if (heart2.collidesWithPlayer() && player.getLevel2Keys() == 3 && !player.getAttack()){
                soundManager.playOrResumeClip("warp", false);
                endLevel();
                return;
            }

            keys3.update();
            keys4.update();
            keys5.update();

            if(!e4.getIsDead())
                e4.update();
            if(!e5.getIsDead())
                e5.update();
            if(!e6.getIsDead())
                e6.update();
        } 

        else if(level ==3){
            // if (heart3.collidesWithPlayer()){
            //     endLevel();
            //     return;
            // }
            chest.update();

            if(chest.collidesWithPlayer()){
                winCon = true;
                time = time+1;
                if(time == 2){
                    endLevel();
                    time = 2;
                }

            }
        }

        heart.update();
        heart2.update();
        //heart3.update();

        // if(level == 1 && player.getLevel1Keys() == 3)
        //     if (heart.collidesWithPlayer()) {
        //         endLevel();
        //     }
        // else if(level ==2)
        //     if (heart2.collidesWithPlayer()) {
        //         endLevel();
        //     }
        // else if(level == 3)
        //     if (heart3.collidesWithPlayer()) {
        //         endLevel();
        // }
    }


	public void endLevel() {
		this.level = this.level + 1;
		levelChange = true;
	}

    public boolean isPlayerDead(){
        return player.isPlayerDead();
    }

    public boolean getLevelChange(){
        return this.levelChange;
    }

    public int  getLevel(){//might have to change to static
        return this.level;
    }

    public void setLvl(int level){
        this.level = level;
    }

    public boolean getWinCon(){
        return this.winCon;
    }




}
