package net.itca.dwm.android.activities;

import net.itca.dwm.android.R;
import net.itca.dwm.android.R.id;
import net.itca.dwm.android.R.layout;
import net.itca.dwm.android.R.menu;
import net.itca.dwm.android.core.DWMAndroidFacade;
import net.itca.dwm.core.User;
import net.itca.dwm.exceptions.ServiceException;
import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends ActionBarActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		// Check if the user is cached (saved / remembered)
		DWMAndroidFacade facade = DWMAndroidFacade.getFacade();
		System.out.println("Is this user cached? " + facade.userCached(this));

		if(!connectionAvailable())
		{
			alterGUI("no connection");
			if(facade.userCached(this)) // no connection but there is a cached user!
			{
				Intent navigateMenu = new Intent(this, MenuActivity.class);
				startActivity(navigateMenu);
			}
		}
		else
		{
			System.out.println("in the else");
			if (facade.userCached(this))
			{
				// the user used the "remember-me functionality" TODO: on logout
				// remove this!!
	
				// fetch user info and stuff
				System.out.println("Remembering a user.");
				String username = facade.getCachedUsername(this);
				new AsyncLoginHandler().execute("loginCachedUser", username);
				Intent navigateMenu = new Intent(this, MenuActivity.class);
				startActivity(navigateMenu);
			}
		}
		

	}

	
	private void alterGUI(String reason)
	{
		if(reason.equals("no connection"))
		{
			EditText userText = (EditText) findViewById(R.id.dynUsername);
			EditText passText = (EditText) findViewById(R.id.dynPassword);
			userText.setVisibility(View.GONE);
			passText.setVisibility(View.GONE);
			Button loginButton = (Button) findViewById(R.id.loginButton);
			loginButton.setVisibility(View.GONE);
			Button register = (Button) findViewById(R.id.createButton);
			register.setVisibility(View.GONE);
			CheckBox remember = (CheckBox) findViewById(R.id.RememberMeBox);
			remember.setVisibility(View.GONE);
			
			
			
			// show popup with info
			System.out.println("Building the alert dialog");
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Connection issues!")
					.setMessage("Sorry, you can not log in to this application without internet. Use 'Remember Me' next time you log in, to have basic functionality without internet!")
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener()
							{
								public void onClick(DialogInterface dialog,
										int which)
								{
									finish();
								}
							});
			builder.show();
		}
	}

	// Only the first two windows that can show need this method, afterwards the facade can take care of it. Not entirely DRY :(
	private boolean connectionAvailable()
	{
		boolean connected = false;
		ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if(networkInfo != null)
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
		getMenuInflater().inflate(R.menu.login, menu);
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

	public void onLoginClick(View view)
	{
		EditText userText = (EditText) findViewById(R.id.dynUsername);
		EditText passText = (EditText) findViewById(R.id.dynPassword);
		String userString = userText.getText().toString();
		String passString = passText.getText().toString();

		try
		{
			boolean succes = new AsyncLoginHandler(userString, passString)
					.execute().get();
			if (succes)
			{
				// Check if we need to cache the username/pw
				CheckBox rememberbox = (CheckBox) findViewById(R.id.RememberMeBox);

				if (rememberbox.isChecked())
				{ // cache it
					DWMAndroidFacade.getFacade().RememberUser(userString, this);
				}

				Intent navigateMenu = new Intent(this, MenuActivity.class);
				startActivity(navigateMenu);
			} else
			{
				System.out.println("Login failed");
			}
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public void onCreateClick(View view)
	{
		Intent createUser = new Intent(this, CreateUserActivity.class);
		startActivity(createUser);
	}

	class AsyncLoginHandler extends AsyncTask<String, Void, Boolean>
	{

		private String username, password;

		public AsyncLoginHandler(String user, String pass)
		{
			username = user;
			password = pass;
		}

		public AsyncLoginHandler()
		{

		}

		@Override
		protected Boolean doInBackground(String... args)
		{
			DWMAndroidFacade f = DWMAndroidFacade.getFacade();
			String encrypted = f.encrypt(password);
			try
			{
				System.out.println("trying to log in with: " + username + " "
						+ encrypted);
				if (f.login(username, encrypted))
				{
					User user = new User(username, f.getUserID(username));
					f.setCurrentUser(user);
					return true;
				}
			} catch (ServiceException e)
			{
				e.printStackTrace();
			}

			if (args[0].equals("loginCachedUser"))
			{
				String username = args[1];
				User user;
				try
				{
					user = new User(username, f.getUserID(username));
					f.setCurrentUser(user);
				} catch (ServiceException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			System.out.println("At the end here");
			return false;
		}

	}

}
