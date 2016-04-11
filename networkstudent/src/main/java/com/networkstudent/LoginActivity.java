package com.networkstudent;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.digits.sdk.android.AuthCallback;
import com.digits.sdk.android.DigitsAuthButton;
import com.digits.sdk.android.DigitsException;
import com.digits.sdk.android.DigitsSession;
import com.networkstudent.utils.ReusableClass;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    @Bind(R.id.mobileNo)
    EditText mobileNo;
    @Bind(R.id.signInButton)
    AppCompatButton signInButton;
    @Bind(R.id.auth_button)
    DigitsAuthButton digitsButton;
    @Bind(R.id.textInputLayoutMobileNo)
    TextInputLayout textInputLayoutMobileNo;
    @Bind(R.id.login_progress)
    ProgressBar loginProgress;
    @Bind(R.id.email_login_form)
    LinearLayout emailLoginForm;
    private String TAG = "MyTag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        signInButton.setVisibility(View.GONE);
        mobileNo.setVisibility(View.GONE);

        digitsButton.setText("Login with Mobile No");
        digitsButton.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));

        digitsButton.setCallback(new AuthCallback() {
            @Override
            public void success(DigitsSession session, String phoneNumber) {

                if (session != null)
                    mobileNo.setText(session.getPhoneNumber());
                else
                    mobileNo.setText(phoneNumber);

                signInButton.setVisibility(View.VISIBLE);
                mobileNo.setVisibility(View.VISIBLE);
                digitsButton.setVisibility(View.GONE);
            }

            @Override
            public void failure(DigitsException exception) {
                Log.d("Digits", "Sign in with Digits failure", exception);
            }
        });
    }

    public void signin(View view) {
        emailLoginForm.setVisibility(View.INVISIBLE);
        loginProgress.setVisibility(View.VISIBLE);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("StudentUsers");
        query.whereEqualTo("StudentPhone", mobileNo.getText().toString());
//        query.whereEqualTo("StudentEmail", "a@a.com");

        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> scoreList, ParseException e) {
                if (e == null) {
                    int len = scoreList.size();
                    if (len > 0) {
                        for (int i = 0; i < len; i++) {
                            ParseObject p = scoreList.get(i);
                            String studentPhone = p.getString("StudentPhone");
                            p.getObjectId();
                            ReusableClass.saveInPreference("studentObjectId", p.getObjectId().toString(), LoginActivity.this);
                            Log.d(TAG, "done StudentPhone: " + studentPhone);
                        }
                        textInputLayoutMobileNo.setError(null);

                        ReusableClass.saveInPreference("session", mobileNo.getText().toString(), LoginActivity.this);

                        Intent i = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    } else {
                        loginProgress.setVisibility(View.INVISIBLE);
                        emailLoginForm.setVisibility(View.VISIBLE);
                        textInputLayoutMobileNo.setError("Not a valid mobile no. Please register first.");
                    }
                } else {
                    Log.d("score", "Error: " + e.getMessage());
                    loginProgress.setVisibility(View.INVISIBLE);
                    emailLoginForm.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public void signUp(View view) {
        Intent i = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(i);
    }
}
