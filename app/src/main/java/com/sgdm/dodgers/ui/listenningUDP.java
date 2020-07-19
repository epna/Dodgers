package com.sgdm.dodgers.ui;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;



public class listenningUDP extends IntentService {
    private Boolean shouldRestartSocketListen = true;
    private static interfaceUDP mInterfaceUDP;

    public listenningUDP() {
        super ( "HelloIntentService" );
    }
    public static void setOnStopTrackEventListener(interfaceUDP eventListener) {
        mInterfaceUDP = eventListener;
    }
    public void sendMessage(String payload) {
        if (mInterfaceUDP != null) {
            mInterfaceUDP.onUDPreceive ( payload );
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        String action = intent.getStringExtra("action");
        if (action.equals (  "start")) startListenForUDPBroadcast ();
        if (action.equals (  "stop"))  stopListen ();

    }

    private void receiveUDP() {
        byte[] lMsg = new byte[15000];

        DatagramPacket dp = new DatagramPacket ( lMsg, lMsg.length );
        DatagramSocket ds = null;
        try {
            ds = new DatagramSocket ( 11111 );
            ds.setSoTimeout ( 5000 );
            ds.receive ( dp );
            String lText = new String ( dp.getData () );
            String[] DataBoat = lText.split ( ";" );
            for (int i = 0; i < DataBoat.length - 1; i++) {
                String[] Item = DataBoat[i].split ( "," );
                /*
                Item 0 : moteur Babord ou tribord
                Item 1 : rubrique
                Item2 : Valeur
                 */

                dataNavigation.dataNavigation[Integer.valueOf ( Item[0] )][Integer.valueOf ( Item[1] )] = Item[2];
            }
            sendMessage (""  );
        } catch (SocketTimeoutException e) {
                // timeout exception.

                ds.close();
        } catch (SocketException e) {
            e.printStackTrace ();
        } catch (IOException e) {
            e.printStackTrace ();
        }
        if (ds != null) ds.close ();
    }

    Thread UDPBroadcastThread;

    void startListenForUDPBroadcast() {
        UDPBroadcastThread = new Thread ( new Runnable () {
            public void run() {


                while (shouldRestartSocketListen) {
                    receiveUDP ();
                }
            }
        } );
        UDPBroadcastThread.start ();
        Log.i ( "Mytag", " exit" );
    }

    public void stopListen() {
        shouldRestartSocketListen = false;
        //socket.close ();
    }
}




