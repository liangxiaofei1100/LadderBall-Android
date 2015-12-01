package com.zhaoyan.ladderball;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            int i = 1 / 0;
        } catch (Exception e) {
            e.printStackTrace();
            StackTraceElement[] traceElements = e.getStackTrace();
            if (traceElements != null) {
                for (int i = 0; i < traceElements.length; i++) {
                    System.out.println(traceElements[i].toString());
                }
            }
        }

    }
}
