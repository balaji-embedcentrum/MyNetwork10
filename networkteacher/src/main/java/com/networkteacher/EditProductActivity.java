package com.networkteacher;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.kbeanie.imagechooser.api.ChooserType;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.kbeanie.imagechooser.api.ChosenImages;
import com.kbeanie.imagechooser.api.ImageChooserListener;
import com.kbeanie.imagechooser.api.ImageChooserManager;
import com.networkteacher.models.Product;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditProductActivity extends BaseActivity implements
        ImageChooserListener {

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

    HashMap<Integer, byte[]> imageData = new HashMap<>(3);
    HashMap<Integer, String> imageUrl = new HashMap<>(3);
    ProgressDialog dialog;
    private int addedImage = 1;
    private ImageChooserManager imageChooserManager;
    private String filePath;
    private int chooserType;
    private boolean isActivityResultOver = false;

    private String originalFilePath;
    private String thumbnailFilePath;
    private String thumbnailSmallFilePath;

    private String objectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        ButterKnife.bind(this);

        Product product = new Gson().fromJson(getIntent().getStringExtra("productDetails"), Product.class);

        textViewSummery.setText(product.getProductSummary());
        textViewDescription.setText(product.getProductDescription());
        textViewCost.setText("" + product.getProductCost());
        activeSwitch.setChecked(true);
        objectId = product.getObjectId();

        if (product.getProductFoto1() != null) {
            imageUrl.put(0, product.getProductFoto1());
            addImage(product.getProductFoto1(), 0);
            addedImage++;
        }
        if (product.getProductFoto2() != null) {
            imageUrl.put(1, product.getProductFoto2());
            addImage(product.getProductFoto2(), 1);
            addedImage++;
        }
        if (product.getProductFoto3() != null) {
            imageUrl.put(2, product.getProductFoto3());
            addImage(product.getProductFoto3(), 2);
            addedImage++;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "OnActivityResult");
        Log.i(TAG, "File Path : " + filePath);
        Log.i(TAG, "Chooser Type: " + chooserType);
        if (resultCode == RESULT_OK
                && (requestCode == ChooserType.REQUEST_PICK_PICTURE || requestCode == ChooserType.REQUEST_CAPTURE_PICTURE)) {
            if (imageChooserManager == null) {
                reinitializeImageChooser();
            }
            imageChooserManager.submit(requestCode, data);
        }
    }

    private void reinitializeImageChooser() {
        imageChooserManager = new ImageChooserManager(this, chooserType, true);
        Bundle bundle = new Bundle();
        bundle.putBoolean(Intent.EXTRA_ALLOW_MULTIPLE, true);
        imageChooserManager.setExtras(bundle);
        imageChooserManager.setImageChooserListener(this);
        imageChooserManager.reinitialize(filePath);
    }

    private void addImage(final String productFotoUrl, final int position) {
        if (productFotoUrl != null) {
            LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View myView = vi.inflate(R.layout.custom_single_captured_image_layout, null);

            ImageView addNewImage = (ImageView) myView.findViewById(R.id.imageViewSingleImage);
            ImageView imageViewCloseButton = (ImageView) myView.findViewById(R.id.imageViewCloseButton);
            imageViewCloseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(EditProductActivity.this)
                            .setTitle("Confirm please")
                            .setMessage("Do you really want to remove this image?")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {
                                    myView.setVisibility(View.GONE);
                                    addedImage = addedImage - 1;
                                    imageUrl.remove(position);
                                }
                            })
                            .setNegativeButton(android.R.string.no, null).show();
                }
            });

            LinearLayoutImageList.addView(myView, 0, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

            Glide.with(this).load(productFotoUrl)
                    .centerCrop()
                    .placeholder(R.drawable.add_image_active)
                    .crossFade()
                    .into(addNewImage);
        }
    }

    @OnClick(R.id.addImage)
    public void addingImages() {
        if (addedImage <= 3)
            openSelectDialog();
        else
            Toast.makeText(this, "Max 3 image you can add.", Toast.LENGTH_LONG).show();
    }

    private void openSelectDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String[] selectorList = new String[]{
                "Gallery", "Camera"
        };
        builder.setItems(selectorList, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    chooseImage();
                } else {
                    takePicture();
                }
            }
        }).create().show();
    }

    private void chooseImage() {
        chooserType = ChooserType.REQUEST_PICK_PICTURE;
        imageChooserManager = new ImageChooserManager(this,
                ChooserType.REQUEST_PICK_PICTURE, true);
        Bundle bundle = new Bundle();
        bundle.putBoolean(Intent.EXTRA_ALLOW_MULTIPLE, true);
        imageChooserManager.setExtras(bundle);
        imageChooserManager.setImageChooserListener(this);
        imageChooserManager.clearOldFiles();
        try {
            filePath = imageChooserManager.choose();
        } catch (Exception e) {
            Log.d(TAG, "chooseImage Error: " + e);
        }
    }

    private void takePicture() {
        chooserType = ChooserType.REQUEST_CAPTURE_PICTURE;
        imageChooserManager = new ImageChooserManager(this, ChooserType.REQUEST_CAPTURE_PICTURE, true);
        imageChooserManager.setImageChooserListener(this);
        try {
            filePath = imageChooserManager.choose();
        } catch (Exception e) {
            Log.d(TAG, "chooseImage Error: " + e);
        }
    }

    public void saving(View view) {
        final String summery = textViewSummery.getText().toString().trim();
        final String description = textViewDescription.getText().toString().trim();
        final String cost = textViewCost.getText().toString().trim();
        if (validated(summery, description, cost)) {
            dialog = ProgressDialog.show(this, "Loading", "Please wait...", true);

            ParseQuery query = ParseQuery.getQuery("ProductData");
            query.whereEqualTo("objectId", objectId);
            query.setLimit(1);
            query.findInBackground(
                    new FindCallback() {
                        @Override
                        public void done(List list, ParseException e) {
                        }

                        @Override
                        public void done(Object o, Throwable throwable) {
                            List<ParseObject> list = (List<ParseObject>) o;
                            ParseObject john = list.get(0);

                            for (int i = 0; i < 3; i++) {
                                String ProductFoto = "ProductFoto" + (i + 1);
                                if (imageUrl.get(i) == null) {
                                    if (imageData.get(i) != null) {
                                        byte[] data = imageData.get(i);
                                        ParseFile fileObject = new ParseFile(System.currentTimeMillis() + ".jpg", data);
                                        john.put(ProductFoto, fileObject);
                                    } else
                                        john.remove(ProductFoto);
                                }

                                john.put("ProductSummary", summery);
                                john.put("ProductDescription", description);
                                john.put("ProductCost", Integer.parseInt(cost));
                                if (activeSwitch.isChecked())
                                    john.put("ProductStatus", "Active");
                                else
                                    john.put("ProductStatus", "Inactive");
                                final int finalI = i;
                                john.saveInBackground(new SaveCallback() {
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            if (finalI == 2) {
                                                dialog.dismiss();
                                                Toast.makeText(EditProductActivity.this, "Thanks for updating.", Toast.LENGTH_LONG).show();
                                                finish();
                                            }
                                        } else {
                                            Log.d(TAG, "error saving image: " + e);
                                            dialog.dismiss();
                                        }
                                    }
                                });
                            }
                        }
                    }
            );
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

    @Override
    public void onImageChosen(final ChosenImage image) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "Chosen Image: O - " + image.getFilePathOriginal());
                Log.i(TAG, "Chosen Image: T - " + image.getFileThumbnail());
                Log.i(TAG, "Chosen Image: Ts - " + image.getFileThumbnailSmall());
                isActivityResultOver = true;
                originalFilePath = image.getFilePathOriginal();
                thumbnailFilePath = image.getFileThumbnail();
                thumbnailSmallFilePath = image.getFileThumbnailSmall();

                if (image != null) {
                    for (int i = 0; i < 3; i++) {
                        if (imageUrl.get(i) == null && imageData.get(i) == null) {
                            try {
                                imageData.put(i, FileUtils.readFileToByteArray(new File(image.getFilePathOriginal())));
                                addedImage++;

                                LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                final View myView = vi.inflate(R.layout.custom_single_captured_image_layout, null);

                                ImageView addNewImage = (ImageView) myView.findViewById(R.id.imageViewSingleImage);
                                ImageView imageViewCloseButton = (ImageView) myView.findViewById(R.id.imageViewCloseButton);
                                final int finalI = i;
                                imageViewCloseButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        new AlertDialog.Builder(EditProductActivity.this)
                                                .setTitle("Confirm please")
                                                .setMessage("Do you really want to remove this image?")
                                                .setIcon(android.R.drawable.ic_dialog_alert)
                                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                                    public void onClick(DialogInterface dialog, int whichButton) {
                                                        myView.setVisibility(View.GONE);
                                                        addedImage = addedImage - 1;
                                                        imageData.remove(finalI);
                                                    }
                                                })
                                                .setNegativeButton(android.R.string.no, null).show();
                                    }
                                });

                                LinearLayoutImageList.addView(myView, 0, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                                Glide.with(EditProductActivity.this).load(Uri.fromFile(new File(image.getFileThumbnail())))
                                        .centerCrop()
                                        .placeholder(R.drawable.add_image_active)
                                        .crossFade()
                                        .into(addNewImage);
                                break;
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } else {
                    Log.i(TAG, "Chosen Image: Is null");
                }
            }
        });
    }

    @Override
    public void onImagesChosen(final ChosenImages images) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "On Images Chosen: " + images.size());
                onImageChosen(images.getImage(0));
            }
        });
    }

    @Override
    public void onError(final String reason) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Log.i(TAG, "OnError: " + reason);
//                pbar.setVisibility(View.GONE);
                Toast.makeText(EditProductActivity.this, reason,
                        Toast.LENGTH_LONG).show();
            }
        });
    }

//    public class MyAsync extends AsyncTask<String, Void, Bitmap> {
//
//        @Override
//        protected Bitmap doInBackground(String... params) {
//
//            HttpClient client = new DefaultHttpClient();
//            HttpResponse httpResponse;
//            Bitmap bmp = null;
//
//            try {
//                httpResponse = client.execute(new HttpGet(params[0]));
//                int responseCode = httpResponse.getStatusLine().getStatusCode();
//
//                HttpEntity entity = httpResponse.getEntity();
//
//                if (entity != null) {
//                    InputStream in = entity.getContent();
//                    bmp = BitmapFactory.decodeStream(in);
//                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                    bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
//
//                    imageData.put(Integer.parseInt(params[1]), stream.toByteArray());
//                    in.close();
//                }
//            } catch (ClientProtocolException e) {
//                client.getConnectionManager().shutdown();
//                e.printStackTrace();
//            } catch (IOException e) {
//                client.getConnectionManager().shutdown();
//                e.printStackTrace();
//            }
//            return bmp;
//        }
//
//        @Override
//        protected void onPostExecute(Bitmap bmp) {
//
//
//        }
//    }
}