package com.pimamobile.pima.fragments;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.pimamobile.pima.LoginActivity;
import com.pimamobile.pima.MainActivity;
import com.pimamobile.pima.PimaApplication;
import com.pimamobile.pima.R;
import com.pimamobile.pima.adapter.HistorySalesRecyclerAdapter;
import com.pimamobile.pima.models.Discount;
import com.pimamobile.pima.models.History;
import com.pimamobile.pima.models.Sale;
import com.pimamobile.pima.utils.ToastMessage;
import com.pimamobile.pima.utils.interfaces.OnFragmentInteractListener;
import com.pimamobile.pima.utils.interfaces.OnLoadMoreListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoryFragment extends Fragment {

    private static final String TAG = "HistoryFragment";
    private static final String TAG_GET_SALES_HISTORY = "get_sales_history";
    private static final String REQUEST_GET_SALES_HISTORY = "GET_SALES_HISTORY";
    private OnFragmentInteractListener mListener;
    private RecyclerView mRecyclerView;
    private HistorySalesRecyclerAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private List<History> mHistories = new ArrayList<>();
    private View mErrorContainer;

    private int loadLimit = 15;
    private int lastItemPosition = 0;
    private int userId = 1;
    private boolean noMoreHistory = false;

    public static HistoryFragment newInstance() {
        return new HistoryFragment();
    }

    public HistoryFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.list);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        mErrorContainer = view.findViewById(R.id.history_error_container);

        mSwipeRefreshLayout.setColorSchemeColors(
                R.color.refresh_progress1,
                R.color.refresh_progress2,
                R.color.refresh_progress3
        );
        mSwipeRefreshLayout.setOnRefreshListener(onRefreshListener);
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
                Log.i(TAG, "Refreshing from onCreatView....");
            }
        });

        getHistoryData();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);


        return view;
    }


    private SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            mSwipeRefreshLayout.setRefreshing(true);
            Log.i(TAG, "onRefresh .......");
            mHistories.clear();
            getHistoryData();
        }
    };
    private OnLoadMoreListener loadMoreListener = new OnLoadMoreListener() {

        @Override
        public void onLoadMore() {
            if (!noMoreHistory) {
                addMoreHistoryData();
            }
            /*
            // remove progress item
            mHistories.remove(mHistories.size() - 1);
            mAdapter.notifyItemRemoved(mHistories.size());
            // add items one by one.. dummy
            int start = mHistories.size();
            int end = start + 20;
            List<Discount> mDiscounts = new ArrayList<>();
            List<Sale> mSales = new ArrayList<>();

            for (int i = 0; i < 5; i++) {
                mSales.add(new Sale("ItemName " + i, "" + 5 + 1, i));
            }
            for (int i = start + 1; i <= end; i++) {
                mHistories.add(new History(mSales, mDiscounts, System.currentTimeMillis()));
                mAdapter.notifyItemInserted(mHistories.size());
            }
            mAdapter.setLoaded();
            // or you can add all at once but do not forget to call mAdapter.notifyDataSetChanged();
            */
        }
    };

    private void addMoreHistoryData() {
        mErrorContainer.setVisibility(View.GONE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                LoginActivity.SERVER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                boolean error = true;
                Log.i(TAG, "onResponse: " + response.toString());
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    error = jsonObject.getBoolean("error");
                    noMoreHistory = jsonObject.getBoolean("noMoreHistory");
                    JSONArray jsonHistories = jsonObject.getJSONArray("histories");

                    mHistories.remove(mHistories.size() - 1);
                    mAdapter.notifyItemRemoved(mHistories.size());
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
                } catch (JSONException e) {
                    ToastMessage.message(getActivity(), "Error getting parsing history data.");
                    e.printStackTrace();
                }
                mAdapter.notifyDataSetChanged();
                mAdapter.setLoaded();
                if (error) {
                    Log.i(TAG, "result error = true");
                    ToastMessage.message(getActivity(), "Error getting history data.");
                } else {
                    Log.i(TAG, "result error = false");

                }
                mSwipeRefreshLayout.setRefreshing(false);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mErrorContainer.setVisibility(View.VISIBLE);
                mSwipeRefreshLayout.setRefreshing(false);
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                lastItemPosition = mHistories.size();
                Log.i(TAG, "getParams().. " + userId + " " + loadLimit + " " + lastItemPosition);
                Map<String, String> params = new HashMap<>();
                params.put("request", REQUEST_GET_SALES_HISTORY);
                params.put("id", "" + userId);
                params.put("load_limit", "" + loadLimit);
                params.put("last_item", "" + lastItemPosition);
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
        PimaApplication.getmInstance().addToReqQueue(stringRequest, TAG_GET_SALES_HISTORY);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractListener) {
            mListener = (OnFragmentInteractListener) context;
        } else {
            throw new RuntimeException(context.toString() +
                    " must implement OnFragmentInteractListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.mIsHome = false;
        mListener.onFragmentStart(true, false, "Sales History");
    }

    private void getHistoryData() {
        mErrorContainer.setVisibility(View.GONE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                LoginActivity.SERVER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                boolean error = true;
                Log.i(TAG, "onResponse: " + response.toString());
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    error = jsonObject.getBoolean("error");
                    noMoreHistory = jsonObject.getBoolean("noMoreHistory");
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
                } catch (JSONException e) {
                    ToastMessage.message(getActivity(), "Error getting parsing history data.");
                    e.printStackTrace();
                }
                if (error) {
                    Log.i(TAG, "result error = true");
                    ToastMessage.message(getActivity(), "Error getting history data.");
                } else {
                    Log.i(TAG, "result error = false");
                    mAdapter = new HistorySalesRecyclerAdapter(mHistories, mRecyclerView, mListener);
                    mRecyclerView.setAdapter(mAdapter);
                    mAdapter.setOnLoadMoreListener(loadMoreListener);
                }
                Log.i(TAG, mHistories.size() + " List Item size: " + mRecyclerView.getChildCount());
                mSwipeRefreshLayout.setRefreshing(false);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mErrorContainer.setVisibility(View.VISIBLE);
                mSwipeRefreshLayout.setRefreshing(false);
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Log.i(TAG, "getParams().. " + userId + " " + loadLimit + " " + lastItemPosition);
                Map<String, String> params = new HashMap<>();
                params.put("request", REQUEST_GET_SALES_HISTORY);
                params.put("id", "" + userId);
                params.put("load_limit", "" + loadLimit);
                params.put("last_item", "0");
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
        PimaApplication.getmInstance().addToReqQueue(stringRequest, TAG_GET_SALES_HISTORY);
    }
}
