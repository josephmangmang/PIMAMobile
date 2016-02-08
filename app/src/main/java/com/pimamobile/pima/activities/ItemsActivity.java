package com.pimamobile.pima.activities;


import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.view.MenuItem;
import android.view.View;


import com.pimamobile.pima.R;
import com.pimamobile.pima.fragments.AllCategoryFragment;
import com.pimamobile.pima.fragments.AllDiscountFragment;
import com.pimamobile.pima.fragments.AllItemFragment;
import com.pimamobile.pima.fragments.ItemFragment;
import com.pimamobile.pima.models.Category;
import com.pimamobile.pima.models.Discount;
import com.pimamobile.pima.models.Item;

public class ItemsActivity extends AppCompatActivity implements ItemFragment.OnItemsInteractionListener,
        AllItemFragment.OnItemListener, AllCategoryFragment.OnCategoryClickListener, AllDiscountFragment.OnDiscountListener {
    public static final String ITEM_NAME = "item_name";
    public static final String ITEM_CATEGORY = "item_category";
    public static final String ITEM_PRICE = "item_price";
    public static final String ITEM_ID = "item_id";
    public static final String CATEGORY_NAME = "category_name";
    public static final String ITEM_POSITION = "item_position";

    public static final String DISCOUNT_NAME = "discount_name";
    public static final String DISCOUNT_AMOUNT = "discount_amount";
    public static final String DISCOUNT_IS_PERCENTAGE = "discount_is_percentage";

    public static final String TO_ACTIVITY = "to_activity";
    public static final int CREATE_ITEM = 30;
    public static final int CREATE_CATEGORY = 31;
    public static final int CREATE_DISCOUNT = 32;
    public static final String EDIT_ITEM = "edit_item";
    private static final String TAG = "ItemsActivity";

    private Fragment listFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, ItemFragment.newInstance())
                .commitAllowingStateLoss();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }


    @Override
    public void onItemsFragmentInteraction(int position) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        switch (position) {
            case 0:

                // response if All Item is click
                listFragment = AllItemFragment.newInstance();
                getSupportActionBar().setTitle(AllItemFragment.TITLE);
                fragmentTransaction
                        .replace(R.id.fragment_container, listFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .addToBackStack(null)
                        .commitAllowingStateLoss();
                break;
            case 1:
                // Categories
                listFragment = AllCategoryFragment.newInstance();
                getSupportActionBar().setTitle(AllCategoryFragment.TITLE);
                fragmentTransaction
                        .replace(R.id.fragment_container, listFragment)
                        .addToBackStack(null)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commitAllowingStateLoss();
                break;
            case 2:
                // Discounts
                listFragment = AllDiscountFragment.newInstance();
                getSupportActionBar().setTitle(AllDiscountFragment.TITLE);
                fragmentTransaction
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .replace(R.id.fragment_container, listFragment)
                        .addToBackStack(null)
                        .commitAllowingStateLoss();
                break;

        }

    }

    @Override
    public void onItemClicked(Item item) {
        Intent intent = new Intent(this, CreateActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean(EDIT_ITEM, true);
        bundle.putInt(TO_ACTIVITY, CREATE_ITEM);
        bundle.putInt(ITEM_POSITION, item.getPosition());
        bundle.putInt(ITEM_ID, item.getId());
        bundle.putString(ITEM_NAME, item.getItemName());
        bundle.putString(ITEM_CATEGORY, item.getItemCategory());
        bundle.putString(ITEM_PRICE, item.getItemPrice());
        intent.putExtras(bundle);
        startActivity(intent);

    }

    @Override
    public void onCategoryClicked(Category category) {
        Intent intent = new Intent(this, CreateActivity.class);
        intent.putExtra(EDIT_ITEM, true);
        intent.putExtra(TO_ACTIVITY, CREATE_CATEGORY);
        intent.putExtra(CATEGORY_NAME, category.getCategoryName());
        startActivity(intent);
    }

    @Override
    public void onDiscountClicked(Discount discount) {
        Intent intent = new Intent(this, CreateActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean(EDIT_ITEM, true);
        bundle.putInt(TO_ACTIVITY, CREATE_DISCOUNT);
        bundle.putString(DISCOUNT_NAME, discount.getDiscountName());
        bundle.putString(DISCOUNT_AMOUNT, discount.getDiscountAmount());
        bundle.putBoolean(DISCOUNT_IS_PERCENTAGE, discount.isPercentage());
        intent.putExtras(bundle);
        startActivity(intent);

    }

    public void createItem(View view) {
        Intent intent = new Intent(this, CreateActivity.class);
        intent.putExtra(EDIT_ITEM, false);
        intent.putExtra(TO_ACTIVITY, CREATE_ITEM);
        startActivity(intent);
    }

    public void createCategory(View view) {
        Intent intent = new Intent(this, CreateActivity.class);
        intent.putExtra(EDIT_ITEM, false);
        intent.putExtra(TO_ACTIVITY, CREATE_CATEGORY);
        startActivity(intent);
    }

}
