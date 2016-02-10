package com.pimamobile.pima;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.pimamobile.pima.R;
import com.pimamobile.pima.login.LoginFragment;
import com.pimamobile.pima.login.LoginInterface;
import com.pimamobile.pima.login.RegisterFragment;

/**
 * A login screen that offers login via username/password.
 */
public class LoginActivity extends AppCompatActivity implements LoginInterface {

    public static final String SERVER_URL = "http://www.pretextsms.com/pima/api.php";
    public static final String REMEMBER_ME = "ehuppsi";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean mRememberMe = mPreferences.getBoolean(REMEMBER_ME, false);
        if (mRememberMe) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.fragment_container, LoginFragment.newInstance()).commit();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void updateShowHomeAsUp(boolean show) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(show);
        getSupportActionBar().setTitle(show ? R.string.title_register_branch : R.string.title_activity_login);
    }

    @Override
    public void onCreateBrachClicked() {
        getSupportFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                .replace(R.id.fragment_container, RegisterFragment.newInstance())
                .addToBackStack(null).commit();
    }

    @Override
    public void onRegisterSuccessfull() {
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }
}

