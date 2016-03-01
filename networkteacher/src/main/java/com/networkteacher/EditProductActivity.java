package com.networkteacher;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
import com.networkteacher.SelectPhotoUtils.AlbumStorageDirFactory;
import com.networkteacher.SelectPhotoUtils.BaseAlbumDirFactory;
import com.networkteacher.SelectPhotoUtils.FroyoAlbumDirFactory;
import com.networkteacher.models.Product;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditProductActivity extends BaseActivity {

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
        setContentView(R.layout.activity_product_details);
        ButterKnife.bind(this);

        Product product = new Gson().fromJson(getIntent().getStringExtra("productDetails"), Product.class);

        textViewSummery.setText(product.getProductSummary());
        textViewDescription.setText(product.getProductDescription());
        textViewCost.setText("" + product.getProductCost());
        activeSwitch.setChecked(true);
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
                    openSelection();
                } else {
                    // openCamera();
                    dispatchTakePictureIntent();
                }
            }
        }).create().show();
    }

    private void openSelection() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 2);
    }

    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    private void dispatchTakePictureIntent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
        } else {
            mAlbumStorageDirFactory = new BaseAlbumDirFactory();
        }

        File f = null;

        try {
            f = setUpPhotoFile();
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
            startActivityForResult(cameraIntent, 1);
        } catch (Exception e) {
            Log.d("TAG", "Error while cam opening: " + e);
        }
    }

    private File setUpPhotoFile() throws IOException {
        File f = createImageFile();
        mCurrentPhotoPath = f.getAbsolutePath();

        return f;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File albumF = getAlbumDir();
        File imageF = File.createTempFile(imageFileName, ".jpg", albumF);
        return imageF;
    }

    private File getAlbumDir() {
        File storageDir = null;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            storageDir = mAlbumStorageDirFactory.getAlbumStorageDir("ProfileCam");

            if (storageDir != null) {
                if (!storageDir.mkdirs()) {
                    if (!storageDir.exists()) {
                        Log.d("CameraSample", "failed to create directory");
                        return null;
                    }
                }
            }

        } else {
            Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
        }

        return storageDir;
    }


    private void setPic() {
        LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View myView = vi.inflate(R.layout.custom_single_captured_image_layout, null);

        ImageView addNewImage = (ImageView) myView.findViewById(R.id.imageViewSingleImage);
        ImageView imageViewCloseButton = (ImageView) myView.findViewById(R.id.imageViewCloseButton);

        int targetW = addNewImage.getWidth();
        int targetH = addNewImage.getHeight();

        ExifInterface exif = null;
        try {
            exif = new ExifInterface(mCurrentPhotoPath);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        int angle = 0;

        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
            angle = 90;
            Log.e("Tag", "rotation 90");
        } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
            angle = 180;
            Log.e("Tag", "rotation 180");
        } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
            angle = 270;
            Log.e("Tag", "rotation 270");
        }

        Matrix mat = new Matrix();
        mat.postRotate(angle);

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        }

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        Bitmap bitmap_before_rotate = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        Bitmap bitmap;

        Log.e("Tag", "land angle " + angle);

        if (angle != 0) {
            bitmap = Bitmap.createBitmap(bitmap_before_rotate, 0, 0, bitmap_before_rotate.getWidth(),
                    bitmap_before_rotate.getHeight(), mat, true);
        } else {
            Log.e("Tag", "land scape");
            bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        }
        imagePath.add(new String(mCurrentPhotoPath));
        addNewImage.setImageBitmap(bitmap);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        final byte[] byteArray = stream.toByteArray();
        imageData.add(byteArray);

        final int indexToRemove = imagePath.indexOf(mCurrentPhotoPath);
        imageViewCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(EditProductActivity.this)
                        .setTitle("Confirm please")
                        .setMessage("Do you really want to remove this image?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                imagePath.remove(indexToRemove);
                                myView.setVisibility(View.GONE);
                                addedImage = addedImage - 1;
                                imageData.remove(byteArray);
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });
        addedImage = addedImage + 1;
        LinearLayoutImageList.addView(myView, 0, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == 1) {
                if (mCurrentPhotoPath != null) {
                    setPic();
                    galleryAddPic();
                    mCurrentPhotoPath = null;
                }
            } else if (requestCode == 2) {
                Uri selectedImage = data.getData();
                String[] filePath = {MediaStore.Images.Media.DATA};

                Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                c.close();

                ExifInterface exif = null;
                try {
                    exif = new ExifInterface(picturePath);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                int orientation = exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL);

                int angle = 0;

                if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                    angle = 90;
                    Log.e("Tag", "rotation");
                } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                    angle = 180;
                    Log.e("Tag", "rotation");
                } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                    angle = 270;
                    Log.e("Tag", "rotation");
                }

                Matrix mat = new Matrix();
                mat.postRotate(angle);

                LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View myView = vi.inflate(R.layout.custom_single_captured_image_layout, null);

                ImageView addNewImage = (ImageView) myView.findViewById(R.id.imageViewSingleImage);
                ImageView imageViewCloseButton = (ImageView) myView.findViewById(R.id.imageViewCloseButton);

                int targetW = addNewImage.getWidth();
                int targetH = addNewImage.getHeight();

                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                bmOptions.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(picturePath, bmOptions);
                int photoW = bmOptions.outWidth;
                int photoH = bmOptions.outHeight;

                int scaleFactor = 1;
                if ((targetW > 0) || (targetH > 0)) {
                    scaleFactor = Math.min(photoW / targetW, photoH / targetH);
                }

                bmOptions.inJustDecodeBounds = false;
                bmOptions.inSampleSize = scaleFactor;
                bmOptions.inPurgeable = true;

                Bitmap bitmap;
                Bitmap bitmap_before_rotate = BitmapFactory.decodeFile(picturePath, bmOptions);
                if (angle != 0) {
                    bitmap = Bitmap.createBitmap(bitmap_before_rotate, 0, 0, bitmap_before_rotate.getWidth(),
                            bitmap_before_rotate.getHeight(), mat, true);
                } else {
                    Log.e("Tag", "land scape");
                    bitmap = BitmapFactory.decodeFile(picturePath, bmOptions);
                }
                imagePath.add(new String(picturePath));
                addNewImage.setImageBitmap(bitmap);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                final byte[] byteArray = stream.toByteArray();
                imageData.add(byteArray);

                final int indexToRemove = imagePath.indexOf(new String(picturePath));

                imageViewCloseButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new AlertDialog.Builder(EditProductActivity.this)
                                .setTitle("Confirm please")
                                .setMessage("Do you really want to remove this image?")
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        imagePath.remove(indexToRemove);
                                        myView.setVisibility(View.GONE);
                                        addedImage = addedImage - 1;
                                        imageData.remove(byteArray);
                                    }
                                })
                                .setNegativeButton(android.R.string.no, null).show();
                    }
                });
                LinearLayoutImageList.addView(myView, 0, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                addedImage = addedImage + 1;
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

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
                                if (imageData.size() >= (i + 1)) {
                                    byte[] data = imageData.get(i);
                                    ParseFile fileObject = new ParseFile(System.currentTimeMillis() + ".jpg", data);
                                    john.put(ProductFoto, fileObject);
                                } else
                                    john.remove(ProductFoto);

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