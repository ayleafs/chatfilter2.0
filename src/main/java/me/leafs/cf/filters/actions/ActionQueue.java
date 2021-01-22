package me.leafs.cf.filters.actions;

import lombok.Getter;
import lombok.Setter;
import me.leafs.cf.ChatFilter;
import me.leafs.cf.filters.BaseFilter;

import java.util.AbstractMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;

@Getter @Setter
public class ActionQueue {
    private final Map<BaseFilter, Matcher> filterQueue = new LinkedHashMap<>();

    private Map.Entry<BaseFilter, Matcher> filter = null;
    private long timerRun = 0L;

    public void queueAction(BaseFilter filter, Matcher context) {
        // don't allow to add if can't be run as an action
        if (filter.getAction() == null) {
            return;
        }

        // run the action immediately if it has the confirm flag off
        if (!filter.getAction().isConfirm()) {
            filter.getAction().performAction(context);
            return;
        }

        // put the filter in with the message context
        filterQueue.put(filter, context);
    }

    public Map.Entry<BaseFilter, Long> runNextFilter() {
        // if the filter is null then setup the next one
        if (filter == null) {
            // ew a nested if statement
            if (filterQueue.size() <= 0) {
                return new AbstractMap.SimpleEntry<>(null, 0L);
            }

            Iterator<Map.Entry<BaseFilter, Matcher>> iterator = filterQueue.entrySet().iterator();

            // update the linked variables
            filter = iterator.next();
            timerRun = System.currentTimeMillis() + (ChatFilter.instance.getConfig().getConfirmDelay() * 1000L);

            // remove from the listed queue
            iterator.remove();
        }

        // run the next filter action if the time left is <= 0
        if (timerRun - System.currentTimeMillis() <= 0L) {
            // run the action using the correct context
            filter.getKey().getAction().performAction(filter.getValue());

            // nullify the filter
            filter = null;
            timerRun = 0L;
        }

        return new AbstractMap.SimpleEntry<>(filter == null ? null : filter.getKey(), timerRun);
    }
}
