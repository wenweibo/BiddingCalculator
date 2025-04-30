package com.ertw.biddingcalculator;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBinding;

/**
 * Author: wenweibo
 * Time: 2025/2/12 10:53
 * Description:This is BaseActivity
 */
public abstract class BaseActivity<VB extends ViewBinding> extends AppCompatActivity {
    public String TAG = this.getClass().getSimpleName();
    protected VB binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = getViewBinding();
        setContentView(binding.getRoot());
        initUI();
        initData();
        initListener();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    // 子类必须实现：返回对应的 ViewBinding 实例
    protected abstract VB getViewBinding();

    protected void initUI() {

    }

    protected void initData() {

    }

    protected void setTitle(String title){
    }

    protected void initListener() {

    }


}
