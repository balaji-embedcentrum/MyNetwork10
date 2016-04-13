package com.networkstudent;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.networkstudent.model.Product;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ProductDetailsViewActivity extends BaseActivity {

    private static final String TAG = "EditProductActivity";

    @Bind(R.id.LinearLayoutImageList)
    LinearLayout LinearLayoutImageList;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.textViewOrderId)
    TextView textViewOrderId;
    @Bind(R.id.textViewOrderStatus)
    TextView textViewOrderStatus;
    @Bind(R.id.textViewProductDetails)
    TextView textViewProductDetails;
    @Bind(R.id.textViewProductDescriptions)
    TextView textViewProductDescriptions;
    @Bind(R.id.textViewProductCost)
    TextView textViewProductCost;
    @Bind(R.id.buttonPickUp)
    Button buttonPickUp;
    @Bind(R.id.buttonCancel)
    Button buttonCancel;

    private int addedImage = 1;
    private String objectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archived_product_details);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Product product = new Gson().fromJson(getIntent().getStringExtra("productDetails"), Product.class);

        textViewOrderId.setText(getIntent().getStringExtra("orderCode"));
        textViewOrderStatus.setText(getIntent().getStringExtra("orderStatus"));
        textViewProductDetails.setText(product.getProductSummary());
        textViewProductDescriptions.setText(product.getProductDescription());
        textViewProductCost.setText("USD " + product.getProductCost());
        objectId = product.getObjectId();

        if (!getIntent().getStringExtra("orderStatus").equalsIgnoreCase("Ordered")) {
            buttonPickUp.setVisibility(View.GONE);
            buttonCancel.setVisibility(View.GONE);
        }

        if (product.getProductFoto1() != null)
            addImage(product.getProductFoto1(), 0);
        if (product.getProductFoto2() != null)
            addImage(product.getProductFoto2(), 1);
        if (product.getProductFoto3() != null)
            addImage(product.getProductFoto3(), 2);
    }

    private void addImage(final String productFotoUrl, final int position) {
        if (productFotoUrl != null) {
            LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
            final View myView = vi.inflate(R.layout.custom_single_captured_image_layout, null);

            ImageView productImageView = (ImageView) myView.findViewById(R.id.imageViewSingleImage);
            LinearLayoutImageList.addView(myView, 0, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            addedImage = addedImage + 1;

            productImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(ProductDetailsViewActivity.this, FullScreenImageActivity.class);
                    i.putExtra("imageUrl", productFotoUrl);
                    ProductDetailsViewActivity.this.startActivity(i);
                }
            });

            Glide.with(this).load(productFotoUrl)
                    .centerCrop()
                    .placeholder(R.drawable.placeholder)
                    .crossFade()
                    .into(productImageView);
        }
    }

    public void pickingUp(View view) {
    }

    public void cancelling(View view) {
    }
}