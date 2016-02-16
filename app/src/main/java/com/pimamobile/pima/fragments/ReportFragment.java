package com.pimamobile.pima.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pimamobile.pima.LoginActivity;
import com.pimamobile.pima.PimaApplication;
import com.pimamobile.pima.R;
import com.pimamobile.pima.activities.SettingActivity;
import com.pimamobile.pima.adapter.SectionedRecyclerViewAdapter;
import com.pimamobile.pima.models.Discount;
import com.pimamobile.pima.models.History;
import com.pimamobile.pima.models.Sale;
import com.pimamobile.pima.utils.Calculator;
import com.pimamobile.pima.utils.interfaces.OnFragmentInteractListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportFragment extends Fragment implements DatePickerFragment.OnDatePickerChangeListener {

    private static final String TAG = "ReportFragment";
    private int USER_ID;
    private TextView mDate, mGrossSales, mTotalSales, mAverageSales, mSummaryGrossSales,
            mSummaryDiscount, mSummaryNetSales, mSummaryNetTotal;
    private ProgressBar mProgressBar;
    private View mReportContainer;
    private TextView mNoDataTitle, mNoDataSubTitle;
    private OnFragmentInteractListener mListener;
    private List<History> mHistories;

    public static ReportFragment newInstance() {
        return new ReportFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        USER_ID = preferences.getInt(SettingActivity.KEY_USER_ID, -1);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report_summary, container, false);
        mDate = (TextView) view.findViewById(R.id.report_date);
        mGrossSales = (TextView) view.findViewById(R.id.report_gross_sales);
        mTotalSales = (TextView) view.findViewById(R.id.report_total_sales);
        mAverageSales = (TextView) view.findViewById(R.id.report_average_sales);
        mSummaryGrossSales = (TextView) view.findViewById(R.id.report_summary_gross_sales);
        mSummaryDiscount = (TextView) view.findViewById(R.id.report_summary_discounts);
        mSummaryNetSales = (TextView) view.findViewById(R.id.report_summary_net_sales);
        mSummaryNetTotal = (TextView) view.findViewById(R.id.report_summary_net_total);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        mReportContainer = view.findViewById(R.id.report_container);
        mNoDataSubTitle = (TextView) view.findViewById(R.id.report_no_data);
        mNoDataTitle = (TextView) view.findViewById(R.id.report_no_data_title);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractListener) {
            mListener = (OnFragmentInteractListener) context;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mListener.onFragmentStart(false, false, "Sales Report");
    }

    private void getSummaryReport(List<Date> dates) {
        mHistories = new ArrayList<>();
        showProgressBar(true);
        if (dates == null) {
            dates = new ArrayList<>();
            dates.add(new Date());
        }
        final List<Date> finalDates = dates;
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                LoginActivity.SERVER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                boolean error = true;
                showProgressBar(false);
                Log.i(TAG, response);
                // "EEEE, MMMM d, yyyy"
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    error = jsonObject.getBoolean("error");
                    //
                    JSONArray jsonHistories = jsonObject.getJSONArray("histories");
                    for (int i = 0; i < jsonHistories.length(); i++) {
                        JSONObject jsonHistory = jsonHistories.getJSONObject(i);
                        History history = new History();
                        history.setReceiptNumber(jsonHistory.getString("sales_receipt_number"));
                        history.setTimeStamp(jsonHistory.getLong("sales_timestamp"));
                        history.setTotalAmount(jsonHistory.getString("sales_total_amount"));

                        JSONArray sold_itemsJsonArray = jsonHistory.getJSONArray("sold_items");
                        List<Sale> sales = new ArrayList<>();
                        for (int j = 0; j < sold_itemsJsonArray.length(); j++) {
                            JSONObject jsonSoldItem = sold_itemsJsonArray.getJSONObject(j);
                            Sale sale = new Sale();
                            sale.setItemName(jsonSoldItem.getString("sold_item_name"));
                            sale.setItemQuantity(jsonSoldItem.getInt("sold_item_quantity"));
                            sale.setItemTotalAmount(jsonSoldItem.getString("sold_item_total_amount"));
                            sales.add(sale);
                        }
                        history.setSales(sales);
                        JSONArray sold_discountsJsonArray = jsonHistory.getJSONArray("sold_discounts");
                        List<Discount> discounts = new ArrayList<>();
                        for (int k = 0; k < sold_discountsJsonArray.length(); k++) {
                            JSONObject jsonSoldDiscount = sold_discountsJsonArray.getJSONObject(k);
                            Discount discount = new Discount();
                            discount.setDiscountName(jsonSoldDiscount.getString("sold_discount_name"));
                            discount.setDiscountAmount(jsonSoldDiscount.getString("sold_discount_amount"));
                            discounts.add(discount);
                        }
                        history.setDiscounts(discounts);
                        mHistories.add(history);
                    }
                    //
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (error) {
                    noDataFromDateRange(true);
                } else {
                    if (mHistories.size() == 0) {
                        noDataFromDateRange(true);
                    } else {
                        noDataFromDateRange(false);
                        updateUiData();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showProgressBar(false);
                noDataFromDateRange(true);
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("request", "GET_SALES_REPORT");
                params.put("id", "" + USER_ID);
                params.put("from", "" + finalDates.get(0).getTime());
                if (finalDates.size() > 1) {
                    params.put("to", "" + finalDates.get(finalDates.size() - 1).getTime());
                } else {
                    params.put("to", "" + finalDates.get(0).getTime());
                }
                return params;
            }
        };
        stringRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 5;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        PimaApplication.getmInstance().addToReqQueue(stringRequest, "sales_report");

        /*
        SELECT *
        FROM  `pima_sales`
        WHERE  `sales_timestamp` >=1455090630874
        AND  `sales_timestamp` <=1455090630874
        LIMIT 0 , 30
        */
    }

    private void updateUiData() {
        String DATE_PATERN = "MMMM d, yyyy";
        String date;
        String grossSales = "0";
        String totalSales = "0";
        String averageSales = "0";
        String discount = "0";
        String netSales = "0";// or netTotal
        if (mHistories.size() == 1) {
            date = mHistories.get(0).formatTimeStamp(DATE_PATERN);
            netSales = mHistories.get(0).getTotalAmount();
            discount = mHistories.get(0).getCalculateTotalDiscountAmount();
            totalSales = mHistories.get(0).getSales().size() + "";
            grossSales = Calculator.addAmount(new BigDecimal(netSales), new BigDecimal(discount)).toString();
            averageSales = new Calculator(new BigDecimal(grossSales), new BigDecimal(totalSales)).getQuotient().toString();

        } else {
            for (int i = 0; i < mHistories.size(); i++) {
                netSales = Calculator.addAmount(new BigDecimal(netSales), new BigDecimal(mHistories.get(i).getTotalAmount())).toString();
                discount = Calculator.addAmount(new BigDecimal(discount), new BigDecimal(mHistories.get(i).getCalculateTotalDiscountAmount())).toString();
                totalSales = Calculator.addAmount(new BigDecimal(totalSales), new BigDecimal(mHistories.get(i).getSales().size())).toString();
            }
            grossSales = Calculator.addAmount(new BigDecimal(netSales), new BigDecimal(discount)).toString();
            averageSales = new Calculator(new BigDecimal(grossSales), new BigDecimal(totalSales)).getQuotient().toString();
            date = mHistories.get(0).formatTimeStamp(DATE_PATERN) + " - " + mHistories.get(mHistories.size() - 1).formatTimeStamp(DATE_PATERN);
        }

        mDate.setText(date);
        mGrossSales.setText("₱" + grossSales);
        mTotalSales.setText(totalSales);
        mAverageSales.setText("₱" + averageSales);
        mSummaryGrossSales.setText("₱" + grossSales);
        mSummaryDiscount.setText("-₱" + discount);
        mSummaryNetSales.setText("₱" + netSales);
        mSummaryNetTotal.setText("₱" + netSales);
    }

    private void showProgressBar(boolean show) {
        mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        mReportContainer.setVisibility(!show ? View.VISIBLE : View.GONE);
    }

    public void noDataFromDateRange(boolean nodata) {
        mNoDataTitle.setVisibility(nodata ? View.VISIBLE : View.GONE);
        mNoDataSubTitle.setVisibility(nodata ? View.VISIBLE : View.GONE);
        mReportContainer.setVisibility(!nodata ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.report_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_time_frame) {
            DatePickerFragment datePickerFragment = DatePickerFragment.newInstance();
            datePickerFragment.setOnDatePickerListener(this);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .addToBackStack(null).replace(R.id.fragment_container, datePickerFragment).commit();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDatePickerChange(List<Date> dates) {
        noDataFromDateRange(false);
        getSummaryReport(dates);
    }
}
