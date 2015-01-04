package net.itca.dwm.android.activities;

import java.util.ArrayList;

import net.itca.dwm.android.R;
import net.itca.dwm.android.R.id;
import net.itca.dwm.android.R.layout;
import net.itca.dwm.android.R.menu;
import net.itca.dwm.android.core.DWMAndroidFacade;
import net.itca.dwm.core.DineWithMeFacade;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class EventActivity extends Activity
{

	private ArrayList<String> hosting, attending;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event);
		DWMAndroidFacade facade = DWMAndroidFacade.getFacade();
		
		if(facade.getConnected())
		{
			// fetch data
			try
			{
				hosting = new AsyncEventHandler().execute("hosting").get();
				attending = new AsyncEventHandler().execute("attending").get();
				// Also cache this
				
				facade.cacheHostingEvents(hosting, this);
				facade.cacheAttendingEvents(attending, this);
				
			} catch (Exception ex)
			{
				ex.printStackTrace();
			}
			
		}
		else
		{
			System.out.println("Trying to fetch local data");
			readFromCache();
			alterGUI("offline");
		}
		
		fillGUI();
	}
	
	private void readFromCache()
	{
		hosting = DWMAndroidFacade.getFacade().getCachedHostingEvents(this);
		attending = DWMAndroidFacade.getFacade().getCachedAttendingEvents(this);
	}
	
	private void alterGUI(String reason)
	{
		if(reason.equals("offline"))
		{
			Button addButton = (Button) findViewById(R.id.NavigateAddEvent);
			addButton.setVisibility(View.GONE);
		}
	}

	private void fillGUI()
	{
		if (hosting != null && !hosting.isEmpty())
		{
			try
			{
				System.out.println("Setting hosted events");
				ListView list = (ListView) findViewById(R.id.EventMyEventList);
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
						android.R.layout.simple_list_item_1, hosting);
				list.setAdapter(adapter);
				// set action listener when connected
				if(DWMAndroidFacade.getFacade().getConnected())
					{
					list.setOnItemClickListener(new OnItemClickListener()
					{
						@Override
						public void onItemClick(AdapterView<?> adapter, View arg1,
								int position, long arg3)
						{
							navigateEventDetails(adapter
									.getItemAtPosition(position).toString());
						}
					});
				}
			} catch (Exception e)
			{
				e.printStackTrace();
			}
			

			
		}

		if (attending != null && !attending.isEmpty())
		{
			try
			{
				System.out.println("Setting attending events");
				ListView list = (ListView) findViewById(R.id.EventAcceptedEvents);
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
						android.R.layout.simple_list_item_1, attending);
				list.setAdapter(adapter);
				// set action listener when connected
				if(DWMAndroidFacade.getFacade().getConnected())
				{
					list.setOnItemClickListener(new OnItemClickListener()
					{
						@Override
						public void onItemClick(AdapterView<?> adapter,
								View arg1, int position, long arg3)
						{
							navigateEventDetails(adapter.getItemAtPosition(
									position).toString());
						}
					});
				}
			} catch (Exception e)
			{
				e.printStackTrace();
			}
			
			
			// Also cache this
		}
	}

	private void navigateEventDetails(String event)
	{

		Intent eventDetails = new Intent(this, EventDetailsActivity.class);
		if (hosting.contains(event))
		{
			System.out.println("Hosting: " + event);
			eventDetails.putExtra("event", event); // Pass this to the other
													// intent.
		} else
		{
			eventDetails.putExtra("event", event.split("\\|")[0]); // Pass this
																	// to the
																	// other
																	// intent.
		}

		System.out.println("Navigate to event details");
		eventDetails.putExtra("origin", "EventActivity");
		startActivity(eventDetails);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.event, menu);
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
	
	public void EventClick(View view)
	{
		Intent addEvent = new Intent(this,AddEventActivity.class);
		startActivityForResult(addEvent,0xF00D);
	}
	
	
	// handle the callback to update the list
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode)
		{
		case 0xF00D:
		{
			if (resultCode == Activity.RESULT_OK)
			{
				// fetch the data
				try
				{
					hosting = new AsyncEventHandler().execute("hosting").get();
					attending = new AsyncEventHandler().execute("attending").get();
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
				fillGUI(); // Button to add is gone when offline, so no risk of fetching data again! :)
				// Show a toast to inform the user that the result from the adding was OK, so it's added!
				// Good thing fillGUI checks for NPE's
				Toast.makeText(getApplicationContext(),"Event added!",Toast.LENGTH_SHORT).show();
			}
		}
		}
	}

	
	
	class AsyncEventHandler extends AsyncTask<String, Void, ArrayList<String>>
	{
		@Override
		protected ArrayList<String> doInBackground(String... args)
		{

			ArrayList<String> results = null;
			DWMAndroidFacade facade = DWMAndroidFacade.getFacade();
			if (args[0].equals("hosting"))
			{
				try
				{
					results = facade.getEvents();
				} catch (Exception ex)
				{
					ex.printStackTrace();
				}
			} else if (args[0].equals("attending"))
			{
				try
				{
					results = facade.getAcceptedEvents();
				} catch (Exception ex)
				{
					ex.printStackTrace();
				}
			}

			return results;
		}

	
	}
}
