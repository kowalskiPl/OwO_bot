package com.owobot.utilities;

import com.owobot.model.TrigramSearchResult;

import java.util.ArrayList;
import java.util.List;

public class TrigramStringSearch {

    private static final int DIRECT_MATCH_INT = Integer.MAX_VALUE;
    public List<String> prepareTrigrams(String text) {
        List<String> trigrams = new ArrayList<>();
        for (int i = 1; i < text.length() - 1; i++) {
            trigrams.add(text.substring(i - 1, i + 1));
        }
        return trigrams;
    }

    public TrigramSearchResult compareStrings(String query, String checkedText) {
        List<String> queryTrigrams = prepareTrigrams(query);
        List<String> checkedTextTrigrams = prepareTrigrams(checkedText);

        TrigramSearchResult result = new TrigramSearchResult(queryTrigrams.size(), 0, query, checkedText, false);
        if (query.equalsIgnoreCase(checkedText)){
            result.setMatchCount(DIRECT_MATCH_INT);
            result.setDirectMatch(true);
            return result;
        }

        for (String checkedTrigram : checkedTextTrigrams) {
            for (String queriedTrigram : queryTrigrams){
                if (checkedTrigram.equalsIgnoreCase(queriedTrigram)){
                    result.setMatchCount(result.getMatchCount() + 1);
                }
            }
        }
        return result;
    }
}
