package com.ertw.biddingcalculator.constants;

/**
 * Author: wenweibo
 * Time: 2025/2/11 16:13
 * Description:This is GlobalConstants
 */
public class GlobalConstants {

    /**
     * Shared preferences name
     */
    public static final String SP_NAME = "settings";

    public static final String SP_KEY_PERCENTAGE_HOME = "percentage_home";


    public static final String SP_KEY_PERCENTAGE_OTHER = "percentage_other";

    public static final String SP_KEY_PERCENTAGE_PRODUCT = "percentage_product";

    /**
     * page auto move
     */
    public static final String SP_KEY_AUTO_MOVE = "auto_move";
    /**
     * csv file save path
     */
    public static final String SP_KEY_SAVE_PATH = "save_path";
    public static final String SP_KEY_SAVE_URI= "save_uri";
    /**google drive folder path*/
    public static final String SP_KEY_DRIVE_FOLDER = "drive_folder";
    /**google drive parent folder id*/
    public static final String SP_KEY_DRIVE_FOLDER_ID = "drive_folder_id";
    /**google drive system history folder id*/
    public static final String SP_KEY_DRIVE_SYS_FOLDER_ID = "drive_sys_folder_id";
    /**google drive medication history folder id*/
    public static final String SP_KEY_DRIVE_MEC_FOLDER_ID = "drive_mec_folder_id";

    /**Skip the medicine bag page?*/
    public static final String SP_KEY_PACKAGE_SKIP = "package_skip";
//    /**Region*/
//    public static final String SP_KEY_LOCAL = "local";

    /**
     * Scan bar code type
     * <p>
     * 1:BarcodeFormat.QR_CODE
     * </p>
     * <p>
     * 2:BarcodeFormat.CODE_128
     * </p>
     * <p>
     * 3:BarcodeFormat.CODE_39
     * </p>
     * <p>
     * 4:BarcodeFormat.PDF_417
     * </p>
     */
    public static final String SCAN_CODE_TYPE = "scan_code_type";
    public static final String SCAN_RESULT = "scan_result";

    public static final String PAGE_TITLE = "page_title";
    public static final String FROM_PAGE_TITLE = "from_page_title";
    public static final String REQUEST_CODE = "request_code";

    /**
     * sqlite database file name
     */
    public static final String DB_NAME = "calculator.db";


    /**
     * Log file name;
     */
    public static final String EXPORT_FILE_NAME = "BiddingCalculator_";


    public static final String EXPORT_FOLDER = "Calculator";
    public static final String HISTORY_FOLDER = "ERTW/history";



}
