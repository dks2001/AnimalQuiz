package com.dheerendrakumar.animalquiz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.preference.PreferenceManager;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Set;

public class MainActivity extends AppCompatActivity {

    public static final String GUESSES = "settings_numberOfGusses";
    public static final String ANIMALS_TYPE = "settingsAnimalType";
    public static final String QUIZ_BACKGROUND_COLOR = "settings_quiz_background_color";
    public static final String QUIZ_FONT = "settings_quiz_fonts";

    private boolean isSettingsChanged = false;

    static Typeface chunkfive;
    static Typeface fontlerybrown;
    static Typeface wonderbarDemo;

    BlankFragment blankFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        chunkfive = Typeface.createFromAsset(getAssets(),"fonts/Chunkfive.otf");
        fontlerybrown = Typeface.createFromAsset(getAssets(),"fonts/FontleroyBrown.ttf");
        wonderbarDemo = Typeface.createFromAsset(getAssets(),"fonts/Wonderbar Demo.otf");

        PreferenceManager.setDefaultValues(MainActivity.this,R.xml.quiz_preferences,false);

        PreferenceManager.getDefaultSharedPreferences(MainActivity.this).
                registerOnSharedPreferenceChangeListener(settingsChangeListener);
        blankFragment = (BlankFragment) getSupportFragmentManager().findFragmentById(R.id.animalQuizFragment);

        blankFragment.modifyAnimalGuessRow(PreferenceManager.getDefaultSharedPreferences(MainActivity.this));
        blankFragment.modifyTypeOfAnimal(PreferenceManager.getDefaultSharedPreferences(MainActivity.this));
        blankFragment.modifyFont(PreferenceManager.getDefaultSharedPreferences(MainActivity.this));
        blankFragment.modifyBackground(PreferenceManager.getDefaultSharedPreferences(MainActivity.this));
        blankFragment.resetAnimalQuiz();
        isSettingsChanged = false;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent intent = new Intent(MainActivity.this,SettingsActivity.class);
        startActivity(intent);
        return super.onOptionsItemSelected(item);
    }

    private SharedPreferences.OnSharedPreferenceChangeListener settingsChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

            isSettingsChanged = true;

            if(key.equals(GUESSES)) {
                blankFragment.modifyAnimalGuessRow(sharedPreferences);
                blankFragment.resetAnimalQuiz();
            } else if(key.equals(ANIMALS_TYPE)) {
                Set<String> animalType = sharedPreferences.getStringSet(ANIMALS_TYPE,null);
                if(animalType != null && animalType.size()>0) {
                    blankFragment.modifyTypeOfAnimal(sharedPreferences);
                    blankFragment.resetAnimalQuiz();
                } else {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    animalType.add(getString(R.string.default_animal_type));
                    editor.putStringSet(ANIMALS_TYPE,animalType);
                    editor.apply();

                    Toast.makeText(MainActivity.this, "Wild Animals is default", Toast.LENGTH_SHORT).show();

                }
            } else if(key.equals(QUIZ_FONT)) {
                    blankFragment.modifyFont(sharedPreferences);
                    blankFragment.resetAnimalQuiz();
            } else if(key.equals(QUIZ_BACKGROUND_COLOR)) {
                blankFragment.modifyBackground(sharedPreferences);
                blankFragment.resetAnimalQuiz();
            }

            Toast.makeText(MainActivity.this, "New Changes are applied", Toast.LENGTH_SHORT).show();

        }
    };
}