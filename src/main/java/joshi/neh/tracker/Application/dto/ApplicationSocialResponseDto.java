package joshi.neh.tracker.Application.dto;

import joshi.neh.tracker.Application.Application;

public record ApplicationSocialResponseDto(
        String firstName,
        String lastName,
        Application application
) {
}
