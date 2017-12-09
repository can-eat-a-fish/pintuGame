package com.fq.pitu_game;

import com.fq.pitu_view.GamePintuLayout;
import com.fq.pitu_view.GamePintuLayout.GamePintuListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	private GamePintuLayout mGamePintuLayout;
	private TextView mLevel;
	private TextView mTime;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mLevel=(TextView) findViewById(R.id.id_level);
		mTime= (TextView) findViewById(R.id.id_time);
		
		mGamePintuLayout=(GamePintuLayout) findViewById(R.id.id_gamepintu);
		mGamePintuLayout.setTimeEnabled(true);
		mGamePintuLayout.setOnGamePintuListener(new GamePintuListener() {
			
			@Override
			public void timeChanged(int currentTime) {
				mTime.setText(""+currentTime);
			}
			
			@Override
			public void nextLevel(final int nextLevel) {
				
				
				new AlertDialog.Builder(MainActivity.this).setTitle("拼图小游戏")
				.setMessage("下一关!!!").setCancelable(false).setPositiveButton("开始 ", new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mGamePintuLayout.nextLevel();
						mLevel.setText(""+nextLevel);
					}
				}).show();
			}
			
			@Override
			public void gameOver() {
				new AlertDialog.Builder(MainActivity.this).setTitle("拼图小游戏")
				.setMessage("Game Over!!!").setCancelable(false).setPositiveButton("继续游戏 ", new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mGamePintuLayout.restart();
					}
				}).setNegativeButton("结束游戏", new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				}).show();
				
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		mGamePintuLayout.resume();
	}
	@Override
	protected void onPause() {
		super.onPause();
		mGamePintuLayout.pause();
	}
	

}
