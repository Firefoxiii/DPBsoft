package com.dpbsoft.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		/* Knoppen */
		Button loginButton = (Button)findViewById(R.id.loginButton);
		Button aboutButton = (Button)findViewById(R.id.aboutButton);
		
		loginButton.setOnClickListener(new View.OnClickListener() {
		      @Override
		      public void onClick(View view) {
		        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
		        startActivity(intent);
		      }
		});
		
		aboutButton.setOnClickListener(new View.OnClickListener() {
		      @Override
		      public void onClick(View view) {
		        Intent intent = new Intent(MainActivity.this, AboutActivity.class);
		        startActivity(intent);
		      }
		});
		/* Einde knoppen */
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	
	
	public void aboutScreen(View view) {
	}
}
