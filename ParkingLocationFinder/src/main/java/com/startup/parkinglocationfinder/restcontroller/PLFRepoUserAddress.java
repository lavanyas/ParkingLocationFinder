package com.startup.parkinglocationfinder.restcontroller;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface PLFRepoUserAddress extends CrudRepository<UserAddressData, Long> {

	List<UserAddressData> findByUserCustId(Long custId);
}



