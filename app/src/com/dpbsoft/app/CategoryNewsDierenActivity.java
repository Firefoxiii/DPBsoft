package com.dpbsoft.app;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class CategoryNewsDierenActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_category_news_dieren);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.category_news_dieren, menu);
		return true;
	}

}
