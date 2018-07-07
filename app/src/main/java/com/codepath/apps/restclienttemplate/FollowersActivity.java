package com.codepath.apps.restclienttemplate;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class FollowersActivity extends AppCompatActivity {

    TwitterClient client;
    ArrayAdapter followersAdapter;
    ArrayAdapter followingAdapter;
    private ArrayList<String> followers;
    private ArrayList<String> following;
    @BindView(R.id.lvFollowing) ListView lvFollowing;
    @BindView(R.id.lvFollowers) ListView lvFollowers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        followers = new ArrayList<String>();
        following = new ArrayList<String>();

        setContentView(R.layout.activity_followers);
        ButterKnife.bind(this);
        // initialize the adapter using the items list
        followersAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, followers);
        // wire the adapter to the view
        lvFollowers.setAdapter(followersAdapter);

        // initialize the adapter using the items list
        followingAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, following);
        // wire the adapter to the view
        lvFollowing.setAdapter(followingAdapter);


        client = TwitterApp.getRestClient(this);

        Long id = getIntent().getLongExtra("ID", 0);

        client.getFollowers(id, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray users = response.getJSONArray("users");
                    Log.d("USERS", users.toString());
                    for(int i = 0; i < users.length(); i++){
                        Log.d("USERS" + i, users.getJSONObject(i).toString());
                        try{
                            followers.add(users.getJSONObject(i).getString("screen_name"));
                            followersAdapter.notifyDataSetChanged();
                        } catch(JSONException e){
                            e.printStackTrace();
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        followersAdapter.notifyDataSetChanged();

        client.getFollowing(id, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray users = response.getJSONArray("users");
                    for(int i = 0; i < users.length(); i++){
                        try{
                            following.add(users.getJSONObject(i).getString("screen_name"));
                            Log.d("FOLLOWER LIST", following.toString());
                            followingAdapter.notifyDataSetChanged();
                        } catch(JSONException e){
                            e.printStackTrace();
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }
}
