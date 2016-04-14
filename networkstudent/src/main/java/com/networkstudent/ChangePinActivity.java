package com.networkstudent;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.networkstudent.utils.ReusableClass;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ChangePinActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.editTextOldPin)
    EditText editTextOldPin;
    @Bind(R.id.textInputLayoutOldPin)
    TextInputLayout textInputLayoutOldPin;
    @Bind(R.id.editTextNewPin)
    EditText editTextNewPin;
    @Bind(R.id.textInputLayoutNewPin)
    TextInputLayout textInputLayoutNewPin;
    @Bind(R.id.editTextRepeatPin)
    EditText editTextRepeatPin;
    @Bind(R.id.textInputLayoutRepeatPin)
    TextInputLayout textInputLayoutRepeatPin;
    @Bind(R.id.pickupConfirmButton)
    Button pickupConfirmButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pin);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    public void savingNewPin(View view) {
        if (validate()) {
            final ProgressDialog dialog = ProgressDialog.show(ChangePinActivity.this, "Loading", "Please wait...", true);

            ParseQuery<ParseObject> query = ParseQuery.getQuery("StudentUsers");
            query.whereEqualTo("StudentPin", Integer.parseInt(editTextOldPin.getText().toString().trim()));
            query.whereEqualTo("StudentPhone", ReusableClass.getFromPreference("session", ChangePinActivity.this));
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> list, ParseException e) {
                    if (e == null) {
                        if (list.size() > 0) {
                            ParseQuery<ParseObject> query1 = ParseQuery.getQuery("StudentUsers");
                            query1.whereEqualTo("StudentPhone", ReusableClass.getFromPreference("session", ChangePinActivity.this));
                            query1.findInBackground(new FindCallback<ParseObject>() {
                                public void done(List<ParseObject> list, ParseException e) {
                                    if (e == null) {
                                        ParseObject person = list.get(0);
                                        person.put("StudentPin", Integer.parseInt(editTextNewPin.getText().toString().trim()));
                                        person.saveInBackground(new SaveCallback() {
                                            public void done(ParseException e) {
                                                if (e == null) {
                                                    Toast.makeText(ChangePinActivity.this, "Your pin successfully changed.", Toast.LENGTH_LONG).show();
                                                    finish();
                                                } else {
                                                    dialog.dismiss();
                                                    Toast.makeText(ChangePinActivity.this, "Try again!! Some error occurred.", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                                    } else {
                                        Log.d("score", "Error: " + e.getMessage());
                                    }
                                }
                            });
                        } else {
                            dialog.dismiss();
                            textInputLayoutOldPin.setError("Please check your current pin.");
                            editTextOldPin.requestFocus();
                        }
                    } else {
                        Log.d("score", "Error: " + e.getMessage());
                    }
                }
            });
        }
    }

    private boolean validate() {
        String oldPin = editTextOldPin.getText().toString().trim();
        String newPin = editTextNewPin.getText().toString().trim();
        String repeatPin = editTextRepeatPin.getText().toString().trim();

        if (TextUtils.isEmpty(oldPin)) {
            textInputLayoutOldPin.setError("Pin cannot be empty");
            editTextOldPin.requestFocus();
            return false;
        } else if (oldPin.length() != 4) {
            textInputLayoutOldPin.setError("Pin should have 4 digit no.");
            editTextOldPin.requestFocus();
            return false;
        } else
            textInputLayoutOldPin.setError(null);

        if (TextUtils.isEmpty(newPin)) {
            textInputLayoutNewPin.setError("Pin cannot be empty");
            editTextNewPin.requestFocus();
            return false;
        } else if (newPin.length() != 4) {
            textInputLayoutNewPin.setError("Pin should have 4 digit no.");
            editTextNewPin.requestFocus();
            return false;
        } else
            textInputLayoutNewPin.setError(null);

        if (TextUtils.isEmpty(repeatPin)) {
            textInputLayoutRepeatPin.setError("Pin cannot be empty");
            editTextRepeatPin.requestFocus();
            return false;
        } else if (repeatPin.length() != 4) {
            textInputLayoutRepeatPin.setError("Pin should have 4 digit no.");
            editTextRepeatPin.requestFocus();
            return false;
        } else
            textInputLayoutRepeatPin.setError(null);

        if (!repeatPin.equalsIgnoreCase(newPin)) {
            textInputLayoutNewPin.setError("New pin should match with repeat pin");
            textInputLayoutRepeatPin.setError("Repeat pin should match with new pin");
            return false;
        } else {
            textInputLayoutNewPin.setError(null);
            textInputLayoutRepeatPin.setError(null);
        }
        return true;
    }
}
