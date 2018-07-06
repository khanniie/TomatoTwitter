package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

public class ComposeActivity extends AppCompatActivity {

    TwitterClient client;
    EditText et;
    TextView charCount;
    Boolean replying;

    private final TextWatcher characterCounter = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //This sets a textview to the current length
            charCount.setText(String.valueOf(s.length()));
        }

        public void afterTextChanged(Editable s) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        et = findViewById(R.id.compose_box);
        client = TwitterApp.getRestClient(this);
        charCount = findViewById(R.id.charCount);
        replying = getIntent().getBooleanExtra("REPLY", false);

        et.addTextChangedListener(characterCounter);

    }
    public void sendTweet(View btn){
        String message = et.getText().toString();

        if(replying){
            String username = getIntent().getStringExtra("USERNAME");
            Long id = getIntent().getLongExtra("TWEET_ID", 0);
            client.reply("@" + username + " " + message, id, new JsonHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    Log.d("TwitterClient", response.toString());
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d("TwitterClient", errorResponse.toString());
                    throwable.printStackTrace();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    Log.d("TwitterClient", errorResponse.toString());
                    throwable.printStackTrace();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.d("TwitterClient", responseString);
                    throwable.printStackTrace();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try{
                        Intent data = new Intent();
                        Tweet tweet = Tweet.fromJson(response);
                        data.putExtra("TWEET", Parcels.wrap(tweet));
                        setResult(RESULT_OK, data); // set result code and bundle data for response
                        finish(); // closes the edit activity, passes intent back to main
                    } catch(JSONException e){
                        e.printStackTrace();
                    }
                }
            });
        }
        else{
            client.sendTweet(message, new JsonHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    Log.d("TwitterClient", response.toString());
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d("TwitterClient", errorResponse.toString());
                    throwable.printStackTrace();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    Log.d("TwitterClient", errorResponse.toString());
                    throwable.printStackTrace();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.d("TwitterClient", responseString);
                    throwable.printStackTrace();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try{
                        Intent data = new Intent();
                        Tweet tweet = Tweet.fromJson(response);
                        data.putExtra("TWEET", Parcels.wrap(tweet));
                        setResult(RESULT_OK, data); // set result code and bundle data for response
                        finish(); // closes the edit activity, passes intent back to main
                    } catch(JSONException e){
                        e.printStackTrace();
                    }
                }
            });
        }



    }

}
