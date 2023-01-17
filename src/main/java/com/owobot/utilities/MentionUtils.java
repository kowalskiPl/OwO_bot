package com.owobot.utilities;

import java.util.regex.Pattern;

public class MentionUtils {
    private static final Pattern userRegEX = Pattern.compile("<@(!|)+[0-9]{16,}+>", Pattern.CASE_INSENSITIVE);
}
