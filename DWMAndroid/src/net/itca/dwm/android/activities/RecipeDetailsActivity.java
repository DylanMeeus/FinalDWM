package net.itca.dwm.android.activities;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import net.itca.dwm.android.R;
import net.itca.dwm.android.R.id;
import net.itca.dwm.android.R.layout;
import net.itca.dwm.android.R.menu;
import net.itca.dwm.android.activities.RecipeActivity.AsyncRecipeHandler;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class RecipeDetailsActivity extends Activity
{
	private String name, people;
	private ArrayList<String> ingredients, instructions;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recipe_details);
		
		// Instantiate the arraylists
		ingredients = new ArrayList<String>();
		instructions = new ArrayList<String>();
		
		// Fetch the parameters used to start this.
		Intent thisIntent = getIntent();
		String recipe = thisIntent.getStringExtra("recipe");
		
		
		// Fetch the details of this recipe.
		try
		{
			String details = new AsyncRecipeDetailHandler().execute(recipe)
					.get();
			// we get back a quite unclear string. But there are names that we
			// can use to "format" it?
		
			String split[] = details.split("\n");
			name = split[0].substring("name: ".length()); // we know the name because it is in our intent. Might be easier. but now it's more 'uniform' or something of the sort.
			people = split[1];
			
			
			boolean inInstructions = false;
			// Ingredients
			for(int splitIndex = 2; splitIndex < split.length; splitIndex++) // Tried to make the variable name clear. (Code Comlete²) It starts at 2 because the first 2 of split[] are 'name and people'
			{
				if(split[splitIndex].contains("instructions")) // This is our 'break' condition.
				{
					inInstructions = true;
				}
				// else
				
				if(!inInstructions)
				{
					if(split[splitIndex].contains("ingredients"))
					{
						ingredients.add(split[splitIndex].substring("ingredients: ".length()));
					}
					else
					{					
						ingredients.add(split[splitIndex]);
					}
				}
				else
				{
					if(split[splitIndex].contains("instructions"))
					{
						instructions.add(split[splitIndex].substring("instructions: ".length()));
					}
					else
					{					
						instructions.add(split[splitIndex]);
					}
				}
			}
		} 
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
		fillGUI();

	}
	
	
	private void fillGUI() // add the details to the GUI.
	{
		TextView recipepeople = (TextView) findViewById(R.id.RecipeDetailsPeopleLabel);
		TextView recipename = (TextView) findViewById(R.id.RecipeDetailsNameLabel);
		recipename.setText(name);
		recipepeople.setText(people);
		
		
		
		// Fill the ingredients listview
		try
		{
			System.out.println("Setting ingredients");
			final ListView list = (ListView) findViewById(R.id.RecipeDetailsIngredientsList);
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_1, ingredients);
			list.setAdapter(adapter);
			// set action listener
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		
		// Add instructions
		try
		{
			System.out.println("Setting instructions");
			final ListView list = (ListView) findViewById(R.id.RecipeDetailsInstructions);
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_1, instructions);
			list.setAdapter(adapter);
			// set action listener
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.recipe_details, menu);
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

	class AsyncRecipeDetailHandler extends AsyncTask<String, Void, String>
	{

		@Override
		protected String doInBackground(String... args) // Why the ... instead
														// of String[]?!
		{
			DWMAndroidFacade facade = DWMAndroidFacade.getFacade();
			String details = "";
			try
			{
				details = facade.getRecipeDetails(args[0]);
			} catch (Exception ex)
			{
				ex.printStackTrace();
				details = "Something went wrong :(";
			}
			return details;
		}

	}
}
