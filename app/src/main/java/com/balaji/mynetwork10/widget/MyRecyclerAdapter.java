package com.balaji.mynetwork10.widget;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.balaji.mynetwork10.DetailsActivity;
import com.balaji.mynetwork10.R;
import com.balaji.mynetwork10.event.PassProfileDataEvent;
import com.balaji.mynetwork10.model.ProfileData;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by Dream on 13-Dec-15.
 */
public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.CustomViewHolder> {
    ArrayList<ProfileData> profileDataArrayList;
    private Context context;

    public MyRecyclerAdapter(Context context) {
        this.context = context;
        profileDataArrayList = new ArrayList<>();
    }

    public void addAll(ArrayList<ProfileData> profileDataList) {
        profileDataArrayList.addAll(profileDataList);
        notifyDataSetChanged();
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_row, viewGroup, false);

        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    public void clearingAll() {
        profileDataArrayList.clear();
    }

    public void addingList(List<ProfileData> profileDataList) {
        profileDataArrayList.addAll(profileDataList);
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder customViewHolder, final int i) {
        final ProfileData profileData = profileDataArrayList.get(i);

        //Setting text view name and address
        customViewHolder.textViewName.setText(profileData.getProfileName());
//        customViewHolder.textViewZip.setText(profileData.getProfileAddr2() + ", " + profileData.getProfileCity()
//                + ", " + profileData.getProfileCountry() + ", " + profileData.getProfileZip());
        customViewHolder.textViewZip.setText(profileData.getProfileCity() + ", " + profileData.getProfileZip());
        customViewHolder.textViewProfileView.setText(String.valueOf(profileData.getProfileViews()));
        customViewHolder.textViewAddress.setText(profileData.getProfileAddr1());

        customViewHolder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().postSticky(new PassProfileDataEvent(profileDataArrayList.get(i)));
                Intent i = new Intent(context, DetailsActivity.class);
                context.startActivity(i);
            }
        });

        customViewHolder.imageViewFev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("ProfileData");
                query.getInBackground(profileData.getObjectId(), new GetCallback<ParseObject>() {
                    public void done(ParseObject profileViews, ParseException e) {
                        if (e == null) {
                            profileViews.increment("profileViews");
                            profileViews.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    Log.d("TAG", " UPDATED");
                                    if (e == null) {
                                        customViewHolder.textViewProfileView.setText(String.valueOf(profileData.getProfileViews() + 1));
                                        customViewHolder.imageViewFev.setImageResource(R.drawable.fav_btn_active);

                                        Snackbar snackbar = Snackbar
                                                .make(v, "Added to your Favorites.", Snackbar.LENGTH_LONG)
                                                .setAction("UNDO", new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        ParseQuery<ParseObject> query = ParseQuery.getQuery("ProfileData");
                                                        query.getInBackground(profileData.getObjectId(), new GetCallback<ParseObject>() {
                                                            public void done(ParseObject profileViews, ParseException e) {
                                                                if (e == null) {
                                                                    profileViews.increment("profileViews", -1);
                                                                    profileViews.saveInBackground(new SaveCallback() {
                                                                        @Override
                                                                        public void done(ParseException e) {
                                                                            Log.d("TAG", " UPDATED");
                                                                            if (e == null) {
                                                                                customViewHolder.imageViewFev.setImageResource(R.drawable.fav_btn_inactive);
                                                                                Toast.makeText(context, "Removed from favorites.", Toast.LENGTH_LONG).show();
                                                                            } else {
                                                                                Toast.makeText(context, "Sorry unable to undo please try again.", Toast.LENGTH_LONG).show();
                                                                            }
                                                                        }
                                                                    });
                                                                }
                                                            }
                                                        });
                                                    }
                                                });

                                        snackbar.show();
                                    } else {
                                        Snackbar snackbar = Snackbar
                                                .make(v, "Sorry try again.", Snackbar.LENGTH_LONG)
                                                .setAction("RETRY", new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        customViewHolder.imageViewFev.performClick();
                                                    }
                                                });

                                        snackbar.show();
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return (null != profileDataArrayList ? profileDataArrayList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        protected TextView textViewName;
        protected TextView textViewZip;
        protected TextView textViewAddress;
        protected TextView textViewProfileView;
        protected ImageView imageViewFev;
        protected CardView cardView;
        protected LinearLayout mainLayout;

        public CustomViewHolder(View view) {
            super(view);
            this.textViewName = (TextView) view.findViewById(R.id.textViewName);
            this.textViewAddress = (TextView) view.findViewById(R.id.textViewAddress);
            this.textViewZip = (TextView) view.findViewById(R.id.textViewZip);
            this.textViewProfileView = (TextView) view.findViewById(R.id.textViewProfileView);
            this.imageViewFev = (ImageView) view.findViewById(R.id.imageViewFev);
            this.cardView = (CardView) view.findViewById(R.id.cardView);
            this.mainLayout = (LinearLayout) view.findViewById(R.id.mainLayout);
        }
    }
}
