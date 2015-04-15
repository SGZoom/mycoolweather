package com.mycoolweather.app.model;

public class County {
	private int id;
	private String CountyName;
	private String CountyCode;
	private int cityId;
	
	public int getid()
	{
		return id;
	}
	public String getCountryName()
	{
		return CountyName;
	}
	public String getCountryCode()
	{
		return CountyCode;
	}
	public void setid(int i)
	{
		id=i;
	}
	public void setCountryName(String s)
	{
		CountyName=s;
	}
	public void setCountryCode(String s)
	{
		CountyCode=s;
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
