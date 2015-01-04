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
import android.widget.TextView;

public class FriendDetailsActivity extends Activity
{

	
	private String friend;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friend_details);
		// Get data passed to this one!
		
		Intent thisIntent = getIntent();
		friend = thisIntent.getStringExtra("friend");
		fillGUI(friend);
	}
	
	
	private void fillGUI(String friendInfo)
	{
		String split[] = friendInfo.split(" "); 
		// Get the texts
		TextView username = (TextView) findViewById(R.id.FriendDetailsUsername);
		TextView firstname = (TextView) findViewById(R.id.FriendDetailsFirstname);
		TextView lastname = (TextView) findViewById(R.id.FriendDetailsLastname);
		
		username.setText(split[0]);
		firstname.setText(split[1]);
		// Those are not allowed to have some spaces, but the last names of certain people do. (E.g: John Von Neumann)
		String last = "";
		for(int index = 2; index < split.length; index++)
		{
			last += split[index] + " ";
		}
		lastname.setText(last);
	}
	
	public void DeclineClick(View view)
	{
		new AsyncFriendHandler().execute("decline");
	}
	
	public void AcceptClick(View view)
	{
		new AsyncFriendHandler().execute("accept");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.friend_details, menu);
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
	
	class AsyncFriendHandler extends AsyncTask<String, Void, Boolean>
	{

		@Override
		protected Boolean doInBackground(String... args)
		{
			
			DWMAndroidFacade facade = DWMAndroidFacade.getFacade();
			if(args[0].equals("decline"))
			{
				try
				{
					facade.declineRequest(friend.split(" ")[0]);
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
					facade.acceptFriend(friend.split(" ")[0]);
					finish();
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
			}
			
			
			return false;
		}
		
	}
}
