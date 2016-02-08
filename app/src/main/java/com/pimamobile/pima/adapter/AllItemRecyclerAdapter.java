package com.pimamobile.pima.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pimamobile.pima.R;
import com.pimamobile.pima.fragments.AllDiscountFragment;
import com.pimamobile.pima.fragments.AllItemFragment;
import com.pimamobile.pima.fragments.ItemLibraryFragment;
import com.pimamobile.pima.models.Discount;
import com.pimamobile.pima.models.Item;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Item} and makes a call to the
 * specified {@link AllItemFragment.OnItemListener}.
 */
public class AllItemRecyclerAdapter extends RecyclerView.Adapter<AllItemRecyclerAdapter.ViewHolder> {

    private final Context mContext;
    private List<Item> mItems;
    private List<Discount> mDiscounts;
    private AllItemFragment.OnItemListener mListener;
    private AllDiscountFragment.OnDiscountListener mDiscountListener;
    private ItemLibraryFragment.OnLibraryItemClickListener mLibraryItemListener;
    private boolean mIsDiscountList = false;

    public AllItemRecyclerAdapter(Context context, List<Item> items) {
        this(context, items, null);
    }


    public AllItemRecyclerAdapter(Context context, List<Discount> discounts,
                                  AllDiscountFragment.OnDiscountListener discountListener) {
        this.mContext = context;
        mDiscounts = discounts;
        mDiscountListener = discountListener;
        mIsDiscountList = true;
    }

    public AllItemRecyclerAdapter(Context mContext, List<Item> items, AllItemFragment.OnItemListener listener) {
        this.mContext = mContext;
        mItems = items;
        mListener = listener;
        mIsDiscountList = false;
    }

    public AllItemRecyclerAdapter(Context mContext, List<Discount> discounts, List<Item> items, ItemLibraryFragment.OnLibraryItemClickListener mLibraryItemListener) {
        this.mContext = mContext;
        if (items == null) {
            mDiscounts = discounts;
            mIsDiscountList = true;
        } else {
            mIsDiscountList = false;
            mItems = items;
        }
        this.mLibraryItemListener = mLibraryItemListener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_all_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (mIsDiscountList) {
            holder.mDiscount = mDiscounts.get(position);
            boolean isPercentage = mDiscounts.get(position).isPercentage();
            holder.mDiscount.setIsPercentage(isPercentage);
            holder.mItemName.setText(mDiscounts.get(position).getDiscountName());
            holder.mItemPrice.setText(isPercentage ? mDiscounts.get(position).getDiscountAmount() + "%" :
                    "-₱" + mDiscounts.get(position).getDiscountAmount());
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mDiscountListener) {
                        mDiscountListener.onDiscountClicked(holder.mDiscount);
                    }
                    if (null != mLibraryItemListener) {
                        mLibraryItemListener.onLibraryItemClickListener(holder.mDiscount);
                    }
                }
            });
        } else {
            holder.mItem = mItems.get(position);
            holder.mItem.setPosition(position);
            holder.mItemName.setText(mItems.get(position).getItemName());
            holder.mItemPrice.setText(mContext.getString(R.string.peso_symbol) + mItems.get(position).getItemPrice());

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mListener) {
                        // Notify the active callbacks interface (the activity, if the
                        // fragment is attached to one) that an item has been selected.
                        mListener.onItemClicked(holder.mItem);
                    }
                    if (null != mLibraryItemListener) {
                        mLibraryItemListener.onLibraryItemClickListener(holder.mItem);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (!mIsDiscountList) {
            return mItems.size();
        } else {
            return mDiscounts.size();
        }
    }


    public Item removeItem(int position) {
        final Item item = mItems.remove(position);
        notifyItemRemoved(position);
        return item;
    }

    public void addItem(int position, Item item) {
        mItems.add(position, item);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        Item item = mItems.remove(fromPosition);
        mItems.add(toPosition, item);
        notifyItemMoved(fromPosition, toPosition);
    }

    public void animateTo(List<Item> items) {
        applyAndAnimateRemovals(items);
        applyAndAnimateAdditions(items);
        applyAndAnimateMovedItems(items);
    }

    private void applyAndAnimateMovedItems(List<Item> newItems) {
        for (int i = mItems.size() - 1; i >= 0; i--) {
            final Item item = mItems.get(i);
            if (!newItems.contains(item)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<Item> newItems) {
        for (int i = 0, count = newItems.size(); i < count; i++) {
            final Item item = newItems.get(i);
            if (!mItems.contains(item)) {
                addItem(i, item);
            }
        }
    }

    private void applyAndAnimateRemovals(List<Item> newItems) {
        for (int toPosition = newItems.size() - 1; toPosition >= 0; toPosition--) {
            final Item item = newItems.get(toPosition);
            final int fromPosition = mItems.indexOf(item);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mItemPrice;
        public final TextView mItemName;
        public Item mItem;
        public Discount mDiscount;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mItemName = (TextView) view.findViewById(R.id.itemName);
            mItemPrice = (TextView) view.findViewById(R.id.itemPrice);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mItemName.getText() + "'";
        }
    }
}
