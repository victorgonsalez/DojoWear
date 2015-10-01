package watch.watchtemper;

import android.util.Log;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Service que captura mensagens e objetos enviados por outro dispositivo conectado a ele
 */
public class ServiceListener extends WearableListenerService {
    private static final String TAG = ServiceListener.class.toString();
    private static final String DATA_MAP_KEY = "DATA_MAP_KEY";
    private static final String SENSOR_KEY = "SENSOR_KEY";

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        super.onDataChanged(dataEvents);
        Log.d(TAG, "Data recebido");
        for (DataEvent item : dataEvents) {
            DataItem dataItem = item.getDataItem();
            DataMap dataMap = DataMapItem.fromDataItem(dataItem).getDataMap();
            DataMap retorno = dataMap.getDataMap(DATA_MAP_KEY);
            if (retorno != null) {
                float number = retorno.getFloat(SENSOR_KEY);
                Log.d(TAG, "Valor do sensor recebido: " + number);
            }
        }
    }
}
