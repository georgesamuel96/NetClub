package com.egcoders.technologysolution.netclub.ui.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.egcoders.technologysolution.netclub.model.CurrentMentor;
import com.egcoders.technologysolution.netclub.R;

public class MoreDetailsMentorActivity extends AppCompatActivity {

    private ImageView continueBtn;
    private EditText content, description;
    private CurrentMentor mentor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_details_mentor);

        continueBtn = (ImageView) findViewById(R.id.next_btn);
        content = (EditText) findViewById(R.id.mentor_content);
        description = (EditText) findViewById(R.id.mentor_desc);

        mentor = new CurrentMentor();

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mentor.setContent(content.getText().toString());
                mentor.setDescription(description.getText().toString());
                Intent i = new Intent(MoreDetailsMentorActivity.this, MentorCategoriesActivity.class);
                startActivity(i);

            }
        });
    }
}
