package com.pimamobile.pima.fragments;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pimamobile.pima.MainActivity;
import com.pimamobile.pima.R;
import com.pimamobile.pima.adapter.HistorySalesRecyclerAdapter;
import com.pimamobile.pima.models.Discount;
import com.pimamobile.pima.models.History;
import com.pimamobile.pima.models.Sale;
import com.pimamobile.pima.utils.EndlessRecyclerOnScrollChangeListener;
import com.pimamobile.pima.utils.interfaces.OnFragmentInteractListener;
import com.pimamobile.pima.utils.interfaces.OnLoadMoreListener;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {

    private OnFragmentInteractListener mListener;
    private RecyclerView mRecyclerView;
    private HistorySalesRecyclerAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private List<History> mHistories;


    private int loadLimit = 10;

    protected Handler handler;

    public static HistoryFragment newInstance() {
        return new HistoryFragment();
    }

    public HistoryFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.list);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        mHistories = new ArrayList<>();
        loadData();
        handler = new Handler();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mAdapter = new HistorySalesRecyclerAdapter(mHistories, mRecyclerView, mListener);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {

            @Override
            public void onLoadMore() {
                mHistories.add(null);
                mAdapter.notifyItemInserted(mHistories.size() - 1);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // remove progress item
                        mHistories.remove(mHistories.size() - 1);
                        mAdapter.notifyItemRemoved(mHistories.size());
                        // add items one by one.. dummy
                        int start = mHistories.size();
                        int end = start + 20;
                        List<Discount> mDiscounts = new ArrayList<>();
                        List<Sale> mSales = new ArrayList<>();

                        for (int i = 0; i < 5; i++) {
                            mSales.add(new Sale("ItemName " + i, "" + 5 + 1, i));
                        }
                        for (int i = start + 1; i <= end; i++) {
                            mHistories.add(new History(mSales, mDiscounts, System.currentTimeMillis()));
                            mAdapter.notifyItemInserted(mHistories.size());
                        }
                        mAdapter.setLoaded();
                        // or you can add all at once but do not forget to call mAdapter.notifyDataSetChanged();
                    }
                }, 2000);
            }
        });

        return view;
    }

    private void loadData() {
        List<Discount> mDiscounts = new ArrayList<>();
        List<Sale> mSales = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            mSales.add(new Sale("ItemName " + i, "" + 5 + 1, i));
        }
        for (int i = 1; i <= loadLimit; i++) {
            History history = new History(mSales, mDiscounts, System.currentTimeMillis());
            mHistories.add(history);

        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractListener) {
            mListener = (OnFragmentInteractListener) context;
        } else {
            throw new RuntimeException(context.toString() +
                    " must implement OnFragmentInteractListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.mIsHome = false;
        mListener.onFragmentStart(true);
    }
}
