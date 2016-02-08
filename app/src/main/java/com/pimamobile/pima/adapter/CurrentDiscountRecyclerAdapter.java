package com.pimamobile.pima.adapter;


import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pimamobile.pima.R;
import com.pimamobile.pima.fragments.CurrentSalesDiscountFragment;
import com.pimamobile.pima.models.Discount;

import java.util.List;

public class CurrentDiscountRecyclerAdapter extends RecyclerView.Adapter<CurrentDiscountRecyclerAdapter.ViewHolder> {

    private static final String TAG = "CurrentDiscountAdapter";
    private List<Discount> mDiscounts;
    private CurrentSalesDiscountFragment.OnCurrentDiscountsClick mListener;

    public CurrentDiscountRecyclerAdapter(List<Discount> mDiscounts, CurrentSalesDiscountFragment.OnCurrentDiscountsClick mListener) {
        this.mDiscounts = mDiscounts;
        this.mListener = mListener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.current_sales_discount, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.discount = mDiscounts.get(position);
        holder.position = position;
        holder.discountName.setText(mDiscounts.get(position).getDiscountName());
        holder.affectedItem.setText("(All Items)");
        String amount = mDiscounts.get(position).getDiscountAmount();
        holder.discountAmount.setText(mDiscounts.get(position).isPercentage() ?
                amount + "%" : "-₱" + amount);
        holder.removeDiscount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    Log.i(TAG, "removeDiscount is clicked");
                    mListener.onCurrentDiscountRemove(holder.discount, holder.position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDiscounts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public int position;
        public TextView discountName;
        public TextView affectedItem;
        public TextView discountAmount;
        public ImageView removeDiscount;
        public Discount discount;

        public ViewHolder(View view) {
            super(view);
            discountName = (TextView) view.findViewById(R.id.current_sales_discount_name);
            discountAmount = (TextView) view.findViewById(R.id.current_sales_discount_amount);
            affectedItem = (TextView) view.findViewById(R.id.current_sales_discount_affected);
            removeDiscount = (ImageView) view.findViewById(R.id.current_sales_discount_remove);
        }
    }
}
