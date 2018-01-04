package igd.fraunhofer.pocket;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;

class Bluetooth extends Activity {

    private BluetoothSPP mBluetoothSPP;

    private MainActivity caller;
    private boolean connected = false;

    Bluetooth(MainActivity mainActivity){
        this.caller = mainActivity;

        mBluetoothSPP = new BluetoothSPP(caller.getApplicationContext());
        if (!mBluetoothSPP.isBluetoothAvailable()) {
            return;
        }
        if (!mBluetoothSPP.isBluetoothEnabled()) {
            mBluetoothSPP.enable();
        }

        while (!mBluetoothSPP.isBluetoothEnabled()) ;


        mBluetoothSPP.setupService(); // setup bluetooth service
        mBluetoothSPP.startService(BluetoothState.DEVICE_OTHER); // start bluetooth service
        mBluetoothSPP.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() {
            public void onDeviceConnected(String name, String address) {
                connected = true;
                Toast toast = Toast.makeText(caller.getApplicationContext(), "Connected", Toast.LENGTH_LONG);
                toast.show();
                caller.bluetoothConnectionSuccess();
            }
            public void onDeviceDisconnected() {
                connected = false;
                Toast toast = Toast.makeText(caller.getApplicationContext(), "Disconnected", Toast.LENGTH_LONG);
                toast.show();
            }
            public void onDeviceConnectionFailed() {
                Toast toast = Toast.makeText(caller.getApplicationContext(), "Couldn't connect to device", Toast.LENGTH_LONG);
                toast.show();
            }
        });
    }

    public void bluetoothResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if(resultCode == Activity.RESULT_OK) {
                mBluetoothSPP.connect(data);
                mBluetoothSPP.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {

                    public void onDataReceived(byte[] data, String message) {
                        Log.d("IGD", message);
                        caller.setData(data, message);
                    }
                });
            }
        } else if(requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if(resultCode == Activity.RESULT_OK) {
                mBluetoothSPP.setupService();
                mBluetoothSPP.startService(BluetoothState.DEVICE_ANDROID);
            }
        }
    }

    public void disconnect() {
        if (connected) {
            mBluetoothSPP.disconnect();
            connected = false;
        }
    }

}
