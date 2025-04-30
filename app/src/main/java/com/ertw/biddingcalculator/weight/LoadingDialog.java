package com.ertw.biddingcalculator.weight;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;

import com.ertw.biddingcalculator.R;


/**
 * Author: wenweibo
 * Time: 2024/04/27 14:19
 * Description:This is LoadingDialog
 */
public class LoadingDialog extends Dialog {
    private Context mContext;
    public LoadingDialog(@NonNull Context context) {
        super(context, R.style.FullScreenDialog);
        this.mContext = context;
    }

    public LoadingDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullScreen();
        setCancelable(false);
        setContentView(R.layout.loading_dialog_layout);
    }
    private void fullScreen() {
        Window window = getWindow();
        if(window!=null){
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    @Override
    @UiThread
    public void show() {
        super.show();
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

}
