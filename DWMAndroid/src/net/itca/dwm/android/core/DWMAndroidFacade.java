package net.itca.dwm.android.core;

import java.util.ArrayList;

import android.app.Activity;
import net.itca.dwm.core.DineWithMeFacade;

public class DWMAndroidFacade extends DineWithMeFacade
{
	static DWMAndroidFacade facade;
	private boolean isConnected;

	private DWMAndroidFacade()
	{
		super();
	}

	public static DWMAndroidFacade getFacade()
	{
		if (facade == null)
			facade = new DWMAndroidFacade();

		return facade;
	}

	public void RememberUser(String username, Activity sender) // this is local
																// but should
																// work. Not
																// storing the
																// PW for
																// security
																// reasons.
	{
		CachingSystem caching = new CachingSystem(sender);
		caching.CacheUsername(username);
	}

	public String getCachedUsername(Activity sender)
	{
		return new CachingSystem(sender).getPropertyValue("username");
	}

	public boolean userCached(Activity sender)
	{
		CachingSystem caching = new CachingSystem(sender);
		return caching.userCached();
	}
	
	public void removeCachedUsername(Activity sender)
	{
		CachingSystem caching = new CachingSystem(sender);
		caching.CacheUsername("-1");
	}

	public void setConnected(boolean connected)
	{
		isConnected = connected;
	}

	public boolean getConnected()
	{
		return isConnected;
	}
	
	public void cacheHostingEvents(ArrayList<String> events, Activity sender)
	{
		CachingSystem caching = new CachingSystem(sender);
		caching.cacheHostingEvents(events);
	}
	
	public ArrayList<String> getCachedHostingEvents(Activity sender)
	{
		return new CachingSystem(sender).readCachedHostingEvents();
	}
	
	public void cacheAttendingEvents(ArrayList<String> events, Activity sender)
	{
		CachingSystem caching = new CachingSystem(sender);
		caching.cacheAttendingEvents(events);
	}
	
	public ArrayList<String> getCachedAttendingEvents(Activity sender)
	{
		return new CachingSystem(sender).readCachedAttendingEvents();
	}
	
	
	// cache friends
	
	public void cacheFriends(ArrayList<String> friends, Activity sender)
	{
		new CachingSystem(sender).cacheFriends(friends);
	}
	
	public ArrayList<String> getCachedFriends(Activity sender)
	{
		return new CachingSystem(sender).readCachedFriends();
	}
	
	public void cacheRecipes(ArrayList<String> recipes, Activity sender)
	{
		new CachingSystem(sender).cacheRecipes(recipes);
	}
	
	public ArrayList<String> getCachedRecipes(Activity sender)
	{
		return new CachingSystem(sender).readCachedRecipes();
	}
}
