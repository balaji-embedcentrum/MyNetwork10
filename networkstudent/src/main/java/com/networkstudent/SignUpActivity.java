package com.networkstudent;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.digits.sdk.android.AuthCallback;
import com.digits.sdk.android.DigitsAuthButton;
import com.digits.sdk.android.DigitsException;
import com.digits.sdk.android.DigitsSession;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SignUpActivity extends AppCompatActivity {

    @Bind(R.id.editTextSchoolCode)
    EditText editTextSchoolCode;
    @Bind(R.id.textInputLayoutSchoolCode)
    TextInputLayout textInputLayoutSchoolCode;
    @Bind(R.id.editTextEmail)
    EditText editTextEmail;
    @Bind(R.id.textInputLayoutEmail)
    TextInputLayout textInputLayoutEmail;
    @Bind(R.id.editTextPin)
    EditText editTextPin;
    @Bind(R.id.textInputLayoutPin)
    TextInputLayout textInputLayoutPin;
    @Bind(R.id.checkBoxToc)
    CheckBox checkBoxToc;
    @Bind(R.id.signInButton)
    Button signInButton;
    @Bind(R.id.mobileNo)
    EditText mobileNo;
    @Bind(R.id.textInputLayoutMobileNo)
    TextInputLayout textInputLayoutMobileNo;
    @Bind(R.id.auth_button)
    DigitsAuthButton digitsButton;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);

        signInButton.setVisibility(View.GONE);
        mobileNo.setVisibility(View.GONE);

        digitsButton.setText("Authenticate your mobile no");
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

    public void registering(View view) {
        String schoolCode = editTextSchoolCode.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String pin = editTextPin.getText().toString().trim();
        String phoneNo = mobileNo.getText().toString();

        if (validated()) {
            dialog = ProgressDialog.show(this, "Loading", "Please wait...", true);

            ParseObject gameScore = new ParseObject("StudentUsers");
            if (!schoolCode.equalsIgnoreCase(""))
                gameScore.put("TeacherCode", Integer.parseInt(schoolCode));
            gameScore.put("StudentEmail", email);
            gameScore.put("StudentPin", Integer.parseInt(pin));
            gameScore.put("StudentPhone", phoneNo);
            gameScore.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Intent i = new Intent(SignUpActivity.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    } else {
                        Toast.makeText(SignUpActivity.this, "Sorry error occured. Try again.", Toast.LENGTH_LONG).show();
                    }
                    dialog.dismiss();
                }
            });
        }
    }

    private boolean validated() {
        String schoolCode = editTextSchoolCode.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String pin = editTextPin.getText().toString().trim();
        String phoneNo = mobileNo.getText().toString();

        if (TextUtils.isEmpty(email)) {
            textInputLayoutEmail.setError("Email cannot be empty");
            textInputLayoutEmail.requestFocus();
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            textInputLayoutEmail.setError("Please enter a valid Email.");
            textInputLayoutEmail.requestFocus();
            return false;
        } else
            textInputLayoutEmail.setError(null);

        if (TextUtils.isEmpty(pin)) {
            textInputLayoutPin.setError("Pin cannot be empty");
            textInputLayoutPin.requestFocus();
            return false;
        } else if (pin.length() != 4) {
            textInputLayoutPin.setError("Pin should have 4 digit no.");
            textInputLayoutPin.requestFocus();
            return false;
        } else
            textInputLayoutPin.setError(null);

        if (TextUtils.isEmpty(phoneNo)) {
            textInputLayoutMobileNo.setError("Phone no cannot be empty");
            textInputLayoutMobileNo.requestFocus();
            return false;
        } else
            textInputLayoutMobileNo.setError(null);

        if (!checkBoxToc.isChecked()) {
            Toast.makeText(SignUpActivity.this, "You must agreed with the terms and conditions.", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
}
