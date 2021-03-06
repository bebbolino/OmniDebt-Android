package org.omnidebt.client.view.main;

import java.util.ArrayList;
import java.util.List;

import org.omnidebt.client.R;
import org.omnidebt.client.controller.UserController;
import org.omnidebt.client.view.main.about.AboutFragment;
import org.omnidebt.client.view.main.contact.AddContactFragment;
import org.omnidebt.client.view.main.contact.ContactFragment;
import org.omnidebt.client.view.main.dashboard.AddDebtFragment;
import org.omnidebt.client.view.main.dashboard.DashboardFragment;
import org.omnidebt.client.view.main.dashboard.DebtPayFragment;
import org.omnidebt.client.view.main.history.HistoryFragment;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainODActivity extends FragmentActivity {
	
	private enum			EFragments {
		// Top level Fragments
		Dashboard,
		Contact,
		History,
		About,

		// Other
		NonTopLevel,

		AddContact,
		AddDebt,
		ContactInfos,
		SelectContact,
		PayDebt
	};

	private DrawerLayout			dlMainLayout		= null;
	private String[]				sDrawerContent		= null;
	private String[]				sNonTopLevelTitles	= null;
	private ListView				lvDrawerContainer	= null;
	private ActionBarDrawerToggle	abActionBar			= null;
	private Integer					iPosition			= null;
	private List<Integer>			lPreviousFragments	= null;
	public Fragment 				currentFragment		= null;
	private String					sAddDebtName		= "";
	private SharedPreferences		preferences			= null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_od);

		preferences = getSharedPreferences("user", 0);

		dlMainLayout		= (DrawerLayout)	findViewById(R.id.drawer_layout);
		lvDrawerContainer	= (ListView)		findViewById(R.id.drawer_container);
		sDrawerContent		= (String[])		getResources().getStringArray(R.array.drawer_content);
		sNonTopLevelTitles	= (String[])		getResources().getStringArray(R.array.non_top_level_fragments);
		if(savedInstanceState != null)
		{
			iPosition			= savedInstanceState.getInt("position");
			lPreviousFragments	= savedInstanceState.getIntegerArrayList("previous");
			sAddDebtName		= savedInstanceState.getString("addDebtName");
		}
		else
		{
			iPosition			= EFragments.Dashboard.ordinal();
			lPreviousFragments	= new ArrayList<Integer>();
		}

		abActionBar			= new ODActionBarDrawerToggle(this, dlMainLayout, R.drawable.ic_drawer,
															R.string.drawer_open, R.string.drawer_close);

		// Set the adapter for the list view
		lvDrawerContainer.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, sDrawerContent));

		// Set the list's click listener
		lvDrawerContainer.setOnItemClickListener(new ODDrawerItemClickListener());		

		dlMainLayout.setDrawerListener(abActionBar);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		// Setup the selected fragment
		if(savedInstanceState != null)
			changeFragment(iPosition, savedInstanceState.getBundle("fragment"));
		else
			changeFragment(iPosition);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putInt("position", iPosition);
		outState.putIntegerArrayList("previous", (ArrayList<Integer>) lPreviousFragments);
		Bundle state = new Bundle();
		( (Fragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container) ).onSaveInstanceState(state);
		outState.putBundle("fragment", state);
		outState.putString("addDebtName", sAddDebtName);
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		abActionBar.syncState();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (iPosition.equals(EFragments.Dashboard.ordinal()) ||
			iPosition.equals(EFragments.ContactInfos.ordinal()) )
		{
			getMenuInflater().inflate(R.menu.dashboard, menu);
		}
		else if (iPosition.equals(EFragments.Contact.ordinal()) ||
				iPosition.equals(EFragments.SelectContact.ordinal()))
		{
			getMenuInflater().inflate(R.menu.contact, menu);
		}
		else if (iPosition.equals(EFragments.History.ordinal()))
		{
			getMenuInflater().inflate(R.menu.history, menu);
		}
		else if (iPosition.equals(EFragments.About.ordinal()))
		{
			getMenuInflater().inflate(R.menu.about, menu);
		}
		return true;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		abActionBar.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// If the event on the drawer toggle button
		if (abActionBar.onOptionsItemSelected(item)) {
			return true;
		}

		// Handle presses on the action bar items
		if(iPosition.equals(EFragments.Contact.ordinal()) ||
			iPosition.equals(EFragments.SelectContact.ordinal()))
    	{
			ContactFragment fragment = (ContactFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);

    		if(item.getItemId() == R.id.add_contact)
    		{
    			fragment.onAddContact();
    		}
    	}
		if(iPosition.equals(EFragments.Dashboard.ordinal()) ||
			iPosition.equals(EFragments.ContactInfos.ordinal()) )
    	{
			DashboardFragment fragment = (DashboardFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);

    		if(item.getItemId() == R.id.add_debt)
    		{
    			fragment.onAddDebt();
    		}
    	}
		

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed()
	{
		Integer length = lPreviousFragments.size();

		if(length.equals(0))
		{
			SharedPreferences.Editor editor = preferences.edit();
			editor.clear();
			editor.commit();

			super.onBackPressed();
		}
		else
		{
			goToPreviousFragment();
		}
	}
	
	private class ODDrawerItemClickListener implements ListView.OnItemClickListener {
		
		@Override
		public void onItemClick(AdapterView parent, View view, int position, long id) {
			changeFragment(position);
		}
	}
	
	private class ODActionBarDrawerToggle extends ActionBarDrawerToggle {
		
		public ODActionBarDrawerToggle(Activity activity,
				DrawerLayout drawerLayout, int drawerImageRes,
				int openDrawerContentDescRes, int closeDrawerContentDescRes) {
			super(activity, drawerLayout, drawerImageRes, openDrawerContentDescRes,
					closeDrawerContentDescRes);
		}

		@Override
		public void onDrawerOpened(View drawerView) {
			getActionBar().setTitle(R.string.app_name);
			invalidateOptionsMenu();
		}

		@Override
		public void onDrawerClosed(View view) {
			updateActionBarTitle();
			invalidateOptionsMenu();
		}
		
	};

	private void changeFragment(Integer position) {
		Bundle arg = new Bundle();
		changeFragment(position, arg);
	}
	
	private void changeFragment(Integer position, Bundle args) {

		// Create a new fragment and specify args
		Fragment	fragment		= null;
		boolean		isSon			= false;
		boolean		isGoingDeeper	= false;
		Integer		iTopLevelParent	= iPosition;

		FragmentManager fragmentManager			= getSupportFragmentManager();
		FragmentTransaction fragmentTransaction	= fragmentManager.beginTransaction();

		if( position.equals(EFragments.Dashboard.ordinal()) )
		{
			fragment = new DashboardFragment();
			args.putBoolean("User", false);
			currentFragment=fragment;
		}
		else if( position.equals(EFragments.Contact.ordinal()) )
		{
			fragment = new ContactFragment();
		}
		else if( position.equals(EFragments.History.ordinal()) )
		{
			fragment = new HistoryFragment();
		}
		else if( position.equals(EFragments.About.ordinal()) )
		{
			fragment = new AboutFragment();
		}
		else if( position.equals(EFragments.AddContact.ordinal()))
		{
			fragment = new AddContactFragment();
			if(!iPosition.equals(position))
				isGoingDeeper = true;
		}
		else if(position.equals(EFragments.AddDebt.ordinal()))
		{
			fragment = new AddDebtFragment();
			if(!iPosition.equals(position))
				isGoingDeeper = true;
		}
		else if( position.equals(EFragments.ContactInfos.ordinal()))
		{
			fragment = new DashboardFragment();
			args.putBoolean("User", true);
			if(iPosition.equals(EFragments.Contact.ordinal()))
				isGoingDeeper = true;
		}
		else if( position.equals(EFragments.SelectContact.ordinal()))
		{
			fragment = new ContactFragment();
			args.putBoolean("isSelectContact", true);
			if(iPosition.equals(EFragments.Dashboard.ordinal()))
				isGoingDeeper = true;
		}
		else if(position.equals(EFragments.PayDebt.ordinal()))
		{
			fragment=new DebtPayFragment();
			if(iPosition.equals(EFragments.Dashboard.ordinal()) ||
				iPosition.equals(EFragments.ContactInfos.ordinal()) ||
				iPosition.equals(EFragments.History.ordinal()))
			{
				isGoingDeeper=true;
			}
			
		}

		if(position.compareTo(EFragments.NonTopLevel.ordinal()) < 0)
		{
			int i = lPreviousFragments.size() - 1;
			while(i > -1 && lPreviousFragments.get(i).compareTo(EFragments.NonTopLevel.ordinal()) > 0)
			{
				i--;
			}

			if(i > -1)
			{
				iTopLevelParent = lPreviousFragments.get(i);
				if(iTopLevelParent == position)
					isSon = true;
			}


			lPreviousFragments.clear();
			if(isSon && !position.equals(iPosition))
			{
				fragmentTransaction.setCustomAnimations(R.anim.left_in, R.anim.right_out);
			}
			else
			{
				if(position.compareTo(iTopLevelParent) < 0)
				{
					fragmentTransaction.setCustomAnimations(R.anim.top_in, R.anim.bottom_out);
				}
				else if(position.compareTo(iTopLevelParent) > 0)
				{
					fragmentTransaction.setCustomAnimations(R.anim.bottom_in, R.anim.top_out);
				}
			}
		}
		else
		{
			if(isGoingDeeper)
			{
				lPreviousFragments.add(iPosition);
				fragmentTransaction.setCustomAnimations(R.anim.right_in, R.anim.left_out);
			}
			else if(!iPosition.equals(position))
			{
				fragmentTransaction.setCustomAnimations(R.anim.left_in, R.anim.right_out);
			}
		}

		iPosition = position;

		fragment.setArguments(args);

		// Insert the fragment by replacing any existing fragment
		fragmentTransaction.replace(R.id.fragment_container, fragment)
							.commit();

		// Highlight the selected item, update the title, and close the drawer
		lvDrawerContainer.setItemChecked(iPosition, true);
		dlMainLayout.closeDrawer(lvDrawerContainer);
		
		invalidateOptionsMenu();
		updateActionBarTitle();
	}

	public void updateActionBarTitle()
	{
		if(iPosition.compareTo(EFragments.NonTopLevel.ordinal()) > 0)
			getActionBar().setTitle(sNonTopLevelTitles[iPosition - EFragments.NonTopLevel.ordinal() - 1]);
		else
			getActionBar().setTitle(sDrawerContent[iPosition]);
	}
	
	public void goToAddContact() {
		changeFragment(EFragments.AddContact.ordinal());
	}
	
	public void goToAddDebt(String name) {
		sAddDebtName = name;
		if(name.equals(UserController.getName()))
		{
			changeFragment(EFragments.SelectContact.ordinal());
		}
		else
		{
			Bundle arg = new Bundle();
			arg.putString("User", name);
			changeFragment(EFragments.AddDebt.ordinal(), arg);
		}
	}
	
	public void goToPayDebt(String name, double debt)
	{
		Log.d("debt", "name is " + name + " debt is " + debt);
		Bundle arg=new Bundle();
		arg.putString("User", name);
		arg.putString("Debt", ""+debt);
		
		changeFragment(EFragments.PayDebt.ordinal(), arg);
	}

	public void goToContactInfos(String name) {
		sAddDebtName = name;
		Bundle arg = new Bundle();
		arg.putString("User", name);
		changeFragment(EFragments.ContactInfos.ordinal(), arg);
	}

	public void goToPreviousFragment() {
		if(!lPreviousFragments.isEmpty())
		{
			int pos = lPreviousFragments.size() - 1;
			changeFragment(lPreviousFragments.get(pos));
			if(!lPreviousFragments.isEmpty())
				lPreviousFragments.remove(pos);
		}
	}

	public void multipleSelection(Integer number) {
		if(number.equals(0))
			updateActionBarTitle();
		else
			getActionBar().setTitle(number.toString());
	}

	public String getAddDebtName()
	{
		return sAddDebtName;
	}

	public SharedPreferences getPreferences()
	{
		return preferences;
	}

}
