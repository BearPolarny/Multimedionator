package ja.aplikacjafinal;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;

public class ImageActivity extends AppCompatActivity implements SensorEventListener {

    boolean isLocked = false;
    long lastTime = 0;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        File[] files = (File[]) bundle.get("Files");
        int position = bundle.getInt("i");

        SensorManager manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        assert manager != null;
        Sensor sensor = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        manager.registerListener(this, sensor, 1000000);

        setContentView(R.layout.activity_obrazek);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), files, position);

        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.setCurrentItem(position);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_obraz, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        CoordinatorLayout layout = findViewById(R.id.main_content);
        if (layout != null) {
            switch (id) {
                case R.id.action_dark:
                    layout.setBackground(getDrawable(R.color.colorPrimaryDark));
                    break;
                case R.id.action_light:
                    layout.setBackground(getDrawable(R.color.colorBackground));
                    break;
                case R.id.Tilt_option:
                    if (item.isChecked()) {
                        item.setChecked(false);
                        isLocked = true;
                    } else {
                        item.setChecked(true);
                        isLocked = false;
                    }
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        long time = System.currentTimeMillis();
        if (!isLocked && sensorEvent.values[0] > 5 && time - lastTime > 1250) {
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
            Log.d("qwe", "10 " + (time - lastTime));
            lastTime = System.currentTimeMillis();
        }
        if (!isLocked && sensorEvent.values[0] < -5 && time - lastTime > 1250) {
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
            Log.d("qwe", "-10 " + (time - lastTime));
            lastTime = System.currentTimeMillis();
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public static class PlaceholderFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        public static PlaceholderFragment newInstance(int sectionNumber, File file) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);

            args.putString("Path", file.getAbsolutePath());


            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_obraz, container, false);
            ImageView image = rootView.findViewById(R.id.fragment_obraz);
            Bitmap bmp = BitmapFactory.decodeFile(getArguments().getString("Path"));
            image.setImageBitmap(bmp);

            return rootView;
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        File[] files;
        int current;

        SectionsPagerAdapter(FragmentManager fm, File[] files, int curr) {
            super(fm);
            this.current = curr;
            this.files = files;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position, files[position]);
        }


        @Override
        public int getCount() {
            return files.length;
        }
    }
}
