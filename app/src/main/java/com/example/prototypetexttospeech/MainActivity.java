package com.example.prototypetexttospeech;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextToSpeech mTTS;
    private TextView mText1,mText2;
    private Button mAsk;
    private ArrayList<String> result;
    private int i;
    private boolean b;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAsk = findViewById(R.id.askButton);
        mText1 = findViewById(R.id.textAnswer);
        mText2 = findViewById(R.id.responseTextView);

        Handler handler = new Handler();

        // Using Text to Speech
        mTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {

                if(status == TextToSpeech.SUCCESS){
                    int result = mTTS.setLanguage(Locale.ENGLISH);

                    if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                        Log.e("TTS","Language not supported");
                    }else{
                        mAsk.setEnabled(true);
                    }
                }else{
                    Log.e("TTS","Initialization Failed");
                }

            }
        });

        // On click listener for ask button
        mAsk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mText1.setText("Answer");
                mText2.setText("Response");

                speak();

                //Handler for delaying user voice input
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        speechInput();
                    }
                }, 2700);


            }
        });


    }

    // Speech Input
    public void speechInput(){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,Locale.ENGLISH);

        if(intent.resolveActivity(getPackageManager()) != null){
                startActivityForResult(intent,10);
        }else{
            Toast.makeText(this, "Your Device Don't Support Speech Input",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case 10:
                if(resultCode == RESULT_OK && data != null){
                    result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    mText1.setText("Answer : " + result.get(0));


                    String s = "4";
                    String r = result.get(0);

                    // defining boolean to check if the string we got from user input is equal to 4 or not
                    // this boolean will be used in check function
                    b = s.equals(r);

                    check();
                }
                break;
        }

    }



    // Speak method
    private void speak(){
        String text = "What is the value of 2 into 2 ?";
        mTTS.setPitch(1);
        mTTS.setSpeechRate((float) 0.8);
        mTTS.speak(text,TextToSpeech.QUEUE_FLUSH,null);
    }


    // Checking the response from user
    private void check(){
        String text1 = "Correct";
        String text2 = "Incorrect";
        mTTS.setPitch(1);
        mTTS.setSpeechRate((float) 0.8);

        if(b){
            mText2.setText("Response : Correct");
            mTTS.speak(text1,TextToSpeech.QUEUE_FLUSH,null);
        }else{
            mText2.setText("Response : Incorrect");
            mTTS.speak(text2,TextToSpeech.QUEUE_FLUSH,null);
        }
    }

    @Override
    protected void onDestroy() {
        if(mTTS != null){
            mTTS.stop();
            mTTS.shutdown();
        }
        super.onDestroy();
    }


}