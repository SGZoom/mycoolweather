package com.mycoolweather.app.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
//遍历省市数据
import android.widget.TextView;
import android.widget.Toast;

import com.mycoolweather.app.R;
//import com.mycoolweather.app.R;
import com.mycoolweather.app.db.CoolWeatherDB;
import com.mycoolweather.app.model.City;
import com.mycoolweather.app.model.County;
import com.mycoolweather.app.model.Province;
import com.mycoolweather.app.util.HttpCallbackListener;
import com.mycoolweather.app.util.HttpUtil;
import com.mycoolweather.app.util.Utility;

public class ChooseAreaActivity extends Activity {
	public static final int LEVEL_PROVINCE = 0;
	public static final int LEVEL_CITY = 1;
	public static final int LEVEL_COUNTY = 2;
	// 是否是从weatherActivity中过来的
	private boolean isFromWeatherAct;
	private ProgressDialog progressDialog;
	private TextView titleview;
	private ListView listview;
	private ArrayAdapter<String> adapter;
	private CoolWeatherDB coolWeatherDB;
	private List<String> dataList = new ArrayList<String>();
	// 省列表
	private List<Province> provinceList;
	private List<City> cityList;
	private List<County> countyList;
	// 选中的省份
	private Province selectedProvince;
	private City selectedCity;
	// 选中的级别
	private int currentLevel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		isFromWeatherAct = getIntent().getBooleanExtra("from_weather_activity",
				false);
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);// 用来储存的
		// 已经选择了城市而不是从weather跳转过来，才会直接跳转到weatherActivity
		if (prefs.getBoolean("city_selected", false) && !isFromWeatherAct)// 如果不存在就返回false
		{
			Intent intent = new Intent(this, WeatherActivity.class);
			startActivity(intent);
			finish();
			return;
		}
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);
		listview = (ListView) findViewById(R.id.listView);
		titleview = (TextView) findViewById(R.id.titletext);
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, dataList);
		listview.setAdapter(adapter);
		coolWeatherDB = CoolWeatherDB.getInstance(this);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if (currentLevel == LEVEL_PROVINCE) {
					selectedProvince = provinceList.get(position);
					titleview.setText(selectedProvince.getprovinceName());
					queryCities();
				} else if (currentLevel == LEVEL_CITY) {
					selectedCity = cityList.get(position);
					queryCounties();
				} else if (currentLevel == LEVEL_COUNTY) {
					String countryCode = countyList.get(position)
							.getCountryCode();
					Intent intent = new Intent(ChooseAreaActivity.this,
							WeatherActivity.class);
					intent.putExtra("county_code", countryCode);
					Log.d("Taa", "onclick ,LEVEL_county,countryCode=="+countryCode);
					startActivity(intent);
					finish();
				}

			}
		});
		queryProvinces();
	}

	// 查询全国所有的省，优先从数据库查询，没有的话再到服务器查
	private void queryProvinces() {
		provinceList = coolWeatherDB.loadProvinces();
		if (provinceList.size() > 0) {
			dataList.clear();
			for (Province province : provinceList) {
				dataList.add(province.getprovinceName());
			}
			adapter.notifyDataSetChanged();
			listview.setSelection(0);
			titleview.setText("China");
			currentLevel = LEVEL_PROVINCE;
		} else {
			queryFromServer(null, "province");
			Log.d("Taa", "queryProvince,province.list==0,进入查询网络");
		}
	}

	// 查询县
	private void queryCounties() {
		// TODO Auto-generated method stub
		countyList = coolWeatherDB.loadCountries(selectedCity.getid());
		if (countyList.size() > 0) {
			dataList.clear();
			for (County country : countyList) {
				dataList.add(country.getCountryName());
				Log.d("Taa", country.getCountryName());
			}
			adapter.notifyDataSetChanged();
			listview.setSelection(0);
			titleview.setText(selectedCity.getCityName());
			currentLevel = LEVEL_COUNTY;
		} else {
			queryFromServer(selectedCity.getCityCode(), "county");
			Log.d("Taa", "queryCounties,Counties.list==0,进入查询网络");
		}
	}

	// 查询所有的城市
	private void queryCities() {
		// TODO Auto-generated method stub
		cityList = coolWeatherDB.loadCities(selectedProvince.getid());
		if (cityList.size() > 0) {
			dataList.clear();
			for (City city : cityList) {
				dataList.add(city.getCityName());
			}
			adapter.notifyDataSetChanged();
			listview.setSelection(0);
			titleview.setText(selectedProvince.getprovinceName());
			currentLevel = LEVEL_CITY;
		} else {
			queryFromServer(selectedProvince.getprovinceCode(), "city");
			Log.d("Taa", "queryCityies,City.list==0,进入查询网络");
		}
	}

	private void queryFromServer(final String code, final String type) {
		// TODO Auto-generated method stub
		String address;
		if (!TextUtils.isEmpty(code)) {
			address = "http://www.weather.com.cn/data/list3/city" + code
					+ ".xml";
		} else {
			address = "http://www.weather.com.cn/data/list3/city.xml";
		}
		showProgressDialog();
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			@Override
			public void onFinish(String response) {
				// TODO Auto-generated method stub
				boolean result = false;
				Log.d("Taa", "进入onfinish,respond==  " + response);
				if ("province".equals(type)) {
					result = Utility.handleProvincesRespnse(coolWeatherDB,
							response);
					Log.d("Taa", "province:  " + response + "  " + result);
				} else if ("city".equals(type)) {
					result = Utility.handleCitiesResponse(coolWeatherDB,
							response, selectedProvince.getid());
					Log.d("Taa", "city:  " + response + "  Provinceid: "
							+ selectedProvince.getid() + "  " + result);
				} else if ("county".equals(type)) {
					result = Utility.handleCountiesResponse(coolWeatherDB,
							response, selectedCity.getid());
					Log.d("Taa", "county:  " + response + "  " + result);
				}
				if (result) {
					// 通过runonUiThread()方法回到主线程处理逻辑
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							Log.d("Taa", "进入run");
							closeProgressDialog();
							if ("province".equals(type)) {
								queryProvinces();
							} else if ("city".equals(type)) {
								queryCities();
								Log.d("Taa", "in run in city,finish");
							} else if ("county".equals(type)) {
								queryCounties();
								Log.d("Taa", "in run in county,完成");
							}
						}

					});
				}
				// Toast.makeText(ChooseAreaActivity.this,
				// "Succeeed in onfinish", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "加载失败",
								Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}

	private void showProgressDialog() {
		// TODO Auto-generated method stub
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("正在加载...");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}

	private void closeProgressDialog() {
		// TODO Auto-generated method stub
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}

	// 捕获back按钮，判断是应该返回市列表还是退出。。。
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (currentLevel == LEVEL_COUNTY) {
			queryCities();
		} else if (currentLevel == LEVEL_CITY) {
			queryProvinces();
		} else {
			finish();
		}
	}
}
