package com.networkteacher.widget;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.networkteacher.ProductDetailsViewActivity;
import com.networkteacher.R;
import com.networkteacher.models.OrderData;
import com.networkteacher.models.Product;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;


public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.CustomViewHolder> {
    ArrayList<OrderData> orderDatas;
    private Context context;

    public MyRecyclerAdapter(Context context) {
        this.context = context;
        orderDatas = new ArrayList<>();
    }

    public void addAll(ArrayList<OrderData> orderDatas) {
        this.orderDatas.addAll(orderDatas);
        notifyDataSetChanged();
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.product_row_with_image, viewGroup, false);

        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    public void clearingAll() {
        orderDatas.clear();
    }

    public void addingList(List<OrderData> profileDataList) {
        orderDatas.addAll(profileDataList);
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder customViewHolder, final int i) {
        final OrderData singleOrder = orderDatas.get(i);

        customViewHolder.textViewSummery.setText("Order Id: " + singleOrder.getOrderCode());
        customViewHolder.textViewDescription.setText(singleOrder.getProductSummary());
        customViewHolder.textViewCost.setText("$" + String.valueOf(singleOrder.getProductCost()));

        customViewHolder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ArrayList<Product> productArrayList = new ArrayList<>();

                ParseQuery<ParseObject> query = ParseQuery.getQuery("ProductData");
                query.whereEqualTo("objectId", singleOrder.getProductCode());
                query.setLimit(1);
                query.findInBackground(
                        new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> profileList, ParseException e) {
                                if (e == null) {
                                    if (profileList.size() > 0) {
                                        for (int i = 0; i < profileList.size(); i++) {
                                            ParseObject p = profileList.get(i);

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
                                        Intent i = new Intent(context, ProductDetailsViewActivity.class);
                                        i.putExtra("productDetails", productArrayList.get(0).toString());
                                        context.startActivity(i);
                                    }
                                } else {
                                    Log.d("score", "Error: " + e.getMessage());
                                }
                            }
                        }
                );
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderDatas.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        protected TextView textViewSummery;
        protected TextView textViewDescription;
        protected TextView textViewCost;
        protected LinearLayout mainLayout;

        public CustomViewHolder(View view) {
            super(view);
            this.textViewSummery = (TextView) view.findViewById(R.id.textViewSummery);
            this.textViewDescription = (TextView) view.findViewById(R.id.textViewDescription);
            this.textViewCost = (TextView) view.findViewById(R.id.textViewCost);
            this.mainLayout = (LinearLayout) view.findViewById(R.id.mainLayout);
        }
    }
}
