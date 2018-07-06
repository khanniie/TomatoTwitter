package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
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
    ImageView ivMedia;
    TextView tvUserName;
    TextView tvDate;
    TextView tvBody;
    TextView tvRetweets;
    TextView tvLikes;
    ImageButton btnLike;
    ImageButton btnRT;
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
        btnRT = findViewById(R.id.btnRT);
        ivMedia = findViewById(R.id.ivMedia);

        if(tweet.favorited){
            btnLike.setBackgroundResource(R.drawable.ic_vector_heart);
        }
        if(tweet.retweeted){
            btnRT.setBackgroundResource(R.drawable.ic_vector_retweet);
        }

        Glide.with(this).load(tweet.user.profileImageUrl).into(ivProfileImage);
        uid = tweet.uid + "";
        favorite_count = Integer.parseInt(tweet.fav_count);

        if(tweet.has_media){

            Toast.makeText(this, "has media: " + tweet.media_type, Toast.LENGTH_LONG).show();
            Glide.with(this).load(tweet.media_url).into(ivMedia);

        } else{
            ivMedia.setVisibility(View.GONE);
        }

        tvBody.setText(tweet.body);
        tvDate.setText(tweet.createdAt);
        tvUserName.setText(tweet.user.name);
        tvLikes.setText(tweet.fav_count);
        tvRetweets.setText(tweet.retweet_count);

        client = TwitterApp.getRestClient(this);

    }
    public void reply(View view){
        Log.i("REPLY", "clicked");
        Intent i = new Intent(this, ComposeActivity.class);
        i.putExtra("USERNAME", tweet.user.screenName);
        i.putExtra("TWEET_ID", tweet.uid);
        i.putExtra("REPLY", true);
        startActivityForResult(i, 21);
    }
    public void retweet(View view){

        final boolean undo = tweet.retweeted; //if already retweeted, the click will UNDO instead

        client.retweet(tweet.retweeted, tweet.uid, new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    tweet = Tweet.fromJson(response);
                    tvRetweets.setText(tweet.retweet_count);

                    if(undo){
                        Toast.makeText(context, "UN-Retweeted!", Toast.LENGTH_LONG).show();
                        btnRT.setBackgroundResource(R.drawable.ic_vector_retweet_stroke);
                    } else {
                        Toast.makeText(context, "Retweeted!", Toast.LENGTH_LONG).show();
                        btnRT.setBackgroundResource(R.drawable.ic_vector_retweet);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("TwitterClient1", errorResponse.toString());
                throwable.printStackTrace();
                Toast.makeText(context, "Failed retweet/unretweet action", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void likePost(View view){
        boolean favorited = tweet.favorited;
        client.likeTweet(tweet.favorited, uid, new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("TwitterClient", response.toString());
                try {
                    tweet = Tweet.fromJson(response);
                    tvLikes.setText(tweet.fav_count);
                    if(tweet.favorited){
                        btnLike.setBackgroundResource(R.drawable.ic_vector_heart);
                        tweet.setFavoriteStatus(true);
                        Toast.makeText(context, "Liked Post!", Toast.LENGTH_LONG).show();
                    } else{
                        btnLike.setBackgroundResource(R.drawable.ic_vector_heart_stroke);
                        tweet.setFavoriteStatus(false);
                        Toast.makeText(context, "Unliked Post!", Toast.LENGTH_LONG).show();
                    }

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
