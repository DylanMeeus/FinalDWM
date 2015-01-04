package net.itca.dwm.android.activities;

import java.util.ArrayList;

import net.itca.dwm.android.R;
import net.itca.dwm.android.R.id;
import net.itca.dwm.android.R.layout;
import net.itca.dwm.android.R.menu;
import net.itca.dwm.android.activities.FriendActivity.AsyncFriendHandler;
import net.itca.dwm.android.core.DWMAndroidFacade;
import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class RecipeActivity extends ActionBarActivity
{

	private ArrayList<String> recipes;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recipe);

		// fetch recipes!
		DWMAndroidFacade facade = DWMAndroidFacade.getFacade();
		if (!facade.getConnected())
		{
			alterGUI("no internet");
			// Get cached recipes
			recipes = facade.getCachedRecipes(this);
			// fetch some cached recipes
		} else
		{
			try
			{
				recipes = new AsyncRecipeHandler().execute().get();
				facade.cacheRecipes(recipes, this);
			} catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
		// Check the connection status
		if (recipes != null)
		{
			fillList();
		}

	}

	private void alterGUI(String reason)
	{
		if (reason.equals("no internet")) // think I used "no connection" some
											// days ago as well. Ah well,
											// refactor later.
		{
			// Remove the add button. (I could also cache the actions and wait
			// for the user to have internet again. Then update the DB from
			// cached memory, but I think I won't have enough time to implement
			// this
			Button addButton = (Button) findViewById(R.id.NavigateCreateRecipeButton);
			addButton.setVisibility(View.GONE);
		}
	}

	private void fillList()
	{
		try
		{
			System.out.println("Setting listview");
			final ListView list = (ListView) findViewById(R.id.RecipeList);
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_1, recipes);
			list.setAdapter(adapter);
			// set action listener when connected.
			if (DWMAndroidFacade.getFacade().getConnected())
			{
				list.setOnItemClickListener(new OnItemClickListener()
				{
					@Override
					public void onItemClick(AdapterView<?> adapter, View arg1,
							int position, long arg3)
					{
						navigateDetails(adapter.getItemAtPosition(position)
								.toString());
					}
				});
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	// handle a callback
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		System.out.println("in callback");
		super.onActivityResult(requestCode, resultCode, data);
		System.out.println("Requestcode: " + requestCode);
		switch (requestCode)
		{
		case 0xC0DE:
		{
			if (resultCode == Activity.RESULT_OK)
			{
				// fetch the data again
				try
				{
					recipes = new AsyncRecipeHandler().execute().get();
				} catch (Exception ex)
				{
					ex.printStackTrace();
				}
				if (recipes != null) // More than three lines of indentation, Linus Torvalds would not like this!
				{
					fillList();
				}
			}
		}
		}
	}

	private void navigateDetails(String recipeName)
	{
		// Open new window (intent) with the recipename here.
		Intent recipeDetails = new Intent(this, RecipeDetailsActivity.class);
		recipeDetails.putExtra("recipe", recipeName); // Pass this to the other
														// intent.
		startActivity(recipeDetails);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.recipe, menu);
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
		Intent addRecipe = new Intent(this, AddRecipeActivity.class);
		startActivityForResult(addRecipe, 0xC0DE);
	}

	class AsyncRecipeHandler extends AsyncTask<Void, Void, ArrayList<String>>
	{

		@Override
		protected ArrayList<String> doInBackground(Void... params)
		{
			ArrayList<String> recipes = null;
			DWMAndroidFacade facade = DWMAndroidFacade.getFacade();
			try
			{
				recipes = facade.getRecipes();
			} catch (Exception ex)
			{
				ex.printStackTrace();
			}

			return recipes;
		}
	}
}
