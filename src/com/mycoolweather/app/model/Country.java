package com.mycoolweather.app.model;

public class Country {
	private int id;
	private String CountryName;
	private String CountryCode;
	private int cityId;
	
	public int getid()
	{
		return id;
	}
	public String getCountryName()
	{
		return CountryName;
	}
	public String getCountryCode()
	{
		return CountryCode;
	}
	public void setid(int i)
	{
		id=i;
	}
	public void setCountryName(String s)
	{
		CountryName=s;
	}
	public void setCountryCode(String s)
	{
		CountryCode=s;
	}
	public int getcityID()
	{
		return cityId;
	}
	public void setcityId(int i)
	{
		cityId=i;
	}
}
