package com.lidago.stayfitabit.register;

import android.app.ProgressDialog;
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
import com.lidago.stayfitabit.Args;
import com.lidago.stayfitabit.R;
import com.lidago.stayfitabit.firebase.FirebaseClient;
import com.lidago.stayfitabit.firebase.Gender;
import com.lidago.stayfitabit.firebase.User;

import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText mMailAddressEditText;
    private EditText mPasswordEditText;
    private EditText mPasswordConfirmEditText;
    private Button mRegisterButton;
    private TextView mLinkTextView;
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Firebase.setAndroidContext(this);
        init();
    }

    private void init() {
        setupUserInterface();
        setListeners();
    }

    private void setupUserInterface() {
        mMailAddressEditText = (EditText) findViewById(R.id.register_email_editText);
        mPasswordEditText = (EditText) findViewById(R.id.register_password_editText);
        mPasswordConfirmEditText = (EditText) findViewById(R.id.register_passwordConfirm_editText);
        mRegisterButton = (Button) findViewById(R.id.register_button);
        mLinkTextView = (TextView) findViewById(R.id.register_link_textView);
        mProgress = new ProgressDialog(this);
        mProgress.setTitle(getString(R.string.register));
        mProgress.setMessage(getString(R.string.please_wait));
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);
    }

    private void setListeners() {
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

        mLinkTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    private boolean validate() {
        boolean valid = true;

        String mailAddress = mMailAddressEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();
        String passwordConfirm = mPasswordConfirmEditText.getText().toString();

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

        if(passwordConfirm.isEmpty() || passwordConfirm.length() < 6) {
            mPasswordConfirmEditText.setError(getString(R.string.invalid_password));
            valid = false;
        }
        else {
            if(!password.contentEquals(passwordConfirm)) {
                mPasswordConfirmEditText.setError(getString(R.string.invalid_password_confirm));
                valid = false;
            }
            else {
                mPasswordConfirmEditText.setError(null);
            }
        }

        return valid;
    }

    private void register() {
        if (!validate()) {
            return;
        }
        mProgress.show();
        FirebaseClient.getInstance().createUser(mMailAddressEditText.getText().toString(), mPasswordEditText.getText().toString(), new Firebase.ValueResultHandler<Map<String, Object>>() {
            @Override
            public void onSuccess(Map<String, Object> result) {
                final Firebase loginRef = new Firebase(Args.USERS_URL);
                loginRef.authWithPassword(mMailAddressEditText.getText().toString(), mPasswordEditText.getText().toString(), new Firebase.AuthResultHandler() {
                    @Override
                    public void onAuthenticated(AuthData authData) {
                        loginRef.child(loginRef.getAuth().getUid()).setValue(new User(Gender.MALE));
                        loginRef.unauth();
                        setResult(RESULT_OK);
                        mProgress.dismiss();
                        finish();
                    }

                    @Override
                    public void onAuthenticationError(FirebaseError firebaseError) {
                        setResult(RESULT_CANCELED);
                        mProgress.dismiss();
                        finish();
                    }
                });
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                mProgress.dismiss();
                Toast.makeText(getApplicationContext(), firebaseError.getMessage() , Toast.LENGTH_LONG).show();
            }
        });
    }

    private void login() {
        setResult(RESULT_CANCELED);
        finish();
    }
}
