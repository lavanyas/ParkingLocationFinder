package com.startup.parkinglocationfinder.restcontroller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
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
	
	public List<UserAddressData> getAllAddress() {
		List<UserAddressData> addrs = new ArrayList<>();
		Iterable<UserAddressData> data = addressRepo.findAll();
		data.forEach( x-> addrs.add(x) );
		return addrs;
	}
	
	public void addAddress (UserAddressData address, long custId) {
		//address.setUser(repo.findById(custId).get());
		address.setCustId(custId);
		addressRepo.save(address);
	}

	
	public List<UserAddressData> getUserDataByAddress(String strAddress, String radiusToSearch,Long requestedTime) throws Exception {
		
		//String distanceToFetch = null;
		List<UserAddressData> closestLocations = new ArrayList<>();

		
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
			//List<UserAddressData> addrs = getAllAddress();
			getAllAddress().stream()
					     .filter(x -> x.isConfirmed())
					     .forEach(x-> distances.put(x.getAddressId(),discal.distance(StringtoDouble(latitude), StringtoDouble(longtitude)
					, StringtoDouble(x.getLocationLatitude()), StringtoDouble(x.getLocationLongitude()), "K")));
			
		
			
			//Java 8 using streams
			distances.entrySet().stream()
			.filter(x -> x.getValue()<= Double.parseDouble(distanceToFetch))
			.forEach( x -> {
				UserAddressData addr = addressRepo.findById(x.getKey()).get();
				
				if (isParkingAvailableAtThisTime(addr.getAvailablity(),requestedTime))
				 {
					closestLocations.add(addr);
				 }
			});
			
			
			
		} else {
			throw new Exception("No data found");
		}
	
		return closestLocations;
	}
	
	
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
		return addressRepo.findBycustId(custId)
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

	public void resetAvailability(Long adderssId, Long timings) {	
		// TODO Auto-generated method stub
		addressRepo.findById(adderssId).ifPresent( x-> {
			x.setAvailablity(x.getAvailablity() 
					| timings);
			addressRepo.save(x);
		});
	}
	
	public void resetAddressConfirmation(Long adderssId, boolean isConfirmed) {	
		// TODO Auto-generated method stub
		addressRepo.findById(adderssId).ifPresent( x-> {
			x.setConfirmed(isConfirmed);
			addressRepo.save(x);
		});
	}

	private Boolean isParkingAvailableAtThisTime(Long inTime, Long requestedTime) {
		
		if ((inTime.longValue() & requestedTime.longValue()) == requestedTime.longValue()){
			return true;
		}
			return false;
	}


/*	private String formatString(String address) {
		
		System.out.println("input address" + address);
		address.replace('\0','+');
		address.replace(',', '+');
		
		System.out.println("Formatted address" + address);
		return address;
	}*/	
	
	public long convertTimeslotAvialable(int startHour, int endHour) {
		int i;
		long timings = 0;
		
		for(i=startHour;i<endHour;i++)
		{	
			timings = timings | (1 << i);
		}
		return timings;
	}
	
	public boolean isBitSet(long avialability, int bitNumber)
	{
		if ((avialability | (1 << bitNumber)) == avialability)
			    return true;
		else
			return false;
	}
	
	public long setBit (long avialability, int bitNumber)
	{
		return avialability | (1 << bitNumber);
		//return avialability;
	}
	
	public long clearBit (long avialability, int bitNumber)
	{
		return avialability & ~(1 << bitNumber);
		//return avialability;
	}
	
}
