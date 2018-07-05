package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

public class TweetDetailActivity extends AppCompatActivity {

    ImageView ivProfileImage;
    TextView tvUserName;
    TextView tvDate;
    TextView tvBody;
    TextView tvRetweets;
    TextView tvLikes;
    Button btnLike;
    TwitterClient client;
    Integer favorite_count;
    String uid;
    Context context;
    Tweet tweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context = this;
        super.onCreate(savedInstanceState);
        tweet = Parcels.unwrap(getIntent().getParcelableExtra("TWEET"));

        setContentView(R.layout.activity_tweet_detail);

        ivProfileImage = findViewById(R.id.ivProfileImage);
        tvUserName = findViewById(R.id.tvUsername);
        tvDate = findViewById(R.id.tvDate);
        tvBody = findViewById(R.id.tvBody);
        tvRetweets = findViewById(R.id.tvRetweets);
        tvLikes = findViewById(R.id.tvLikeAmt);
        btnLike = findViewById(R.id.btnLike);

        if(tweet.favorited){
            btnLike.setText("Liked");
        }

        Glide.with(this).load(tweet.user.profileImageUrl).into(ivProfileImage);
        uid = tweet.uid + "";
        favorite_count = Integer.parseInt(tweet.fav_count);

        tvBody.setText(tweet.body);
        tvDate.setText(tweet.createdAt);
        tvUserName.setText(tweet.user.name);
        tvLikes.setText(tweet.fav_count);
        tvRetweets.setText(tweet.retweet_count);

        client = TwitterApp.getRestClient(this);

    }


    public void likePost(View view){
        if(tweet.favorited){
            client.unlikeTweet(uid, new JsonHttpResponseHandler(){

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.d("TwitterClient", response.toString());
                    Toast.makeText(context, "Unliked Post!", Toast.LENGTH_LONG).show();
                    try {
                        tweet = Tweet.fromJson(response);
                        tvDate.setText(tweet.createdAt);
                        //Integer count = favorite_count - 1;
                        tvLikes.setText(tweet.fav_count);
                        tvRetweets.setText(tweet.retweet_count);
                        btnLike.setText("Like");
                        tweet.setFavoriteStatus(false);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d("TwitterClient1", errorResponse.toString());
                    throwable.printStackTrace();
                    try {
                        JSONObject error = (JSONObject) errorResponse.getJSONArray("errors").get(0);
                        Toast.makeText(context, error.getString("message"), Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            });
        }
        else{
            Log.d("DEBUG", "trying to like post");
            client.likeTweet(uid, new JsonHttpResponseHandler(){

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.d("TwitterClient", response.toString());
                    Toast.makeText(context, "Liked Post!", Toast.LENGTH_LONG).show();
                    try {
                        //Integer favorite_count =
                        tweet = Tweet.fromJson(response);
                        tvDate.setText(tweet.createdAt);
                        //Integer count = favorite_count + 1;
                        tvLikes.setText(tweet.fav_count);
                        tvRetweets.setText(tweet.retweet_count);
                        btnLike.setText("Liked");
                        tweet.setFavoriteStatus(true);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d("TwitterClient1", errorResponse.toString());
                    throwable.printStackTrace();
                    try {
                        JSONObject error = (JSONObject) errorResponse.getJSONArray("errors").get(0);
                        Toast.makeText(context, error.getString("message"), Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            });
        }

    }
}
