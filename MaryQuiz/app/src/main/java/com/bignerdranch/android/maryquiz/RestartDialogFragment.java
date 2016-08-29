package com.bignerdranch.android.maryquiz;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RestartDialogFragment extends DialogFragment {
    public RestartDialogFragment(){

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.confirm_message)
                .setPositiveButton(R.string.confirm, new ConfirmOnClickListener())
                .setNegativeButton(R.string.cancel, new CancelOnClickListener()) ;
        return builder.create(); // Create the AlertDialog object and return it
    }
    private class ConfirmOnClickListener extends AppCompatActivity implements DialogInterface.OnClickListener {

        public ConfirmOnClickListener(){

        }
        @Override
        public void onClick(DialogInterface dialog, int which) {
            QuizActivity.ll.setVisibility(View.VISIBLE);
            QuizActivity.summaryLayout.setVisibility(View.GONE);
            QuizActivity.mPoints = 0;
            QuizActivity.mCurrentIndex = 0;
            QuizActivity.mPointsTextView.setText("Points: " + Integer.toString(0));
            QuizActivity.updateQuestion();
            hideDiag();
        }
    }
    private class CancelOnClickListener implements DialogInterface.OnClickListener {

        public CancelOnClickListener(){
            //
        }
        @Override
        public void onClick(DialogInterface dialog, int which) {
            hideDiag();
        }
    }

    public void showDiag(){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        super.show(ft, "dialog");
    }

    public void hideDiag(){
        super.dismiss();
    }
}
