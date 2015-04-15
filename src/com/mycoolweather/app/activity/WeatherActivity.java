package com.mycoolweather.app.activity;

import com.mycoolweather.app.R;
import com.mycoolweather.app.receiver.AutoUpdateReceiver;
import com.mycoolweather.app.util.HttpCallbackListener;
import com.mycoolweather.app.util.HttpUtil;
import com.mycoolweather.app.util.Utility;

import android.app.Activity;
import android.app.PendingIntent.OnFinished;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WeatherActivity extends Activity implements OnClickListener {
	private LinearLayout weatherInfoLayout;
	private TextView cityNameText;
	private TextView publishText;// 发布时间
	private TextView weatherDespText;// 显示天气描述信息
	private TextView temp1Text;// 气温1.2
	private TextView temp2Text;
	private TextView currentDateText;
	private Button switchCity;// 切换城市按钮
	private Button refreshWeather;// 刷新天气

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);

		weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
		cityNameText = (TextView) findViewById(R.id.city_name);
		publishText = (TextView) findViewById(R.id.publish_text);
		weatherDespText = (TextView) findViewById(R.id.weather_desp);
		temp1Text = (TextView) findViewById(R.id.temp1);
		temp2Text = (TextView) findViewById(R.id.temp2);
		currentDateText = (TextView) findViewById(R.id.current_date);
		switchCity = (Button) findViewById(R.id.switch_city);
		refreshWeather = (Button) findViewById(R.id.refresh_weather);
		String countrycode = getIntent().getStringExtra("county_code");
		if (!TextUtils.isEmpty(countrycode)) {
			// 有县级代号就去查天气
			publishText.setText("同步中。。。");
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			cityNameText.setVisibility(View.INVISIBLE);
			queryWeatherCode(countrycode);
		} else {
			showWeather();
		}
		switchCity.setOnClickListener(this);
		refreshWeather.setOnClickListener(this);

	}

	private void showWeather() {
		// TODO Auto-generated method stub
		// 显示天气到界面上
		SharedPreferences pres = PreferenceManager
				.getDefaultSharedPreferences(this);
		cityNameText.setText(pres.getString("city_name", ""));
		temp1Text.setText(pres.getString("temp1", ""));
		temp2Text.setText(pres.getString("temp2", ""));
		weatherDespText.setText(pres.getString("weather_desp", ""));
		publishText.setText("今天" + pres.getString("publish_time", "") + "发布");
		currentDateText.setText(pres.getString("current_date", ""));
		weatherInfoLayout.setVisibility(View.VISIBLE);
		cityNameText.setVisibility(View.VISIBLE);
		Intent intent=new Intent(this,AutoUpdateReceiver.class);
		startService(intent);
	}

	// 获取县级代号对应的天气代号
	private void queryWeatherCode(String countrycode) {
		// TODO Auto-generated method stub
		String address = "http://www.weather.com.cn/data/list3/city"
				+ countrycode + ".xml";
		queryFromServer(address, "countyCode");
	}

	// 查询代号对应的天气
	private void queryWeatherInfo(String weatherCode) {
		String address = "http://www.weather.com.cn/data/cityinfo/"
				+ weatherCode + ".html";
		queryFromServer(address, "weatherCode");

	}

	private void queryFromServer(final String address, final String type) {
		// TODO Auto-generated method stub
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {

			@Override
			public void onFinish(String response) {
				Log.d("Taa", "WeatherAct.queryserver.respond=="+response);
				// TODO Auto-generated method stub
				if ("countyCode".equals(type)) {// 从服务器返回的数据中解析出天气代号
					if (!TextUtils.isEmpty(response)) {
						String[] array = response.split("\\|");
						if (array != null && array.length == 2) {
							String weatherCode = array[1];
							queryWeatherInfo(weatherCode);
						}

					}
				} else if ("weatherCode".equals(type)) {// 处理服务器返回的天气信息
					Utility.handleWeatherResponse(WeatherActivity.this,
							response);
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							Log.d("Taa", "Weather.Httoutil.run.start");
							showWeather();
							
						}
					});

				}
			}

			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				publishText.setText("同步失败");
			}
		});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.switch_city:
			Intent intent = new Intent(this, ChooseAreaActivity.class);
			intent.putExtra("from_weather_activity", true);
			startActivity(intent);
			finish();
			break;
		case R.id.refresh_weather:
			publishText.setText("同步中...");
			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(this);
			String weatherCode = prefs.getString("weather_code", "");
			if (!TextUtils.isEmpty(weatherCode)) {
				queryWeatherInfo(weatherCode);
			}
			break;
		}
	}

}
