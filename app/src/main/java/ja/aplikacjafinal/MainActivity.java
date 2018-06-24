package ja.aplikacjafinal;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    Button buttonMusic, buttonImages;
    ConstraintLayout layout;
    Sensor light;
    SensorManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        layout = findViewById(R.id.main_layout);

        buttonMusic = findViewById(R.id.button_muzyka);
        buttonImages = findViewById(R.id.button_obrazy);

        buttonMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), SongsListActivity.class));
            }
        });

        buttonImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ImageListActivity.class));
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
            layout.setBackground(getDrawable(R.color.colorPrimaryDark));


        } else {
            layout.setBackground(getDrawable(R.color.colorBackground));

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
