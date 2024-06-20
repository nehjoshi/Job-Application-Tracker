package joshi.neh.tracker.Application.dto;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Builder
public record ApplicationStatisticsResponseDto(
        int totalCount,
        int appliedCount,
        int offerCount,
        int rejectedCount,
        int stageCount,
        List<Map<String, Integer>> topLocations,
        List<Map<LocalDate, Integer>> fiveDayAppCount
) {
}
