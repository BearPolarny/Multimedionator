package ja.aplikacjafinal;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class MusicPlayerActivity extends AppCompatActivity implements SensorEventListener {

    private int position;
    private boolean isPaused = false;
    private boolean isLocked = false;
    private boolean puszczone = false;

    private AudioManager audioManager;
    private Button play;
    private ConstraintLayout layout;
    private File[] songs = null;
    private MediaPlayer mediaPlayer;
    private Sensor acc;
    private SensorManager sensors;
    private TextView author, title, currentTime, finalTime;
    private Switch aSwitch;
    private SeekBar seekBar;
    private Handler handler;


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Toast.makeText(getApplicationContext(), "ewq", Toast.LENGTH_SHORT).show();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_odtwarzacz);
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            songs = (File[]) bundle.get("Songs");
            position = bundle.getInt("Pos");
        }

        layout = findViewById(R.id.lay_odt);

        sensors = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensors != null) {
            acc = sensors.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            Sensor light = sensors.getDefaultSensor(Sensor.TYPE_LIGHT);
            sensors.registerListener(this, acc, SensorManager.SENSOR_DELAY_NORMAL);
            sensors.registerListener(this, light, SensorManager.SENSOR_DELAY_NORMAL);
        }


        play = findViewById(R.id.start);
        Button back = findViewById(R.id.back);
        Button ff = findViewById(R.id.forward);
        Button volUp = findViewById(R.id.volUp);
        Button volDown = findViewById(R.id.volDown);
        finalTime = findViewById(R.id.final_time);
        currentTime = findViewById(R.id.current_time);

        aSwitch = findViewById(R.id.switch1);

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isLocked = !b;
            }
        });

        seekBar = findViewById(R.id.seekBar);

        ImageView image = findViewById(R.id.imageView);
        registerForContextMenu(image);

        title = findViewById(R.id.song_title);
        author = findViewById(R.id.song_author);

        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }

        mediaPlayer = new MediaPlayer();

        try {
            mediaPlayer.setDataSource(songs != null ? songs[position].getPath() : null);
            mediaPlayer.prepare();
            textUpdate(songs[position]);
            finalTime.setText(miliToTime(mediaPlayer.getDuration()));
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "cos jest nie tak: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.getStackTrace();
        }

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!isPaused) {
                    mediaPlayer.pause();
                    isPaused = true;
                    play.setBackground(getDrawable(R.drawable.play));
                } else {
                    isPaused = false;
                    mediaPlayer.start();
                    play.setBackground(getDrawable(R.drawable.pause));
                }
            }
        });

        ff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 10000);
            }
        });

        ff.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mediaPlayer.release();
                position += 1;
                if (position == songs.length) {
                    position = 0;
                }
                try {
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(songs[position].getPath());
                    mediaPlayer.prepare();
                    textUpdate(songs[position]);
                    finalTime.setText(miliToTime(mediaPlayer.getDuration()));
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(), "cos jest nie tak: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.getStackTrace();
                }
                return false;
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - 10000);
            }
        });

        back.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mediaPlayer.release();
                position -= 1;
                if (position == -1) {
                    position = songs.length - 1;
                }
                try {
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(songs[position].getPath());
                    mediaPlayer.prepare();
                    textUpdate(songs[position]);
                    finalTime.setText(miliToTime(mediaPlayer.getDuration()));
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(), "cos jest nie tak: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.getStackTrace();
                }
                return false;
            }
        });

        volUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                audioManager.adjustVolume(AudioManager.ADJUST_RAISE, 0);
            }
        });

        volDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                audioManager.adjustVolume(AudioManager.ADJUST_LOWER, 0);
            }
        });

        seekBar.setMax(mediaPlayer.getDuration());

        handler = new Handler();
        MusicPlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null && !puszczone) {
                    int mCurrentPosition = mediaPlayer.getCurrentPosition();
                    seekBar.setProgress(mCurrentPosition);
                    currentTime.setText(miliToTime(mediaPlayer.getCurrentPosition()));
                }
                handler.postDelayed(this, 1000);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    mediaPlayer.seekTo(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        if (sensorEvent.sensor.getType() == Sensor.TYPE_LIGHT) {
            if (sensorEvent.values[0] < 2) {
                layout.setBackground(getDrawable(R.color.colorPrimaryDark));
                author.setTextColor(Color.WHITE);
                title.setTextColor(Color.WHITE);
                aSwitch.setTextColor(Color.WHITE);
                currentTime.setTextColor(Color.WHITE);
                finalTime.setTextColor(Color.WHITE);

            } else {
                layout.setBackground(getDrawable(R.color.colorBackground));
                author.setTextColor(Color.BLACK);
                title.setTextColor(Color.BLACK);
                aSwitch.setTextColor(Color.BLACK);
                finalTime.setTextColor(Color.BLACK);
                currentTime.setTextColor(Color.BLACK);
            }
        } else {
            if (!isLocked && sensorEvent.values[2] < -8) {
                mediaPlayer.pause();
            } else if (!isPaused) {
                mediaPlayer.start();
            }
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }


    @Override
    protected void onResume() {
        super.onResume();
        sensors.registerListener(this, acc, SensorManager.SENSOR_DELAY_NORMAL);
    }


    @Override
    protected void onPause() {
        super.onPause();
        sensors.unregisterListener(this);
    }


    @Override
    protected void onDestroy() {
        puszczone = true;
        assert mediaPlayer != null;
        mediaPlayer.release();
        super.onDestroy();
    }

    private void textUpdate(File song) {

        String[] data = song.getName().split("-");

        title.setText(data[1]);
        author.setText(data[0]);

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater m = getMenuInflater();
        m.inflate(R.menu.context_menu, menu);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.delete:
                startActivity(new Intent(getApplicationContext(), ImageListActivity.class));
                break;
            default:
                break;
        }

        return super.onContextItemSelected(item);
    }

    public String miliToTime(int milis) {
        String time = "";

        int seconds = milis / 1000;
        int minutes = seconds / 60;
        seconds = seconds % 60;

        time += minutes;
        time += ":";
        if (seconds < 10) time += "0";
        time += seconds;

        return time;
    }

}
