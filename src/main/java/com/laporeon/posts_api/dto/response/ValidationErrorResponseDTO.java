package com.laporeon.posts_api.dto.response;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public record ValidationErrorResponseDTO(
        int status,
        String type,
        String message,
        List<Map<String, String>> errors,
        Instant timestamp
) {
}
