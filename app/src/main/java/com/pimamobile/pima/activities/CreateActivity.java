package com.pimamobile.pima.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.pimamobile.pima.R;
import com.pimamobile.pima.fragments.CreateCategoryFragment;
import com.pimamobile.pima.fragments.CreateDiscountFragment;
import com.pimamobile.pima.fragments.CreateItemFragment;

public class CreateActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        inflateFragment(savedInstanceState);
    }

    protected void inflateFragment(Bundle savedInstanceState) {
        boolean editItem = getIntent().getBooleanExtra(ItemsActivity.EDIT_ITEM, false);
        int distination = getIntent().getIntExtra(ItemsActivity.TO_ACTIVITY, -1);
        String title = "Create "; // + title name
        Bundle bundle = null;
        if (editItem) {
            bundle = getIntent().getExtras();
            title = "Update ";
        }

        switch (distination) {
            case ItemsActivity.CREATE_ITEM:
                getSupportActionBar().setTitle(title + "Item");
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, CreateItemFragment.newInstance(
                                bundle)).commitAllowingStateLoss();
                break;
            case ItemsActivity.CREATE_CATEGORY:
                getSupportActionBar().setTitle(title + "Category");
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, CreateCategoryFragment.newInstance(
                                bundle)).commitAllowingStateLoss();
                break;
            case ItemsActivity.CREATE_DISCOUNT:
                getSupportActionBar().setTitle(title + "Discount");
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, CreateDiscountFragment.newInstance(
                                bundle)).commitAllowingStateLoss();
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }
}
