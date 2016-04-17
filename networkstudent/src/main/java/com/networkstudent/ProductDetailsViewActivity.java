package com.networkstudent;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.networkstudent.model.Product;
import com.networkstudent.utils.ReusableClass;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ProductDetailsViewActivity extends BaseActivity {

    private static final String TAG = "EditProductActivity";

    @Bind(R.id.LinearLayoutImageList)
    LinearLayout LinearLayoutImageList;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.textViewProductDetails)
    TextView textViewProductDetails;
    @Bind(R.id.textViewProductDescriptions)
    TextView textViewProductDescriptions;
    @Bind(R.id.textViewProductCost)
    TextView textViewProductCost;


    private int addedImage = 1;
    private String orderCode;
    private Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archived_order_details);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        product = new Gson().fromJson(getIntent().getStringExtra("productDetails"), Product.class);
        orderCode = getIntent().getStringExtra("orderCode");

        textViewProductDetails.setText(product.getProductSummary());
        textViewProductDescriptions.setText(product.getProductDescription());
        textViewProductCost.setText("USD " + product.getProductCost());

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

    public void ordering(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ProductDetailsViewActivity.this);

        builder.setMessage("Are you sure, you want to place the order?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        SimpleDateFormat year = new SimpleDateFormat("yy");
                        SimpleDateFormat month = new SimpleDateFormat("MM");
                        SimpleDateFormat date = new SimpleDateFormat("dd");
                        Date now = new Date();

                        String oderNo = year.format(now) + month.format(now) + date.format(now) + "-" + randomString(4);

                        final ProgressDialog progressDialog = ProgressDialog.show(ProductDetailsViewActivity.this, "Loading", "Please wait...", true);

                        ParseObject parseObject = new ParseObject("OrderData");
                        parseObject.put("ProductCode", product.getObjectId());
                        parseObject.put("ProductCost", product.getProductCost());
                        parseObject.put("OrderCode", oderNo);
                        parseObject.put("StudentPhone", ReusableClass.getFromPreference("session", ProductDetailsViewActivity.this));
                        parseObject.put("TeacherCode", product.getProfileCode());
                        parseObject.put("ProductSummary", product.getProductSummary());
                        parseObject.put("OrderStatus", "Ordered");
                        parseObject.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    Intent i = new Intent(ProductDetailsViewActivity.this, OrderedProductListActivity.class);
                                    startActivity(i);
                                    finish();
                                    Toast.makeText(ProductDetailsViewActivity.this, "Thanks for placing the order.", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(ProductDetailsViewActivity.this, "Sorry error occurred. Try again.", Toast.LENGTH_LONG).show();
                                }
                                progressDialog.dismiss();
                            }
                        });
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .show();
    }


    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    static SecureRandom rnd = new SecureRandom();

    String randomString(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }


}