package com.resqmitra.module.incident.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DateModel {

	LocalDate startDate;
	
	LocalDate endDate;
}
