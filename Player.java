import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.JFrame;
import java.awt.Image;
import javax.swing.ImageIcon;
import java.awt.Point;

public class Player {			

   private static final int DX = 8;	// amount of X pixels to move in one keystroke
   private static final int DY = 32;	// amount of Y pixels to move in one keystroke
   int x2, y2;

   private static final int TILE_SIZE = 64;

   private JFrame window;		// reference to the JFrame on which player is drawn
   private TileMap tileMap;
   private BackgroundManager bgManager;
   int lookDirection;

   private int x;			// x-position of player's sprite
   private int y;			// y-position of player's sprite

   Graphics2D g2;
   private Dimension dimension;

   private Image playerImage, playerHurtLImage, playerHurtRImage;
   private Image playerIdleR, playerRunR, playerJumpR, playerIdleL, playerRunL, playerJumpL, playerFallL, playerFallR;
   private Image playerSlashL, playerSlashR;
   private SoundManager soundManager;
   private boolean moving;


   private boolean jumping;
   private int timeElapsed;
   private int startY;

   private boolean goingUp;
   private boolean goingDown;
   boolean idle;
   boolean attack;
   public static int lives =  100;
   public static int keys = 0;
   int level1keys = 0;
   int level2keys = 0;
   public static int totalKeys = 6;
   int time;
  // Animation attacking;
   

   private boolean inAir;
   private int initialVelocity;
   private int startAir;

   public Player (JFrame window, TileMap t, BackgroundManager b) {
      this.window = window;
	  this.lookDirection = 2;
	  time= 0;
	  moving = false;
	 // this.attack = false;
	  //attacking = new Animation(false);

	 

      tileMap = t;			// tile map on which the player's sprite is displayed
      bgManager = b;			// instance of BackgroundManager

      goingUp = goingDown = false;
      inAir = false;

	  playerIdleR = ImageManager.loadImage("images/idle2.gif");
	  playerRunR = ImageManager.loadImage("images/running.gif");
	  playerJumpR = ImageManager.loadImage("images/jumping.gif");
	  playerIdleL = ImageManager.loadImage("images/idle1.gif");
	  playerRunL = ImageManager.loadImage("images/runningleft.gif");
	  playerJumpL = ImageManager.loadImage("images/jumpingleft.gif");
	  playerFallL = ImageManager.loadImage("images/fallingleft.gif");
	  playerFallR = ImageManager.loadImage("images/fallingright.gif");
	  playerSlashL = ImageManager.loadImage("images/attack2.gif");
	  playerSlashR = ImageManager.loadImage("images/attack1.gif");
      playerHurtLImage = ImageManager.loadImage("images/playerHurtL.gif");
      playerHurtRImage = ImageManager.loadImage("images/playerHurtR.gif");
      playerImage = playerIdleR;


	  //attack anim
	//   Image animImage1 = ImageManager.loadImage("images/a1.png");
	//   Image animImage2 = ImageManager.loadImage("images/a2.png");
	//   Image animImage3 = ImageManager.loadImage("images/a3.png");
	//   Image animImage4 = ImageManager.loadImage("images/a4.png");
	//   Image animImage5 = ImageManager.loadImage("images/a5.png");
	//   Image animImage6 = ImageManager.loadImage("images/a6.png");


	//   attacking.addFrame(animImage1, 200);
	//   attacking.addFrame(animImage2, 200);
	//   attacking.addFrame(animImage3, 200);
	//   attacking.addFrame(animImage4, 200);
	//   attacking.addFrame(animImage5, 300);
	//   attacking.addFrame(animImage6, 300);

	soundManager = SoundManager.getInstance();

   }

   public int getLookDir(){
	return this.lookDirection;
   }

   public boolean isPlayerDead(){
		if(this.lives <= 0)
			return true;
		else return false;
   }

   public void loseLife(){
		time = time +1;

		if(time == 1){
			time = 0;
			soundManager.playOrResumeClip("playerHurt", false);
			if(lookDirection == 1)
				playerImage= playerHurtLImage;
			else
				playerImage = playerHurtRImage;

		}


	if(tileMap.getLevel() ==1 && this.lives >=0)
		this.lives = this.lives - 5;

	else if (tileMap.getLevel() == 2 && this.lives >=0)
		this.lives = this.lives - 10;

	if(this.lives < 0)
		this.lives = 0;
   }

   public void addKey(){
		if(this.keys <= 6){
			soundManager.playOrResumeClip("collect", false);
			this.keys = this.keys + 1;
		}
		else this.keys = 6;

		if(tileMap.getLevel()==1){
			if(level1keys <=3)
				level1keys = level1keys+1;
		}
		else if(tileMap.getLevel()==2){
			if(level1keys <=3)
				level2keys = level2keys+1;
		}

   }

   public int getLevel1Keys(){
	return this.level1keys;
   }

   public int getLevel2Keys(){
	return this.level2keys;
   }

   public Point collidesWithTile(int newX, int newY) {

      	int playerWidth = playerImage.getWidth(null);
      	int offsetY = tileMap.getOffsetY();
		int xTile = tileMap.pixelsToTiles(newX);
		int yTile = tileMap.pixelsToTiles(newY - offsetY);

	  if (tileMap.getTile(xTile, yTile) != null) {
	        Point tilePos = new Point (xTile, yTile);
	  	return tilePos;
	  }
	  else {
		return null;
	  }
   }

   public void setAttack(boolean attacking){
		this.attack = attacking;
   }


   public Point collidesWithTileDown (int newX, int newY) {

		int playerWidth = playerImage.getWidth(null);
		int playerHeight = playerImage.getHeight(null);
		int offsetY = tileMap.getOffsetY();
		int xTile = tileMap.pixelsToTiles(newX);
		int yTileFrom = tileMap.pixelsToTiles(y - offsetY);
		int yTileTo = tileMap.pixelsToTiles(newY - offsetY + playerHeight);

	  for (int yTile=yTileFrom; yTile<=yTileTo; yTile++) {
		if (tileMap.getTile(xTile, yTile) != null) {
	        	Point tilePos = new Point (xTile, yTile);
	  		return tilePos;
	  	}
		else {
			if (tileMap.getTile(xTile+1, yTile) != null) {
				int leftSide = (xTile + 1) * TILE_SIZE;
				if (newX + playerWidth > leftSide) {
				    Point tilePos = new Point (xTile+1, yTile);
				    return tilePos;
			        }
			}
		}
	  }

	  return null;
   }


   public Point collidesWithTileUp (int newX, int newY) {

	  int playerWidth = playerImage.getWidth(null);
	  int playerHeight =(int) playerImage.getHeight(null);
      	  int offsetY = tileMap.getOffsetY();
	  int xTile = tileMap.pixelsToTiles(newX);

	  int yTileFrom = tileMap.pixelsToTiles(y - offsetY);
	  int yTileTo = tileMap.pixelsToTiles(newY - offsetY);
	 
	  for (int yTile=yTileFrom; yTile>=yTileTo; yTile--) {
		if (tileMap.getTile(xTile, yTile) != null) {
	        	Point tilePos = new Point (xTile, yTile);
	  		return tilePos;
		}
		else {
			if (tileMap.getTile(xTile+1, yTile) != null) {
				int leftSide = (xTile + 1) * TILE_SIZE;
				if (newX + playerWidth > leftSide) {
				    Point tilePos = new Point (xTile+1, yTile);
				    return tilePos;
			        }
			}
		}
				    
	  }

	  return null;
   }
 
/*

   public Point collidesWithTile(int newX, int newY) {

	 int playerWidth = playerImage.getWidth(null);
	 int playerHeight = playerImage.getHeight(null);

      	 int fromX = Math.min (x, newX);
	 int fromY = Math.min (y, newY);
	 int toX = Math.max (x, newX);
	 int toY = Math.max (y, newY);

	 int fromTileX = tileMap.pixelsToTiles (fromX);
	 int fromTileY = tileMap.pixelsToTiles (fromY);
	 int toTileX = tileMap.pixelsToTiles (toX + playerWidth - 1);
	 int toTileY = tileMap.pixelsToTiles (toY + playerHeight - 1);

	 for (int x=fromTileX; x<=toTileX; x++) {
		for (int y=fromTileY; y<=toTileY; y++) {
			if (tileMap.getTile(x, y) != null) {
				Point tilePos = new Point (x, y);
				return tilePos;
			}
		}
	 }
	
	 return null;
   }
*/

	public void drawBR(Graphics2D g2){
		Rectangle rect = new Rectangle(x2, y2, getWidth(), getHeight());
		g2.setColor(Color.RED);
		g2.draw(rect);
	}


   public synchronized void move (int direction) {
     
      int newX = x;
      Point tilePos = null;

      if (!window.isVisible ()) return;


    //  playerImage = playerIdleR;
	//  System.out.println("AYO  "+ playerImage.getWidth(null));

	//  playerImage= playerLeftImage;
	//  System.out.println("AYO2  "+ playerImage.getWidth(null));


	//IDLE POSITIONS
	if(direction ==-1){
		soundManager.stopClip("running");
		playerImage = playerIdleL;
		moving = false;
		lookDirection = 1;
	}	
	else if(direction == 0){
		soundManager.stopClip("running");
		moving = false;
		playerImage = playerIdleR;
		lookDirection = 2;
	}
		



      if (direction == 1 && !attack) {		// move left
		moving = true;
		if(!jumping){
			soundManager.playOrResumeClip("running", false);
		}
			playerImage = playerRunL;
			lookDirection = 1;
			idle = false;
			attack = false;
			newX = x - DX;
			if (newX < 0) {
				x = 0;
				return;
			}	
	  		tilePos = collidesWithTile(newX, y);
      }	
      else				
      if (direction == 2 && !attack) {		// move right
		moving = true;
			if(!jumping){
				soundManager.playOrResumeClip("running", false);
			}	
			playerImage = playerRunR;
			attack = false;
			lookDirection = 2;
			idle = false;
			int playerWidth = playerImage.getWidth(null);
			newX = x + DX;

      		int tileMapWidth = tileMap.getWidthPixels();

			if (newX + playerImage.getWidth(null) >= tileMapWidth) {
				x = tileMapWidth - playerImage.getWidth(null);
				return;
			}

			tilePos = collidesWithTile(newX+playerWidth, y);			
      }
      else				// jump
      if (direction == 3 && !jumping) {	
			idle = false;
			//attack = false;
			jump(lookDirection);
			int playerHeight = playerImage.getHeight(null);
	  		return;
      }
    
      if (tilePos != null) {  
         if (direction == 1) {
	     System.out.println (": Collision going left");
             x = ((int) tilePos.getX() + 1) * TILE_SIZE;	   // keep flush with right side of tile
	 }
         else
         if (direction == 2) {
	     System.out.println (": Collision going right");
      	     int playerWidth = playerImage.getWidth(null);
             x = ((int) tilePos.getX()) * TILE_SIZE - playerWidth; // keep flush with left side of tile
	 }
      }
      else {
          if (direction == 1) {
	      x = newX;
	      bgManager.moveLeft();
          }
	  else
	  if (direction == 2) {
	      x = newX;
	      bgManager.moveRight();
   	  }

          if (isInAir()) {
	      System.out.println("In the air. Starting to fall.");
	      if (direction == 1) {				// make adjustment for falling on left side of tile
      	          int playerWidth = playerImage.getWidth(null);
		  x = x - playerWidth + DX;
	      }
	      fall(lookDirection);
		  attack = false;
          }
      }

	  if(direction == 4 ){
			if(attack == true){
				soundManager.playOrResumeClip("playerSlash", false);
				if(lookDirection == 2){
					//playerImage = playerSlashR;
				}	
				else {
					//playerImage = playerSlashL;
				}
			}
			else{
				soundManager.stopClip("playerSlash");
				if(attack == false && lookDirection == 1)
					playerImage = playerIdleL;
				else
				 	playerImage = playerIdleR;
			}
		}
   }



   public boolean isInAir() {

      int playerHeight;
      Point tilePos;

      if (!jumping && !inAir) {   
	  playerHeight = playerImage.getHeight(null);
	  tilePos = collidesWithTile(x, y + playerHeight + 1); 	// check below player to see if there is a tile
	
  	  if (tilePos == null)				   	// there is no tile below player, so player is in the air
		return true;
	  else							// there is a tile below player, so the player is on a tile
		return false;
      }

      return false;
   }


   private void fall(int lookDir) {

	// if(lookDir == 1)
	//   	playerImage = playerFallL;

	// 	else
	// 		playerImage = playerFallR;

      jumping = false;
      inAir = true;
      timeElapsed = 0;

      goingUp = false;
      goingDown = true;

      startY = y;
      initialVelocity = 0;
   }


   public void jump (int lookDir) {  

      if (!window.isVisible ()) return;

	//   if(lookDir == 1)
	//   	playerImage = playerJumpL;

	// 	else
	// 		playerImage = playerJumpR;

	  soundManager.playOrResumeClip("jumping", false);

      jumping = true;
      timeElapsed = 0;

      goingUp = true;
      goingDown = false;

      startY = y;
      initialVelocity = 85;
   }

   public boolean getAttack(){
	return this.attack;
   }

   public void update () {
      int distance = 0;
      int newY = 0;

      timeElapsed++;
	

	//   if (!attacking.isStillActive()) {
	// 		attack = false;
	// 	}
	// 	else if(attack ==true)
	// 		attacking.update();


	  if(jumping){
		if(lookDirection == 1){
			playerImage = playerJumpL;
		}
		else{
			playerImage = playerJumpR;
		}
	  }


      if (jumping || inAir) {
	   distance = (int) (initialVelocity * timeElapsed - 
                             6.9 * timeElapsed * timeElapsed);
	   newY = startY - distance;

	   if (newY > y && goingUp) {
		goingUp = false;
 	  	goingDown = true;
	   }

	   if (goingUp) {
		Point tilePos = collidesWithTileUp (x, newY);	
	   	if (tilePos != null) {				// hits a tile going up
		   	System.out.println ("Jumping: Collision Going Up!");

      	  		int offsetY = tileMap.getOffsetY();
			int topTileY = ((int) tilePos.getY()) * TILE_SIZE + offsetY;
			int bottomTileY = topTileY + TILE_SIZE;

		   	y = bottomTileY;
		   	fall(lookDirection);
		}
	   	else {
			y = newY;
			System.out.println ("Jumping: No collision.");
	   	}
            }
	    else
	    if (goingDown) {		
			if(lookDirection == 1)
			playerImage = playerFallL;
	
		  else
			  playerImage = playerFallR;	

		Point tilePos = collidesWithTileDown (x, newY);	
	   	if (tilePos != null) {				// hits a tile going up
		    System.out.println ("Jumping: Collision Going Down!");
			if(lookDirection == 1){
				playerImage = playerIdleL;
			}
			else{
				playerImage = playerIdleR;
			}
	  	    int playerHeight = playerImage.getHeight(null);
		    goingDown = false;

      	    int offsetY = tileMap.getOffsetY();
		    int topTileY = ((int) tilePos.getY()) * TILE_SIZE + offsetY;

	        y = topTileY - playerHeight;
	  	    jumping = false;
		    inAir = false;
	       }
	       else {
		    y = newY;
		    System.out.println ("Jumping: No collision.");
	       }
	   }
      }
   }


   public void moveUp () {

      if (!window.isVisible ()) return;

      y = y - DY;
   }


   public int getX() {
      return x;
   }


   public void setX(int x) {
      this.x = x;
   }


   public int getY() {
      return y;
   }


   public void setY(int y) {
      this.y = y;
   }


   public Image getImage() {
	// if (!attacking.isStillActive()) {
	// 	return playerImage;
	// }
    // else return attacking.getImage();
		if(attack == true ){
			if(lookDirection == 1)
				return playerImage = playerSlashL;
			else
				return playerImage = playerSlashR;
		}
		
	return playerImage;
   }

//if t.level == 1 and enemyobj not a level1 enemy then just return
   public Rectangle2D.Double getBoundingRectangle() {

	int playerWidth, playerHeight;
	if(attack == true){
		if(lookDirection == 1){
			playerWidth = playerSlashL.getWidth(null)+18;
			playerHeight = playerSlashL.getHeight(null)+18;
			x2 = x- 120;
			y2 = y - 40;
		}
		else{
			playerWidth = playerSlashR.getWidth(null)+18;
			playerHeight = playerSlashR.getHeight(null)+18;
			x2 = x- 50;
			y2 = y - 40;
		}
	}
	else{
		playerWidth = playerImage.getWidth(null)+35;
		playerHeight = playerImage.getHeight(null)+25;
		x2 = x- 25;
		y2 = y - 25;
	}
		

		return new Rectangle2D.Double (x2, y2, playerWidth, playerHeight);
 	}
	

		

	public int getWidth(){
		int playerWidth, playerHeight;
		if(attack == true){
			if(lookDirection == 1){
				playerWidth = playerSlashL.getWidth(null)-10;
				playerHeight = playerSlashL.getHeight(null)-10;
			}
			else{
				playerWidth = playerSlashR.getWidth(null)-10;
				playerHeight = playerSlashR.getHeight(null)-10;
			}
		}
		else{
			playerWidth = playerImage.getWidth(null)-10;
			playerHeight = playerImage.getHeight(null)-10;
		}

		return playerWidth;
	}

	public int getHeight(){
		int playerWidth, playerHeight;
		if(attack == true){
			if(lookDirection == 1){
				playerWidth = playerSlashL.getWidth(null)-10;
				playerHeight = playerSlashL.getHeight(null)-10;
			}
			else{
				playerWidth = playerSlashR.getWidth(null)-10;
				playerHeight = playerSlashR.getHeight(null)-10;
			}
		}
		else{
			playerWidth = playerImage.getWidth(null)-10;
			playerHeight = playerImage.getHeight(null)-10;
		}

		return playerHeight;
	}

	public Rectangle2D.Double isNearby () {
		return new Rectangle2D.Double (x-25, y-25, getWidth()+50, getHeight()+50);
	}


}