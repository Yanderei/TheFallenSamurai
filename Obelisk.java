import java.util.Random;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Image;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;


public class Obelisk {

	private static final int XSIZE = 60;		// width of the image
	private static final int YSIZE = 90;		// height of the image
	//private static final int DX = 2;		// amount of pixels to move in one update
	private static final int YPOS = 150;		// vertical position of the image

	private JFrame panel;				// JPanel on which image will be drawn
	private Dimension dimension;
	private int x;
	private int y;
	private int dx;

	private Player player;
	

	private Image spriteImage;			// image for sprite
	Animation animation;

	//Graphics2D g2;

	int time, timeChange;				// to control when the image is grayed
	boolean originalImage, grayImage;


	public Obelisk (JFrame panel, Player player, int x, int y) {
		this.panel = panel;
		animation = new Animation(true);

		//Graphics g = window.getGraphics ();
		//g2 = (Graphics2D) g;

		dimension = panel.getSize();
		Random random = new Random();
		//x = 4174;	
		setPos(x, y);
		dx = 0;

		this.player = player;

		time = 0;				// range is 0 to 10
		timeChange = 1;				// set to 1
		originalImage = true;
		grayImage = false;

		//spriteImage = ImageManager.loadImage("images/Heart.png");
		Image stripImage = ImageManager.loadImage("images/obelisk.png");

        int imageWidth = (int) stripImage.getWidth(null) / 13;
		int imageHeight = stripImage.getHeight(null);

		for (int i=0; i<13; i++) {

			BufferedImage frameImage = new BufferedImage (imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = (Graphics2D) frameImage.getGraphics();
     
			g.drawImage(stripImage, 
					0, 0, imageWidth, imageHeight,
					i*imageWidth, 0, (i*imageWidth)+imageWidth, imageHeight,
					null);

			animation.addFrame(frameImage, 100);
		}
//GET DIMENSION FOR END OF SCREEN
	}

	public void setPos(int x, int y){
		this.x= x;
		this.y = y;
	}

	public void draw (Graphics2D g2) {

		g2.drawImage(animation.getImage(), x, y, XSIZE, YSIZE, null);

	}

	public void start() {
		animation.start();
	}

    public void update(){
        if (!animation.isStillActive()) {
			return;
		}

		animation.update();
    }


	public boolean collidesWithPlayer () {
		Rectangle2D.Double myRect = getBoundingRectangle();
		Rectangle2D.Double playerRect = player.getBoundingRectangle();
		
		if (myRect.intersects(playerRect)) {
			System.out.println ("Collision with player!");
			return true;
		}
		else
			return false;
	}


	public Rectangle2D.Double getBoundingRectangle() {
		return new Rectangle2D.Double (x+30, y+15, XSIZE-30, YSIZE-30);
	}


	// public void update() {				
	// 	x = x + dx;

	// 	if (x < 4064 || x > 4184)
	// 		dx = dx * -1;

	// }


   	public int getX() {
      		return x;
   	}

	public int getWidth(){
		return this.XSIZE;
	}

	public int getHeight(){
		return this.YSIZE;
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
      		return animation.getImage();
   	}

}