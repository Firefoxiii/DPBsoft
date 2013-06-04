package com.dpbsoft.app;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

public class LoginActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Parse.initialize(this, "B6hZvvU0sVgaDY7a9hpYvntoE5rWPHECMJ8PQxkx", "KIsuKwb6sPQO5STRqL4PmN3EqjyoEPRyxLaDhRi9"); 
		ParseFacebookUtils.initialize("172436992923336");		
		ParseAnalytics.trackAppOpened(getIntent());
		
		ParseFacebookUtils.logIn(this, new LogInCallback() {
			  @Override
			  public void done(ParseUser user, ParseException err) {
			    if (user == null) {
			      Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
			    } else if (user.isNew()) {
			      Log.d("MyApp", "User signed up and logged in through Facebook!");
			    } else {
			      Log.d("MyApp", "User logged in through Facebook!");
			    }
			  }
			});
		
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		Button loginButton = (Button)findViewById(R.id.button1);
		
		loginButton.setOnClickListener(new View.OnClickListener() {
		      @Override
		      public void onClick(View view) {
		        Intent intent = new Intent(LoginActivity.this, ListCategoriesActivity.class);
		        startActivity(intent);
		      }
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	  super.onActivityResult(requestCode, resultCode, data);
	  ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}
}
