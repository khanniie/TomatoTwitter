package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder>{
    //pass in tweets array

        private List<Tweet> mTweets;
        Context context;
        TwitterClient client;

        public TweetAdapter(List<Tweet> tweets) {
            mTweets = tweets;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
            context = parent.getContext();
            client = TwitterApp.getRestClient(context);
            LayoutInflater inflater = LayoutInflater.from(context);

            View tweetView = inflater.inflate(R.layout.item_tweet, parent, false);
            ViewHolder viewHolder = new ViewHolder(tweetView);
            return viewHolder;
        }


    @Override
    public int getItemCount() {
        return mTweets.size();
    }

    @Override
        public void onBindViewHolder(final ViewHolder holder, int position){
            //get data
            final Tweet tweet = mTweets.get(position);
            holder.tvUsername.setText(tweet.user.name);
            holder.tvBody.setText(tweet.body);
            holder.tvDate.setText(tweet.createdAt);
            holder.tvScreenName.setText("@" + tweet.user.screenName);

            Glide.with(context).load(tweet.user.profileImageUrl).bitmapTransform(new RoundedCornersTransformation(context, 25, 0)).into(holder.ivProfileImage);

            if(tweet.has_media){
                holder.ivMedia.setVisibility(View.VISIBLE);
                Glide.with(context).load(tweet.media_url).bitmapTransform(new RoundedCornersTransformation(context, 20, 0)).into(holder.ivMedia);

            } else{
                holder.ivMedia.setVisibility(View.GONE);
            }
            if(tweet.favorited){
                holder.btnLike.setBackgroundResource(R.drawable.ic_vector_heart);
            }
            if(tweet.retweeted){
                holder.btnRT.setBackgroundResource(R.drawable.ic_vector_retweet);
            }

            holder.btnLike.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    likePost(tweet, holder);
                }
            });

        holder.btnRT.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                retweet(holder, tweet);
            }
        });

        holder.btnReply.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                reply(tweet);
            }
        });

        }

    public void clear() {
        mTweets.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Tweet> list) {
        mTweets.addAll(list);
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @BindView(R.id.ivProfileImage) public ImageView ivProfileImage;
        @BindView(R.id.tvUserName) public TextView tvUsername;
        @BindView(R.id.tvBody) public TextView tvBody;
        @BindView(R.id.tvDate) public TextView tvDate;
        @BindView(R.id.tvScreenName) public TextView tvScreenName;
        @BindView(R.id.ivMedia) public ImageView ivMedia;

        @BindView(R.id.btnLike) public ImageButton btnLike;
        @BindView(R.id.btnReply) public ImageButton btnReply;
        @BindView(R.id.btnRT) public ImageButton btnRT;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if(position != RecyclerView.NO_POSITION){
                Tweet tweet = mTweets.get(position);
                Intent i = new Intent(context, TweetDetailActivity.class);
                i.putExtra("TWEET", Parcels.wrap(tweet));
                context.startActivity(i);
            }
        }
    }

    public void likePost(Tweet tweet, ViewHolder holder){
        final  ViewHolder final_holder = holder;
        final Tweet org_tweet = tweet;
        client.likeTweet(tweet.favorited, tweet.uid + "", new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("TwitterClient", response.toString());
                try {
                    final Tweet final_tweet = Tweet.fromJson(response);
                    if(final_tweet.favorited){
                        final_holder.btnLike.setBackgroundResource(R.drawable.ic_vector_heart);
                        org_tweet.setFavoriteStatus(true);
                        Toast.makeText(context, "Liked Post!", Toast.LENGTH_LONG).show();
                    } else{
                        final_holder.btnLike.setBackgroundResource(R.drawable.ic_vector_heart_stroke);
                        org_tweet.setFavoriteStatus(false);
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

    public void reply(Tweet tweet){
        Log.i("REPLY", "clicked");
        Intent i = new Intent(context, ComposeActivity.class);
        i.putExtra("USERNAME", tweet.user.screenName);
        i.putExtra("TWEET_ID", tweet.uid);
        i.putExtra("REPLY", true);
        context.startActivity(i);
    }

    public void retweet(ViewHolder holder, Tweet tweet){

        final boolean undo = tweet.retweeted; //if already retweeted, the click will UNDO instead
        final ViewHolder holder_final = holder;
        client.retweet(tweet.retweeted, tweet.uid, new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if(undo){
                    Toast.makeText(context, "UN-Retweeted!", Toast.LENGTH_LONG).show();
                    holder_final.btnRT.setBackgroundResource(R.drawable.ic_vector_retweet_stroke);
                } else {
                    Toast.makeText(context, "Retweeted!", Toast.LENGTH_LONG).show();
                    holder_final.btnRT.setBackgroundResource(R.drawable.ic_vector_retweet);
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
}
