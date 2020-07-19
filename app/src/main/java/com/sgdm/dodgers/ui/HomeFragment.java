package com.sgdm.dodgers.ui;

//import android.app.Fragment;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;

import com.github.capur16.digitspeedviewlib.DigitSpeedView;
import com.sgdm.dodgers.R;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.sgdm.dodgers.ui.dataNavigation.getHotspotName;


public class HomeFragment extends Fragment {
    private DigitSpeedView  fuelTank,speed;
    private TextView home_cap,  home_infos,   home_position_latitude, home_position_longitude ,    home_depth, home_temp_water, home_toWP, home_toDestination;
    private Handler handler = new Handler();
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate ( R.layout.fragment_home, container, false );
        fuelTank=  root.findViewById ( R.id.home_fuel);
        speed=  root.findViewById ( R.id.home_Speed);
        home_cap=  root.findViewById ( R.id.home_cap);
        home_depth =  root.findViewById ( R.id.home_depth);
        home_position_latitude=  root.findViewById ( R.id.home_pos_latitude);
        home_position_longitude=  root.findViewById ( R.id.home_pos_longitude);
        home_temp_water=  root.findViewById ( R.id.home_Temp_Water);
        home_infos= root.findViewById ( R.id.home_infos );
        home_toWP= root.findViewById ( R.id.toWP );
        home_toDestination = root.findViewById ( R.id.toDestination );

        MainActivity.bottomNavigationView2.setVisibility ( View.VISIBLE );
        start ();
        return root;
    }

    Runnable my_runnable = new Runnable() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void run() {
            final Date date = new Date();
            String lib1 = date.toString () + " " + getHotspotName ( getContext () );
            home_infos.setText (lib1 );
            speed.updateSpeed ( dataNavigation.setGauge ("S") );
            home_cap.setText ( String.format ( "%s °", dataNavigation.dataNavigation[0][23] ) );
            home_position_latitude.setText ( String.format ( "%s °", dataNavigation.dataNavigation[0][21] ) );
            home_position_longitude.setText ( String.format ( "%s °", dataNavigation.dataNavigation[0][22] ) );
            home_temp_water.setText ( String.format ( "%s °C", dataNavigation.dataNavigation[0][25] ) );
            home_depth.setText ( String.format ( "%s M", dataNavigation.dataNavigation[0][24] ) );
            home_toWP.setText ( getSentence(26));
            home_toDestination.setText ( getSentence(27) );

            fuelTank.updateSpeed ( dataNavigation.setGauge ("F") );
            handler.postDelayed(my_runnable, dataNavigation.delay_interval);
            
        }
    };

    public Handler mHandler = new Handler(); // use 'new Handler(Looper.getMainLooper());' if you want this handler to control something in the UI
    // to start the handler
    public void start() {
        handler.postDelayed(my_runnable, dataNavigation.delay_demarrage);
    }

    // to stop the handler
    public void stop() {
        handler.removeCallbacks(my_runnable);
    }

    // to reset the handler
    public void restart() {
        handler.removeCallbacks(my_runnable);
        handler.postDelayed(my_runnable, dataNavigation.delay_demarrage);
    }

    @Override
    public void onPause() {

        super.onPause();
        stop ();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        restart ();
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    String  getSentence (Integer indice ) {
        if (!dataNavigation.dataNavigation[0][indice].isEmpty () && dataNavigation.setGauge ( "S" ) > 0 )
        {
            Calendar c = Calendar.getInstance();
            int speed = dataNavigation.setGauge ( "S" );
            int toWP = Integer.parseInt (dataNavigation.dataNavigation[0][indice]);
            int toAddH =  toWP /speed;
            int toAdd = (toWP % speed) + (60 * toAddH);
            c.add ( Calendar.MINUTE,toAdd );
            SimpleDateFormat format = new SimpleDateFormat("hh:mm");
            //System.out.println(format.format(c));
            String reponse = c.get(Calendar.HOUR_OF_DAY)+ ":"+ c.get(Calendar.MINUTE) + " - " +  dataNavigation.dataNavigation[0][indice] + "nm";
            return reponse;
        }
        return "";
    }
}


