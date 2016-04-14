package com.networkstudent;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.networkstudent.event.ChooseTabEvent;
import com.networkstudent.event.CloseProductDetailsScreenEvent;
import com.networkstudent.utils.ReusableClass;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

public class DialogFragmentConfirmPickup extends DialogFragment {


    @Bind(R.id.editTextPin)
    EditText editTextPin;
    @Bind(R.id.textInputLayoutPin)
    TextInputLayout textInputLayoutPin;
    @Bind(R.id.pickupConfirmButton)
    Button pickupConfirmButton;
    private String orderCode;

    public DialogFragmentConfirmPickup(String orderCode) {
        this.orderCode = orderCode;
    }

    public DialogFragmentConfirmPickup() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().setTitle("Pick-up Confirmation");
        View view = inflater.inflate(R.layout.fragment_dialog_fragment_confirm_pickup, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.pickupConfirmButton)
    public void confirmingPickUp() {
        if (validate()) {
            final ProgressDialog dialog = ProgressDialog.show(getContext(), "Loading", "Please wait...", true);
            String pin = editTextPin.getText().toString().trim();

            ParseQuery<ParseObject> query = ParseQuery.getQuery("StudentUsers");
            query.whereEqualTo("StudentPin", Integer.parseInt(pin));
            query.whereEqualTo("StudentPhone", ReusableClass.getFromPreference("session", getContext()));
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> list, ParseException e) {
                    if (e == null) {
                        if (list.size() > 0) {
                            ParseQuery<ParseObject> query1 = ParseQuery.getQuery("OrderData");
                            query1.whereEqualTo("OrderCode", orderCode);
                            query1.findInBackground(new FindCallback<ParseObject>() {
                                public void done(List<ParseObject> list, ParseException e) {
                                    if (e == null) {
                                        ParseObject person = list.get(0);
                                        person.put("OrderStatus", "Picked Up");
                                        person.saveInBackground(new SaveCallback() {
                                            public void done(ParseException e) {
                                                if (e == null) {
                                                    EventBus.getDefault().postSticky(new CloseProductDetailsScreenEvent(true));
                                                    EventBus.getDefault().postSticky(new ChooseTabEvent("PICKED UP"));
                                                    dialog.dismiss();
                                                    getDialog().dismiss();
                                                } else {
                                                    Toast.makeText(getContext(), "Try again!! Some error occurred.", Toast.LENGTH_LONG).show();
                                                    dialog.dismiss();
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
                            textInputLayoutPin.setError("Please check your pin.");
                            textInputLayoutPin.requestFocus();
                        }
                    } else {
                        Log.d("score", "Error: " + e.getMessage());
                    }
                }
            });
        }
    }

    private boolean validate() {
        String pin = editTextPin.getText().toString().trim();

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
        return true;
    }
}
