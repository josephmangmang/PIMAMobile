package com.pimamobile.pima.fragments;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.pimamobile.pima.MainActivity;
import com.pimamobile.pima.R;
import com.pimamobile.pima.models.Discount;
import com.pimamobile.pima.models.Group;
import com.pimamobile.pima.models.Sale;
import com.pimamobile.pima.utils.Calculator;
import com.pimamobile.pima.utils.FragmentInterface;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ChargeFragment extends Fragment {
    private List<Sale> mSales;
    private List<Discount> mDiscounts;
    private ExpandableListView mListView;
    private List<Group> mGroups;
    private EditText mReceivedAmount;
    private Button mConfirmChargeButton;
    private Button mNewSaleButton;
    private View mThankYouContainer;
    private View mConfirmSummaryContainer;
    public TextView mDoneTitle;
    public TextView mDoneSubtitle;
    private FragmentInterface mListener;

    public static ChargeFragment newInstance(Bundle bundle) {
        ChargeFragment fragment = new ChargeFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public ChargeFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mSales = getArguments().getParcelableArrayList(MainActivity.KEY_CURRENT_SALES);
            mDiscounts = getArguments().getParcelableArrayList(MainActivity.KEY_CURRENT_SALES_DISCOUNTS);
            mGroups = new ArrayList<>();
            String totalAmount = getArguments().getString(MainActivity.KEY_CURRENT_SALES_TOTAL_AMOUNT);
            String discount = getArguments().getString(MainActivity.KEY_CURRENT_SALES_TOTAL_DISCOUNT);
            String totalAmountWithOutDiscount = Calculator.addAmount(new BigDecimal(totalAmount), new BigDecimal(discount)).toString();
            Group saleGroup = new Group("Items", totalAmountWithOutDiscount);
            mGroups.add(saleGroup);
            saleGroup.setSales(mSales);

            if (mDiscounts.size() != 0) {
                Group discountGroup = new Group("Discounts", discount);
                discountGroup.setDiscounts(mDiscounts);
                discountGroup.setIsDiscount(true);
                mGroups.add(discountGroup);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_charge, container, false);
        mListView = (ExpandableListView) view.findViewById(R.id.expandable_list);
        mReceivedAmount = (EditText) view.findViewById(R.id.charge_received_amount);
        mConfirmChargeButton = (Button) view.findViewById(R.id.charge_confirm_button);
        mThankYouContainer = view.findViewById(R.id.charge_thank_you_container);
        mConfirmSummaryContainer = view.findViewById(R.id.charge_confirmation_container);
        mNewSaleButton = (Button) view.findViewById(R.id.charge_new_sale);
        mDoneTitle = (TextView) view.findViewById(R.id.charge_thanks_title);
        mDoneSubtitle = (TextView) view.findViewById(R.id.charge_thanks_subtitle);

        mConfirmChargeButton.setOnClickListener(confirmButtonListener);
        mNewSaleButton.setOnClickListener(newSaleButtonListener);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.mIsHome = false;
        ExpandableListAdapter adapter = new ExpandableListAdapter(getActivity(), mGroups);
        mListView.setAdapter(adapter);
        mListView.setGroupIndicator(null);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentInterface) {
            mListener = (FragmentInterface) context;
        } else {
            throw new RuntimeException(context.toString() +
                    "must implement FragmentInterface");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void paymentDone() {
        mThankYouContainer.setVisibility(View.VISIBLE);
        mConfirmSummaryContainer.setVisibility(View.GONE);
    }

    private View.OnClickListener confirmButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mListener.onChargeConfirmClicked(mReceivedAmount.getText().toString());
        }
    };
    private View.OnClickListener newSaleButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mListener.onNewSaleClicked();
        }
    };
/*
    public interface OnChargeFragmentListener {
        void onChargeConfirmClicked(String amountRecieved);

        void onNewSaleClicked();
    }
*/
    public class ExpandableListAdapter extends BaseExpandableListAdapter {
        private List<Group> mGroups;
        private Context mContext;

        public ExpandableListAdapter(Context context, List<Group> groups) {
            this.mContext = context;
            this.mGroups = groups;
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
                    amount.setText("₱" + sale.getItemTotalAmount());
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
}
