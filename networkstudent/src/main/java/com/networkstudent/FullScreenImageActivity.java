package com.networkstudent;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FullScreenImageActivity extends AppCompatActivity {

    @Bind(R.id.imageViewFullScreen)
    ImageView imageViewFullScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);
        ButterKnife.bind(this);

        String url = getIntent().getStringExtra("imageUrl");
        Glide.with(this).load(url)
                .centerCrop()
                .placeholder(R.drawable.placeholder)
                .crossFade()
                .into(imageViewFullScreen);
    }
}
