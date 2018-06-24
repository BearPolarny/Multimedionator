package ja.aplikacjafinal;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;


public class ImageListActivity extends AppCompatActivity implements SensorEventListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_obrazy);

        final File file = new File(Environment.getExternalStorageDirectory().getPath() + "/ImagesF");
        final File[] images = file.listFiles();

        GridView layout = findViewById(R.id.obrazy_layout);
        layout.setAdapter(new CustomAdapter(this, images));

        SensorManager manager = (SensorManager) getSystemService(SENSOR_SERVICE);
        assert manager != null;
        Sensor light = manager.getDefaultSensor(Sensor.TYPE_LIGHT);
        manager.registerListener(this, light, SensorManager.SENSOR_DELAY_NORMAL);

        layout.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), ImageActivity.class);
                intent.putExtra("Files", images);
                intent.putExtra("i", i);

                startActivity(intent);
            }
        });

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private class CustomAdapter extends ArrayAdapter<File> {

        private Context context;
        private File[] files;

        CustomAdapter(Context context, File[] files) {
            super(context, -1, files);
            this.context = context;
            this.files = files;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater != null ? inflater.inflate(R.layout.obraz_item, parent, false) : null;

            ImageView image = rowView != null ? (ImageView) rowView.findViewById(R.id.obraz_obraz) : null;
            TextView text = rowView != null ? (TextView) rowView.findViewById(R.id.obraz_tekst) : null;

            String title = files[position].getName();

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;
            final Bitmap b = BitmapFactory.decodeFile(files[position].getAbsolutePath(), options);

            assert image != null;
            image.setImageBitmap(b);

            text.setText(title);

            return rowView;
        }
    }
}
