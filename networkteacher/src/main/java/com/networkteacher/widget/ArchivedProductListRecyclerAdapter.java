package com.networkteacher.widget;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.networkteacher.EditProductActivity;
import com.networkteacher.R;
import com.networkteacher.models.Product;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Dream on 13-Dec-15.
 */
public class ArchivedProductListRecyclerAdapter extends RecyclerView.Adapter<ArchivedProductListRecyclerAdapter.CustomViewHolder> {
    ArrayList<Product> productArrayList;
    private Context context;

    public ArchivedProductListRecyclerAdapter(Context context) {
        this.context = context;
        productArrayList = new ArrayList<>();
    }

    public void addAll(ArrayList<Product> myProductArrayList) {
        productArrayList.clear();
        productArrayList.addAll(myProductArrayList);
        notifyDataSetChanged();
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.product_row_with_image, viewGroup, false);

        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    public void clearingAll() {
        productArrayList.clear();
    }

    public void addingList(List<Product> profileDataList) {
        productArrayList.addAll(profileDataList);
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder customViewHolder, final int i) {
        final Product product = productArrayList.get(i);

        //Setting text view name and address
        customViewHolder.textViewSummery.setText(product.getProductSummary());
        customViewHolder.textViewDescription.setText(product.getProductDescription());
        customViewHolder.textViewCost.setText("$ " + String.valueOf(product.getProductCost()));

        String imageUrl = "";
        if (product.getProductFoto3() != null)
            imageUrl = product.getProductFoto3();
        if (product.getProductFoto2() != null)
            imageUrl = product.getProductFoto2();
        if (product.getProductFoto1() != null)
            imageUrl = product.getProductFoto1();

        if (!imageUrl.equalsIgnoreCase(""))
            Glide.with(context).load(imageUrl)
                    .centerCrop()
                    .placeholder(R.drawable.placeholder)
                    .crossFade()
                    .into(customViewHolder.imageViewBackGround);

        customViewHolder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, EditProductActivity.class);
                i.putExtra("productDetails", product.toString());
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (null != productArrayList ? productArrayList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        protected TextView textViewSummery;
        protected TextView textViewDescription;
        protected TextView textViewCost;
        protected CardView cardView;
        protected ImageView imageViewBackGround;
        protected LinearLayout mainLayout;

        public CustomViewHolder(View view) {
            super(view);
            this.textViewSummery = (TextView) view.findViewById(R.id.textViewSummery);
            this.textViewDescription = (TextView) view.findViewById(R.id.textViewDescription);
            this.textViewCost = (TextView) view.findViewById(R.id.textViewCost);
            this.cardView = (CardView) view.findViewById(R.id.cardView);
            this.imageViewBackGround = (ImageView) view.findViewById(R.id.imageViewBackGround);
            this.mainLayout = (LinearLayout) view.findViewById(R.id.mainLayout);
        }
    }
}
