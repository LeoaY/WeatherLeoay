package com.android.leoay.weatherleoay;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.*;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import model.City;
import model.County;
import model.Province;
import model.WeatherLeoayDB;
import util.HttpCallbackListener;
import util.HttpUtil;
import util.Utility;

public class ChooseAreaActivity extends Activity {

    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY     = 1;
    public static final int LEVEL_COUNTY   = 2;

    private ProgressDialog progressDialog;
    private TextView       titletextView;
    private ListView       listView;
    private ArrayAdapter<String> adapter;
    private WeatherLeoayDB weatherLeoayDB;
    private List<String> dataList = new ArrayList<String>();
    private Typeface typeface;
    /**
     * 省列表
     * @param savedInstanceState
     */

    private List<Province> provinceList;

    /**
     * 市列表
     * @param savedInstanceState
     */
    private List<City> cityList;

    /**
     * 县列表
     * @param savedInstanceState
     */
    private List<County> countyList;

    /**
     * 选中的省
     * @param savedInstanceState
     */
    private Province selectedProvince;

    /**
     * 选中的市
     */
    private City    selectedCity;


    /**
     * 当前选中的级别
     * @param savedInstanceState
     */
    private int currentLevel;

    /**
     * 是否从WeatherActivity中跳转过来
     * @param savedInstanceState
     */
    private boolean isFromWeatherActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isFromWeatherActivity = getIntent().getBooleanExtra("from_weather_activity", false);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        //已经选择了城市， 并且不是从WeatherActivity跳转过来的，才会直接跳转到WeatherActivity

        if(sharedPreferences.getBoolean("city_selected", false) && !isFromWeatherActivity){
            Intent intent = new Intent(this, WeatherActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        setContentView(R.layout.activity_choose_area);
        typeface = Typeface.createFromAsset(getAssets(), "fonts/fz.ttf");
        listView = (ListView) findViewById(R.id.list_view);
        titletextView = (TextView) findViewById(R.id.title_text);
        titletextView.setTypeface(typeface);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        weatherLeoayDB = WeatherLeoayDB.getWeatherLeoayDB(this);
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long arg3) {
                Log.d(index+"" , "测试数据22");
                if(currentLevel == LEVEL_PROVINCE){
                    selectedProvince = provinceList.get(index);
                    queryCities();
                }else if(currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(index);
                    queryCounties();
                }else if(currentLevel == LEVEL_COUNTY){
                 String countyCode = countyList.get(index).getCountyCode();
                 Intent intent = new Intent(ChooseAreaActivity.this, WeatherActivity.class);
                 intent.putExtra("county_code", countyCode);
                 startActivity(intent);
                 finish();
                }
            }
        });
        queryProvince();
    }

    /**
     * 查询所有的省， 优先从数据库查询， 如果没有查询到再去服务器上查询
     */

    private void queryProvince(){
        provinceList = weatherLeoayDB.loadProvinces();
        if(provinceList.size() > 0){
            dataList.clear();
            for(Province province : provinceList){
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titletextView.setText("中国");
            currentLevel = LEVEL_PROVINCE;
        }
        else {
            queryFromServer(null, "province");
        }
    }


    /**
     * 查询选中的省内所有市，优先从数据库查询,如果没有再从服务器上查询
     */
    private void queryCities(){
        cityList = weatherLeoayDB.loadCities(selectedProvince.getId());
        if(cityList.size() > 0){
            dataList.clear();
            for(City city : cityList){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titletextView.setText(selectedProvince.getProvinceName());
            currentLevel = LEVEL_CITY;
        }
        else{
            queryFromServer(selectedProvince.getProvinceCode(), "city");
        }
    }

    /**
     * 查询选中市内所有的县，优先从数据库查询，如果没有在从服务器查询
     */
    private void queryCounties(){
        countyList = weatherLeoayDB.loadCounties(selectedCity.getId());
        if(countyList.size() > 0){
            dataList.clear();
            for(County county : countyList){
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titletextView.setText(selectedCity.getCityName());
            currentLevel = LEVEL_COUNTY;
        }
        else {
            queryFromServer(selectedCity.getCityCode(), "county");
            Log.d(selectedCity.getCityCode(), "测试数据1");
        }
    }

    /**
     * 根据传入的代号和类型从服务器上查询省市县的数据
     */
    private void queryFromServer(final String code, final String type){
        String address;
        if(!TextUtils.isEmpty(code)){
            address = "http://www.weather.com.cn/data/list3/city" + code + ".xml";
        }
        else{
            address = "http://www.weather.com.cn/data/list3/city.xml";
        }
        showProgressDialog();
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                boolean result = false;
                if("province".equals(type)){
                    result = Utility.handleProvincesResponse(weatherLeoayDB, response);
                }
                else if("city".equals(type)){
                    result = Utility.handleCitiesResponse(weatherLeoayDB, response, selectedProvince.getId());
                }
                else if("county".equals(type)){
                    result = Utility.handleCountiesResponse(weatherLeoayDB, response, selectedCity.getId());
                }
                if(result){
                    //通过runOnUIThread()方法回到主线程处理逻辑
                    runOnUiThread(lnew Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if("province".equals(type)){
                                queryProvince();
                            }
                            else if("city".equals(type)){
                                queryCities();
                            }
                            else if("county".equals(type)){
                                queryCounties();
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                //通过runOnUIThread()方法回到主线程处理逻辑
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /**
     * 显示进度对话框
     */

    private void showProgressDialog(){
        if(progressDialog == null){
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /**
     * 关闭进度条对话框
     */
    private void closeProgressDialog(){
        if(progressDialog != null){
            progressDialog.dismiss();
        }
    }

    /**
     * 捕获back键，根据当前的级别来判断，此时应该返回市列表，省列表，还是直接退出
     */
    @Override
    public void onBackPressed(){
        if(currentLevel == LEVEL_COUNTY){
            queryCities();
        } else if(currentLevel == LEVEL_CITY){
            queryProvince();
        }else {
            if(isFromWeatherActivity){
                Intent intent = new Intent(this, WeatherActivity.class);
                startActivity(intent);
            }
            finish();
        }
    }



}
