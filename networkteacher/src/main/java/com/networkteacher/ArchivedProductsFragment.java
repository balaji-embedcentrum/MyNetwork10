package com.networkteacher;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.networkteacher.models.Product;
import com.networkteacher.utils.ReusableClass;
import com.networkteacher.widget.ArchivedProductListRecyclerAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Dream on 13-Dec-15.
 */
public class ArchivedProductsFragment extends Fragment {

    private static final String TAG = "MyTAG";
    @Bind(R.id.my_recycler_view)
    RecyclerView myRecyclerView;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    @Bind(R.id.textViewNoData)
    TextView textViewNoData;
    @Bind(R.id.progress_container)
    LinearLayout progressContainer;
    @Bind(R.id.imageViewBackProduct)
    ImageView imageViewBackProduct;
    @Bind(R.id.ImageViewCall)
    ImageView ImageViewCall;
    @Bind(R.id.textViewAddress)
    TextView textViewAddress;
    @Bind(R.id.textViewDescription)
    TextView textViewDescription;
    @Bind(R.id.textViewCost)
    TextView textViewCost;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArchivedProductListRecyclerAdapter mAdapter;

    public ArchivedProductsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_archived_products, container, false);
        ButterKnife.bind(this, rootView);

        mAdapter = new ArchivedProductListRecyclerAdapter(getContext());
        myRecyclerView.setHasFixedSize(true);
        mLayoutManager = new GridLayoutManager(getContext(), 2);
        myRecyclerView.setLayoutManager(mLayoutManager);
        myRecyclerView.setAdapter(mAdapter);

        ImageViewCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ImageViewCall.getTag() != null) {
                    Intent dial = new Intent();
                    dial.setAction("android.intent.action.DIAL");
                    dial.setData(Uri.parse("tel:" + ImageViewCall.getTag()));
                    startActivity(dial);
                } else
                    Toast.makeText(getContext(), "No no available.", Toast.LENGTH_LONG).show();
            }
        });
        addingHeaderData();
        return rootView;
    }

    private void addingHeaderData() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("ProfileData");
        query.whereEqualTo("profileCode", Integer.parseInt(ReusableClass.getFromPreference("profileCode", getContext())));
        query.findInBackground(
                new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> profileList, ParseException e) {
                        if (e == null) {
                            if (profileList.size() > 0) {
                                for (int i = 0; i < profileList.size(); i++) {
                                    ParseObject p = profileList.get(i);

                                    String address = "";
                                    if (p.getString("profileAddr1") != null)
                                        address = p.getString("profileAddr1");
                                    if (p.getString("profileAddr2") != null)
                                        address = address + ", " + p.getString("profileAddr2");
                                    if (p.getString("profileCity") != null)
                                        address = address + ", " + p.getString("profileCity");
                                    if (p.getString("profileZip") != null)
                                        address = address + ", " + p.getString("profileZip");
                                    if (p.getString("profileState") != null)
                                        address = address + ", " + p.getString("profileState");
                                    if (p.getString("profileCountry") != null)
                                        address = address + ", " + p.getString("profileCountry");

                                    textViewAddress.setText(address);
                                    ImageViewCall.setTag(p.getString("profilePhone"));

                                    if (p.getParseFile("profileImage").getUrl() != null)
                                        Glide.with(getContext()).load(p.getParseFile("profileImage").getUrl())
                                                .centerCrop()
                                                .placeholder(R.drawable.placeholder)
                                                .crossFade()
                                                .into(imageViewBackProduct);

                                    if (p.getNumber("profileViews") != null)
                                        textViewCost.setText("View - " + p.getNumber("profileViews"));
                                    else
                                        textViewCost.setText("View - 0");

                                }
                            } else {
                                Log.d(TAG, "done: No Value");
                            }
                        } else {
                            Log.d("score", "Error: " + e.getMessage());
                        }
                    }
                }
        );
    }

    @Override
    public void onResume() {
        super.onResume();
        textViewNoData.setVisibility(View.INVISIBLE);
        progressContainer.setVisibility(View.VISIBLE);
        loadData();
    }

    private void loadData() {
        final ArrayList<Product> productArrayList = new ArrayList<>();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("ProductData");
        query.whereEqualTo("ProfileCode", Integer.parseInt(ReusableClass.getFromPreference("profileCode", getContext())));
        query.whereEqualTo("ProductStatus", "Inactive");
        query.findInBackground(
                new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> profileList, ParseException e) {
                        if (e == null) {
                            if (profileList.size() > 0) {
                                for (int i = 0; i < profileList.size(); i++) {
                                    ParseObject p = profileList.get(i);

                                    Log.d(TAG, "done: " + p.getString("ProductDescription"));
                                    Product product = new Product();
                                    product.setObjectId(p.getObjectId());
                                    product.setProductDescription(p.getString("ProductDescription"));
                                    product.setProductStatus(p.getString("ProductStatus"));
                                    product.setProductSummary(p.getString("ProductSummary"));
                                    product.setProductStatus(p.getString("ProductStatus"));
                                    product.setProductCost(p.getInt("ProductCost"));
                                    product.setObjectId(p.getObjectId());
                                    if (p.getParseFile("ProductFoto1") != null)
                                        product.setProductFoto1(p.getParseFile("ProductFoto1").getUrl().toString());
                                    if (p.getParseFile("ProductFoto2") != null)
                                        product.setProductFoto2(p.getParseFile("ProductFoto2").getUrl().toString());
                                    if (p.getParseFile("ProductFoto3") != null)
                                        product.setProductFoto3(p.getParseFile("ProductFoto3").getUrl().toString());

                                    productArrayList.add(product);
                                }
                                mAdapter.addAll(productArrayList);
                            } else {
                                Log.d(TAG, "done: No Value");
                                textViewNoData.setVisibility(View.VISIBLE);
                            }
                        } else {
                            Log.d("score", "Error: " + e.getMessage());
                        }
                        progressContainer.setVisibility(View.INVISIBLE);
                    }
                }
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
