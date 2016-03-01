package com.networkteacher;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.networkteacher.SelectPhotoUtils.AlbumStorageDirFactory;
import com.networkteacher.models.Product;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ArchivedProductDetailsActivity extends BaseActivity {

    private static final String TAG = "EditProductActivity";
    @Bind(R.id.ActiveSwitch)
    SwitchCompat activeSwitch;
    @Bind(R.id.TextViewSummery)
    AppCompatEditText textViewSummery;
    @Bind(R.id.SummeryWrapper)
    TextInputLayout SummeryWrapper;
    @Bind(R.id.textViewDescription)
    AppCompatEditText textViewDescription;
    @Bind(R.id.DescriptionWrapper)
    TextInputLayout DescriptionWrapper;
    @Bind(R.id.textViewCost)
    AppCompatEditText textViewCost;
    @Bind(R.id.CostWrapper)
    TextInputLayout CostWrapper;
    @Bind(R.id.LinearLayoutImageList)
    LinearLayout LinearLayoutImageList;

    AlbumStorageDirFactory mAlbumStorageDirFactory = null;
    ArrayList<String> imagePath = new ArrayList<String>();
    ArrayList<byte[]> imageData = new ArrayList<>();
    ProgressDialog dialog;
    private String mCurrentPhotoPath;
    private int addedImage = 1;
    private String objectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archived_product_details);
        ButterKnife.bind(this);

        Product product = new Gson().fromJson(getIntent().getStringExtra("productDetails"), Product.class);

        textViewSummery.setText(product.getProductSummary());
        textViewDescription.setText(product.getProductDescription());
        textViewCost.setText("" + product.getProductCost());
        activeSwitch.setChecked(false);
        objectId = product.getObjectId();

        if (product.getProductFoto1() != null)
            addImage(product.getProductFoto1(), 0);
        if (product.getProductFoto2() != null)
            addImage(product.getProductFoto2(), 1);
        if (product.getProductFoto3() != null)
            addImage(product.getProductFoto3(), 2);
    }

    private void addImage(final String productFotoUrl, final int position) {
        if (productFotoUrl != null) {
            LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View myView = vi.inflate(R.layout.custom_single_captured_image_layout, null);

            ImageView addNewImage = (ImageView) myView.findViewById(R.id.imageViewSingleImage);
            ImageView imageViewCloseButton = (ImageView) myView.findViewById(R.id.imageViewCloseButton);
            imageViewCloseButton.setVisibility(View.INVISIBLE);
            LinearLayoutImageList.addView(myView, 0, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            addedImage = addedImage + 1;

            Glide.with(this).load(productFotoUrl)
                    .centerCrop()
                    .placeholder(R.drawable.add_image_active)
                    .crossFade()
                    .into(addNewImage);

            new MyAsync().execute(productFotoUrl);
        }
    }

    private boolean validated(String summery, String description, String cost) {
        if (TextUtils.isEmpty(summery)) {
            SummeryWrapper.setError("Summery name cannot be empty");
            textViewSummery.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(description)) {
            DescriptionWrapper.setError("Description cannot be empty");
            textViewDescription.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(cost)) {
            CostWrapper.setError("Cost cannot be empty");
            textViewCost.requestFocus();
            return false;
        }
        return true;
    }

    public class MyAsync extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {

            try {
                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                myBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

                imageData.add(stream.toByteArray());
                return myBitmap;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bmp) {


        }
    }
}