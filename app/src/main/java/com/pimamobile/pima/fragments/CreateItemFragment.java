package com.pimamobile.pima.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.KeyEventCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatSpinner;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import com.pimamobile.pima.R;
import com.pimamobile.pima.activities.ItemsActivity;
import com.pimamobile.pima.models.Category;
import com.pimamobile.pima.models.Item;
import com.pimamobile.pima.utils.DecimalDigitsInputFilter;
import com.pimamobile.pima.utils.SQLiteHelper;
import com.pimamobile.pima.utils.ToastMessage;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CreateItemFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "CreateItemFragment";


    private EditText mItemName;
    private EditText mItemPrice;
    private Button mSaveItem;
    private Button mDeleteItem;
    private Context mContext;

    private String category = "";
    private AppCompatSpinner mSpinner;
    private SQLiteHelper mSqlHelper;
    private boolean mHasArguments;

    private String mName;
    private String mCategory;
    private String mPrice;
    private int mId;
    List<String> categories;


    public CreateItemFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CreateItemFragment.
     */

    public static CreateItemFragment newInstance(Bundle bundle) {
        CreateItemFragment fragment = new CreateItemFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        mSqlHelper = new SQLiteHelper(mContext);
        mHasArguments = getArguments() != null ? true : false;
        if (mHasArguments) {
            mName = getArguments().getString(ItemsActivity.ITEM_NAME);
            mCategory = getArguments().getString(ItemsActivity.ITEM_CATEGORY);
            mPrice = getArguments().getString(ItemsActivity.ITEM_PRICE);
            mId = getArguments().getInt(ItemsActivity.ITEM_ID);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_item, container, false);

        mItemName = (EditText) view.findViewById(R.id.createItem_name);
        mItemPrice = (EditText) view.findViewById(R.id.createItem_price);
        mSaveItem = (Button) view.findViewById(R.id.createItem_save);
        mDeleteItem = (Button) view.findViewById(R.id.createItem_delete);
        mSpinner = (AppCompatSpinner) view.findViewById(R.id.createItem_category);

        // mItemPrice.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(9, 2)});
        //mItemPrice.setOnKeyListener(priceKeyListener);

        //mItemPrice.setKeyListener(priceKeyListener);
        mItemPrice.setOnFocusChangeListener(priceFocusListener);
        mSaveItem.setOnClickListener(saveButtonListener);
        mDeleteItem.setOnClickListener(deleteButtonListener);
        setUpSpinner(mSpinner);
        return view;
    }

    private void setUpSpinner(AppCompatSpinner mSpinner) {
        categories = new ArrayList<>();
        List<Category> categoryList = mSqlHelper.getAllCategory();

        categories.add("None");
        for (int i = 0; i < categoryList.size(); i++) {
            String temp = categoryList.get(i).getCategoryName();
            String name = temp.substring(0, 1).toUpperCase() + temp.substring(1);
            categories.add(name);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext,
                android.R.layout.simple_selectable_list_item, categories);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setPrompt("Select Category");
        mSpinner.setOnItemSelectedListener(this);
        mSpinner.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mHasArguments) {
            mItemName.setText(mName);
            mItemPrice.setText("₱" + mPrice);
            mSpinner.setSelection(categories.indexOf(mCategory), true);
            mDeleteItem.setVisibility(View.VISIBLE);
            mSaveItem.setText("UPDATE");
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        category = mSpinner.getItemAtPosition(position).toString();
        Log.i(TAG, "onItemSelected: " + category);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //mSpinner.setSelection(0);
        Log.i(TAG, "onNothingSelected");
        category = "";
    }


    private View.OnClickListener saveButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String name = mItemName.getText().toString();
            String price =  mItemPrice.getText().toString();
            price = price.isEmpty() ? "0" : convertInputNumberToDecimal(price);
            boolean cancel = false;
            View focus = null;
            if (TextUtils.isEmpty(name)) {
                mItemName.setError(getString(R.string.error_field_empty));
                cancel = true;
                focus = mItemName;
            } else if (Double.parseDouble(price) == 0) {
                mItemPrice.setError(getString(R.string.error_field_empty));
                cancel = true;
                focus = mItemPrice;
            }

            if (cancel) {
                focus.requestFocus();
            } else {

                Log.w(TAG, "name: " + name + " category: " + category + " price: " + price);
                Item item = new Item(name, category, price);
                if (mHasArguments) {
                    Item oldItem = new Item(mId, mName, mCategory, mPrice);
                    int i = mSqlHelper.updateItem(oldItem, item);
                    if (i < 0) {
                        ToastMessage.message(mContext, "Sorry error inserting item. Try again.");
                    } else {
                        ToastMessage.message(mContext, name + " was successfully updated.");
                        getActivity().finish();
                    }
                } else {
                    long id = mSqlHelper.createItem(item);
                    if (id < 0) {
                        ToastMessage.message(mContext, "Sorry error inserting item. Try again.");
                    } else {
                        ToastMessage.message(mContext, name + " was successfully added.");
                        clearInputData();
                    }
                }
            }
        }
    };
    private String userInput = "";

    /**
     * private KeyListener priceKeyListener = new KeyListener() {
     *
     * @Override public int getInputType() {
     * return 0;
     * }
     * @Override public boolean onKeyDown(View view, Editable text, int keyCode, KeyEvent event) {
     * return false;
     * }
     * @Override public boolean onKeyUp(View view, Editable text, int keyCode, KeyEvent event) {
     * short number = -1;
     * if (keyCode > 6 && keyCode < 17) {
     * number = (short) (keyCode - 7);
     * userInput = userInput + number;
     * addInputToTextView(userInput);
     * } else if (keyCode == 67) {
     * // backspace
     * try {
     * userInput = userInput.substring(0, userInput.length() - 1);
     * addInputToTextView(userInput);
     * } catch (StringIndexOutOfBoundsException e) {
     * <p/>
     * }
     * }
     * return true;
     * }
     * @Override public boolean onKeyOther(View view, Editable text, KeyEvent event) {
     * return false;
     * }
     * @Override public void clearMetaKeyState(View view, Editable content, int states) {
     * <p/>
     * }
     * };
     */
    private String addInputToTextView(String userInput) {
        String price = "0.00";
        int length = userInput.length();
        if (length < 3) {
            price = "₱" + price.substring(0, 4 - length) + userInput;
        } else if (length == 3) {
            price = "₱" + userInput.substring(0, 4 - length) + "." + userInput.substring(4 - length);
        } else {
            price = "₱" + userInput.substring(0, length - 2) + "." + userInput.substring(length - 2);
        }
        return price;
    }

    private String convertInputNumberToDecimal(String userInput) {
        if (userInput.contains(".") && !userInput.endsWith(".")) {
            // 123.45
            int pointPosition = userInput.indexOf("."); // 3
            String fromPointPosition = userInput.substring(pointPosition + 1); // .45
            Log.i(TAG, "pointPosition: " + pointPosition + " fromPointPosition: " + fromPointPosition);
            if (fromPointPosition.length() == 1) {
                userInput = userInput + "0";
            }
        } else if (userInput.endsWith(".")) {
            // no point need to add.
            userInput = userInput + "00";
        } else {
            userInput = userInput + ".00";
        }
        Log.i(TAG, "userinput converted: " + userInput);
        return userInput;
    }

    private void clearInputData() {
        mItemName.setText("");
        mItemPrice.setText("");
        mSpinner.setSelection(0);
        userInput = "";
    }

    private View.OnClickListener deleteButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final String name = mItemName.getText().toString();
            final String category = mCategory;
            final String price = mItemPrice.getText().toString().substring(1);

            final Item item = new Item(
                    name,
                    category,
                    price
            );
            Log.w(TAG, "name: " + name + " category: " + category + " price: " + price);

            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
            alert.setMessage(getString(R.string.alert_delete_message) + " " + name + "?")
                    .setPositiveButton(R.string.alert_button_yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int i = mSqlHelper.deleteItem(item);
                            if (i > 0) {
                                ToastMessage.message(getActivity(), name + " was successfully deleted");
                            } else {
                                ToastMessage.message(getActivity(), "Unable to delete " + name + ". No data found.");
                            }
                            clearInputData();
                            getActivity().onBackPressed();
                        }
                    })
                    .setNegativeButton(R.string.alert_button_no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

            alert.show();
        }
    };


    private View.OnFocusChangeListener priceFocusListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                mItemPrice.setHint(R.string.default_amountValue);
            } else {
                mItemPrice.setHint(R.string.price_hint);
            }
        }
    };


}
