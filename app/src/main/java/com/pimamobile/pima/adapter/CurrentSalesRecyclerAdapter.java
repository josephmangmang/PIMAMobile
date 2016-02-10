package com.pimamobile.pima.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pimamobile.pima.MainActivity;
import com.pimamobile.pima.R;
import com.pimamobile.pima.fragments.CurrentSalesFragment;
import com.pimamobile.pima.models.Discount;
import com.pimamobile.pima.models.Sale;
import com.pimamobile.pima.utils.FragmentInterface;

import java.util.ArrayList;
import java.util.List;


public class CurrentSalesRecyclerAdapter extends RecyclerView.Adapter<CurrentSalesRecyclerAdapter.ViewHolder> {

    private static final String TAG = "SalesRecyclerAdapter";
    private List<Sale> mSales = new ArrayList<>();
    private FragmentInterface mListener;
    private String mTotalDiscount;

    public CurrentSalesRecyclerAdapter(Context context, List<Sale> sales,FragmentInterface listener) {
        mSales = sales;
        mListener = listener;
        SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        mTotalDiscount = mPreferences.getString(MainActivity.KEY_CURRENT_SALES_TOTAL_DISCOUNT, "0");
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.current_sales_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.sale = mSales.get(position);
        holder.sale.setId(position);
        boolean lastItem = position == mSales.size() - 1;
        if (lastItem) {
            // insert discount item in list
            Log.i(TAG, "Last Item...");
            if (!mTotalDiscount.equals("0")) {
                Log.i(TAG, "Inserting Discount on last item on the list: Amount: " + mTotalDiscount);
                holder.itemName.setText("Discounts");
                holder.itemAmount.setText("-₱" + mTotalDiscount);
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.onCurrentSaleAllDiscountsClick();
                    }
                });
            }
        } else {
            holder.itemName.setText(mSales.get(position).getItemName());
            holder.itemAmount.setText("₱" + mSales.get(position).getItemTotalAmount());
            holder.itemNote.setText(mSales.get(position).getItemNote());
            int itemQuantity = mSales.get(position).getItemQuantity();
            holder.itemQuantity.setText("x" + itemQuantity);
            holder.itemQuantity.setVisibility(itemQuantity > 1 ? View.VISIBLE : View.GONE);
            holder.itemDiscountImage.setVisibility(mSales.get(position).isDiscount() ? View.VISIBLE : View.GONE);
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mListener) {
                        mListener.onCurrentSaleClicked(holder.sale);
                    }
                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return mSales.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final View mView;
        public Sale sale;
        public TextView itemName;
        public TextView itemNote;
        public TextView itemAmount;
        public TextView itemQuantity;
        public ImageView itemDiscountImage;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            itemName = (TextView) view.findViewById(R.id.current_sales_item_name);
            itemAmount = (TextView) view.findViewById(R.id.current_sales_item_amount);
            itemNote = (TextView) view.findViewById(R.id.current_sales_item_note);
            itemQuantity = (TextView) view.findViewById(R.id.current_sales_item_quantity);
            itemDiscountImage = (ImageView) view.findViewById(R.id.current_sales_item_discount);
        }
    }
}
