package com.streamline.frontend.terminal.navigation.commands;

import com.streamline.frontend.terminal.navigation.NavigationContext;
import com.streamline.frontend.terminal.navigation.NavigationDestination;
import com.streamline.frontend.terminal.page.pages.*;

public class NavigationCommandFactory {

    public static NavigationCommand createNavigationCommand(NavigationContext context) {
        NavigationDestination destination = context.getDestination();

        switch (destination) {
            case MAIN_MENU:
                return new ReturnToMainMenuCommand(context);
            case PLAYLISTS:
                return createPlaylistCommand(context);
            case PLAYLIST_CHOICE:
                return createPlaylistChoiceCommand(context);
            case SEARCH:
                return createSearchCommand(context);
            case LIKED_MUSIC:
                return createLikedMusicCommand(context);
            case DOWNLOADS:
                return createDownloadedMusicCommand(context);
            case RECENTLY_PLAYED:
                return createRecentlyPlayedCommand(context);
            case SETTINGS:
                return createSettingsCommand(context);
            case LANGUAGE:
                return createLanguageCommand(context);
            case HELP:
                return createHelpCommand(context);
            default:
                return createStandardNavigationCommand(destination, context);
        }
    }

    private static NavigationCommand createPlaylistCommand(NavigationContext context) {
        if (context.getContextData("requiresRebuild", Boolean.class) == Boolean.TRUE) {
            return new RebuildDynamicPagesCommand(NavigationDestination.PLAYLISTS);
        } else {
            return new NavigateToPageCommand<>(PlaylistPage.class);
        }
    }

    private static NavigationCommand createPlaylistChoiceCommand(NavigationContext context) {
        if (context.getContextData("requiresRebuild", Boolean.class) == Boolean.TRUE) {
            Object[] args = context.getContextData("rebuildArgs", Object[].class);
            return new RebuildAndNavigateCommand<>(PlaylistChoicePage.class, args);
        } else {
            return new NavigateToPageCommand<>(PlaylistChoicePage.class);
        }
    }

    private static NavigationCommand createStandardNavigationCommand(NavigationDestination destination, NavigationContext context) {
        if (destination.requiresRebuild()) {
            return new RebuildAndNavigateCommand<>(destination.getPageClass());
        } else {
            return new NavigateToPageCommand<>(destination.getPageClass());
        }
    }

    private static NavigationCommand createSearchCommand(NavigationContext context) {
        if (context.getContextData("requiresRebuild", Boolean.class) == Boolean.TRUE) {
            String rebuildType = context.getContextData("rebuildType", String.class);
            if ("cachedSearch".equals(rebuildType)) {
                return new RebuildCachedSearchCommand();
            } else {
                return new RebuildAndNavigateCommand<>(SearchPage.class);
            }
        } else {
            return new NavigateToPageCommand<>(SearchPage.class);
        }
    }

    private static NavigationCommand createLikedMusicCommand(NavigationContext context) {
        if (context.getContextData("requiresRebuild", Boolean.class) == Boolean.TRUE) {
            return new RebuildDynamicPagesCommand(NavigationDestination.LIKED_MUSIC);
        } else {
            return new NavigateToPageCommand<>(LikedMusicPage.class);
        }
    }

    private static NavigationCommand createDownloadedMusicCommand(NavigationContext context) {
        if (context.getContextData("requiresRebuild", Boolean.class) == Boolean.TRUE) {
            return new RebuildDynamicPagesCommand(NavigationDestination.DOWNLOADS);
        } else {
            return new NavigateToPageCommand<>(DownloadedMusicPage.class);
        }
    }

    private static NavigationCommand createRecentlyPlayedCommand(NavigationContext context) {
        if (context.getContextData("requiresRebuild", Boolean.class) == Boolean.TRUE) {
            return new RebuildDynamicPagesCommand(NavigationDestination.RECENTLY_PLAYED);
        } else {
            return new NavigateToPageCommand<>(RecentlyPlayedPage.class);
        }
    }

    private static NavigationCommand createSettingsCommand(NavigationContext context) {
        return new NavigateToPageCommand<>(SettingsPage.class);
    }

    private static NavigationCommand createLanguageCommand(NavigationContext context) {
        return new NavigateToPageCommand<>(LanguagePage.class);
    }

    private static NavigationCommand createHelpCommand(NavigationContext context) {
        return new NavigateToPageCommand<>(HelpPage.class);
    }
}
