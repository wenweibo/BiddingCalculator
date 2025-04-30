package com.ertw.biddingcalculator.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.ertw.biddingcalculator.entity.CalculatorEntity;

import java.util.List;

@Dao
public interface CalculatorEntityDao {
    @Insert
    long insert(CalculatorEntity calculatorEntity);

    @Update
    void update(CalculatorEntity calculatorEntity);

    @Query("SELECT * FROM CalculatorEntity WHERE calculatorDate =:calculatorDate")
    CalculatorEntity queryByCalculatorDate(String calculatorDate);

    @Query("SELECT calculatorDate FROM CalculatorEntity GROUP BY calculatorDate")
    List<String> queryAllDate();


    @Query("SELECT * FROM CalculatorEntity ORDER BY calculatorDate DESC")
    List<CalculatorEntity> queryAll();
}

