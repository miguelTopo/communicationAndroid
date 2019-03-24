package co.edu.udistrital.communicationapp.model;

import co.edu.udistrital.communicationapp.enums.LanguagePreferenceType;

public class UserLangPreferences {

    public LanguagePreferenceType languagePreferenceType;

    public LanPreference audioPref;

    public LanPreference textPref;

    public LanPreference videoPref;

    public LanPreference braillePref;

    public UserLangPreferences(LanguagePreferenceType languagePreferenceType) {
        this.languagePreferenceType = languagePreferenceType;
        if (languagePreferenceType.equals(LanguagePreferenceType.EQUAL_RESOURCE)) {
            this.audioPref = new LanPreference(false);
            this.textPref = new LanPreference(false);
            this.videoPref = new LanPreference(false);
            this.braillePref = new LanPreference(false);
        }
    }
}
