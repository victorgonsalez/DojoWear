package watch.watchtemper;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.app.NotificationCompat.WearableExtender;
import android.support.v4.app.NotificationManagerCompat;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Random;

public class MainActivity extends Activity implements SensorEventListener {
    private SensorManager mSensorManager;
    private Sensor mTemper;
    private EditText mEditMaps;
    private EditText mEditTitle;
    private EditText mEditMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEditMaps = (EditText) findViewById(R.id.edit_maps);
        mEditTitle = (EditText) findViewById(R.id.edit_title);
        mEditMessage = (EditText) findViewById(R.id.edit_message);
        Button btnNotification = (Button) findViewById(R.id.btn_send_notification);
        btnNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendNotification();
            }
        });

        //SensorManager possui acesso direto aos sensores disponíveis do device
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        //Sensor de ambiente padrão de celulares
        mTemper = mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
    }

    @Override
    public void onResume() {
        super.onResume();
        //Registro de listener para o sensor de temperatura, se a temperatura mudar, podemos escutar
        //sua modificação.
        mSensorManager.registerListener(this, mTemper, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    /**
     * Envio de notificação padrão para device e relógio
     */
    private void sendNotification() {

        Intent mapIntent = new Intent(Intent.ACTION_VIEW);
        Uri geoUri = Uri.parse("geo:0,0?q=" + Uri.encode(mEditMaps.getText().toString()));
        mapIntent.setData(geoUri);
        PendingIntent mapPendingIntent = PendingIntent.getActivity(this, 0, mapIntent, 0);

        Uri webPage = Uri.parse("http://www.youtube.com/" + mEditMaps.getText().toString());
        Intent webIntent = new Intent(Intent.ACTION_VIEW, webPage);
        PendingIntent videoPendingIntent = PendingIntent.getActivity(this, 0, webIntent, 0);

        //Botão de ação para abrir o GoogleMaps através de uma notificação
        NotificationCompat.Action mapApp = new NotificationCompat.Action(R.drawable.pin,
                getResources().getString(R.string.map_app), mapPendingIntent);

        //Botão de ação para abrir o Youtube através de uma notificação
        NotificationCompat.Action videoApp = new NotificationCompat.Action(R.drawable.youtube,
                getResources().getString(R.string.video_app), videoPendingIntent);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.androidbackground);

        Random random = new Random();
        int notificationId = random.nextInt(200);
        WearableExtender wearableExtender = new WearableExtender();

        Builder notificationBuilder = new Builder(MainActivity.this)
                .setSmallIcon(R.drawable.compassicon)
                .setLargeIcon(bitmap)
                .setContentTitle(mEditTitle.getText().toString())
                .setContentText(mEditMessage.getText().toString())
                .extend(wearableExtender.addAction(mapApp)
                        .addAction(videoApp));
        // wearableExtender irá adicionar um botão/ação na notificação que somente para relógio aparecerá no relógio
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MainActivity.this);
        notificationManager.notify(notificationId, notificationBuilder.build());
    }
}
