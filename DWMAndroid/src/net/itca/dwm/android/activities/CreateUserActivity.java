package net.itca.dwm.android.activities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import net.itca.dwm.android.R;
import net.itca.dwm.android.R.id;
import net.itca.dwm.android.R.layout;
import net.itca.dwm.android.R.menu;
import net.itca.dwm.android.core.DWMAndroidFacade;
import net.itca.dwm.core.DineWithMeFacade;
import net.itca.dwm.core.Encrypter;
import net.itca.dwm.data.Database;
import net.itca.dwm.exceptions.PasswordException;
import android.support.v7.app.ActionBarActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class CreateUserActivity extends ActionBarActivity
{

	protected String url = "jdbc:postgresql://gegevensbanken.khleuven.be:51415/o3?sslfactory=org.postgresql.ssl.NonValidatingFactory&ssl=true";
	protected Connection connection;
	protected String dbpassword = "Proton16021";
	protected String schema = "public";
	protected String user = "r0368004";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_user);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create_user, menu);
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

	public void onRegisterClick(View view)
	{
		EditText userText = (EditText) findViewById(R.id.dynCreateUserUsername);
		EditText firstnameText = (EditText) findViewById(R.id.dynCreateUserFirstname);
		EditText lastnameText = (EditText) findViewById(R.id.dynCreateUserLastname);
		EditText passwordText = (EditText) findViewById(R.id.dynCreateUserPassword);

		String username = userText.getText().toString();
		String passwordUnencrypted = passwordText.getText().toString();
		String firstname = firstnameText.getText().toString();
		String lastname = lastnameText.getText().toString();
		DWMAndroidFacade facade = DWMAndroidFacade.getFacade();
		String encrypted = facade.encrypt(passwordUnencrypted);
		System.out.println("Creating handler");
		
		boolean created = false;
		try
		{
			created = new AsyncHandler(username,encrypted,firstname,lastname).execute("").get();
		}
		catch(Exception ex){}
		
		if(!created)
		{
			Toast.makeText(getApplicationContext(), "Username already taken!", Toast.LENGTH_SHORT).show();
			userText.setText("");
		}
		else
		{			
			finish();
		}
		
		
		
	}

	class AsyncHandler extends AsyncTask<String, Void, Boolean>
	{

		private String username, password, firstname, lastname;

		public AsyncHandler(String user, String pass, String first, String last)
		{
			username = user;
			password = pass;
			firstname = first;
			lastname = last;
		}

		@Override
		protected Boolean doInBackground(String... params)
		{
			System.out.println("Performing background task");
			DWMAndroidFacade facade = DWMAndroidFacade.getFacade();
			try
			{
				System.out.println("Creating user");
				facade.createUser(username, password, firstname, lastname);
				return true;
			} 
			catch (Exception ex)
			{
				ex.printStackTrace();
			}

			return false;
		}

	}

}
