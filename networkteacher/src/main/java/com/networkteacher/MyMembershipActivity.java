package com.networkteacher;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.networkteacher.utils.ReusableClass;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MyMembershipActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.textViewMembership)
    TextView textViewMembership;
    @Bind(R.id.textViewProfileView)
    TextView textViewProfileView;
    @Bind(R.id.spinnerMembership)
    Spinner spinnerMembership;
    @Bind(R.id.buttonSave)
    Button buttonSave;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_membership);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        fetchData();
        fetchMembershipSpinnerData();
    }

    private void fetchMembershipSpinnerData() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("ProfileMembership");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> scoreList, ParseException e) {
                if (e == null) {
                    int len = scoreList.size();
                    if (len > 0) {
                        ArrayList<String> list = new ArrayList<>();
                        for (int i = 0; i < len; i++) {
                            ParseObject p = scoreList.get(i);
                            list.add(p.getString("profileMembership"));
                        }
                        adapter = new ArrayAdapter<>(MyMembershipActivity.this, android.R.layout.simple_spinner_item, list);
                        spinnerMembership.setAdapter(adapter);
                    }
                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
            }
        });
    }

    @OnClick(R.id.buttonSave)
    public void savingMembership() {
        ParseQuery query = ParseQuery.getQuery("ProfileData");
        query.whereEqualTo("profileCode", Integer.parseInt(ReusableClass.getFromPreference("profileCode", this)));
        query.findInBackground(
                new FindCallback() {
                    @Override
                    public void done(List list, ParseException e) {
                    }

                    @Override
                    public void done(Object o, Throwable throwable) {
                        List<ParseObject> list = (List<ParseObject>) o;
                        ParseObject john = list.get(0);
                        john.put("profileMembership", spinnerMembership.getSelectedItem().toString());

                        john.saveInBackground(new SaveCallback() {
                            public void done(ParseException e) {
                                if (e == null) {
                                    Toast.makeText(MyMembershipActivity.this, "Thanks for updating your membership.", Toast.LENGTH_LONG).show();
                                    fetchData();
                                } else {
                                    Log.d("TAG", "error saving image: " + e);
                                }
                            }

                        });
                    }

                });

    }


    private void fetchData() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("ProfileData");
        query.whereEqualTo("profileCode", Integer.parseInt(ReusableClass.getFromPreference("profileCode", this)));
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> scoreList, ParseException e) {
                if (e == null) {
                    int len = scoreList.size();
                    if (len > 0) {
                        for (int i = 0; i < len; i++) {
                            ParseObject p = scoreList.get(i);
                            textViewMembership.setText(p.getString("profileMembership"));
                            textViewProfileView.setText(p.getNumber("profileViews").toString());
                        }
                    }
                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
            }
        });
    }
}
