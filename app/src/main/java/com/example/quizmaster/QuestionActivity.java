package com.example.quizmaster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static com.example.quizmaster.SetsActivity.category_id;

public class QuestionActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView question , qCount , timer;
    private Button option1 , option2 , option3 ,option4 ;
    private List<Question> questionList;
    private int questionNumber,score;
    private CountDownTimer countDown;
    private FirebaseFirestore firestore;
    private int setno;
    private Dialog loadingDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        question = findViewById(R.id.question);
        qCount = findViewById(R.id.question_no);
        timer = findViewById(R.id.countdown);

        option1 =findViewById(R.id.option1);
        option2 =findViewById(R.id.option2);
        option3 =findViewById(R.id.option3);
        option4 =findViewById(R.id.option4);

        option1.setOnClickListener(this);
        option2.setOnClickListener(this);
        option3.setOnClickListener(this);
        option4.setOnClickListener(this);

        firestore = FirebaseFirestore.getInstance();
        //loading dialog
        loadingDialog = new Dialog(QuestionActivity.this);
        loadingDialog.setContentView(R.layout.loading_progressbar);
        //user can't cancel the dialog
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawableResource(R.drawable.progress_background);
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();

        setno = getIntent().getIntExtra("SETNO",1);

        //initialising firebase



        getQuestionsList();
    }

    private void getQuestionsList(){
    questionList = new ArrayList<>();

    //from the instance made in oncreate method we will access nosql data of collection question
        firestore.collection("QUIZ").document("CAT"+ category_id).collection("SET"+String.valueOf(setno)).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            QuerySnapshot questions = task.getResult(); //querysnapshot is used for multiple document

                            for(QueryDocumentSnapshot doc: questions){
                                questionList.add(new Question(doc.getString("QUESTION"),
                                        doc.getString("A"),
                                        doc.getString("B"),
                                        doc.getString("C"),
                                        doc.getString("D"),
                                        Integer.valueOf(doc.getString("ANSWER"))
                                ));
                            }
                            setQuestion();

                        }
                        else{
                            Toast.makeText(QuestionActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                        loadingDialog.cancel();
                    }
                });


    }
    private void setQuestion(){
        timer.setText(String.valueOf(10));
        question.setText(questionList.get(0).getQuestion());
        option1.setText(questionList.get(0).getOptionA());
        option2.setText(questionList.get(0).getOptionB());
        option3.setText(questionList.get(0).getOptionC());
        option4.setText(questionList.get(0).getOptionD());
        qCount.setText(String.valueOf(1) + "/" + String.valueOf(questionList.size()));
        startTimer();
        questionNumber =0;
        score =0;
    }

    private void startTimer(){

        countDown = new CountDownTimer(10000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timer.setText(String.valueOf(millisUntilFinished/1000));
            }

            @Override
            public void onFinish() {
                changeQuestion();
            }
        };
        countDown.start();
    }

    @Override
    public void onClick(View v) {
        int selectedOption=0;
        switch (v.getId()){
            case R.id.option1:
                selectedOption =1;
                break;
            case R.id.option2:
                selectedOption =2;
                break;

            case R.id.option3:
                selectedOption =3;
                break;

            case R.id.option4:
                selectedOption =4;
                break;
            default:
        }
        countDown.cancel();
        checkAnswer(selectedOption, v);
    }

    private void checkAnswer(int selectedOption,View v){
        if(selectedOption == questionList.get(questionNumber).correctAns){
            ((Button)v).setBackgroundColor(getResources().getColor(R.color.correct));
            score++;
        }
        else{
            ((Button)v).setBackgroundColor(getResources().getColor(R.color.wrong));
            switch (questionList.get(questionNumber).getCorrectAns()){
                case 1:
                option1.setBackgroundColor(getResources().getColor(R.color.correct));
                break;
                case 2:
                    option2.setBackgroundColor(getResources().getColor(R.color.correct));
                    break;
                case 3:
                    option3.setBackgroundColor(getResources().getColor(R.color.correct));
                    break;
                case 4:
                    option4.setBackgroundColor(getResources().getColor(R.color.correct));
                    break;

            }
        }
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                changeQuestion();
            }
        },1000);

    }

    private void changeQuestion(){
        if(questionNumber < questionList.size()-1){
            questionNumber++;
            playAnimation(question,0,0);
            playAnimation(option1,0,1);
            playAnimation(option2,0,2);
            playAnimation(option3,0,3);
            playAnimation(option4,0,4);
            qCount.setText(String.valueOf(questionNumber+1)+"/"+String.valueOf(questionList.size()));
            timer.setText(String.valueOf(10));
            startTimer();
        }
        else{
            //score activity initialises here
            Intent intent = new Intent(QuestionActivity.this,ScoreActivity.class);
            intent.putExtra("SCORE",String.valueOf(score)+"/"+String.valueOf(questionList.size()));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // clearing all the activity before score
            startActivity(intent);
            QuestionActivity.this.finish();
        }
    }

    private void playAnimation(final View view , final int value , final int viewNum){
        view.animate().alpha(value).scaleX(value).scaleY(value).setDuration(500).setStartDelay(100).setInterpolator(new DecelerateInterpolator())
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                    if(value==0){
                        switch (viewNum){
                            case 0:
                                ((TextView)view).setText(questionList.get(questionNumber).getQuestion());
                                break;
                            case 1:
                                ((Button)view).setText(questionList.get(questionNumber).getOptionA());
                                break;
                            case 2:
                                ((Button)view).setText(questionList.get(questionNumber).getOptionB());
                                break;
                            case 3:
                                ((Button)view).setText(questionList.get(questionNumber).getOptionC());
                                break;
                            case 4:
                                ((Button)view).setText(questionList.get(questionNumber).getOptionD());
                                break;

                        }
                        if(viewNum!=0){
                            ((Button)view).setBackgroundColor(getResources().getColor(R.color.option));
                        }
                        playAnimation(view,1,viewNum);
                    }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        countDown.cancel();
    }
}