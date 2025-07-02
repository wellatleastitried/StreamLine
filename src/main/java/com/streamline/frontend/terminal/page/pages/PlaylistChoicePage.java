package com.streamline.frontend.terminal.page.pages;

import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.TextGUIThread;

import com.streamline.audio.Song;
import com.streamline.backend.Dispatcher;
import com.streamline.frontend.terminal.navigation.NavigationContext;
import com.streamline.frontend.terminal.navigation.NavigationDestination;

import java.util.Map;

public class PlaylistChoicePage extends AbstractDynamicPage {

    private final Song selectedSong;
    private final Map<Integer, Button> previousResultsForSearchPage;

    public final AbstractBasePage previousPage;

    public <T extends AbstractBasePage> PlaylistChoicePage(Dispatcher backend, TextGUIThread guiThread, Song selectedSong, T previousPage) {
        super(backend, guiThread);
        setWindowTitle(getText("window.playlistChoicePageTitle"));
        this.selectedSong = selectedSong;
        this.previousPage = previousPage;
        this.previousResultsForSearchPage = null;
    }

    public <T extends AbstractBasePage> PlaylistChoicePage(Dispatcher backend, TextGUIThread guiThread, Song selectedSong, T previousPage, Map<Integer, Button> previousResultsForSearchPage) {
        super(backend, guiThread);
        setWindowTitle(getText("window.playlistChoicePageTitle"));
        this.selectedSong = selectedSong;
        this.previousPage = previousPage;
        this.previousResultsForSearchPage = previousResultsForSearchPage;
    }

    @Override
    public BasicWindow createWindow() {
        fillPanelComponents();
        return window;
    }

    @Override
    protected void rebuildContent() {
        mainPanel.removeAllComponents();
        fillPanelComponents();
        if (window != null && mainPanel != null) {
            window.setComponent(mainPanel);
        }
    }

    private void fillPanelComponents() {
        addSpace();
        mainPanel.addComponent(componentFactory.createLabel(getText("label.playlistChoicePageTitle")));

        addSpace();

        mainPanel.addComponent(componentFactory.createButton(
                    getText("button.back"), 
                    () -> {
                        NavigationContext context = createNavigationContext();
                        context.setContextData("desiredPage", NavigationDestination.SONG_OPTIONS);
                        context.setContextData("selectedSong", selectedSong);
                        if (previousResultsForSearchPage != null) {
                            context.setContextData("previousResultsForSearchPage", previousResultsForSearchPage);
                        }
                        navigateTo(context);
                    },
                    componentFactory.getButtonWidth() / 3, 
                    componentFactory.getButtonHeight() / 2
                        ));

        window.setComponent(mainPanel);
    }

    @Override
    protected AbstractBasePage getPreviousPage() {
        return previousPage;
    }
}
