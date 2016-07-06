package com.lidago.stayfitabit.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.lidago.stayfitabit.Args;
import com.lidago.stayfitabit.R;
import com.lidago.stayfitabit.firebase.FirebaseClient;
import com.lidago.stayfitabit.firebase.Gender;
import com.lidago.stayfitabit.firebase.User;

/**
 * Created on 01.06.2016.
 */
public class SettingsFragment extends Fragment {

    private EditText mForenameEditText;
    private EditText mNameEditText;
    private ToggleButton mMaleToggleButton;
    private ToggleButton mFemaleToggleButton;
    private EditText mAgeEditText;
    private EditText mWeightEditText;
    private EditText mSizeEditText;
    private Button mSaveButton;

    public static SettingsFragment newInstance(String title) {
        Bundle args = new Bundle();
        args.putString(Args.TOOLBAR_TITLE, title);
        SettingsFragment fragment = new SettingsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle bundle) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
        setupUserInterface();
        setupToolbar();
        setListener();
        setSettings();
    }

    private void setupUserInterface() {
        mForenameEditText = (EditText) getView().findViewById(R.id.settings_forename_editText);
        mNameEditText = (EditText) getView().findViewById(R.id.settings_name_editText);
        mMaleToggleButton = (ToggleButton) getView().findViewById(R.id.settings_male_toogleButton);
        mFemaleToggleButton = (ToggleButton) getView().findViewById(R.id.settings_female_toogleButton);
        mAgeEditText = (EditText) getView().findViewById(R.id.settings_age_editText);
        mWeightEditText = (EditText) getView().findViewById(R.id.settings_weight_editText);
        mSizeEditText = (EditText) getView().findViewById(R.id.settings_size_editText);
        mSaveButton = (Button) getView().findViewById(R.id.settings_save_button);
    }

    private void setupToolbar() {
        String title = getArguments().getString(Args.TOOLBAR_TITLE);
        // We have a Toolbar in place so we don't need to care about the NPE warning
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title);
    }

    private void setListener() {
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSettings();
            }
        });
        mMaleToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleGender(Gender.MALE);
            }
        });
        mFemaleToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleGender(Gender.FEMALE);
            }
        });
    }

    private void setSettings() {
        User user = FirebaseClient.getInstance().getUser();
        mForenameEditText.setText(user.getForename());
        mNameEditText.setText(user.getName());
        toggleGender(user.getGender());
        if(user.getAge() != 0 && user.getWeight() != 0 && user.getSize() != 0) {
            mAgeEditText.setText(Integer.toString(user.getAge()));
            mWeightEditText.setText(Integer.toString(user.getWeight()));
            mSizeEditText.setText(Integer.toString(user.getSize()));
        }
    }

    private boolean validate() {
        boolean valid = true;

        String forename = mForenameEditText.getText().toString();
        String name = mNameEditText.getText().toString();
        String ageString = mAgeEditText.getText().toString();
        String weightString = mWeightEditText.getText().toString();
        String sizeString = mSizeEditText.getText().toString();
        int age, weight, size;
        if(ageString.isEmpty()) {
            age = -1;
        }
        else {
            age = Integer.parseInt(ageString);
        }
        if(weightString.isEmpty()) {
            weight = -1;
        }
        else {
            weight = Integer.parseInt(weightString);
        }
        if(sizeString.isEmpty()) {
            size = -1;
        }
        else {
            size = Integer.parseInt(sizeString);
        }

        if(forename.isEmpty()) {
            mForenameEditText.setError(getString(R.string.invalid_forename));
            valid = false;
        }
        else {
            mForenameEditText.setError(null);
        }
        if(name.isEmpty()) {
            mNameEditText.setError(getString(R.string.invalid_name));
            valid = false;
        }
        else {
            mNameEditText.setError(null);
        }
        if(age < 1 || age > 100) {
            mAgeEditText.setError(getString(R.string.invalid_age));
            valid = false;
        }
        else {
            mAgeEditText.setError(null);
        }
        if(weight < 1 || weight > 500) {
            mWeightEditText.setError(getString(R.string.invalid_weight));
            valid = false;
        }
        else {
            mWeightEditText.setError(null);
        }
        if(size < 1 || size > 300) {
            mSizeEditText.setError(getString(R.string.invalid_size));
            valid = false;
        }
        else {
            mSizeEditText.setError(null);
        }

        return valid;
    }

    private void saveSettings() {
        if (!validate()) {
            return;
        }
        User user = new User();
        user.setForename(mForenameEditText.getText().toString());
        user.setName(mNameEditText.getText().toString());
        user.setGender(getGender());
        user.setAge(Integer.parseInt(mAgeEditText.getText().toString()));
        user.setWeight(Integer.parseInt(mWeightEditText.getText().toString()));
        user.setSize(Integer.parseInt(mSizeEditText.getText().toString()));
        FirebaseClient.getInstance().saveToFirebase(user);
        Toast.makeText(getActivity(), getString(R.string.user_saved), Toast.LENGTH_SHORT).show();
    }

    private void toggleGender(Gender gender) {
        if(gender == Gender.MALE) {
            mFemaleToggleButton.setChecked(false);
            mMaleToggleButton.setChecked(true);
        }
        else {
            mMaleToggleButton.setChecked(false);
            mFemaleToggleButton.setChecked(true);
        }
    }

    private Gender getGender() {
        if (mMaleToggleButton.isChecked()) {
            return Gender.MALE;
        }
        else if (mFemaleToggleButton.isChecked()) {
            return Gender.FEMALE;
        }
        else {
            return null;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
