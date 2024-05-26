package joshi.neh.tracker.Application.dto;

import joshi.neh.tracker.Application.Application;

import java.util.List;

public record AllApplicationsResponseDto(
        int count,
        List<Application> applications
) {
}
