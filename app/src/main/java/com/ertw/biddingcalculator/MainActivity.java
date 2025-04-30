package com.ertw.biddingcalculator;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.ertw.biddingcalculator.constants.GlobalConstants;
import com.ertw.biddingcalculator.databinding.ActivityMainBinding;
import com.ertw.biddingcalculator.entity.CalculatorEntity;
import com.ertw.biddingcalculator.entity.MatchEntity;
import com.ertw.biddingcalculator.utils.DBUtils;
import com.ertw.biddingcalculator.utils.DateUtils;
import com.ertw.biddingcalculator.utils.FileUtils;
import com.ertw.biddingcalculator.weight.LoadingDialog;
import com.xuexiang.xui.utils.XToastUtils;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xui.widget.spinner.materialspinner.MaterialSpinner;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends BaseActivity<ActivityMainBinding> implements EasyPermissions.PermissionCallbacks{
    private static final int REQUEST_CODE_QRCODE_PERMISSIONS = 1;
    private List<String> dates;
    private List<MatchEntity> matchEntities = new ArrayList<>();

    private String[] typeValueArr;
    private String[] typeNameArr;
    private String[] offsetArr;

    private CalculatorEntity calculatorEntity;

    DecimalFormat decimalFormat = new DecimalFormat("#.###");

    private LoadingDialog loadingDialog;

    private SharedPreferences prefs;

    @Override
    protected ActivityMainBinding getViewBinding() {
        return ActivityMainBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void onStart() {
        super.onStart();
        requestCodeQRCodePermissions();
    }

    @Override
    protected void initData() {
        super.initData();
        typeValueArr = getResources().getStringArray(R.array.type_value);
        typeNameArr = getResources().getStringArray(R.array.type_name);

        prefs = getSharedPreferences(GlobalConstants.SP_NAME, Context.MODE_PRIVATE);


        DBUtils.queryAllDate(new DBUtils.DBOptionCallback() {
            @Override
            public void success(Object o) {
                dates = (List<String>) o;
                binding.msDate.setItems(dates);
            }

            @Override
            public void fail() {
            }
        });

        initMatch();

        queryDataByDate(DateUtils.getCurrentDateStr());

        MyApplication.logDir = FileUtils.createUniversalFolder(MainActivity.this, GlobalConstants.EXPORT_FOLDER);
    }

    private void queryDataByDate(String date) {
        DBUtils.queryCalculatorEntityByDate(new DBUtils.DBOptionCallback() {
            @Override
            public void success(Object o) {

                if (o != null) {
                    calculatorEntity = (CalculatorEntity) o;
                } else {
                    calculatorEntity = new CalculatorEntity();
                    calculatorEntity.setId(-1);
                    calculatorEntity.setType(typeNameArr[0]);
                    calculatorEntity.setCalculatorDate(DateUtils.getCurrentDateStr());
                }

                handler.sendEmptyMessage(0);
            }

            @Override
            public void fail() {

            }
        }, date);
    }

    private Handler exportHandler = new Handler(msg -> {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(MainActivity.this)
                .title("通知")
                .content((String ) msg.obj)
                .positiveText("OK");
        builder.show();
        return false;
    });

    private Handler handler = new Handler(msg -> {
        boolean isToday = calculatorEntity.getCalculatorDate().equals(DateUtils.getCurrentDateStr());

        binding.msType.setSelectedItem(calculatorEntity.getType());
        binding.msDate.setSelectedItem(calculatorEntity.getCalculatorDate());

        String percentageHome = prefs.getString(GlobalConstants.SP_KEY_PERCENTAGE_HOME, "");
        String etHomeStr = changeStr(calculatorEntity.getPercentageHome());
        etHomeStr = etHomeStr.isEmpty() ? percentageHome : etHomeStr;
        binding.etHome.setText(etHomeStr);
        String percentageOther = prefs.getString(GlobalConstants.SP_KEY_PERCENTAGE_OTHER, "");
        String etOtherStr = changeStr(calculatorEntity.getPercentageOther());
        etOtherStr = etOtherStr.isEmpty() ? percentageOther : etOtherStr;
        binding.etOther.setText(etOtherStr);
        String percentageProduct = prefs.getString(GlobalConstants.SP_KEY_PERCENTAGE_PRODUCT, "");
        String etProductStr = changeStr(calculatorEntity.getPercentageProduct());
        etProductStr = etProductStr.isEmpty() ? percentageProduct : etProductStr;
        binding.etProduct.setText(etProductStr);

        resetAmount(0, calculatorEntity.getMatchExact(), calculatorEntity.getMatchExact1(),
                calculatorEntity.getMatchExact2(), calculatorEntity.getMatchExact3());

        resetAmount(1, calculatorEntity.getMatchBroad(), calculatorEntity.getMatchBroad1(),
                calculatorEntity.getMatchBroad2(), calculatorEntity.getMatchBroad3());

        resetAmount(2, calculatorEntity.getProductsSimilar(), calculatorEntity.getProductsSimilar1(),
                calculatorEntity.getProductsSimilar2(), calculatorEntity.getProductsSimilar3());

        resetAmount(3, calculatorEntity.getProductsRelated(), calculatorEntity.getProductsRelated1(),
                calculatorEntity.getProductsRelated2(), calculatorEntity.getProductsRelated3());

        binding.etBudget.setText(changeStr(calculatorEntity.getBudget()));
        binding.etNumberOfOrders.setText(changeStr(calculatorEntity.getNumberOfOrders()));
        binding.etImpressions.setText(changeStr(calculatorEntity.getImpressions()));
        binding.etExpenditure.setText(changeStr(calculatorEntity.getExpenditure()));
        binding.etNumberOfClicks.setText(calculatorEntity.getNumberOfClicks() + "");
        binding.etSalesRevenue.setText(changeStr(calculatorEntity.getSalesRevenue()));

        if (isToday) {
            setEditEnable(true);
        } else {
            setEditEnable(false);
        }
        return false;
    });

    private void setEditEnable(boolean enable) {
        binding.msType.setEnabled(enable);
        binding.etHome.setEnabled(enable);
        binding.etOther.setEnabled(enable);
        binding.etProduct.setEnabled(enable);
        for (int i = 0; i < binding.linMatch.getChildCount(); i++) {
            LinearLayout linChild = (LinearLayout) binding.linMatch.getChildAt(i);
            EditText et = (EditText) linChild.getChildAt(1);
            et.setEnabled(enable);
        }
        binding.etBudget.setEnabled(enable);

        binding.etNumberOfOrders.setEnabled(!enable);
        binding.etImpressions.setEnabled(!enable);
        binding.etExpenditure.setEnabled(!enable);
        binding.etNumberOfClicks.setEnabled(!enable);
        binding.etSalesRevenue.setEnabled(!enable);
    }

    private String changeStr(String str) {
        return str == null ? "" : str;
    }

    private void resetAmount(int index, String amount, String amount2, String amount3, String amount4) {
        LinearLayout exactLin = (LinearLayout) binding.linMatch.getChildAt(index);
        EditText amountEt = exactLin.findViewById(R.id.et_amount);
        amountEt.setText(changeStr(amount));
        LinearLayout exactChildLin = (LinearLayout) exactLin.getChildAt(2);
        TextView exactHomeAmountTv = (TextView) exactChildLin.getChildAt(0);
        exactHomeAmountTv.setText(changeStr(amount2));
        TextView exactOtherAmountTv = (TextView) exactChildLin.getChildAt(1);
        exactOtherAmountTv.setText(changeStr(amount3));
        TextView exactProductAmountTv = (TextView) exactChildLin.getChildAt(2);
        exactProductAmountTv.setText(changeStr(amount4));
    }

    private void initMatch() {
        matchEntities.clear();
        MatchEntity matchEntity = new MatchEntity();
        matchEntity.setTitle(getString(R.string.match_exact));
        matchEntities.add(matchEntity);

        matchEntity = new MatchEntity();
        matchEntity.setTitle(getString(R.string.match_broad));
        matchEntities.add(matchEntity);

        matchEntity = new MatchEntity();
        matchEntity.setTitle(getString(R.string.products_similar));
        matchEntities.add(matchEntity);

        matchEntity = new MatchEntity();
        matchEntity.setTitle(getString(R.string.products_related));
        matchEntities.add(matchEntity);

        binding.linMatch.removeAllViews();
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        TextChange textChange = new TextChange();
        for (MatchEntity match : matchEntities) {
            View view = layoutInflater.inflate(R.layout.item_match, null);
            TextView title = view.findViewById(R.id.tv_title);
            title.setText(match.getTitle());

            EditText amount = view.findViewById(R.id.et_amount);
            amount.setText(match.getAmount() + "");
            amount.addTextChangedListener(textChange);

            LinearLayout linCalcAmount = view.findViewById(R.id.lin_calc_amount);
            TextView calcAmount1 = (TextView) linCalcAmount.getChildAt(0);
            calcAmount1.setText(match.getAmount1() + "");
            TextView calcAmount2 = (TextView) linCalcAmount.getChildAt(1);
            calcAmount2.setText(match.getAmount2() + "");
            TextView calcAmount3 = (TextView) linCalcAmount.getChildAt(2);
            calcAmount3.setText(match.getAmount3() + "");
            binding.linMatch.addView(view);
        }

    }

    @Override
    protected void initListener() {
        super.initListener();
        binding.msType.setOnItemSelectedListener((view, position, id, item) -> {
            String typeValue = typeValueArr[position];
            offsetArr = typeValue.split(",");

            showAmount();

        });
        offsetArr = typeValueArr[0].split(",");

        TextChange textChange = new TextChange();
        binding.etHome.addTextChangedListener(textChange);
        binding.etOther.addTextChangedListener(textChange);
        binding.etProduct.addTextChangedListener(textChange);

        binding.msDate.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                String dateStr = (String) item;
                queryDataByDate(dateStr);
            }
        });

        binding.btnExport.setOnClickListener(v -> {
            if (loadingDialog == null) {
                loadingDialog = new LoadingDialog(MainActivity.this);
            }
            loadingDialog.show();
            FileUtils.exportHistory(new DBUtils.DBOptionCallback() {
                @Override
                public void success(Object o) {
                    loadingDialog.dismiss();
                    Message message = Message.obtain();
                    message.obj = o;
                    exportHandler.sendMessage(message);
                }

                @Override
                public void fail() {
                    loadingDialog.dismiss();
                }
            });
        });

        binding.btnSave.setOnClickListener(v -> {
            if (loadingDialog == null) {
                loadingDialog = new LoadingDialog(MainActivity.this);
            }
            loadingDialog.show();
            if (calculatorEntity == null) {
                calculatorEntity = new CalculatorEntity();
                calculatorEntity.setId(-1);
            }



            calculatorEntity.setType(binding.msType.getSelectedItem());
            calculatorEntity.setCalculatorDate(binding.msDate.getSelectedItem());
            String percentageHome = binding.etHome.getText().toString();
            if (!percentageHome.isEmpty()) {
                calculatorEntity.setPercentageHome(percentageHome);
                if(calculatorEntity.getId() == -1){
                    prefs.edit().putString(GlobalConstants.SP_KEY_PERCENTAGE_HOME, percentageHome).apply();
                }
            }

            String percentageOther = binding.etOther.getText().toString();
            if (!percentageOther.isEmpty()) {
                calculatorEntity.setPercentageOther(percentageOther);
                if(calculatorEntity.getId() == -1){
                    prefs.edit().putString(GlobalConstants.SP_KEY_PERCENTAGE_OTHER, percentageOther).apply();
                }
            }

            String percentageProduct = binding.etProduct.getText().toString();
            if (!percentageProduct.isEmpty()) {
                calculatorEntity.setPercentageProduct(percentageProduct);
                if(calculatorEntity.getId() == -1){
                    prefs.edit().putString(GlobalConstants.SP_KEY_PERCENTAGE_PRODUCT, percentageProduct).apply();
                }
            }



            LinearLayout exactLin = (LinearLayout) binding.linMatch.getChildAt(0);
            EditText exactEt = (EditText) exactLin.getChildAt(1);
            String exactEtStr = exactEt.getText().toString();
            if (!exactEtStr.isEmpty()) {
                calculatorEntity.setMatchExact(exactEtStr);
            }
            LinearLayout exactChildLin = (LinearLayout) exactLin.getChildAt(2);
            TextView exactAmount1Tv = (TextView) exactChildLin.getChildAt(0);
            calculatorEntity.setMatchExact1(exactAmount1Tv.getText().toString());
            TextView exactAmount2Tv = (TextView) exactChildLin.getChildAt(1);
            calculatorEntity.setMatchExact2(exactAmount2Tv.getText().toString());
            TextView exactAmount3Tv = (TextView) exactChildLin.getChildAt(2);
            calculatorEntity.setMatchExact3(exactAmount3Tv.getText().toString());


            LinearLayout broadLin = (LinearLayout) binding.linMatch.getChildAt(1);
            EditText broadEt = (EditText) broadLin.getChildAt(1);
            String broadEtStr = broadEt.getText().toString();
            if (!broadEtStr.isEmpty()) {
                calculatorEntity.setMatchBroad(broadEtStr);
            }
            LinearLayout broadChildLin = (LinearLayout) broadLin.getChildAt(2);
            TextView broadAmount1Tv = (TextView) broadChildLin.getChildAt(0);
            calculatorEntity.setMatchBroad1(broadAmount1Tv.getText().toString());
            TextView broadAmount2Tv = (TextView) broadChildLin.getChildAt(1);
            calculatorEntity.setMatchBroad2(broadAmount2Tv.getText().toString());
            TextView broadAmount3Tv = (TextView) broadChildLin.getChildAt(2);
            calculatorEntity.setMatchBroad3(broadAmount3Tv.getText().toString());

            LinearLayout similarLin = (LinearLayout) binding.linMatch.getChildAt(2);
            EditText similarEt = (EditText) similarLin.getChildAt(1);
            String similarEtStr = similarEt.getText().toString();
            if (!similarEtStr.isEmpty()) {
                calculatorEntity.setProductsSimilar(similarEtStr);
            }
            LinearLayout similarChildLin = (LinearLayout) similarLin.getChildAt(2);
            TextView similarAmount1Tv = (TextView) similarChildLin.getChildAt(0);
            calculatorEntity.setProductsSimilar1(similarAmount1Tv.getText().toString());
            TextView similarAmount2Tv = (TextView) similarChildLin.getChildAt(1);
            calculatorEntity.setProductsSimilar2(similarAmount2Tv.getText().toString());
            TextView similarAmount3Tv = (TextView) similarChildLin.getChildAt(2);
            calculatorEntity.setProductsSimilar3(similarAmount3Tv.getText().toString());

            LinearLayout relatedLin = (LinearLayout) binding.linMatch.getChildAt(3);
            EditText relatedEt = (EditText) relatedLin.getChildAt(1);
            String relatedEtStr = relatedEt.getText().toString();
            if (!relatedEtStr.isEmpty()) {
                calculatorEntity.setProductsRelated(relatedEtStr);
            }
            LinearLayout relatedChildLin = (LinearLayout) relatedLin.getChildAt(2);
            TextView relatedAmount1Tv = (TextView) relatedChildLin.getChildAt(0);
            calculatorEntity.setProductsRelated1(relatedAmount1Tv.getText().toString());
            TextView relatedAmount2Tv = (TextView) relatedChildLin.getChildAt(1);
            calculatorEntity.setProductsRelated2(relatedAmount2Tv.getText().toString());
            TextView relatedAmount3Tv = (TextView) relatedChildLin.getChildAt(2);
            calculatorEntity.setProductsRelated3(relatedAmount3Tv.getText().toString());

            String budgetStr = binding.etBudget.getText().toString();
            if (!budgetStr.isEmpty()) {
                calculatorEntity.setBudget(budgetStr);
            }
            String numberOfOrdersStr = binding.etNumberOfOrders.getText().toString();
            if (!numberOfOrdersStr.isEmpty()) {
                calculatorEntity.setNumberOfOrders(numberOfOrdersStr);
            }
            String impressionsStr = binding.etImpressions.getText().toString();
            if (!impressionsStr.isEmpty()) {
                calculatorEntity.setImpressions(impressionsStr);
            }
            String expenditureStr = binding.etExpenditure.getText().toString();
            if (!expenditureStr.isEmpty()) {
                calculatorEntity.setExpenditure(expenditureStr);
            }
            String numberOfClicksStr = binding.etNumberOfClicks.getText().toString();
            if (!numberOfClicksStr.isEmpty()) {
                calculatorEntity.setNumberOfClicks(Integer.parseInt(numberOfClicksStr));
            }
            String salesRevenueStr = binding.etSalesRevenue.getText().toString();
            if (!salesRevenueStr.isEmpty()) {
                calculatorEntity.setSalesRevenue(salesRevenueStr);
            }

            DBUtils.insertOrUpdateCalculator(calculatorEntity, new DBUtils.DBOptionCallback() {
                @Override
                public void success(Object o) {
                    loadingDialog.dismiss();
                    long insertId = (long) o;
                    if (insertId != -1) {
                        calculatorEntity.setId((int) insertId);
                    }
                    XToastUtils.success("保存成功");
                    Log.d(TAG, "insertId---------------" + insertId);
                }

                @Override
                public void fail() {
                    loadingDialog.dismiss();
                    XToastUtils.error("保存失败");
                }
            });
        });

    }

    @AfterPermissionGranted(REQUEST_CODE_QRCODE_PERMISSIONS)
    private void requestCodeQRCodePermissions() {
        String[] perms = {
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE
        };
        if (!EasyPermissions.hasPermissions(this, perms)) {
            EasyPermissions.requestPermissions(this,
                    "请给我权限", REQUEST_CODE_QRCODE_PERMISSIONS, perms);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        MyApplication.logDir = FileUtils.createUniversalFolder(MainActivity.this, GlobalConstants.EXPORT_FOLDER);
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }

    class TextChange implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            showAmount();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    private void showAmount() {
        String homePercentageStr = binding.etHome.getText().toString();
        if (!homePercentageStr.isEmpty()) {
            setAmount(Float.parseFloat(homePercentageStr), 0);
        }
        String otherPercentageStr = binding.etOther.getText().toString();
        if (!otherPercentageStr.isEmpty()) {
            setAmount(Float.parseFloat(otherPercentageStr), 1);
        }

        String productPercentageStr = binding.etProduct.getText().toString();
        if (!productPercentageStr.isEmpty()) {
            setAmount(Float.parseFloat(productPercentageStr), 2);
        }

    }

    private void setAmount(float percentage, int index) {
        for (int i = 0; i < binding.linMatch.getChildCount(); i++) {
            LinearLayout parentLin = (LinearLayout) binding.linMatch.getChildAt(i);
            EditText amountEdit = parentLin.findViewById(R.id.et_amount);
            String amountStr = amountEdit.getText().toString();
            amountStr = amountStr.isEmpty() ? "0" : amountStr;
            float amount = Float.parseFloat(amountStr);
            LinearLayout childLin = (LinearLayout) parentLin.getChildAt(2);

            TextView tv = (TextView) childLin.getChildAt(index);
            tv.setText(decimalFormat.format(calcAmount(amount, index, percentage)));

        }

    }

    private float calcAmount(float amount, int x, float percentage) {
        return amount * (1 + Float.parseFloat(offsetArr[x])) * (1 + percentage / 100);
    }
}