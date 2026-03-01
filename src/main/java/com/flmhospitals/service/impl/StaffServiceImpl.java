package com.flmhospitals.service.impl;



import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.flmhospitals.builder.StaffBuilder;
import com.flmhospitals.builder.StaffDtoBuilder;
import com.flmhospitals.dao.StaffDetailsRepository;
import com.flmhospitals.dao.StaffRepository;
import com.flmhospitals.dto.EmailRequestDto;
import com.flmhospitals.dto.RegisterStaffDto;
import com.flmhospitals.dto.ResetPasswordRequest;
import com.flmhospitals.dto.StaffDetailsDto;
import com.flmhospitals.dto.VerifyOtpRequest;
import com.flmhospitals.enums.StaffType;
import com.flmhospitals.exception.DoctorNotFoundException;
import com.flmhospitals.exception.InvalidOtpException;
import com.flmhospitals.exception.StaffNotFoundException;
import com.flmhospitals.feignclient.NotificationFeignClient;
import com.flmhospitals.model.Staff;
import com.flmhospitals.model.StaffDetails;
import com.flmhospitals.service.StaffService;

import jakarta.transaction.Transactional;

@Service
public class StaffServiceImpl implements StaffService {

	private final StaffRepository staffRepository;
	
	private final NotificationFeignClient notificationFeignClient;
	
	private final StaffDetailsRepository staffDetailsRepository;
	
	

	public StaffServiceImpl(StaffRepository staffRepository,NotificationFeignClient notificationFeignClient, StaffDetailsRepository staffDetailsRepository) {
		
		this.staffRepository = staffRepository;
		this.notificationFeignClient = notificationFeignClient;
		this.staffDetailsRepository = staffDetailsRepository;
		
	}

	@Override
	public Staff getStaffByStaffId(String staffId) {
		return staffRepository.findById(staffId)
				.orElseThrow(() -> new StaffNotFoundException("Staff with ID :" + staffId + " not found"));
	}

	@Override
	public ResponseEntity<List<StaffDetailsDto>> searchByStaffFirstNameOrLastName(String name) {

		List<Staff> staffs = staffRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(name,
				name);

		List<StaffDetailsDto> staffDetailsDtoList = new ArrayList<>();

		if (staffs.isEmpty()) {
			throw new StaffNotFoundException("No staff found with name : " + name);
		}

		for (Staff staff : staffs) {
			staffDetailsDtoList.add(StaffDtoBuilder.buildStaffDetailsDto(staff));
		}

		return ResponseEntity.ok(staffDetailsDtoList);
	}

	@Override
	public StaffDetailsDto registerStaffDeatils(RegisterStaffDto registerStaffDto) {

		Staff staff = StaffBuilder.buildStaffFromRegisterStaffDto(registerStaffDto);
		//boolean roleFlag = registerStaffDto.getStaffType().equals(StaffType.DOCTOR);
//		if (roleFlag) {
//			staff.setRole("Admin");
//			staff.setCanLogin(true);
//		} else {
//			staff.setRole("Non-Admin");
//		}
		staff.setEmployeeActive(true);

		Staff registerdStaff = staffRepository.save(staff);
		
		sendNotificationMail(registerdStaff);

		return StaffDtoBuilder.buildStaffDetailsDto(registerdStaff);
	}

	private void sendNotificationMail(Staff registerdStaff) {
		EmailRequestDto emailRequest = new EmailRequestDto();
        emailRequest.setTo(registerdStaff.getEmail());
        emailRequest.setSubject("Welcome to MedSync"); 
        emailRequest.setBody(
                "<h2>Welcome " + registerdStaff.getFirstName() + "</h2>"
                + "<p>Your account has been successfully created.</p>"
                + "<h4>Username : "+registerdStaff.getStaffId()+"</h4>"
                + "<h4>Password : "+registerdStaff.getStaffDetails().getPassword()+"</h4>"
        );
        notificationFeignClient.sendEmail(emailRequest);
	}


	@Override
	public StaffDetailsDto updateStaff(String staffId, RegisterStaffDto dto) {
		Staff existingStaff = staffRepository.findById(staffId)
				.orElseThrow(() -> new StaffNotFoundException("Staff ID: " + staffId + " not found"));

		Staff updatedStaff = StaffBuilder.updateStaffBuilder(dto, existingStaff);
		//updatedStaff.setStaffId(existingStaff.getStaffId());
//		updatedStaff.getStaffAddress().setStaffAddressId(existingStaff.getStaffAddress().getStaffAddressId());
//		updatedStaff.getStaffDetails().setStaffDetailsId(existingStaff.getStaffDetails().getStaffDetailsId());
		Staff savedStaff = staffRepository.save(updatedStaff);
		return StaffDtoBuilder.buildStaffDetailsDto(savedStaff);

	}

	@Override
	public String deleteStaff(String staffId) {

		Staff staff = staffRepository.findById(staffId)
				.orElseThrow(() -> new StaffNotFoundException("No staff Found with the Id :" + staffId));

		staff.setEmployeeActive(false);

		staffRepository.save(staff);

		return staff.getFirstName() + " " + staff.getLastName();

	}

	@Override
	public String getDoctorName(String doctorId) {
		
		Staff staff = staffRepository.findById(doctorId)
		.orElseThrow(()-> new DoctorNotFoundException("no doctor with the id: "+ doctorId));
		
		return staff.getFirstName()+" "+staff.getLastName();
	}

	@Override
	public List<StaffDetailsDto> getAllStaff() {
		List<StaffDetailsDto> staffDetailsList = staffRepository.findAll().stream().map(staff -> StaffDtoBuilder.buildStaffDetailsDto(staff)).toList();
		return staffDetailsList;
	}
	
	@Override
	public void sendOtp(String email) {

	    String normalizedEmail = email.trim().toLowerCase();

	    Optional<StaffDetails> optionalStaff = staffDetailsRepository.findByEmail(normalizedEmail);

	    if (optionalStaff.isEmpty()) {
	        return;
	    }

	    StaffDetails staffDetails = optionalStaff.get();

	    String otp = String.valueOf(new Random().nextInt(900000) + 100000);

	    staffDetails.setResetOtp(otp);
	    staffDetails.setOtpExpiryTime(LocalDateTime.now().plusMinutes(5));

	    staffDetailsRepository.save(staffDetails);

	    EmailRequestDto emailRequest = new EmailRequestDto();
	    emailRequest.setTo(email);
	    emailRequest.setSubject("Password Reset OTP - MedSync");
	    emailRequest.setBody(
	            "<h3>Your OTP for password reset is: <b>" + otp + "</b></h3>"
	            + "<p>This OTP is valid for 5 minutes.</p>"
	    );

	    notificationFeignClient.sendEmail(emailRequest);
	    return;
	}
	
	@Override
	public void verifyOtp(VerifyOtpRequest request) {

        StaffDetails staff = staffDetailsRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidOtpException("Invalid email or OTP"));

        if (staff.getResetOtp() == null ||
            !staff.getResetOtp().equals(request.getOtp()) ||
            staff.getOtpExpiryTime().isBefore(LocalDateTime.now())) {

            throw new InvalidOtpException("Invalid or expired OTP");
        }
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {

        StaffDetails staff = staffDetailsRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "User not found"
                ));

        staff.setPassword(request.getNewPassword());
        staff.setResetOtp(null);
        staff.setOtpExpiryTime(null);

        staffDetailsRepository.save(staff);
    }


}
