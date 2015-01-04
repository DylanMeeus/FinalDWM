package net.itca.dwm.android.dialogs;

import net.itca.dwm.android.R;
import net.itca.dwm.android.R.id;
import net.itca.dwm.android.R.layout;
import net.itca.dwm.android.R.menu;
import net.itca.dwm.android.core.DWMAndroidFacade;
import net.itca.dwm.core.DineWithMeFacade;
import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class AddFriendDialog extends Activity // Fixed the "You need to use a theme.appcompat theme error" caused by ActionBarActivity"
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_friend_dialog);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_friend_dialog, menu);
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
	
	public void AddClick(View view)
	{
		EditText friendText = (EditText) findViewById(R.id.dynAddFriendFriendname);
		String friend = friendText.getText().toString();
		new AsyncFriendInviteHandler().execute(friend);
		finish(); // "Back button functionality"
	}
	
	
	class AsyncFriendInviteHandler extends AsyncTask<String,Void,Void>
	{

		@Override
		protected Void doInBackground(String... params)
		{
			DWMAndroidFacade facade = DWMAndroidFacade.getFacade();
			try
			{
				facade.addFriend(params[0]);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
			
			return null;
		}
		
	}
	
	// Async task again!
	
}
