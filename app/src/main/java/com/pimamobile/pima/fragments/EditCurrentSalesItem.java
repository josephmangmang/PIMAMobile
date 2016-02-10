package com.pimamobile.pima.fragments;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.pimamobile.pima.MainActivity;
import com.pimamobile.pima.R;
import com.pimamobile.pima.models.Discount;
import com.pimamobile.pima.models.Sale;
import com.pimamobile.pima.utils.FragmentInterface;

public class EditCurrentSalesItem extends Fragment {

    private static final String TAG = "EditCurrentSalesItem";
    private EditText mNote;
    private EditText mQuantity;
    private ImageView mQuantityDeduct;
    private ImageView mQuantityAdd;
    private Button mQuantityDeleteItem;
    private RecyclerView mRecyclerView;
    private Sale mCurrentSalesItem;
    private FragmentInterface mListener;
    private int mItemQuantity;
    private String mItemNote;


    public static EditCurrentSalesItem newInstance(Bundle bundle) {
        EditCurrentSalesItem fragment = new EditCurrentSalesItem();
        fragment.setArguments(bundle);
        return fragment;
    }

    public EditCurrentSalesItem() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCurrentSalesItem = getArguments().getParcelable(MainActivity.KEY_SELECTED_SALES_ITEM_);
            mItemQuantity = mCurrentSalesItem.getItemQuantity();
            mItemNote = mCurrentSalesItem.getItemNote();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_current_sales_item, container, false);
        mNote = (EditText) view.findViewById(R.id.edit_current_sales_note);
        mQuantity = (EditText) view.findViewById(R.id.edit_current_sales_quantity);
        mQuantityAdd = (ImageView) view.findViewById(R.id.edit_current_sales_quantity_add);
        mQuantityDeduct = (ImageView) view.findViewById(R.id.edit_current_sales_quantity_deduct);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.list);
        mQuantityDeleteItem = (Button) view.findViewById(R.id.edit_current_sales_delete);

        mQuantityDeleteItem.setOnClickListener(deletItemListener);
        mQuantityAdd.setOnClickListener(quantityButtonListener);
        mQuantityDeduct.setOnClickListener(quantityButtonListener);
        mQuantity.setText("" + mCurrentSalesItem.getItemQuantity());
        mNote.setText(mItemNote);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentInterface) {
            mListener = (FragmentInterface) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement FragmentInterface");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mListener.updateCurrentItemNote(mNote.getText().toString());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener.onEditCurrentSalesItemDistroy();
        mListener = null;
    }

    private View.OnClickListener quantityButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            boolean weAdd = v.getId() == R.id.edit_current_sales_quantity_add;
            if (weAdd) {
                mItemQuantity = mItemQuantity + 1;
                mListener.onEditQuantityButtonClicked(mItemQuantity);
                mQuantity.setText("" + mItemQuantity);
            } else {
                if (mItemQuantity == 1) return;
                mItemQuantity = mItemQuantity - 1;
                mListener.onEditQuantityButtonClicked(mItemQuantity);
                mQuantity.setText("" + mItemQuantity);
            }
        }
    };
    private boolean confirmDetetion;
    private View.OnClickListener deletItemListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (confirmDetetion) {
                mListener.onEditCurrentSalesItemDeleted();
                getActivity().onBackPressed();
            } else {
                mQuantityDeleteItem.setBackgroundColor(getResources().getColor(R.color.deleteButtonRed));
                mQuantityDeleteItem.setTextColor(Color.WHITE);
                mQuantityDeleteItem.setText(R.string.confirm_remove_item);
                confirmDetetion = true;
            }
        }
    };
/*
    public interface OnEditCurrentSalesItemInteraction {
        void onEditDiscountClicked(Discount discount);

        void onEditCurrentSalesItemDistroy();

        void onEditQuantityButtonClicked(int newQuantity);

        void onEditCurrentSalesItemDeleted();

        void updateCurrentItemNote(String note);
    }
    */
}
