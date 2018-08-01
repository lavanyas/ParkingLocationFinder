package com.startup.parkinglocationfinder.restcontroller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.startup.parkinglocationfinder.restcontroller.helper.DistanceCalculator;

@Service
public class PFLService {
	
	@Autowired
	private PFLRepository repo ;
	
	@Autowired
	private DistanceCalculator discal;
	
	@Autowired
	private Environment env;
	
	//private WebTarget webTarget;
	
	private Client client = ClientBuilder.newClient();

    private static final String REST_URI = "https://maps.googleapis.com/maps/api/geocode/json";
    private static final String API_KEY = "AIzaSyDF2444EpQLpd-ivFY8JenNu5dv2A8046U";
	
//	private List<UserData> usersData = new ArrayList<>();
	
	public List<UserData> getUserData() {
		List<UserData> users = new ArrayList<>();
		Iterable<UserData> data = repo.findAll();
		data.forEach( x-> users.add(x) );
		return users;
	}

	public void setUserData(UserData user) throws Exception {
		Response res = client.target(REST_URI)
		.queryParam("address", user.getAddress())
		.queryParam("key", API_KEY)
		.request(MediaType.APPLICATION_JSON)
		.get();
		
		
		System.out.println("Status:" + res.getStatus());
		
		if(res.getStatus() == HttpStatus.OK.value()) {		
			JsonNode node = res.readEntity(JsonNode.class);
			
			if(!node.get("status").asText().equals("OK"))
			{
				throw new Exception("Not able to find coordinates");
			}
			JsonNode location = node.findValue("geometry").findValue("location");
			user.setLocationLatitude(location.get("lat").asText());
			user.setLocationLongitude(location.get("lng").asText());
			user.setAddress(node.findValue("formatted_address").asText());
			user.setAvailable(true);
			repo.save(user);
		} else {
			throw new Exception("Not able to find coordinates");
		}
		
	}

	public List<UserData> getUserDataByAddress(String strAddress, String radiusToSearch) throws Exception {
		
		//String distanceToFetch = null;
		List<UserData> closestLocations = new ArrayList<>();
	/*	if (StringUtils.isEmpty(radiusToSearch)) {
			distanceToFetch = env.getProperty("distancetofetch", "1.0");
		} else {
			distanceToFetch = radiusToSearch;
		}*/
		
		String distanceToFetch = (StringUtils.isEmpty(radiusToSearch))
								 ? env.getProperty("distancetofetch", "1.0")
								 : radiusToSearch;
		
		Response res = client.target(REST_URI)
				.queryParam("address", strAddress)
				.queryParam("key", API_KEY)
				.request(MediaType.APPLICATION_JSON)
				.get();
		
		if(res.getStatus() == HttpStatus.OK.value()) {
			JsonNode node = res.readEntity(JsonNode.class);
			JsonNode location = node.findValue("geometry").findValue("location");
			String latitude = location.get("lat").asText();
			String longtitude = location.get("lng").asText();	
			
			
			Map<Long,Double> distances = new HashMap<>();
			getUserData().stream()
					     .filter(x -> x.isAvailable())
					     .forEach(x-> distances.put(x.getCustId(),discal.distance(StringtoDouble(latitude), StringtoDouble(longtitude)
					, StringtoDouble(x.getLocationLatitude()), StringtoDouble(x.getLocationLongitude()), "K")));
			
			/* This is Java 5
			 * for (Map.Entry<Long,Double> entry : distances.entrySet()) {
			    System.out.println(entry.getKey()+" : "+entry.getValue());
			    if(entry.getValue()<=1.0) // distance less than 1 km
			    {
			    	closestLocations.add(repo.findById(entry.getKey()).get());
			    }
			}*/
			
			//Java 8 using streams
			distances.entrySet().stream()
			.filter(x -> x.getValue()<= Double.parseDouble(distanceToFetch))
			.forEach( x -> {
				closestLocations.add(repo.findById(x.getKey()).get());
			});
			
		} else {
			throw new Exception("No data found");
		}
	
		return closestLocations;
	}
	
	// How to handle failures here ????
	public void resetAvailability(Long CustId, boolean avl) {
		repo.findById(CustId).ifPresent( x -> {
			x.setAvailable(avl);
			repo.save(x);
		});
	}
	
	private Double StringtoDouble(String s)
	{
		return Double.parseDouble(s);
	}

/*	private String formatString(String address) {
		
		System.out.println("input address" + address);
		address.replace('\0','+');
		address.replace(',', '+');
		
		System.out.println("Formatted address" + address);
		return address;
	}*/	

}
