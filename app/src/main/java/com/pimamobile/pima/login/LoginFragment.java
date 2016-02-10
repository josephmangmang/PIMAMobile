package com.pimamobile.pima.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pimamobile.pima.LoginActivity;
import com.pimamobile.pima.MainActivity;
import com.pimamobile.pima.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginFragment extends Fragment {
    private static final String LOGIN_USERNAME = "name";
    private static final String LOGIN_PASSWORD = "password";
    private static final String REQUEST = "request";
    private EditText mUserName;
    private EditText mUserPassword;
    private Button mLoginButton;
    private TextView mCreateBranch;
    private AppCompatCheckBox mRememberMe;
    private ProgressBar mProgressBar;
    private LoginInterface mListener;
    private TextView mErrorMessage;


    public static LoginFragment newInstance() {
        return new LoginFragment();
    }
    public LoginFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        mUserName = (EditText) view.findViewById(R.id.login_user_name);
        mUserPassword = (EditText) view.findViewById(R.id.login_user_password);
        mLoginButton = (Button) view.findViewById(R.id.login_sign_in);
        mRememberMe = (AppCompatCheckBox) view.findViewById(R.id.login_remember_me);
        mProgressBar = (ProgressBar) view.findViewById(R.id.login_progress);
        mCreateBranch = (TextView) view.findViewById(R.id.login_create_branch);
        mErrorMessage = (TextView) view.findViewById(R.id.login_error_message);

        mCreateBranch.setOnClickListener(createBranchListener);
        mLoginButton.setOnClickListener(loginButtonListener);
        return view;
    }

    private void loginUser(final String name, final String password) {
        final boolean rememberMe = mRememberMe.isChecked();
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, LoginActivity.SERVER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        boolean error = true;
                        showProgress(false);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            error = jsonObject.getBoolean("error");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (error) {
                            showErrorMessage("Incorrect Branch Name or Password.");
                        } else {
                            preferences.edit().putBoolean(LoginActivity.REMEMBER_ME, rememberMe).apply();
                            startActivity(new Intent(getActivity(), MainActivity.class));
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showProgress(false);
                showErrorMessage(getString(R.string.connection_error));
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(REQUEST, "LOGIN");
                params.put(LOGIN_USERNAME, name);
                params.put(LOGIN_PASSWORD, password);
                return params;
            }
        };
        Volley.newRequestQueue(getActivity()).add(stringRequest);
    }

    private void showErrorMessage(String message) {
        mErrorMessage.setVisibility(View.VISIBLE);
        mErrorMessage.setText(message);
    }

    private void showProgress(boolean show) {
        mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        mLoginButton.setVisibility(!show ? View.VISIBLE : View.GONE);
        mUserName.setEnabled(!show);
        mUserPassword.setEnabled(!show);
        mRememberMe.setEnabled(!show);
    }
    private View.OnClickListener createBranchListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mListener.onCreateBrachClicked();
        }
    };

    private View.OnClickListener loginButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mErrorMessage.setVisibility(View.GONE);
            String name = mUserName.getText().toString().toLowerCase();
            String password = mUserPassword.getText().toString();
            View focus = null;
            boolean cancel = false;

            if (TextUtils.isEmpty(name)) {
                mUserName.setError(getString(R.string.error_field_required));
                focus = mUserName;
                cancel = true;
            } else if (TextUtils.isEmpty(password)) {
                mUserPassword.setError(getString(R.string.error_field_required));
                focus = mUserPassword;
                cancel = true;
            }

            if (cancel) {
                focus.requestFocus();
            } else {
                showProgress(true);
                loginUser(name, password);
            }
        }
    };


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
}
