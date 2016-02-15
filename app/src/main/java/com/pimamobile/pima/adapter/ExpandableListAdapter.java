package com.pimamobile.pima.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.pimamobile.pima.R;
import com.pimamobile.pima.models.Discount;
import com.pimamobile.pima.models.Group;
import com.pimamobile.pima.models.Sale;

import java.util.List;

public class ExpandableListAdapter extends BaseExpandableListAdapter {
    private List<Group> mGroups;
    private Context mContext;
    private boolean fromHistory;

    public ExpandableListAdapter(Context context, boolean fromHistory, List<Group> groups) {
        this.mContext = context;
        this.mGroups = groups;
        this.fromHistory = fromHistory;
    }

    @Override
    public int getGroupCount() {
        return mGroups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        boolean isDiscount = mGroups.get(groupPosition).isDiscount();
        return isDiscount ? mGroups.get(groupPosition).getDiscounts().size() : mGroups.get(groupPosition).getSales().size();

    }

    @Override
    public Object getGroup(int groupPosition) {
        return mGroups.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        boolean isDiscount = mGroups.get(groupPosition).isDiscount();
        return isDiscount ? mGroups.get(groupPosition).getDiscounts().get(childPosition) : mGroups.get(groupPosition).getSales().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflator = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
            convertView = inflator.inflate(R.layout.expandable_group_item, null);
        }
        Group group = (Group) getGroup(groupPosition);

        TextView name = (TextView) convertView.findViewById(R.id.expandable_item_name);
        TextView amount = (TextView) convertView.findViewById(R.id.expandable_item_total_amount);

        name.setText(group.getName());
        amount.setText(group.isDiscount() ? "-₱" + group.getTotalAmount() : "₱" + group.getTotalAmount());
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflator = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
            convertView = inflator.inflate(R.layout.expandable_child_item, null);
        }
        TextView name = (TextView) convertView.findViewById(R.id.expandable_item_name);
        TextView amount = (TextView) convertView.findViewById(R.id.expandable_item_total_amount);
        TextView quantity = (TextView) convertView.findViewById(R.id.expandable_item_quantity);
        TextView note = (TextView) convertView.findViewById(R.id.expandable_item_note);

        Group group = (Group) getGroup(groupPosition);
        if (group.isDiscount()) {
            Discount discount = (Discount) getChild(groupPosition, childPosition);
            name.setText(discount.getDiscountName());
            quantity.setVisibility(View.GONE);
            amount.setText("-₱" + discount.getDiscountAmount());
        } else {
            Sale sale = (Sale) getChild(groupPosition, childPosition);
            boolean b = childPosition == ((Group) getGroup(groupPosition)).getSales().size() - 1;
            if (b && sale.isDiscount()) {
                // this is the dummySalesFor Discount.
            } else {
                name.setText(sale.getItemName());
                quantity.setVisibility(sale.getItemQuantity() > 1 ? View.VISIBLE : View.GONE);
                quantity.setText("x" + sale.getItemQuantity());
                amount.setText(fromHistory ? "₱" + sale.getItemTotalAmount() : "₱" + sale.calculateItemTotalAmount());
                note.setText(sale.getItemNote());
            }
        }


        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
