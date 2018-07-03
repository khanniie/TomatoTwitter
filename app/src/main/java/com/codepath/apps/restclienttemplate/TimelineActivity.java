package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {

    TwitterClient client;
    TweetAdapter tweetAdapter;
    ArrayList<Tweet> tweets;
    RecyclerView rvTweets;
    public static int REQUEST_CODE = 20;
    private SwipeRefreshLayout swipeContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        client = TwitterApp.getRestClient(this);
        rvTweets = (RecyclerView) findViewById(R.id.rvTweet);

        tweets = new ArrayList<>();

        tweetAdapter = new TweetAdapter(tweets);
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                fetchTimelineAsync(0);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        rvTweets.setLayoutManager(new LinearLayoutManager(this));
        rvTweets.setAdapter(tweetAdapter);

        populateTimeline();
    }

    public void fetchTimelineAsync(int page) {
        // Send the network request to fetch the updated data
        // `client` here is an instance of Android Async HTTP
        // getHomeTimeline is an example endpoint.
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response)  {
                Log.d("DEBUG", "starting getTweetList");
                // Remember to CLEAR OUT old items before appending in the new ones
                tweetAdapter.clear();
                // ...the data has come back, add new items to your adapter...
                getTweetList();
            }

            public void onFailure(Throwable e) {
                Log.d("DEBUG", "Fetch timeline error: " + e.toString());
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    public void onComposeAction(MenuItem mi) {
        // handle click here
        Log.i("COMPOSE", "clicked");
        Intent i = new Intent(this, ComposeActivity.class);
        startActivityForResult(i, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Toast.makeText(this, "made_tweet", Toast.LENGTH_SHORT).show();
        super.onActivityResult(requestCode, resultCode, data);
        // EDIT_REQUEST_CODE defined with constants

        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            // extract updated item value from result extras
            Tweet tweet = (Tweet) Parcels.unwrap(data.getParcelableExtra("TWEET"));
            tweets.add(0, tweet);
            tweetAdapter.notifyItemInserted(0);
            rvTweets.scrollToPosition(0);

            Toast.makeText(this, "Item updated", Toast.LENGTH_SHORT).show();
        }
    }
    private void getTweetList(){
        Toast.makeText(TimelineActivity.this, "trying to make tweet list", Toast.LENGTH_SHORT).show();
        List<Tweet> new_list;
        client.getHomeTimeline(
            new JsonHttpResponseHandler() {
              @Override
              public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Toast.makeText(TimelineActivity.this, "did a correcrt ghing", Toast.LENGTH_SHORT).show();
                final List<Tweet> temp_list = new ArrayList<>();

                for (int i = 0; i < response.length(); i++) {
                  try {
                    Tweet tweet = Tweet.fromJson(response.getJSONObject(i));
                    temp_list.add(tweet);
                  } catch (JSONException e) {
                    e.printStackTrace();
                  }
                }
                tweetAdapter.addAll(temp_list);
                // Now we call setRefreshing(false) to signal refresh has finished
                swipeContainer.setRefreshing(false);
              }
            });
    }

    private void populateTimeline(){
        client.getHomeTimeline(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("TwitterClient", response.toString());
                for(int i = 0; i < response.length(); i++){
                    try{
                        Tweet tweet = Tweet.fromJson(response.getJSONObject(i));
                        tweets.add(tweet);
                        tweetAdapter.notifyItemInserted(tweets.size() - 1);
                    } catch(JSONException e){
                        e.printStackTrace();
                    }



                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
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
        });
    }
}
