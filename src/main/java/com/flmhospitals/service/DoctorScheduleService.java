package com.flmhospitals.service;

import java.time.LocalDate;


public interface DoctorScheduleService {

	public boolean isDoctorAvailable(String staffId, LocalDate date);
}
