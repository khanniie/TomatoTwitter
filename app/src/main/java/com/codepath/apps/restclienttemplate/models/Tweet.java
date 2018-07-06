package com.codepath.apps.restclienttemplate.models;

import android.text.format.DateUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

@Parcel
public class Tweet {

    public String body;
    public long uid;
    public String createdAt;
    public User user;
    public String retweet_count;
    public String fav_count;
    public String tweet_id;
    public boolean favorited;
    public boolean retweeted;
    public boolean has_media;
    public String media_url;
    public String media_type;

    public Tweet(){

    }

    public static Tweet fromJson(JSONObject jsonObject) throws JSONException{
        Tweet tweet = new Tweet();

        tweet.body = jsonObject.getString("text");
        tweet.uid = jsonObject.getLong("id");
        tweet.createdAt = getRelativeTimeAgo(jsonObject.getString("created_at"));
        tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
        tweet.retweet_count = jsonObject.getString("retweet_count");
        tweet.fav_count = jsonObject.getString("favorite_count");
        tweet.tweet_id = jsonObject.getString("id");
        tweet.favorited = jsonObject.getBoolean("favorited");
        tweet.retweeted= jsonObject.getBoolean("retweeted");

        if(jsonObject.has("extended_entities")){
            try {

                JSONObject media_obj = (JSONObject) jsonObject.getJSONObject("extended_entities").getJSONArray("media").get(0);
                tweet.media_url = media_obj.getString("media_url");
                tweet.media_type = media_obj.getString("type");
                tweet.has_media = true;
            } catch(Exception e){

                Log.d("MEDIA", "media error");

            }
        }else{
                tweet.has_media = false;
                tweet.media_url = "";
                tweet.media_type = "";
        }


        return tweet;
    }

    public static String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }

    public void setFavoriteStatus(boolean status){
        favorited  = status;
    }


}
