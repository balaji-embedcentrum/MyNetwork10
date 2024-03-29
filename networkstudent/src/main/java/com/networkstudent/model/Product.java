package com.networkstudent.model;

import com.google.gson.Gson;

/**
 * Created by anirban on 18/02/2016.
 */
public class Product {
    private String ObjectId;
    private int ProfileCode;
    private String ProductStatus;
    private String ProductDescription;
    private String ProductSummary;
    private float ProductCost;
    private float ProductDiscount;
    private String ProductFoto1;
    private String ProductFoto2;
    private String ProductFoto3;

    public float getProductDiscount() {
        return ProductDiscount;
    }

    public void setProductDiscount(float productDiscount) {
        ProductDiscount = productDiscount;
    }

    public String getProductFoto1() {
        return ProductFoto1;
    }

    public void setProductFoto1(String productFoto1) {
        ProductFoto1 = productFoto1;
    }

    public String getProductFoto2() {
        return ProductFoto2;
    }

    public void setProductFoto2(String productFoto2) {
        ProductFoto2 = productFoto2;
    }

    public String getProductFoto3() {
        return ProductFoto3;
    }

    public void setProductFoto3(String productFoto3) {
        ProductFoto3 = productFoto3;
    }

    public float getProductCost() {
        return ProductCost;
    }

    public void setProductCost(float productCost) {
        ProductCost = productCost;
    }

    public String getObjectId() {
        return ObjectId;
    }

    public void setObjectId(String objectId) {
        ObjectId = objectId;
    }

    public int getProfileCode() {
        return ProfileCode;
    }

    public void setProfileCode(int profileCode) {
        ProfileCode = profileCode;
    }

    public String getProductStatus() {
        return ProductStatus;
    }

    public void setProductStatus(String productStatus) {
        ProductStatus = productStatus;
    }

    public String getProductDescription() {
        return ProductDescription;
    }

    public void setProductDescription(String productDescription) {
        ProductDescription = productDescription;
    }

    public String getProductSummary() {
        return ProductSummary;
    }

    public void setProductSummary(String productSummary) {
        ProductSummary = productSummary;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
