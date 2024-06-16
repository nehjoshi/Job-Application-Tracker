package joshi.neh.tracker.Application.dto;

import joshi.neh.tracker.Application.ApplicationStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ApplicationDto(
        String companyName,
        String positionTitle,
        String location,
        String compensation,
        ApplicationStatus status,
        String additionalInfo,
        String dateApplied
) {
}
