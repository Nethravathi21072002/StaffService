package com.flmhospitals.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.flmhospitals.dto.ForgotPasswordRequestDto;
import com.flmhospitals.dto.RegisterStaffDto;
import com.flmhospitals.dto.ResetPasswordRequest;
import com.flmhospitals.dto.StaffDetailsDto;
import com.flmhospitals.dto.VerifyOtpRequest;
import com.flmhospitals.model.Staff;
import com.flmhospitals.service.StaffService;

@RestController
@RequestMapping("/staff")
public class StaffController {
	
	private final StaffService staffService;

	public StaffController(StaffService staffService) {
		super();
		this.staffService = staffService;
	}
	
	@GetMapping()
	public ResponseEntity<List<StaffDetailsDto>> getAllStaffDetails(){
		List<StaffDetailsDto> allStaff = staffService.getAllStaff();
		return ResponseEntity.ok(allStaff);
		
	}
	
	@GetMapping("/{staffId}")
	public Staff getStaffByStaffId(@PathVariable String staffId) {
		return staffService.getStaffByStaffId(staffId);
	}


	@GetMapping("/searchByStaffName")
	public ResponseEntity<List<StaffDetailsDto>> searchByStaffFirstNameOrLastName(
			@RequestParam(name = "name", required = true) String name) {
		return staffService.searchByStaffFirstNameOrLastName(name);
	}
	
	@PostMapping("/register")
	public ResponseEntity<StaffDetailsDto> registerStaffDetails(@RequestBody RegisterStaffDto registerStaffDto){
		StaffDetailsDto registeredStaffDetailsDto = staffService.registerStaffDeatils(registerStaffDto);	
		return  ResponseEntity.status(HttpStatus.CREATED).body(registeredStaffDetailsDto);
		
	}
	
	@PutMapping("/update/{staffId}")
	public ResponseEntity<StaffDetailsDto> updateStaff(@PathVariable String staffId,@RequestBody RegisterStaffDto staffDetailsDto) {
		StaffDetailsDto updatedStaff = staffService.updateStaff(staffId, staffDetailsDto);
		return ResponseEntity.ok(updatedStaff);
		
	}
	
	@DeleteMapping("/resign/{staffId}")
	public ResponseEntity<String> deleteStaff(@PathVariable(name="staffId") String staffId) {
		
		 String deleteStaff = staffService.deleteStaff(staffId);
		 
		 return ResponseEntity.status(HttpStatus.OK).body(deleteStaff);

	}
	
	@GetMapping("/getDoctorName/{doctorId}")
	public String getDoctorName(@PathVariable(name="doctorId") String doctorId) {
		
		return staffService.getDoctorName(doctorId);
	}
	
	@PostMapping("/forgot-password")
	public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequestDto request) {
		staffService.sendOtp(request.getEmail());
	    return ResponseEntity.ok("OTP sent to registered email, If exists");
	}
	
	@PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(
            @RequestBody VerifyOtpRequest otpRequest) {

        staffService.verifyOtp(otpRequest);

        return ResponseEntity.ok("OTP verified successfully.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @RequestBody ResetPasswordRequest request) {

        staffService.resetPassword(request);

        return ResponseEntity.ok("Password reset successfully.");
    }

}
