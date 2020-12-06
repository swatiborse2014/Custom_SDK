package com.example.custom_sdk.client;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.custom_sdk.IMyAidlInterface;
import com.example.custom_sdk.R;
import com.example.custom_sdk.RemoteService;


public class MainActivity extends AppCompatActivity {

    protected IMyAidlInterface aidlInterface = null;
    private TextView resultView;
    private SensorManager sensorManager;
    private Sensor rotationVectorSensor;
    private SensorEventListener rvListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        resultView = (TextView) findViewById(R.id.txt_data);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        rvListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                float[] rotationMatrix = new float[16];
                SensorManager.getRotationMatrixFromVector(rotationMatrix, sensorEvent.values);

                float[] remappedRotationMatrix = new float[16];
                SensorManager.remapCoordinateSystem(rotationMatrix,
                        SensorManager.AXIS_X,
                        SensorManager.AXIS_Z,
                        remappedRotationMatrix);

                float[] orientations = new float[3];
                SensorManager.getOrientation(remappedRotationMatrix, orientations);

                for (int i = 0; i < 3; i++) {
                    orientations[i] = (float) (Math.toDegrees(orientations[i]));
                    resultView.setText("DATA : " + orientations[i]);
                }

                if (orientations[2] > 45) {
                    getWindow().getDecorView().setBackgroundColor(Color.YELLOW);
                } else if (orientations[2] < -45) {
                    getWindow().getDecorView().setBackgroundColor(Color.BLUE);
                } else if (Math.abs(orientations[2]) < 10) {
                    getWindow().getDecorView().setBackgroundColor(Color.CYAN);
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (aidlInterface == null) {
            Intent it = new Intent(MainActivity.this, RemoteService.class);
            bindService(it, connection, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(rvListener, rotationVectorSensor, 80000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(rvListener);
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            aidlInterface = IMyAidlInterface.Stub.asInterface(service);
            Toast.makeText(getApplicationContext(), "Service Connected", Toast.LENGTH_SHORT).show();
            int orientation = getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                Toast.makeText(MainActivity.this, "Portrait mode", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "landscape mode", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            aidlInterface = null;
            Toast.makeText(getApplicationContext(), "Service Disconnected", Toast.LENGTH_SHORT).show();
        }
    };
}