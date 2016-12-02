package com.mti.meetme.Tools.Network;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.View;

import com.google.common.util.concurrent.ExecutionError;
import com.ibm.watson.developer_cloud.dialog.v1.model.DialogContent;

/**
 * Created by Alex on 30/11/2016.
 */

public class DialogNotConnected {
    private AlertDialog _alertDialog;
    private Activity _acti;
    private  Thread _daemonThread;

    public DialogNotConnected(final Activity acti) {
        _acti = acti;
        init();
    }

    private void init()
    {
        _alertDialog = new AlertDialog.Builder(_acti).create();
        _alertDialog.setTitle("Connexion Lost");
        _alertDialog.setMessage("Please connect to a network to continue");
        _alertDialog.setCanceledOnTouchOutside(false);
        _alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        _daemonThread.interrupt();
                        interuptNoConection();
                    }
                });



        _alertDialog.setOnKeyListener(new AlertDialog.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode,
                                 KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog.cancel();
                    _daemonThread.interrupt();
                    interuptNoConection();
                }
                return true;
            }
        });


    }

    public void interuptNoConection()
    {
        if (_alertDialog == null || _acti == null)
            return; //error should init before

        _daemonThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (Network.isConnectedToInternet(_acti))
                        continue;

                   // init();
                    _acti.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                _alertDialog.show();
                            }
                            catch (Exception e) {}
                        }
                    });
                } catch (Exception e) {
                }
            }
        }, "Demon");

        _daemonThread.setDaemon(true);
        _daemonThread.start();
    }

    public void stopInteruptNoConection() {
        _alertDialog.cancel();
        _alertDialog.dismiss();
        _daemonThread.interrupt();
    }

    public void retartInteruptNoConection()
    {
        _alertDialog.cancel();
        interuptNoConection();
     //   if (_daemonThread.isInterrupted())
        //    init();
    }
}
