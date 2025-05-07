package com.ertw.biddingcalculator.utils;

import android.content.ContentValues;
import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.documentfile.provider.DocumentFile;

import com.ertw.biddingcalculator.MyApplication;
import com.ertw.biddingcalculator.constants.GlobalConstants;
import com.ertw.biddingcalculator.entity.CalculatorEntity;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

/**
 * Author: wenweibo
 * Time: 2025/2/18 9:22
 * Description:This is FileUtils
 */
public class FileUtils {

    public static String getReadablePathFromUri(Uri uri) {
        if ("com.android.externalstorage.documents".equals(uri.getAuthority())) {
            String docId = DocumentsContract.getTreeDocumentId(uri);
            String[] parts = docId.split(":");
            String type = parts[0];
            String relativePath = parts.length > 1 ? parts[1] : "";

            if ("primary".equalsIgnoreCase(type)) {
                return Environment.getExternalStorageDirectory() + "/" + relativePath;
            }
        }
        return uri.toString();
    }

    /**
     * Export history
     *
     * @param dbOptionCallback
     */
    public static void exportHistory(Context context, DBUtils.DBOptionCallback dbOptionCallback) {
        new Thread(() -> {
            List<CalculatorEntity> calculatorEntities = MyApplication.appDatabase.calculatorEntityDao().queryAll();
            if (calculatorEntities == null || calculatorEntities.isEmpty()) {
                dbOptionCallback.success("抱歉，无可导出的数据");
            } else {
                String result = exportCalculatorEntityList2(context, calculatorEntities);
                dbOptionCallback.success(result);
            }


        }).start();
    }

    /**
     * Export `List<HistoryEntity>` to a CSV file.
     *
     * @param context
     * @param uri
     * @param historyList
     * @throws IOException
     */
    public static void exportHistoryToCsv(Context context, Uri uri, List<CalculatorEntity> historyList, String facilityCode) throws IOException {
        // Generate the file name in the format: 施設コード_SagyouHoukoku_yyyymmdd.csv
        String fileName = generateFileName(context, facilityCode, DateUtils.getCurrentDateTimeStr());

        // Construct the Uri with the file name to create a file at the specified location
        DocumentFile documentFile = DocumentFile.fromTreeUri(context, uri)
                .createFile("text/csv", fileName);
        try (OutputStream outputStream = context.getContentResolver().openOutputStream(documentFile.getUri())) {
            if (outputStream == null) {
                throw new IOException("Unable to open OutputStream.");
            }

            // Writer to write data to the file
            Writer writer = new BufferedWriter(new OutputStreamWriter(outputStream));

            // Write the header to the CSV
            writer.write("日付,用法,用法CD,施設,職員コード,利用者,利用者CD,与薬日時,結果,備考\n");

            // Write the data of each `HistoryEntity`
//            for (HistoryEntity history : historyList) {
//                writer.write(String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s\n",
//                        history.getDate(),
//                        history.getUsage(),
//                        history.getUsageCode(),
//                        history.getFacility(),
//                        history.getEmployeeCode(),
//                        history.getPatient() == null ? "" : history.getPatient(),
//                        history.getPatientCode() == null ? "" : history.getPatientCode(),
//                        history.getTime(),
//                        history.getResult(),
//                        history.getRemark()));
//            }

            writer.flush();
        } catch (IOException e) {
            throw e;
        }
    }


    public static String exchangeNull(String str) {
        if (str == null) {
            str = "";
        }
        return str;
    }

    /**
     * Helper method to generate the file name
     * 施設コード_Mac address_SagyouHoukoku_yyyymmdd
     *
     * @param facilityCode
     * @return
     */
    private static String generateFileName(Context context, String facilityCode, String dateString) {
        dateString = dateString.replaceAll("/", "");
        dateString = dateString.replaceAll(" ", "_");
        return facilityCode + "_" + dateString + ".csv";
    }

    /**
     * Create universal folder
     *
     * @param context
     * @param folderName
     * @return
     */
    public static File createUniversalFolder(Context context, String folderName) {
        File baseDir = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { // Android 10+
            // use MediaStore API
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.Downloads.DISPLAY_NAME, folderName);
            contentValues.put(MediaStore.Downloads.RELATIVE_PATH,
                    Environment.DIRECTORY_DOWNLOADS + "/" + folderName);

            try {
                Uri uri = context.getContentResolver().insert(
                        MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                        contentValues
                );

                if (uri != null) {
                    baseDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                            folderName);
                }
            } catch (Exception e) {
                e.printStackTrace();
                baseDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                        folderName);
            }
        } else { // Android 5.0-9

            baseDir = new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    folderName
            );
        }
        if (baseDir != null && !baseDir.exists()) {
            if (baseDir.mkdirs()) {
                // refresh media
                MediaScannerConnection.scanFile(
                        context,
                        new String[]{baseDir.getAbsolutePath()},
                        null,
                        null
                );
            }
        }

        return baseDir;
    }

    /**
     * save crash log file
     *
     * @param ex
     */
    public static void saveCrashLog(Throwable ex) {
//        try {
//            File logFile = new File(MyApplication.logDir, GlobalConstants.LOG_FILE_NAME+DateUtils.getCurrentDateByPattern("yyyyMMdd")+".log");
//            Log.e("createLogFile", "createLogFile-----" + logFile.getAbsolutePath());
//            FileWriter writer = new FileWriter(logFile, true);
//            writer.write("Crash Date time：" + DateUtils.getCurrentDateTimeStr() + "\n");
//            writer.write(Log.getStackTraceString(ex) + "\n\n");
//            writer.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }


    public static String exportCalculatorEntityList(Context context, List<CalculatorEntity> dataList) {
        String result = "导出失败";
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Calculator Data");

        // 创建表头
        Row headerRow = sheet.createRow(0);
        String[] headers = {
                "ID", "首页首位", "其余位置", "商品详情页",
                "紧密匹配", "紧密匹配1", "紧密匹配2", "紧密匹配3",
                "宽泛匹配", "宽泛匹配1", "宽泛匹配2", "宽泛匹配3",
                "同类商品", "同类商品1", "同类商品2", "同类商品3",
                "关联商品", "关联商品1", "关联商品2", "关联商品3",
                "预算", "订单数", "曝光数", "支出", "点击数", "销售额",
                "日期", "类型"
        };

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        // 写入数据
        for (int i = 0; i < dataList.size(); i++) {
            CalculatorEntity entity = dataList.get(i);
            Row row = sheet.createRow(i + 1);

            row.createCell(0).setCellValue(entity.getId());
            row.createCell(1).setCellValue(entity.getPercentageHome() + "%");
            row.createCell(2).setCellValue(entity.getPercentageOther() + "%");
            row.createCell(3).setCellValue(entity.getPercentageProduct() + "%");

            row.createCell(4).setCellValue(entity.getMatchExact());
            row.createCell(5).setCellValue(entity.getMatchExact1());
            row.createCell(6).setCellValue(entity.getMatchExact2());
            row.createCell(7).setCellValue(entity.getMatchExact3());

            row.createCell(8).setCellValue(entity.getMatchBroad());
            row.createCell(9).setCellValue(entity.getMatchBroad1());
            row.createCell(10).setCellValue(entity.getMatchBroad2());
            row.createCell(11).setCellValue(entity.getMatchBroad3());

            row.createCell(12).setCellValue(entity.getProductsSimilar());
            row.createCell(13).setCellValue(entity.getProductsSimilar1());
            row.createCell(14).setCellValue(entity.getProductsSimilar2());
            row.createCell(15).setCellValue(entity.getProductsSimilar3());

            row.createCell(16).setCellValue(entity.getProductsRelated());
            row.createCell(17).setCellValue(entity.getProductsRelated1());
            row.createCell(18).setCellValue(entity.getProductsRelated2());
            row.createCell(19).setCellValue(entity.getProductsRelated3());

            row.createCell(20).setCellValue(entity.getBudget());
            row.createCell(21).setCellValue(entity.getNumberOfOrders());
            row.createCell(22).setCellValue(entity.getImpressions());
            row.createCell(23).setCellValue(entity.getExpenditure());
            row.createCell(24).setCellValue(entity.getNumberOfClicks());
            row.createCell(25).setCellValue(entity.getSalesRevenue());
            row.createCell(26).setCellValue(entity.getCalculatorDate());
            row.createCell(27).setCellValue(entity.getType());
        }

        // 保存到本地
        try {
            String fileName = GlobalConstants.EXPORT_FILE_NAME + DateUtils.getCurrentDateByPattern("yyyyMMdd_HHmmss") + ".xlsx";
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
            contentValues.put(MediaStore.Downloads.MIME_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            contentValues.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/" + GlobalConstants.EXPORT_FOLDER);

            Uri uri = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                uri = context.getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues);
            }

            if (uri != null) {
                try (OutputStream os = context.getContentResolver().openOutputStream(uri)) {
                    workbook.write(os); // 假设你是用 Apache POI 或类似库创建的 Excel
                    workbook.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                File baseDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                        GlobalConstants.EXPORT_FOLDER);
                File file = new File(baseDir, fileName);

                Log.d("exportCalculatorEntityList", "--保存成功--");
                result = "导出成功,请到:<" + file.getAbsolutePath() + ">查看";
            } else {
                Log.e("Export", "创建文件失败: uri 为 null");
            }


        } catch (Exception e) {
            e.printStackTrace();
            Log.e("exportCalculatorEntityList", "--保存失败--");
        }

        return result;
    }

    public static String exportCalculatorEntityList2(Context context, List<CalculatorEntity> dataList) {
        String result = "导出失败";
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Calculator Data");

        // 设置合并单元格的第一行（表头）
        Row headerRow1 = sheet.createRow(0);
//        Row headerRow2 = sheet.createRow(1);

        // 表头第1行
        int col = 0;
        headerRow1.createCell(col++).setCellValue( "日期");
        headerRow1.createCell(col++).setCellValue( "类型");
//        sheet.addMergedRegion(new CellRangeAddress(0, 1, col, col++)); // ID

        headerRow1.createCell(col++).setCellValue("首页首位");
//        sheet.addMergedRegion(new CellRangeAddress(0, 1, col, col++)); // 首页首位

        headerRow1.createCell(col++).setCellValue("其余位置");
//        sheet.addMergedRegion(new CellRangeAddress(0, 1, col, col++)); // 其余位置

        headerRow1.createCell(col++).setCellValue("商品详情页");
//        sheet.addMergedRegion(new CellRangeAddress(0, 1, col, col++)); // 商品详情页

        // 匹配类合并列
        String[] matchTitles = {"紧密匹配", "宽泛匹配", "同类商品", "关联商品"};
        for (String title : matchTitles) {
            headerRow1.createCell(col++).setCellValue(title);
            sheet.addMergedRegion(new CellRangeAddress(0, 0,col - 1, col + 1));
            col += 2;
//            headerRow2.createCell(col++).setCellValue("1");
//            headerRow2.createCell(col++).setCellValue("2");
//            headerRow2.createCell(col++).setCellValue("3");
        }

        // 剩余字段
        String[] others = {"预算", "订单数", "曝光数", "支出", "点击数", "销售额"};
        for (String s : others) {
            headerRow1.createCell(col++).setCellValue(s);
//            sheet.addMergedRegion(new CellRangeAddress(0, 1, col, col++));
        }

        // 写入数据（从第2行开始）
        int rowIndex = 1;
        for (CalculatorEntity entity : dataList) {
            Row row1 = sheet.createRow(rowIndex++);
            Row row2 = sheet.createRow(rowIndex++);

            int c = 0;
            row1.createCell(c).setCellValue(entity.getCalculatorDate());
            sheet.addMergedRegion(new CellRangeAddress(rowIndex-2, rowIndex-1, c, c++));
            row1.createCell(c).setCellValue(entity.getType());
            sheet.addMergedRegion(new CellRangeAddress(rowIndex-2, rowIndex-1, c, c++));
            row1.createCell(c).setCellValue(entity.getPercentageHome() + "%");
            sheet.addMergedRegion(new CellRangeAddress(rowIndex-2, rowIndex-1, c, c++));
            row1.createCell(c).setCellValue(entity.getPercentageOther() + "%");
            sheet.addMergedRegion(new CellRangeAddress(rowIndex-2, rowIndex-1,c, c++));
            row1.createCell(c).setCellValue(entity.getPercentageProduct() + "%");
            sheet.addMergedRegion(new CellRangeAddress(rowIndex-2, rowIndex-1, c, c++));

            // 紧密匹配（主值写三列）
            row1.createCell(c).setCellValue(entity.getMatchExact());
            row1.createCell(c + 1).setCellValue(entity.getMatchExact());
            row1.createCell(c + 2).setCellValue(entity.getMatchExact());
            sheet.addMergedRegion(new CellRangeAddress(rowIndex-2, rowIndex-2, c, c+2));
            row2.createCell(c++).setCellValue(entity.getMatchExact1());
            row2.createCell(c++).setCellValue(entity.getMatchExact2());
            row2.createCell(c++).setCellValue(entity.getMatchExact3());

            // 宽泛匹配
            row1.createCell(c).setCellValue(entity.getMatchBroad());
            row1.createCell(c + 1).setCellValue(entity.getMatchBroad());
            row1.createCell(c + 2).setCellValue(entity.getMatchBroad());
            sheet.addMergedRegion(new CellRangeAddress(rowIndex-2, rowIndex-2, c, c+2));
            row2.createCell(c++).setCellValue(entity.getMatchBroad1());
            row2.createCell(c++).setCellValue(entity.getMatchBroad2());
            row2.createCell(c++).setCellValue(entity.getMatchBroad3());

            // 同类商品
            row1.createCell(c).setCellValue(entity.getProductsSimilar());
            row1.createCell(c + 1).setCellValue(entity.getProductsSimilar());
            row1.createCell(c + 2).setCellValue(entity.getProductsSimilar());
            sheet.addMergedRegion(new CellRangeAddress(rowIndex-2, rowIndex-2, c, c+2));
            row2.createCell(c++).setCellValue(entity.getProductsSimilar1());
            row2.createCell(c++).setCellValue(entity.getProductsSimilar2());
            row2.createCell(c++).setCellValue(entity.getProductsSimilar3());

            // 关联商品
            row1.createCell(c).setCellValue(entity.getProductsRelated());
            row1.createCell(c + 1).setCellValue(entity.getProductsRelated());
            row1.createCell(c + 2).setCellValue(entity.getProductsRelated());
            sheet.addMergedRegion(new CellRangeAddress(rowIndex-2, rowIndex-2, c, c+2));
            row2.createCell(c++).setCellValue(entity.getProductsRelated1());
            row2.createCell(c++).setCellValue(entity.getProductsRelated2());
            row2.createCell(c++).setCellValue(entity.getProductsRelated3());

            // 其他数据写在第一行
            row1.createCell(c).setCellValue(entity.getBudget());
            sheet.addMergedRegion(new CellRangeAddress(rowIndex-2, rowIndex-1, c, c++));
            row1.createCell(c).setCellValue(entity.getNumberOfOrders());
            sheet.addMergedRegion(new CellRangeAddress(rowIndex-2, rowIndex-1, c, c++));
            row1.createCell(c).setCellValue(entity.getImpressions());
            sheet.addMergedRegion(new CellRangeAddress(rowIndex-2, rowIndex-1, c, c++));
            row1.createCell(c).setCellValue(entity.getExpenditure());
            sheet.addMergedRegion(new CellRangeAddress(rowIndex-2, rowIndex-1, c, c++));
            row1.createCell(c).setCellValue(entity.getNumberOfClicks());
            sheet.addMergedRegion(new CellRangeAddress(rowIndex-2, rowIndex-1, c, c++));
            row1.createCell(c).setCellValue(entity.getSalesRevenue());
            sheet.addMergedRegion(new CellRangeAddress(rowIndex-2, rowIndex-1, c, c++));

        }

        // 保存到本地（Android 10+）
        try {
            String fileName = GlobalConstants.EXPORT_FILE_NAME + DateUtils.getCurrentDateByPattern("yyyyMMdd_HHmmss") + ".xlsx";
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
            contentValues.put(MediaStore.Downloads.MIME_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            contentValues.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/" + GlobalConstants.EXPORT_FOLDER);

            Uri uri = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                uri = context.getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues);
            }

            if (uri != null) {
                try (OutputStream os = context.getContentResolver().openOutputStream(uri)) {
                    workbook.write(os);
                    workbook.close();
                }
                File baseDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), GlobalConstants.EXPORT_FOLDER);
                File file = new File(baseDir, fileName);
                Log.d("exportCalculatorEntityList2", "--保存成功--");
                result = "导出成功,请到:<" + file.getAbsolutePath() + ">查看";
            } else {
                Log.e("Export", "创建文件失败: uri 为 null");
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("exportCalculatorEntityList2", "--保存失败--");
        }

        return result;
    }


}
