package com.streamline.frontend.terminal.pages;

import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.TextGUIThread;

import com.streamline.audio.Song;
import com.streamline.backend.Dispatcher;
import com.streamline.utilities.LanguagePeer;

import java.util.Map;

public class PlaylistChoicePage extends AbstractDynamicPage {

    private final Song selectedSong;
    private final Map<Integer, Button> previousResultsForSearchPage;

    private BasicWindow window;
    public final AbstractBasePage previousPage;

    public <T extends AbstractBasePage> PlaylistChoicePage(Dispatcher backend, TextGUIThread guiThread, Song selectedSong, T previousPage) {
        super(backend, guiThread);
        this.selectedSong = selectedSong;
        this.previousPage = previousPage;
        this.previousResultsForSearchPage = null;
    }

    public <T extends AbstractBasePage> PlaylistChoicePage(Dispatcher backend, TextGUIThread guiThread, Song selectedSong, T previousPage, Map<Integer, Button> previousResultsForSearchPage) {
        super(backend, guiThread);
        this.selectedSong = selectedSong;
        this.previousPage = previousPage;
        this.previousResultsForSearchPage = previousResultsForSearchPage;
    }

    @Override
    public BasicWindow createWindow() {
        window = createStandardWindow(LanguagePeer.getText("window.playlistChoicePageTitle"));

        Panel panel = componentFactory.createStandardPanel();

        panel.addComponent(componentFactory.createEmptySpace());
        panel.addComponent(componentFactory.createLabel(LanguagePeer.getText("label.playlistChoicePageTitle")));

        panel.addComponent(componentFactory.createEmptySpace());

        panel.addComponent(componentFactory.createButton(
                    LanguagePeer.getText("button.back"), 
                    () -> handlePageTransition(),
                    componentFactory.getButtonWidth() / 3, 
                    componentFactory.getButtonHeight() / 2
                        ));

        window.setComponent(panel);
        return window;
    }

    @Override
    public BasicWindow updateWindow() {
        return window;
    }

    private void handlePageTransition() {
        switch (previousPage.getClass().getSimpleName()) {
            case "SearchPage":
                windowManager.rebuildSearchPage(previousResultsForSearchPage);
                windowManager.transitionToCachedSearchPage();
                break;
            case "LikedMusicPage":
                windowManager.rebuildDynamicWindows();
                windowManager.transitionTo(windowManager.likedMusicPage);
                break;
            case "DownloadedMusicPage":
                windowManager.rebuildDynamicWindows();
                windowManager.transitionTo(windowManager.downloadedPage);
                break;
            case "PlaylistPage":
                windowManager.rebuildDynamicWindows();
                windowManager.transitionTo(windowManager.playlistPage);
                break;
            default:
                windowManager.rebuildAllWindows();
                windowManager.returnToMainMenu(window);
                break;
        }
    }
}
