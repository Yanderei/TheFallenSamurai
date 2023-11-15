import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.security.spec.X509EncodedKeySpec;

import javax.swing.JPanel;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.swing.JFrame;
import java.awt.Image;
import javax.swing.ImageIcon;
import java.awt.Point;

public class Enemy1 {			

   private static final int DX = 8;	// amount of X pixels to move in one keystroke
   private static final int DY = 32;	// amount of Y pixels to move in one keystroke
   int x2, y2;

   private static final int TILE_SIZE = 64;

   private JFrame window;		// reference to the JFrame on which player is drawn
   private TileMap tileMap;
   private BackgroundManager bgManager;
   int lookDirection;
   private Rectangle2D healthBar;
   private int health;
   boolean isPunched;
   boolean isDead;

   private int x;			// x-position of player's sprite
   private int y;			// y-position of player's sprite

   Graphics2D g2;
   private Dimension dimension;

   private Image playerImage, playerLeftImage, playerRightImage;
   private Image playerIdleR, playerRunR, playerHurtR, playerIdleL, playerRunL, playerHurtL, playerFallL, playerFallR;
   private Image playerSlashL, playerSlashR;
   Player mc;

   private boolean jumping;
   private int timeElapsed;
   private int startY;

   private boolean goingUp;
   private boolean goingDown;
   boolean idle;
   int time;
   int t1;
   boolean collision;
   boolean attack;
   boolean playedOnce;
   int oldx;
   boolean yHigher;
   private SoundManager soundManager;
  // Animation attacking;
   

   private boolean inAir;
   private int initialVelocity;
   private int startAir;

   public Enemy1 (JFrame window, TileMap t, BackgroundManager b, Player mc) {
      this.window = window;
	  this.lookDirection = 1;
	  this.mc = mc;
	  this.isPunched = false;
	  this.health =100;
	  attack = false;
	  collision = false;
	  isDead = false;
	  yHigher = false;
	  playedOnce = false;
	 // this.attack = false;
	  //attacking = new Animation(false);

	 

      tileMap = t;			// tile map on which the player's sprite is displayed
      bgManager = b;			// instance of BackgroundManager

      goingUp = goingDown = false;
      inAir = false;

	  playerIdleR = ImageManager.loadImage("images/enemyIdleR.gif");
	  playerIdleL = ImageManager.loadImage("images/enemyIdleL.gif");

	  playerRunR = ImageManager.loadImage("images/enemyRunR.gif");
	  playerRunL = ImageManager.loadImage("images/enemyRunL.gif");
	  playerHurtR = ImageManager.loadImage("images/enemyHurtR.gif");
	  playerHurtL = ImageManager.loadImage("images/enemyHurtL.gif");


	  //NOT USED
	  playerFallL = ImageManager.loadImage("images/enemyIdleL.gif");
	  playerFallR = ImageManager.loadImage("images/enemyIdleR.gif");
	  //

	  playerSlashL = ImageManager.loadImage("images/enemyAttackL.gif");
	  playerSlashR = ImageManager.loadImage("images/enemyAttackR.gif");

      playerLeftImage = ImageManager.loadImage("images/enemyIdleL.gif");
      playerRightImage = ImageManager.loadImage("images/enemyIdleR.gif");

      playerImage = playerIdleL;

	  soundManager = SoundManager.getInstance();

	  time = 0;
	  t1= 0;

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

	

   }

   public int getLookDir(){
	return this.lookDirection;
   }

   public void setIsDead(boolean dead){
		this.isDead = dead;
   }

   public boolean getIsDead(){
	return this.isDead;
}


   public Point collidesWithTile(int newX, int newY) {
		collision =true;
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

   public void setLocationDead(){
	x = -999;
	y = -999;
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
		if(!yHigher)
			soundManager.stopClip("runningEnemy");
		
		playerImage = playerIdleL;
		attack =false;
		x=x+0;
		lookDirection = 1;
	}	
	else if(direction == 0){
		if(!yHigher)
			soundManager.stopClip("runningEnemy");

		playerImage = playerIdleR;
		attack =false;
		x=x+0;
		lookDirection = 2;
	}
		

      if (direction == 1) {		// move left
			soundManager.playOrResumeClip("runningEnemy", false);
			playerImage = playerRunL;
			lookDirection = 1;
			idle = false;
			attack = false;
			collision = false;
			oldx = x;
			newX = x - DX;
			if (newX < 0) {
				x = 0;
				return;
			}	
	  		tilePos = collidesWithTile(newX, y);
      }	
      else				
      if (direction == 2) {		// move right
			playerImage = playerRunR;
			soundManager.playOrResumeClip("runningEnemy", false);
			attack = false;
			collision = false;
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
	  else  if(direction == 4){
				if(attack == true){
					soundManager.playOrResumeClip("enemySlash", false);
					if(lookDirection == 2){
						playerImage = playerSlashR;
					}	
					else {
						playerImage = playerSlashL;
					}
				}
				else{
					soundManager.stopClip("enemySlash");
					if(attack == false && lookDirection == 1)
						playerImage = playerIdleL;
					else
						playerImage = playerIdleR;
				}
			}
			
	  	else if(direction == 7){//right
			int playerWidth = playerImage.getWidth(null);
      		int tileMapWidth = tileMap.getWidthPixels();
			if (x + playerImage.getWidth(null) >= tileMapWidth) {
				x = tileMapWidth - playerImage.getWidth(null);
				return;
			}

			tilePos = collidesWithTile(x+playerWidth, y);		
		
	  }
	  else 
	  if (direction == 8) {		// move left
		lookDirection = 1;
		idle = false;
		attack = false;
		oldx = x;
		int newX2 = x;
		if (newX2 < 0) {
			x = 0;
			return;
		}	
	  		tilePos = collidesWithTile(newX2, y);
  	}	
 
      if (tilePos != null) { 
			soundManager.stopClip("runningEnemy");
			collision = true;
			attack = false; 
			if (direction == 1 ) {
				soundManager.stopClip("runningEnemy");
				System.out.println (": Collision going left");
				playerImage = playerIdleL;
				x = ((int) tilePos.getX() + 1) * TILE_SIZE;	   // keep flush with right side of tile
		}
			else
			if (direction == 2) {
			System.out.println (": Collision going right");
				soundManager.stopClip("runningEnemy");
				int playerWidth = playerImage.getWidth(null);
				playerImage = playerIdleR;
				x = ((int) tilePos.getX()) * TILE_SIZE - playerWidth; // keep flush with left side of tile
		}
		else if(direction == 7){
			int playerWidth = playerImage.getWidth(null);
				playerImage = playerIdleR;
				x = ((int) tilePos.getX()) * TILE_SIZE - playerWidth; // keep flush with left side of tile
		}
		else if(direction == 8){
			int playerWidth = playerImage.getWidth(null);
			playerImage = playerIdleL;
				x = ((int) tilePos.getX() + 1) * TILE_SIZE + playerWidth-20;	   // keep flush with right side of tile
		}
      }

      else {
          if (direction == 1) {
			x = newX;
		
          }
	  else
	  if (direction == 2) {
	      x = newX;
	  
   	  }

          if (isInAir()) {
			System.out.println("In the air. Starting to fall.");
			if (direction == 1) {				// make adjustment for falling on left side of tile
					int playerWidth = playerImage.getWidth(null);
					x = x - playerWidth + DX;
			}
			fall(lookDirection);
			
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
	

	  //if player within range
	  if(Math.abs(this.x - mc.getX()) <= 600){
		
		if(!playedOnce){
			soundManager.playOrResumeClip("detected", false);
			playedOnce = true;
		}
			

		if(this.y > mc.getY()+150 ){
			yHigher = true;
			System.out.println("PLAYER Y TOO HIGH");
			if(this.getLookDir() == 1){
				move(-1);
			}
			else{
				move(0);
			}
				
		}

		if(this.x > mc.getX() + mc.getWidth() + 25){
			yHigher = false;
			if(mc.getAttack()){
				attack =false;
				playerImage = playerIdleL;
			}
			else
				move(1);
				//this.x = this.x +DX;
		}
			

		else
		if(this.x + getWidth() + 35< mc.getX()){
			yHigher = false;
			if(mc.getAttack()){
				attack =false;
				playerImage = playerIdleR;
			}
			else
				move(2);
				//this.x = this.x +DX;
		}
			

		//if player is attacking, enemy will not approach

		if(collidesWithPlayerProxL() ){
			isPunched = false;
			System.out.println("COLLISION WITH PLAYERP333");
			if(!mc.getAttack() && collidesWithPlayer()){
				isPunched = false;
				System.out.println("COLLISION WITH PLAYERP");
				if(this.getLookDir() == 1){
					attack = true;
					move(4);
				}
				else{
					attack = true;
					move(4);
				}

				//taking away a player life after a certain amt of times getting hit
				t1 = t1+1;
				if(t1 == 5){
					mc.loseLife();
					t1= 0;
				}	

			}
			else{
				attack = false;
				if(lookDirection == 1){
					playerImage = playerIdleL;
				}
				else playerImage = playerIdleR;
			}
	
		}

		if(collidesWithPlayer()){
			if(mc.getAttack()){
				isPunched = true;
				soundManager.playOrResumeClip("enemyHurt", false);
				health = health-2;
				if(health==0)
				  {
				   //setLocationDead();
				   soundManager.playOrResumeClip("enemyDead", false);
				   isDead = true;
				   health=0;
				  }
				if(mc.getLookDir() == 1){
					time = time+1;
					playerImage = playerHurtR;
					if(time ==5){
						 x = x - 60;
						move(8);
						time= 0;
					}
					
				}
				else{
					time = time+1;
					playerImage = playerHurtL;
					if(time ==5){
						x = x + 60;
						move(7);
						time= 0;
					}
				}

			}

		}


	  }

	  else{
		if(lookDirection == 1){
			playerImage = playerIdleL;
			isPunched = false;
		}
		else{
			playerImage = playerIdleR;
			isPunched = false;
		}
	  }


	  if(jumping){
		if(lookDirection == 1){
			//playerImage = playerJumpL;
		}
		else{
			//playerImage = playerJumpR;
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
		if(attack == true){
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
			playerWidth = playerSlashL.getWidth(null)+55;
			playerHeight = playerSlashL.getHeight(null)+55;
			x2 = x- 35;
			y2= y-70;
		}
		else{
			playerWidth = playerSlashR.getWidth(null)+55;
			playerHeight = playerSlashR.getHeight(null)+55;
			x2 = x- 45;
			y2= y-70;
		}
	}
	else{
		playerWidth = playerImage.getWidth(null)+25;
		playerHeight = playerImage.getHeight(null)+30;
		x2 = x- 5;
		y2= y-25;
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
			playerWidth = playerImage.getWidth(null);
			playerHeight = playerImage.getHeight(null);
		}

		return playerHeight;
	}


	 public boolean collidesWithPlayer() {
		Rectangle2D.Double myRect = getBoundingRectangle();
		Rectangle2D.Double playerRect = mc.getBoundingRectangle();
		
	 
		return myRect.intersects(playerRect); 
	 }

	 
	 public boolean collidesWithPlayerProxL() {
		Rectangle2D.Double myRect = getBoundingRectangle();
		Rectangle2D.Double playerRect = mc.isNearby();
		
	 
		return myRect.intersects(playerRect); 
	 }

	 public void drawBR(Graphics2D g2){
		Rectangle rect = new Rectangle(x2, y2, getWidth(), getHeight());
		g2.setColor(Color.RED);
		g2.draw(rect);
	}

	// public void drawHealthBar(Graphics2D g2){
	// 	g2.setColor(Color.BLACK);
	// 	g2.drawRect(x, y-80, 100, 8);
	// 	healthBar = new Rectangle2D.Double(x, y-80, getHealth(),8);
	// 	g2.setColor(Color.RED);
	// 	g2.fill(healthBar);
	// }

		public boolean getIsPunched(){
			return this.isPunched;
		}

		public int getHealth(){
			return this.health;
		}


}