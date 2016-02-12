package com.pimamobile.pima.adapter;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pimamobile.pima.R;
import com.pimamobile.pima.models.History;
import com.pimamobile.pima.utils.interfaces.OnFragmentInteractListener;
import com.pimamobile.pima.utils.interfaces.OnLoadMoreListener;

import java.util.List;

public class HistorySalesRecyclerAdapter extends RecyclerView.Adapter {
    private static final String TAG = "HistorySalesRecycler";
    private List<History> mHistories;
    private OnFragmentInteractListener mListener;
    private OnLoadMoreListener onLoadMoreListener;

    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    // The minimum amount of items to have below your current scroll position before loading more
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;

    public HistorySalesRecyclerAdapter(List<History> mHistories, RecyclerView recyclerView, OnFragmentInteractListener mListener) {
        this.mHistories = mHistories;
        this.mListener = mListener;
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    totalItemCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                    if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                        //end has been reached
                        Log.i(TAG, "list end reached....!");
                        if (onLoadMoreListener != null) {
                            onLoadMoreListener.onLoadMore();
                        }
                        loading = true;
                    }
                }
            });
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        if (viewType == VIEW_ITEM) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.history_sales_item, parent, false);
            viewHolder = new HistoryItemViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.progressbar_item, parent, false);
            viewHolder = new ProgressBarViewHolder(view);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HistoryItemViewHolder) {
            final HistoryItemViewHolder historyHolder = (HistoryItemViewHolder) holder;
            historyHolder.mHistory = mHistories.get(position);
            historyHolder.mAmount.setText("₱"+mHistories.get(position).getTotalAmount());
            historyHolder.mItems.setText(mHistories.get(position).appendAllItem());
            historyHolder.mTime.setText(mHistories.get(position).formatTimeStamp("h:mm a"));
            historyHolder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onHistoryItemClicked(historyHolder.mHistory);
                }
            });
        } else {
            ((ProgressBarViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }


    @Override
    public int getItemCount() {
        return mHistories.size();
    }

    @Override
    public int getItemViewType(int position) {

        return mHistories.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    public void setLoaded() {
        loading = false;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public class HistoryItemViewHolder extends RecyclerView.ViewHolder {
        TextView mAmount;
        TextView mItems;
        TextView mTime;
        History mHistory;
        View mView;

        public HistoryItemViewHolder(View view) {
            super(view);
            mAmount = (TextView) view.findViewById(R.id.history_sales_amount);
            mItems = (TextView) view.findViewById(R.id.history_sales_items);
            mTime = (TextView) view.findViewById(R.id.history_sales_time);
            mView = view;
        }
    }

    private class ProgressBarViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressBarViewHolder(View view) {
            super(view);
            progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        }
    }
}
