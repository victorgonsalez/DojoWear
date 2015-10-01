package watch.watchtemper;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.Wearable;

public class MainActivity extends WearableActivity implements SensorEventListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    private TextView mTextHeart, mTextLight, mTextStep;
    private SensorManager mSensorManager;
    public GoogleApiClient mGoogleApiClient;
    private static final String DATA_MAP_KEY = "DATA_MAP_KEY";
    private static final String SENSOR_KEY = "SENSOR_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextHeart = (TextView) findViewById(R.id.textHeart);
        mTextLight = (TextView) findViewById(R.id.textLight);
        mTextStep = (TextView) findViewById(R.id.textStepCounter);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();

    }

    @Override
    protected void onResume() {
        super.onResume();

        //SensorManager possui acesso direto aos sensores disponíveis do device
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        //Sensor cardíaco
        Sensor heartSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);

        //Sensor de movimento por passos
        Sensor stepSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        //Sensor de lumininação
        Sensor lightSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        //Precisamos validar se os sensor estão existem, se forem nulos, significa que ele não foi encontrado pelo SensorManager.
        if (heartSensor != null) {
            //Registrando listener para capturar mudanças de valores no sensor cardíaco
            mSensorManager.registerListener(this, heartSensor, SensorManager.SENSOR_DELAY_UI);
        }

        if (stepSensor != null) {
            //Registrando listener para capturar mudanças de valores no sensor de passos
            mSensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        if (lightSensor != null) {
            //Registrando listener para capturar mudanças de valores no sensor luz
            mSensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //Removendo todos os listener dos sensores acima
        mSensorManager.unregisterListener(this);
    }

    //Este método abaixo captura qualquer mudança de valores nos sensores que registramos os listeners.
    @Override
    public void onSensorChanged(SensorEvent event) {
        final float number;
        if (event.sensor.getType() == Sensor.TYPE_HEART_RATE) {
            number = 0;
            mTextHeart.setText(event.values[0] + "");
        } else if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            number = 0;
            mTextStep.setText(event.values[0] + "");
        } else if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            number = event.values[0];
            mTextLight.setText(event.values[0] + "");
            int valor = (int) event.values[0];
            int cor = 0;
            if ((valor > 0) && (valor < 1000)) {
                cor = Color.BLUE;
            } else if ((valor > 1001) && (valor < 5000)) {
                cor = Color.RED;
            } else if ((valor > 5001) && (valor < 10000)) {
                cor = Color.GREEN;
            }
            mTextLight.setTextColor(cor);
        } else {
            number = 0;
        }
        final Runnable r = new Runnable() {
            @Override
            public void run() {
                if (mGoogleApiClient.isConnected()) {
                    PutDataMapRequest dataMap = PutDataMapRequest.create("/data");
                    DataMap data = new DataMap();
                    data.putFloat(SENSOR_KEY, number);

                    dataMap.getDataMap().putDataMap(DATA_MAP_KEY, data);
                    Wearable.DataApi.putDataItem(mGoogleApiClient, dataMap.asPutDataRequest()).await();
                }
            }
        };
        Thread t = new Thread(r);
        t.start();

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d("onAccuracyChanged", "onAccuracyChanged: " + sensor.getName() + " : " + sensor.getPower() + " : " + accuracy);
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
