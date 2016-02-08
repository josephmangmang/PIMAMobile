package com.pimamobile.pima.fragments;


import android.content.Context;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pimamobile.pima.R;
import com.pimamobile.pima.activities.CreateActivity;
import com.pimamobile.pima.activities.ItemsActivity;
import com.pimamobile.pima.adapter.AllItemRecyclerAdapter;
import com.pimamobile.pima.models.Discount;
import com.pimamobile.pima.utils.SQLiteHelper;

import java.util.ArrayList;
import java.util.List;

public class AllDiscountFragment extends Fragment {

    public static final String TITLE = " All Discounts";
    private static final String TAG = "AllDiscountFragment";
    private OnDiscountListener mListener;
    private AllItemRecyclerAdapter mAdapter;
    private SQLiteHelper mSqlHelper;
    private View mEmptyView;
    private RecyclerView recyclerView;
    private ProgressBar mProgressBar;

    public static AllDiscountFragment newInstance() {
        return new AllDiscountFragment();

    }

    public AllDiscountFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSqlHelper = new SQLiteHelper(getActivity());

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_item_list, container, false);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        Button createButton = (Button) view.findViewById(R.id.allItem_creatItem);
        createButton.setOnClickListener(createButtonListener);
        createButton.setText(R.string.create_discount);

        recyclerView = (RecyclerView) view.findViewById(R.id.list);
        mEmptyView = view.findViewById(R.id.empty);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mSqlHelper == null) {
            mSqlHelper = new SQLiteHelper(getActivity());
        }
        new ListData().execute();
    }

    class ListData extends AsyncTask<Void, Void, List<Discount>> {
        @Override
        protected List<Discount> doInBackground(Void... params) {

            return mSqlHelper.getAllDiscount();
        }

        @Override
        protected void onPostExecute(List<Discount> discounts) {
            mProgressBar.setVisibility(View.GONE);
            mEmptyView.setVisibility(discounts.isEmpty() ? View.VISIBLE : View.GONE);
            mAdapter = new AllItemRecyclerAdapter(getActivity(), discounts, mListener);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(mAdapter);
            mSqlHelper.close();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.w(TAG, "onAttach is called");
        if (context instanceof OnDiscountListener) {
            mListener = (OnDiscountListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnDiscountListener");
        }
    }

    public interface OnDiscountListener {
        void onDiscountClicked(Discount discount);
    }

    private View.OnClickListener createButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), CreateActivity.class);
            intent.putExtra(ItemsActivity.EDIT_ITEM, false);
            intent.putExtra(ItemsActivity.TO_ACTIVITY, ItemsActivity.CREATE_DISCOUNT);
            startActivity(intent);
        }
    };
}
