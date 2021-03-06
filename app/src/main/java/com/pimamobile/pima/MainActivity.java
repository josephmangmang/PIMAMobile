package com.pimamobile.pima;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.pimamobile.pima.activities.ItemsActivity;
import com.pimamobile.pima.activities.SettingActivity;
import com.pimamobile.pima.fragments.ChargeFragment;
import com.pimamobile.pima.fragments.CurrentSalesDiscountFragment;
import com.pimamobile.pima.fragments.CurrentSalesFragment;
import com.pimamobile.pima.fragments.EditCurrentSalesItem;
import com.pimamobile.pima.fragments.HistoryFragment;
import com.pimamobile.pima.fragments.HistoryItemViewFragment;
import com.pimamobile.pima.fragments.HomeFragment;
import com.pimamobile.pima.fragments.ReportFragment;
import com.pimamobile.pima.models.Discount;
import com.pimamobile.pima.models.History;
import com.pimamobile.pima.models.Item;
import com.pimamobile.pima.models.Sale;
import com.pimamobile.pima.utils.Calculator;
import com.pimamobile.pima.utils.interfaces.OnFragmentInteractListener;
import com.pimamobile.pima.utils.ToastMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 This is the MainActivity that extends AppCompatActivity class.
 The starting point of the app, mao ni ang tawgon every time we open the app.
*/
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnFragmentInteractListener {

    private static final String TAG = "MainActivity";
    public static final String KEY_CURRENT_SALES = "sales_data";
    public static final String KEY_CURRENT_SALES_TOTAL_AMOUNT = "sales_total_amount";
    public static final String KEY_CURRENT_SALES_DISCOUNTS = "sales_discounts";
    public static final String KEY_CURRENT_SALES_TOTAL_DISCOUNT = "sales_total_discount";
    public static final String KEY_SELECTED_SALES_ITEM_ = "item_selected";

    private DrawerLayout mDrawer;
    private LinearLayout mCurrentSaleButton;
    private TextView mToolbarTitleVIew;
    private TextView mCurrentSalesCountView;
    private ArrayList<Sale> mCurrentSales = new ArrayList<>();
    private ArrayList<Discount> mCurrentSalesDiscounts = new ArrayList<>();

    private String mTotalDiscount = "0";
    private String mCurrentSalesTotalAmount = "0.00";
    private String mCurrentSalesTotalItems = "0";
    private ActionBarDrawerToggle mDrawerToggle;
    private int mCurrentItemPosition;
    public static boolean mIsHome = true;
    private boolean mTransactionIsOnGoing = false;
    private SharedPreferences mPreferences;
    private int USER_ID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate is called");
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        navigationView.setNavigationItemSelectedListener(this);
        mToolbarTitleVIew = (TextView) findViewById(R.id.toolbar_title);
        mCurrentSaleButton = (LinearLayout) findViewById(R.id.toolbar_sales_summary_container);
        mCurrentSalesCountView = (TextView) findViewById(R.id.toolbar_sales_count);
        mCurrentSaleButton.setOnClickListener(currentSalesButtonListener);

        USER_ID = mPreferences.getInt(SettingActivity.KEY_USER_ID, -1);
        String branchName = mPreferences.getString(SettingActivity.KEY_BRANCH_NAME, getString(R.string.app_name));
        View navHeader = navigationView.getHeaderView(0);
        ((TextView) navHeader.findViewById(R.id.branch_name)).setText(branchName);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, HomeFragment.newInstance(), "home").commit();

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume is called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause is called");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.i(TAG, "onSaveInstanceState is called");
        outState.putParcelableArrayList(KEY_CURRENT_SALES, mCurrentSales);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.i(TAG, "onRestoreInstanceState is called");
        mCurrentSales = savedInstanceState.getParcelableArrayList(KEY_CURRENT_SALES);
        calculateCurrentTotalSale(mCurrentSales);
    }


    @Override
    public void setSupportActionBar(Toolbar toolbar) {
        super.setSupportActionBar(toolbar);
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.setDrawerListener(mDrawerToggle);
        mDrawerToggle.setHomeAsUpIndicator(android.support.design.R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        mDrawerToggle.syncState();
        mDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (!mTransactionIsOnGoing) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            if (!item.isChecked())
                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        } else if (id == R.id.nav_history) {
            if (!item.isChecked()) getSupportFragmentManager().beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .replace(R.id.fragment_container, HistoryFragment.newInstance())
                    .addToBackStack(null).commit();
        } else if (id == R.id.nav_reports) {
            if (!item.isChecked()) getSupportFragmentManager().beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .replace(R.id.fragment_container, ReportFragment.newInstance())
                    .addToBackStack(null).commit();
        } else if (id == R.id.nav_items) {
            startActivity(new Intent(this, ItemsActivity.class));
        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(this, SettingActivity.class));
        }
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }


    @Override
    public void onLibraryItemClickListener(Item item) {
        Sale curentSaleItem = new Sale();
        curentSaleItem.setItemName(item.getItemName());
        curentSaleItem.setItemPrice(item.getItemPrice());
        curentSaleItem.setItemQuantity(1);
        if (mCurrentSales.size() > 0) {
            if (mCurrentSales.get(mCurrentSales.size() - 1).getItemName().equals("") &&
                    mCurrentSales.get(mCurrentSales.size() - 1).getItemPrice().equals("0")) {
                mCurrentSales.remove(mCurrentSales.size() - 1);
            }
            if (mCurrentSales.size() > 0) {
                Sale lastSaleItem = mCurrentSales.get(mCurrentSales.size() - 1);
                if (lastSaleItem.toString().equals(curentSaleItem.toString())) {
                    lastSaleItem.setItemQuantity(lastSaleItem.getItemQuantity() + 1);
                } else {
                    // mCurrentSales.remove(mCurrentSales.size() - 1);
                    mCurrentSales.add(curentSaleItem);
                    //mCurrentSales.add(dummySaleForDiscount);
                }
            } else {
                mCurrentSales.add(curentSaleItem);
            }
        } else {

            mCurrentSales.add(curentSaleItem);
            //mCurrentSales.add(dummySaleForDiscount);
        }
        calculateCurrentTotalSale(mCurrentSales);
    }

    private void calculateCurrentTotalSale(List<Sale> sales) {
        List<BigDecimal> totalItemAmount = new ArrayList<>();
        List<BigDecimal> totalItem = new ArrayList<>();
        for (int i = 0; i < sales.size(); i++) {
            totalItemAmount.add(new BigDecimal(sales.get(i).calculateItemTotalAmount()));
            totalItem.add(new BigDecimal(sales.get(i).getItemQuantity()));
        }
        mCurrentSalesTotalAmount = Calculator.addAllData(totalItemAmount).toString();


        if (mCurrentSalesDiscounts.size() > 0) {
            List<BigDecimal> allDiscount = new ArrayList<>();
            Log.i(TAG, "CalculateCurrentTotalSale: DiscountList size: " + mCurrentSalesDiscounts.size());
            for (int i = 0; i < mCurrentSalesDiscounts.size(); i++) {
                Calculator calculator = new Calculator(new BigDecimal(mCurrentSalesTotalAmount), new BigDecimal(mCurrentSalesDiscounts.get(i).getDiscountAmount()));
                mCurrentSalesTotalAmount = calculator.getDifference().toString();
                allDiscount.add(new BigDecimal(mCurrentSalesDiscounts.get(i).getDiscountAmount()));
            }
            mTotalDiscount = Calculator.addAllData(allDiscount).toString();
        } else {
            mTotalDiscount = "0";
        }
        mPreferences.edit().putString(KEY_CURRENT_SALES_TOTAL_DISCOUNT, mTotalDiscount).apply();
        mCurrentSalesTotalItems = Calculator.addAllData(totalItem).toString();

        Log.i(TAG, "total sales amount: " + mCurrentSalesTotalAmount + " total discount: " + mTotalDiscount);
        updateUI();
    }

    private void updateUI() {
        HomeFragment home = (HomeFragment) getSupportFragmentManager().findFragmentByTag("home");
        if (home != null) {
            home.mChargeButton.setText(getString(R.string.button_charge) + mCurrentSalesTotalAmount);

        }
        mCurrentSalesCountView.setText(mCurrentSalesTotalItems);
        if (mIsHome) {
            mToolbarTitleVIew.setText(mCurrentSalesTotalItems.equals("0") ? R.string.no_current_sale : R.string.have_current_sale);
        }
    }

    @Override
    public void onLibraryItemClickListener(Discount discount) {
        Log.i(TAG, "onLibraryItemClickListener");
        this.mCurrentSalesDiscounts.add(discount);
        calculateCurrentTotalSale(mCurrentSales);
    }

    private View.OnClickListener currentSalesButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mCurrentSales.size() == 0 || !mIsHome) return;
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(KEY_CURRENT_SALES, mCurrentSales);
            bundle.putString(KEY_CURRENT_SALES_TOTAL_DISCOUNT, mTotalDiscount);
            getSupportFragmentManager().beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .replace(R.id.fragment_container, CurrentSalesFragment.newInstance(bundle))
                    .addToBackStack(null).commit();
        }
    };


    @Override
    public void onCurrentSaleClicked(Sale sale) {
        String itemNameWithPrice = sale.getItemName() + " ₱" + sale.calculateItemTotalAmount();
        mCurrentItemPosition = sale.getId();
        Bundle itemData = new Bundle();
        itemData.putParcelable(KEY_SELECTED_SALES_ITEM_, sale);
        mToolbarTitleVIew.setText(itemNameWithPrice);
        getSupportFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.fragment_container, EditCurrentSalesItem.newInstance(itemData), EditCurrentSalesItem.class.getName())
                .addToBackStack(null).commit();
    }

    @Override
    public void onCurrentSaleAllDiscountsClick() {
        mToolbarTitleVIew.setText("Discounts -₱" + mTotalDiscount);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(KEY_CURRENT_SALES_DISCOUNTS, mCurrentSalesDiscounts);
        getSupportFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.fragment_container, CurrentSalesDiscountFragment.newInstance(bundle), CurrentSalesDiscountFragment.class.getName())
                .addToBackStack(null).commit();

    }

    @Override
    public void onHomeResume(boolean resumed) {
        if (resumed) {
            if (mCurrentSales.size() > 0) {
                if (mCurrentSales.get(mCurrentSales.size() - 1).getItemName().equals("") &&
                        mCurrentSales.get(mCurrentSales.size() - 1).getItemPrice().equals("0")) {
                    mCurrentSales.remove(mCurrentSales.size() - 1);
                }
            }
        }
    }

    @Override
    public void onFragmentStart(boolean showHomAsUp, boolean fromSaleActivity, String title) {
        Log.i(TAG, "onFragmentStart is " + showHomAsUp);
        mCurrentSalesCountView.setVisibility(fromSaleActivity ? View.VISIBLE : View.GONE);
        if (showHomAsUp) {
            mDrawerToggle.setDrawerIndicatorEnabled(false);
            mToolbarTitleVIew.setText(fromSaleActivity ? "Total: ₱" + mCurrentSalesTotalAmount : title);
        } else {
            mDrawerToggle.setDrawerIndicatorEnabled(true);
            mToolbarTitleVIew.setText(fromSaleActivity ? "Total: ₱" + mCurrentSalesTotalAmount : title);
            updateUI();
        }
    }

    @Override
    public void onEditDiscountClicked(Discount discount) {
        // TODO listener for list of discount inside EditCurrentSalesItem

    }

    @Override
    public void onEditCurrentSalesItemDistroy() {
        calculateCurrentTotalSale(mCurrentSales);
        mToolbarTitleVIew.setText("Total: ₱" + mCurrentSalesTotalAmount);

    }

    @Override
    public void onEditQuantityButtonClicked(int newQuantity) {
        mCurrentSales.get(mCurrentItemPosition).setItemQuantity(newQuantity);
        mToolbarTitleVIew.setText(mCurrentSales.get(mCurrentItemPosition).getItemName()
                + " ₱" + mCurrentSales.get(mCurrentItemPosition).calculateItemTotalAmount());
        calculateCurrentTotalSale(mCurrentSales);
    }

    @Override
    public void onEditCurrentSalesItemDeleted() {
        mCurrentSales.remove(mCurrentItemPosition);
        calculateCurrentTotalSale(mCurrentSales);
    }

    @Override
    public void updateCurrentItemNote(String note) {
        Log.i(TAG, "new Note: " + note);
        mCurrentSales.get(mCurrentItemPosition).setItemNote(note);
        calculateCurrentTotalSale(mCurrentSales);
    }

    @Override
    public void onCurrentDiscountRemove(final Discount discount, final int position) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Remove")
                .setMessage("Are you sure you will remove " + discount.getDiscountName() + "?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mCurrentSalesDiscounts.remove(position);
                        calculateCurrentTotalSale(mCurrentSales);
                        Fragment discountFragment = getSupportFragmentManager().findFragmentByTag(CurrentSalesDiscountFragment.class.getName());
                        getSupportFragmentManager().beginTransaction()
                                .detach(discountFragment)
                                .attach(discountFragment).commit();

                    }
                }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alert.show();
    }

    @Override
    public void onChargeButtonClicked() {
        int i = new BigDecimal(mCurrentSalesTotalAmount).compareTo(new BigDecimal(BigInteger.ZERO));
        if (i > 0) {
            mToolbarTitleVIew.setText("₱" + mCurrentSalesTotalAmount);
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(KEY_CURRENT_SALES, mCurrentSales);
            bundle.putParcelableArrayList(KEY_CURRENT_SALES_DISCOUNTS, mCurrentSalesDiscounts);
            bundle.putString(KEY_CURRENT_SALES_TOTAL_AMOUNT, mCurrentSalesTotalAmount);
            bundle.putString(KEY_CURRENT_SALES_TOTAL_DISCOUNT, mTotalDiscount);
            getSupportFragmentManager().beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .replace(R.id.fragment_container, ChargeFragment.newInstance(bundle), ChargeFragment.class.getName())
                    .addToBackStack(null).commit();
            onFragmentStart(true, true, null);
        } else {
            ToastMessage.message(this, "Charge amount should be greater than Zero(0).");
        }
    }

    @Override
    public void onChargeConfirmClicked(String amountRecieved) {
        hideSoftKeyboard();
        ChargeFragment chargeFragment = (ChargeFragment) getSupportFragmentManager().findFragmentByTag(ChargeFragment.class.getName());
        boolean empty = amountRecieved.isEmpty();
        String subtitle;
        if (!empty) {
            int isAcceptable = new BigDecimal(amountRecieved).compareTo(new BigDecimal(mCurrentSalesTotalAmount));
            if (isAcceptable < 0) {
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle(R.string.app_name)
                        .setMessage("Received amount is less than payable amount.")
                        .setPositiveButton("OK", null);
                alert.show();
                return;
            }
            String change = Calculator.deductAmount(new BigDecimal(amountRecieved), new BigDecimal(mCurrentSalesTotalAmount)).toString();
            subtitle = "₱" + change + " change out of ₱" + amountRecieved;
            // add sales to our web server

        } else {
            subtitle = "No change out of ₱" + mCurrentSalesTotalAmount;
        }
        mTransactionIsOnGoing = true;
        chargeFragment.mDoneTitle.setText("Thank You!");
        chargeFragment.mDoneSubtitle.setText(subtitle);
        chargeFragment.paymentDone();
        mToolbarTitleVIew.setText("Charged");
        mCurrentSalesCountView.setText("0");
        // TODO save all sales data to db;
        addSalesToServer(mCurrentSales, mCurrentSalesTotalAmount, mCurrentSalesDiscounts);

    }

    private void hideSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public void onNewSaleClicked() {
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        mTransactionIsOnGoing = false;
        onFragmentStart(false, true, null);
        calculateCurrentTotalSale(mCurrentSales);
    }

    @Override
    public void onAddCustomItem(String note, String price) {
        Sale customItem = new Sale();
        customItem.setItemName("Custom Item");
        customItem.setItemNote(note);
        customItem.setItemQuantity(1);
        customItem.setItemPrice(price);
        if (mCurrentSales.size() > 1) {
            Sale lastSaleItem = mCurrentSales.get(mCurrentSales.size() - 2);
            if (lastSaleItem.toString().equals(customItem.toString())) {
                lastSaleItem.setItemQuantity(lastSaleItem.getItemQuantity() + 1);
            } else {
                //mCurrentSales.remove(mCurrentSales.size() - 1);
                mCurrentSales.add(customItem);
                //mCurrentSales.add(dummySaleForDiscount);
            }
        } else {
            mCurrentSales.add(customItem);
            //mCurrentSales.add(dummySaleForDiscount);
        }
        calculateCurrentTotalSale(mCurrentSales);
    }


    private void addSalesToServer(final ArrayList<Sale> mCurrentSales, final String mCurrentSalesTotalAmount, final ArrayList<Discount> mCurrentSalesDiscounts) {

        new AsyncTask<Void, Void, JSONArray[]>() {
            final String REQUEST_TAG = "add_sales_to_server";
            final JSONArray jsonArraySales = new JSONArray();
            final JSONArray jsonArrayDiscounts = new JSONArray();
            final JSONObject jsonObject = new JSONObject();
            final ArrayList<Discount> finalMCurrentSalesDiscounts = mCurrentSalesDiscounts;

            @Override
            protected JSONArray[] doInBackground(Void... params) {
                Log.i(TAG, "doInBackground is called..");
                JSONArray[] jsonArray = new JSONArray[2];
                int salesSize = mCurrentSales.size();
                if (mCurrentSales.get(salesSize - 1).getItemPrice().equals("0")) {
                    mCurrentSales.remove(salesSize - 1);
                }
                salesSize = mCurrentSales.size();
                for (int i = 0; i < salesSize; i++) {
                    jsonArraySales.put(mCurrentSales.get(i).getJSONObject());
                }
                for (int i = 0; i < finalMCurrentSalesDiscounts.size(); i++) {
                    jsonArrayDiscounts.put(finalMCurrentSalesDiscounts.get(i).getJsonObject());
                }

                jsonArray[0] = jsonArraySales;
                jsonArray[1] = jsonArrayDiscounts;
                return jsonArray;
            }

            @Override
            protected void onPostExecute(JSONArray[] jsonArrays) {
                Log.i(TAG, "onPostExecute.. is called");
                try {
                    jsonObject.put("id", USER_ID);
                    jsonObject.put("time", System.currentTimeMillis());
                    jsonObject.put("sales_total_amount", mCurrentSalesTotalAmount);
                    jsonObject.put("sold_items", jsonArrays[0]);
                    jsonObject.put("sold_discounts", jsonArrays[1]);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.i(TAG, "JsonObject: " + jsonObject.toString());
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                        LoginActivity.SERVER_URL, jsonObject, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        boolean error = true;
                        try {
                            Log.i(TAG, "RESPONSE FROM SERVER:\n" + response.toString());
                            error = response.getBoolean("error");
                        } catch (JSONException e) {
                            VolleyLog.e(TAG, e);
                        }
                        if (error) {
                            ToastMessage.message(MainActivity.this, "Failed sending data to server.");
                        } else {
                            // TODO if saving to server is successfull

                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        // TODO do something if we received error
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("Accept", "application/json");
                        params.put("Content-Type", "application/json");
                        return params;
                    }
                };
                jsonObjectRequest.setRetryPolicy(new RetryPolicy() {
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
                // Add request to our queue
                PimaApplication.getmInstance().addToReqQueue(jsonObjectRequest, REQUEST_TAG);
            }
        }.execute();

        // after passing to server reset all variable for new sale
        this.mCurrentSalesDiscounts = new ArrayList<>();
        this.mCurrentSales = new ArrayList<>();
        this.mCurrentItemPosition = 0;
        this.mCurrentSalesTotalItems = "0";
        this.mTotalDiscount = "0";
        this.mCurrentSalesTotalAmount = "0";
    }

    @Override
    public void onHistoryItemClicked(History history) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(HistoryFragment.KEY_HISTORY, history);
        getSupportFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .add(R.id.fragment_container, HistoryItemViewFragment.newInstance(bundle))
                .addToBackStack(null).commit();
        onFragmentStart(true, false, "₱" + history.getTotalAmount() + " Sale");
    }


}
