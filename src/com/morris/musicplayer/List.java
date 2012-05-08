package com.morris.musicplayer;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.AudioColumns;
import android.provider.MediaStore.MediaColumns;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class List extends ListActivity {
	private Menu mMenu;
	ListView list;
	static SimpleCursorAdapter mAdapter;
	public static Cursor cursor;
    public String[] columns;
    public int[] to;
    public static int mSongCount;
    public static int mCurrentSongPosition;




    @Override
    public void onCreate(Bundle savedInstance) {
    	super.onCreate(savedInstance);
          setContentView(R.layout.search);
          
          startService(new Intent(this, MusicService.class));

          // some code

          cursor = managedQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[] {BaseColumns._ID, AudioColumns.ARTIST, MediaColumns.TITLE, MediaColumns.DATA, MediaColumns.DISPLAY_NAME, AudioColumns.DURATION, AudioColumns.ALBUM}, AudioColumns.IS_MUSIC + " != 0", null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
          mSongCount = cursor.getCount();
          
         String[] columns = {AudioColumns.TITLE, AudioColumns.ARTIST , AudioColumns.DURATION, AudioColumns.ALBUM};
         int[] to = {R.id.song_title, R.id.song_artist, R.id.song_duration, R.id.song_album};

          mAdapter = new SimpleCursorAdapter(this, R.layout.search2, cursor, columns, to);
          
          list = (ListView) findViewById(android.R.id.list);
          list.setAdapter(mAdapter);
          list.setOnItemClickListener(musicgridlistener);
          list.setFastScrollEnabled(true);
          }
    
        
    private OnItemClickListener musicgridlistener = new OnItemClickListener() {
        public void onItemClick(AdapterView parent, View v, int position, long id) {
              System.gc();
              TextView song_artist = (TextView) findViewById(R.id.song_artist);
              song_artist.setMovementMethod(new ScrollingMovementMethod());
              mCurrentSongPosition = position;
              cursor.moveToPosition(mCurrentSongPosition);
              startActivity(new Intent(getApplicationContext(), AudioFX.class));
			  MusicService.playMusic();
    }};
	  
      @Override
      public boolean onCreateOptionsMenu(Menu menu) {
          // Hold on to this
          mMenu = menu;
          
          // Inflate the currently selected menu XML resource.
          MenuInflater inflater = getMenuInflater();
          inflater.inflate(R.menu.listmenu, menu);
          return true;
      }
      
      @Override
  	public boolean onOptionsItemSelected(MenuItem item) {
          switch (item.getItemId()) {
              // For "Title only": Examples of matching an ID with one assigned in
              //                   the XML
              case R.id.Settings:
              	Intent aIntent = new Intent(this, Settings.class);
              	startActivity(aIntent);
                  return true;

              case R.id.Back:
            	  finish();
                  return true;
                  }
          return false;
          }
      
      @Override
      public boolean onKeyUp(int keyCode, KeyEvent event) {
              if(keyCode == KeyEvent.KEYCODE_SEARCH){
                                          //launch the search dialog
          				onSearchRequested();
          				return true;
              }else{
                      return false;
              }
      }
      
      @Override
      public boolean onSearchRequested() {
      	Bundle bundle=new Bundle();
  		bundle.putString("extra", "exttra info");
  		// search initial query
  		startSearch("", false, bundle, false);
  		return true;
      }
      
      @Override
      public void onDestroy() {
        cursor.close();
        android.os.Process.killProcess(android.os.Process.myPid());
        finish();
        super.onDestroy();
      }
}
    

