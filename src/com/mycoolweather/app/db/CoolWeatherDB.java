package com.mycoolweather.app.db;

import java.util.ArrayList;
import java.util.List;

import com.mycoolweather.app.model.City;
import com.mycoolweather.app.model.Country;
import com.mycoolweather.app.model.Province;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CoolWeatherDB {
	// 数据库名
	public static final String DB_NAME = "cool_weather";
	// 数据库版本
	public static final int VERSION = 1;
	private static CoolWeatherDB coolweatherDB;
	private SQLiteDatabase db;

	// 构造方法私有化
	private CoolWeatherDB(Context context) {
		CoolWeatherOpenHelper dbhelper = new CoolWeatherOpenHelper(context,
				DB_NAME, null, VERSION);
		db = dbhelper.getWritableDatabase();
	}

	// 获取CoolWeatherDb的实例
	public synchronized static CoolWeatherDB getInstance(Context context) {
		if (coolweatherDB == null) {
			coolweatherDB = new CoolWeatherDB(context);
		}
		return coolweatherDB;
	}

	// 将Province实例储存到数据库
	public void saveProvince(Province province) {
		if (province != null) {
			ContentValues values = new ContentValues();// This class is used to
														// store a set of values
														// that the
														// ContentResolver can
														// process.
			values.put("province_name", province.getprovinceName());
			values.put("province_code", province.getprovinceCode());
			db.insert("Province", null, values);
		}
		
	}
	//从数据库读取全国的省份信息
	public List<Province>loadProvinces()
	{
		List<Province>list=new ArrayList<Province>();
		Cursor cursor=db.query("Province", null,null, null,null,null, null);
		if(cursor.moveToFirst())
		{
			do{
				Province province =new Province();
				province.setid(cursor.getInt(cursor.getColumnIndex("id")));
				province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
				province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
				list.add(province);
			}while(cursor.moveToNext());
			
		}
		return list;
	}
	//将city实例储存到数据库
	public void saveCity(City city)
	{
		if(city!=null)
		{
			ContentValues values=new ContentValues();
			values.put("city_name", city.getCityName());
			values.put("city_code", city.getCityCode());
			values.put("province_id", city.getprovinceID());
			db.insert("City", null, values);
		}
	}
	//从数据库读取某省的所有城市信息
	public List<City> loadCities(int provinceid)
	{
		List<City>list=new ArrayList<City>();
		Cursor cursor=db.query("City", null, "province_id = ?", new String[]{String.valueOf(provinceid)},null, null, null);
		if(cursor.moveToFirst())
		{
			do{
				City city=new City();
				city.setid(cursor.getInt(cursor.getColumnIndex("id")));
				city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
				city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
				city.setprovinceId(provinceid);
				list.add(city);
			}while(cursor.moveToNext());
			
		}
		return list;
	}
	public void saveCountry(Country country)
	{
		if(country!=null)
		{
			ContentValues values=new ContentValues();
			values.put("country_name", country.getCountryName());
			values.put("country_code", country.getCountryCode());
			values.put("city_id", country.getcityID());
			db.insert("Country", null, values);
		}
	}
	
	public List<Country> loadCountries(int cityid)
	{
		List<Country>list=new ArrayList<Country>();
		Cursor cursor=db.query("Country", null, "city_id = ?", new String[]{String.valueOf(cityid)},null, null, null);
		if(cursor.moveToFirst())
		{
			do{
				Country country=new Country();
				country.setid(cursor.getInt(cursor.getColumnIndex("id")));
				country.setCountryName(cursor.getString(cursor.getColumnIndex("country_name")));
				country.setCountryCode(cursor.getString(cursor.getColumnIndex("country_code")));
				country.setcityId(cityid);
				list.add(country);
			}while(cursor.moveToNext());
		}
		return list;
	}
}
