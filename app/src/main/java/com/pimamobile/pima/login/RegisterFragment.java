package com.pimamobile.pima.login;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pimamobile.pima.LoginActivity;
import com.pimamobile.pima.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterFragment extends Fragment {
    private static final String REQUEST = "request";
    private static final String NAME = "name";
    private static final String ADDRESS = "address";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email";
    private static final String TAG = "RegisterFragment";
    private LoginInterface mListener;
    private EditText mBranchName;
    private EditText mBranchAddress;
    private EditText mBranchEmail;
    private EditText mBranchPassword;
    private Button mRegisterButton;
    private AppCompatCheckBox mShowPlainPassword;
    private ProgressBar mProgressBar;

    private CompoundButton.OnCheckedChangeListener checkChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                mBranchPassword.setInputType(InputType.TYPE_CLASS_TEXT);
            } else {
                mBranchPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
        }
    };
    private View.OnClickListener registerButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String name = mBranchName.getText().toString().toLowerCase();
            String address = mBranchAddress.getText().toString().toLowerCase();
            String email = mBranchEmail.getText().toString();
            String password = mBranchPassword.getText().toString();
            View focus = null;
            boolean cancel = false;

            if (TextUtils.isEmpty(name)) {
                mBranchName.setError(getString(R.string.error_field_required));
                focus = mBranchName;
                cancel = true;
            } else if (TextUtils.isEmpty(address)) {
                mBranchAddress.setError(getString(R.string.error_field_required));
                focus = mBranchAddress;
                cancel = true;
            } else if (TextUtils.isEmpty(email)) {
                mBranchEmail.setError(getString(R.string.error_field_required));
                focus = mBranchEmail;
                cancel = true;
            } else if (TextUtils.isEmpty(password)) {
                mBranchPassword.setError(getString(R.string.error_field_required));
                focus = mBranchPassword;
                cancel = true;
            } else if (name.contains(" ")) {
                mBranchName.setError(getString(R.string.error_branch_name_has_space));
                focus = mBranchName;
                cancel = true;
            }

            if (cancel) {
                focus.requestFocus();
            } else {
                showProgress(true);
                registerBranch(name, address, password, email);
            }
        }
    };

    public static RegisterFragment newInstance() {
        return new RegisterFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_branch, container, false);
        mBranchName = (EditText) view.findViewById(R.id.register_brach_name);
        mBranchAddress = (EditText) view.findViewById(R.id.register_address);
        mBranchEmail = (EditText) view.findViewById(R.id.register_brach_email);
        mBranchPassword = (EditText) view.findViewById(R.id.register_password);
        mShowPlainPassword = (AppCompatCheckBox) view.findViewById(R.id.register_show_password);
        mRegisterButton = (Button) view.findViewById(R.id.register_button);
        mProgressBar = (ProgressBar) view.findViewById(R.id.register_progress);

        mRegisterButton.setOnClickListener(registerButtonListener);
        mShowPlainPassword.setOnCheckedChangeListener(checkChangeListener);
        mListener.updateShowHomeAsUp(true);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof LoginInterface) {
            mListener = (LoginInterface) context;
        } else {
            throw new RuntimeException(context.toString() +
                    " must implement LoginInterface");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener.updateShowHomeAsUp(false);
        mListener = null;
    }

    private void registerBranch(final String name, final String address, final String password, final String email) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, LoginActivity.SERVER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        showProgress(false);
                        boolean error = true;
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Log.i(TAG, jsonObject.toString());
                            error = jsonObject.getBoolean("error");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (error) {
                            alert.setTitle("Error")
                                    .setMessage(R.string.register_error_branch_exist)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    });
                            alert.show();
                        } else {
                            alert.setTitle("Success!")
                                    .setMessage(R.string.register_success)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            mListener.onRegisterSuccessfull();
                                        }
                                    });
                            alert.show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showProgress(false);
                alert.setTitle("Error")
                        .setMessage(R.string.connection_error)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                alert.show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(REQUEST, "REGISTER");
                params.put(NAME, name);
                params.put(ADDRESS, address);
                params.put(PASSWORD, password);
                params.put(EMAIL, email);
                return params;
            }
        };
        Volley.newRequestQueue(getActivity()).add(stringRequest);
    }

    private void showProgress(boolean show) {
        mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        mRegisterButton.setVisibility(!show ? View.VISIBLE : View.GONE);
        mBranchName.setEnabled(!show);
        mBranchAddress.setEnabled(!show);
        mBranchPassword.setEnabled(!show);
        mBranchEmail.setEnabled(!show);
        mShowPlainPassword.setEnabled(!show);
    }
}
