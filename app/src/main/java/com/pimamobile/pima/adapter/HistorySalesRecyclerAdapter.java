package com.pimamobile.pima.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pimamobile.pima.R;
import com.pimamobile.pima.fragments.HistoryFragment;
import com.pimamobile.pima.models.History;
import com.pimamobile.pima.models.Sale;

import java.util.List;

public class HistorySalesRecyclerAdapter extends RecyclerView.Adapter<HistorySalesRecyclerAdapter.ViewHolder> {
    private List<History> mHistory;
    private HistoryFragment.OnHistoryFragment mListener;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.history_sales_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        // format date with EEEE, MMMM d, yyyy

        holder.mHistory = mHistory.get(position);
        holder.mAmount.setText(mHistory.get(position).getTotalAmount());
        holder.mItems.setText(mHistory.get(position).appendAllItem());
        holder.mTime.setText(mHistory.get(position).formatTimeStamp("h:mm a"));
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onHistoryItemClicked(holder.mHistory);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mHistory.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mAmount;
        TextView mItems;
        TextView mTime;
        History mHistory;
        View mView;

        public ViewHolder(View view) {
            super(view);
            mAmount = (TextView) view.findViewById(R.id.history_sales_amount);
            mItems = (TextView) view.findViewById(R.id.history_sales_items);
            mTime = (TextView) view.findViewById(R.id.history_sales_time);
            mView = view;
        }
    }
}
