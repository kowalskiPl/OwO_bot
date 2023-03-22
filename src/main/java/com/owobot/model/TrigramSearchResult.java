package com.owobot.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TrigramSearchResult {
    private int searchTrigramCount;
    private int checkedTrigramCount;
    private int matchCount;
    private String searchQuery;
    private String comparedString;
    private boolean directMatch;
}
