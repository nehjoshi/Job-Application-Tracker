package joshi.neh.tracker.Application.dto;

import joshi.neh.tracker.Application.Application;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public record AllApplicationsResponseDto(
        int count,
        List<Application> applications
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
