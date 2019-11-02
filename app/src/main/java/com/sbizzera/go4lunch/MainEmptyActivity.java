package com.sbizzera.go4lunch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainEmptyActivity extends AppCompatActivity implements View.OnClickListener {

    private Button goToLogInBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_empty);
        goToLogInBtn = findViewById(R.id.go_to_log_in_btn);
        goToLogInBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
    }
}

