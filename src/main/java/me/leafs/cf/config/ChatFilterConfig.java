package me.leafs.cf.config;

import lombok.Data;
import me.leafs.cf.filters.BaseFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@Data
public class ChatFilterConfig {
    private int confirmDelay = 10;

    private final List<Integer> regexFlags = Arrays.asList(Pattern.CASE_INSENSITIVE, Pattern.MULTILINE);
    private final List<BaseFilter> filters = new ArrayList<>();
}
