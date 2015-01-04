package net.itca.dwm.android.activities;

import java.util.ArrayList;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class InvitesActivity extends Activity
{

	private ArrayList<String> friendInvites, eventInvites;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_invites);
		
		friendInvites = new ArrayList<String>();
		eventInvites = new ArrayList<String>();
		
		ArrayList<String> tempFriends = null;
		ArrayList<String> tempEvents = null;
		try
		{
			tempFriends = new AsyncInviteHandler().execute("friends").get();
			tempEvents = new AsyncInviteHandler().execute("events").get();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		// format the returns
		
		for(String friend : tempFriends)
		{
			friendInvites.add(friend);
		}
		
		for(String event : tempEvents)
		{
			System.out.println("adding to events: " + event);
			eventInvites.add(event.split("\\|")[0] + " ~ " + event.split("\\|")[1]);
		}
	
		
		fillGUI();
	}
	
	private void fillGUI()
	{
		// friends
		
		if(friendInvites != null)
		{
			try
			{
				System.out.println("Setting friend invites");
				final ListView list = (ListView) findViewById(R.id.InvitesFriendList);
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
						android.R.layout.simple_list_item_1, friendInvites);
				list.setAdapter(adapter);
				list.setOnItemClickListener(new OnItemClickListener()
				{
					@Override
					public void onItemClick(AdapterView<?> adapter, View arg1,	int position, long arg3)
					{
						navigateFriendDetails(adapter.getItemAtPosition(position).toString());
					}
				});
				// set action listener
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
		
		// events
		
		if(eventInvites != null)
		{
			try
			{
				System.out.println("Setting event invites");
				final ListView list = (ListView) findViewById(R.id.InvitesEventList);
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
						android.R.layout.simple_list_item_1, eventInvites);
				list.setAdapter(adapter);
				list.setOnItemClickListener(new OnItemClickListener()
				{
					@Override
					public void onItemClick(AdapterView<?> adapter, View arg1,	int position, long arg3)
					{
						navigateEventDetails(adapter.getItemAtPosition(position).toString());
					}
				});
				// set action listener
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	private void navigateFriendDetails(String friend)
	{
		Intent friendDetails = new Intent(this, FriendDetailsActivity.class);
		friendDetails.putExtra("friend", friend); // Pass this to the other intent.
		startActivity(friendDetails);
	}
	
	private void navigateEventDetails(String event)
	{
		System.out.println("navigate to: " + event.split(" ~ ")[0]);
		
		// Open new window (intent) with the recipename here.
		Intent eventDetails = new Intent(this, EventDetailsActivity.class);
		eventDetails.putExtra("event", event.split(" ~ ")[0]); // Pass this to the other intent.
		startActivity(eventDetails);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.invites, menu);
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
	
	class AsyncInviteHandler extends AsyncTask<String, Void, ArrayList<String>>
	{

		@Override
		protected ArrayList<String> doInBackground(String... args)
		{
			DWMAndroidFacade facade = DWMAndroidFacade.getFacade();
			
			if(args[0].equals("friends"))
			{
				System.out.println("Args == friends!");
				try
				{
					return facade.getFriendInvites();
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
			}
			else if(args[0].equals("events"))
			{
				try
				{
					return facade.getEventInvites();
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
			}
			
			System.out.println("Returning null here!");
			return null;
		}
		
	}
}
