package joshi.neh.tracker.User.dto;

import lombok.Builder;

@Builder
public record UserDto(
        String firstName,
        String lastName,
        String email,
        String password,
        String targetPosition
) {
}
