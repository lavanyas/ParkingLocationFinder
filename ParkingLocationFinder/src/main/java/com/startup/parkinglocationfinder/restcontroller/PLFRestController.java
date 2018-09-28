package com.startup.parkinglocationfinder.restcontroller;

import java.io.IOException;
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
	
	/*@RequestMapping(method=RequestMethod.GET,value="/customers")
	public List<UserData> getParkingLocationByCoordinates(@RequestParam(value = "address") String strAddress, @RequestParam(value = "radius") String radiusToSearch ) throws Exception{
		return serviceObj.getUserDataByAddress(strAddress,radiusToSearch);
		
	}*/
	
	/*@RequestMapping(method=RequestMethod.PUT,value="/reserve")
	public void reserve(@RequestBody JsonNode request) throws Exception { 
		Long custId = request.findValue("CustId").asLong();
		serviceObj.resetAvailability(custId, false);
	}*/
	
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
