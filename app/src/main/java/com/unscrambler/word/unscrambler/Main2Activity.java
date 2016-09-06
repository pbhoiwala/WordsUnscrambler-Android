package com.unscrambler.word.unscrambler;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

public class Main2Activity extends AppCompatActivity {

    private String marketURL = "market://details?id=com.unscrambler.word.unscrambler";
    private String playStoreURL = "https://play.google.com/store/apps/details?id=com.unscrambler.word.unscrambler";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        android.support.v7.app.ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#51c2f2")));
//        ab.setLogo(R.mipmap.ic_launcher);
        ab.setDisplayUseLogoEnabled(true);
        ab.setDisplayShowHomeEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("Word Unscrambler");

        TextView info = (TextView)findViewById(R.id.help);
        info.setText("I will keep this app as simple as possible and completely ad-free.\n\n" +
                "If you have any feedback for this app or " +
                "would like to suggest a feature, please leave " +
                "your comment in the play store.\n\n");

        Button rate = (Button)findViewById(R.id.rate);
        rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent rate = new Intent(Intent.ACTION_VIEW, Uri.parse(marketURL));
                startActivity(rate);
            }
        });

        Button share = (Button)findViewById(R.id.share);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                shareIt();
            }
        });

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
//                NavUtils.navigateUpFromSameTask(this);
//                Intent info = new Intent(Main2Activity.this, MainActivity.class);
//                startActivity(info);
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
//        return super.onOptionsItemSelected(item);
    }

    private void shareIt() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String text = "Hey, checkout this awesome android app called Word Unscrambler." +
                " It helps you unscramble almost any scrambled english word. Check it out: " + playStoreURL;
        sharingIntent.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }



}
