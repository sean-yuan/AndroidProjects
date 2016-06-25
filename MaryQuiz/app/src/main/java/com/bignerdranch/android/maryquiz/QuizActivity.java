package com.bignerdranch.android.maryquiz;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class QuizActivity extends AppCompatActivity {
    private Button mTrueButton;
    private Button mFalseButton;
    private Button mNextButton;
    private TextView mQuestionTextView;
    private TextView mPointsTextView;
    private static final String TAG = "QuizActivity";
    private static final String KEY_INDEX = "index";

    private Question[] mQuestionBank = new Question[]{
            new Question(R.string.question_one, true),
            new Question(R.string.question_two, true),
            new Question(R.string.question_three, false),
            new Question(R.string.question_four, false),
            new Question(R.string.question_five, true),
            new Question(R.string.question_six, false),
    };

    private int mCurrentIndex = 0;
    private int mPoints = 0;

    private void updateQuestion(){
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
        int messageResId = 0;

        if (userPressedTrue == answerIsTrue){
            messageResId = R.string.correct_toast;
            updatePoints(true);
        }else{
            messageResId = R.string.incorrect_toast;
            updatePoints(false);
        }
        mCurrentIndex++;
        if (mCurrentIndex+1 == mQuestionBank.length){//game finished
            endGame();
        }
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show();
        updateQuestion();
    }

    private void endGame(){
        LinearLayout ll = (LinearLayout) findViewById(R.id.question_layout);
        ll.removeAllViews();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Log.d(TAG, "onCreate(Bundle) called");

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
    public void onSaveInstanceState(Bundle savedInstanceState){//when save save to state
        super.onSaveInstanceState(savedInstanceState);
        Log.i(TAG, "onSavedInstanceState");
        savedInstanceState.putInt(KEY_INDEX, mCurrentIndex);
    }

}
