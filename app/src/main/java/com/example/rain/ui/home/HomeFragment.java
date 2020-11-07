package com.example.rain.ui.home;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.example.rain.R;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import interfaces.heweather.com.interfacesmodule.bean.Code;
import interfaces.heweather.com.interfacesmodule.bean.Lang;
import interfaces.heweather.com.interfacesmodule.bean.Unit;
import interfaces.heweather.com.interfacesmodule.bean.air.now.AirNow;
import interfaces.heweather.com.interfacesmodule.bean.weather.forecast.Forecast;
import interfaces.heweather.com.interfacesmodule.bean.weather.now.Now;
import interfaces.heweather.com.interfacesmodule.view.HeWeather;

public class HomeFragment extends Fragment {
    final String TAG = "myTag";
    static Boolean flag = false;
    private AMapLocationClient mLocationClient=null;
    //声明定位回调监听器
    private AMapLocationClientOption mLocationOption=null;
    private String location = null;
    private String today_weather = null;  // 当前天气
    private String tmp = null;  // 当前气温
    private String city = null;  // 当前城市
    private String today_tmp_high = null;  // 今天最高气温
    private String today_tmp_low = null;  // 今天最底气温
    private String todayHum;
    private String todayRain;
    private String todayPressure;
    private String todayVisible;
    private String windSc;
    private String airNum;
    private String air;  // 当前空气质量
    private String todayPm25;
    private String todayPm10;
    private String todaySo2;
    private String todayNo2;
    private String todayCo;
    private String todayO3;

    private TextView  textView_today_weather ;
    private TextView textView_tmp;
    private TextView textView_city;
    private TextView textView_today_tmp_high;
    private TextView textView_today_tmp_low;

    private TextView tvTodayHum;
    private TextView tvTodayRain;
    private TextView tvTodayPressure;
    private TextView tvTodayVisible;
    private TextView tvWindSc;
    private TextView tvAir;
    private TextView tvAirNum;
    private TextView tvTodayPm25;
    private TextView tvTodayPm10;
    private TextView tvTodaySo2;
    private TextView tvTodayNo2;
    private TextView tvTodayCo;
    private TextView tvTodayO3;
    private SwipeRefreshLayout swipeRefreshLayout;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        ScrollView scrollView = root.findViewById(R.id.scrollView);
        textView_today_weather = root.findViewById(R.id.today_weather);
        textView_tmp = root.findViewById(R.id.tmp);
        textView_city = root.findViewById(R.id.city);
        textView_today_tmp_high = root.findViewById(R.id.today_tmp_high);
        textView_today_tmp_low = root.findViewById(R.id.today_tmp_low);

        tvTodayHum = root.findViewById(R.id.tv_today_hum);
        tvTodayRain = root.findViewById(R.id.tv_today_rain);
        tvTodayPressure = root.findViewById(R.id.tv_today_pressure);
        tvTodayVisible = root.findViewById(R.id.tv_today_visible);
        tvWindSc = root.findViewById(R.id.tv_wind_sc);
        tvAir = root.findViewById(R.id.tv_air);
        tvAirNum = root.findViewById(R.id.tv_air_num);
        tvTodayPm25 = root.findViewById(R.id.tv_today_pm25);
        tvTodayPm10 = root.findViewById(R.id.tv_today_pm10);
        tvTodaySo2 = root.findViewById(R.id.tv_today_so2);
        tvTodayNo2 = root.findViewById(R.id.tv_today_no2);
        tvTodayCo = root.findViewById(R.id.tv_today_co);
        tvTodayO3 = root.findViewById(R.id.tv_today_o3);
        swipeRefreshLayout = root.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setDistanceToTriggerSync(300);

        initMap();
        getForecast(location);
        getAir(location);
        getNow(location);
        mTimeHandler.sendEmptyMessageDelayed(0, 300);


        // 下拉刷新
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getForecast(location);
                getAir(location);
                getNow(location);
                mTimeHandler.sendEmptyMessageDelayed(0, 250);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

//        String url = "https://free-api.heweather.net/s6/weather/now?location=beijing&key=9404a99698cb4f78a4b408829855f7f5";
//        RequestQueue queue = Volley.newRequestQueue(requireActivity());
//        StringRequest stringRequest = new StringRequest(
//                StringRequest.Method.GET,
//                url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        textView.setText(response);
//                        System.out.println(response);
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Log.e(TAG, "onErrorResponse: ",error );
//                    }
//                }
//        );
//        queue.add(stringRequest);
        return root;
    }
    private void initMap() {
        //初始化定位
        mLocationClient=new AMapLocationClient(requireActivity());
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式，AMapLocationMode.Battery_Saving为低功耗模式，AMapLocationMode.Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setNeedAddress(true);//设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setOnceLocation(false);//设置是否只定位一次,默认为false
        mLocationOption.setMockEnable(false);//设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setInterval(15000);//设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setOnceLocation(false);//可选，是否设置单次定位默认为false即持续定位
        mLocationOption.setOnceLocationLatest(false); //可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        mLocationOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mLocationOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }
    public AMapLocationListener mLocationListener=new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (aMapLocation != null) {
                if (aMapLocation.getErrorCode() == 0) {
                    //定位成功回调信息，设置相关消息
                    // aMapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                    // aMapLocation.getLatitude();//获取纬度
                    // aMapLocation.getLongitude();//获取经度
                    // aMapLocation.getAccuracy();//获取精度信息
                    //  aMapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                    //  aMapLocation.getCountry();//国家信息
                    //  aMapLocation.getProvince();//省信息
                    //  aMapLocation.getCity();//城市信息
                    //   aMapLocation.getDistrict();//城区信息
                    //    aMapLocation.getStreet();//街道信息
                    //     aMapLocation.getStreetNum();//街道门牌号信息
                    //    aMapLocation.getCityCode();//城市编码
                    //     aMapLocation.getAdCode();//地区编码
                    location = aMapLocation.getCity();
                    mLocationClient.stopLocation();//停止定位
                } else {
                    //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                    Log.e("info", "location Error, ErrCode:"
                            + aMapLocation.getErrorCode() + ", errInfo:"
                            + aMapLocation.getErrorInfo());
                }
            }
        }
    };
   private void getForecast(String location) {
        // 获 取天气预报
        HeWeather.getWeatherForecast(requireActivity(), location, Lang.CHINESE_SIMPLIFIED, Unit.METRIC, new HeWeather.OnResultWeatherForecastBeanListener() {
            @Override
            public void onError(Throwable throwable) {
                Log.e(TAG, "getWeatherForecast: ",throwable );
            }

            @Override
            public void onSuccess(Forecast forecast) {
                Log.i(TAG, " Weather Forecast onSuccess: " + new Gson().toJson(forecast));

                if ( Code.OK.getCode().equalsIgnoreCase(forecast.getStatus()) ){
                    //此时返回数据
                    String jsonForecast = new Gson().toJson(forecast.getDaily_forecast());
                    try{
                        JSONArray jsonArray = new JSONArray(jsonForecast);
                        JSONObject jsonObject  = (JSONObject) jsonArray.get(0);
                        today_tmp_high = jsonObject.getString("tmp_max");
                        today_tmp_low = jsonObject.getString("tmp_min");

                    }catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    //在此查看返回数据失败的原因
                    String status = forecast.getStatus();
                    Code code = Code.toEnum(status);
                    Log.i(TAG, "failed code: " + code);
                }

            }
        });
    }
    private void getAir(String location) {
        // 获取当天空气状况
        HeWeather.getAirNow(requireActivity(), location, Lang.CHINESE_SIMPLIFIED, Unit.METRIC, new HeWeather.OnResultAirNowBeansListener() {
            @Override
            public void onError(Throwable throwable) {
                Log.e(TAG, "getAirNow: ",throwable );
            }

            @Override
            public void onSuccess(AirNow dataObject) {
                Log.i(TAG, " Air Now onSuccess: " + new Gson().toJson(dataObject));
                if ( Code.OK.getCode().equalsIgnoreCase(dataObject.getStatus()) ){
                    //此时返回数据
                    String JsonAirNow = new Gson().toJson(dataObject.getAir_now_city());
                    try{
                        JSONObject jsonObject = new JSONObject(JsonAirNow);
                        airNum = jsonObject.getString("aqi");
                        air = jsonObject.getString("qlty");
                        todayPm25 = jsonObject.getString("pm25");
                        todayPm10 = jsonObject.getString("pm10");
                        todaySo2 = jsonObject.getString("so2");
                        todayNo2 = jsonObject.getString("no2");
                        todayCo = jsonObject.getString("co");
                        todayO3 = jsonObject.getString("o3");

                    }catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    //在此查看返回数据失败的原因
                    String status = dataObject.getStatus();
                    Code code = Code.toEnum(status);
                    Log.i(TAG, "failed code: " + code);
                }
            }
        });

    }

    private void getNow(String location) {
        // 获取实况天气
        HeWeather.getWeatherNow(requireActivity(), location, Lang.CHINESE_SIMPLIFIED, Unit.METRIC, new HeWeather.OnResultWeatherNowBeanListener() {
            @Override
            public void onError(Throwable throwable) {
                Log.e(TAG, "getWeatherNow: ",throwable );
            }

            @Override
            public void onSuccess(Now dataObject) {
                Log.i(TAG, " Weather Now onSuccess: " + new Gson().toJson(dataObject));

                if ( Code.OK.getCode().equalsIgnoreCase(dataObject.getStatus()) ){
                    //此时返回数据
                    String JsonNow = new Gson().toJson(dataObject.getNow());
                    String JsonBasic = new Gson().toJson(dataObject.getBasic());
                    try {
                        JSONObject jsonObject = new JSONObject(JsonNow);
                        JSONObject jsonObject2 = new JSONObject(JsonBasic);
                        today_weather = jsonObject.getString("cond_txt");
                        tmp = jsonObject.getString("tmp");
                        city = jsonObject2.getString("parent_city");
                        todayHum = jsonObject.getString("hum");
                        todayRain = jsonObject.getString("pcpn");
                        todayPressure = jsonObject.getString("pres");
                        todayVisible = jsonObject.getString("vis");
                        windSc = jsonObject.getString("wind_sc");
                    }catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    //在此查看返回数据失败的原因
                    String status = dataObject.getStatus();
                    Code code = Code.toEnum(status);
                    Log.i(TAG, "failed code: " + code);
                }
            }
        });
    }

    Handler mTimeHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 0) {
                if (today_tmp_high !=null)
                    textView_today_tmp_high.setText(today_tmp_high + "°");
                if (today_tmp_low !=null)
                    textView_today_tmp_low.setText(today_tmp_low + "°");
                textView_city.setText(city);
                textView_today_weather.setText(today_weather);
                if (tmp !=null)
                    textView_tmp.setText(tmp + "°");

                tvTodayHum.setText(todayHum);
                tvTodayRain.setText(todayRain);
                tvTodayPressure.setText(todayPressure);
                if (todayVisible !=null)
                    tvTodayVisible.setText(todayVisible + "KM");
                if (windSc !=null)
                    tvWindSc.setText(windSc + "级");
                tvAir.setText(air);
                tvAirNum.setText(airNum);
                tvTodayPm25.setText(todayPm25);
                tvTodayPm10.setText(todayPm10);
                tvTodaySo2.setText(todaySo2);
                tvTodayNo2.setText(todayNo2);
                tvTodayCo.setText(todayCo);
                tvTodayO3.setText(todayO3);
            }
        }
    };
    @Override
    public void onDestroy() {
        super.onDestroy();
        //销毁
        if (mLocationClient !=null){
            mLocationClient.onDestroy();
        }
    }
}