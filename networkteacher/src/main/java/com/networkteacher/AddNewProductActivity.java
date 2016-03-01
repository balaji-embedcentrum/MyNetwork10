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
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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
//                    openSelection();
                    chooseImage();
                } else {
                    // openCamera();
//                    dispatchTakePictureIntent();
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
//            pbar.setVisibility(View.VISIBLE);
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
//            pbar.setVisibility(View.VISIBLE);
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

//    private void openSelection() {
//        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        startActivityForResult(intent, 2);
//    }

    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//    private void dispatchTakePictureIntent() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        } else {
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
//            mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
//        } else {
//            mAlbumStorageDirFactory = new BaseAlbumDirFactory();
//        }
//
//        File f = null;
//
//        try {
//            f = setUpPhotoFile();
//            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
//            startActivityForResult(cameraIntent, 1);
//        } catch (Exception e) {
//            Log.d("TAG", "Error while cam opening: " + e);
//        }
//    }
//
//    private File setUpPhotoFile() throws IOException {
//        File f = createImageFile();
//        mCurrentPhotoPath = f.getAbsolutePath();
//
//        return f;
//    }
//
//    private File createImageFile() throws IOException {
//        // Create an image file name
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        String imageFileName = "IMG_" + timeStamp + "_";
//        File albumF = getAlbumDir();
//        File imageF = File.createTempFile(imageFileName, ".jpg", albumF);
//        return imageF;
//    }
//
//    private File getAlbumDir() {
//        File storageDir = null;
//
//        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
//            storageDir = mAlbumStorageDirFactory.getAlbumStorageDir("ProfileCam");
//
//            if (storageDir != null) {
//                if (!storageDir.mkdirs()) {
//                    if (!storageDir.exists()) {
//                        Log.d("CameraSample", "failed to create directory");
//                        return null;
//                    }
//                }
//            }
//
//        } else {
//            Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
//        }
//
//        return storageDir;
//    }
//
//
//    private void setPic() {
//        LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        final View myView = vi.inflate(R.layout.custom_single_captured_image_layout, null);
//
//        ImageView addNewImage = (ImageView) myView.findViewById(R.id.imageViewSingleImage);
//        ImageView imageViewCloseButton = (ImageView) myView.findViewById(R.id.imageViewCloseButton);
//
//        int targetW = addNewImage.getWidth();
//        int targetH = addNewImage.getHeight();
//
//        ExifInterface exif = null;
//        try {
//            exif = new ExifInterface(mCurrentPhotoPath);
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
//        int angle = 0;
//
//        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
//            angle = 90;
//            Log.e("Tag", "rotation 90");
//        } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
//            angle = 180;
//            Log.e("Tag", "rotation 180");
//        } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
//            angle = 270;
//            Log.e("Tag", "rotation 270");
//        }
//
//        Matrix mat = new Matrix();
//        mat.postRotate(angle);
//
//        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
//        bmOptions.inJustDecodeBounds = true;
//        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
//        int photoW = bmOptions.outWidth;
//        int photoH = bmOptions.outHeight;
//
//        int scaleFactor = 1;
//        if ((targetW > 0) || (targetH > 0)) {
//            scaleFactor = Math.min(photoW / targetW, photoH / targetH);
//        }
//
//        bmOptions.inJustDecodeBounds = false;
//        bmOptions.inSampleSize = scaleFactor;
//
//        Bitmap bitmap_before_rotate = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
//        Bitmap bitmap;
//
//        Log.e("Tag", "land angle " + angle);
//
//        if (angle != 0) {
//            bitmap = Bitmap.createBitmap(bitmap_before_rotate, 0, 0, bitmap_before_rotate.getWidth(),
//                    bitmap_before_rotate.getHeight(), mat, true);
//        } else {
//            Log.e("Tag", "land scape");
//            bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
//        }
//        imagePath.add(new String(mCurrentPhotoPath));
//        addNewImage.setImageBitmap(bitmap);
//
//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.PNG, 80, stream);
//        final byte[] byteArray = stream.toByteArray();
//        imageData.add(byteArray);
//
//        final int indexToRemove = imagePath.indexOf(mCurrentPhotoPath);
//        imageViewCloseButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new AlertDialog.Builder(AddNewProductActivity.this)
//                        .setTitle("Confirm please")
//                        .setMessage("Do you really want to remove this image?")
//                        .setIcon(android.R.drawable.ic_dialog_alert)
//                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//
//                            public void onClick(DialogInterface dialog, int whichButton) {
//                                imagePath.remove(indexToRemove);
//                                myView.setVisibility(View.GONE);
//                                addedImage = addedImage - 1;
//                                imageData.remove(byteArray);
//                            }
//                        })
//                        .setNegativeButton(android.R.string.no, null).show();
//            }
//        });
//        addedImage = addedImage + 1;
//        LinearLayoutImageList.addView(myView, 0, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
//    }
//
//    private void galleryAddPic() {
//        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
//        File f = new File(mCurrentPhotoPath);
//        Uri contentUri = Uri.fromFile(f);
//        mediaScanIntent.setData(contentUri);
//        this.sendBroadcast(mediaScanIntent);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        try {
//            if (requestCode == 1) {
//                if (mCurrentPhotoPath != null) {
//                    setPic();
//                    galleryAddPic();
//                    mCurrentPhotoPath = null;
//                }
//            } else if (requestCode == 2) {
//                Uri selectedImage = data.getData();
//                String[] filePath = {MediaStore.Images.Media.DATA};
//
//                Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
//                c.moveToFirst();
//                int columnIndex = c.getColumnIndex(filePath[0]);
//                String picturePath = c.getString(columnIndex);
//                c.close();
//
//                ExifInterface exif = null;
//                try {
//                    exif = new ExifInterface(picturePath);
//                } catch (IOException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//                int orientation = exif.getAttributeInt(
//                        ExifInterface.TAG_ORIENTATION,
//                        ExifInterface.ORIENTATION_NORMAL);
//
//                int angle = 0;
//
//                if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
//                    angle = 90;
//                    Log.e("Tag", "rotation");
//                } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
//                    angle = 180;
//                    Log.e("Tag", "rotation");
//                } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
//                    angle = 270;
//                    Log.e("Tag", "rotation");
//                }
//
//                Matrix mat = new Matrix();
//                mat.postRotate(angle);
//
//                LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                final View myView = vi.inflate(R.layout.custom_single_captured_image_layout, null);
//
//                ImageView addNewImage = (ImageView) myView.findViewById(R.id.imageViewSingleImage);
//                ImageView imageViewCloseButton = (ImageView) myView.findViewById(R.id.imageViewCloseButton);
//
//                int targetW = addNewImage.getWidth();
//                int targetH = addNewImage.getHeight();
//
//                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
//                bmOptions.inJustDecodeBounds = true;
//                BitmapFactory.decodeFile(picturePath, bmOptions);
//                int photoW = bmOptions.outWidth;
//                int photoH = bmOptions.outHeight;
//
//                int scaleFactor = 1;
//                if ((targetW > 0) || (targetH > 0)) {
//                    scaleFactor = Math.min(photoW / targetW, photoH / targetH);
//                }
//
//                bmOptions.inJustDecodeBounds = false;
//                bmOptions.inSampleSize = scaleFactor;
//                bmOptions.inPurgeable = true;
//
//                Bitmap bitmap;
//                Bitmap bitmap_before_rotate = BitmapFactory.decodeFile(picturePath, bmOptions);
//                if (angle != 0) {
//                    bitmap = Bitmap.createBitmap(bitmap_before_rotate, 0, 0, bitmap_before_rotate.getWidth(),
//                            bitmap_before_rotate.getHeight(), mat, true);
//                } else {
//                    Log.e("Tag", "land scape");
//                    bitmap = BitmapFactory.decodeFile(picturePath, bmOptions);
//                }
//                imagePath.add(new String(picturePath));
//                addNewImage.setImageBitmap(bitmap);
//                ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                bitmap.compress(Bitmap.CompressFormat.PNG, 80, stream);
//                final byte[] byteArray = stream.toByteArray();
//                imageData.add(byteArray);
//
//                final int indexToRemove = imagePath.indexOf(new String(picturePath));
//
//                imageViewCloseButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        new AlertDialog.Builder(AddNewProductActivity.this)
//                                .setTitle("Confirm please")
//                                .setMessage("Do you really want to remove this image?")
//                                .setIcon(android.R.drawable.ic_dialog_alert)
//                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//
//                                    public void onClick(DialogInterface dialog, int whichButton) {
//                                        imagePath.remove(indexToRemove);
//                                        myView.setVisibility(View.GONE);
//                                        addedImage = addedImage - 1;
//                                        imageData.remove(byteArray);
//                                    }
//                                })
//                                .setNegativeButton(android.R.string.no, null).show();
//                    }
//                });
//                LinearLayoutImageList.addView(myView, 0, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
//                addedImage = addedImage + 1;
//            }
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    public void saving(View view) {
        final String summery = textViewSummery.getText().toString().trim();
        final String description = textViewDescription.getText().toString().trim();
        final String cost = textViewCost.getText().toString().trim();
        if (validated(summery, description, cost)) {
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
                john.put("ProductCost", Integer.parseInt(cost));
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

    private boolean validated(String summery, String description, String cost) {
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