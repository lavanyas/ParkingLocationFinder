package com.startup.parkinglocationfinder.restcontroller;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.ws.rs.QueryParam;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class PLFRestController {
	
	@Autowired
	private PFLService serviceObj;
		
	@RequestMapping(method=RequestMethod.POST,value="/addcustomer")
	public void addCustomer(@RequestBody UserData user) throws Exception { 
		serviceObj.createUser(user);
	}
	
	@RequestMapping(method=RequestMethod.POST,value="/customer/{custId}/addAddress")
	public void addAddress(@RequestBody UserAddressData address, @PathVariable long custId) throws Exception { 
		serviceObj.addAddress(address, custId);
	}
	
	@RequestMapping(method=RequestMethod.PUT,value="/address/{addressId}/setavailability/")
	public void resetAvailability(@PathVariable Long addressId,@RequestParam(value = "startTime") String startTime,@RequestParam(value = "endTime") String endTime) throws Exception { 
		//long timings = Long.parseLong(endTime) - Long.parseLong(startTime);
		long timings;
		//Date dC = new Date();
		Date dS = new Date(Long.parseLong(startTime) * 1000);
		int startHour = dS.getHours();
		Date dE = new Date(Long.parseLong(endTime) * 1000);
		int endHour = dE.getHours();
		long baseDate;
		int nthday;
		System.out.println(startHour);
		System.out.println(endHour);
		
		baseDate = serviceObj.getBasedate(addressId);
		
		if(baseDate == 0)
		{
			nthday = 0;
			serviceObj.resetBasedate(addressId, (Long.parseLong(startTime) * 1000));
		}
		else
		{
			Date dB = new Date(baseDate);
			nthday = dS.getDay() - dB.getDay();
			if(nthday > 21)
			{
				nthday = 0;
				serviceObj.resetBasedate(addressId, (Long.parseLong(startTime) * 1000));
			}
		}
		
	   // if(nthday > 0)
	   // {
	    	timings = serviceObj.convertTimeslotAvialable(startHour, endHour);
	   // }
		serviceObj.resetAvailability(addressId, timings);
//		boolean test;
//		test = serviceObj.isBitSet(timings, 1);
//		test = serviceObj.isBitSet(timings, 4);
//		timings = serviceObj.clearBit(timings, 1);
//		test = serviceObj.isBitSet(timings, 1);
//		serviceObj.resetAvailability(addressId, timings);
	}
	
	@RequestMapping(method=RequestMethod.PUT,value="/address/{addressId}/isConfirmed/{confirmationStatus}")
	public void resetAddressConfirmation(@PathVariable Long adderssId,@PathVariable boolean confirmationStatus) throws Exception { 
		serviceObj.resetAddressConfirmation(adderssId, confirmationStatus);
	}
	
	
	@RequestMapping(method = RequestMethod.GET, value = "/search")
	public List<UserAddressData> getParkingLocationByCoordinates(@RequestParam(value = "address") String strAddress,
			@RequestParam(value = "radius") String radiusToSearch,
			@RequestParam(value = "reqTimeSlot") Long requestedTime) throws Exception {
		return serviceObj.getUserDataByAddress(strAddress, radiusToSearch, requestedTime);

	}
	
//	@RequestMapping(method=RequestMethod.PUT,value="/reserve")
//	public void reserve(@RequestBody JsonNode request) throws Exception { 
//		Long addressId = request.findValue("addressIdId").asLong();
//		serviceObj.reserve(addressId);
//	}
	
	@RequestMapping(method=RequestMethod.GET,value="/customers/all")
	public List<UserData> getAllParkingLocations() {
		return serviceObj.getUserData();
	}
	
	@RequestMapping(method=RequestMethod.GET,value="/validateuser")
	public UserData login(@RequestParam(value = "PhoneNumber") String phoneNumber, @RequestParam(value = "PassCode") String passCode ) throws Exception { 
		return serviceObj.validateUser(phoneNumber,passCode);
		
	}
	
	@RequestMapping(method=RequestMethod.GET,value="/normalizeaddress")
	public UserAddressData getNormalizedAddress(@RequestParam(value = "rawaddr") String rawAddress) throws Exception
	{
		return serviceObj.normalizeAddress(rawAddress);
	}
	
	
	@RequestMapping(method=RequestMethod.GET,value="/customers/{custId}/address/all")
	public List<UserAddressData> getAlladdressbyCustomerId(@PathVariable Long custId) {
		return serviceObj.getUserAddressData(custId);
	}
	
	@RequestMapping(method=RequestMethod.GET,value="/customers/{custId}/find")
	public UserData findByCustomerId(@PathVariable Long custId) {
		return serviceObj.getUserDataByCustId(custId);
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(method=RequestMethod.GET,value="/requestotp/{number}")
	public JsonNode sendOTP(@PathVariable(value="number") String mobNumber) throws IOException {
		
		String retVal = "xxd1238nddbF" ;//serviceObj.requestOTP(mobNumber);
		
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("sessionId", retVal);
		
		ObjectMapper objectMapper = new ObjectMapper();
	    return objectMapper.readTree(jsonObj.toString());
		 
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(method=RequestMethod.GET,value="/verifyotp/{session}/{otp}")
	public JsonNode ValidateOTP(@PathVariable(value="session") String sessionId, @PathVariable(value="otp") String otpNumber) throws  IOException{
		Boolean retVal = (Integer.parseInt(otpNumber) % 2 == 0)?true:false;//serviceObj.verifyOTP(sessionId,otpNumber);
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("success", retVal);
		
		ObjectMapper objectMapper = new ObjectMapper();
	    return objectMapper.readTree(jsonObj.toString());
		 
	}

}
