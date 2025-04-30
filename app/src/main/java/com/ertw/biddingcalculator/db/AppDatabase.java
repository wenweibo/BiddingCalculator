package com.ertw.biddingcalculator.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.ertw.biddingcalculator.db.dao.CalculatorEntityDao;
import com.ertw.biddingcalculator.entity.CalculatorEntity;

/**
 * Author: wenweibo
 * Time: 2025/2/19 11:19
 * Description:This is AppDatabase
 */
@Database(entities = {CalculatorEntity.class},version = 1,exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract CalculatorEntityDao calculatorEntityDao();



}
