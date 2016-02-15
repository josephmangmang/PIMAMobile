package com.pimamobile.pima.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.pimamobile.pima.R;
import com.pimamobile.pima.adapter.ExpandableListAdapter;
import com.pimamobile.pima.adapter.SectionedRecyclerViewAdapter;
import com.pimamobile.pima.models.Discount;
import com.pimamobile.pima.models.Group;
import com.pimamobile.pima.models.History;
import com.pimamobile.pima.models.Sale;
import com.pimamobile.pima.utils.Calculator;
import com.pimamobile.pima.utils.interfaces.OnFragmentInteractListener;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class HistoryItemViewFragment extends Fragment {
    private static final String TAG = "HistoryItemViewFragment";
    private TextView mDate;
    private TextView mTotalAmount;
    private TextView mReceiptNumber;
    private ExpandableListView mExpandableList;
    private History mHistory;
    private OnFragmentInteractListener mListener;
    private List<Sale> mSales;
    private List<Discount> mDiscounts;
    private List<Group> mGroups;

    public static HistoryItemViewFragment newInstance(Bundle bundle) {
        HistoryItemViewFragment fragment = new HistoryItemViewFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public HistoryItemViewFragment() {
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mHistory = getArguments().getParcelable(HistoryFragment.KEY_HISTORY);
            mSales = mHistory.getSales();
            mDiscounts = mHistory.getDiscounts();

            mGroups = new ArrayList<>();
            String totalAmount = mHistory.getTotalAmount();
            String totalAmountWithOutDiscount = Calculator.addAmount(new BigDecimal(totalAmount), new BigDecimal(mHistory.getCalculateTotalDiscountAmount())).toString();
            Group saleGroup = new Group("Items", totalAmountWithOutDiscount);
            mGroups.add(saleGroup);
            saleGroup.setSales(mSales);

            if (mDiscounts.size() != 0) {
                Group discountGroup = new Group("Discounts", mHistory.getCalculateTotalDiscountAmount());
                discountGroup.setDiscounts(mDiscounts);
                discountGroup.setIsDiscount(true);
                mGroups.add(discountGroup);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_history_item, container, false);
        mDate = (TextView) view.findViewById(R.id.history_item_view_date);
        mTotalAmount = (TextView) view.findViewById(R.id.history_item_view_total_amount);
        mReceiptNumber = (TextView) view.findViewById(R.id.history_item_view_receipt_number);
        mExpandableList = (ExpandableListView) view.findViewById(R.id.expandable_list);

        //
        mDate.setText(mHistory.formatTimeStamp("MM/dd/yyyy h:mm a"));
        mTotalAmount.setText("₱" + mHistory.getTotalAmount());
        mReceiptNumber.setText(mHistory.getReceiptNumber());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ExpandableListAdapter adapter = new ExpandableListAdapter(getActivity(), true, mGroups);
        mExpandableList.setAdapter(adapter);
        mListener.onFragmentStart(true, false, mTotalAmount.getText().toString() + " Sale");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractListener) {
            mListener = (OnFragmentInteractListener) context;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mListener.onFragmentStart(false, false, "Sales History");
    }



}
