package com.restassured.stepdefinitions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.Assert;

import com.google.gson.Gson;
import com.restassured.pojo.WeatherData;
import com.restassured.pojo.ZoneData;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static com.restassured.base.ConstantUtil.*;
import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;


public class FeatureTest {
	
		private Map<String, String> postcodeMap = new HashMap<String, String>();
		private String firstPostalCode, secondPostalCode;
		private String firstSuburb, secondSuburb;
		private float lowTemp, highTemp, uv;
		
		public String getPostCode(String suburb) {
			
			postcodeMap.put(BONDI , BONDI_POST_CODE);
			postcodeMap.put(MANLY , MANLY_POST_CODE);
			return postcodeMap.get(suburb);
		}
		
		private Response getResponse(String firstPostalCode, String secondPostalCode) {
			
			return given().filter(new RequestLoggingFilter()).filter(new ResponseLoggingFilter()).queryParam(KEY_CONST, API_KEY).queryParam(POSTAL_CODE_CONST, firstPostalCode)
					.queryParam(POSTAL_CODE_CONST, secondPostalCode).queryParam(COUNTRY_CONST, AUS_COUNTRY).when()
					.get(API_URL).then().contentType(ContentType.JSON).extract().response();
		}

		@Given("I like to surf in any {int} beaches {string} of Sydney")
		public void i_like_to_surf_in_any_beaches_of_sydney(Integer int1, String string) {
			String[] suburbs =  string.split(",");
			firstSuburb = suburbs[0];
			secondSuburb = suburbs[1];
			firstPostalCode = getPostCode(firstSuburb);
			secondPostalCode = getPostCode(secondSuburb);
		}

		@Given("I only like to surf on any {int} days specifically {string} in next {int} Days")
		public void i_only_like_to_surf_on_any_days_specifically_in_next_days(Integer int1, String string, Integer int2) {
			
			Response response =  getResponse(firstPostalCode, secondPostalCode);
			Gson gson = new com.google.gson.Gson();
			WeatherData weatherData = gson.fromJson(response.asString(), WeatherData.class);
			List<ZoneData> zoneData = weatherData.getData();

			int thursdayCount = 0, fridayCount = 0;
			for (ZoneData data : zoneData) {
				String date = data.getValid_date();
				String[] dateArr = date.split("-");
				Calendar cal = Calendar.getInstance();
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
			Assert.assertNotEquals(thursdayCount, 0, "No Thursday in next 16 days");
			Assert.assertNotEquals(fridayCount, 0, "No Friday in next 16 days");
			
		}
		
		
		@When("I look up the the weather forecast for the next {int} days using POSTAL CODES")
		public void i_look_up_the_the_weather_forecast_for_the_next_days_using_postal_codes(Integer int1) {
		    // Write code here that turns the phrase above into concrete actions
			Response response = getResponse(firstPostalCode, secondPostalCode);
			
			response.then().assertThat().body(matchesJsonSchemaInClasspath("weatherSample.json"));
		}
		
		
		@Then("I check to if see the temperature is between {string}")
		public void i_check_to_if_see_the_temperature_is_between(String string) {
			String[] temperature = string.split(",");
			lowTemp = Float.valueOf(temperature[0]);
			highTemp = Float.valueOf(temperature[1]);
		}
		
		
		@Then("I check to see if UV index is <= {int}")
		public void i_check_to_see_if_uv_index_is(Integer int1) {
			uv = int1;
		}
		
		@Then("I Pick two spots based on suitable weather forecast for the day")
		public void i_pick_two_spots_based_on_suitable_weather_forecast_for_the_day() {
			Gson gson = new com.google.gson.Gson();
			Response response = getResponse(firstPostalCode, secondPostalCode);
			WeatherData weatherData = gson.fromJson(response.asString(), WeatherData.class);
			System.out.println(response.asString());
			System.out.println(weatherData.getData().size());
			List<ZoneData> zoneData = weatherData.getData();

			int thursdayCount = 0, fridayCount = 0;
			List<ZoneData> shortListedDates = new ArrayList<ZoneData>();
			for (ZoneData data : zoneData) {
				String date = data.getValid_date();
				String[] dateArr = date.split("-");
				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.YEAR, Integer.valueOf(dateArr[0]));
				cal.set(Calendar.MONTH, Integer.valueOf(dateArr[1]) - 1);
				cal.set(Calendar.DATE, Integer.valueOf(dateArr[2]));

				if (Calendar.THURSDAY == cal.get(Calendar.DAY_OF_WEEK) 
						&& Float.valueOf(data.getLow_temp()) > lowTemp
						&& Float.valueOf(data.getMax_temp()) < highTemp 
						&& Float.valueOf(data.getUv()) <= uv) {
					
					thursdayCount++;
					shortListedDates.add(data);
				}

				if (Calendar.FRIDAY == cal.get(Calendar.DAY_OF_WEEK) 
						&& Float.valueOf(data.getLow_temp()) > lowTemp
						&& Float.valueOf(data.getMax_temp()) < highTemp
						&& Float.valueOf(data.getUv()) <= uv) {
					
					fridayCount++;
					shortListedDates.add(data);
				}
			}
			System.out.println("Total Thurdays : " + thursdayCount);
			System.out.println("Total Fridays :  " + fridayCount);
			System.out.println("Total Suitable days : " + shortListedDates.size());
			boolean dateFound =  thursdayCount >= 1 && fridayCount >= 1 ? true : false;
			Assert.assertEquals(dateFound, true, "No suitable dates found");

		}
}
