package com.morris.musicplayer;


import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Message;
import android.provider.MediaStore.Audio.AudioColumns;
import android.provider.MediaStore.MediaColumns;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.morris.visualizer.VisualizerView;
import com.morris.visualizer.renderer.BarGraphRenderer;
import com.morris.visualizer.renderer.CircleBarRenderer;
import com.morris.visualizer.renderer.CircleRenderer;
import com.morris.visualizer.renderer.LineRenderer;


public class AudioFX extends Activity {

	private RelativeLayout mRelativeLayout;
    public static VisualizerView mVisualizerView;
    public static Button btn;
    public static Button btn2;
    public static Button btn3;
    public static Button btn4;
    public static TextView songtitle;
    private static TextView songduration;
    private TextView songbeginning;
    private int bCounter = 0;
    public static SeekBar seekbar;

/** Called when the activity is first created. */
@Override
public void onCreate(Bundle icicle) {
    super.onCreate(icicle);
    setContentView(R.layout.main2);
    mRelativeLayout = (RelativeLayout) findViewById(R.id.relativelayout2);
    seekbar = (SeekBar) findViewById(R.id.seekBar1);
    songduration = (TextView) findViewById(R.id.songduration);
    songbeginning = (TextView) findViewById(R.id.songbeginning);
    
    if (AudioColumns.DURATION == null)
    {
      mVisualizerView.release();
    }
    
    btn = (Button) findViewById(R.id.button1);
    btn.setBackgroundResource(R.drawable.powerbutton);
    btn2 = (Button) findViewById(R.id.button3);
    btn2.setBackgroundResource(R.drawable.pausebutton);
    btn3 = (Button) findViewById(R.id.button4);
    btn3.setBackgroundResource(R.drawable.nextbutton);
    btn4 = (Button) findViewById(R.id.button2);
    btn4.setBackgroundResource(R.drawable.previousbutton);
    songtitle = (TextView) findViewById(R.id.songtitle);
    songbeginning.setText("0:00");
    ShapeDrawable thumb = new ShapeDrawable( new OvalShape() );
	thumb.getPaint().setColor(Color.WHITE);
	thumb.setIntrinsicHeight(8);
	thumb.setIntrinsicWidth(8);
	seekbar.setThumb( thumb );
    
    btn.setOnClickListener(new View.OnClickListener() {
        public void onClick(View view) {
        	mVisualizerView.clearRenderers();
        	addCircleRenderer();}});
    
    btn2.setOnClickListener(new View.OnClickListener() {
    public void onClick(View view) {
    	bCounter++;
        updateCounter();
    	if (MusicService.mp.isPlaying()) {
    		MusicService.mp.pause();
    	}
    	else MusicService.mp.start();}});
    
    btn3.setOnClickListener(new View.OnClickListener() {
        public void onClick(View view) {
        	MusicService.next();
        	updateSeekbar();
        	}});
    
    btn4.setOnClickListener(new View.OnClickListener() {
        public void onClick(View view) {
        	MusicService.prev();
        	updateSeekbar();
        	}});

}
public void updateSeekbar(){
	seekbar.setMax(MusicService.duration);
	System.out.println("curpos" + MusicService.duration);
	songtitle.setText(String.valueOf(List.cursor.getString(List.cursor.getColumnIndexOrThrow(MediaColumns.TITLE))));
	songduration.setText(String.format("%d:%d", TimeUnit.MILLISECONDS.toMinutes(MusicService.duration), TimeUnit.MILLISECONDS.toSeconds(MusicService.duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(MusicService.duration))));
	new Thread(new Runnable() {
		public void run() {
			while(MusicService.mp!=null && MusicService.mp.isPlaying())
			{
				seekbar.setProgress(MusicService.mp.getCurrentPosition());
                Message msg=new Message();
                int millis = MusicService.mp.getCurrentPosition();
                msg.obj=millis/1000;
                try {
                	Thread.sleep(1000);
                	}
                catch (InterruptedException e) {
                	e.printStackTrace();
                	System.out.println("interrupt exeption" +e);
                   }
                }
			}
		}
	).start();
	}

@Override
protected void onResume()
{
  super.onResume();
  init();
  }

@Override
protected void onPause()
{
	super.onPause();
	cleanUp();
	
}

protected void onDestroy()
{
	super.onDestroy();
	cleanUp();
	
}

private void init()
{

  // We need to link the visualizer view to the media player so that
  // it displays something
  mVisualizerView = (VisualizerView) findViewById(R.id.visualizerView);
  mVisualizerView.link(MusicService.mp);
  updateSeekbar();

  // Start with just line renderer
  addLineRenderer();
}

private void cleanUp()
{
  if (MusicService.mp != null)
  {
    mVisualizerView.release();
  }
}

// Methods for adding renderers to visualizer
private void addBarGraphRenderer()
{
  Paint paint = new Paint();
  paint.setStrokeWidth(50f);
  paint.setAntiAlias(true);
  paint.setColor(Color.argb(200, 56, 138, 252));
  BarGraphRenderer barGraphRendererBottom = new BarGraphRenderer(16, paint, false);
  mVisualizerView.addRenderer(barGraphRendererBottom);

  Paint paint2 = new Paint();
  paint2.setStrokeWidth(12f);
  paint2.setAntiAlias(true);
  paint2.setColor(Color.argb(200, 181, 111, 233));
  BarGraphRenderer barGraphRendererTop = new BarGraphRenderer(4, paint2, true);
  mVisualizerView.addRenderer(barGraphRendererTop);
}

private void addCircleBarRenderer()
{
  Paint paint = new Paint();
  paint.setStrokeWidth(8f);
  paint.setAntiAlias(true);
  paint.setXfermode(new PorterDuffXfermode(Mode.LIGHTEN));
  paint.setColor(Color.argb(255, 222, 92, 143));
  CircleBarRenderer circleBarRenderer = new CircleBarRenderer(paint, 32, true);
  mVisualizerView.addRenderer(circleBarRenderer);
}

private void addCircleRenderer()
{
  Paint paint = new Paint();
  paint.setStrokeWidth(3f);
  paint.setAntiAlias(true);
  paint.setColor(Color.argb(255, 222, 92, 143));
  CircleRenderer circleRenderer = new CircleRenderer(paint, true);
  mVisualizerView.addRenderer(circleRenderer);
}

private void addLineRenderer()
{
  Paint linePaint = new Paint();
  linePaint.setStrokeWidth(1f);
  linePaint.setAntiAlias(true);
  linePaint.setColor(Color.argb(88, 0, 128, 255));

  Paint lineFlashPaint = new Paint();
  lineFlashPaint.setStrokeWidth(5f);
  lineFlashPaint.setAntiAlias(true);
  lineFlashPaint.setColor(Color.argb(188, 255, 255, 255));
  LineRenderer lineRenderer = new LineRenderer(linePaint, lineFlashPaint, true);
  mVisualizerView.addRenderer(lineRenderer);
}
public boolean onCreateOptionsMenu(Menu menu) {
    // Hold on to this
    
    // Inflate the currently selected menu XML resource.
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.audiofxmenu, menu);
    return true;
}

@Override
public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
        // For "Title only": Examples of matching an ID with one assigned in
        //
		case R.id.Back:
		startActivity(new Intent(this, List.class));
		return true;
    }
    return false;
    }



private void updateCounter() {
	
	if (bCounter == 1){ 
		btn2.setBackgroundResource(R.drawable.playbutton);
	}
	if (bCounter == 2){
		btn2.setBackgroundResource(R.drawable.pausebutton);
		bCounter = 0;
	}
}
}