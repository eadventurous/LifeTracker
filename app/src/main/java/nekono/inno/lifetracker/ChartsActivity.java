package nekono.inno.lifetracker;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
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

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import nekono.inno.lifetracker.model.Model;
import nekono.inno.lifetracker.model.Task;

public class ChartsActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charts);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_charts, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView;
            switch (getArguments().getInt(ARG_SECTION_NUMBER)) {
                case 0:
                    rootView = inflater.inflate(R.layout.category_chart, container, false);
                    PieChart pieChart = rootView.findViewById(R.id.category_chart);
                    List<PieEntry> entries = new ArrayList<>();
                    HashMap<String, Integer> dictionary = new HashMap<>();
                    for (Task task :
                            (new Model()).getTasks()) {
                        if (dictionary.containsKey(task.getCategory())) {
                            dictionary.put(task.getCategory(), dictionary.get(task.getCategory()) + 1);
                        } else {
                            dictionary.put(task.getCategory(), 1);
                        }
                    }
                    for (String key :
                            dictionary.keySet()) {
                        entries.add(new PieEntry(dictionary.get(key), key));
                    }
                    PieData pieData = new PieData(new PieDataSet(entries, "Categories"));
                    pieChart.setData(pieData);
                    pieChart.invalidate();
                    break;
                case 1:
                    rootView = inflater.inflate(R.layout.n_completed_chart, container, false);
                    BarChart completedChart = rootView.findViewById(R.id.completed_chart);
                    List<BarEntry> completedEntries = new ArrayList<>();
                    HashMap<Date, Integer> completed = new HashMap<>();
                    for (Task task :
                            (new Model()).getTasks()) {
                        if (completed.containsKey(task.getDateCompleted())) {
                            completed.put(task.getDateCompleted(), completed.get(task.getDateCompleted()) + 1);
                        } else {
                            completed.put(task.getDateCompleted(), 1);
                        }
                    }
                    final List<Date> keySet = new ArrayList<Date>(completed.keySet());
                    Collections.sort(keySet, new Comparator<Date>() {
                        public int compare(Date o1, Date o2) {
                            return o1.compareTo(o2);
                        }
                    });
                    for (int i = 0; i < keySet.size(); i++) {
                        completedEntries.add(new BarEntry(i, completed.get(keySet.get(i))));
                    }
                    IAxisValueFormatter formatter = new IAxisValueFormatter() {

                        @Override
                        public String getFormattedValue(float value, AxisBase axis) {
                            return keySet.get((int) value).toString();
                        }

                    };
                    completedChart.getXAxis().setValueFormatter(formatter);
                    completedChart.setData(new BarData(new BarDataSet(completedEntries, "Completed tasks")));
                    completedChart.invalidate();
                    break;
                default:
                    rootView = inflater.inflate(R.layout.time_spent_chart, container, false);
                    PieChart timeChart = rootView.findViewById(R.id.time_spent_chart);
                    List<PieEntry> timeEntries = new ArrayList<>();
                    for (Task task :
                            (new Model()).getTasks()) {
                        timeEntries.add(new PieEntry(task.getTimeElapsed().getTime(), task.getName()));
                    }
                    PieData timeData = new PieData(new PieDataSet(timeEntries, "Time elapsed"));
                    timeChart.setData(timeData);
                    timeChart.invalidate();
                    break;
            }
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            Log.d("item position", Integer.toString(position));
            return PlaceholderFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }
}