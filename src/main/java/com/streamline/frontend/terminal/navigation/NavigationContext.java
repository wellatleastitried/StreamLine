package com.streamline.frontend.terminal.navigation;

import com.streamline.frontend.terminal.navigation.rules.NavigationRule;
import com.streamline.frontend.terminal.navigation.rules.NavigationRuleRegistry;
import com.streamline.frontend.terminal.page.pages.AbstractBasePage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        for (NavigationRule rule : applicableRules) {
            if (rule.appliesTo(this)) {
                return rule.getDestination(this);
            }
        }
        return getDefaultDestination();
    }

    private NavigationDestination getDefaultDestination() {
        if (previousPage != null) {
            return NavigationDestination.fromPageClass(previousPage.getClass());
        }
        return NavigationDestination.MAIN_MENU;
    }

    public AbstractBasePage getCurrentPage() {
        return currentPage;
    }

    public AbstractBasePage getPreviousPage() {
        return previousPage;
    }
}
