package com.sgdm.dodgers.ui;

import android.content.Context;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public  class dataNavigation {
    public static Integer delay_interval = 2000;
    public static Integer delay_demarrage = 500;


    public static String[][] dataNavigation = new String[2][30];

    public dataNavigation() {
        super ();
    }

    public static void setDataNavigation(Integer II, Integer JJ, String value) {
        dataNavigation[II][JJ] = value;

    }

    public static String getDataNavigation(Integer II, Integer JJ) {
        return dataNavigation[II][JJ];
    }

    public static String getHotspotName(Context context) {
        try {

            WifiManager wifiManager = (WifiManager) context.getApplicationContext ().getSystemService ( Context.WIFI_SERVICE );
            ;
            WifiInfo wifiInfo;

            wifiInfo = wifiManager.getConnectionInfo ();
            if (wifiInfo.getSupplicantState () == SupplicantState.COMPLETED) {
                return wifiInfo.getSSID ();
            }
            return "";
        } catch (Exception e) {
            e.printStackTrace ();
            return "";
        }
    }

    private void sendMessage(final String message) {
        DatagramSocket ds = null;
        try {
            ds = new DatagramSocket ();
            InetAddress serverAddr = InetAddress.getByName ( "30.30.30.30" );
            DatagramPacket dp;
            dp = new DatagramPacket ( message.getBytes (), message.length (), serverAddr, 9001 );
            ds.send ( dp );
            byte[] lMsg = new byte[100];
            dp = new DatagramPacket ( lMsg, lMsg.length );
        } catch (IOException e) {
            e.printStackTrace ();
            if (ds != null) {
                ds.close ();
            }
        } finally {
            if (ds != null) {
                ds.close ();
            }
        }
    }
    public static  Integer  setGauge(String mType )
    {
        Integer mIndicateur=20;
        if (mType.equals("F"))  mIndicateur=7;
        if (dataNavigation[0][mIndicateur].isEmpty ()) return   0;
        String mValue;
        if (dataNavigation[0][mIndicateur].indexOf ( "." )>0 ) {
            mValue = dataNavigation[0][mIndicateur].substring ( 0, dataNavigation[0][mIndicateur].indexOf ( "." ) );
        }
        else
        {
            mValue=dataNavigation[0][mIndicateur];
        }

        return Integer.parseInt ( mValue ) ;
        }
public static Integer checkValue(String param)
{
    if (param.isEmpty ())
        return 0 ;
        else
            return Integer.parseInt ( param );

}


}
