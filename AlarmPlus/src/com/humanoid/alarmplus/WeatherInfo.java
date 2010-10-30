/*
 * @(#)WeatherInfo.java 1.0 2010. 10. 29.
 */
package com.humanoid.alarmplus;

import java.util.ArrayList;

/**
 * <pre>
 * 1.기능 : . <br>
 * 2.처리 개요
 *     - 
 *     - 
 * 3.주의사항 
 *     -
 * </pre>
 * @author  천성민 2010. 10. 29.
 * @version  v1.0.0 
 * @see 
 * @since JDK v1.4
 */
public class WeatherInfo {

	private ArrayList<WeatherData> weatherList = null;
	private String address;
	
	
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public WeatherInfo() {
		weatherList = new ArrayList<WeatherData>();
	}
	
	public void addWeatherData(WeatherData data) {
		weatherList.add(data);
	}
	
	public ArrayList<WeatherData> getWeatherList() {
		return weatherList;
	}
}
