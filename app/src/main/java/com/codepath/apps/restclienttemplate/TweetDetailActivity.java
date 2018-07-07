package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class TweetDetailActivity extends AppCompatActivity {

    @BindView(R.id.ivProfileImage) ImageView ivProfileImage;
    @BindView(R.id.ivMedia) ImageView ivMedia;
    @BindView(R.id.tvUsername) TextView tvUserName;
    @BindView(R.id.tvDate) TextView tvDate;
    @BindView(R.id.tvBody) TextView tvBody;
    @BindView(R.id.tvRetweets) TextView tvRetweets;
    @BindView(R.id.tvLikeAmt) TextView tvLikes;
    @BindView(R.id.btnLike) ImageButton btnLike;
    @BindView(R.id.btnRT) ImageButton btnRT;

    TwitterClient client;
    Integer favorite_count;
    String uid;
    Context context;
    Tweet tweet;
    MenuItem miActionProgressItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context = this;
        super.onCreate(savedInstanceState);
        tweet = Parcels.unwrap(getIntent().getParcelableExtra("TWEET"));

        setContentView(R.layout.activity_tweet_detail);
        ButterKnife.bind(this);

        if(tweet.favorited){
            btnLike.setBackgroundResource(R.drawable.ic_vector_heart);
        }
        if(tweet.retweeted){
            btnRT.setBackgroundResource(R.drawable.ic_vector_retweet);
        }

        Glide.with(this).load(tweet.user.profileImageUrl).bitmapTransform(new RoundedCornersTransformation(context, 25, 0)).into(ivProfileImage);
        uid = tweet.uid + "";
        favorite_count = Integer.parseInt(tweet.fav_count);

        if(tweet.has_media){

            Glide.with(this).load(tweet.media_url).bitmapTransform(new RoundedCornersTransformation(context, 20, 0)).into(ivMedia);

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
        showProgressBar();

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
                hideProgressBar();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("TwitterClient1", errorResponse.toString());
                throwable.printStackTrace();
                Toast.makeText(context, "Failed retweet/unretweet action", Toast.LENGTH_LONG).show();
                hideProgressBar();
            }
        });
    }
    @OnClick(R.id.ivProfileImage)
    public void seeFollowers(){
        Intent i = new Intent(this, FollowersActivity.class);
        i.putExtra("ID", tweet.user.uid);
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }
    public void onComposeAction(MenuItem mi) {
        // handle click here
        showProgressBar();
        Intent i = new Intent(this, ComposeActivity.class);
        i.putExtra("REPLY", false);
        startActivityForResult(i, 22);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        miActionProgressItem = menu.findItem(R.id.miActionProgress);
        ProgressBar v =  (ProgressBar) MenuItemCompat.getActionView(miActionProgressItem);
        return super.onPrepareOptionsMenu(menu);
    }

    public void showProgressBar() {
        // Show progress item
        miActionProgressItem.setVisible(true);
    }

    public void hideProgressBar() {
        // Hide progress item
        miActionProgressItem.setVisible(false);
    }

    public void likePost(View view){
        showProgressBar();
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
                hideProgressBar();
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
                hideProgressBar();
            }

        });

    }
}
