package ja.aplikacjafinal;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;

public class SongsListActivity extends AppCompatActivity implements SensorEventListener {

    ListView listView;
    SensorManager manager;
    Sensor light;
    CustomAdapter adapter;
    Context context;
    File[] songs;
    TextView firstLine, secondLine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_muzyki);
        File file = new File(Environment.getExternalStorageDirectory().getPath() + "/Multimedionator");
        songs = file.listFiles();

        context = this;

        listView = findViewById(R.id.listView);
        adapter = new CustomAdapter(context, songs, Color.BLACK);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), MusicPlayerActivity.class);
                intent.putExtra("Songs", songs);
                intent.putExtra("Pos", i);
                startActivity(intent);
            }
        });

        manager = (SensorManager) getSystemService(SENSOR_SERVICE);
        assert manager != null;
        light = manager.getDefaultSensor(Sensor.TYPE_LIGHT);
        manager.registerListener(this, light, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        if (sensorEvent.values[0] < 2) {
            listView.setBackground(getDrawable(R.color.colorPrimaryDark));
            listView.setAdapter(new CustomAdapter(context, songs, Color.WHITE));

        } else {
            listView.setBackground(getDrawable(R.color.colorBackground));
            listView.setAdapter(new CustomAdapter(context, songs, Color.BLACK));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    private class CustomAdapter extends ArrayAdapter<File> {

        private Context context;
        private File[] files;
        private int color;

        CustomAdapter(Context context, File[] files, int color) {
            super(context, -1, files);
            this.context = context;
            this.files = files;
            this.color = color;

        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater != null ? inflater.inflate(R.layout.muzyka_item, parent, false) : null;

            firstLine = rowView != null ? (TextView) rowView.findViewById(R.id.firstLine) : null;
            secondLine = rowView != null ? (TextView) rowView.findViewById(R.id.secondLine) : null;

            String[] data = files[position].getName().split("-");

            assert firstLine != null;
            String[] tyt = (data[1]).split("\\.");
            Log.d("data1", data[1]);  //== Mój przyjacielu.mp3
            Log.d("tyt1", "" + tyt[0]);  //== true

            firstLine.setText(tyt[0]);
            secondLine.setText(data[0]);
            firstLine.setTextColor(color);
            secondLine.setTextColor(color);

            return rowView;
        }
    }
}


