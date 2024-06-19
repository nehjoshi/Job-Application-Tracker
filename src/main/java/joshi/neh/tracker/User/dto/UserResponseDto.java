package joshi.neh.tracker.User.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record UserResponseDto (
        UUID userId,
        String email,
        String firstName,
        String lastName,
        String accessToken
) {
}
