package com.startup.parkinglocationfinder.restcontroller;

import javax.transaction.Transactional;

import org.springframework.data.repository.CrudRepository;

@Transactional
public interface PFLRepository extends CrudRepository<UserData, Long> {

}
