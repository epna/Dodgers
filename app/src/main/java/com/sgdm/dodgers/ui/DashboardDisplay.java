package com.sgdm.dodgers.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sgdm.dodgers.R;


import java.util.Date;

import pl.pawelkleczkowski.customgauge.CustomGauge;

public class DashboardDisplay extends Fragment {

    private CustomGauge fuelTank,speed,BRateFuelValue,TRateFuelValue,BVoltValue,TVoltValue,BRPMValue,TRPMValue,BCoolantValue,TCoolantValue;
    private TextView tx_HoursB,tx_HoursT,tx_Infos, tx_tourMinuteB,tx_tourMinuteT,tx_consoInstB,tx_consoInstT, tx_batterieB, tx_batterieT, tx_tempB, tx_tempT, tx_vitesse, tx_essence;
    private Handler handler = new Handler();
    private BottomNavigationView bottomNavigationView;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate ( R.layout.fragment_tdb, container, false );
        MainActivity.bottomNavigationView2.setVisibility(View.GONE);

        fuelTank= root.findViewById ( R.id.cg_essence);
        speed=  root.findViewById ( R.id.cg_vitesse);
        BRateFuelValue = root.findViewById ( R.id.cg_consoInstB);
        TRateFuelValue = root.findViewById ( R.id.cg_consoInstT);
        BVoltValue= root.findViewById ( R.id.cg_batterieB);
        TVoltValue= root.findViewById ( R.id.cg_batterieT );
        BRPMValue= root.findViewById ( R.id.cg_toursminute_babord);
        TRPMValue= root.findViewById ( R.id.cg_toursMinuteT);
        BCoolantValue= root.findViewById ( R.id.cg_tempMoteurB);
        TCoolantValue= root.findViewById ( R.id.cg_tempMoteurT );

        tx_HoursB= root.findViewById ( R.id.tx_heuresMoteurB);
        tx_HoursT= root.findViewById ( R.id.tx_heuresMoteurT);
        tx_Infos= root.findViewById ( R.id.tx_Infos );
        tx_tourMinuteB=root.findViewById ( R.id.tx_tourMinuteB);
        tx_tourMinuteT=root.findViewById ( R.id.tx_tourMinuteT);
        tx_consoInstB=root.findViewById ( R.id.tx_cosoInstB);
        tx_consoInstT=root.findViewById ( R.id.tx_consoInstT);
        tx_batterieB=root.findViewById ( R.id.tx_batterieB );
        tx_batterieT=root.findViewById ( R.id.tx_batterieT );
        tx_tempB=root.findViewById ( R.id.tx_temperatureB );
        tx_tempT=root.findViewById ( R.id.tx_temperatureT );

        tx_vitesse=root.findViewById ( R.id.tx_speed );
        tx_essence=root.findViewById ( R.id.tx_essencevalue );

        start ();
        return root;
    }
    Runnable my_runnable = new Runnable() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void run() {
            final Date date = new Date();
            String lib = date.toString () +   " " + dataNavigation.getHotspotName ( getContext () );



            tx_Infos.setText ( lib);
            tx_vitesse.setText ( String.valueOf ( dataNavigation.setGauge ( "S" ) ));
            speed.setValue (dataNavigation.setGauge ( "S" ));
            tx_essence.setText ( String.valueOf ( dataNavigation.setGauge ( "F" ) ));
            fuelTank.setValue (dataNavigation.setGauge ( "F" ));


            // Babord
            BVoltValue.setValue (dataNavigation.checkValue (  dataNavigation.dataNavigation[0][6]) );
            BRPMValue.setValue ( dataNavigation.checkValue (dataNavigation.dataNavigation[0][0]));
            BCoolantValue.setValue ( dataNavigation.checkValue ( dataNavigation.dataNavigation[0][2]) );
            BRateFuelValue.setValue (  dataNavigation.checkValue (  dataNavigation.dataNavigation[0][3]));

            tx_tourMinuteB.setText ( String.valueOf (  dataNavigation.checkValue (dataNavigation.dataNavigation[0][0])) );
            tx_consoInstB.setText ( String.valueOf (  dataNavigation.checkValue (  dataNavigation.dataNavigation[0][3])) );
            tx_HoursB.setText ( String.valueOf ( dataNavigation.dataNavigation[0][4]) );
            tx_batterieB.setText ( String.valueOf (  dataNavigation.checkValue (  dataNavigation.dataNavigation[0][6])) );
            tx_tempB.setText ( String.valueOf (  dataNavigation.checkValue ( dataNavigation.dataNavigation[0][2])  ));


            // Tribord
            TVoltValue.setValue ( dataNavigation.checkValue (  dataNavigation.dataNavigation[1][6]) );
            TRPMValue.setValue ( dataNavigation.checkValue ( String.valueOf ( dataNavigation.dataNavigation[1][0]) ));
            TCoolantValue.setValue (dataNavigation.checkValue (dataNavigation.dataNavigation[1][2]) );
            TRateFuelValue.setValue ( dataNavigation.checkValue ( dataNavigation.dataNavigation[1][3]) );

            tx_tourMinuteT.setText ( String.valueOf (  dataNavigation.checkValue (dataNavigation.dataNavigation[1][0]) ));
            tx_consoInstT.setText ( String.valueOf (  dataNavigation.checkValue (  dataNavigation.dataNavigation[1][3])) );
            tx_HoursT.setText ( String.valueOf ( dataNavigation.dataNavigation[1][4]) );
            tx_batterieT.setText ( String.valueOf (  dataNavigation.checkValue (  dataNavigation.dataNavigation[1][6])) );
            tx_tempT.setText ( String.valueOf (  dataNavigation.checkValue ( dataNavigation.dataNavigation[1][2])  ));
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
        MainActivity.bottomNavigationView2.setVisibility ( View.VISIBLE );
    }

    @Override
    public void onResume()
    {
        super.onResume();
        restart ();
    }



}
