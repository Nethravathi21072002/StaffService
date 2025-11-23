package com.flmhospitals.controller;

import java.time.LocalDate;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.flmhospitals.service.DoctorScheduleService;

@RestController
@RequestMapping("doctorSchedule")
public class DoctoreScheduleController {

	private final DoctorScheduleService doctorScheduleService;

	public DoctoreScheduleController(DoctorScheduleService doctorScheduleService) {
		this.doctorScheduleService = doctorScheduleService;
	}

	@GetMapping("/isDoctorAvailable")
	public ResponseEntity<Boolean> isDoctorAvailable(@RequestParam("staffId") String staffId,
			@RequestParam("date") String date) {
		LocalDate localDate = LocalDate.parse(date);
		return ResponseEntity.ok(doctorScheduleService.isDoctorAvailable(staffId, localDate));
	}

}
