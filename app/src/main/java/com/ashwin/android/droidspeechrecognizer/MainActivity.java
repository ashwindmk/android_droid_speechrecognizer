package com.ashwin.android.droidspeechrecognizer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.SpeechRecognizer;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

import github.com.vikramezhil.dks.speech.Dks;
import github.com.vikramezhil.dks.speech.DksListener;

public class MainActivity extends AppCompatActivity {
    public static final String APP_TAG = "speech-to-text";
    private static final int RecordAudioRequestCode = 1;
    private Dks dks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        System.out.println(APP_TAG + ": SpeechRecognizer.isRecognitionAvailable: " + SpeechRecognizer.isRecognitionAvailable(this));

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            checkPermission();
        }

        EditText editText = findViewById(R.id.text);
        ImageView micButton = findViewById(R.id.button);

        dks = new Dks(getApplication(), getSupportFragmentManager(), new DksListener() {
            @Override
            public void onDksLiveSpeechResult(String s) {
                System.out.println(APP_TAG + ": onDksLiveSpeechResult( " + s + " )");
                editText.setHint("Listening...");
            }

            @Override
            public void onDksFinalSpeechResult(String s) {
                System.out.println(APP_TAG + ": onDksFinalSpeechResult( " + s + " )");
                editText.setText(s);
            }

            @Override
            public void onDksLiveSpeechFrequency(float v) {
                // This is called very frequently.
//                System.out.println(APP_TAG + ": onDksLiveSpeechFrequency( " + v + " )");
            }

            @Override
            public void onDksLanguagesAvailable(String s, ArrayList<String> arrayList) {
                System.out.println(APP_TAG + ": onDksLanguagesAvailable( " + s + " )");
            }

            @Override
            public void onDksSpeechError(String s) {
                System.out.println(APP_TAG + ": onDksSpeechError( " + s + " )");
                micButton.setImageResource(R.drawable.ic_mic_off);
                editText.setText("");
            }
        });
        dks.setOneStepResultVerify(true);

        micButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                System.out.println(APP_TAG + ": micButton.onTouch (thread: " + Thread.currentThread().getName() + ")");
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    System.out.println(APP_TAG + ":  ACTION_UP");
                    dks.closeSpeechOperations();
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    System.out.println(APP_TAG + ":  ACTION_DOWN");
                    micButton.setImageResource(R.drawable.ic_mic_on);
                    dks.startSpeechRecognition();
                }
                return false;
            }
        });
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, RecordAudioRequestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RecordAudioRequestCode && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                System.out.println(APP_TAG + ": Permission Granted");
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                System.out.println(APP_TAG + ": Permission Denied");
            }
        } else {
            System.out.println(APP_TAG + ": Permission Denied");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dks.closeSpeechOperations();
    }
}
