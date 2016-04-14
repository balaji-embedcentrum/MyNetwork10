package com.networkstudent.utils;

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

import com.networkstudent.ProductShowcaseActivity;
import com.networkstudent.R;
import com.networkstudent.model.ProfileData;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dream on 13-Dec-15.
 */
public class MyRecyclerFavoritesAdapter extends RecyclerView.Adapter<MyRecyclerFavoritesAdapter.CustomViewHolder> {
    ArrayList<ProfileData> profileDataArrayList;
    private Context context;

    public MyRecyclerFavoritesAdapter(Context context) {
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
        customViewHolder.textViewAddress.setText(profileData.getProfileAddr1() + ", " +
                profileData.getProfileCity() + ", " + profileData.getProfileZip());
        customViewHolder.imageViewFev.setImageResource(R.drawable.fav_btn_active);

        customViewHolder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customViewHolder.textViewProfileView.setText(String.valueOf(Integer.parseInt(customViewHolder.textViewProfileView.getText().toString()) + 1));
                Intent intent = new Intent(context, ProductShowcaseActivity.class);
                intent.putExtra("profileCode", profileDataArrayList.get(i).getProfileCode() + "");
                intent.putExtra("userId", profileDataArrayList.get(i).getUserId() + "");
                context.startActivity(intent);
            }
        });

        customViewHolder.imageViewFev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("UserFavorites");
                query.whereEqualTo("profileCode", profileData.getProfileCode());
                query.whereEqualTo("userID", Integer.parseInt(profileData.getUserId()));
                query.findInBackground(new FindCallback<ParseObject>() {
                    public void done(List<ParseObject> fevList, ParseException e) {
                        if (e == null) {
                            Log.d("score", "Retrieved " + fevList.size() + " scores");
                            if (fevList.size() > 0) {
                                ParseQuery<ParseObject> query = ParseQuery.getQuery("UserFavorites");
                                query.whereEqualTo("profileCode", profileData.getProfileCode());
                                query.whereEqualTo("userID", Integer.parseInt(profileData.getUserId()));
                                query.findInBackground(new FindCallback<ParseObject>() {
                                    public void done(List<ParseObject> invites, ParseException e) {
                                        if (e == null) {
                                            // iterate over all messages and delete them
                                            for (ParseObject invite : invites) {
                                                invite.deleteInBackground();
                                            }
                                            Toast.makeText(context, "Removed from favorites.", Toast.LENGTH_LONG).show();
                                            profileDataArrayList.remove(profileData);
                                            notifyDataSetChanged();
                                            customViewHolder.imageViewFev.setImageResource(R.drawable.fav_btn_inactive);
                                        } else {
                                            Toast.makeText(context, "Sorry unable to remove please try again.", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            } else {
                                ParseObject gameScore = new ParseObject("UserFavorites");
                                gameScore.put("userID", Integer.parseInt(profileData.getUserId()));
                                gameScore.put("profileCode", profileData.getProfileCode());
                                gameScore.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        Log.d("TAG", " UPDATED");
                                        if (e == null) {
                                            customViewHolder.imageViewFev.setImageResource(R.drawable.fav_btn_active);
                                            Snackbar snackbar = Snackbar
                                                    .make(v, "Added to your Favorites.", Snackbar.LENGTH_LONG)
                                                    .setAction("UNDO", new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            customViewHolder.imageViewFev.performClick();
                                                        }
                                                    });
                                            snackbar.show();
                                        } else {
                                            Toast.makeText(context, "Sorry unable to undo please try again.", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }
                        } else {
                            Log.d("score", "Error: " + e.getMessage());
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
