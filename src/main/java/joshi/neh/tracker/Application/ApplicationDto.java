package joshi.neh.tracker.Application;

public record ApplicationDto(
        String companyName,
        String positionTitle,
        String location,
        String compensation,
        String status,
        String additionalInfo
) {
}
