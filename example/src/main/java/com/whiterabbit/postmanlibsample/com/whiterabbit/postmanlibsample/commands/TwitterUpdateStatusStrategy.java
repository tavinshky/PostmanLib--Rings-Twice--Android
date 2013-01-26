package com.whiterabbit.postmanlibsample.com.whiterabbit.postmanlibsample.commands;

import android.content.Context;
import android.os.Parcel;
import com.whiterabbit.postman.commands.RestServerStrategy;
import com.whiterabbit.postman.exceptions.ResultParseException;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Verb;

/**
 * Created with IntelliJ IDEA.
 * User: fedepaol
 * Date: 12/18/12
 * Time: 12:41 AM
 */
public class TwitterUpdateStatusStrategy implements RestServerStrategy{
    private static final String url = "https://api.twitter.com/1.1/statuses/update.json";
    private String mStatus;


    public TwitterUpdateStatusStrategy(String status){
        mStatus = status;
    }

    @Override
    public String getOAuthSigner() {
        return "Twitter";
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public Verb getVerb() {
        return Verb.POST;
    }

    @Override
    public void processHttpResult(Response result, Context context) throws ResultParseException {
    }

    @Override
    public void addParamsToRequest(OAuthRequest request) {
        request.addBodyParameter("status", mStatus);
    }

    @Override
    public int describeContents() {
        return 0;  //TODO Autogenerated
    }



    public static final Creator<TwitterUpdateStatusStrategy> CREATOR
            = new Creator<TwitterUpdateStatusStrategy>() {
        public TwitterUpdateStatusStrategy createFromParcel(Parcel in) {
            return new TwitterUpdateStatusStrategy(in);
        }

        public TwitterUpdateStatusStrategy[] newArray(int size) {
            return new TwitterUpdateStatusStrategy[size];
        }
    };


    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mStatus);
    }

    public TwitterUpdateStatusStrategy(Parcel in){
        mStatus = in.readString();
    }

}
