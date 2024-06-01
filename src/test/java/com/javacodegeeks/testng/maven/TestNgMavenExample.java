package com.javacodegeeks.testng.maven;

import static org.testng.Assert.*;
import java.util.*;
import org.json.*;
import org.testng.annotations.*;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class TestNgMavenExample {
	String baseURI = "http://jsonplaceholder.typicode.com";
	ArrayList<Integer> fanCodeCityUserIds = new ArrayList<Integer>();
	RequestSpecification request;
	
	@BeforeClass
	public void setup() {
		RestAssured.baseURI = baseURI;
		request = RestAssured.given();
	}
	
	@Test
	public void getFanCodeCityUsersWithMostTodosTasksCompleted() {
		fanCodeCityUserIds = getFancodeCityUsers(getUsers());
		getCityUsersWithMostTasksCompleted(fanCodeCityUserIds,getUsersTodos());
		
	}
	
	//Get All Users
	public Response getUsers() {
		String users = "/users";
		Response getUsersResponse = request.get(users);
		assertEquals(getUsersResponse.getStatusCode(),200);
		return getUsersResponse;
	}
	
	//Get All Users Todos
	public Response getUsersTodos() {
		String todos = "/todos";
		Response getUsersTodos = request.get(todos);
		assertEquals(getUsersTodos.getStatusCode(),200);
		return getUsersTodos;
	}

	//Get All FanCode City Users
	public ArrayList<Integer> getFancodeCityUsers(Response response) {
		ArrayList<Integer> userIds = new ArrayList<Integer>();
		String jsonData = response.getBody().asString();
		JSONArray jsonArray = new JSONArray(jsonData);
		for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject userObject = jsonArray.getJSONObject(i);
            JSONObject addressObject = userObject.getJSONObject("address");
            JSONObject geoObject = addressObject.getJSONObject("geo");

            double lat = geoObject.getDouble("lat");
            double lng = geoObject.getDouble("lng");

            if (lat >= -40 && lat <= 5 && lng >= 5 && lng <= 100) {
                int id = userObject.getInt("id");
                userIds.add(id);
            }
        }
		System.out.println("FanCodeCityUserIds : "+userIds);
		return userIds;
	}
	
	//Get All Users whose Tasks Completion is >50%
	public void getCityUsersWithMostTasksCompleted(ArrayList<Integer>userIds, Response usersTodos) {
		String jsonData = usersTodos.getBody().asString();
		JSONArray jsonArray = new JSONArray(jsonData);
		
		Map<Integer, Integer> userTaskCount = new HashMap<>();
        Map<Integer, Integer> userCompletedTaskCount = new HashMap<>();

        // Counting total and completed tasks for each user
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject task = jsonArray.getJSONObject(i);
            int userId = task.getInt("userId");
            if(userIds.contains(userId)) {
            	boolean completed = task.getBoolean("completed");

                userTaskCount.put(userId, userTaskCount.getOrDefault(userId, 0) + 1);

                if (completed) {
                    userCompletedTaskCount.put(userId, userCompletedTaskCount.getOrDefault(userId, 0) + 1);
                }
            }
        }
        // Calculating completion percentage and checking if it's greater than 50%
        for (Map.Entry<Integer, Integer> entry : userTaskCount.entrySet()) {
            int userId = entry.getKey();
            double completionPercentage = (double) userCompletedTaskCount.getOrDefault(userId, 0) / entry.getValue() * 100;

            if (completionPercentage > 50) {
                System.out.println("User " + userId + " has a completed task percentage greater than 50%.");
            }
        }
	}
}
