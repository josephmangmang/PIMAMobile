package com.pimamobile.pima.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.pimamobile.pima.R;
import com.pimamobile.pima.activities.ItemsActivity;
import com.pimamobile.pima.models.Discount;
import com.pimamobile.pima.utils.DecimalDigitsInputFilter;
import com.pimamobile.pima.utils.SQLiteHelper;
import com.pimamobile.pima.utils.ToastMessage;

import java.math.BigDecimal;

public class CreateDiscountFragment extends Fragment {

    private static final String TAG = "CreateDiscountFragment";
    private EditText mDiscountName;
    private EditText mDiscountAmount;

    private SQLiteHelper mSqLiteHelper;
    private String discountName;
    private String discountAmount;
    private boolean mHasArgument;
    private boolean discountIsPercentage;

    private SwitchCompat mDiscountPercentage;
    private Button mSaveButton;
    private Button mDeleteButton;
    private TextView mSymbol;

    public static CreateDiscountFragment newInstance(Bundle bundle) {
        CreateDiscountFragment fragment = new CreateDiscountFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public CreateDiscountFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSqLiteHelper = new SQLiteHelper(getActivity());
        mHasArgument = getArguments() != null ? true : false;

        if (mHasArgument) {
            discountName = getArguments().getString(ItemsActivity.DISCOUNT_NAME);
            discountAmount = getArguments().getString(ItemsActivity.DISCOUNT_AMOUNT);
            discountIsPercentage = getArguments().getBoolean(ItemsActivity.DISCOUNT_IS_PERCENTAGE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_discount, container, false);
        mDiscountName = (EditText) view.findViewById(R.id.createDiscount_name);
        mDiscountAmount = (EditText) view.findViewById(R.id.createDiscount_amount);
        mDiscountPercentage = (SwitchCompat) view.findViewById(R.id.createDiscount_switch);
        mSaveButton = (Button) view.findViewById(R.id.createDiscount_save);
        mDeleteButton = (Button) view.findViewById(R.id.createDiscount_delete);
        mSymbol = (TextView) view.findViewById(R.id.createDiscount_symbol);

        //mDiscountAmount.setKeyListener(priceKeyListener);
        mDiscountPercentage.setOnCheckedChangeListener(percentageCheckListener);
        mSaveButton.setOnClickListener(saveButtonListener);
        mDeleteButton.setOnClickListener(deleteButtonListener);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mHasArgument) {
            mDiscountName.setText(discountName);
            mDiscountAmount.setText(discountAmount);
            mDiscountPercentage.setChecked(discountIsPercentage);
            mDeleteButton.setVisibility(View.VISIBLE);
            mSaveButton.setText("UPDATE");
        }
        if (mDiscountPercentage.isChecked()) {
            // use percentage
             mDiscountAmount.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(3, 2)});
            mSymbol.setText("%");
        } else {
            // use peso
             mDiscountAmount.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(9, 2)});
            mSymbol.setText(R.string.peso_symbol);
        }
    }

    private boolean mInPeso = true;
    private CompoundButton.OnCheckedChangeListener percentageCheckListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                mInPeso = false;
                mSymbol.setText("%");

            } else {
                mInPeso = true;
                mSymbol.setText(R.string.peso_symbol);
            }

            mDiscountName.setText("");
            mDiscountAmount.setText("0.00");
            userInput = "";
        }
    };

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

    private String formatInputToPercentage(String userInput) {
        String converted;
        if (userInput.contains(".")) {
            String amountAfterDecimal = userInput.substring(userInput.indexOf(".") + 1);
            int i = new BigDecimal(userInput).compareTo(new BigDecimal("100"));
            if (i < 0) {
                return userInput;
            } else {
                return "100";
            }
        } else {
            int i = new BigDecimal(userInput).compareTo(new BigDecimal("100"));
            if (i < 0) {
                return userInput;
            }
            return "100";
        }
    }

    private View.OnClickListener saveButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String name = mDiscountName.getText().toString();
            String amount = mDiscountAmount.getText().toString();
            boolean isPercent = mDiscountPercentage.isChecked();
            amount = amount.isEmpty() ? "0" : amount;
            boolean cancel = false;
            View focus = null;

            if (isPercent) {
                int i = new BigDecimal(userInput).compareTo(new BigDecimal("100"));
                if (i > 0) {
                    amount = "100";
                }
            } else {
                amount = convertInputNumberToDecimal(amount);
            }
            if (TextUtils.isEmpty(name)) {
                cancel = true;
                mDiscountName.setError(getString(R.string.error_field_empty));
                focus = mDiscountName;
            } else if (Double.parseDouble(amount) == 0) {
                cancel = true;
                mDiscountAmount.setError(getString(R.string.error_field_empty));
                focus = mDiscountAmount;
            }

            if (cancel) {
                focus.requestFocus();
            } else {
                Discount discount = new Discount();
                discount.setDiscountName(name);
                discount.setDiscountAmount(amount);
                discount.setIsPercentage(isPercent);
                if (mHasArgument) {
                    // update only
                    Discount oldDiscount = new Discount(discountName, discountAmount, discountIsPercentage);
                    int i = mSqLiteHelper.updateDiscount(oldDiscount, discount);
                    if (i > 0) {
                        ToastMessage.message(getActivity(), name + " successfully updated");
                        discountName = name;
                        discountAmount = amount;
                        discountIsPercentage = isPercent;
                        getActivity().finish();
                    } else {
                        ToastMessage.message(getActivity(), name + " cannot be update. Not found.");
                    }
                } else {
                    // create
                    long i = mSqLiteHelper.createDiscount(discount);
                    if (i < 0) {
                        ToastMessage.message(getActivity(), "Error inserting " + name);
                    } else {
                        ToastMessage.message(getActivity(), name + " was successfully added");
                        clearInputData();
                    }
                }
            }
        }
    };
    private String userInput = "";
    private KeyListener priceKeyListener = new KeyListener() {
        @Override
        public int getInputType() {
            return 0;
        }

        @Override
        public boolean onKeyDown(View view, Editable text, int keyCode, KeyEvent event) {
            return false;
        }

        @Override
        public boolean onKeyUp(View view, Editable text, int keyCode, KeyEvent event) {
            short number = -1;
            if (keyCode > 6 && keyCode < 17) {
                number = (short) (keyCode - 7);
                userInput = userInput + number;
                addInputToTextView(userInput);

            } else if (keyCode == 67) {
                // backspace
                try {
                    userInput = userInput.substring(0, userInput.length() - 1);
                    addInputToTextView(userInput);
                } catch (StringIndexOutOfBoundsException e) {

                }
            }
            return false;
        }

        @Override
        public boolean onKeyOther(View view, Editable text, KeyEvent event) {
            return false;
        }

        @Override
        public void clearMetaKeyState(View view, Editable content, int states) {

        }
    };

    private void addInputToTextView(String userInput) {
        String price = "0.00";

        int length = userInput.length();
        if (length < 3) {
            price = price.substring(0, 4 - length) + userInput;
        } else if (length == 3) {
            price = userInput.substring(0, 4 - length) + "." + userInput.substring(4 - length);
        } else {
            price = userInput.substring(0, length - 2) + "." + userInput.substring(length - 2);
        }

        if (!mInPeso && userInput.length() > 2) {
            if (Double.parseDouble(userInput.substring(userInput.length() - 1)) > 100) {
                userInput = "100.00";
                price = userInput;
            }
        }
        mDiscountAmount.setText(price);
    }

    private void clearInputData() {
        mDiscountName.setText("");
        mDiscountAmount.setText("0.00");
        mDiscountPercentage.setChecked(false);
        userInput = "";
    }

    private View.OnClickListener deleteButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
            alert.setTitle(R.string.delete_title)
                    .setMessage("Are you sure you want to delete " + discountName)
                    .setPositiveButton(R.string.alert_button_yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.i(TAG, discountName + " " + discountAmount + " " + discountIsPercentage);
                            Discount discount = new Discount();
                            discount.setDiscountName(discountName);
                            discount.setIsPercentage(discountIsPercentage);
                            discount.setDiscountAmount(discountAmount);
                            int i = mSqLiteHelper.deleteDiscount(discount);
                            if (i > 0) {
                                ToastMessage.message(getActivity(), discountName + " successfully deleted");
                                getActivity().finish();
                            } else {
                                ToastMessage.message(getActivity(), "Cannot delete " + discountName + ". No data found.");
                            }
                        }
                    })
                    .setNegativeButton(R.string.alert_button_no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // nothing to do
                        }
                    });
            alert.show();
        }
    };

}
