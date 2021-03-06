package com.mbq.menudrawer.mbqscpuguide;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class SamplesActivity extends ListActivity {

    private SamplesAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new SamplesAdapter();

        mAdapter.addSample("CPU Governors", "A list and defitinion of IO Schedulers and CPU Governors", ContentSample.class);
        mAdapter.addSample("Q and A", "A list of Questions and Answers",
                ListActivitySample.class);
        mAdapter.addSample("Future feature list", "A list of features to be included soon", WindowSample.class);
        mAdapter.addSample("Best combinations", "A list of the best potential CPU and IO combinations",
                ActionBarOverlaySample.class);
        mAdapter.addSample("Right menu", "The menu is positioned to the right of the content", RightMenuSample.class);
        mAdapter.addSample("MBQs Twitter Feed", "MBQ_'s Twitter feed", TopMenuSample.class);
        mAdapter.addSample("MBQs current ROM and kernel", "What I'm currently running", BottomMenuSample.class);
        mAdapter.addSample("Settings", "Change this app to your liking. :)", ViewPagerSample.class);
        mAdapter.addSample("Version", "Shows the apps build date and version", LayoutSample.class);
        mAdapter.addSample("DPI Changer", "Change your ROMs DPI quickly and easily", StaticDrawerSample.class);

        setListAdapter(mAdapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        SampleItem sample = (SampleItem) mAdapter.getItem(position);
        Intent i = new Intent(this, sample.mClazz);
        startActivity(i);
    }

    private static class SampleItem {

        String mTitle;
        String mSummary;
        Class mClazz;

        public SampleItem(String title, String summary, Class clazz) {
            mTitle = title;
            mSummary = summary;
            mClazz = clazz;
        }
    }

    public class SamplesAdapter extends BaseAdapter {

        private List<SampleItem> mSamples = new ArrayList<SampleItem>();

        public void addSample(String title, String summary, Class clazz) {
            mSamples.add(new SampleItem(title, summary, clazz));
        }

        @Override
        public int getCount() {
            return mSamples.size();
        }

        @Override
        public Object getItem(int position) {
            return mSamples.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            SampleItem sample = (SampleItem) getItem(position);

            View v = convertView;
            if (v == null) {
                v = getLayoutInflater().inflate(R.layout.list_row_sample, parent, false);
            }

            ((TextView) v.findViewById(R.id.title)).setText(sample.mTitle);
            ((TextView) v.findViewById(R.id.summary)).setText(sample.mSummary);

            return v;
        }
    }
}
