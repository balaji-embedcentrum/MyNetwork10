package com.networkstudent;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.networkstudent.event.ChooseTabEvent;
import com.networkstudent.event.CloseProductDetailsScreenEvent;
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

public class DialogFragmentCancelConfirmation extends DialogFragment {


    @Bind(R.id.spinnerReason)
    Spinner spinnerReason;
    @Bind(R.id.textInputLayoutPin)
    TextInputLayout textInputLayoutPin;
    @Bind(R.id.pickupConfirmButton)
    Button pickupConfirmButton;
    private String orderCode;

    public DialogFragmentCancelConfirmation(String orderCode) {
        this.orderCode = orderCode;
    }

    public DialogFragmentCancelConfirmation() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().setTitle("Cancel Confirmation");
        View view = inflater.inflate(R.layout.fragment_dialog_fragment_confirm_cancel, container, false);
        ButterKnife.bind(this, view);

        final ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, android.R.id.text1);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerReason.setAdapter(spinnerAdapter);


//        final ProgressDialog dialog = ProgressDialog.show(getContext(), "Loading", "Please wait...", true);
        ParseQuery<ParseObject> query = ParseQuery.getQuery("CancelReasons");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        for (int i = 0; i < list.size(); i++) {
                            spinnerAdapter.add(list.get(i).getString("Reason"));
                        }
                        spinnerAdapter.notifyDataSetChanged();
                    } else {
//                        dialog.dismiss();
                        Toast.makeText(getContext(), "Sorry unable to get the list of reason.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.pickupConfirmButton)
    public void confirmingPickUp() {
//        if (validate()) {
        final ProgressDialog dialog = ProgressDialog.show(getContext(), "Loading", "Please wait...", true);
        final String reason = spinnerReason.getSelectedItem().toString();

        ParseQuery<ParseObject> query1 = ParseQuery.getQuery("OrderData");
        query1.whereEqualTo("OrderCode", orderCode);
        query1.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    ParseObject person = list.get(0);
                    person.put("StatusReason", reason);
                    person.put("OrderStatus", "Cancelled");
                    person.saveInBackground(new SaveCallback() {
                        public void done(ParseException e) {
                            if (e == null) {
                                EventBus.getDefault().postSticky(new CloseProductDetailsScreenEvent(true));
                                EventBus.getDefault().postSticky(new ChooseTabEvent("CANCELLED"));
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
//        }
    }

//    private boolean validate() {
//        String pin = spi.getText().toString().trim();
//
//        if (TextUtils.isEmpty(pin)) {
//            textInputLayoutPin.setError("Pin cannot be empty");
//            textInputLayoutPin.requestFocus();
//            return false;
//        } else if (pin.length() != 4) {
//            textInputLayoutPin.setError("Pin should have 4 digit no.");
//            textInputLayoutPin.requestFocus();
//            return false;
//        } else
//            textInputLayoutPin.setError(null);
//        return true;
//    }
}
