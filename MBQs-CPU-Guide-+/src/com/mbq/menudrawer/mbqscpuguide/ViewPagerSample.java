package com.mbq.menudrawer.mbqscpuguide;

import java.util.ArrayList;
import java.util.List;

import net.simonvt.menudrawer.MenuDrawer;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

public class ViewPagerSample extends FragmentActivity {

    private static final String STATE_ACTIVE_POSITION = "com.mbq.menudrawer.mbqscpuguides.ContentSample.activePosition";

    private MenuDrawer mMenuDrawer;

    private MenuAdapter mAdapter;
    private ListView mList;

    private int mActivePosition = -1;
    private PagerAdapter mPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle inState) {
        super.onCreate(inState);

        if (inState != null) {
            mActivePosition = inState.getInt(STATE_ACTIVE_POSITION);
        }

        mMenuDrawer = MenuDrawer.attach(this, MenuDrawer.MENU_DRAG_CONTENT);
        mMenuDrawer.setContentView(R.layout.activity_viewpagersample);

        List<Object> items = new ArrayList<Object>();
        items.add(new Item("Item 1", R.drawable.ic_action_refresh_dark));
        items.add(new Item("Item 2", R.drawable.ic_action_select_all_dark));
        items.add(new Category("Cat 1"));
        items.add(new Item("Item 3", R.drawable.ic_action_refresh_dark));
        items.add(new Item("Item 4", R.drawable.ic_action_select_all_dark));
        items.add(new Category("Cat 2"));
        items.add(new Item("Item 5", R.drawable.ic_action_refresh_dark));
        items.add(new Item("Item 6", R.drawable.ic_action_select_all_dark));

        // A custom ListView is needed so the drawer can be notified when it's
        // scrolled. This is to update the position
        // of the arrow indicator.
        mList = new ListView(this);
        mAdapter = new MenuAdapter(items);
        mList.setAdapter(mAdapter);
        mList.setOnItemClickListener(mItemClickListener);

        mMenuDrawer.setMenuView(mList);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mMenuDrawer.setTouchMode(MenuDrawer.TOUCH_MODE_FULLSCREEN);

        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mViewPager
                .setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(final int position) {
                        mMenuDrawer.setTouchMode(position == 0
                                ? MenuDrawer.TOUCH_MODE_FULLSCREEN
                                : MenuDrawer.TOUCH_MODE_NONE);
                    }
                });

        mPagerAdapter = new PagerAdapter(this);
        mPagerAdapter.addTab(TextViewFragment.class, null);
        mPagerAdapter.addTab(TextViewFragment.class, null);
        mPagerAdapter.addTab(TextViewFragment.class, null);

        mViewPager.setAdapter(mPagerAdapter);
    }

    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                long id) {
            mActivePosition = position;
            mMenuDrawer.setActiveView(view, position);
            mMenuDrawer.closeMenu();
        }
    };

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_ACTIVE_POSITION, mActivePosition);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mMenuDrawer.toggleMenu();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        final int drawerState = mMenuDrawer.getDrawerState();
        if (drawerState == MenuDrawer.STATE_OPEN
                || drawerState == MenuDrawer.STATE_OPENING) {
            mMenuDrawer.closeMenu();
            return;
        }

        super.onBackPressed();
    }

    private static final class Item {

        String mTitle;
        int mIconRes;

        Item(String title, int iconRes) {
            mTitle = title;
            mIconRes = iconRes;
        }
    }

    private static final class Category {

        String mTitle;

        Category(String title) {
            mTitle = title;
        }
    }

    private class MenuAdapter extends BaseAdapter {

        private List<Object> mItems;

        MenuAdapter(List<Object> items) {
            mItems = items;
        }

        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public Object getItem(int position) {
            return mItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            return getItem(position) instanceof Item ? 0 : 1;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public boolean isEnabled(int position) {
            return getItem(position) instanceof Item;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            Object item = getItem(position);

            if (item instanceof Category) {
                if (v == null) {
                    v = getLayoutInflater().inflate(R.layout.menu_row_category,
                            parent, false);
                }

                ((TextView) v).setText(((Category) item).mTitle);

            } else {
                if (v == null) {
                    v = getLayoutInflater().inflate(R.layout.menu_row_item,
                            parent, false);
                }

                TextView tv = (TextView) v;
                tv.setText(((Item) item).mTitle);
                tv.setCompoundDrawablesWithIntrinsicBounds(
                        ((Item) item).mIconRes, 0, 0, 0);
            }

            v.setTag(R.id.mdActiveViewPosition, position);

            if (position == mActivePosition) {
                mMenuDrawer.setActiveView(v, position);
            }

            return v;
        }
    }

    /**
     * This is a helper class that implements the management of tabs and all
     * details of connecting a ViewPager with associated TabHost. It relies on a
     * trick. Normally a tab host has a simple API for supplying a View or
     * Intent that each tab will show. This is not sufficient for switching
     * between pages. So instead we make the content part of the tab host 0dp
     * high (it is not shown) and the TabsAdapter supplies its own dummy view to
     * show as the tab content. It listens to changes in tabs, and takes care of
     * switch to the correct paged in the ViewPager whenever the selected tab
     * changes.
     */
    public static class PagerAdapter extends FragmentPagerAdapter {

        private final Context mContext;
        private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();

        static final class TabInfo {

            private final Class<?> mClss;
            private final Bundle mArgs;

            TabInfo(Class<?> aClass, Bundle args) {
                mClss = aClass;
                mArgs = args;
            }
        }

        public PagerAdapter(FragmentActivity activity) {
            super(activity.getSupportFragmentManager());
            mContext = activity;
        }

        @Override
        public int getCount() {
            return mTabs.size();
        }

        @Override
        public Fragment getItem(int position) {
            TabInfo info = mTabs.get(position);
            return Fragment.instantiate(mContext, info.mClss.getName(),
                    info.mArgs);
        }

        public void addTab(Class<?> clss, Bundle args) {
            TabInfo info = new TabInfo(clss, args);
            mTabs.add(info);
            notifyDataSetChanged();
        }

    }

    public static class TextViewFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            FrameLayout frameLayout = new FrameLayout(getActivity());
            frameLayout.setLayoutParams(new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT, Gravity.CENTER));

            TextView tv = new TextView(getActivity());
            tv.setText("This is an example of a Fragment in a View Pager");
            frameLayout.addView(tv);
            return frameLayout;
        }
    }
}
