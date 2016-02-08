package com.pimamobile.pima.fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.pimamobile.pima.R;
import com.pimamobile.pima.activities.ItemsActivity;
import com.pimamobile.pima.adapter.AllItemRecyclerAdapter;
import com.pimamobile.pima.models.Category;
import com.pimamobile.pima.models.Item;
import com.pimamobile.pima.utils.SQLiteHelper;
import com.pimamobile.pima.utils.ToastMessage;

import java.util.ArrayList;
import java.util.List;

public class CreateCategoryFragment extends Fragment {
    private Button mSave;
    private EditText mCategoryName;
    private SQLiteHelper mSqLiteHelper;
    private boolean mHasArgument;
    private String mCategory;
    private Button mDeleteButton;
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private View mEmptyView;

    public CreateCategoryFragment() {
    }

    public static CreateCategoryFragment newInstance(Bundle bundle) {
        CreateCategoryFragment fragment = new CreateCategoryFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSqLiteHelper = new SQLiteHelper(getActivity());
        mHasArgument = getArguments() != null ? true : false;
        if (mHasArgument) {
            mCategory = getArguments().getString(ItemsActivity.CATEGORY_NAME);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_category, container, false);
        mCategoryName = (EditText) view.findViewById(R.id.createCategory_name);
        mSave = (Button) view.findViewById(R.id.createCategory_save);
        mDeleteButton = (Button) view.findViewById(R.id.createCategory_delete);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        mEmptyView = view.findViewById(R.id.empty);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.list);

        mSave.setOnClickListener(saveButtonListener);
        mDeleteButton.setOnClickListener(deleteButtonListener);


        return view;
    }  @Override
       public void onResume() {
        super.onResume();
        if (mSqLiteHelper == null) {
            mSqLiteHelper = new SQLiteHelper(getActivity());
        }
        mProgressBar.setVisibility(mHasArgument? View.VISIBLE : View.GONE);
        if (mHasArgument) {
            mCategoryName.setText(mCategory);
            mDeleteButton.setVisibility(View.VISIBLE);
            mSave.setText("UPDATE");

            new LoadListData().execute();
        }

    }

    class LoadListData extends AsyncTask<Void, Void, List<Item>> {

        @Override
        protected List<Item> doInBackground(Void... params) {
            return mSqLiteHelper.getAllItemInCategory(new Category(mCategory));
        }

        @Override
        protected void onPostExecute(List<Item> items) {
            mProgressBar.setVisibility(View.GONE);
            mEmptyView.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
            AllItemRecyclerAdapter adapter = new AllItemRecyclerAdapter(getActivity(), items);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            mRecyclerView.setAdapter(adapter);
        }
    }




    @Override
    public void onStop() {
        super.onStop();
        if (mSqLiteHelper != null) {
            mSqLiteHelper.close();
        }
    }

    private View.OnClickListener deleteButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final String name = mCategory;
            final Category category = new Category(name);

            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
            alert.setMessage(getString(R.string.alert_delete_message) + " " + name + "?")
                    .setPositiveButton(R.string.alert_button_yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int i = mSqLiteHelper.deleteCategory(category);
                            if (i > 0) {
                                ToastMessage.message(getActivity(), name + " was successfully deleted");
                            } else {
                                ToastMessage.message(getActivity(), "Unable to delete " + name + ". No data found.");
                            }
                            mCategoryName.setText("");
                            getActivity().onBackPressed();

                        }
                    })
                    .setNegativeButton(R.string.alert_button_no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            alert.show();
        }
    };


    private View.OnClickListener saveButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String name = mCategoryName.getText().toString();
            View focus = null;
            boolean cancel = false;
            if (TextUtils.isEmpty(name)) {
                mCategoryName.setError(getString(R.string.error_field_empty));
                cancel = true;
                focus = mCategoryName;
            }
            if (cancel) {
                focus.requestFocus();
            } else {
                Category category = new Category(name);
                if (mHasArgument) {
                    int i = mSqLiteHelper.updateCategory(new Category(mCategory), category);
                    if (i < 0) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                        alert.setTitle("Error")
                                .setMessage("Error updating " + name + ". Category already exist")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                        alert.show();
                    } else {
                        ToastMessage.message(getActivity(), name + " was updated");
                        getActivity().onBackPressed();
                    }
                } else {

                    long id = mSqLiteHelper.createCategory(category);
                    if (id < 0) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                        alert.setTitle("Error")
                                .setMessage("Error inserting " + name + ". Category already exist")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                        alert.show();
                    } else {
                        ToastMessage.message(getActivity(), "Success! " + name + " was added");
                        mCategoryName.setText("");
                    }
                }
            }
        }
    };


}
