package co.edu.udistrital.communicationapp;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import co.edu.udistrital.communicationapp.application.AppPreferences;
import co.edu.udistrital.communicationapp.enums.LanguagePreferenceType;
import co.edu.udistrital.communicationapp.enums.Role;
import co.edu.udistrital.communicationapp.model.LanPreference;
import co.edu.udistrital.communicationapp.model.User;
import co.edu.udistrital.communicationapp.model.UserLangPreferences;
import co.edu.udistrital.communicationapp.navigation.NavigateTo;
import co.edu.udistrital.communicationapp.rest.UserService;
import co.edu.udistrital.communicationapp.util.JSONUtil;
import co.edu.udistrital.communicationapp.util.ToastMessage;
import co.edu.udistrital.communicationapp.values.PreferenceKey;

public class LanPreferenceActivity extends AppCompatActivity {

    private static final String TAG = LanPreferenceActivity.class.getSimpleName();

    //Switch components
    private Switch audioEnable;
    private Switch textEnable;
    private Switch videoEnable;
    private Switch brailleEnable;

    //Spinner components
    private Spinner audioOrder;
    private Spinner textOrder;
    private Spinner videoOrder;
    private Spinner brailleOrder;
    private Spinner langPrefType;

    //Button components
    private MaterialButton cancelButton;
    private MaterialButton saveButton;

    //Layouts
    private LinearLayout lanInitialPanel;
    private LinearLayout lanCustomizedPanel;

    private Role userDefaultRole;
    //Services
    private UserService userService;

    private List<String> orderLabelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lan_preference);
        getUserData();
        initComponents();
        buildCustomSettings();
    }

    private void buildCustomSettings() {
        String json = AppPreferences.getString(PreferenceKey.LANG_USER_PREF);
        if (json == null || json.trim().isEmpty())
            return;
        UserLangPreferences preferences = (UserLangPreferences) JSONUtil.parseToObject(json, UserLangPreferences.class);
        if (preferences == null)
            return;
        if (preferences.languagePreferenceType.equals(LanguagePreferenceType.EQUAL_RESOURCE)) {
            langPrefType.setSelection(1);
            return;
        }
        orderLabelList = Arrays.asList(getString(R.string.core_first), getString(R.string.core_second), getString(R.string.core_third), getString(R.string.core_fourth));
        if (preferences.textPref.isEnable()) {
            textEnable.setChecked(true);
            textOrder.setSelection(preferences.textPref.getPriority() - 1);
        }
        if (preferences.audioPref.isEnable()) {
            audioEnable.setChecked(true);
            audioOrder.setSelection(preferences.audioPref.getPriority() - 1);
        }
        if (preferences.videoPref.isEnable()) {
            videoEnable.setChecked(true);
            videoOrder.setSelection(preferences.videoPref.getPriority() - 1);
        }
        if (preferences.braillePref.isEnable()) {
            brailleEnable.setChecked(true);
            brailleOrder.setSelection(preferences.braillePref.getPriority() - 1);
        }
    }

    private void getUserData() {
        String roleStr = AppPreferences.getString(PreferenceKey.DEFAULT_ROLE);
        userDefaultRole = roleStr.equalsIgnoreCase(Role.SINGLE.toString()) ? Role.SINGLE : Role.HOME;
    }

    private void initComponents() {
        Toolbar defaultToolbar = findViewById(R.id.lang_pref_toolbar);
        setSupportActionBar(defaultToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        //Switch Components
        audioEnable = findViewById(R.id.audio_enable);
        textEnable = findViewById(R.id.text_enable);
        videoEnable = findViewById(R.id.video_enable);
        brailleEnable = findViewById(R.id.braille_enable);

        //Spinner Components
        audioOrder = findViewById(R.id.audio_order);
        textOrder = findViewById(R.id.text_order);
        videoOrder = findViewById(R.id.video_order);
        brailleOrder = findViewById(R.id.braille_order);
        langPrefType = findViewById(R.id.lang_pref_type);

        //Paneles de configuración
        lanInitialPanel = findViewById(R.id.lang_pref_initial);
        lanCustomizedPanel = findViewById(R.id.lang_pref_customized);

        //Load init behaviors
        loadLangPrefTypeAdapter();
        loadOrderAdapter();

        cancelButton = findViewById(R.id.lang_pref_cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                NavigateTo.contactActivity();
            }
        });
        saveButton = findViewById(R.id.lang_pref_save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserLangPreferences();
            }
        });
        showBrailleOption();
    }

    private void showBrailleOption() {
        if (userDefaultRole.equals(Role.SINGLE))
            return;
        LinearLayout braillePanel = findViewById(R.id.lang_pref_braille_panel);
        braillePanel.setVisibility(View.VISIBLE);
    }

    private void loadLangPrefTypeAdapter() {
        ArrayAdapter<CharSequence> langPrefAdapter = ArrayAdapter.createFromResource(this, R.array.lang_pref_type_array, android.R.layout.simple_spinner_item);
        langPrefAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        langPrefType.setAdapter(langPrefAdapter);
        langPrefType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                boolean showCustomPanel = i <= 0;
                lanInitialPanel.setVisibility(showCustomPanel ? View.GONE : View.VISIBLE);
                lanCustomizedPanel.setVisibility(showCustomPanel ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void loadOrderAdapter() {
        //user 1 = normal;
        int numberOptions = userDefaultRole.equals(Role.SINGLE) ? 3 : 4;
        String[] orderValues = new String[numberOptions];
        orderLabelList = Arrays.asList(getString(R.string.core_first), getString(R.string.core_second), getString(R.string.core_third), getString(R.string.core_fourth));
        for (int i = 0; i < numberOptions; i++) {
            orderValues[i] = orderLabelList.get(i);
        }
        ArrayAdapter<String> orderAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, orderValues);
        audioOrder.setAdapter(orderAdapter);
        textOrder.setAdapter(orderAdapter);
        videoOrder.setAdapter(orderAdapter);
        if (userDefaultRole.equals(Role.HOME))
            brailleOrder.setAdapter(orderAdapter);
    }

    private void addErrorOptionSelectedToast() {
        ToastMessage.addWarn(R.string.lan_pref_option_out_same_order);
    }

    private boolean validSaveUserLangPreferences() {
        if (langPrefType.getSelectedItem().toString().equalsIgnoreCase("Recursos originales"))
            return true;

        if (!audioEnable.isChecked() && !textEnable.isChecked() && !videoEnable.isChecked() && !brailleEnable.isChecked()) {
            ToastMessage.addWarn(R.string.lan_pref_empty_option_out);
            return false;
        }
        List<String> optionSelected = new ArrayList<>(1);
        if (audioEnable.isChecked()) {
            if (optionSelected.contains(audioOrder.getSelectedItem().toString())) {
                addErrorOptionSelectedToast();
                return false;
            } else
                optionSelected.add(audioOrder.getSelectedItem().toString());
        }
        if (textEnable.isChecked()) {
            if (optionSelected.contains(textOrder.getSelectedItem().toString())) {
                addErrorOptionSelectedToast();
                return false;
            } else
                optionSelected.add(textOrder.getSelectedItem().toString());
        }
        if (videoEnable.isChecked()) {
            if (optionSelected.contains(videoOrder.getSelectedItem().toString())) {
                addErrorOptionSelectedToast();
                return false;
            } else
                optionSelected.add(videoOrder.getSelectedItem().toString());
        }
        if (brailleEnable.isChecked()) {
            if (optionSelected.contains(brailleOrder.getSelectedItem().toString())) {
                addErrorOptionSelectedToast();
                return false;
            } else
                optionSelected.add(brailleOrder.getSelectedItem().toString());
        }
        return true;
    }


    private int getIndexPriorityOption(final String option) {
        if (this.orderLabelList == null || this.orderLabelList.isEmpty())
            return -1;
        for (int i = 0; i < this.orderLabelList.size(); i++) {
            if (this.orderLabelList.get(i).equalsIgnoreCase(option))
                return (i + 1);
        }
        return -1;
    }

    private UserLangPreferences getLangUserPrefereces() {
        if (langPrefType.getSelectedItem().toString().equalsIgnoreCase("Recursos originales"))
            return new UserLangPreferences(LanguagePreferenceType.EQUAL_RESOURCE);
        UserLangPreferences preferences = new UserLangPreferences(LanguagePreferenceType.CUSTOMIZED);
        preferences.audioPref = new LanPreference(audioEnable.isChecked(), audioEnable.isChecked() ? getIndexPriorityOption(audioOrder.getSelectedItem().toString()) : -1);
        preferences.textPref = new LanPreference(textEnable.isChecked(), textEnable.isChecked() ? getIndexPriorityOption(textOrder.getSelectedItem().toString()) : -1);
        preferences.videoPref = new LanPreference(videoEnable.isChecked(), videoEnable.isChecked() ? getIndexPriorityOption(videoOrder.getSelectedItem().toString()) : -1);
        preferences.braillePref = new LanPreference(brailleEnable.isChecked(), brailleEnable.isChecked() ? getIndexPriorityOption(brailleOrder.getSelectedItem().toString()) : -1);
        return preferences;
    }

    private void saveUserLangPreferences() {
        if (!validSaveUserLangPreferences())
            return;
        User user = new User();
        user.id = AppPreferences.getString(PreferenceKey.APP_USER_ID);
        user.userLangPreferences = getLangUserPrefereces();
        getUserService().saveUserLangPreferencesAndContactRedirect(user);
    }

    private UserService getUserService() {
        if (this.userService == null)
            this.userService = new UserService();
        return this.userService;
    }

}
