package com.zenve.admin.dto;

import java.util.List;

public record DoctorsResponse(List<DoctorDto> doctors, CountsDto counts) {
}
