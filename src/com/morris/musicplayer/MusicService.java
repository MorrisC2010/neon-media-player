package com.morris.musicplayer;

import java.io.FileInputStream;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;


public class MusicService extends Service {
	
public Binder IBinder;
private static String TAG = "MusicService";
public static MediaPlayer mp;
public NotificationManager mNotificationManager;
static int duration;



	
	@Override
	public IBinder onBind(Intent intent) {
	return null;
	}
	@Override
	public void onCreate() {
	mp = new MediaPlayer();
	Log.d(TAG, "Service Created..");
	}

	//code to execute when the service is first created
	
	@Override
	public void onDestroy() {
		if (mp.isPlaying()) {
			mp.stop();
		}
		mp.release();
		mp = null;
		mNotificationManager.cancelAll();
		stopSelf();
	//code to execute when the service is shutting down
	Log.d(TAG, "Service Destroyed..");
	}
	@Override
	public void onStart(Intent intent, int startid) {
	//code to execute when the service is starting up
	Log.d(TAG, "Service Started..");
	}
	
	public void Notify() {
		String ns = Context.NOTIFICATION_SERVICE;
		mNotificationManager = (NotificationManager) getSystemService(ns);
		int icon = R.drawable.playbutton;
		CharSequence tickerText = "Media Player - Playing: " + String.valueOf(List.cursor.getString(2));
		long when = System.currentTimeMillis();

		Notification notification = new Notification(icon, tickerText, when);
		
		Context context = getApplicationContext();
		CharSequence contentTitle = "Media Player";
		CharSequence contentText = "Playing: " + String.valueOf(List.cursor.getString(2));
		Intent notificationIntent = new Intent(this, AudioFX.class);
		notificationIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		notification.flags = Notification.FLAG_ONGOING_EVENT;

		
		final int HELLO_ID = 1;

		mNotificationManager.notify(HELLO_ID, notification);
    }
		
	public static void playMusic(){
		if (mp.isPlaying()) {
			mp.stop();
		}
	  	  mp.reset();
	  	if (List.cursor.moveToPosition(List.mCurrentSongPosition)) {
	  	  try {
	  		    FileInputStream fis = new FileInputStream(List.cursor.getString(3));
	  		    mp.setDataSource(fis.getFD());
	  		    mp.prepare();
	      } catch (Exception e) {
	            Log.e("TAG", "Couldn't Play This Song.");
	            next();
	      }
	  mp.setOnPreparedListener(new OnPreparedListener(){
	  	public void onPrepared(MediaPlayer mp){
	  		duration = MusicService.mp.getDuration();
			MusicService.mp.start();
			List.cursor.requery();
			}});
	  mp.setOnCompletionListener(new OnCompletionListener(){
		public void onCompletion(MediaPlayer mp) {
			next();
		}});
	    }}
	    
	    public static void next() {
		    if (List.mCurrentSongPosition < (List.mSongCount-1)) {
		      List.mCurrentSongPosition++;
		      playMusic();
		    }
		  }

		  public static void prev() {
		    if (List.mCurrentSongPosition > 0) {
		      List.mCurrentSongPosition--;
		    }
		    playMusic();
		  }
	    
}