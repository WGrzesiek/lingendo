package com.learnwords.deckservice.dto.share;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO odpowiedzi z wynikiem operacji batch udostępniania.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchShareResponse {

    private List<String> success;
    private List<String> failed;
    private List<String> errors;
    private int totalProcessed;
    private int successCount;
    private int failedCount;
}
