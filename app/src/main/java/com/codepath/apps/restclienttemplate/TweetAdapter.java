package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder>{
    //pass in tweets array

        private List<Tweet> mTweets;
        Context context;

        public TweetAdapter(List<Tweet> tweets) {
            mTweets = tweets;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
            context = parent.getContext();
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
        public void onBindViewHolder(ViewHolder holder, int position){
            //get data
            Tweet tweet = mTweets.get(position);
            holder.tvUsername.setText(tweet.user.name);
            holder.tvBody.setText(tweet.body);
            holder.tvDate.setText(tweet.createdAt);

            Glide.with(context).load(tweet.user.profileImageUrl).into(holder.ivProfileImage);

            if(tweet.has_media){
                holder.ivMedia.setVisibility(View.VISIBLE);
                Glide.with(context).load(tweet.media_url).into(holder.ivMedia);

            } else{
                holder.ivMedia.setVisibility(View.GONE);
            }

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
        @BindView(R.id.ivMedia) public ImageView ivMedia;

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
}
