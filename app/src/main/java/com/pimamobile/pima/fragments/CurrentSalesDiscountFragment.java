package com.pimamobile.pima.fragments;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.pimamobile.pima.MainActivity;
import com.pimamobile.pima.R;
import com.pimamobile.pima.adapter.CurrentDiscountRecyclerAdapter;
import com.pimamobile.pima.models.Discount;
import com.pimamobile.pima.utils.FragmentInterface;

import java.util.List;

public class CurrentSalesDiscountFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private List<Discount> mDiscounts;
    private ProgressBar mProgressBar;
    private FragmentInterface mListener;

    public static CurrentSalesDiscountFragment newInstance(Bundle bundle) {
        CurrentSalesDiscountFragment fragment = new CurrentSalesDiscountFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public CurrentSalesDiscountFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mDiscounts = getArguments().getParcelableArrayList(MainActivity.KEY_CURRENT_SALES_DISCOUNTS);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_current_sales_discounts, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.list);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        new LoadDiscounts().execute(mDiscounts);
    }

    class LoadDiscounts extends AsyncTask<List<Discount>, Void, List<Discount>> {

        @Override
        protected List<Discount> doInBackground(List<Discount>... params) {

            return params[0];
        }

        @Override
        protected void onPostExecute(List<Discount> discounts) {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            CurrentDiscountRecyclerAdapter adapter = new CurrentDiscountRecyclerAdapter(mDiscounts, mListener);
            mRecyclerView.setAdapter(adapter);
            mProgressBar.setVisibility(View.GONE);
        }

    }

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
    public void onDetach() {
        super.onDetach();
        mListener.onEditCurrentSalesItemDistroy();
        mListener = null;
    }
/*
    public interface OnCurrentDiscountsClick {
        void onCurrentDiscountRemove(Discount discount, int position);
    }
    */
}
