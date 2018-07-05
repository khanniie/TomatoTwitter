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
        public ImageView ivProfileImage;
        public TextView tvUsername;
        public TextView tvBody;
        public TextView tvDate;

        public ViewHolder(View itemView) {
            super(itemView)
;

        ivProfileImage = (ImageView) itemView.findViewById(R.id.ivProfileImage );
        tvUsername = (TextView) itemView.findViewById(R.id.tvUserName);
        tvBody = (TextView) itemView.findViewById(R.id.tvBody);
        tvDate = (TextView) itemView.findViewById(R.id.tvDate);
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
