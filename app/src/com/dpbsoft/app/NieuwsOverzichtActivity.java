package com.dpbsoft.app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.developerworks.android.FeedParserFactory;
import org.developerworks.android.Message;
import org.developerworks.android.ParserType;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.SessionState;

public class NieuwsOverzichtActivity extends Activity implements AdapterView.OnItemClickListener{

	// Store the messages from the RSS
	private List<Message> messages = new ArrayList<Message>();
	
	// Store the extracted information from the messages
	static List<String> titles;
	static List<String> descriptions;
	static List<String> dates;
	static List<String> paths;
	static List<String> hosts;
	static List<String> protocols;
	List<String[]> rank = new ArrayList<String[]>();
	
	private boolean mIsBackButtonPressed;
	private int group1Id = 1;
	private boolean isFBinstalled;
	private static Context context;
	static int ArrayPosition;
	
	public ListView lstTweets = null;
	String newscategory = new String();
	
	int nieuwsId = Menu.FIRST;
	int logoutId = Menu.FIRST +1;
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

	    menu.add(group1Id, nieuwsId, nieuwsId, "CategorieŽn");
	    if (!isFBinstalled) { 
	    	menu.add(group1Id, logoutId, logoutId, "Logout");
	    }

	    return super.onCreateOptionsMenu(menu); 
	    }
	
	private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        boolean app_installed = false;
        try {
               pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
               app_installed = true;
        }
        catch (PackageManager.NameNotFoundException e){
               app_installed = false;
        }
        return app_installed ;
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		   switch (item.getItemId()) {

				case 1:
					Intent intent = new Intent(NieuwsOverzichtActivity.this, ListCategoriesActivity.class);
			        startActivity(intent);
			        return true;
			
				case 2:
					Session session = Session.getActiveSession();
					session.closeAndClearTokenInformation();
					
					SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(context);
	    			p.edit().putBoolean("loggedIn", false).commit();
	    			
					Toast.makeText(getApplicationContext(), "U bent uitgelogd.", Toast.LENGTH_LONG).show();
					startActivity(new Intent(NieuwsOverzichtActivity.this, MainActivity.class));

				default:
				    break;
				}
		   
	    return super.onOptionsItemSelected(item);
	    
	}
	
	@Override
	  public void onActivityResult(int requestCode, int resultCode, Intent data) {
	      super.onActivityResult(requestCode, resultCode, data);
	      Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);	
	}
	
	public void fbLogin() {
		Session.openActiveSession(this, true, new Session.StatusCallback() {
	    	@Override
	    	public void call(Session session, SessionState state, Exception exception) { 
	    	}
		});
	}

	 @Override
	 public void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
		 addInt();
		 sortList();
		 algemeenOrder();
		 setRankingPoints();
		 getRankingPoints();
		 setContentView(R.layout.activity_nieuw_overzicht);
		 isFBinstalled = appInstalledOrNot("com.facebook.katana");
		 fbLogin();
		 NieuwsOverzichtActivity.context = getApplicationContext();
		 
		 Bundle extras = getIntent().getExtras();
		 if (extras != null) {
		     newscategory = extras.getString("newscategory");
		 }
		 else
			newscategory = "algemeen";
	        
		 // Load the list with messages
		 lstTweets = (ListView)findViewById(R.id.listViewRss);
		 lstTweets.setOnItemClickListener(this);

		 new AsyncTask<Void, Void, Void>() {

			 ProgressDialog p;
			 boolean success = false;

			 @Override
			 protected void onPostExecute(Void aVoid) {
				 p.dismiss();
				 if (!success) {

					 NieuwsOverzichtActivity.this.runOnUiThread(new Runnable() {
						 public void run() {
							 Toast toast = Toast.makeText(NieuwsOverzichtActivity.this, "Sorry, er kon geen verbinding met internet gemaakt worden.", Toast.LENGTH_LONG);
							 toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
							 toast.show();
						 }
					 });

				 } else {
					 ArrayAdapter<String> adapter = new ArrayAdapter<String>(NieuwsOverzichtActivity.this, R.layout.rss_list,titles);
					 lstTweets.setAdapter(adapter);
				 }
			 }

			 @Override
			 protected void onPreExecute() {
				 p  = ProgressDialog.show(NieuwsOverzichtActivity.this,"Laden...","...een ogenblik geduld alstublieft.");
			 }

			 @Override
			 protected Void doInBackground(Void... params) {
				 try {
					 loadFeed(ParserType.ANDROID_SAX);
					 if (messages!=null&&messages.size()>0) success = true;
				 } catch (RuntimeException e) {}
				 catch (Exception e) {}
				 return null;
			 }
		 }.execute();
	}

	public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
		 lstTweets.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			 @Override
			 public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				 	ArrayPosition = position;
				 	Intent intent = new Intent(NieuwsOverzichtActivity.this, NieuwsArtikelActivity.class);
				 	startActivity(intent);
			 		}
		 	}); 
	 }


	 private void loadFeed(ParserType type){
	     try{
	    	 Log.i("Info",newscategory);
	      Log.i("AndroidNews", "ParserType="+type.name());
	      if(newscategory.equals("algemeen"))
	      {
	    	  int sum = 0;
	    	  for(int num : points)
	    	  {
	    		  if(num>1)
	    			  sum+=num;
	    		  else
	    			  sum++;
	    	  }
	    		  
	    		  int iz = 0;
	    	  for(String[] categorie : rank)
	    	  {
	    		  List<Message> tempMessages = new ArrayList<Message>();
	    		  for(String s : categorie)
	    		  {
	    			  tempMessages.addAll(FeedParserFactory.getParser(type, s).parse());
	    		  }
	    		  Collections.sort(tempMessages, new CustomComparator());
	    	      Collections.reverse(tempMessages);
	    	      
	    	      int l = points.get(iz);
	    	      int listLength = (int)l / sum * 30;
	    	      if(listLength ==0)
	    	    	  listLength = 1;
	    	      
	    	      int sz = tempMessages.size();
	    	      for(int i = listLength;i<sz;i++)
	    	    	  tempMessages.remove(listLength);

	    	      
	    	      messages.addAll(tempMessages);
	    	      
	    	      iz++;
	    	  }
	      
	      }
	      else
	      {
	      for(String s : getFeeds(newscategory))
	      {
	    	  messages.addAll(FeedParserFactory.getParser(type, s).parse());
	      }
		      Collections.sort(messages, new CustomComparator());
		      Collections.reverse(messages);
	      }
	      
	      
	      
	      // Initialize the ArrayLists
	      titles = new ArrayList<String>(messages.size());
	      descriptions = new ArrayList<String>(messages.size());
	      dates = new ArrayList<String>(messages.size());
	      paths = new ArrayList<String>(messages.size());
	      hosts = new ArrayList<String>(messages.size());
	      protocols = new ArrayList<String>(messages.size());
	      
	      // Extract and store the information
	      for (Message msg : messages){
	    	  titles.add(msg.getTitle());
	    	  descriptions.add(msg.getDescription());
	    	  dates.add(msg.getDate());
	    	  paths.add(msg.getLink().getPath());
	    	  hosts.add(msg.getLink().getHost());
	    	  protocols.add(msg.getLink().getProtocol());
	      }
	      
	      } catch (Throwable t){
	      Log.e("AndroidNews",t.getMessage(),t);
	      }
	}

	
	    
	public class CustomComparator implements Comparator<Message> {
		@Override
		public int compare(Message o1, Message o2) {
		return o1.getDate().compareTo(o2.getDate());
		}
	}
	 
	public class CustomIntComparator implements Comparator<Integer> {
		@Override
		public int compare(Integer o1, Integer o2)
		{
			return o2.compareTo(o1);
		}
	}
	 
	public String[] getFeeds(String cat){
		if(cat == "natuur")
			return feedNatuur;
		else if(cat=="ontwikkeling")
			return feedOntwikkeling;
		else if(cat=="vluchtelingen")
			return feedVluchtelingen;
		else if(cat == "overig")
			return feedOverig;
		else if (cat=="ziektes")
			return feedZiektes;
		else
			return feedDieren;
		}
	 
	private String feedAap = "http://www.nu.nl/feeds/rss/tag/stichting%20aap.rss";
	private String feedGreen = "http://www.nu.nl/feeds/rss/tag/greenpeace.rss";
	private String feedAmnesty = "http://www.nu.nl/feeds/rss/tag/amnesty%20international.rss";
	private String feedWarChild = "http://www.nu.nl/feeds/rss/tag/war%20child.rss";
	private String feedWnf = "http://www.nu.nl/feeds/rss/tag/wnf.rss";
	private String feedWspa = "http://www.nu.nl/feeds/rss/tag/wspa.rss";
	private String feedKwf = "http://www.nu.nl/feeds/rss/tag/kwf%20kankerbestrijding.rss";
	private String feedUnicef = "http://www.nu.nl/feeds/rss/tag/unicef.rss";
	private String feedNmonument = "http://www.nu.nl/feeds/rss/tag/natuurmonumenten.rss";
	private String feedMilieuDef = "http://www.nu.nl/feeds/rss/tag/milieudefensie.rss";
	private String feedLdHeils = "http://www.nu.nl/feeds/rss/tag/leger%20des%20heils.rss";
	private String feedTdH = "http://www.nu.nl/feeds/rss/tag/terre%20des%20hommes.rss";	
	private String feedLong = "http://www.nu.nl/feeds/rss/tag/longfonds.rss";	
	private String feedKika = "http://www.nu.nl/feeds/rss/tag/kika.rss";
	private String feedStopAids = "http://www.nu.nl/feeds/rss/tag/stop%20aids%20now.rss";
	private String feedDiabetsFonds = "http://www.diabetesfonds.nl/feeds/rss.xml";
	private String feedNier = "http://www.nierstichting.nl/rss";
	private String feedAzG = "http://www.artsenzondergrenzen.nl/feeds/news.aspx";

		private String[] feedDieren = {feedWnf,feedWspa,feedAap};
		private String[] feedNatuur = {feedGreen,feedMilieuDef};
		private String[] feedOntwikkeling = {feedUnicef, feedTdH, feedLdHeils};
		private String[] feedVluchtelingen = {feedWarChild};
		private String[] feedOverig = {feedAmnesty,feedNmonument};
		private String[] feedZiektes = {feedKwf,feedLong, feedKika, feedStopAids, feedDiabetsFonds, feedNier, feedAzG};
		

	ListCategoriesActivity lca = new ListCategoriesActivity();
	int dierenRank = lca.getRankingDieren();
	int natuurRank = lca.getRankingNatuur();
	int ontwikkelingRank = lca.getRankingOntwikkeling();
	int vluchtelingenRank = lca.getRankingVluchtelingen();
	int ziektesRank = lca.getRankingZiektes();
	int overigRank = lca.getRankingOverig();
		
	
public void setRankingPoints()
{
	SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(context);
    p.edit().putInt("dierenPoints", dierenRank).commit();
    p.edit().putInt("natuurPoints", natuurRank).commit();
    p.edit().putInt("ontwikkelingPoints", ontwikkelingRank).commit();
    p.edit().putInt("vluchtelingenPoints", vluchtelingenRank).commit();
    p.edit().putInt("ziektesPoints", ziektesRank).commit();
    p.edit().putInt("overigPoints", overigRank).commit();   
}

public void getRankingPoints(){
SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(context);
int currentDierenRank = p.getInt("dierenPoints", 0);
int currentNatuurRank = p.getInt("natuurPoints", 0);
int currentOntwikkelingRank = p.getInt("ontwikkelingPoints", 0);
int currentVluchtelingenRank = p.getInt("vluchtelingenPoints", 0);
int currentZiektesRank = p.getInt("ziektesPoints", 0);
int currentOverigRank = p.getInt("overigPoints", 0);

ListCategoriesActivity lca = new ListCategoriesActivity();
lca.setRankingDieren(currentDierenRank);
lca.setRankingNatuur(currentNatuurRank);
lca.setRankingOntwikkeling(currentOntwikkelingRank);
lca.setRankingVluchtelingen(currentVluchtelingenRank);
lca.setRankingZiektes(currentZiektesRank);
lca.setRankingOverig(currentOverigRank);
}


	
		List<Integer> points = new ArrayList<Integer>();
		
		public void algemeenOrder(){
			for(int i=0; i<points.size();i++)
			{
				 if(points.get(i) == dierenRank && !rank.contains(feedDieren))
					{
					 rank.add(feedDieren); 
					}
				 else if(points.get(i) == natuurRank && !rank.contains(feedNatuur))
						 rank.add(feedNatuur);
				 else if(points.get(i) == ontwikkelingRank && !rank.contains(feedOntwikkeling))
						 rank.add(feedOntwikkeling);			
				 else if(points.get(1) == vluchtelingenRank && !rank.contains(feedVluchtelingen))
						 rank.add(feedVluchtelingen);			 
				 else if(points.get(1) == ziektesRank && !rank.contains(feedZiektes))
						 rank.add(feedZiektes);			
				 else if(points.get(1) == overigRank && !rank.contains(feedOverig))
						 rank.add(feedOverig);		 
				 
			}		 
			
		}
			 
	public void addInt() {
		points.add(dierenRank);
		points.add(natuurRank);
		points.add(ontwikkelingRank);
		points.add(vluchtelingenRank);
		points.add(ziektesRank);
		points.add(overigRank);
	}
			
		
	public void sortList() {
		Collections.sort(points, new CustomIntComparator());
		Collections.reverse(points);
	}
		
	
		
	@Override
	public void onBackPressed() {
		// set the flag to true so the next activity won't start up
		mIsBackButtonPressed = true;
		super.onBackPressed();
	}
}
