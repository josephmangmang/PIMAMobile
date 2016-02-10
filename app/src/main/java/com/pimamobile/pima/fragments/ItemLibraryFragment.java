package com.pimamobile.pima.fragments;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pimamobile.pima.R;
import com.pimamobile.pima.adapter.AllItemRecyclerAdapter;
import com.pimamobile.pima.models.Category;
import com.pimamobile.pima.models.Discount;
import com.pimamobile.pima.models.Item;
import com.pimamobile.pima.utils.SQLiteHelper;

import java.util.ArrayList;
import java.util.List;

public class ItemLibraryFragment extends Fragment {

    private static final String TAG = "ItemLibraryFragment";
    private AppCompatSpinner mSpinner;
    private SQLiteHelper mSqLiteHelper;
    private OnLibraryItemClickListener mListener;
    private RecyclerView mRecyclerView;
    List<String> mLibrarySpinnerItems;
    private Context mContext;
    private TextView mEmptyView;
    private ProgressBar mProgressBar;


    public static Fragment newInstance() {
        ItemLibraryFragment itemLibrary = new ItemLibraryFragment();
        return itemLibrary;
    }

    public ItemLibraryFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSqLiteHelper = new SQLiteHelper(getContext());
        mContext = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView is called");
        View view = inflater.inflate(R.layout.item_library_fragment, container, false);
        mSpinner = (AppCompatSpinner) view.findViewById(R.id.library_spinner);
        mEmptyView = (TextView) view.findViewById(R.id.empty);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.list);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mSqLiteHelper == null) {
            mSqLiteHelper = new SQLiteHelper(getActivity());
        }
        new SpinnerData().execute();

    }

    class SpinnerData extends AsyncTask<Void, Integer, List<String>> {

        @Override
        protected List<String> doInBackground(Void... params) {

            mLibrarySpinnerItems = new ArrayList<>();
            mLibrarySpinnerItems.add("All Items");
            mLibrarySpinnerItems.add("Discounts");

            List<Category> allCategory = mSqLiteHelper.getAllCategory();
            for (int i = 0; i < allCategory.size(); i++) {
                String name = allCategory.get(i).getCategoryName();
                name = name.substring(0, 1).toUpperCase() + name.substring(1);
                mLibrarySpinnerItems.add(name);
            }
            return mLibrarySpinnerItems;
        }

        @Override
        protected void onPostExecute(List<String> libraryItems) {
            // Initialize adapter
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                    android.R.layout.simple_spinner_item, libraryItems);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            if (mSpinner != null) {
                mSpinner.setAdapter(adapter);
                mSpinner.setOnItemSelectedListener(spinnerItemListener);
            }
            mSqLiteHelper.close();
        }
    }

    private AdapterView.OnItemSelectedListener spinnerItemListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (mSqLiteHelper == null) {
                mSqLiteHelper = new SQLiteHelper(getActivity());
            }
            new LoadListData().execute(position);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    class LoadListData extends AsyncTask<Integer, Void, Integer> {
        List<Item> items;
        List<Discount> discounts;

        @Override
        protected Integer doInBackground(Integer... params) {
            int type = params[0];
            switch (type) {
                // All Items
                case 0:
                    items = mSqLiteHelper.getAllItems();
                    break;
                // All Discounts
                case 1:
                    discounts = mSqLiteHelper.getAllDiscount();
                    break;
                // All Items by category
                default:
                    Category category = new Category(mLibrarySpinnerItems.get(type));
                    items = mSqLiteHelper.getAllItemInCategory(category);
                    break;
            }
            return type;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            mProgressBar.setVisibility(View.GONE);
            int type = integer;
            AllItemRecyclerAdapter adapter;
            switch (type) {

                case 1:
                    mEmptyView.setVisibility(discounts.isEmpty() ? View.VISIBLE : View.GONE);
                    mEmptyView.setText(getString(R.string.empty_library_category_list) + " Discount");
                    adapter = new AllItemRecyclerAdapter(mContext, discounts, null, mListener);

                    break;
                default:
                    mEmptyView.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
                    mEmptyView.setText(getString(R.string.empty_library_category_list) + " " + mLibrarySpinnerItems.get(type));
                    adapter = new AllItemRecyclerAdapter(mContext, null, items, mListener);
                    break;
            }
            mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            mRecyclerView.setAdapter(adapter);
            mSqLiteHelper.close();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnLibraryItemClickListener) {
            mListener = (OnLibraryItemClickListener) context;
        } else {
            throw new RuntimeException(context.toString() +
                    "Must implement OnLibraryItemClickListener on your activity");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mListener = null;
    }

    public interface OnLibraryItemClickListener {
        void onLibraryItemClickListener(Item item);
        void onLibraryItemClickListener(Discount discount);
    }
}
