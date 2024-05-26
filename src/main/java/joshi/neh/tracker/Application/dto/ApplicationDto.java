package joshi.neh.tracker.Application.dto;

import joshi.neh.tracker.Application.ApplicationStatus;

public record ApplicationDto(
        String companyName,
        String positionTitle,
        String location,
        String compensation,
        ApplicationStatus status,
        String additionalInfo
) {
}
