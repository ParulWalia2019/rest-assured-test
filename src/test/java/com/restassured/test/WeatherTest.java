package com.restassured.test;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

import static com.restassured.base.ConstantUtil.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import com.google.gson.Gson;
import com.restassured.pojo.WeatherData;
import com.restassured.pojo.ZoneData;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class WeatherTest {

	
	public static Response getResponse() {
		return given().queryParam(KEY_CONST, API_KEY).queryParam(POSTAL_CODE_CONST, BONDI_POST_CODE)
				.queryParam(POSTAL_CODE_CONST, MANLY_POST_CODE).queryParam(COUNTRY_CONST, AUS_COUNTRY).when()
				.get(API_URL).then().contentType(ContentType.JSON).extract().response();
	}

	public static WeatherData getWeatherData() {
		Gson gson = new com.google.gson.Gson();
		Response response = getResponse();
		WeatherData weatherData = gson.fromJson(response.asString(), WeatherData.class);
		return weatherData;
	}

	/**
	 * Logs Response Body
	 */
	@Test
	public void getResponseBody() {
		given().queryParam(KEY_CONST, API_KEY).queryParam(POSTAL_CODE_CONST, BONDI_POST_CODE)
				.queryParam(POSTAL_CODE_CONST, MANLY_POST_CODE).queryParam(COUNTRY_CONST, AUS_COUNTRY).when()
				.get(API_URL).then().log().body();
	}

	@Test
	public void testSuccessResponseCode() {
		getResponse().then().assertThat().statusCode(HttpStatus.SC_OK);
	}

	/**
	 * Validates Response is valid Json
	 */
	@Test
	public void testSchemaValidation() {
		getResponse().then().assertThat().body(matchesJsonSchemaInClasspath("weatherSample.json"));
	}

	/**
	 * Count no of days in next 16 days 
	 */
	@Test
	public void testCountNoOfThursAndFriInResponse() {
		
		WeatherData weatherData = getWeatherData();
		List<ZoneData> zoneData = weatherData.getData();

		int thursdayCount = 0, fridayCount = 0;
		for (ZoneData data : zoneData) {
			String date = data.getValid_date();
			String[] dateArr = date.split("-");
			Calendar cal = Calendar.getInstance();
			System.out.println(date);
			cal.set(Calendar.YEAR, Integer.valueOf(dateArr[0]));
			cal.set(Calendar.MONTH, Integer.valueOf(dateArr[1]) - 1);
			cal.set(Calendar.DATE, Integer.valueOf(dateArr[2]));

			if (Calendar.THURSDAY == cal.get(Calendar.DAY_OF_WEEK)) {
				
				thursdayCount++;
			}

			if (Calendar.FRIDAY == cal.get(Calendar.DAY_OF_WEEK)) {
				
				fridayCount++;
			}
		}
		System.out.println("Total Thurdays : " + thursdayCount);
		System.out.println("Total Fridays :  " + fridayCount);

	
	}
	/**
	 * Find Suitable date
	 */
	@Test
	public void testFindSuitableDate() {
		WeatherData weatherData = getWeatherData();
		System.out.println(weatherData.getData().size());
		List<ZoneData> zoneData = weatherData.getData();

		int thursdayCount = 0, fridayCount = 0;
		List<ZoneData> shortListedDates = new ArrayList<ZoneData>();
		for (ZoneData data : zoneData) {
			String date = data.getValid_date();
			String[] dateArr = date.split("-");
			Calendar cal = Calendar.getInstance();
			System.out.println(date);
			cal.set(Calendar.YEAR, Integer.valueOf(dateArr[0]));
			cal.set(Calendar.MONTH, Integer.valueOf(dateArr[1]) - 1);
			cal.set(Calendar.DATE, Integer.valueOf(dateArr[2]));

			if (Calendar.THURSDAY == cal.get(Calendar.DAY_OF_WEEK) 
					&& Float.valueOf(data.getLow_temp()) > 20
					&& Float.valueOf(data.getMax_temp()) < 30 
					&& Float.valueOf(data.getUv()) <= 3) {
				
				thursdayCount++;
				shortListedDates.add(data);
			}

			if (Calendar.FRIDAY == cal.get(Calendar.DAY_OF_WEEK) 
					&& Float.valueOf(data.getLow_temp()) > 20
					&& Float.valueOf(data.getMax_temp()) < 30 
					&& Float.valueOf(data.getUv()) <= 3) {
				
				fridayCount++;
				shortListedDates.add(data);
			}
		}
		System.out.println("Total Thurdays : " + thursdayCount);
		System.out.println("Total Fridays :  " + fridayCount);
		System.out.println("Total Suitable days : " + shortListedDates.size());

	}
}
