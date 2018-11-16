package com.example.ericho.validationtext;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ValidationText validationText = findViewById(R.id.validationText);
        ErrorMsg errorMsg = findViewById(R.id.err);

        errorMsg.bindValidationText(validationText);

        errorMsg.showError("HELLO WORLD");
    }
}
