package com.egcoders.technologysolution.netclub.ui.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.egcoders.technologysolution.netclub.model.CurrentMentor;
import com.egcoders.technologysolution.netclub.R;

public class AddDateActivity extends AppCompatActivity {

    private EditText date;
    private Button add;
    private CurrentMentor mentor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_date);

        date = (EditText) findViewById(R.id.postDate);
        add = (Button) findViewById(R.id.add_btn);

        mentor = new CurrentMentor();

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(date.getText().toString().trim().equals("")){
                    date.setText("");
                }
                else{
                    mentor.getDates().add(date.getText().toString().trim());
                    date.setText("");
                    Intent i = new Intent(AddDateActivity.this, DatesMentorActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent i = new Intent(AddDateActivity.this, DatesMentorActivity.class);
        startActivity(i);
        finish();
    }
}
