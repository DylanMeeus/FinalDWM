package net.itca.dwm.android.activities;

import net.itca.dwm.android.R;
import net.itca.dwm.android.R.id;
import net.itca.dwm.android.R.layout;
import net.itca.dwm.android.R.menu;
import net.itca.dwm.android.core.DWMAndroidFacade;
import net.itca.dwm.core.DineWithMeFacade;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MenuActivity extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu_activity);

		// If the user is not connected, show a message!
		if (connectionAvailable())
		{
			System.out.println("Connection available!");
		} else
		{
			System.out.println("No connection available!");
			Toast.makeText(getApplicationContext(),
					"Functionality is limited without internet!!",
					Toast.LENGTH_LONG).show();
			alterGUI();
		}
	}

	private void alterGUI()
	{
		// Hide the logout and "invites" option.
		// Logout == Useless, because it would render the app unusable
		// Invites == useless, because you can't accept or decline them.

		Button logout = (Button) findViewById(R.id.menu_Logout);
		Button invites = (Button) findViewById(R.id.menu_ViewInvites);
		logout.setVisibility(View.GONE);
		invites.setVisibility(View.GONE);
		TextView menuText = (TextView) findViewById(R.id.menu_Menu);
		menuText.setText("Menu - OFFLINE");
	}

	@Override
	public void onBackPressed()
	{
		System.out.println("Hardware back button");
		if(!connectionAvailable())
		{
			System.exit(0);
		}
		else
		{
			super.onBackPressed();
		}
	}

	private boolean connectionAvailable()
	{
		boolean connected = false;
		ConnectivityManager connectivityManager = (ConnectivityManager) this
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo != null)
		{
			connected = networkInfo.isConnected();
		}
		connectivityManager = null;
		networkInfo = null;
		DWMAndroidFacade facade = DWMAndroidFacade.getFacade();
		facade.setConnected(connected);
		return connected;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_activitiy, menu);
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

	public void FriendsClick(View view)
	{
		Intent friends = new Intent(this, FriendActivity.class);
		startActivity(friends);
	}

	public void RecipesClick(View view)
	{
		Intent recipes = new Intent(this, RecipeActivity.class);
		startActivity(recipes);
	}

	public void InvitesClick(View view)
	{
		Intent invites = new Intent(this, InvitesActivity.class);
		startActivity(invites);
	}

	public void EventsClick(View view)
	{
		Intent events = new Intent(this, EventActivity.class);
		startActivity(events);
	}

	public void LogoutClick(View view)
	{
		DWMAndroidFacade facade = DWMAndroidFacade.getFacade();
		facade.removeCachedUsername(this);
		finish();
	}
}
