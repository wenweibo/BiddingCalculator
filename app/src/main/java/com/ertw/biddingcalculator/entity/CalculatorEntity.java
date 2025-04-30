package com.ertw.biddingcalculator.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Author: wenweibo
 * Time: 2025/4/27 9:25
 * Description:This is CalculatorEntity
 */
@Entity(tableName = "CalculatorEntity")
public class CalculatorEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;

    // 搜索结果页首页首位
    private String percentageHome;
    // 搜索结果的其余位置
    private String percentageOther;
    // 商品详情页
    private String percentageProduct;

    // 紧密匹配
    private String matchExact;
    // 紧密匹配1
    private String matchExact1;
    // 紧密匹配2
    private String matchExact2;
    // 紧密匹配3
    private String matchExact3;

    // 宽泛匹配
    private String matchBroad;
    // 宽泛匹配1
    private String matchBroad1;
    // 宽泛匹配2
    private String matchBroad2;
    // 宽泛匹配3
    private String matchBroad3;

    // 同类商品
    private String productsSimilar;
    // 同类商品1
    private String productsSimilar1;
    // 同类商品2
    private String productsSimilar2;
    // 同类商品3
    private String productsSimilar3;

    // 关联商品
    private String productsRelated;
    // 关联商品1
    private String productsRelated1;
    // 关联商品2
    private String productsRelated2;
    // 关联商品3
    private String productsRelated3;

    // 预算
    private String budget;
    // 订单数
    private String numberOfOrders;
    // 曝光数
    private String impressions;
    // 支出
    private String expenditure;
    // 点击数
    private int numberOfClicks;
    // 销售额
    private String salesRevenue;

    // 日期
    private String calculatorDate;

    // 类型：0--固定或仅降低策略，1--上升和降低策略
    private String type;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPercentageHome() {
        return percentageHome;
    }

    public void setPercentageHome(String percentageHome) {
        this.percentageHome = percentageHome;
    }

    public String getPercentageOther() {
        return percentageOther;
    }

    public void setPercentageOther(String percentageOther) {
        this.percentageOther = percentageOther;
    }

    public String getPercentageProduct() {
        return percentageProduct;
    }

    public void setPercentageProduct(String percentageProduct) {
        this.percentageProduct = percentageProduct;
    }

    public String getMatchExact() {
        return matchExact;
    }

    public void setMatchExact(String matchExact) {
        this.matchExact = matchExact;
    }

    public String getMatchExact1() {
        return matchExact1;
    }

    public void setMatchExact1(String matchExact1) {
        this.matchExact1 = matchExact1;
    }

    public String getMatchExact2() {
        return matchExact2;
    }

    public void setMatchExact2(String matchExact2) {
        this.matchExact2 = matchExact2;
    }

    public String getMatchExact3() {
        return matchExact3;
    }

    public void setMatchExact3(String matchExact3) {
        this.matchExact3 = matchExact3;
    }

    public String getMatchBroad() {
        return matchBroad;
    }

    public void setMatchBroad(String matchBroad) {
        this.matchBroad = matchBroad;
    }

    public String getMatchBroad1() {
        return matchBroad1;
    }

    public void setMatchBroad1(String matchBroad1) {
        this.matchBroad1 = matchBroad1;
    }

    public String getMatchBroad2() {
        return matchBroad2;
    }

    public void setMatchBroad2(String matchBroad2) {
        this.matchBroad2 = matchBroad2;
    }

    public String getMatchBroad3() {
        return matchBroad3;
    }

    public void setMatchBroad3(String matchBroad3) {
        this.matchBroad3 = matchBroad3;
    }

    public String getProductsSimilar() {
        return productsSimilar;
    }

    public void setProductsSimilar(String productsSimilar) {
        this.productsSimilar = productsSimilar;
    }

    public String getProductsSimilar1() {
        return productsSimilar1;
    }

    public void setProductsSimilar1(String productsSimilar1) {
        this.productsSimilar1 = productsSimilar1;
    }

    public String getProductsSimilar2() {
        return productsSimilar2;
    }

    public void setProductsSimilar2(String productsSimilar2) {
        this.productsSimilar2 = productsSimilar2;
    }

    public String getProductsSimilar3() {
        return productsSimilar3;
    }

    public void setProductsSimilar3(String productsSimilar3) {
        this.productsSimilar3 = productsSimilar3;
    }

    public String getProductsRelated() {
        return productsRelated;
    }

    public void setProductsRelated(String productsRelated) {
        this.productsRelated = productsRelated;
    }

    public String getProductsRelated1() {
        return productsRelated1;
    }

    public void setProductsRelated1(String productsRelated1) {
        this.productsRelated1 = productsRelated1;
    }

    public String getProductsRelated2() {
        return productsRelated2;
    }

    public void setProductsRelated2(String productsRelated2) {
        this.productsRelated2 = productsRelated2;
    }

    public String getProductsRelated3() {
        return productsRelated3;
    }

    public void setProductsRelated3(String productsRelated3) {
        this.productsRelated3 = productsRelated3;
    }

    public String getBudget() {
        return budget;
    }

    public void setBudget(String budget) {
        this.budget = budget;
    }

    public String getNumberOfOrders() {
        return numberOfOrders;
    }

    public void setNumberOfOrders(String numberOfOrders) {
        this.numberOfOrders = numberOfOrders;
    }

    public String getImpressions() {
        return impressions;
    }

    public void setImpressions(String impressions) {
        this.impressions = impressions;
    }

    public String getExpenditure() {
        return expenditure;
    }

    public void setExpenditure(String expenditure) {
        this.expenditure = expenditure;
    }

    public int getNumberOfClicks() {
        return numberOfClicks;
    }

    public void setNumberOfClicks(int numberOfClicks) {
        this.numberOfClicks = numberOfClicks;
    }

    public String getSalesRevenue() {
        return salesRevenue;
    }

    public void setSalesRevenue(String salesRevenue) {
        this.salesRevenue = salesRevenue;
    }

    public String getCalculatorDate() {
        return calculatorDate;
    }

    public void setCalculatorDate(String calculatorDate) {
        this.calculatorDate = calculatorDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

