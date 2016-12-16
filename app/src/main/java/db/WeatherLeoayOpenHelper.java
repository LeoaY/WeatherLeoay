package db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.*;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by leoay on 2016/12/13.
 */

public class WeatherLeoayOpenHelper extends SQLiteOpenHelper {

    /**
     * Province 表建表语句
     */
    public static final String CREATE_PROVINCE = "create table Province("
                    + "id integer primary key autoincrement, "
                    + "province_name text, "
                    + "province_code text)";

    /**
     * City 表建表语句
     */
    public static final String CREATE_CITY = "create table City("
            + "id integer primary key autoincrement, "
            + "city_name text, "
            + "city_code text, "
            + "province_id integer)";

    /**
     *
     */
    public static final String CREATE_COUNTY = "create table County("
            + "id integer primary key autoincrement, "
            + "county_name text, "
            + "county_code text, "
            + "city_id integer)";

    public WeatherLeoayOpenHelper(Context context, String name, CursorFactory cursorFactory, int version){
        super(context, name, cursorFactory, version);
    }

    /**
     * 创建表
     * @param sqLiteDatabase
     */

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase){
        sqLiteDatabase.execSQL(CREATE_PROVINCE);   //创建表
        sqLiteDatabase.execSQL(CREATE_CITY);   //创建表
        sqLiteDatabase.execSQL(CREATE_COUNTY);   //创建表
    }

    /**
     * 更新表
     * @param sqLiteDatabase  数据库参数
     * @param oldVersion      旧版本号
     * @param newVersion      新版本号
     */

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

    }

}
