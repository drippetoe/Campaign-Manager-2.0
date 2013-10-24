package com.mobilebits.samy;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.SherlockListFragment;
import com.mobilebits.samy.fragments.CompanyListFragment;
import com.mobilebits.samy.fragments.OfferFragment;
import com.mobilebits.samy.fragments.ShareFragment;

public class HomeActivity extends SherlockFragmentActivity {
    
    public static int THEME = R.style.Theme_Sherlock_Light_DarkActionBar;
    private ActionBar actionBar;
    private ActionBar.Tab listTab;
    private ActionBar.Tab offerTab;
    private ActionBar.Tab shareTab;
    private SherlockListFragment listFragment;
    private SherlockFragment offerFragment;
    private SherlockFragment shareFragment;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(THEME); //Used for theme switching in samples
        super.onCreate(savedInstanceState);
       
        actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        setContentView(R.layout.test_layout);
        listTab = actionBar.newTab().setText("List").setTag("list");
        offerTab = actionBar.newTab().setText("Offer").setTag("offer");
        shareTab = actionBar.newTab().setText("Share").setTag("share");
        
        listFragment = new CompanyListFragment(); 
        offerFragment = new OfferFragment();
        shareFragment = new ShareFragment();
        
        listTab.setTabListener(new NavigationListener(listFragment));
        offerTab.setTabListener(new NavigationListener(offerFragment));
        shareTab.setTabListener(new NavigationListener(shareFragment));
        
        actionBar.addTab(listTab,false);
        actionBar.addTab(offerTab, false);
        actionBar.addTab(shareTab, false);
        actionBar.selectTab(offerTab);
        
    }
    
    
private class NavigationListener implements ActionBar.TabListener {
        
        private Fragment fragment;
        int id;
        
        public NavigationListener(Fragment fragment) {
            this.fragment = fragment;
            this.id = R.id.fragment_container;
        }

        @Override
        public void onTabSelected(Tab tab, FragmentTransaction ft) {
                ft.replace(id, fragment);
        }

        @Override
        public void onTabUnselected(Tab tab, FragmentTransaction ft) {
            ft.remove(fragment);
            
        }

        @Override
        public void onTabReselected(Tab tab, FragmentTransaction ft) {          
        }
    }

}
