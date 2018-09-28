package com.startup.parkinglocationfinder.restcontroller;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface PFLRepository extends CrudRepository<UserData, Long> {
	
	public List<UserData> findByphoneNumber(String PhoneNumber);

}
