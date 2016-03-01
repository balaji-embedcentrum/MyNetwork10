package com.networkteacher;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.networkteacher.models.Product;
import com.networkteacher.utils.ReusableClass;
import com.networkteacher.widget.ProductListRecyclerAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {
    private static final String TAG = "MyTAG";
    @Bind(R.id.my_recycler_view)
    RecyclerView myRecyclerView;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    @Bind(R.id.textViewNoData)
    TextView textViewNoData;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.fab)
    FloatingActionButton fab;
    @Bind(R.id.progress_container)
    LinearLayout progressContainer;
    private RecyclerView.LayoutManager mLayoutManager;
    private ProductListRecyclerAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        mAdapter = new ProductListRecyclerAdapter(this);
        myRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        myRecyclerView.setLayoutManager(mLayoutManager);
        myRecyclerView.setAdapter(mAdapter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAdapter.getItemCount() < 3) {
                    Intent i = new Intent(MainActivity.this, AddNewProductActivity.class);
                    startActivity(i);
                } else
                    Toast.makeText(MainActivity.this, "Already 3 Activated product is displaying.", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        textViewNoData.setVisibility(View.INVISIBLE);
        progressContainer.setVisibility(View.VISIBLE);
        loadData();
    }

    private void loadData() {
        final ArrayList<Product> productArrayList = new ArrayList<>();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("ProductData");
        query.whereEqualTo("ProfileCode", Integer.parseInt(ReusableClass.getFromPreference("profileCode", this)));
        query.whereEqualTo("ProductStatus", "Active");
        query.findInBackground(
                new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> profileList, ParseException e) {
                        if (e == null) {
                            if (profileList.size() > 0) {
                                for (int i = 0; i < profileList.size(); i++) {
                                    ParseObject p = profileList.get(i);

                                    Log.d(TAG, "done: " + p.getString("ProductDescription"));
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
                                mAdapter.addAll(productArrayList);
                                fab.setVisibility(View.VISIBLE);
                            } else {
                                Log.d(TAG, "done: No Value");
                                fab.setVisibility(View.VISIBLE);
                                textViewNoData.setVisibility(View.VISIBLE);
                            }
                        } else {
                            Log.d("score", "Error: " + e.getMessage());
                        }
                        progressContainer.setVisibility(View.INVISIBLE);
                    }
                }
        );
    }
}
