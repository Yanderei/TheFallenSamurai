import javax.sound.sampled.AudioInputStream;		// for playing sound clips
import javax.sound.sampled.*;
import java.io.*;
import java.util.HashMap;				// for storing sound clips

public class SoundManager {				// a Singleton class
	HashMap<String, Clip> clips;

	private static SoundManager instance = null;	// keeps track of Singleton instance
    private boolean isActive = false;
	private float volume;

	private SoundManager () {
		clips = new HashMap<String, Clip>();

		Clip clip = loadClip("sounds/bgMusic.wav");	
		clips.put("background", clip);

		clip = loadClip("sounds/collectKeys.wav");	
		clips.put("collect", clip);

		clip = loadClip("sounds/dieSound.wav");	
		clips.put("dead", clip);

		clip = loadClip("sounds/jump.wav");	
		clips.put("jumping", clip);

		clip = loadClip("sounds/runningInGrass.wav");	
		clips.put("running", clip);

		clip = loadClip("sounds/swordSlashEnemy.wav");	
		clips.put("enemySlash", clip);

		clip = loadClip("sounds/swordSlashPlayer.wav");	
		clips.put("playerSlash", clip);

		clip = loadClip("sounds/warpSound.wav");	
		clips.put("warp", clip);

		clip = loadClip("sounds/winSound.wav");	
		clips.put("win", clip);

		clip = loadClip("sounds/playerHit.wav");	
		clips.put("playerHurt", clip);

		clip = loadClip("sounds/enemyHit.wav");	
		clips.put("enemyHurt", clip);

		clip = loadClip("sounds/killed.wav");	
		clips.put("enemyDead", clip);

		clip = loadClip("sounds/runningInGrass.wav");	
		clips.put("runningEnemy", clip);

		clip = loadClip("sounds/aggro.wav");	
		clips.put("detected", clip);

		volume = 0.3f;
	}


	public static SoundManager getInstance() {	// class method to retrieve instance of Singleton
		if (instance == null)
			instance = new SoundManager();
		
		return instance;
	}		


    	public Clip loadClip (String fileName) {	// gets clip from the specified file
 		AudioInputStream audioIn;
		Clip clip = null;

		try {
    			File file = new File(fileName);
    			audioIn = AudioSystem.getAudioInputStream(file.toURI().toURL()); 
    			clip = AudioSystem.getClip();
    			clip.open(audioIn);
		}
		catch (Exception e) {
 			System.out.println ("Error opening sound files: " + e);
		}
    		return clip;
    	}


	public Clip getClip (String title) {

		return clips.get(title);
	}


    	public void playClip(String title, boolean looping) {
			Clip clip = getClip(title);
			if (clip != null) {
					clip.setFramePosition(0);

					FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
					float dB = (float) (Math.log(volume) / Math.log(10.0) * 20.0);
					gainControl.setValue(dB);

					
					if (looping)
						clip.loop(Clip.LOOP_CONTINUOUSLY);
					else
						clip.start();
				isActive= true;
			}
    	}


    	public void stopClip(String title) {
		Clip clip = getClip(title);
		if (clip != null) {
			clip.stop();
			isActive=false;
		}
    	}


		public boolean checkClipStatus()
		      {
 
               return isActive;
			  }

			  public void pauseClip(String title) {
				Clip clip = getClip(title);
				if (clip != null && clip.isRunning()) {
					clip.stop();
				}
			}
		
			public void resumeClip(String title, boolean looping) {
				Clip clip = getClip(title);
				if (clip != null && !clip.isRunning()) {
					clip.start();

					FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
					float dB = (float) (Math.log(volume) / Math.log(10.0) * 20.0);
					gainControl.setValue(dB);

					if(looping==true)
					  {
						clip.loop(Clip.LOOP_CONTINUOUSLY);
					  }
				}
			}


			public void playOrResumeClip(String title, boolean looping) {
				Clip clip = getClip(title);
				if (clip != null) {
					if (clip.isRunning()) {
						resumeClip(title, looping);
					} else {
						playClip(title, looping);
					}
				}
			}

}