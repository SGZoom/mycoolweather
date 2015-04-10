package com.mycoolweather.app.model;

public class City {
	private int id;
	private String CityName;
	private String CityCode;
	private int provinceId;
	
	public int getid()
	{
		return id;
	}
	public String getCityName()
	{
		return CityName;
	}
	public void setCityName(String s)
	{
		CityName=s;
	}
	public String getCityCode()
	{
		return CityCode;
	}
	public void setid(int i)
	{
		id=i;
	}
	public void setCityCode(String citycode)
	{
		this.CityCode=citycode;
	}


	public int getprovinceID()
	{
		return provinceId;
	}
	public void setprovinceId(int i)
	{
		provinceId=i;
	}
}
