package com.the_spartan.virtualdiary.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.the_spartan.virtualdiary.R;
import com.the_spartan.virtualdiary.activity.MainActivity;
import com.the_spartan.virtualdiary.data.ToDoProvider;
import com.the_spartan.virtualdiary.model.ToDoItem;
import com.the_spartan.virtualdiary.interfacing.DeleteListCollector;
import com.the_spartan.virtualdiary.view.CustomDialog;

import java.util.ArrayList;
import java.util.List;

public class ToDoParentFragment extends Fragment implements DeleteListCollector {

    private static final String TAG = "ToDoParentFragment";

    private boolean isActiveFragmentOnScreen;

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private MenuItem deleteMenu;

    private ArrayList<ToDoItem> activeItemDeleteList;
    private ArrayList<ToDoItem> oldItemDeleteList;

    private ToDoActiveFragment activeFragment;
    private ToDoOldFragment oldFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_parent_to_do, container, false);
        Toolbar toolbar = view.findViewById(R.id.my_toolbar);
        ((MainActivity) getActivity()).setToolbar(toolbar);

        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        tabLayout = (TabLayout) view.findViewById(R.id.tab);

        setHasOptionsMenu(true);

        Log.d(TAG, "onCreateView");

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated");

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

        activeItemDeleteList = new ArrayList<>();
        oldItemDeleteList = new ArrayList<>();
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        activeFragment = new ToDoActiveFragment(this);
        oldFragment = new ToDoOldFragment(this);

        viewPagerAdapter.addFragment(activeFragment, "Active");
        viewPagerAdapter.addFragment(oldFragment, "Last 30 days");

        viewPager.setAdapter(viewPagerAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                isActiveFragmentOnScreen = position == 0;
                checkForDeleteVisibility(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.d(TAG, "onCreateOptionsMenu");
        menu.clear();
        inflater.inflate(R.menu.to_do_menu_main, menu);
        deleteMenu = menu.findItem(R.id.deleteItems);

        if (isActiveFragmentOnScreen) {
            checkForDeleteVisibility(activeItemDeleteList);
        } else {
            checkForDeleteVisibility(oldItemDeleteList);
        }

        deleteMenu.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                showDeleteConfirmDialog();
                return true;
            }
        });
    }

    public void showDeleteConfirmDialog() {
        ViewGroup viewGroup = getView().findViewById(android.R.id.content);
        final CustomDialog dialog = new CustomDialog(getContext(),
                viewGroup,
                R.layout.dialog,
                R.string.confirm_delete,
                R.string.dialog_msg_todo_multi_delete,
                R.string.dialog_btn_delete,
                R.string.dialog_btn_cancel);

        dialog.posBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteItems();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void deleteItems() {
        ArrayList<ToDoItem> deleteList;
        if (isActiveFragmentOnScreen) {
            deleteList = activeItemDeleteList;
        } else {
            deleteList = oldItemDeleteList;
        }

        for (ToDoItem item : deleteList) {
            Log.d(TAG, item.getSubject());
            String[] selectionArgs = new String[]{String.valueOf(item.getID())};

            Uri uri = Uri.withAppendedPath(ToDoProvider.CONTENT_URI, String.valueOf(ToDoProvider.DELETE_A_TODO));
            getContext().getContentResolver().delete(uri, null, selectionArgs);
        }

        deleteList.clear();
        deleteMenu.setVisible(false);

        if (isActiveFragmentOnScreen) {
            activeFragment.onResume();
        } else {
            oldFragment.onResume();
        }
    }

    @Override
    public void updateActiveItemDeleteList(ArrayList<ToDoItem> deleteList) {
        this.activeItemDeleteList = deleteList;
        checkForDeleteVisibility(deleteList);
    }

    @Override
    public void updateOldItemDeleteList(ArrayList<ToDoItem> deleteList) {
        this.oldItemDeleteList = deleteList;
        checkForDeleteVisibility(deleteList);
    }

    private void checkForDeleteVisibility(int position) {
        if (position == 0) {
            checkForDeleteVisibility(activeItemDeleteList);
        } else {
            checkForDeleteVisibility(oldItemDeleteList);
        }
    }

    private void checkForDeleteVisibility(ArrayList<ToDoItem> deleteList) {
        if (deleteMenu == null) {
            return;
        }

        if (deleteList.size() > 0) {
            deleteMenu.setVisible(true);
        } else {
            deleteMenu.setVisible(false);
        }
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {

        List<Fragment> fragmentList = new ArrayList<>();
        List<String> fragmentTitles = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitles.get(position);
        }

        public void addFragment(Fragment fragment, String name) {
            fragmentList.add(fragment);
            fragmentTitles.add(name);
        }
    }
}
