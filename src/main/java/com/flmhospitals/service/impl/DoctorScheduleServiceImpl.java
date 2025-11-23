package com.flmhospitals.service.impl;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import com.flmhospitals.dao.DoctorScheduleRepository;
import com.flmhospitals.model.Staff;
import com.flmhospitals.service.DoctorScheduleService;
import com.flmhospitals.service.StaffService;

@Service
public class DoctorScheduleServiceImpl implements DoctorScheduleService {

	private final DoctorScheduleRepository doctorScheduleRepository;
	private final StaffService staffService;

	public DoctorScheduleServiceImpl(DoctorScheduleRepository doctorScheduleRepository, StaffService staffService) {
		this.doctorScheduleRepository = doctorScheduleRepository;
		this.staffService = staffService;
	}

	@Override
	public boolean isDoctorAvailable(String staffId, LocalDate date) {
		Staff staff = staffService.getStaffByStaffId(staffId);
		boolean isAvailable = !doctorScheduleRepository.existsByStaff_StaffIdAndUnavailableDate(staffId, date);
		return isAvailable;
	}

}
