package joshi.neh.tracker.User.dto;

import java.util.UUID;

public record UserResponseDto (
        UUID userId,
        String email,
        String accessToken
) {
}
