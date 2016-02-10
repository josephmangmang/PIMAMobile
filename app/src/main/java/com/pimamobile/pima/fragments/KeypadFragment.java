package com.pimamobile.pima.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.pimamobile.pima.R;
import com.pimamobile.pima.utils.FragmentInterface;
import com.pimamobile.pima.utils.ToastMessage;

public class KeypadFragment extends Fragment {

    private static final String TAG = "KeypadFragment";
    private FragmentInterface mListener;
    private EditText mAddNote;
    private TextView mPrice;
    private TextView mKey1;
    private TextView mKey2;
    private TextView mKey3;
    private TextView mKey4;
    private TextView mKey5;
    private TextView mKey6;
    private TextView mKey7;
    private TextView mKey8;
    private TextView mKey9;
    private TextView mKey0;
    private TextView mKeyAdd;
    private TextView mKeyCancel;
    private String userInput = "";

    public static KeypadFragment newInstance() {
        KeypadFragment keypadFragment = new KeypadFragment();

        return keypadFragment;
    }

    public KeypadFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView is called");
        View view = inflater.inflate(R.layout.keypad_fragment, container, false);
        mAddNote = (EditText) view.findViewById(R.id.keypad_add_note);
        mPrice = (TextView) view.findViewById(R.id.keypad_price);
        mKeyAdd = (TextView) view.findViewById(R.id.key_add);
        mKeyCancel = (TextView) view.findViewById(R.id.key_c);
        mKey0 = (TextView) view.findViewById(R.id.key_0);
        mKey1 = (TextView) view.findViewById(R.id.key_1);
        mKey2 = (TextView) view.findViewById(R.id.key_2);
        mKey3 = (TextView) view.findViewById(R.id.key_3);
        mKey4 = (TextView) view.findViewById(R.id.key_4);
        mKey5 = (TextView) view.findViewById(R.id.key_5);
        mKey6 = (TextView) view.findViewById(R.id.key_6);
        mKey7 = (TextView) view.findViewById(R.id.key_7);
        mKey8 = (TextView) view.findViewById(R.id.key_8);
        mKey9 = (TextView) view.findViewById(R.id.key_9);
        mKey0 = (TextView) view.findViewById(R.id.key_0);


        mKey0.setOnClickListener(keyListener);
        mKey1.setOnClickListener(keyListener);
        mKey2.setOnClickListener(keyListener);
        mKey3.setOnClickListener(keyListener);
        mKey4.setOnClickListener(keyListener);
        mKey5.setOnClickListener(keyListener);
        mKey6.setOnClickListener(keyListener);
        mKey7.setOnClickListener(keyListener);
        mKey8.setOnClickListener(keyListener);
        mKey9.setOnClickListener(keyListener);
        mKeyAdd.setOnClickListener(keyAddListener);
        mKeyCancel.setOnClickListener(keyCancelListener);

        return view;
    }

    private View.OnClickListener keyListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String n = "";

            switch (v.getId()) {
                case R.id.key_0:
                    n = "0";
                    break;
                case R.id.key_1:
                    n = "1";
                    break;
                case R.id.key_2:
                    n = "2";
                    break;
                case R.id.key_3:
                    n = "3";
                    break;
                case R.id.key_4:
                    n = "4";
                    break;
                case R.id.key_5:
                    n = "5";
                    break;
                case R.id.key_6:
                    n = "6";
                    break;
                case R.id.key_7:
                    n = "7";
                    break;
                case R.id.key_8:
                    n = "8";
                    break;
                case R.id.key_9:
                    n = "9";
                    break;


            }
            Log.i(TAG, n);
            userInput = userInput + n;
            addInputToTextView(userInput);
        }
    };

    private void addInputToTextView(String userInput) {
        String price = "0.00";
        int length = userInput.length();
        if (length < 3) {
            price = "₱" + price.substring(0, 4 - length) + userInput;
        } else if (length == 3) {
            price = "₱" + userInput.substring(0, 4 - length) + "." + userInput.substring(4 - length);
        } else {
            price = "₱" + userInput.substring(0, length - 2) + "." + userInput.substring(length - 2);
        }
        mPrice.setText(price);
    }

    private View.OnClickListener keyAddListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String price = mPrice.getText().toString().substring(1);
            String noteName = mAddNote.getText().toString();
            boolean cancel = false;
            View focus = null;
            if (Double.parseDouble(price) == 0) {
                ToastMessage.message(v.getContext(), "Your price is zero(0)");
                cancel = true;
                focus = mPrice;
            } else if (TextUtils.isEmpty(noteName)) {
                mAddNote.setError(getString(R.string.error_field_empty));
                cancel = true;
                focus = mAddNote;
            }

            if (cancel) {

                focus.requestFocus();

            } else {
                Log.i(TAG, "Note name: " + mAddNote.getText().toString() + " Price: " + price);
                // TODO add custom item to mCurrentSales
                mListener.onAddCustomItem(mAddNote.getText().toString(), price);
                keyCancelListener.onClick(null);
            }

        }
    };
    private View.OnClickListener keyCancelListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            userInput = "";
            mPrice.setText(R.string.default_amountValue);
            mAddNote.setText("");
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentInterface) {
            mListener = (FragmentInterface) context;
        } else {
            throw new RuntimeException(context.toString() +
                    " must implement FragmentInterface");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
/*
    public interface OnKeypadFragmentInteraction {
        void onAddCustomItem(String note, String price);
    }
    */
}
