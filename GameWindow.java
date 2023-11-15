import javax.swing.*;			// need this for GUI objects
import java.awt.*;			// need this for certain AWT classes
import java.awt.image.BufferedImage;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferStrategy;	// need this to implement page flipping


public class GameWindow extends JFrame implements
				Runnable,
				KeyListener,
				MouseListener,
				MouseMotionListener
{
  	private static final int NUM_BUFFERS = 2;	// used for page flipping

	private int pWidth, pHeight;     		// width and height of screen

	private Thread gameThread = null;            	// the thread that controls the game
	private volatile boolean isRunning = false;    	// used to stop the game thread

	private BufferedImage image;			// drawing area for each frame

	private Image quit1Image;			// first image for quit button
	private Image quit2Image;			// second image for quit button

	private Image pause1;			// first image for quit button
	private Image pause2;			// second image for quit button

	private Image resume1;			// first image for quit button
	private Image resume2;			// second image for quit button
	boolean isPlayedOnce;
	boolean isPlayedOnce2;


	private boolean finishedOff = false;		// used when the game terminates

	private volatile boolean isOverQuitButton = false;
	private Rectangle quitButtonArea;		// used by the quit button

	private volatile boolean isOverPauseButton = false;
	private Rectangle pauseButtonArea;		// used by the pause 'button'
	private volatile boolean isPaused = false;

	private volatile boolean isOverStopButton = false;
	private Rectangle stopButtonArea;		// used by the stop 'button'
	private volatile boolean isStopped = false;

	private volatile boolean isOverShowAnimButton = false;
	private Rectangle showAnimButtonArea;		// used by the show animation 'button'
	private volatile boolean isAnimShown = false;

	private volatile boolean isOverPauseAnimButton = false;
	private Rectangle pauseAnimButtonArea;		// used by the pause animation 'button'
	private volatile boolean isAnimPaused = false;
   
	private GraphicsDevice device;			// used for full-screen exclusive mode 
	private Graphics gScr;
	private BufferStrategy bufferStrategy;

	private SoundManager soundManager;
	TileMapManager tileManager;
	TileMap	tileMap;

	private boolean levelChange;
	private int level;
	private boolean gameOver;
	private boolean playerDead;
	private boolean winCon;

	// SMOOTH MOVEMENT STEP 1 out of 5 (create vars)
	// v=================================================v
	private boolean movingLeft;
	private boolean movingRight;
	// ^=================================================^
	private int lookDirection;

	public GameWindow() {
 
		super("Tiled Bat and Ball Game: Full Screen Exclusive Mode");

		initFullScreen();

		quit1Image = ImageManager.loadImage("images/Quit1.png");
		quit2Image = ImageManager.loadImage("images/Quit2.png");
		pause1 = ImageManager.loadImage("images/LightPause.png");
		pause2 = ImageManager.loadImage("images/DarkPause.png");
		resume1 = ImageManager.loadImage("images/LightPlay.png");
		resume2 = ImageManager.loadImage("images/DarkPlay.png");


		setButtonAreas();

		addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);

		soundManager = SoundManager.getInstance();
		image = new BufferedImage (pWidth, pHeight, BufferedImage.TYPE_INT_RGB);
		soundManager = SoundManager.getInstance();

		lookDirection = 0;
		// SMOOTH MOVEMENT STEP 2 out of 5 (initialize vars)
		// v=================================================v
		movingLeft = false;
		movingRight = false;
		// ^=================================================^

		level = 1;
		levelChange = false;
		gameOver= false;
		playerDead = false;
		winCon = false;
		isPlayedOnce = false;
		isPlayedOnce2 = false;

		startGame();
	}


	// implementation of Runnable interface

	public void run () {
		try {
			isRunning = true;
			while (isRunning) {
				if (!isPaused  && !gameOver) {
					gameUpdate();
				}
				screenUpdate();
				Thread.sleep (33);
			}
		}
		catch(InterruptedException e) {}

		finishOff();
	}


	/* This method performs some tasks before closing the game.
	   The call to System.exit() should not be necessary; however,
	   it prevents hanging when the game terminates.
	*/

	private void finishOff() { 
    		if (!finishedOff) {
			finishedOff = true;
			restoreScreen();
			System.exit(0);
		}
	}


	/* This method switches off full screen mode. The display
	   mode is also reset if it has been changed.
	*/

	private void restoreScreen() { 
		Window w = device.getFullScreenWindow();
		
		if (w != null)
			w.dispose();
		
		device.setFullScreenWindow(null);
	}

	

	

	public void gameUpdate () {
		if(tileMap.isPlayerDead()){
			gameOver =true;
			playerDead = true;
		}

		if(tileMap.getWinCon()){
			System.out.println("PLAYER WON");
			gameOver = true;
			winCon = true;
		}

		tileMap.update();
		
		level = tileMap.getLevel();
		levelChange = tileMap.getLevelChange();

		// SMOOTH MOVEMENT STEP 5 out of 5 (let player move when specified button is
		// down [its important for the actual movement code to occur in the gameUpdate
		// function that runs many frames a second consistently, rather than in the
		// keyReleased function which does not run reliably consistent])
		// v=================================================v
		if (movingLeft) {
			tileMap.moveLeft();
		
		}
		if (movingRight) {
			tileMap.moveRight();
			
		}
		// ^=================================================^
		

		if (levelChange) {
			levelChange = false;
			tileManager = new TileMapManager (this);

			try {
				String filename = "maps/map" + level + ".txt";
				tileMap = tileManager.loadMap(filename) ;
				int w, h;
				w = tileMap.getWidth();
				h = tileMap.getHeight();
				System.out.println ("Changing level to Level " + level);
				tileMap.setLvl(level);
				System.out.println ("Width of tilemap " + w);
				System.out.println ("Height of tilemap " + h);
			}
			catch (Exception e) {		// no more maps: terminate game
				//gameOver = true;
				System.out.println(e);
				System.out.println("Game Over"); 
				return;
/*
				System.exit(0);
*/
			}

		}
		// if (!isPaused && isAnimShown && !isAnimPaused)
		// 	animation.update();
		

	}


	private void screenUpdate() { 

		try {
			gScr = bufferStrategy.getDrawGraphics();
			gameRender(gScr);
			gScr.dispose();
			if (!bufferStrategy.contentsLost())
				bufferStrategy.show();
			else
				System.out.println("Contents of buffer lost.");
      
			// Sync the display on some systems.
			// (on Linux, this fixes event queue problems)

			Toolkit.getDefaultToolkit().sync();
		}
		catch (Exception e) { 
			e.printStackTrace();  
			isRunning = false; 
		} 
	}


	public void gameRender (Graphics gScr) {		// draw the game objects

		Graphics2D imageContext = (Graphics2D) image.getGraphics();

		tileMap.draw(imageContext);
	
		// if (isAnimShown)
		// 	animation.draw(imageContext);		// draw the animation

		//Graphics2D g2 = (Graphics2D) getGraphics();	// get the graphics context for window
		drawButtons(imageContext);			// draw the buttons

		Graphics2D g2 = (Graphics2D) gScr;
		g2.drawImage(image, 0, 0, pWidth, pHeight, null);

		if(gameOver && playerDead) {
			gameOverMessage(g2);
		}

		if(gameOver && winCon){
			winGame(g2);
		}
			
		if(isPaused == true){
			pausedGame(g2);
		}

		imageContext.dispose();
		g2.dispose();
	}


	private void initFullScreen() {				// standard procedure to get into FSEM

		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		device = ge.getDefaultScreenDevice();

		setUndecorated(true);	// no menu bar, borders, etc.
		setIgnoreRepaint(true);	// turn off all paint events since doing active rendering
		setResizable(false);	// screen cannot be resized
		
		if (!device.isFullScreenSupported()) {
			System.out.println("Full-screen exclusive mode not supported");
			System.exit(0);
		}

		device.setFullScreenWindow(this); // switch on full-screen exclusive mode

		// we can now adjust the display modes, if we wish

		showCurrentMode();

		pWidth = getBounds().width;
		pHeight = getBounds().height;
		
		System.out.println("Width of window is " + pWidth);
		System.out.println("Height of window is " + pHeight);

		try {
			createBufferStrategy(NUM_BUFFERS);
		}
		catch (Exception e) {
			System.out.println("Error while creating buffer strategy " + e); 
			System.exit(0);
		}

		bufferStrategy = getBufferStrategy();
	}


	// This method provides details about the current display mode.

	private void showCurrentMode() {
/*
		DisplayMode dm[] = device.getDisplayModes();

		for (int i=0; i<dm.length; i++) {
			System.out.println("Current Display Mode: (" + 
                           dm[i].getWidth() + "," + dm[i].getHeight() + "," +
                           dm[i].getBitDepth() + "," + dm[i].getRefreshRate() + ")  " );			
		}

		//DisplayMode d = new DisplayMode (800, 600, 32, 60);
		//device.setDisplayMode(d);
*/

		DisplayMode dm = device.getDisplayMode();

		System.out.println("Current Display Mode: (" + 
                           dm.getWidth() + "," + dm.getHeight() + "," +
                           dm.getBitDepth() + "," + dm.getRefreshRate() + ")  " );
  	}


	// Specify screen areas for the buttons and create bounding rectangles

	private void setButtonAreas() {
		
		//  leftOffset is the distance of a button from the left side of the window.
		//  Buttons are placed at the top of the window.

		int leftOffset = (pWidth - (5 * 150) - (4 * 20)) / 2;
		pauseButtonArea = new Rectangle(leftOffset*3 + 80, 70, 50, 50);

		leftOffset = leftOffset + 170;
		leftOffset = leftOffset + 170;
		leftOffset = leftOffset + 170;
		leftOffset = leftOffset + 170;

		int quitLength = quit1Image.getWidth(null);
		int quitHeight = quit1Image.getHeight(null);
		quitButtonArea = new Rectangle(leftOffset, 70, 180, 50);
	}


	private void drawButtons (Graphics g) {
		if(!isPaused){
			if (isOverPauseButton && !isStopped)
				g.drawImage(pause2, pauseButtonArea.x, pauseButtonArea.y, 50, 50, null);
			else
				g.drawImage(pause1, pauseButtonArea.x, pauseButtonArea.y, 50, 50, null);
		}
		
		else {
			if (!isStopped && isOverPauseButton)
				g.drawImage(resume2, pauseButtonArea.x, pauseButtonArea.y, 50, 50, null);
			else
				g.drawImage(resume1, pauseButtonArea.x, pauseButtonArea.y, 50, 50, null);
		}
	
		// draw the quit button (an actual image that changes when the mouse moves over it)

		if (isOverQuitButton)
		   g.drawImage(quit1Image, quitButtonArea.x, quitButtonArea.y, 180, 50, null);
		    	       //quitButtonArea.width, quitButtonArea.height, null);
				
		else
		   g.drawImage(quit2Image, quitButtonArea.x, quitButtonArea.y, 180, 50, null);
		    	       //quitButtonArea.width, quitButtonArea.height, null);

/*
		g.setColor(Color.BLACK);
		g.drawOval(quitButtonArea.x, quitButtonArea.y, 
			   quitButtonArea.width, quitButtonArea.height);
		if (isOverQuitButton)
			g.setColor(Color.WHITE);
		else
			g.setColor(Color.RED);

		g.drawString("Quit", quitButtonArea.x+60, quitButtonArea.y+25);
*/

	}


	private void startGame() { 
		if (gameThread == null) {
			soundManager.playOrResumeClip("background", true);
			gameOver = false;
			tileManager = new TileMapManager (this);
			try {
				tileMap = tileManager.loadMap("maps/map1.txt");
				int w, h;
				w = tileMap.getWidth();
				h = tileMap.getHeight();
				System.out.println ("Width of tilemap " + w);
				System.out.println ("Height of tilemap " + h);
			}
			catch (Exception e) {
				System.out.println(e);
				System.exit(0);
			}
			gameThread = new Thread(this);
			gameThread.start();			

		}
	}


	// displays a message to the screen when the user stops the game

	private void gameOverMessage(Graphics g) {
		if(tileMap.isPlayerDead()){
			soundManager.stopClip("background");
			soundManager.stopClip("collect");
			soundManager.stopClip("jumping");
			soundManager.stopClip("running");
			soundManager.stopClip("enemySlash");
			soundManager.stopClip("playerSlash");
			soundManager.stopClip("warp");
			soundManager.stopClip("win");
			soundManager.stopClip("playerHurt");
			soundManager.stopClip("enemyHurt");
			soundManager.stopClip("enemyDead");
			soundManager.stopClip("runningEnemy");
			soundManager.stopClip("detected");

			if(!isPlayedOnce){
				soundManager.playOrResumeClip("dead", false);
				isPlayedOnce = true;
			}
			
		}
			
		Image backgroundImage = ImageManager.loadImage ("images/gameOverBG2.jpg");
		g.drawImage(backgroundImage,0, 0,getWidth(),getHeight(), null);
		
		Font font = new Font("SansSerif", Font.BOLD, 30);
		FontMetrics metrics = this.getFontMetrics(font);

		String msg = "YOU DIED. Thanks for playing!";

		int x = (pWidth - metrics.stringWidth(msg)) / 2- 180; 
		int y = (pHeight - metrics.getHeight()) / 2;

		g.setColor(Color.RED);
		g.setFont(font);
		g.drawString(msg, x, y);

		drawButtons(g);

	}

	private void pausedGame(Graphics g) {
		if(isPaused){
			//soundManager.pauseClip("background");
			soundManager.pauseClip("collect");
			soundManager.pauseClip("jumping");
			soundManager.pauseClip("running");
			soundManager.pauseClip("enemySlash");
			soundManager.pauseClip("playerSlash");
			soundManager.pauseClip("warp");
			//soundManager.pauseClip("win");
			soundManager.pauseClip("playerHurt");
			soundManager.pauseClip("enemyHurt");
			soundManager.pauseClip("enemyDead");
			soundManager.pauseClip("runningEnemy");
			soundManager.pauseClip("detected");
			//soundManager.pauseClip("dead");
		}
		Image backgroundImage = ImageManager.loadImage ("images/isPausedScreen3.jpg");
		g.drawImage(backgroundImage, 0, 0,getWidth(),getHeight(), null);
		
		Font font = new Font("SansSerif", Font.BOLD, 30);
		FontMetrics metrics = this.getFontMetrics(font);

		String msg = "GAME PAUSED";

		int x = (pWidth - metrics.stringWidth(msg)) / 2 ; 
		int y = (pHeight - metrics.getHeight()) / 2;

		g.setColor(Color.WHITE);
		g.setFont(font);
		g.drawString(msg, x, y);

		drawButtons(g);

	}


	private void winGame(Graphics g) {
		if(tileMap.getWinCon()){

			soundManager.stopClip("background");
			soundManager.stopClip("collect");
			soundManager.stopClip("jumping");
			soundManager.stopClip("running");
			soundManager.stopClip("enemySlash");
			soundManager.stopClip("playerSlash");
			soundManager.stopClip("warp");
			//soundManager.stopClip("win");
			soundManager.stopClip("playerHurt");
			soundManager.stopClip("enemyHurt");
			soundManager.stopClip("enemyDead");
			soundManager.stopClip("runningEnemy");
			soundManager.stopClip("detected");

			if(!isPlayedOnce2){
				soundManager.playOrResumeClip("win", false);
				isPlayedOnce2 = true;
			}
		}

		Image backgroundImage = ImageManager.loadImage ("images/WinScreen.jpg");
		g.drawImage(backgroundImage, 0, 0,getWidth(),getHeight(), null);
		drawButtons(g);

		Font font = new Font("SansSerif", Font.BOLD, 25);
		FontMetrics metrics = this.getFontMetrics(font);

		String msg = "Ornate Box Obtained! You are now the Master of The Shadows!";
		int x = (pWidth - metrics.stringWidth(msg)) / 2 ; 
		int y = (pHeight - metrics.getHeight()) / 2;

		g.setColor(Color.BLACK);
		g.setFont(font);
		g.drawString(msg, x, y);

		Font font2 = new Font("SansSerif", Font.BOLD, 30);
		FontMetrics metrics2 = this.getFontMetrics(font2);

		String msg2 = "Thanks for Playing :)";
		int x2 = (pWidth - metrics2.stringWidth(msg2)) / 2 ; 
		int y2 = (pHeight - metrics2.getHeight()) / 2+30;
		g.setColor(Color.BLACK);
		g.setFont(font2);
		g.drawString(msg2, x2, y2);


		

	}


	// implementation of methods in KeyListener interface

	boolean isSpacePressed, isXPressed;
	public void keyPressed (KeyEvent e) {
		
		if (isPaused || gameOver)
			return;

		int keyCode = e.getKeyCode();
		
		// Set the corresponding key state to true
		if (keyCode == KeyEvent.VK_SPACE) {
			isSpacePressed = true;
		} 
		if (keyCode == KeyEvent.VK_X) {
			isXPressed = true;
		}

	   // Set the attacking state to true if both keys are pressed
	   if (isSpacePressed && isXPressed) {
			tileMap.setPlayerAttack(true);
			tileMap.playerAttack();
    	}
		else{
			if(isXPressed == true){
				if (keyCode == KeyEvent.VK_SPACE) {
					tileMap.setPlayerAttack(true);
					tileMap.jump();
					
				}		
				if (keyCode == KeyEvent.VK_X) {
					tileMap.setPlayerAttack(true);
					tileMap.playerAttack();
				}
			}
			else{
				if (keyCode == KeyEvent.VK_SPACE) {
					tileMap.setPlayerAttack(false);
					tileMap.jump();
					
				}	
				if (keyCode == KeyEvent.VK_X) {
					tileMap.setPlayerAttack(true);
					tileMap.playerAttack();
					//tileMap.attackStart();
				}
				
			}

	
	
		}


		// if((keyCode  == KeyEvent.VK_SPACE) && (keyCode == KeyEvent.VK_X)){
		// 	tileMap.setPlayerAttack(true);
		// 	tileMap.playerAttack();
		// }
	

		if ((keyCode == KeyEvent.VK_ESCAPE) || (keyCode == KeyEvent.VK_Q) ||
             	   (keyCode == KeyEvent.VK_END)) {
           		isRunning = false;		// user can quit anytime by pressing
			return;				//  one of these keys (ESC, Q, END)			
         	}
		else
		if (keyCode == KeyEvent.VK_LEFT) {
			tileMap.setPlayerAttack(false);
			tileMap.moveLeft();
			//movingLeft = true;
			
		}
		else
		if (keyCode == KeyEvent.VK_RIGHT) {
			tileMap.setPlayerAttack(false);
			tileMap.moveRight();
			//movingRight = true;
			
		}
		else
		if (keyCode == KeyEvent.VK_SPACE) {
			tileMap.jump();
			// tileMap.setPlayerAttack(false);
		}
		else
		if (keyCode == KeyEvent.VK_UP) {
			//bat.moveUp();
		}


	}


	public void keyReleased (KeyEvent e) {
		if (isPaused || gameOver)
			return;


		int keyCode = e.getKeyCode();
		String keyText = e.getKeyText(keyCode);
		

		if (keyCode == KeyEvent.VK_LEFT) {
			tileMap.playerIdleL();
			//movingLeft = false;
		}

		if (keyCode == KeyEvent.VK_RIGHT) {
			tileMap.playerIdleR();
			//movingRight = false;
		}

		if (keyCode == KeyEvent.VK_X) {
			tileMap.setPlayerAttack(false);
			tileMap.playerAttack();
			isXPressed = false;
		}
		
		if (keyCode == KeyEvent.VK_SPACE) {
			tileMap.setPlayerAttack(false);
			tileMap.playerAttack();
			isSpacePressed = false;
		}

	}


	public void keyTyped (KeyEvent e) {

	}


	// implement methods of MouseListener interface

	public void mouseClicked(MouseEvent e) {

	}


	public void mouseEntered(MouseEvent e) {

	}


	public void mouseExited(MouseEvent e) {

	}


	public void mousePressed(MouseEvent e) {
		testMousePress(e.getX(), e.getY());
	}


	public void mouseReleased(MouseEvent e) {

	}


	// implement methods of MouseMotionListener interface

	public void mouseDragged(MouseEvent e) {

	}	


	public void mouseMoved(MouseEvent e) {
		testMouseMove(e.getX(), e.getY()); 
	}


	/* This method handles mouse clicks on one of the buttons
	   (Pause, Stop, Start Anim, Pause Anim, and Quit).
	*/

	private void testMousePress(int x, int y) {

		if (isStopped && !isOverQuitButton) 	// don't do anything if game stopped
			return;

		if (isOverStopButton) {			// mouse click on Stop button
			isStopped = true;
			isPaused = false;
		}
		else
		if (isOverPauseButton) {		// mouse click on Pause button
			isPaused = !isPaused;     	// toggle pausing
		}
		// else 
		// if (isOverShowAnimButton && !isPaused) {// mouse click on Start Anim button
		// 	isAnimShown = true;
		//  	isAnimPaused = false;
		// 	animation.start();
		// }
		// else
		// if (isOverPauseAnimButton) {		// mouse click on Pause Anim button
		// 	if (isAnimPaused) {
		// 		isAnimPaused = false;
		// 		animation.playSound();
		// 	}
		// 	else {
		// 		isAnimPaused = true;	// toggle pausing
		// 		animation.stopSound();
		// 	}
		// }
		else if (isOverQuitButton) {		// mouse click on Quit button
			isRunning = false;		// set running to false to terminate
		}
  	}


	/* This method checks to see if the mouse is currently moving over one of
	   the buttons (Pause, Stop, Show Anim, Pause Anim, and Quit). It sets a
	   boolean value which will cause the button to be displayed accordingly.
	*/

	private void testMouseMove(int x, int y) { 
		if (isRunning) {
			isOverPauseButton = pauseButtonArea.contains(x,y) ? true : false;
			// isOverStopButton = stopButtonArea.contains(x,y) ? true : false;
			// isOverShowAnimButton = showAnimButtonArea.contains(x,y) ? true : false;
			// isOverPauseAnimButton = pauseAnimButtonArea.contains(x,y) ? true : false;
			isOverQuitButton = quitButtonArea.contains(x,y) ? true : false;
		}
	}

}