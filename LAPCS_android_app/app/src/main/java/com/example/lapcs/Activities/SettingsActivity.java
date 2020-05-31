package com.example.lapcs.Activities;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.lapcs.Activities.fragments.dialog.UpdatePasswordDialogFragment;
import com.example.lapcs.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            Drawable colorPrimary = ContextCompat.getDrawable(getApplicationContext(), R.drawable.gradient2);
            actionBar.setBackgroundDrawable(colorPrimary);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            Preference button = findPreference(getString(R.string.update_password_key));
            button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Toast.makeText(getActivity(),"Update Password Clicked",Toast.LENGTH_LONG).show();
                    showUpdatePasswordDialog();
                    return true;
                }
            });

        }

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

            View view =  super.onCreateView(inflater, container, savedInstanceState);
            int colorPrimaryDark = ContextCompat.getColor(getContext(), R.color.colorGray);
            view.setBackgroundColor(colorPrimaryDark);

            return view;
        }

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }

        private void showUpdatePasswordDialog() {
            FragmentManager fm = getChildFragmentManager();
            UpdatePasswordDialogFragment updatePasswordDialogFragment = UpdatePasswordDialogFragment.newInstance("Update Password");
            updatePasswordDialogFragment.show(fm, "fragment_update_password");
        }


    }

}