package com.sgdm.dodgers.ui;


import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.github.capur16.digitspeedviewlib.DigitSpeedView;
import com.sgdm.dodgers.R;

import java.util.Date;




public class AutonomieFragment extends Fragment {
    private DigitSpeedView  fuelTank;
    private DigitSpeedView  speed;
    private TextView txinfos;
    private  Handler handler = new Handler();
    private TextView tx_lig1,tx_lig2,tx_lig3,tx_lig4,tx_lig5,tx_lig6,tx_lig7;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate ( R.layout.fragment_autonomie, container, false );
        fuelTank=  root.findViewById ( R.id.auto_fuel);
        speed=  root.findViewById ( R.id.auto_speed);
        txinfos=root.findViewById ( R.id.auto_infos );

        tx_lig1 =root.findViewById ( R.id.tx_1 );
        tx_lig2 =root.findViewById ( R.id.tx_2 );
        tx_lig3 =root.findViewById ( R.id.tx_3 );
        tx_lig4 =root.findViewById ( R.id.tx_4 );
        tx_lig5 =root.findViewById ( R.id.tx_5 );
        tx_lig6 =root.findViewById ( R.id.tx_6 );
        tx_lig7 =root.findViewById ( R.id.tx_7 );
        start ();
        return root;
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

    Runnable my_runnable = new Runnable() {
        @Override
        public void run() {
            final Date date = new Date();

            String lib1= dataNavigation.dataNavigation[0][7] + " L";
            tx_lig1.setText (lib1);

            lib1 = dataNavigation.dataNavigation[0][9]  + " L/Knts";
            tx_lig2.setText ( lib1);

            lib1= dataNavigation.dataNavigation[0][3]  + " L/h";
            tx_lig3.setText ( lib1);

            lib1= "";
            if (!dataNavigation.dataNavigation[0][2].isEmpty () &!dataNavigation.dataNavigation[0][1].isEmpty ()) {
                if (Integer.parseInt ( dataNavigation.dataNavigation[0][2] ) > 0)
                    lib1 = String.format ( "%2f", Integer.parseInt ( dataNavigation.dataNavigation[0][1] ) / Integer.parseInt ( dataNavigation.dataNavigation[0][2] ) + " Knts" );
            }
            tx_lig4.setText (  lib1);

            lib1= "";
            if (!dataNavigation.dataNavigation[0][3].isEmpty () &!dataNavigation.dataNavigation[0][1].isEmpty ()) {
                if (Integer.parseInt ( dataNavigation.dataNavigation[0][3] ) > 0)
                    lib1 = String.format ( "%2f", Integer.parseInt ( dataNavigation.dataNavigation[0][1] ) / Integer.parseInt ( dataNavigation.dataNavigation[0][3] ) + " Knts" );
            }            tx_lig5.setText (lib1);

            lib1=  dataNavigation.dataNavigation[0][5] + " Knts";
            tx_lig6.setText ( lib1);

            lib1=     dataNavigation.dataNavigation[0][6] + " L";
            tx_lig7.setText ( lib1);

            lib1 = date.toString () + " " + dataNavigation.getHotspotName ( getContext () );
            txinfos.setText ( lib1);

            speed.updateSpeed ( dataNavigation.setGauge ( "S" ));
            fuelTank.updateSpeed ( dataNavigation.setGauge ( "F" ));
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
}
