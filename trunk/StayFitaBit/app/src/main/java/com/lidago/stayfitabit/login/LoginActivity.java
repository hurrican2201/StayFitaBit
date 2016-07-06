package com.lidago.stayfitabit.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.lidago.stayfitabit.MainActivity;
import com.lidago.stayfitabit.R;
import com.lidago.stayfitabit.event.FirebaseDataEventListener;
import com.lidago.stayfitabit.firebase.FirebaseClient;
import com.lidago.stayfitabit.firebase.Gender;
import com.lidago.stayfitabit.firebase.User;
import com.lidago.stayfitabit.register.RegisterActivity;

import java.util.EventObject;

public class LoginActivity extends AppCompatActivity {

    private final int REGISTER_REQUEST = 45;

    private EditText mMailAddressEditText;
    private EditText mPasswordEditText;
    private Button mLoginButton;
    private TextView mLinkTextView;
    private ProgressDialog mProgress;
    private int mWaitTime;
    private boolean mLoggedIn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Firebase.setAndroidContext(this);
        init();

        if(FirebaseClient.getInstance().isLoggedIn()) {
            login();
        }
    }

    @Override
    public void onBackPressed() {
    }

    private void init() {
        setupUserInterface();
        setListeners();
    }

    private void setupUserInterface() {
        mMailAddressEditText = (EditText) findViewById(R.id.login_email_editText);
        mPasswordEditText = (EditText) findViewById(R.id.login_password_editText);
        mLoginButton = (Button) findViewById(R.id.login_button);
        mLinkTextView = (TextView) findViewById(R.id.login_link_textView);
        mProgress = new ProgressDialog(this);
        mProgress.setTitle(getString(R.string.login));
        mProgress.setMessage(getString(R.string.please_wait));
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);
    }

    private void setListeners() {
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validate()) {
                    return;
                }
                login();
            }
        });

        mLinkTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
    }

    private boolean validate() {
        boolean valid = true;

        String mailAddress = mMailAddressEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();

        if(mailAddress.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(mailAddress).matches()) {
            mMailAddressEditText.setError(getString(R.string.invalid_mail_address));
            valid = false;
        }
        else {
            mMailAddressEditText.setError(null);
        }

        if(password.isEmpty() || password.length() < 6) {
            mPasswordEditText.setError(getString(R.string.invalid_password));
            valid = false;
        }
        else {
            mPasswordEditText.setError(null);
        }

        return valid;
    }

    private void login() {
        mProgress.show();
        if(FirebaseClient.getInstance().getUserRef().getAuth() == null || isExpired(FirebaseClient.getInstance().getUserRef().getAuth())) {
            FirebaseClient.getInstance().getUserRef().authWithPassword(mMailAddressEditText.getText().toString(), mPasswordEditText.getText().toString(), new Firebase.AuthResultHandler() {
                @Override
                public void onAuthenticated(AuthData authData) {
                    startLogin();
                    FirebaseClient.getInstance().getFirebaseData().addFirebaseDataEventListener(new FirebaseDataEventListener() {
                        @Override
                        public void allDataReceived(EventObject e) {
                            mLoggedIn = true;
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            mProgress.dismiss();
                            startActivity(intent);
                            finish();
                        }
                    });
                    if(!FirebaseClient.getInstance().isFirebaseListenerSet()) {
                        FirebaseClient.getInstance().setListener();
                    }
                }

                @Override
                public void onAuthenticationError(FirebaseError firebaseError) {
                    mProgress.dismiss();
                    Toast.makeText(getApplicationContext(), firebaseError.getMessage() , Toast.LENGTH_LONG).show();
                }
            });
        }
        else {
            startLogin();
            FirebaseClient.getInstance().getFirebaseData().addFirebaseDataEventListener(new FirebaseDataEventListener() {
                @Override
                public void allDataReceived(EventObject e) {
                    mLoggedIn = true;
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    mProgress.dismiss();
                    startActivity(intent);
                    finish();
                }
            });
            if(!FirebaseClient.getInstance().isFirebaseListenerSet()) {
                FirebaseClient.getInstance().setListener();
            }
        }
    }

    private boolean isExpired(AuthData auth) {
        return (System.currentTimeMillis() / 1000) >= auth.getExpires();
    }

    private void register() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivityForResult(intent, REGISTER_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REGISTER_REQUEST) {
            if(resultCode == RESULT_OK) {
                Toast.makeText(getApplicationContext(), getString(R.string.user_added), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startLogin() {
        mWaitTime = 35;
        Thread refreshThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while ((mWaitTime >= 0) && !mLoggedIn) {
                    try {
                        if(mWaitTime != 0) {
                            Thread.sleep(1000);
                        }
                        mWaitTime--;
                        if(mWaitTime == 0) {
                            mLoggedIn = true;
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            mProgress.dismiss();
                            startActivity(intent);
                            finish();
                        }
                    } catch (InterruptedException ex) {

                    }
                }
            }
        });
        refreshThread.start();
    }
}
