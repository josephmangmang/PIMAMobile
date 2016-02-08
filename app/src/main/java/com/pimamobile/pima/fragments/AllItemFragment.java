package com.pimamobile.pima.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.pimamobile.pima.adapter.AllItemRecyclerAdapter;
import com.pimamobile.pima.R;
import com.pimamobile.pima.models.Item;
import com.pimamobile.pima.utils.SQLiteHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnItemListener}
 * interface.
 */
public class AllItemFragment extends Fragment implements SearchView.OnQueryTextListener {


    public static final String TITLE = "All Items";
    private static final String TAG = "AllItemFragment";
    private OnItemListener mListener;
    private AllItemRecyclerAdapter mAdapter;
    private RecyclerView recyclerView;
    private ProgressBar mProgressBar;
    protected SQLiteHelper mSqlHelper;
    private View mEmptyView;
    private List<Item> mItems = new ArrayList<>();

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AllItemFragment() {
    }


    public static AllItemFragment newInstance() {
        AllItemFragment fragment = new AllItemFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSqlHelper = new SQLiteHelper(getActivity());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_item_list, container, false);
        setHasOptionsMenu(true);
        mEmptyView = view.findViewById(R.id.empty);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        recyclerView = (RecyclerView) view.findViewById(R.id.list);

        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mSqlHelper != null) {
            mSqlHelper.close();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mSqlHelper == null) {
            mSqlHelper = new SQLiteHelper(getActivity());
        }
        new ListData().execute();
    }

    class ListData extends AsyncTask<Void, Void, List<Item>> {
        @Override
        protected List<Item> doInBackground(Void... params) {

            return mSqlHelper.getAllItems();
        }

        @Override
        protected void onPostExecute(List<Item> items) {
            mItems = items;
            mProgressBar.setVisibility(View.GONE);
            mEmptyView.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
            mAdapter = new AllItemRecyclerAdapter(getActivity(), items, mListener);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(mAdapter);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Log.w(TAG, "onAttach is called");
        if (context instanceof OnItemListener) {
            mListener = (OnItemListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnItemListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.search_menu, menu);

        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        // Here is where we are going to implement our filter logic
        final List<Item> filteredItemList = filter(mItems, newText);
        mAdapter.animateTo(filteredItemList);
        recyclerView.scrollToPosition(0);
        return true;
    }

    private List<Item> filter(List<Item> mItems, String query) {
        query = query.toLowerCase();
        final List<Item> filteredItemList = new ArrayList<>();
        for (Item item : mItems) {
            final String text = item.getItemName().toLowerCase();
            if (text.contains(query)) {
                filteredItemList.add(item);
            }
        }
        return filteredItemList;
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */

    public interface OnItemListener {

        void onItemClicked(Item item);
    }


}
