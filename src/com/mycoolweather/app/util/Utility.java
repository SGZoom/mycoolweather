package com.mycoolweather.app.util;

import android.text.TextUtils;

import com.mycoolweather.app.db.CoolWeatherDB;
import com.mycoolweather.app.model.City;
import com.mycoolweather.app.model.Country;
import com.mycoolweather.app.model.Province;

//解析包，解析数据  代号|城市，代号|城市
/*ava语言的关键字，可用来给对象和方法或者代码块加锁，
 * 当它锁定一个方法或者一个代码块的时候，同一时刻最多只有一个线程执行这段代码。
 * 当两个并发线程访问同一个对象object中的这个加锁同步代码块时，一个时间内只能有一个线程得到执行。
 * 另一个线程必须等待当前线程执行完这个代码块以后才能执行该代码块。
 * 然而，当一个线程访问object的一个加锁代码块时，另一个线程仍然可以访问该object中的非加锁代码块。*/
public class Utility {
	//解析省级数据
	public synchronized static boolean handleProvincesRespnse(CoolWeatherDB coolWeatherDB,String response)
	{
		if(!TextUtils.isEmpty(response))
		{
			String[]allProvinces=response.split(",");//分割成字符串
			if(allProvinces!=null&&allProvinces.length>0)
			{
				for(String p:allProvinces)
				{
					String[]array=p.split("\\|");
					Province province =new Province();
					province.setProvinceCode(array[0]);
					province.setProvinceName(array[1]);
					//储存到province表
					coolWeatherDB.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}
	//解析市级数据
	public static boolean handleCitiesResponse(CoolWeatherDB coolWeatherDB,String response,int provinceID)
	{
		if(!TextUtils.isEmpty(response))
		{
			String[]allCities=response.split(",");
			if(allCities!=null&&allCities.length>0)
			{
				for(String c:allCities)
				{
					String []array=c.split("//|");
					City city=new City();
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
	
	public static boolean handleCountiesResponse(CoolWeatherDB coolWeatherDB,String response,int cityId)
	{
		if(!TextUtils.isEmpty(response))
		{
			String[]allcountries=response.split(",");
			if(allcountries!=null&&allcountries.length>0)
			{
				for(String c:allcountries)
				{
					String[]array=c.split("//|");
					Country country=new Country();
					country.setCountryCode(array[0]);
					country.setCountryName(array[1]);
					country.setcityId(cityId);
					coolWeatherDB.saveCountry(country);
				}
				return true;
			}
			
		}
		return false;
	}
}
