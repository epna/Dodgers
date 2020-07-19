package com.sgdm.dodgers.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sgdm.dodgers.R;

public class MainActivity extends AppCompatActivity implements interfaceUDP {

    static BottomNavigationView bottomNavigationView2;
    public Handler handler = new Handler ();
    public static final String TAG = "==>";
    private Configuration newConfig;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_main );
        BottomNavigationView navView = findViewById ( R.id.nav_view );
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder (
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build ();
        NavController navController = Navigation.findNavController ( this, R.id.nav_host_fragment );
        bottomNavigationView2 = findViewById ( R.id.nav_view );
        NavigationUI.setupActionBarWithNavController ( this, navController, appBarConfiguration );
        NavigationUI.setupWithNavController ( navView, navController );
        for (Integer i = 0; i < 2; i++) {
            for (Integer j = 0; j < 30; j++) {
                dataNavigation.dataNavigation[i][j] = "";
            }
        }
        startListenningUDP ();

    }

    @Override
    public void onUDPreceive(final String result) {
/*
                String resultat = "";
                for (Integer i = 0; i < 2; i++) {
                    for (Integer j = 0; j < 30; j++) {
                        if (dataNavigation.dataNavigation[i][j]  != null) {
                            resultat += i.toString () + "  " +  j.toString () + "  " + dataNavigation.dataNavigation[i][j] + " \n"  ;
                        }
                    }
                }
*/
    }

    @Override
    protected void onResume() {
        super.onResume ();
        startListenningUDP ();
    }

    @Override
    protected void onPause() {
        Intent stopIntent = new Intent ( this, listenningUDP.class );
        stopIntent.putExtra ( "action", "stop" );
        startService ( stopIntent );
        super.onPause ();
    }

    public void startListenningUDP() {
        listenningUDP xxx = new listenningUDP ();
        listenningUDP.setOnStopTrackEventListener ( this );
        Intent startIntent = new Intent ( this, listenningUDP.class );
        startIntent.putExtra ( "action", "start" );
        startService ( startIntent );
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        this.newConfig = newConfig;

        super.onConfigurationChanged ( newConfig );
        FragmentManager fragmentManager = getSupportFragmentManager ();
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            fragmentManager
                    .beginTransaction ()
                    .addToBackStack("tag")
                    .add ( R.id.nav_host_fragment, new DashboardDisplay () )
                    .commit ();
        } else {
            //FragmentManager fm = getSupportFragmentManager ();

            if (fragmentManager.getBackStackEntryCount() > 0) {
                Log.i("MainActivity", "popping backstack");
                fragmentManager.popBackStack();
            } else {
                Log.i("MainActivity", "nothing on backstack, calling super");
                super.onBackPressed();
            }
        }
    }
}
