package model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import db.WeatherLeoayOpenHelper;

/**
 * Created by leoay on 2016/12/15.
 * 这个类把常用的数据库操作封装起来
 */

public class WeatherLeoayDB {

    /**
     * 数据库名
     */
    private static final String DB_NAME = "weather_leoay";

    /**
     * 数据库版本
     */
    private static final int DB_VERSION = 1;

    private static WeatherLeoayDB weatherLeoayDB;

    /**
     * 创建一个SQLiteDatabase数据库对象
     */

    private SQLiteDatabase sqLiteDatabase;

    /**
     * 将构造方法私有化
     */

    private WeatherLeoayDB(Context context) {
        WeatherLeoayOpenHelper weatherLeoayOpenHelper = new WeatherLeoayOpenHelper(context, DB_NAME, null, DB_VERSION);
        sqLiteDatabase = weatherLeoayOpenHelper.getWritableDatabase();
    }

    /**
     * 获取WeatherLeoayDB的实例
     */
    public synchronized static WeatherLeoayDB getWeatherLeoayDB(Context context) {
        if (weatherLeoayDB == null) {
            weatherLeoayDB = new WeatherLeoayDB(context);
        }
        return weatherLeoayDB;
    }

    /**
     * 将Province实例存储到数据库
     */

    public void saveProvince(Province province) {
        if (province != null) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("province_name", province.getProvinceName());
            contentValues.put("province_code", province.getProvinceCode());
            sqLiteDatabase.insert("Province", null, contentValues);
        }
    }

    /**
     * 从数据库读取全国所有省份的信息
     */

    public List<Province> loadProvinces() {
        List<Province> list = new ArrayList<Province>();
        Cursor cursor = sqLiteDatabase.query("Province", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Province province = new Province();
                province.setId(cursor.getInt(cursor.getColumnIndex("id")));
                province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
                province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
                list.add(province);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return list;
    }

    /**
     * 将City实例存储到数据库
     */
    public void saveCity(City city) {
        if (city != null) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("city_name", city.getCityName());
            contentValues.put("city_code", city.getCityCode());
            contentValues.put("province_id", city.getProvinceId());
            sqLiteDatabase.insert("City", null, contentValues);
        }
    }

    /**
     * 从数据库读取某省下所有的城市信息
     */

    public List<City> loadCities(int provinceId) {
        List<City> list = new ArrayList<City>();
        Cursor cursor = sqLiteDatabase.query("City", null, "province_id = ?", new String[]{String.valueOf(provinceId)}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                City city = new City();
                city.setId(cursor.getInt(cursor.getColumnIndex("id")));
                city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
                city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
                city.setProvinceId(provinceId);
                list.add(city);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return list;
    }

    /**
     * 将County实例存储到数据库
     */
    public void saveCounty(County county) {
        if (county != null) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("county_name", county.getCountyName());
            contentValues.put("county_code", county.getCountyCode());
            contentValues.put("city_id", county.getCityId());
            sqLiteDatabase.insert("County", null, contentValues);
        }
    }

    /**
     * 从数据库中读取某城市下所有的县信息  根据City_id
     */
    public List<County> loadCounties(int cityId) {
        List<County> list = new ArrayList<County>();
        Cursor cursor = sqLiteDatabase.query("County", null, "city_id = ?", new String[]{String.valueOf(cityId)}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                County county = new County();
                county.setId(cursor.getInt(cursor.getColumnIndex("id")));
                county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
                county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
                county.setCityId(cityId);
                list.add(county);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return list;
    }
}
