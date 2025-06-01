package com.streamline.frontend.terminal.navigation.rules;

import com.streamline.frontend.terminal.navigation.NavigationContext;
import com.streamline.frontend.terminal.navigation.NavigationDestination;

public interface NavigationRule {

    boolean appliesTo(NavigationContext context);
    NavigationDestination getDestination(NavigationContext context);

    /* Higher priority rules are evaluated first */
    int getPriority();

}
