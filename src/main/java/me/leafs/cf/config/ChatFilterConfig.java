package me.leafs.cf.config;

import lombok.Data;
import me.leafs.cf.filters.BaseFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

@Data
public class ChatFilterConfig {
    // advanced options that won't make an appearance in the GUI
    private int confirmDelay = 10;
    private final List<Integer> regexFlags = Arrays.asList(Pattern.CASE_INSENSITIVE, Pattern.MULTILINE);

    private final List<BaseFilter> filters = new ArrayList<>();

    public final BaseFilter filterById(UUID uuid) {
        return filters.stream()
                .filter(filter -> filter.getId().equals(uuid))
                .findAny().orElse(null);
    }
}
