package com.networkstudent.model;

import com.google.gson.Gson;
import com.parse.ParseGeoPoint;

/**
 * Created by Dream on 03-Jan-16.
 */
public class OrderData {
    private String objectId;
    private String OrderCode;
    private int TeacherCode;
    private String ProductCode;
    private int ProductCost;
    private String OrderStatus;
    private String StudentPhone;
    private ParseGeoPoint CurrentLocation;
    private String ProductSummary;
    private String productDescription;

    public String getProductSummary() {
        return ProductSummary;
    }

    public void setProductSummary(String productSummary) {
        ProductSummary = productSummary;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getProductDesciption() {
        return productDescription;
    }

    public void setProductDesciption(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getOrderCode() {
        return OrderCode;
    }

    public void setOrderCode(String orderCode) {
        OrderCode = orderCode;
    }

    public int getTeacherCode() {
        return TeacherCode;
    }

    public void setTeacherCode(int teacherCode) {
        TeacherCode = teacherCode;
    }

    public String getProductCode() {
        return ProductCode;
    }

    public void setProductCode(String productCode) {
        ProductCode = productCode;
    }

    public int getProductCost() {
        return ProductCost;
    }

    public void setProductCost(int productCost) {
        ProductCost = productCost;
    }

    public String getOrderStatus() {
        return OrderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        OrderStatus = orderStatus;
    }

    public String getStudentPhone() {
        return StudentPhone;
    }

    public void setStudentPhone(String studentPhone) {
        StudentPhone = studentPhone;
    }

    public ParseGeoPoint getCurrentLocation() {
        return CurrentLocation;
    }

    public void setCurrentLocation(ParseGeoPoint currentLocation) {
        CurrentLocation = currentLocation;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
