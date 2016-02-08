package com.pimamobile.pima.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pimamobile.pima.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.Manifest.permission.READ_CONTACTS;

public class LoginFragment extends Fragment {
    private static final String SERVE_URL = "http://www.pretextsms.com/pima/api.php";
    private EditText mUserName;
    private EditText mUserPassword;
    private Button mLoginButton;
    private AppCompatCheckBox mRememberMe;
    private View.OnClickListener loginButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String username = mUserName.getText().toString();
            String password = mUserPassword.getText().toString();
            View focus = null;
            boolean cancel = false;

            if (TextUtils.isEmpty(username)) {
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
                loginUser();
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        mUserName = (EditText) view.findViewById(R.id.login_user_name);
        mUserPassword = (EditText) view.findViewById(R.id.login_user_password);
        mLoginButton = (Button) view.findViewById(R.id.login_sign_in);
        mRememberMe = (AppCompatCheckBox) view.findViewById(R.id.login_remember_me);

        mLoginButton.setOnClickListener(loginButtonListener);
        return view;
    }

    private void loginUser() {
        boolean rememberMe = mRememberMe.isChecked();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, SERVE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                return params;
            }
        };
    }
}
