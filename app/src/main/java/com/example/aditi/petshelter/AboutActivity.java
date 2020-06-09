package com.example.aditi.petshelter;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class AboutActivity extends AppCompatActivity {
    Toolbar toolbar;
    Button shareButton,feedbackButton;
    CardView sourceCodeOnGithubCardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        toolbar=findViewById(R.id.activity_about_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                                                 @Override
                                                 public void onClick(View view) {
                                                     finish();
                                                 }
                                             }

        );
        setTitle("About");
        shareButton=findViewById(R.id.share_button);
        feedbackButton=findViewById(R.id.feedback_button);
        sourceCodeOnGithubCardView=findViewById(R.id.githubCardView);


        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        feedbackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent mail_intent = new Intent();
                mail_intent.setAction(Intent.ACTION_SENDTO);
                Uri uri = Uri.parse("mailto:aggarwal.aditi97@gmail.com");
                mail_intent.setData(uri);
                startActivity(mail_intent);

            }
        });

        sourceCodeOnGithubCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String githubLink = "https://github.com/imaditiagg/" + getResources().getString(R.string.app_name);
                Intent githubIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(githubLink));
                startActivity(githubIntent);
            }
        });
    }
}
