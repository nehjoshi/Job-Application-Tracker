package joshi.neh.tracker.Application.dto;

import joshi.neh.tracker.Application.ApplicationStatus;
import lombok.Builder;

@Builder
public record ApplicationDto(
        String companyName,
        String positionTitle,
        String location,
        String compensation,
        ApplicationStatus status,
        String additionalInfo
) {
}
