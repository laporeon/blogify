package com.laporeon.blogify.dto.response;

import java.time.Instant;

public record ErrorResponseDTO(
        int status,
        String type,
        String message,
        Instant timestamp
) {
}