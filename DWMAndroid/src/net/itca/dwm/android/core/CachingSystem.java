package net.itca.dwm.android.core;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;

/**
 * Class responsible for caching in a property-style fashion.
 * 
 * @author Dylan
 *
 */
public class CachingSystem extends Activity
{

	Properties cachedprops = new Properties();
	Activity sendActivity;
	File savedFilesDir;

	public CachingSystem(Activity sender)
	{
		sendActivity = sender;
		savedFilesDir = sendActivity.getFilesDir();
	}

	// I don't cache the password because it'd be "unsafe"
	public void CacheUsername(String username)
	{
		System.out.println("Trying to remember: " + username);
		cachedprops.setProperty("username", username);
		saveprops();
	}

	public boolean userCached()
	{
		// Check if there is a user entered in the cache file
		try
		{
			FileInputStream inputStream = sendActivity.openFileInput("cached.properties");
			cachedprops.load(inputStream);
			if(cachedprops.containsKey("username"))
			{
				if(cachedprops.getProperty("username").equals("-1")) // Prohibit username "-1" to be created!
				{ // Contains the username property from earlier, but the user does not wish to be remembered
					return false;
				}
				else
				{
					System.out.println("Contains the key username with value: " + cachedprops.getProperty("username"));
					return true;
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return false;
	}

	
	public String getPropertyValue(String key) // TODO: remove the boolean to check if user exists, and just check the return value in the controller to give back T/F
	{
		try
		{
			FileInputStream inputStream = sendActivity.openFileInput("cached.properties");
			cachedprops.load(inputStream);
			return cachedprops.getProperty("username");
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return null;
	}
	
	private void saveprops()
	{
		List<String> availableFiles = Arrays.asList(savedFilesDir.list());
		if (availableFiles == null
				|| !availableFiles.contains("cached.properties"))
		{
			File file = new File(savedFilesDir, "cached.properties");
			try
			{
				System.out.println("Trying to create a new file");
				file.createNewFile();
			} catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}

		// write to the properties file
		System.out.println("trying to write to file");
		try
		{
			cachedprops.store(new FileOutputStream(new File(savedFilesDir,
					"cached.properties")), null);
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	
	public void cacheRecipes(ArrayList<String> recipes)
	{
		List<String> availableFiles = Arrays.asList(savedFilesDir.list());
		if (availableFiles == null
				|| !availableFiles.contains("cachedrecipes.txt"))
		{
			File file = new File(savedFilesDir, "cachedrecipes.txt");
			try
			{
				System.out.println("Trying to create a new file");
				file.createNewFile();
			} catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}

		// write to the properties file
		System.out.println("trying to write to cachedrecipes");
		try
		{
			FileOutputStream outputStream = sendActivity.openFileOutput("cachedrecipes.txt",Context.MODE_PRIVATE);
			
			for(String recipe : recipes)
			{
				outputStream.write((recipe + "=").getBytes());
			}
		} catch (IOException ex)
		{
			ex.printStackTrace();
		}
	}
	
	public ArrayList<String> readCachedRecipes()
	{
		ArrayList<String> recipes = new ArrayList<String>();
		try
		{
			FileInputStream inputStream = sendActivity
					.openFileInput("cachedrecipes.txt");
			BufferedReader bufferedReader = new BufferedReader(new FileReader(
					new File(savedFilesDir, "cachedrecipes.txt")));
			String line;
			while ((line = bufferedReader.readLine()) != null)
			{
				System.out.println("line: " + line);
				String splitfriends[] = line.split("=");
				for(String f : splitfriends)
				{
					recipes.add(f);
				}
			}
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
		return recipes;
	}
	
	
	// friends
	
	public void cacheFriends(ArrayList<String> friends)
	{
		List<String> availableFiles = Arrays.asList(savedFilesDir.list());
		if (availableFiles == null
				|| !availableFiles.contains("cachedfriends.txt"))
		{
			File file = new File(savedFilesDir, "cachedevents.txt");
			try
			{
				System.out.println("Trying to create a new file");
				file.createNewFile();
			} catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}

		// write to the properties file
		System.out.println("trying to write to cachedevents");
		try
		{
			FileOutputStream outputStream = sendActivity.openFileOutput("cachedfriends.txt",Context.MODE_PRIVATE);
			
			for(String friend : friends)
			{
				outputStream.write((friend + "=").getBytes());
			}
		} catch (IOException ex)
		{
			ex.printStackTrace();
		}
	}
	
	public ArrayList<String> readCachedFriends()
	{
		ArrayList<String> friends = new ArrayList<String>();
		try
		{
			FileInputStream inputStream = sendActivity
					.openFileInput("cachedfriends.txt");
			BufferedReader bufferedReader = new BufferedReader(new FileReader(
					new File(savedFilesDir, "cachedfriends.txt")));
			String line;
			while ((line = bufferedReader.readLine()) != null)
			{
				System.out.println("line: " + line);
				String splitfriends[] = line.split("=");
				for(String f : splitfriends)
				{
					friends.add(f);
				}
			}
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
		return friends;
	}
	
	
	public void cacheHostingEvents(ArrayList<String> events)
	{
		List<String> availableFiles = Arrays.asList(savedFilesDir.list());
		if (availableFiles == null
				|| !availableFiles.contains("cachedhostingevents.txt"))
		{
			File file = new File(savedFilesDir, "cachedevents.txt");
			try
			{
				System.out.println("Trying to create a new file");
				file.createNewFile();
			} catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}

		// write to the properties file
		System.out.println("trying to write to cachedevents");
		try
		{
			FileOutputStream outputStream = sendActivity.openFileOutput("cachedhostingevents.txt",Context.MODE_PRIVATE);
			
			for(String event : events)
			{
				outputStream.write((event + "=").getBytes());
			}
		} catch (IOException ex)
		{
			ex.printStackTrace();
		}
	}
	
	public void cacheAttendingEvents(ArrayList<String> events)
	{
		List<String> availableFiles = Arrays.asList(savedFilesDir.list());
		if (availableFiles == null
				|| !availableFiles.contains("cachedattendingevents.txt"))
		{
			File file = new File(savedFilesDir, "cachedevents.txt");
			try
			{
				System.out.println("Trying to create a new file");
				file.createNewFile();
			} catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}

		// write to the properties file
		System.out.println("trying to write to cachedevents");
		try
		{
			FileOutputStream outputStream = sendActivity.openFileOutput("cachedattendingevents.txt",Context.MODE_PRIVATE);
			
			for(String event : events)
			{
				outputStream.write((event + "=").getBytes());
			}
		} catch (IOException ex)
		{
			ex.printStackTrace();
		}
	}
	
	public ArrayList<String> readCachedHostingEvents()
	{
		ArrayList<String> events = new ArrayList<String>();
		try
		{
			FileInputStream inputStream = sendActivity
					.openFileInput("cachedhostingevents.txt");
			BufferedReader bufferedReader = new BufferedReader(new FileReader(
					new File(savedFilesDir, "cachedhostingevents.txt")));
			String line;
			while ((line = bufferedReader.readLine()) != null)
			{
				System.out.println("line: " + line);
				String splitevents[] = line.split("=");
				for(String s : splitevents)
				{
					events.add(s);
				}
			}
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
		return events;
	}
	
	public ArrayList<String> readCachedAttendingEvents()
	{
		ArrayList<String> events = new ArrayList<String>();
		try
		{
			FileInputStream inputStream = sendActivity
					.openFileInput("cachedattendingevents.txt");
			BufferedReader bufferedReader = new BufferedReader(new FileReader(
					new File(savedFilesDir, "cachedattendingevents.txt")));
			String line;
			while ((line = bufferedReader.readLine()) != null)
			{
				System.out.println("line: " + line);
				String splitevents[] = line.split("=");
				for(String s : splitevents)
				{
					events.add(s);
				}
			}
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
		return events;
	}
}
