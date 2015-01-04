package net.itca.dwm.android.activities;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.itca.dwm.android.R;
import net.itca.dwm.android.R.id;
import net.itca.dwm.android.R.layout;
import net.itca.dwm.android.R.menu;
import net.itca.dwm.android.core.DWMAndroidFacade;
import net.itca.dwm.core.DineWithMeFacade;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class AddRecipeActivity extends Activity
{
	static final int REQUEST_IMAGE_CAPTURE = 1;
	static final int REQUEST_TAKE_PHOTO = 1; // Wtf google 'javadoc?'

	// TODO : Make sure after the recipe is handled, we push a callback to
	// Recipe'overview' so it can update the list
	// TODO : Stop spreading random TODO's around the code.

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_recipe);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_recipe, menu);
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

	public void SaveClick(View view)
	{
		// fetch data from the view
		EditText recipetitle = (EditText) findViewById(R.id.dynAddRecipeTitle); // Very
																				// "legacy
																				// to
																				// use
																				// dyn.
																				// I
																				// didn't
																				// really
																				// follow
																				// with
																				// this
																				// convention
																				// apart
																				// from
																				// the
																				// first
																				// tryout.
		EditText recipepeople = (EditText) findViewById(R.id.dynAddRecipePeople);
		String title = recipetitle.getText().toString();
		String people = recipepeople.getText().toString();

		// now fetch the multiline instructions

		EditText recipeingredients = (EditText) findViewById(R.id.AddRecipeIngredients);
		EditText recipeinstructions = (EditText) findViewById(R.id.AddRecipeInstructions);
		String ingredients = recipeingredients.getText().toString();
		String instructions = recipeinstructions.getText().toString();

		System.out.println("Creating recipe with: " + title + "\n" + people
				+ "\n" + ingredients + "\n" + instructions);

		boolean numberOK = false;
		try
		{
			Integer.parseInt(people);
			numberOK = true;
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}

		if (numberOK)
		{
			new AsyncRecipeHandler().execute(title, people, ingredients,
					instructions);

			setResult(Activity.RESULT_OK);
			finish();
		} else
		{
			Toast.makeText(getApplicationContext(),"People should be a number!",Toast.LENGTH_SHORT).show();
		}

	}

	/*
	 * PHOTO STUFF SOURCE:
	 * http://developer.android.com/training/camera/photobasics
	 * .html#TaskCaptureIntent All I did to this code was alter it in small
	 * parts to be more uniform with the rest of the code Here and there some
	 * changes with regards to naming as well.
	 */
	public void PicClick(View view)
	{

		System.out.println("Taking a photo!");
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// Ensure that there's a camera activity to handle the intent
		if (takePictureIntent.resolveActivity(getPackageManager()) != null)
		{
			// Create the File where the photo should go
			File photoFile = null;
			try
			{
				System.out.println("Trying to create image file");
				photoFile = createImageFile();
			} catch (Exception ex)
			{
				// Error occurred while creating the File
				ex.printStackTrace();
			}
			// Continue only if the File was successfully created
			if (photoFile != null)
			{
				takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
						Uri.fromFile(photoFile));
				startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK)
		{
			// When saving to file (which I want to do, the result == empty?)
			try
			{
				// Bundle extras = data.getExtras(); Google's android docs
				// messed up here. They should have mentioned you can !!NOT!! do
				// this when saving to file.
				// System.out.println("Got extras");
				// Bitmap imageBitmap = (Bitmap) extras.get("data");
				Toast.makeText(getApplicationContext(),
						"Picture saved on device!", Toast.LENGTH_SHORT).show();
			} catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}

	String mCurrentPhotoPath;

	private File createImageFile() throws IOException
	{
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		String imageFileName = "JPEG_" + timeStamp + "_";
		File storageDir = Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		File image = File.createTempFile(imageFileName, /* prefix */
				".jpg", /* suffix */
				storageDir /* directory */
		);

		// Save a file: path for use with ACTION_VIEW intents
		mCurrentPhotoPath = "file:" + image.getAbsolutePath();
		System.out.println("image uri: " + image.getAbsolutePath());
		return image;
	}

	// provide it in some android photo gallery

	private void galleryAddPic()
	{
		Intent mediaScanIntent = new Intent(
				Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		File f = new File(mCurrentPhotoPath);
		Uri contentUri = Uri.fromFile(f);
		mediaScanIntent.setData(contentUri);
		this.sendBroadcast(mediaScanIntent);
	}

	/* END PHOTO STUFF (The code below is thus my own) */

	class AsyncRecipeHandler extends AsyncTask<String, Void, Void>
	{

		@Override
		protected Void doInBackground(String... args)
		{
			DWMAndroidFacade facade = DWMAndroidFacade.getFacade();
			String title = args[0];
			String people = args[1];
			String ingredients = args[2];
			String instructions = args[3];
			try
			{
				// remember to encode for database when multiline text is
				// involved! (\r \n)
				facade.createRecipe(title, facade.encodeForDB(ingredients),
						facade.encodeForDB(instructions),
						Integer.parseInt(people)); // Parsing here can create a
													// seperate error. Should
													// probably consider
													// multi-catch so..
			} catch (Exception ex)
			{
				ex.printStackTrace();
			}

			return null;
		}

	}

}
