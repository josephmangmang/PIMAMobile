/*
 * Copyright (c) 2016.  PIMA Mobile
 */

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

import com.pimamobile.pima.adapter.AllCategoryRecyclerAdapter;
import com.pimamobile.pima.R;
import com.pimamobile.pima.models.Category;
import com.pimamobile.pima.utils.SQLiteHelper;

import java.util.ArrayList;
import java.util.List;

public class AllCategoryFragment extends Fragment {

    public static final String TITLE = "All Category";
    private RecyclerView recyclerView;

    private OnCategoryClickListener mListener;
    private AllCategoryRecyclerAdapter mAdapter;
    private SQLiteHelper mSqlHelper;
    private View mEmptyView;
    private ProgressBar mProgressBar;

    public AllCategoryFragment() {

    }

    public static AllCategoryFragment newInstance() {

        return new AllCategoryFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSqlHelper = new SQLiteHelper(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_category, container, false);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
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

    class ListData extends AsyncTask<Void, Void, List<Category>> {
        @Override
        protected List<Category> doInBackground(Void... params) {

            return mSqlHelper.getAllCategory();
        }

        @Override
        protected void onPostExecute(List<Category> mCategories) {
            mProgressBar.setVisibility(View.GONE);
            mEmptyView.setVisibility(mCategories.isEmpty() ? View.VISIBLE : View.GONE);
            mAdapter = new AllCategoryRecyclerAdapter(mCategories, mListener);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(mAdapter);
            mSqlHelper.close();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnCategoryClickListener) {
            mListener = (OnCategoryClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnCategoryClickListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnCategoryClickListener {
        void onCategoryClicked(Category category);
    }
}
