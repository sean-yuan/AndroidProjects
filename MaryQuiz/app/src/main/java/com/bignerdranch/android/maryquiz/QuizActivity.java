package com.bignerdranch.android.maryquiz;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class QuizActivity extends AppCompatActivity {
    private Button mTrueButton;
    private Button mFalseButton;
    //private Button mNextButton;
    private static TextView mQuestionTextView;
    public static TextView mPointsTextView;
    private Button mRestartButton;
    private Button mCheatButton;
    public static LinearLayout ll;
    private boolean mIsCheater;
    public static LinearLayout summaryLayout;
    private static final String TAG = "QuizActivity";
    private static final String KEY_INDEX = "index";
    private static final int REQUEST_CODE_CHEAT = 0;

    private static Question[] mQuestionBank = new Question[]{
            new Question(R.string.question_one, true),
            new Question(R.string.question_two, true),
            new Question(R.string.question_three, false),
            new Question(R.string.question_four, false),
            new Question(R.string.question_five, true),
            new Question(R.string.question_six, true),
            new Question(R.string.question_seven, false),
            new Question(R.string.question_eight, false),
            new Question(R.string.question_nine, true),
            new Question(R.string.question_ten, false),
    };

    public void setCurrentIndex(int currentIndex) {
        mCurrentIndex = currentIndex;
    }

    public void setPoints(int points) {
        mPoints = points;
    }

    public static int mCurrentIndex = 0;
    public static int mPoints = 0;

    public static void updateQuestion(){
        //Log.d(TAG, "Updating question text for question #" + mCurrentIndex, new Exception());
        int question = mQuestionBank[mCurrentIndex].getTextResId();//access text id
        mQuestionTextView.setText(question);//set widget to current text id
    }

    private void updatePoints(Boolean answerCorrect){
        if (answerCorrect){
            mPoints++;
        }
        else{
            mPoints--;
        }
        mPointsTextView.setText("Points: " + Integer.toString(mPoints));
    }

    private void checkAnswer(boolean userPressedTrue){
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
        //int messageResId = 0;
        if(mIsCheater){
            //messageResId = R.string.judgment_toast;
        }else{
            if (userPressedTrue == answerIsTrue){
               // messageResId = R.string.correct_toast;
                updatePoints(true);
            }else{
                //messageResId = R.string.incorrect_toast;
                updatePoints(false);
            }
            mCurrentIndex++;
            if (mCurrentIndex == mQuestionBank.length){//game finished
                endGame();
            } else{
                mIsCheater = false;
                updateQuestion();
            }
        }
        //Toast.makeText(this, messageResId, Toast.LENGTH_LONG).show();
    }

    private void endGame(){
        ll = (LinearLayout) findViewById(R.id.question_layout);
        ll.setVisibility(View.GONE);
        summaryLayout= (LinearLayout) findViewById(R.id.summary_layout);// change id here
        summaryLayout.setVisibility(View.VISIBLE);
        TextView mFinalPointsTextView;
        mFinalPointsTextView = (TextView) findViewById(R.id.final_points_text_view);
        String finalPoints = Integer.toString(mPoints);
        if(mPoints <= 6){
            mFinalPointsTextView.setText("Wow seriously? " + finalPoints + " points scored...");
        }
        else if (mPoints == 1){
            mFinalPointsTextView.setText("Wow seriously? " + finalPoints + " point scored...");
        }
        else{
            mFinalPointsTextView.setText("Congratulations! " + finalPoints + " points scored!");
        }
        mRestartButton = (Button) findViewById(R.id.restart_button);
        mRestartButton.setOnClickListener(new RestartOnClickListener(ll,summaryLayout));
    }

    public class RestartOnClickListener implements View.OnClickListener {
        LinearLayout lQuestion;
        LinearLayout lSummary;
        public RestartOnClickListener(LinearLayout lQuestion, LinearLayout lSummary){
            this.lQuestion = lQuestion;
            this.lSummary = lSummary;
        }
        @Override
        public void onClick(View v) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            RestartDialogFragment restartDialog = new RestartDialogFragment();
            restartDialog.show(ft, "dialog");
        }
    }


    public void newGame(){
        LinearLayout ll = (LinearLayout) findViewById(R.id.question_layout);
        LinearLayout summaryLayout= (LinearLayout) findViewById(R.id.summary_layout);// change id here
        summaryLayout.setVisibility(View.GONE);
        mPoints = 0;
        mCurrentIndex = 0;
        ll.setVisibility(View.VISIBLE);
        mPointsTextView.setText("Points: " + Integer.toString(mPoints));
        updateQuestion();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_quiz);

        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);//access textview widget
        /*mQuestionTextView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                mCurrentIndex = (mCurrentIndex+1)%mQuestionBank.length;
                updateQuestion();
            }
        });*/
        mPointsTextView = (TextView) findViewById(R.id.points_text_view);
        mPointsTextView.setText("Points: "+ Integer.toString(mPoints));

        //reference the inflated widget and cast as button
        mTrueButton = (Button) findViewById(R.id.true_button);
        mTrueButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                checkAnswer(true);
            }
        });
        mFalseButton = (Button) findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                checkAnswer(false);
            }
        });

        mCheatButton = (Button) findViewById(R.id.cheat_button);
        mCheatButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
                Intent i = CheatActivity.newIntent(QuizActivity.this, answerIsTrue);
                startActivityForResult(i, REQUEST_CODE_CHEAT);
            }
        });

        /*mNextButton = (Button) findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener(){
           @Override
            public void onClick(View v){
               mCurrentIndex = (mCurrentIndex+1)%mQuestionBank.length;
               updateQuestion();
           }
        });*/
        if(savedInstanceState != null){
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);//check if saved state exists
        }

        updateQuestion();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode != Activity.RESULT_OK){
            return;
        }
        if(requestCode == REQUEST_CODE_CHEAT){
            if(data == null){
                return;
            }
            mIsCheater = CheatActivity.wasAnserShown(data);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){//when save save to state
        super.onSaveInstanceState(savedInstanceState);
        Log.i(TAG, "onSavedInstanceState");
        savedInstanceState.putInt(KEY_INDEX, mCurrentIndex);
    }

}
