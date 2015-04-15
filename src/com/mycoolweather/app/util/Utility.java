package com.mycoolweather.app.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.mycoolweather.app.db.CoolWeatherDB;
import com.mycoolweather.app.model.City;
import com.mycoolweather.app.model.County;
import com.mycoolweather.app.model.Province;

//解析包，解析数据  代号|城市，代号|城市
/*ava语言的关键字，可用来给对象和方法或者代码块加锁，
 * 当它锁定一个方法或者一个代码块的时候，同一时刻最多只有一个线程执行这段代码。
 * 当两个并发线程访问同一个对象object中的这个加锁同步代码块时，一个时间内只能有一个线程得到执行。
 * 另一个线程必须等待当前线程执行完这个代码块以后才能执行该代码块。
 * 然而，当一个线程访问object的一个加锁代码块时，另一个线程仍然可以访问该object中的非加锁代码块。*/
public class Utility {
	// 解析省级数据
	public synchronized static boolean handleProvincesRespnse(
			CoolWeatherDB coolWeatherDB, String response) {
		if (!TextUtils.isEmpty(response)) {
			String[] allProvinces = response.split(",");// 分割成字符串
			if (allProvinces != null && allProvinces.length > 0) {
				for (String p : allProvinces) {
					String[] array = p.split("\\|");
					Province province = new Province();
					province.setProvinceCode(array[0]);
					province.setProvinceName(array[1]);
					// 储存到province表
					coolWeatherDB.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}

	// 解析市级数据
	public static boolean handleCitiesResponse(CoolWeatherDB coolWeatherDB,
			String response, int provinceID) {
		if (!TextUtils.isEmpty(response)) {
			String[] allCities = response.split(",");
			if (allCities != null && allCities.length > 0) {
				for (String c : allCities) {
					String[] array = c.split("\\|");//一开始这里打错了成//|。。。
					City city = new City();
					city.setCityCode(array[0]);
					city.setCityName(array[1]);
					city.setprovinceId(provinceID);
					coolWeatherDB.saveCity(city);
				}
				return true;
			}
		}
		return false;
	}

	public static boolean handleCountiesResponse(CoolWeatherDB coolWeatherDB,
			String response, int cityId) {
		if (!TextUtils.isEmpty(response)) {
			String[] allcountries = response.split(",");
			if (allcountries != null && allcountries.length > 0) {
				for (String c : allcountries) {
					String[] array = c.split("\\|");
					County country = new County();
					country.setCountryCode(array[0]);
					country.setCountryName(array[1]);
					country.setcityId(cityId);
					coolWeatherDB.saveCounty(country);
				}
				return true;
			}

		}
		return false;
	}

	// 解析服务器返回的json数据
	public static void handleWeatherResponse(Context context, String response) {
		try {
			JSONObject jsonbject = new JSONObject(response);
			JSONObject weatherInfo = jsonbject.getJSONObject("weatherinfo");
			String cityName = weatherInfo.getString("city");
			String weathercode = weatherInfo.getString("cityid");
			String temp1 = weatherInfo.getString("temp1");
			String temp2 = weatherInfo.getString("temp2");
			String weatherDesp = weatherInfo.getString("weather");
			String publishTime = weatherInfo.getString("ptime");
			saveWeaherInfo(context, cityName, weathercode, temp1, temp2,
					weatherDesp, publishTime);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void saveWeaherInfo(Context context, String cityName,
			String weathercode, String temp1, String temp2, String weatherDesp,
			String publishTime) {
		// TODO Auto-generated method stub
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
		SharedPreferences.Editor editor = PreferenceManager
				.getDefaultSharedPreferences(context).edit();// z合格玩意好像是处理参数的
		editor.putBoolean("city_selected", true);
		editor.putString("city_name", cityName);
		editor.putString("weather_code", weathercode);
		editor.putString("temp1", temp1);
		editor.putString("temp2", temp2);
		editor.putString("weather_desp", weatherDesp);
		editor.putString("publish_time", publishTime);
		editor.putString("current_date", sdf.format(new Date()));
		editor.commit();

	}
}
