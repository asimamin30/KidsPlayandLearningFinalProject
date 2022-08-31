package com.fyp.autisticchildlearner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executor;

public class QuizActivity extends AppCompatActivity {


    int locationOfCorrectAnswer;
    ArrayList<Integer> answers = new ArrayList<Integer>();
    int totalQuestions;
    int correctQuestions;

    TextView pointsTextView;
    TextView resultTextView;
    TextView timeTextView;
    Button button1;
    Button button2;
    Button button3;
    Button button4;
    TextView questionTextView;
    Button playAgainButton;
    MediaPlayer mediaPlayer,mediaPlayer1;
    RelativeLayout gameRelativeLayout;
    CountDownTimer timer;
    SharedPreferences preferences;
    int highScore;
    TextView highScoreView;

    String SITE_KEY = " 6Lcu5cIhAAAAAKR_5z_kS-msSUUU8tRAsuhRbO_k";
    String SECRET_KEY = "6Lcu5cIhAAAAAAPkkfzVR8AfPIbdfBAf5hf9Fce0";
    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);


        mediaPlayer = MediaPlayer.create( QuizActivity.this, R.raw.clock );
        mediaPlayer1=MediaPlayer.create( this,R.raw.applause );
//        getSupportActionBar().setElevation( 0f );
//        getSupportActionBar().setHomeAsUpIndicator( getResources().getDrawable( R.drawable.ic_arrow_back_white_24dp ) );
//        getSupportActionBar().setTitle( "quiz" );
        initViews();

    }

    private void initViews() {

        preferences = PreferenceManager.getDefaultSharedPreferences( this );

        highScore = preferences.getInt( "high_score", 0 );
        highScoreView = findViewById( R.id.tv_high_score );
        highScoreView.setText( "HighScore = " + String.valueOf( preferences.getInt( "high_score", 0 ) ) );

        pointsTextView = findViewById( R.id.tv_points );
        resultTextView = findViewById( R.id.tv_result );
        timeTextView = findViewById( R.id.tv_timer );
        questionTextView = findViewById( R.id.tv_question );
        button1 = findViewById( R.id.button1 );
        button2 = findViewById( R.id.button2 );
        button3 = findViewById( R.id.button3 );
        button4 = findViewById( R.id.button4 );
        playAgainButton = findViewById( R.id.button_play_again );
        gameRelativeLayout = findViewById( R.id.gameRelativeLayout );
        gameRelativeLayout.setVisibility( View.INVISIBLE );

        playAgainButton.setText( "Play" );

        playAgainButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playAgainButton.setVisibility( View.INVISIBLE );
                gameRelativeLayout.setVisibility( View.VISIBLE );
                highScoreView.setVisibility( View.INVISIBLE );
                createQuestion();
                startQuiz();
            }
        } );

    }

    private void startQuiz() {
        timeTextView.setText( "30s" );
        totalQuestions = 0;
        correctQuestions = 0;
        mediaPlayer = MediaPlayer.create( QuizActivity.this, R.raw.clock );
        pointsTextView.setText( "0/0" );
        resultTextView.setVisibility( View.INVISIBLE );
        timer = new CountDownTimer( 30100, 1000 ) {
            @Override
            public void onTick(long millisUntilFinished) {


                mediaPlayer.start();
                timeTextView.setText( String.valueOf( millisUntilFinished / 1000 ) + "s" );
            }

            @Override
            public void onFinish() {
                mediaPlayer.pause();

                timeTextView.setText( "0s" );
                playAgainButton.setText( "Play Again" );
                playAgainButton.setVisibility( View.VISIBLE );
                gameRelativeLayout.setVisibility( View.INVISIBLE );
                showScore();

            }
        }.start();
    }

    private void showScore() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("if you are parents! click yes else select No");
                alertDialogBuilder.setPositiveButton("yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                              OpneRecaptcha();
                            }
                        });

        alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void OpneRecaptcha() {

        // below line is use for getting our safety
        // net client and verify with reCAPTCHA
        SafetyNet.getClient(this).verifyWithRecaptcha(SITE_KEY)
                // after getting our client we have
                // to add on success listener.
                .addOnSuccessListener(this, new OnSuccessListener<SafetyNetApi.RecaptchaTokenResponse>() {
                    @Override
                    public void onSuccess(SafetyNetApi.RecaptchaTokenResponse response) {
                        // in below line we are checking the response token.
                        if (!response.getTokenResult().isEmpty()) {
                            // if the response token is not empty then we
                            // are calling our verification method.
                            handleVerification(response.getTokenResult());
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // this method is called when we get any error.
                        if (e instanceof ApiException) {
                            ApiException apiException = (ApiException) e;
                            // below line is use to display an error message which we get.
                            Log.d("TAG", "Error message: " +
                                    CommonStatusCodes.getStatusCodeString(apiException.getStatusCode()));
                        } else {
                            // below line is use to display a toast message for any error.
                        }
                    }
                });
    }

    protected void handleVerification(final String responseToken) {
        // inside handle verification method we are
        // verifying our user with response token.
        // url to sen our site key and secret key
        // to below url using POST method.
        String url = "https://www.google.com/recaptcha/api/siteverify";

        // in this we are making a string request and
        // using a post method to pass the data.
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // inside on response method we are checking if the
                        // response is successful or not.
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getBoolean("success")) {
                                // if the response is successful then we are
                                // showing below toast message.
                                showNo();

                            } else {
                                // if the response if failure we are displaying
                                // a below toast message.
                                Toast.makeText(getApplicationContext(), String.valueOf(jsonObject.getString("error-codes")), Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception ex) {
                            // if we get any exception then we are
                            // displaying an error message in logcat.
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("TAG", "Error message: " + error.getMessage());

                    }


                }) {
            // below is the getParamns method in which we will
            // be passing our response token and secret key to the above url.
            @Override
            protected Map<String, String> getParams() {
                // we are passing data using hashmap
                // key and value pair.
                Map<String, String> params = new HashMap<>();
                params.put("secret", SECRET_KEY);
                params.put("response", responseToken);
                return params;
            }
        };
        // below line of code is use to set retry
        // policy if the api fails in one try.
        request.setRetryPolicy(new DefaultRetryPolicy(
                // we are setting time for retry is 5 seconds.
                50000,

                // below line is to perform maximum retries.
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // at last we are adding our request to queue.
        queue.add(request);

//



    }

    private void showNo() {

        resultTextView.setVisibility( View.VISIBLE );
        resultTextView.setText( "Score = " + correctQuestions + "/" + totalQuestions );

        String message;

        if (correctQuestions >= highScore) {
            highScore = correctQuestions;
            preferences.edit().putInt( "high_score", correctQuestions ).apply();
            message = "CONGRATS\nNew High Score = ";
            mediaPlayer1.start();
        } else {
            message = "High Score = ";
        }

        highScoreView.setText( message + String.valueOf( highScore ) );
        highScoreView.setVisibility( View.VISIBLE );
    }


    public void checkAnswer(final View view) {

        int g = R.color.green;
        int r = R.color.red;
        if (view.getTag().toString().equals( String.valueOf( locationOfCorrectAnswer ) )) {
            view.setBackgroundResource( g );
            //resultTextView.setText("Correct");
            correctQuestions++;
        } else {
            view.setBackgroundResource( r );
            //resultTextView.setText("Incorrect");
        }

        totalQuestions++;
        pointsTextView.setText( correctQuestions + "/" + totalQuestions );
        final Handler handler = new Handler();
        handler.postDelayed( new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                view.setBackgroundResource( R.color.grey );
                createQuestion();

            }
        }, 300 );


    }

    private void createQuestion() {

        answers.clear();

        Random random = new Random();

        int a = random.nextInt( 30 );
        int b = random.nextInt( 30 );

        locationOfCorrectAnswer = random.nextInt( 4 );
        int wrongAnswer;

        for (int i = 0; i < 4; i++) {

            if (i == locationOfCorrectAnswer) {
                answers.add( a + b );
            } else {
                wrongAnswer = random.nextInt( 30 );

                while (wrongAnswer == (a + b)) {
                    wrongAnswer = random.nextInt( 30 );
                }
                answers.add( wrongAnswer );
            }

        }

        String question = String.valueOf( a ) + "+" + String.valueOf( b );
        questionTextView.setText( question );

        button1.setText( String.valueOf( answers.get( 0 ) ) );
        button2.setText( String.valueOf( answers.get( 1 ) ) );
        button3.setText( String.valueOf( answers.get( 2 ) ) );
        button4.setText( String.valueOf( answers.get( 3 ) ) );

    }

    @Override
    public void onBackPressed() {

        if(highScoreView.getVisibility()==View.VISIBLE)
        {
            mediaPlayer.stop();
            super.onBackPressed();
            mediaPlayer1.stop();
        }
        else {
            timer.onFinish();
            timer.cancel();
            mediaPlayer.stop();
        }

    }
}
