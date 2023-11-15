import java.awt.Graphics2D;
import java.awt.Dimension;
import java.awt.Image;
import javax.swing.JPanel;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class Background {
  	private Image bgImage;
  	private int bgImageWidth;      		// width of the background (>= panel Width)
	private int bgImageHeight;

	private Dimension dimension;

 	private int bgX;
	private int backgroundX;
	private int backgroundX2;
	private int bgDX;			// size of the background move (in pixels)


	public Background(JFrame window, String imageFile, int bgDX) {
			dimension = window.getSize();

    		this.bgImage = loadImage(imageFile);
    		bgImageWidth = (int)dimension.getWidth();	// get width of the background
			bgImageHeight = (int)dimension.getHeight()+70;



		System.out.println ("bgImageWidth = " + bgImageWidth);

		if (bgImageWidth < dimension.width)
      			System.out.println("Background width < panel width");

    		this.bgDX = bgDX;

  	}


  	public void moveRight() {

		if (bgX == 0) {
			backgroundX = 0;
			backgroundX2 = bgImageWidth;			
		}

		bgX = bgX - bgDX;

		backgroundX = backgroundX - bgDX;
		backgroundX2 = backgroundX2 - bgDX;

		if ((bgX + bgImageWidth) % bgImageWidth == 0) {
			System.out.println ("Background change: bgX = " + bgX); 
			backgroundX = 0;
			backgroundX2 = bgImageWidth;
		}

  	}


  	public void moveLeft() {
	
		if (bgX == 0) {
			backgroundX = bgImageWidth * -1;
			backgroundX2 = 0;			
		}

		bgX = bgX + bgDX;
				
		backgroundX = backgroundX + bgDX;	
		backgroundX2 = backgroundX2 + bgDX;

		if ((bgX + bgImageWidth) % bgImageWidth == 0) {
			//System.out.println ("Background change: bgX = " + bgX); 
			backgroundX = bgImageWidth * -1;
			backgroundX2 = 0;
		}			
   	}
 

  	public void draw (Graphics2D g2) {
		g2.drawImage(bgImage, backgroundX, -85, bgImageWidth, bgImageHeight, null);
		g2.drawImage(bgImage, backgroundX2, -85, bgImageWidth, bgImageHeight, null);
  	}


  	public Image loadImage (String fileName) {
		return new ImageIcon(fileName).getImage();
  	}

}
