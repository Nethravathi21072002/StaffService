package com.flmhospitals.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

import org.springframework.http.ResponseEntity;

import com.flmhospitals.dto.RegisterStaffDto;


import com.flmhospitals.dto.StaffDetailsDto;
import com.flmhospitals.dao.StaffRepository;
import com.flmhospitals.model.Staff;

public interface StaffService {
	
	Staff getStaffByStaffId(String staffId);

	public ResponseEntity<List<StaffDetailsDto>> searchByStaffFirstNameOrLastName(String name);

	StaffDetailsDto registerStaffDeatils(RegisterStaffDto registerStaffDto);

}
