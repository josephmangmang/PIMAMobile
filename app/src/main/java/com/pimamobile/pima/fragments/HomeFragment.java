package com.pimamobile.pima.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.pimamobile.pima.MainActivity;
import com.pimamobile.pima.R;
import com.pimamobile.pima.adapter.ViewPagerAdapter;
import com.pimamobile.pima.utils.FragmentInterface;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";
    public Button mChargeButton;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private FragmentInterface mListener;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    public HomeFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView is called");
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mChargeButton = (Button) view.findViewById(R.id.button_charge);
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mTabLayout = (TabLayout) view.findViewById(R.id.tabs);
        setUpViewPager(mViewPager);
        mTabLayout.setupWithViewPager(mViewPager);
        mChargeButton.setOnClickListener(chargeButtonListener);
        return view;
    }

    private void setUpViewPager(ViewPager mViewPager) {
        Log.i(TAG, "setUpViewpager is called");
        ViewPagerAdapter mPagerAdapter = new ViewPagerAdapter((getChildFragmentManager()));
        mPagerAdapter.addFragment(KeypadFragment.newInstance(), getString(R.string.viewpager_keypad));
        mPagerAdapter.addFragment(ItemLibraryFragment.newInstance(), getString(R.string.viewpager_itemlibrary));
        mViewPager.setAdapter(mPagerAdapter);
    }

    private View.OnClickListener chargeButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mListener.onChargeButtonClicked();
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentInterface) {
            mListener = (FragmentInterface) context;
        } else {
            throw new RuntimeException(context.toString() +
                    "must implement FragmentInterface");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        MainActivity.mIsHome = true;
        mListener.onFragmentStart(false);
    }
    /*
    public interface OnHomeFragmentInteraction{
        void onChargeButtonClicked();
    }
    */
}
