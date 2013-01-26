package com.whiterabbit.postman;

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
 * Date: 12/30/12
 * Time: 2:25 PM
 */
public class SimpleRestStrategy implements RestServerStrategy {
    public final String KEY = "key";
    public final String VALUE = "value";
    private String mResultString;
    private OAuthRequest mMockedRequest;
    private boolean mMustSign;



    public final static String SERVICE_NAME = "Service";

    public SimpleRestStrategy(OAuthRequest request, boolean  mustSign){
        mMockedRequest = request;
        mMustSign = mustSign;
    }

    protected SimpleRestStrategy(Parcel in) {
    }


    @Override
    public String getOAuthSigner() {
        if(mMustSign)
            return SERVICE_NAME;
        else
            return null;
    }

    @Override
    public String getUrl() {
        return "www.google.com";
    }

    @Override
    public Verb getVerb() {
        return null;  //TODO Autogenerated
    }

    @Override
    public void processHttpResult(Response result, Context context) throws ResultParseException {
        mResultString = result.getBody();
    }

    @Override
    public void addParamsToRequest(OAuthRequest request) {
        request.addHeader("Key", "Value");
    }

    @Override
    public int describeContents() {
        return 0;  //TODO Autogenerated
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        //TODO Autogenerated
    }

    public String getResultString(){
        return mResultString;
    }
}
