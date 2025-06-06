package com.streamline.backend.jobs;

import com.streamline.utilities.LanguagePeer;
import com.streamline.utilities.internal.Config;

import org.tinylog.Logger;

public class LanguageJob extends AbstractStreamLineJob {

    private final String languageCode;

    public LanguageJob(Config config, String languageCode) {
        super(config);
        this.languageCode = languageCode;
    }

    @Override
    public void execute() {
        LanguagePeer.setLanguage(languageCode);
        Logger.info("[*] Language set to \"" + languageCode + "\"");
    }

}
