package joshi.neh.tracker.User.dto;

public record UserDto(
        String firstName,
        String lastName,
        String email,
        String password,
        String targetPosition
) {
}
