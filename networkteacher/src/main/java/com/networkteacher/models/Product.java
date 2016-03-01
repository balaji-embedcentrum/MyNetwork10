package com.networkteacher.models;

import com.google.gson.Gson;

/**
 * Created by anirban on 18/02/2016.
 */
public class Product {
    private String ObjectId;
    private String ProfileCode;
    private String ProductStatus;
    private String ProductDescription;
    private String ProductSummary;
    private int ProductCost;
    private String ProductFoto1;
    private String ProductFoto2;
    private String ProductFoto3;

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

    public int getProductCost() {
        return ProductCost;
    }

    public void setProductCost(int productCost) {
        ProductCost = productCost;
    }

    public String getObjectId() {
        return ObjectId;
    }

    public void setObjectId(String objectId) {
        ObjectId = objectId;
    }

    public String getProfileCode() {
        return ProfileCode;
    }

    public void setProfileCode(String profileCode) {
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
