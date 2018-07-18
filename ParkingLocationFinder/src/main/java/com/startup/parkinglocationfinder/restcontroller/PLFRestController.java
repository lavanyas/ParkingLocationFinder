package com.startup.parkinglocationfinder.restcontroller;

import java.util.List;

import javax.ws.rs.QueryParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
	public List<UserData> getParkingLocationByCoordinates(@RequestParam(value = "address") String strAddress) throws Exception{
		return serviceObj.getUserDataByAddress(strAddress);
		
	}

}
