

import javax.swing.plaf.nimbus.State;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

public class MusicPlayer implements Runnable {

	public static final int Building    = 0;
	public static final int Button      = 1;
	public static final int menu        = 2;
	
    private ArrayList<AudioFile> musicFiles;
    private int currentSongIndex = 0;
    private String bgPath = "Content/Audio/BackgroundMusic/";
    private String sePath = "Content/Audio/SoundEffects/";
    private boolean running;
    private ArrayList<AudioFile> bgMusic = new ArrayList<AudioFile>();
    private ArrayList<AudioFile> soundEffects = new ArrayList<AudioFile>();
    public float audioVolume = -19, MaxVolume = 6, MinVolume = -80;

	/**
	 * Creates a playlist of all the songs in the audio folder
	 */
	public MusicPlayer(boolean isForBackground) {
		if (isForBackground) {
			File[] bgFiles = new File(bgPath).listFiles();
			setPlaylist(bgMusic, bgPath, bgFiles);
		} else {
			File[] seFiles = new File(sePath).listFiles();
			setPlaylist(soundEffects, sePath, seFiles);
		}
	}

	/**
	 * Sets the playlist to a given playlist
	 */
	public void setPlaylist(ArrayList<AudioFile> playlist, String pathName, File[] files) {
		try {
			for (File file : files) {
				playlist.add(new AudioFile("./" + pathName + file.getName()));
			}
		} catch (Exception e1) {
			try {
				String s = "";
				e1.printStackTrace(new PrintWriter(s));
				Log.add(s);
			} catch (IOException e2) {
				/**
				 * In this case the error was unable to be added to the log, throwing an error.
				 * Because the new error was adding the original error to the log, we cannot add
				 * errors to the log for some reason so the game moves on.
				 */
			}
		}

	}

	/**
	 * Changes the volume of the music
	 */
	public void changeVolume(float i) {
		int volume = 5;//need to switch to saved volume
		if (volume + i <= 0) {
		}else if (volume + i <= 0 || audioVolume + i * 5 <= MinVolume) {
			audioVolume = MinVolume;
			try {
				Thread.sleep(50);
			} catch (Exception e1) {
				try {
					String s = "";
					e1.printStackTrace(new PrintWriter(s));
					Log.add(s);
				} catch (IOException e2) {
					/**
					 * In this case the error was unable to be added to the log, throwing an error.
					 * Because the new error was adding the original error to the log, we cannot add
					 * errors to the log for some reason so the game moves on.
					 */
				}
			}
		} else if (volume + i >= 10 || audioVolume + i * 5 >= MaxVolume) {
			volume = 10;
			audioVolume = MaxVolume;
			try {
				Thread.sleep(50);
			} catch (Exception e1) {
				try {
					String s = "";
					e1.printStackTrace(new PrintWriter(s));
					Log.add(s);
				} catch (IOException e2) {
					/**
					 * In this case the error was unable to be added to the log, throwing an error.
					 * Because the new error was adding the original error to the log, we cannot add
					 * errors to the log for some reason so the game moves on.
					 */
				}
			}
		} else if (volume + i > 0 && volume == 0 && audioVolume == MinVolume) {
			volume += i;
			audioVolume = -44;
			audioVolume += i * 5;
			try {
				Thread.sleep(50);
			} catch (Exception e1) {
				try {
					String s = "";
					e1.printStackTrace(new PrintWriter(s));
					Log.add(s);
				} catch (IOException e2) {
					/**
					 * In this case the error was unable to be added to the log, throwing an error.
					 * Because the new error was adding the original error to the log, we cannot add
					 * errors to the log for some reason so the game moves on.
					 */
				}
			}
		} else {
			volume += i;
			audioVolume += i * 5;
		}
		//StateMachine.getRender().setVolume(volume);
	}

	/**
	 * Returns the Current background song
	 */
	public AudioFile getCurrentBackgroundSong() {
		AudioFile song = bgMusic.get(currentSongIndex);
		return song;
	}

	/**
	 * Starts playing background music
	 */
	public void playBackgroundMusic() {
		running = true;
		AudioFile song = getCurrentBackgroundSong();
		song.play(audioVolume);
		while (running) {
			if (!song.isPlaying()) {
				currentSongIndex++;
				if (currentSongIndex >= bgMusic.size())
					currentSongIndex = 0;
				song = bgMusic.get(currentSongIndex);
				song.play(audioVolume);
			}
			try {
				Thread.sleep(1);
			} catch (Exception e1) {
				try {
					String s = "";
					e1.printStackTrace(new PrintWriter(s));
					Log.add(s);
				} catch (IOException e2) {
					/**
					 * In this case the error was unable to be added to the log, throwing an error.
					 * Because the new error was adding the original error to the log, we cannot add
					 * errors to the log for some reason so the game moves on.
					 */
				}
			}
		}
		song.stop();
	}

	/**
	 * Makes this class multithreaded so that we can start and stop music while
	 * other things are happening
	 */
	public void run() {
		running = true;
		playBackgroundMusic();
	}

	/**
	 * Starts the thread
	 */
	public void start() {
		if (!running) {
			running = true;
			new Thread(this).start();
		}
	}

	/**
	 * Stops the thread
	 */
	public void stop() {
		running = false;
	}

	public float getAudioVolume() {
		return audioVolume;
	}
	
	public void setAudioVolume(float i) {
		audioVolume = i;
	}
	
	public ArrayList<AudioFile> getSoundEffect() {
		return soundEffects;
	}
}
