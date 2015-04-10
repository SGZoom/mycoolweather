package com.mycoolweather.app.model;
//实体类
public class Province {
	private int id;
	private String provinceName;
	private String provinceCode;
	
	public int getid()
	{
		return id;
	}
	public String getprovinceName()
	{
		return provinceName;
	}
	public String getprovinceCode()
	{
		return provinceCode;
	}
	public void setid(int i)
	{
		id=i;
	}
	public void setProvinceName(String s)
	{
		provinceName=s;
	}
	public void setProvinceCode(String s)
	{
		provinceCode=s;
	}
}
