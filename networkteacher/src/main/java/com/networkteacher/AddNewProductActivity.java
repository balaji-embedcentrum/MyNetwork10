package com.networkteacher;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.kbeanie.imagechooser.api.ChooserType;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.kbeanie.imagechooser.api.ChosenImages;
import com.kbeanie.imagechooser.api.ImageChooserListener;
import com.kbeanie.imagechooser.api.ImageChooserManager;
import com.networkteacher.SelectPhotoUtils.AlbumStorageDirFactory;
import com.networkteacher.utils.DecimalDigitsInputFilter;
import com.networkteacher.utils.ReusableClass;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import org.apache.commons.io.FileUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddNewProductActivity extends BaseActivity implements
        ImageChooserListener {

    private static final String TAG = "AddNewProductActivity";
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
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.textViewDiscount)
    AppCompatEditText textViewDiscount;
    @Bind(R.id.DiscountWrapper)
    TextInputLayout DiscountWrapper;
    @Bind(R.id.allImages)
    HorizontalScrollView allImages;
    private String mCurrentPhotoPath;
    private int addedImage = 1;
    private ImageChooserManager imageChooserManager;
    private String filePath;
    private int chooserType;
    private boolean isActivityResultOver = false;

    private String originalFilePath;
    private String thumbnailFilePath;
    private String thumbnailSmallFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        textViewCost.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(2)});
        textViewDiscount.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(2)});
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
                    new AlertDialog.Builder(AddNewProductActivity.this)
                            .setTitle("Confirm please")
                            .setMessage("Do you really want to remove this image?")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {
                                    myView.setVisibility(View.GONE);
                                    addedImage = addedImage - 1;
                                    imageData.remove(position);
                                }
                            })
                            .setNegativeButton(android.R.string.no, null).show();
                }
            });

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
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void takePicture() {
        chooserType = ChooserType.REQUEST_CAPTURE_PICTURE;
        imageChooserManager = new ImageChooserManager(this,
                ChooserType.REQUEST_CAPTURE_PICTURE, true);
        imageChooserManager.setImageChooserListener(this);
        try {
            filePath = imageChooserManager.choose();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
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
        } else {
//            pbar.setVisibility(View.GONE);
        }
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
//                pbar.setVisibility(View.GONE);
                if (image != null) {
                    Log.i(TAG, "Chosen Image: Is not null");
//                    textViewFile.setText(image.getFilePathOriginal());
//                    loadImage(addNewImage, image.getFileThumbnail());
//                    loadImage(addNewImage, image.getFileThumbnailSmall());

                    LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    final View myView = vi.inflate(R.layout.custom_single_captured_image_layout, null);

                    ImageView addNewImage = (ImageView) myView.findViewById(R.id.imageViewSingleImage);
                    ImageView imageViewCloseButton = (ImageView) myView.findViewById(R.id.imageViewCloseButton);
                    imageViewCloseButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new AlertDialog.Builder(AddNewProductActivity.this)
                                    .setTitle("Confirm please")
                                    .setMessage("Do you really want to remove this image?")
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            myView.setVisibility(View.GONE);
                                            addedImage = addedImage - 1;
                                            try {
                                                imageData.remove(FileUtils.readFileToByteArray(new File(image.getFilePathOriginal())));
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    })
                                    .setNegativeButton(android.R.string.no, null).show();
                        }
                    });

                    addNewImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(AddNewProductActivity.this, FullScreenImageActivity.class);
                            i.putExtra("imageUrl", image.getFilePathOriginal());
                            AddNewProductActivity.this.startActivity(i);
                        }
                    });

                    LinearLayoutImageList.addView(myView, 0, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                    addedImage = addedImage + 1;

                    Glide.with(AddNewProductActivity.this).load(Uri.fromFile(new File(image.getFileThumbnail())))
                            .centerCrop()
                            .placeholder(R.drawable.add_image_active)
                            .crossFade()
                            .into(addNewImage);

//                    new MyAsync().execute(productFotoUrl);
                    try {
                        imageData.add(FileUtils.readFileToByteArray(new File(image.getFilePathOriginal())));
                    } catch (IOException e) {
                        e.printStackTrace();
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
                Toast.makeText(AddNewProductActivity.this, reason,
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void reinitializeImageChooser() {
        imageChooserManager = new ImageChooserManager(this, chooserType, true);
        Bundle bundle = new Bundle();
        bundle.putBoolean(Intent.EXTRA_ALLOW_MULTIPLE, true);
        imageChooserManager.setExtras(bundle);
        imageChooserManager.setImageChooserListener(this);
        imageChooserManager.reinitialize(filePath);
    }

    public void saving(View view) {
        final String summery = textViewSummery.getText().toString().trim();
        final String description = textViewDescription.getText().toString().trim();
        final String cost = textViewCost.getText().toString().trim();
        final String discount = textViewDiscount.getText().toString().trim();
        if (validated(summery, description, cost, discount)) {
            dialog = ProgressDialog.show(this, "Loading", "Please wait...", true);

            ParseObject john = new ParseObject("ProductData");

            for (int i = 0; i < 3; i++) {
                String ProductFoto = "ProductFoto" + (i + 1);
                if (imageData.size() >= (i + 1)) {
                    byte[] data = imageData.get(i);
                    ParseFile fileObject = new ParseFile(System.currentTimeMillis() + ".jpg", data);
                    john.put(ProductFoto, fileObject);
                } else
                    john.remove(ProductFoto);
                john.put("ProfileCode", Integer.parseInt(ReusableClass.getFromPreference("profileCode", AddNewProductActivity.this)));
                john.put("ProductSummary", summery);
                john.put("ProductDescription", description);
                john.put("ProductCost", Float.parseFloat(cost));
                john.put("ProductDiscount", Float.parseFloat(discount));
                if (activeSwitch.isActivated())
                    john.put("ProductStatus", "Inactive");
                else
                    john.put("ProductStatus", "Active");

                final int finalI = i;
                john.saveInBackground(new SaveCallback() {
                    public void done(ParseException e) {
                        if (e == null) {
                            if (finalI == 2) {
                                dialog.dismiss();
                                Toast.makeText(AddNewProductActivity.this, "Thanks for updating.", Toast.LENGTH_LONG).show();
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

    private boolean validated(String summery, String description, String cost, String discount) {
        if (TextUtils.isEmpty(summery)) {
            SummeryWrapper.setError("Summery name cannot be empty");
            textViewSummery.requestFocus();
            return false;
        } else
            SummeryWrapper.setError(null);
        if (TextUtils.isEmpty(description)) {
            DescriptionWrapper.setError("Description cannot be empty");
            textViewDescription.requestFocus();
            return false;
        } else
            DescriptionWrapper.setError(null);
        if (TextUtils.isEmpty(cost)) {
            CostWrapper.setError("Cost cannot be empty");
            textViewCost.requestFocus();
            return false;
        } else
            CostWrapper.setError(null);
        if (TextUtils.isEmpty(discount)) {
            DiscountWrapper.setError("If no discount put 0");
            textViewDiscount.requestFocus();
            return false;
        } else
            DiscountWrapper.setError(null);
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