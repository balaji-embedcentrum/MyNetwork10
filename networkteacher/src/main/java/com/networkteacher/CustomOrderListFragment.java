package com.networkteacher;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.networkteacher.models.OrderData;
import com.networkteacher.utils.ReusableClass;
import com.networkteacher.widget.MyRecyclerAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Dream on 13-Dec-15.
 */
public class CustomOrderListFragment extends Fragment {

    private static final String TAG = "MYTAG";
    @Bind(R.id.my_recycler_view)
    RecyclerView myRecyclerView;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    @Bind(R.id.textViewNoData)
    TextView textViewNoData;
    private RecyclerView.LayoutManager mLayoutManager;
    private MyRecyclerAdapter mAdapter;

    String orderStatus;
    Context context;

    public CustomOrderListFragment() {
    }

    public CustomOrderListFragment(String orderStatus, Context context) {
        this.orderStatus = orderStatus;
        this.context = context;

        mAdapter = new MyRecyclerAdapter(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_custom_order_layout, container, false);
        ButterKnife.bind(this, rootView);

        populateList();
        myRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        myRecyclerView.setLayoutManager(mLayoutManager);
        myRecyclerView.setAdapter(mAdapter);

        return rootView;
    }

    private void populateList() {
        progressBar.setVisibility(View.VISIBLE);
        final ArrayList<OrderData> orderDataList = new ArrayList<>();

        ParseQuery<ParseObject> queryOrder = ParseQuery.getQuery("OrderData");
        queryOrder.whereEqualTo("OrderStatus", orderStatus);
        queryOrder.whereEqualTo("TeacherCode", Integer.parseInt(ReusableClass.getFromPreference("profileCode", getContext())));
        queryOrder.findInBackground(
                new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> orderList, ParseException e) {
                        if (e == null) {
                            if (orderList.size() > 0) {
                                for (ParseObject singleOrder : orderList) {
                                    final OrderData orderData = new OrderData();
                                    orderData.setObjectId(singleOrder.getObjectId());
                                    orderData.setCurrentLocation(singleOrder.getParseGeoPoint("CurrentLocation"));
                                    orderData.setOrderCode(singleOrder.getString("OrderCode"));
                                    orderData.setOrderStatus(singleOrder.getString("OrderStatus"));
                                    orderData.setProductCost(singleOrder.getInt("ProductCost"));
                                    orderData.setProductCode(singleOrder.getString("ProductCode"));
                                    orderData.setStudentPhone(singleOrder.getString("StudentPhone"));
                                    orderData.setTeacherCode(singleOrder.getInt("TeacherCode"));
                                    orderData.setProductSummary(singleOrder.getString("ProductSummary"));
                                    orderData.setProductDesciption(singleOrder.getString("ProductDescription"));

                                    orderDataList.add(orderData);
                                }
                                mAdapter.addAll(orderDataList);
                            } else {
                                Log.d(TAG, "done: No Value");
                                textViewNoData.setVisibility(View.VISIBLE);
                            }
                        } else {
                            Log.d("score", "Error: " + e.getMessage());
                        }
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                }
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
