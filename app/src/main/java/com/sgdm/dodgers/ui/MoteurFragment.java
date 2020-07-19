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

public class MoteurFragment extends Fragment {

    private DigitSpeedView fuelTank;
    private DigitSpeedView  speed;
    private TextView BRateFuelValue;
    private TextView TRateFuelValue;
    private TextView BVoltValue;
    private TextView TVoltValue;
    private TextView BRPMValue;
    private TextView TRPMValue;
    private TextView BCoolantValue;
    private TextView TCoolantValue;
    private TextView BHoursValue;
    private TextView THoursValue;
    private TextView txinfos;
    private  Handler handler = new Handler();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate ( R.layout.fragment_moteur, container, false );
        fuelTank= root.findViewById ( R.id.Fuelboat);
        speed=  root.findViewById ( R.id.Speedboat);
        BRateFuelValue = root.findViewById ( R.id.ConsoBabord);
        TRateFuelValue = root.findViewById ( R.id.ConsoTribord);
        BVoltValue= root.findViewById ( R.id.VoltageBabord);
        TVoltValue= root.findViewById ( R.id.VoltageTribord );
        BRPMValue= root.findViewById ( R.id.RPMBabord);
        TRPMValue= root.findViewById ( R.id.RPMTribord);
        BCoolantValue= root.findViewById ( R.id.CoolantBabord);
        TCoolantValue= root.findViewById ( R.id.CoolantTribord );
        BHoursValue= root.findViewById ( R.id.HourdBabord);
        THoursValue= root.findViewById ( R.id.Hourstribord);
        txinfos= root.findViewById ( R.id.SSIDMAJ );
        start ();
        return root;
    }
    Runnable my_runnable = new Runnable() {
        @Override
        public void run() {
            final Date date = new Date();
            txinfos.setText ( date.toString () +   " " + dataNavigation.getHotspotName ( getContext () ));
            speed.updateSpeed (dataNavigation.setGauge ( "S" ));
            fuelTank.updateSpeed (dataNavigation.setGauge ( "F" ));

            // Babord
            TVoltValue.setText ( String.valueOf ( dataNavigation.dataNavigation[1][6]) +  "V");
            TRPMValue.setText ( dataNavigation.dataNavigation[1][0]) ;
            TCoolantValue.setText ( dataNavigation.dataNavigation[1][2]+ "°C" );
            THoursValue.setText (  dataNavigation.dataNavigation[1][4]);
            TRateFuelValue.setText ( dataNavigation.dataNavigation[1][3]+ " L/H" );

            // Tribord
            BVoltValue.setText ( dataNavigation.dataNavigation[0][6] +"V");
            BRPMValue.setText (  dataNavigation.dataNavigation[0][0]);
            BCoolantValue.setText (  dataNavigation.dataNavigation[0][2] +"°C" );
            BHoursValue.setText (  dataNavigation.dataNavigation[0][4] );
            BRateFuelValue.setText (  dataNavigation.dataNavigation[0][3]+ " L/H");
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

}
