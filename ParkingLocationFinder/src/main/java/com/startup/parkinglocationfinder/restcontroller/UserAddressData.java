package com.startup.parkinglocationfinder.restcontroller;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

@Entity
public class UserAddressData {
	
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "id_Sequence")
	@SequenceGenerator(name = "id_Sequence", sequenceName = "ORACLE_DB_SEQ_ID")
	private Long addressId;
	private Long custId;
	
	private String locationLatitude;
	private String locationLongitude;
	private String address;
	private boolean isConfirmed;
	private long baseDate;
	private long availablity;
	
	public long getAvailablity() {
		return availablity;
	}
	
	public long getBasedate() {
		return baseDate;
	}
	
	public void setBasedate(long baseDate) {
		this.baseDate = baseDate;
	}
	
	public void setAvailablity(long availablity) {
		this.availablity = availablity;
	}
	public Long getCustId() {
		return custId;
	}
	public void setCustId(Long custId) {
		this.custId = custId;
	}
	
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public Long getAddressId() {
		return addressId;
	}
	
	public String getLocationLatitude() {
		return locationLatitude;
	}
	public void setLocationLatitude(String locationLatitude) {
		this.locationLatitude = locationLatitude;
	}
	public String getLocationLongitude() {
		return locationLongitude;
	}
	public void setLocationLongitude(String locationLongitude) {
		this.locationLongitude = locationLongitude;
	}
	
	public boolean isConfirmed() {
		return isConfirmed;
	}
	public void setConfirmed(boolean isConfirmed) {
		this.isConfirmed= isConfirmed;
	}
	
}
