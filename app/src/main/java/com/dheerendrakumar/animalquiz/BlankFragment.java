package com.dheerendrakumar.animalquiz;

import android.animation.Animator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.InputStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class BlankFragment extends Fragment {


    private static final int NUMBER_OF_ANIMALS_INCLUDED_IN_QUIZ = 10;
    private List<String> allAnimalsNameList;
    private List<String> animalsNameQuizList;
    private Set<String> animalsTypeInQuiz;
    private String correctAnimalAnswer;
    private int numberOfAllGuesses;
    private int numberOfRightAnswers;
    private int numberOfAnimalGuessRows;
    private SecureRandom secureRandom;
    private Handler handler;
    private Animation wrongAnswerAnimation;

    private LinearLayout animalQuizLinearLayout;
    private TextView txtQuestionNumber;
    private ImageView imgAnimal;
    private LinearLayout[] rowsOfGuessButtonInAnimalQuiz;
    private TextView txtAnswer;

    
    public BlankFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_blank,container,false);

        allAnimalsNameList = new ArrayList<>();
        animalsNameQuizList = new ArrayList<>();
        secureRandom = new SecureRandom();
        handler = new Handler();

        wrongAnswerAnimation = AnimationUtils.loadAnimation(getActivity(),R.anim.wrong_answer_animation);
        wrongAnswerAnimation.setRepeatCount(2);

        animalQuizLinearLayout = view.findViewById(R.id.animalQuizLinearLayout);
        txtQuestionNumber = view.findViewById(R.id.questonNumberTextView);
        imgAnimal = view.findViewById(R.id.imgAnimal);
        rowsOfGuessButtonInAnimalQuiz = new LinearLayout[3];
        rowsOfGuessButtonInAnimalQuiz[0] = view.findViewById(R.id.firstRowLinearLayout);
        rowsOfGuessButtonInAnimalQuiz[1] = view.findViewById(R.id.secondRowLinearLayout);
        rowsOfGuessButtonInAnimalQuiz[2] = view.findViewById(R.id.thirdRowLinearLayout);
        txtAnswer = view.findViewById(R.id.txtAnswer);

        for(LinearLayout row : rowsOfGuessButtonInAnimalQuiz) {

            for(int i=0;i<row.getChildCount();i++) {
                Button btn = (Button) row.getChildAt(i);
                btn.setOnClickListener(btnGuessListener);
                btn.setTextSize(24);

            }
        }

        txtQuestionNumber.setText(getString(R.string.question_text,1,NUMBER_OF_ANIMALS_INCLUDED_IN_QUIZ));
        return view;
    }

    private View.OnClickListener btnGuessListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            Button btnGuess = ((Button) view);
            String guessValue = btnGuess.getText().toString();
            String answerValue = getTheExactAnialName(correctAnimalAnswer);
            ++numberOfAllGuesses;

            if(guessValue.equals(answerValue)) {

                ++numberOfRightAnswers;

                txtAnswer.setText(answerValue+"!"+" RIGHT");

                disableAllQuizButtons();

                if(numberOfRightAnswers==NUMBER_OF_ANIMALS_INCLUDED_IN_QUIZ) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setCancelable(false);


                    builder.setMessage(getString(R.string.results_string_vaue,numberOfAllGuesses,(1000/(double)numberOfAllGuesses)));
                    builder.setPositiveButton(R.string.reset_animal_quiz, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            resetAnimalQuiz();
                        }
                    });

                    builder.show();

                } else {

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            animateAnimalQuiz(true);
                        }
                    },1000);
                }


            } else {

                imgAnimal.setAnimation(wrongAnswerAnimation);
                txtAnswer.setText(R.string.wrong_answer_message);
                btnGuess.setEnabled(false);
            }
        }
    };

    private String getTheExactAnialName(String animalName) {
        return animalName.substring(animalName.indexOf('-')+1).replace('_',' ');
    }

    private void disableAllQuizButtons() {
        for(int row=0;row<numberOfAnimalGuessRows;row++) {
            LinearLayout guessRowLinearLayout = rowsOfGuessButtonInAnimalQuiz[row];
            for(int bi = 0;bi<guessRowLinearLayout.getChildCount();bi++) {
                guessRowLinearLayout.getChildAt(bi).setEnabled(false);
            }
        }
    }

    public void resetAnimalQuiz() {
        AssetManager assets = getActivity().getAssets();
        allAnimalsNameList.clear();

        try {
            for (String animalType : animalsTypeInQuiz) {
                String[] animalImagePathsInQuiz = assets.list(animalType);
                for (String animalImagePathInQuiz : animalImagePathsInQuiz) {
                    allAnimalsNameList.add(animalImagePathInQuiz.replace(".png", ""));

                }
            }
        } catch(Exception e) {
            Log.e("AnimalQuiz","Error",e);
        }

        numberOfRightAnswers=0;
        numberOfAllGuesses=0;
        animalsNameQuizList.clear();

        int counter = 1;
        int numberOfAvailableAnimal = allAnimalsNameList.size();

        while(counter<=NUMBER_OF_ANIMALS_INCLUDED_IN_QUIZ) {
            int randomIndex = secureRandom.nextInt(numberOfAvailableAnimal);
            String animalImageName = allAnimalsNameList.get(randomIndex);

            if(!animalsNameQuizList.contains(animalImageName)) {
                animalsNameQuizList.add(animalImageName);
                ++counter;
            }
        }
        showNextAnimal();
    }

    private void animateAnimalQuiz(boolean animateOutAnimalImage) {
        if(numberOfRightAnswers==0) {
            return;
        }
        int xTopLeft = 0;
        int yTopLeft = 0;

        int xBottomRight = animalQuizLinearLayout.getLeft() + animalQuizLinearLayout.getRight();
        int yBottomRight = animalQuizLinearLayout.getTop() + animalQuizLinearLayout.getBottom();

        int radius = Math.max(animalQuizLinearLayout.getWidth(),animalQuizLinearLayout.getHeight());

        Animator animator;

        if(animateOutAnimalImage) {

            animator = ViewAnimationUtils.createCircularReveal(animalQuizLinearLayout,xBottomRight,yBottomRight,radius,0);

            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    showNextAnimal();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        } else {

            animator = ViewAnimationUtils.createCircularReveal(animalQuizLinearLayout,xTopLeft,yTopLeft,0,radius);

        }
        animator.setDuration(700);
        animator.start();
    }

    public void showNextAnimal() {

        String nextAnimalImageName = animalsNameQuizList.remove(0);
        correctAnimalAnswer = nextAnimalImageName;
        txtAnswer.setText("");

        txtQuestionNumber.setText(getString(R.string.question_text,(numberOfRightAnswers+1),NUMBER_OF_ANIMALS_INCLUDED_IN_QUIZ));
        String animalType = nextAnimalImageName.substring(0,nextAnimalImageName.indexOf("-"));

        AssetManager asset = getActivity().getAssets();

        try(InputStream stream = asset.open(animalType+"/"+nextAnimalImageName+".png")) {

            Drawable animalImage = Drawable.createFromStream(stream,nextAnimalImageName);
            imgAnimal.setImageDrawable(animalImage);
            animateAnimalQuiz(false);

        } catch (Exception e) {
            Log.e("AnimalQuiz","Error",e);
        }

        Collections.shuffle(allAnimalsNameList);
        int correctAnimalNameIndex = allAnimalsNameList.indexOf(correctAnimalAnswer);
        String correctAnimalName = allAnimalsNameList.remove(correctAnimalNameIndex);
        allAnimalsNameList.add(correctAnimalName);

        for(int row=0;row<numberOfAnimalGuessRows;row++) {
            for(int col = 0;col<rowsOfGuessButtonInAnimalQuiz[row].getChildCount();col++) {
                Button btnGuess = (Button) rowsOfGuessButtonInAnimalQuiz[row].getChildAt(col);
                btnGuess.setEnabled(true);

                String animalImageName = allAnimalsNameList.get((row*2)+col);
                btnGuess.setText(getTheExactAnialName(animalImageName));
            }
        }

        int row = secureRandom.nextInt(numberOfAnimalGuessRows);
        int col = secureRandom.nextInt(2);
        LinearLayout randomRow = rowsOfGuessButtonInAnimalQuiz[row];

        String animalImageName =  getTheExactAnialName(correctAnimalAnswer);
        ((Button) randomRow.getChildAt(col)).setText(animalImageName);

    }

    public void modifyAnimalGuessRow(SharedPreferences sharedPreferences) {

        final String NUMBER_OF_GUESS_OPTIONS = sharedPreferences.getString(MainActivity.GUESSES,null);
        numberOfAnimalGuessRows = Integer.parseInt(NUMBER_OF_GUESS_OPTIONS)/2;

        for(LinearLayout horizontalLinearLayout:rowsOfGuessButtonInAnimalQuiz) {

            horizontalLinearLayout.setVisibility(View.GONE);
        }

        for(int row = 0;row<numberOfAnimalGuessRows;row++) {
            rowsOfGuessButtonInAnimalQuiz[row].setVisibility(View.VISIBLE);
        }

    }

    public void modifyTypeOfAnimal(SharedPreferences sharedPreferences) {

        animalsTypeInQuiz = sharedPreferences.getStringSet(MainActivity.ANIMALS_TYPE,null);

    }

    public void modifyFont(SharedPreferences sharedPreferences) {

        String fontStringValue = sharedPreferences.getString(MainActivity.QUIZ_FONT,null);
        switch (fontStringValue) {

            case "Chunkfive.otf":
                for(LinearLayout row:rowsOfGuessButtonInAnimalQuiz) {
                    for(int i=0;i<row.getChildCount();i++) {
                        Button button = (Button) row.getChildAt(i);
                        button.setTypeface(MainActivity.chunkfive);
                    }
                }
                break;

            case "FontleroyBrown.ttf":
                for(LinearLayout row:rowsOfGuessButtonInAnimalQuiz) {
                    for(int i=0;i<row.getChildCount();i++) {
                        Button button = (Button) row.getChildAt(i);
                        button.setTypeface(MainActivity.fontlerybrown);
                    }
                }
                break;

            case "Wonderbar Demo.otf" :
                for(LinearLayout row:rowsOfGuessButtonInAnimalQuiz) {
                    for(int i=0;i<row.getChildCount();i++) {
                        Button button = (Button) row.getChildAt(i);
                        button.setTypeface(MainActivity.wonderbarDemo);
                    }
                }

        }
    }

    public void modifyBackground(SharedPreferences sharedPreferences) {

        String backgroundColor = sharedPreferences.getString(MainActivity.QUIZ_BACKGROUND_COLOR,null);

        switch (backgroundColor) {

            case "White":

                animalQuizLinearLayout.setBackgroundColor(Color.WHITE);
                txtAnswer.setTextColor(Color.BLACK);
                txtQuestionNumber.setTextColor(Color.BLACK);
                break;

            case "Black":
                animalQuizLinearLayout.setBackgroundColor(Color.BLACK);
                txtAnswer.setTextColor(Color.WHITE);
                txtQuestionNumber.setTextColor(Color.WHITE);
                break;

            case "Red":
                animalQuizLinearLayout.setBackgroundColor(Color.RED);
                txtAnswer.setTextColor(Color.WHITE);
                txtQuestionNumber.setTextColor(Color.WHITE);
                break;

            case "Yellow":
                animalQuizLinearLayout.setBackgroundColor(Color.YELLOW);
                txtAnswer.setTextColor(Color.BLACK);
                txtQuestionNumber.setTextColor(Color.BLACK);
                break;

            case "Green":
                animalQuizLinearLayout.setBackgroundColor(Color.GREEN);
                txtAnswer.setTextColor(Color.RED);
                txtQuestionNumber.setTextColor(Color.RED);
                break;

            case "Blue":
                animalQuizLinearLayout.setBackgroundColor(Color.BLUE);
                txtAnswer.setTextColor(Color.BLACK);
                txtQuestionNumber.setTextColor(Color.BLACK);
                break;
        }
    }
}