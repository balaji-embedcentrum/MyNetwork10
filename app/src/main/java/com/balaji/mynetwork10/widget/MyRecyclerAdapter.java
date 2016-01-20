package com.balaji.mynetwork10.widget;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.balaji.mynetwork10.R;
import com.balaji.mynetwork10.model.ProfileData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dream on 13-Dec-15.
 */
public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.CustomViewHolder> {
    private Context mContext;
    private CardView previousCardView;

    ArrayList<ProfileData> profileDataArrayList;

    public MyRecyclerAdapter(Context context) {
        this.mContext = context;
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
        ProfileData profileData = profileDataArrayList.get(i);

        //Setting text view name and address
        customViewHolder.textViewName.setText(profileData.getProfileName());
        customViewHolder.textViewZip.setText("Zipcode- " + profileData.getProfileZip());
        customViewHolder.textViewProfileView.setText("View - " + profileData.getProfileViews());
        customViewHolder.textViewAddress.setText(profileData.getProfileAddr1() + ", " + profileData.getProfileAddr2()
                + ", " + profileData.getProfileCity() + ", " + profileData.getProfileCountry());

//        customViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                EventBus.getDefault().post(new GetSelectedLocationEvent(locationDataList.get(i)));
//                customViewHolder.cardView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
//                if (previousCardView != null && customViewHolder.cardView != previousCardView)
//                    previousCardView.setCardBackgroundColor(ContextCompat.getColor(mContext, android.R.color.white));
//                previousCardView = customViewHolder.cardView;
//            }
//        });
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
        protected CardView cardView;
        protected LinearLayout mainLayout;

        public CustomViewHolder(View view) {
            super(view);
            this.textViewName = (TextView) view.findViewById(R.id.textViewName);
            this.textViewAddress = (TextView) view.findViewById(R.id.textViewAddress);
            this.textViewZip = (TextView) view.findViewById(R.id.textViewZip);
            this.textViewProfileView = (TextView) view.findViewById(R.id.textViewProfileView);
            this.cardView = (CardView) view.findViewById(R.id.cardView);
            this.mainLayout = (LinearLayout) view.findViewById(R.id.mainLayout);
        }
    }
}
