package com.madless.ytloader;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.commit451.youtubeextractor.YouTubeExtractionResult;
import com.commit451.youtubeextractor.YouTubeExtractor;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String YOUTUBE_ID = "qTnIBcRsiDc";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle extras = getIntent().getExtras();
        if(extras != null && extras.getString(Intent.EXTRA_TEXT) != null) {
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
                        public void onSuccess(YouTubeExtractionResult value) {
                            Log.d("madless", "value240: " + value.getSd240VideoUri());
                            Log.d("madless", "value360: " + value.getSd360VideoUri());
                            Log.d("madless", "value720: " + value.getHd720VideoUri());
                            Log.d("madless", "value1080: " + value.getHd1080VideoUri());
                            Intent intent = new Intent(Intent.ACTION_SEND);
                            intent.putExtra(Intent.EXTRA_TEXT, value.getBestAvailableQualityVideoUri().toString());
                            intent.setType("text/plain");
                            startActivity(Intent.createChooser(intent, "Chose app to download video"));
                            finish();
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                        }
                    });
        } else {
            Log.d("madless", "Can't fetch url");
            finish();
        }
    }
}
