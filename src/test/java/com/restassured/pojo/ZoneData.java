package com.restassured.pojo;

public class ZoneData {
	
	private String max_temp;
	
	private String low_temp;
	
	private String uv;
	
	private String valid_date;

	public String getMax_temp() {
		return max_temp;
	}

	public void setMax_temp(String max_temp) {
		this.max_temp = max_temp;
	}

	public String getLow_temp() {
		return low_temp;
	}

	public void setLow_temp(String low_temp) {
		this.low_temp = low_temp;
	}

	public String getUv() {
		return uv;
	}

	public void setUv(String uv) {
		this.uv = uv;
	}

	public String getValid_date() {
		return valid_date;
	}

	public void setValid_date(String valid_date) {
		this.valid_date = valid_date;
	}

}
