package com.ertw.biddingcalculator.utils;

import com.ertw.biddingcalculator.MyApplication;
import com.ertw.biddingcalculator.entity.CalculatorEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: wenweibo
 * Time: 2025/4/27 13:55
 * Description:This is DBUtils
 */
public class DBUtils {
    public interface DBOptionCallback<T> {
        void success(T t);

        void fail();
    }

    public static void insertOrUpdateCalculator(CalculatorEntity calculatorEntity, DBOptionCallback dbOptionCallback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (calculatorEntity != null) {
                    long insertId = -1;
                    if(calculatorEntity.getId() == -1){
                        calculatorEntity.setId(0);
                        insertId = MyApplication.appDatabase.calculatorEntityDao().insert(calculatorEntity);
                    }else{
                        MyApplication.appDatabase.calculatorEntityDao().update(calculatorEntity);
                    }

                    if (dbOptionCallback != null) {
                        dbOptionCallback.success(insertId);
                    }
                } else {
                    if (dbOptionCallback != null) {
                        dbOptionCallback.fail();
                    }
                }
            }
        }).start();
    }

    /**
     * query all history
     *
     * @param dbOptionCallback
     */
//    public static void queryAllHistory(DBOptionCallback dbOptionCallback) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                List<HistoryEntity> historyEntities = MyApplication.appDatabase.historyEntityDao().queryAll();
//                if (dbOptionCallback != null) {
//                    dbOptionCallback.success(historyEntities);
//                }
//            }
//        }).start();
//    }

    public static void queryAllDate(DBOptionCallback dbOptionCallback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<String> dates = MyApplication.appDatabase.calculatorEntityDao().queryAllDate();
                if(dates == null){
                    dates = new ArrayList<>();
                }
                String today = DateUtils.getCurrentDateStr();
                if(!dates.contains(today)){
                    dates.add(0,today);
                }

                if (dbOptionCallback != null) {
                    dbOptionCallback.success(dates);
                }
            }
        }).start();
    }


    public static void queryCalculatorEntityByDate(DBOptionCallback dbOptionCallback, String calculatorDate) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                CalculatorEntity calculatorEntity = MyApplication.appDatabase.calculatorEntityDao().queryByCalculatorDate(calculatorDate);

                if (dbOptionCallback != null) {
                    dbOptionCallback.success(calculatorEntity);
                }
            }
        }).start();
    }



}
