package com.pimamobile.pima.fragments;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.pimamobile.pima.MainActivity;
import com.pimamobile.pima.R;
import com.pimamobile.pima.adapter.CurrentSalesRecyclerAdapter;
import com.pimamobile.pima.models.Discount;
import com.pimamobile.pima.models.Sale;

import java.util.ArrayList;
import java.util.List;

public class CurrentSalesFragment extends Fragment {

    private static final String TAG = "CurrentSalesFragment";
    private OnCurrentSalesClickListener mListener;
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private ArrayList<Sale> mCurrentSales;
    private String totalDiscount;

    public static CurrentSalesFragment newInstance(Bundle bundle) {
        CurrentSalesFragment fragment = new CurrentSalesFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public CurrentSalesFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Log.i(TAG, "getArguments != null");
            mCurrentSales = getArguments().getParcelableArrayList(MainActivity.KEY_CURRENT_SALES);
            totalDiscount = getArguments().getString(MainActivity.KEY_CURRENT_SALES_TOTAL_DISCOUNT);
            Log.i(TAG, "Total Discount: " + totalDiscount);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_current_sales, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.list);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.mIsHome = false;
        mListener.onFragmentStart(true);
        new LoadSalesData().execute(mCurrentSales);

    }


    class LoadSalesData extends AsyncTask<ArrayList<Sale>, Void, List<Sale>> {

        @Override
        protected List<Sale> doInBackground(ArrayList<Sale>... params) {
            return params[0];
        }

        @Override
        protected void onPostExecute(List<Sale> sales) {
            CurrentSalesRecyclerAdapter adapter = new CurrentSalesRecyclerAdapter(getActivity(),sales, mListener);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            mRecyclerView.setAdapter(adapter);
            mProgressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnCurrentSalesClickListener) {
            mListener = (OnCurrentSalesClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + "Must implement OnCurrentSalesClickListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;

    }

    public interface OnCurrentSalesClickListener {
        void onCurrentSaleClicked(Sale sale);

        void onCurrentSaleAllDiscountsClick();

        void onFragmentStart(boolean showHomAsUp);
    }
}
