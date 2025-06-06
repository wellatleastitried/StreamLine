package com.streamline.frontend.terminal.navigation;

import com.streamline.frontend.terminal.navigation.rules.NavigationRule;
import com.streamline.frontend.terminal.navigation.rules.NavigationRuleRegistry;
import com.streamline.frontend.terminal.page.pages.AbstractBasePage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.tinylog.Logger;

public class NavigationContext {

    private final AbstractBasePage currentPage;
    private final AbstractBasePage previousPage;
    private final Map<String, Object> contextData;
    private final List<NavigationRule> applicableRules;

    public NavigationContext(AbstractBasePage currentPage, AbstractBasePage previousPage) {
        this.currentPage = currentPage;
        this.previousPage = previousPage;
        this.contextData = new HashMap<>();
        this.applicableRules = NavigationRuleRegistry.getRulesForPage(currentPage.getClass());
    }

    public <T> void setContextData(String key, T value) {
        contextData.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T getContextData(String key, Class<T> type) {
        Object value = contextData.get(key);
        return type.isInstance(value) ? (T) value : null;
    }

    public NavigationDestination getDestination() {
        Logger.debug("Getting navigation destination, rules count: {}", applicableRules.size());
        for (NavigationRule rule : applicableRules) {
            Logger.debug("Checking rule: {}", rule.getClass().getSimpleName());
            if (rule.appliesTo(this)) {
                NavigationDestination destination = rule.getDestination(this);
                Logger.debug("Rule applies, destination: {}", destination);
                return destination;
            }
        }
        NavigationDestination defaultDestination = getDefaultDestination();
        Logger.debug("Using default destination: {}", defaultDestination);
        return defaultDestination;
    }

    private NavigationDestination getDefaultDestination() {
        if (previousPage != null) {
            Logger.debug("Default destination based on previousPage: {}", previousPage.getClass().getSimpleName());
            return NavigationDestination.fromPageClass(previousPage.getClass());
        }
        Logger.debug("No previousPage, default destination is MAIN_MENU");
        return NavigationDestination.MAIN_MENU;
    }

    public AbstractBasePage getCurrentPage() {
        return currentPage;
    }

    public AbstractBasePage getPreviousPage() {
        return previousPage;
    }
}
