package com.startup.parkinglocationfinder.restcontroller;

import java.util.List;

import javax.ws.rs.QueryParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

@RestController
public class PLFRestController {
	
	@Autowired
	private PFLService serviceObj;
	
	@RequestMapping(method=RequestMethod.GET,value="/customers/all")
	public List<UserData> getAllParkingLocations() {
		return serviceObj.getUserData();
	}
	
	@RequestMapping(method=RequestMethod.POST,value="/addcustomer")
	public void addCustomer(@RequestBody UserData user) throws Exception { 
		serviceObj.setUserData(user);
	}
	
	@RequestMapping(method=RequestMethod.GET,value="/customers")
	public List<UserData> getParkingLocationByCoordinates(@RequestParam(value = "address") String strAddress, @RequestParam(value = "radius") String radiusToSearch ) throws Exception{
		return serviceObj.getUserDataByAddress(strAddress,radiusToSearch);
		
	}
	
	@RequestMapping(method=RequestMethod.PUT,value="/reserve")
	public void reserve(@RequestBody JsonNode request) throws Exception { 
		Long custId = request.findValue("CustId").asLong();
		serviceObj.resetAvailability(custId, false);
	}

}
