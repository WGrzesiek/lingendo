package com.learnwords.common.dto;

/**
 * Minimalna reprezentacja słowa - tylko ID i słowo.
 * Używana gdy nie potrzeba tłumaczeń ani zdań.
 */
public record OnlyWordDto(String id, String word) {}