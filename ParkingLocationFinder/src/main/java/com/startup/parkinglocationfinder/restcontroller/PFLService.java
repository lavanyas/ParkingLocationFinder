package com.startup.parkinglocationfinder.restcontroller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.startup.parkinglocationfinder.restcontroller.helper.DistanceCalculator;

@Service
public class PFLService {
	
	@Autowired
	private PFLRepository repo ;
	
	@Autowired
	private PLFRepoUserAddress addressRepo ;
	
	@Autowired
	private DistanceCalculator discal;
	
	@Autowired
	private Environment env;
	
	//private WebTarget webTarget;
	
	private Client client = ClientBuilder.newClient();

    private static final String REST_URI = "https://maps.googleapis.com/maps/api/geocode/json";
    private static final String API_KEY = "";
    private static final String OTP_URI = "";
	
//	private List<UserData> usersData = new ArrayList<>();
	
	public List<UserData> getUserData() {
		List<UserData> users = new ArrayList<>();
		Iterable<UserData> data = repo.findAll();
		data.forEach( x-> users.add(x) );
		return users;
	}
	
	public void addAddress (UserAddressData address, long custId) {
		address.setUser(repo.findById(custId).get());
		addressRepo.save(address);
	}

	/*
	public List<UserData> getUserDataByAddress(String strAddress, String radiusToSearch) throws Exception {
		
		//String distanceToFetch = null;
		List<UserData> closestLocations = new ArrayList<>();

		
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
	
	*/
	
	// How to handle failures here ????
	/*public void resetAvailability(Long CustId, boolean avl) {
		repo.findById(CustId).ifPresent( x -> {
			x.setAvailable(avl);
			repo.save(x);
		});
	}*/
	
	
	private Double StringtoDouble(String s)
	{
		return Double.parseDouble(s);
	}

	public UserData validateUser(String phoneNumber, String passCode) {
		return repo.findByphoneNumber(phoneNumber).stream()
						.filter(x -> x.getPassCode().equals(passCode))
						.findFirst().orElse(null);
	}

	public void createUser(UserData user) {
		repo.save(user);
		
	}

	public List<UserAddressData> getUserAddressData(Long custId) {
		return addressRepo.findByUserCustId(custId)
				.stream()
				.collect(Collectors.toList());
	}

	public UserAddressData normalizeAddress(String rawAddress) throws Exception {
		
		UserAddressData address = new UserAddressData();
		
		Response res = client.target(REST_URI)
		.queryParam("address", rawAddress)
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
			address.setLocationLatitude(location.get("lat").asText());
			address.setLocationLongitude(location.get("lng").asText());
			address.setAddress(node.findValue("formatted_address").asText());
			
		} else {
			throw new Exception("Not able to find coordinates");
		}
		
		return address;
	}
	
	public String requestOTP(String mobNumber) {
		
		String retVal = null;
		
		String requestURL = OTP_URI.concat(mobNumber+"/AUTOGEN");
		
		Response res = client.target(requestURL)
				.request(MediaType.APPLICATION_JSON)
				.get();
		
		if(res.getStatus() == HttpStatus.OK.value()) {
			JsonNode node = res.readEntity(JsonNode.class);
			retVal = node.findValue("Details").asText();
		}
		return retVal;
	}
	
	public Boolean verifyOTP(String sessionId, String otp) {
		Boolean otpMatched = false;
		String requestURL = OTP_URI.concat("VERIFY/"+sessionId+"/"+otp);
		Response res = client.target(requestURL)
				.request(MediaType.APPLICATION_JSON)
				.get();
		
		if(res.getStatus() == HttpStatus.OK.value()) {
			JsonNode node = res.readEntity(JsonNode.class);
			String details = node.findValue("Details").asText();
			if(details.equalsIgnoreCase("OTP Matched")) {
				otpMatched = true;
			}
		}
		
		return otpMatched;
	}

	public UserData getUserDataByCustId(Long custId) {
		// TODO Auto-generated method stub
		return repo.findById(custId).get();
	}



/*	private String formatString(String address) {
		
		System.out.println("input address" + address);
		address.replace('\0','+');
		address.replace(',', '+');
		
		System.out.println("Formatted address" + address);
		return address;
	}*/	
	
	
}
