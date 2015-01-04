package net.itca.dwm.android.activities;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import net.itca.dwm.android.R;
import net.itca.dwm.android.R.id;
import net.itca.dwm.android.R.layout;
import net.itca.dwm.android.R.menu;
import net.itca.dwm.android.core.DWMAndroidFacade;
import net.itca.dwm.android.dialogs.AddFriendDialog;
import net.itca.dwm.exceptions.ServiceException;
import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class FriendActivity extends ActionBarActivity
{

	private Intent thisIntent;
	private ArrayList<String> selectedFriends;
	private ArrayList<String> friends;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friend);
		thisIntent = getIntent();
		// Load the data for our list

		DWMAndroidFacade facade = DWMAndroidFacade.getFacade();
		if (facade.getConnected())
		{
			selectedFriends = new ArrayList<String>();
			try
			{
				friends = new AsyncFriendHandler().execute(
						"getfriends").get();
				
				// Cache it as well now we have it!
				facade.cacheFriends(friends, this);  // Rather fill the list before caching. I/O is slow so maybe there's noticeable impact if people have a lot of friends? Maybe cache fb friends some day?
				
				
			} catch (Exception e)
			{
				e.printStackTrace();
			}
			alterGUI("calling");
		} else
		{
			// But we still have the friends!
			alterGUI("offline");
			// We still have the friends though.
			friends = facade.getCachedFriends(this);
		}
		if(friends!=null)
		{
			fillFriendsList(); // always call outside so it gets called off/online
		}
	}
	
	private void fillFriendsList()
	{
		System.out.println("Setting listview");
		final ListView list = (ListView) findViewById(R.id.FriendList);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, friends);
		list.setAdapter(adapter);
		list.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> adapter, View view,
					int position, long arg3)
			{
				if (thisIntent.getStringExtra("origin") != null)
				{
					if (thisIntent.getStringExtra("origin").equals(
							"AddEvent"))
					{
						list.setItemChecked(position, true);
						selectedFriends.add(adapter.getItemAtPosition(
								position).toString());
						view.setBackgroundColor(Color.GREEN);
					}
				}
			}
		});
	}

	private void alterGUI(String reason)
	{
		// Change gui depending on calling activity

		if (reason.equals("calling"))
		{
			if (thisIntent.getStringExtra("origin") != null
					&& thisIntent.getStringExtra("origin").equals("AddEvent"))
			{
				Button addFriend = (Button) findViewById(R.id.AddFriendButton);
				addFriend.setVisibility(View.GONE);

				// Alter listview to accept multiple selection
				ListView friendsList = (ListView) findViewById(R.id.FriendList);
				friendsList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

				// set the send button to visible
				Button sendButton = (Button) findViewById(R.id.FriendSendInvitesButton);
				sendButton.setVisibility(View.VISIBLE);

			}
		}
		else if(reason.equals("offline"))
		{
			Button addButton = (Button) findViewById(R.id.AddFriendButton);
			addButton.setVisibility(View.GONE);
		}
	}

	public void SendInvitesClick(View view)
	{
		Intent result = new Intent();

		// get all selected items from list.

		result.putStringArrayListExtra("friends", selectedFriends);
		setResult(Activity.RESULT_OK, result);
	
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.friend, menu);
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

	public void AddFriendClick(View view)
	{
		Intent addFriendDialog = new Intent(this, AddFriendDialog.class);
		startActivity(addFriendDialog);
	}

	class AsyncFriendHandler extends AsyncTask<String, Void, ArrayList<String>>
	{

		@Override
		protected ArrayList<String> doInBackground(String... params)
		{
			// Depending on the input in params,choose an option?
			DWMAndroidFacade facade = DWMAndroidFacade.getFacade();
			ArrayList<String> friends = null;
			if (params[0].equals("getfriends"))
			{
				try
				{
					System.out.println("Getting friends");
					friends = facade.getFriendsByUserID();
					System.out.println("Friends recieved");
				} catch (ServiceException e)
				{
					e.printStackTrace();
				}
			}

			return friends;
		}

	}
}
