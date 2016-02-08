/*
 * Copyright (c) 2016.  PIMA Mobile
 */

package com.pimamobile.pima.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pimamobile.pima.R;
import com.pimamobile.pima.fragments.AllCategoryFragment;
import com.pimamobile.pima.models.Category;

import java.util.List;

public class AllCategoryRecyclerAdapter extends RecyclerView.Adapter<AllCategoryRecyclerAdapter.ViewHolder> {
    private List<Category> categories;
    private AllCategoryFragment.OnCategoryClickListener mListener;

    public AllCategoryRecyclerAdapter(List<Category> categories, AllCategoryFragment.OnCategoryClickListener mListener) {
        this.categories = categories;
        this.mListener = mListener;

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_category_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

            holder.category = categories.get(position);
            holder.categoryName.setText(categories.get(position).getCategoryName());
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onCategoryClicked(holder.category);
                    }
                }
            });

    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView categoryName;
        public Category category;
        public View mView;


        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            categoryName = (TextView) itemView.findViewById(R.id.categoryName);
        }
    }
}
