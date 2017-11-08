package com.madless.ytloader;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.commit451.youtubeextractor.YouTubeExtractionResult;
import com.commit451.youtubeextractor.YouTubeExtractor;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    Button buttonQuality1080;
    Button buttonQuality240;
    Button buttonQuality360;
    Button buttonQuality720;
    ProgressBar progressBar;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonQuality1080 = (Button) findViewById(R.id.buttonQuality1080);
        buttonQuality240 = (Button) findViewById(R.id.buttonQuality240);
        buttonQuality360 = (Button) findViewById(R.id.buttonQuality360);
        buttonQuality720 = (Button) findViewById(R.id.buttonQuality720);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        textView = (TextView) findViewById(R.id.textView);

        Bundle extras = getIntent().getExtras();
        if(extras != null && extras.getString(Intent.EXTRA_TEXT) != null) {
            progressBar.setVisibility(View.VISIBLE);
            String url = extras.getString(Intent.EXTRA_TEXT);
            String[] parts = url.split("/");
            String videoId = parts[parts.length - 1];
            Log.d("madless", "Fetched url: " + url);
            YouTubeExtractor extractor = YouTubeExtractor.create();
            extractor.extract(videoId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<YouTubeExtractionResult>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            //You should store this disposable and dispose when appropriate
                        }

                        @Override
                        public void onSuccess(final YouTubeExtractionResult value) {
                            progressBar.setVisibility(View.GONE);

                            final Intent intent = new Intent(Intent.ACTION_SEND);
                            intent.setType("text/plain");

                            try {
                                if (value.getSd240VideoUri() != null) {
                                    buttonQuality240.setVisibility(View.VISIBLE);
                                    buttonQuality240.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            intent.putExtra(Intent.EXTRA_TEXT, value.getSd240VideoUri().toString());
                                            startActivity(Intent.createChooser(intent, "Chose app to download video"));
                                        }
                                    });
                                }
                                if (value.getSd360VideoUri() != null) {
                                    buttonQuality360.setVisibility(View.VISIBLE);
                                    buttonQuality360.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            intent.putExtra(Intent.EXTRA_TEXT, value.getSd360VideoUri().toString());
                                            startActivity(Intent.createChooser(intent, "Chose app to download video"));
                                        }
                                    });
                                }
                                if (value.getHd720VideoUri() != null) {
                                    buttonQuality720.setVisibility(View.VISIBLE);
                                    buttonQuality720.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            intent.putExtra(Intent.EXTRA_TEXT, value.getHd720VideoUri().toString());
                                            startActivity(Intent.createChooser(intent, "Chose app to download video"));
                                        }
                                    });
                                }
                                if (value.getHd1080VideoUri() != null) {
                                    buttonQuality1080.setVisibility(View.VISIBLE);
                                    buttonQuality1080.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            intent.putExtra(Intent.EXTRA_TEXT, value.getHd1080VideoUri().toString());
                                            startActivity(Intent.createChooser(intent, "Chose app to download video"));
                                        }
                                    });
                                }
                            } catch (Exception e) {
                                Toast.makeText(MainActivity.this, "Youtube url fetching error.", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                e.printStackTrace();
                                finish();
                            }
                            Log.d("madless", "value240: " + value.getSd240VideoUri());
                            Log.d("madless", "value360: " + value.getSd360VideoUri());
                            Log.d("madless", "value720: " + value.getHd720VideoUri());
                            Log.d("madless", "value1080: " + value.getHd1080VideoUri());
                        }

                        @Override
                        public void onError(Throwable e) {
                            Toast.makeText(MainActivity.this, "Youtube url fetching error.", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            e.printStackTrace();
                            finish();
                        }
                    });
        } else {
            Toast.makeText(this, "Youtube url fetching error.", Toast.LENGTH_SHORT).show();
            Log.d("madless", "Can't fetch url");
            finish();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}
