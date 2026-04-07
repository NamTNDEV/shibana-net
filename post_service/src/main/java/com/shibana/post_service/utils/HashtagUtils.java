package com.shibana.post_service.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HashtagUtils {
    static public List<String> extractFromContent(String content) {
        if (content == null || content.isEmpty()) {
            return new ArrayList<>();
        }

        Set<String> hashtags = new HashSet<>();
        Pattern hashtagPattern = Pattern.compile("(?U)#\\w+");
        Matcher hashtagMatcher = hashtagPattern.matcher(content);

        while (hashtagMatcher.find()) {
            String hashtag = hashtagMatcher.group().substring(1).toLowerCase();
            hashtags.add(hashtag);
        }

        return new ArrayList<>(hashtags);
    }
}
