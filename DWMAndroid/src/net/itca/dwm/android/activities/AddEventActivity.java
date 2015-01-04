package net.itca.dwm.android.activities;

import java.util.ArrayList;
import java.util.List;

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
import android.text.StaticLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

public class AddEventActivity extends Activity
{

	private List<String> recipes;
	private List<String> invitees;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_event);

		// Get items for spinner
		try
		{
			recipes = new AsyncEventHandler().execute("recipes").get();
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}

		fillGUI();
	}

	private void fillGUI()
	{
		// Get the spinner

		if (recipes != null)
		{
			populateSpinner();
		}
	}

	private void populateSpinner()
	{
		Spinner spinner = (Spinner) findViewById(R.id.AddEventRecipeSpinner);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, recipes);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_event, menu);
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

	public void InviteClick(View view)
	{
		// Start an altered friend activity!
		System.out.println("Inviting friends");
		Intent addFriends = new Intent(this, FriendActivity.class);
		addFriends.putExtra("origin", "AddEvent");
		startActivityForResult(addFriends, 0xDEAD); // 0xDEAD, just some static
													// integer value to mark the
													// intent.
	}
	
	public void SaveClick(View view)
	{
		// Fetch data
		EditText eventbox = (EditText) findViewById(R.id.addEventName);
		String eventname = eventbox.getText().toString();
		Spinner recipeSpinner = (Spinner) findViewById(R.id.AddEventRecipeSpinner);
		String recipename = recipeSpinner.getSelectedItem().toString();
		
		DatePicker datepicker = (DatePicker) findViewById(R.id.AddEventDatepicker);
		TimePicker timepicker = (TimePicker) findViewById(R.id.AddEventTimepicker);
		
		int day = datepicker.getDayOfMonth();
		int month = datepicker.getMonth()+1;
		int year = datepicker.getYear();
		String date = month + "/" + day + "/" + year;
		
		int hour = timepicker.getCurrentHour();
		int min = timepicker.getCurrentMinute();
		
		String time = hour + ":" + min;
		System.out.println("Save data: " + eventname + " " + recipename+ " " + date + " " + time);
		try
		{
			new AsyncEventHandler().execute("save",eventname,date,time,recipename);		
			setResult(RESULT_OK);
			finish();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

	// Fetch data back from child-activities

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode)
		{
		case 0xDEAD:
		{
			if (resultCode == Activity.RESULT_OK)
			{
				invitees = data.getStringArrayListExtra("friends");
				System.out.println("People invited: " + invitees.size());
				// set save to visible
				Button saveButton = (Button) findViewById(R.id.EventSaveClick);
				saveButton.setVisibility(View.VISIBLE);
			}
		}
		}
	}

	class AsyncEventHandler extends AsyncTask<String, Void, List<String>>
	{

		@Override
		protected List<String> doInBackground(String... args)
		{
			DWMAndroidFacade facade = DWMAndroidFacade.getFacade();
			if (args[0].equals("recipes"))
			{
				try
				{
					return facade.getRecipes();
				} catch (Exception ex)
				{
					ex.printStackTrace();
				}
			} else if (args[0].equals("save"))
			{
				// fetch the other variables
				String eventname = args[1];
				String date = args[2];
				String time = args[3];
				String recipename = args[4];
				try
				{
					// Create the event
					facade.createEvent(eventname, date, time, recipename);
					// send the invites
					for(String friend : invitees)
					{
						facade.InviteFriend(eventname, friend.split(" ")[0]);
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
