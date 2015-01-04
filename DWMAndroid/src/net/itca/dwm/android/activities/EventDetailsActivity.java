package net.itca.dwm.android.activities;

import java.util.concurrent.ExecutionException;

import net.itca.dwm.android.R;
import net.itca.dwm.android.R.id;
import net.itca.dwm.android.R.layout;
import net.itca.dwm.android.R.menu;
import net.itca.dwm.android.core.DWMAndroidFacade;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class EventDetailsActivity extends Activity
{

	String event;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_details);
		
		// get the data
		Intent thisIntent = getIntent();
		event = thisIntent.getStringExtra("event");
		String ancestor = thisIntent.getStringExtra("origin");
		try
		{
			String details = new AsyncEventDetailHandler().execute(event).get();
			fillGUI(details);
		} 
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		alterGUI(ancestor);
	}
	
	// Depending on how this intent was Insstarted, we need to show different buttons.
	private void alterGUI(String modifier)
	{
		if(modifier != null && modifier.equals("EventActivity"))
		{
			System.out.println("Altering button visibility");
			Button acceptButton = (Button) findViewById(R.id.EventDetailsAcceptButton);
			acceptButton.setVisibility(View.GONE); // hide button
		}
	}
	
	private void fillGUI(String details)
	{
		// Get the labels
		TextView eventField = (TextView) findViewById(R.id.EventDetailsName);
		TextView hostField = (TextView) findViewById(R.id.EventDetailsHost);
		TextView recipeField = (TextView) findViewById(R.id.EventDetailsRecipe);
		TextView dateField = (TextView) findViewById(R.id.EventDetailsDate);
		TextView timeField = (TextView) findViewById(R.id.EventDetailsTime);
		
		System.out.println("details: " + details);
		String split[] = details.split("\n");
		for(String s : split)
		{
			System.out.println("Part: " + s);
		}
		
		if(split.length > 4) // I don't like that the index looks arbitrary to someone not debugging the details string :(  Sorry Mrs. Verfaille! 
		{
			eventField.setText(split[0]);
			hostField.setText(split[4]);
			recipeField.setText(split[5]);
			String date = split[1];
			String dateparts[] = date.split("-");
			dateField.setText(dateparts[2]+"-"+dateparts[1]+"-"+dateparts[0]);
			// Format time
			timeField.setText(split[2]);			
		}
		else
		{
			hostField.setVisibility(View.GONE); // hide this field, it's our event, so we kind of know the host (hopefully)
			recipeField.setText(split[3]);
			String date = split[1];
			String dateparts[] = date.split("-");
			dateField.setText(dateparts[2]+"-"+dateparts[1]+"-"+dateparts[0]);
			timeField.setText(split[2]);	
			eventField.setText(split[0]);
			Button declineButton = (Button) findViewById(R.id.EventDetailsDeclineButton);
			declineButton.setVisibility(View.GONE); // hide button
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.event_details, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings)
		{
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void DeclineClick(View view)
	{
		new AsyncEventDetailHandler().execute("decline");
		finish();
	}
	
	public void AcceptClick(View view)
	{
		new AsyncEventDetailHandler().execute("accept");
		finish();
	}
	
	class AsyncEventDetailHandler extends AsyncTask<String, Void, String>
	{
		@Override
		
		protected String doInBackground(String... args)
		{
			DWMAndroidFacade facade = DWMAndroidFacade.getFacade();
			
			if(args[0].equals("decline"))
			{
				try
				{
					facade.deleteEvent(event);
					finish();
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
			}
			else if(args[0].equals("accept"))
			{
				try
				{
					facade.acceptEventInvite(event);
					finish();
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
			}
			else
			{
				try
				{
					System.out.println("Details of event: " + args[0]);
					String detailsOfEvent = facade.getEventDetails(args[0]);
					if(detailsOfEvent.equals("")  || detailsOfEvent == null)
					{
						System.out.println("Here we are.");
						// The current method didn't have any results, maybe we are requesting details of one of the logged in users event?
						return facade.getMyEventDetails(args[0]);
					}
					else
					{
						return detailsOfEvent;
					}
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
			}
			return null;
		}
	}
}
