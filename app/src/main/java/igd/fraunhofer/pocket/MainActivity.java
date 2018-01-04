package igd.fraunhofer.pocket;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;

public class MainActivity extends AppCompatActivity {
    private Bluetooth bluetooth;
    private TextView debugTextView;
    private Boolean connected = false;
    private Classifier classifier;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pocket);
        this.context = super.getApplicationContext();
        classifier = new Classifier(context);
        bluetooth = new Bluetooth(this);
        debugTextView = (TextView)findViewById(R.id.valueTextView);
    }


    /**
     * function to set sensor data received form sensor.
     * @param data for completeness, don't know if useful
     * @param message sensor data
     */
    void setData(byte[] data, String message){
//        debugTextView.setText(message + " data" + Arrays.toString(data));        //for debug
        debugTextView.setText(message);
//        Position p = classifier.classifyValues(message);
        Position p = classifier.processValues(message);
        switchImage(p);
    }

    /**
     * switch image to be displayed
     */
    void switchImage(Position position){
        int resource = position.getPositionResource();
        ImageView imageView = (ImageView) findViewById(R.id.positionImageView);
        imageView.setImageResource(resource);
    }
    /**
     * callback from connection intent
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        bluetooth.bluetoothResult(requestCode,resultCode,data);
    }

    /**
     * function to be called from connect button
     */
    public void connectButtonAction(View v){
        if(connected){
            bluetooth.disconnect();
            ((Button) findViewById(R.id.connectButton)).setText(R.string.connect);
            connected = false;
        } else {
            //todo put function into bluetooth class
            Intent intent = new Intent(this.getApplicationContext(), DeviceList.class);
            startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
        }
    }
    /**
     * bluetooth connection successful. switch button functionality to disconnect
     */
    void bluetoothConnectionSuccess(){
        connected = true;
        ((Button) findViewById(R.id.connectButton)).setText(R.string.disconnect);
    }
}
