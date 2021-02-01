package com.restassured.pojo;

import java.util.List;

public class WeatherData {
	
	private String country_code;
	
	private String state_code;
	
	private List<ZoneData> data;

	public String getCountry_code() {
		return country_code;
	}

	public void setCountry_code(String country_code) {
		this.country_code = country_code;
	}

	public String getState_code() {
		return state_code;
	}

	public void setState_code(String state_code) {
		this.state_code = state_code;
	}

	public List<ZoneData> getData() {
		return data;
	}

	public void setData(List<ZoneData> data) {
		this.data = data;
	}

}
