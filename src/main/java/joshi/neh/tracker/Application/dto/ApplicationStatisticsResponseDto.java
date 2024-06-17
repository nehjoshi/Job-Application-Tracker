package joshi.neh.tracker.Application.dto;
import lombok.Builder;
import java.util.Map;

@Builder
public record ApplicationStatisticsResponseDto(
        int totalCount,
        int appliedCount,
        int offerCount,
        int rejectedCount,
        int stageCount,
        Map<String, Integer> topLocations
) {
}
