package com.streamline.frontend.terminal.navigation.rules;

import com.streamline.frontend.terminal.page.pages.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class NavigationRuleRegistry {

    private static final Map<Class<?>, List<NavigationRule>> pageRules = new HashMap<>();

    static {
        registerDefaultRules();
    }

    public static void registerRule(Class<?> pageClass, NavigationRule rule) {
        pageRules.computeIfAbsent(pageClass, k -> new ArrayList<>()).add(rule);
        pageRules.get(pageClass).sort((r1, r2) -> Integer.compare(r2.getPriority(), r1.getPriority()));
    }

    public static List<NavigationRule> getRulesForPage(Class<?> pageClass) {
        return pageRules.getOrDefault(pageClass, Collections.emptyList());
    }

    // Register navigation rules for specific pages
    private static void registerDefaultRules() {
        registerRule(CreatePlaylistPage.class, new CreatePlaylistPageNavigationRule());
        registerRule(SongOptionPage.class, new SongOptionPageNavigationRule());
        registerRule(PlaylistChoicePage.class, new PlaylistChoicePageNavigationRule());
        registerRule(SongsFromPlaylistPage.class, new SongsFromPlaylistPageNavigationRule());
        registerRule(LikedMusicPage.class, new LikedMusicPageNavigationRule());
        registerRule(DownloadedMusicPage.class, new DownloadedMusicPageNavigationRule());
        registerRule(PlaylistPage.class, new PlaylistPageNavigationRule());
        registerRule(RecentlyPlayedPage.class, new RecentlyPlayedPageNavigationRule());
        registerRule(SearchPage.class, new SearchPageNavigationRule());
        registerRule(SettingsPage.class, new SettingsPageNavigationRule());
        registerRule(LanguagePage.class, new LanguagePageNavigationRule());
        registerRule(HelpPage.class, new HelpPageNavigationRule());
        registerRule(MainPage.class, new MainPageNavigationRule());
        // Add more rules as needed for other pages
    }
}
