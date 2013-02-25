package com.whiterabbit.postman.commands;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import com.whiterabbit.postman.ServerInteractionHelper;
import com.whiterabbit.postman.exceptions.PostmanException;
import com.whiterabbit.postman.exceptions.ResultParseException;
import com.whiterabbit.postman.oauth.OAuthHelper;
import com.whiterabbit.postman.oauth.OAuthServiceInfo;
import com.whiterabbit.postman.utils.Constants;
import org.scribe.exceptions.OAuthException;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Verb;


/**
 * Server command implementation intended to be used to interact with a rest server
 * @author fede
 *
 */
public class RestServerCommand extends ServerCommand  {
    private final RestServerRequest mFirstStrategy;
    private final Parcelable[] mStrategies; // must be a Parcelable[] instead of RestServerRequest[] because I wouldn't be
                                            // able to read it (can't cast Parcelable[] to RestServerRequest[] )

    /**
     * Constructor
     */
    public RestServerCommand(RestServerRequest firstStrategy, RestServerRequest... otherStrategies){
        mFirstStrategy = firstStrategy;
        mStrategies = otherStrategies;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(mFirstStrategy, 0);
        parcel.writeParcelableArray(mStrategies, 0);
    }

    protected RestServerCommand(Parcel in){
        mFirstStrategy = in.readParcelable(RestServerRequest.class.getClassLoader());
        mStrategies = in.readParcelableArray(RestServerRequest.class.getClassLoader());

    }

    public static final Creator<RestServerCommand> CREATOR
            = new Creator<RestServerCommand>() {
        public RestServerCommand createFromParcel(Parcel in) {
            return new RestServerCommand(in);
        }

        public RestServerCommand[] newArray(int size) {
            return new RestServerCommand[size];
        }
    };





    /**
     * Utility method to be used for mocking up request objects
     * inside unit tests
     * @param v
     * @param url
     * @return
     */
    protected OAuthRequest getRequest(Verb v, String url){
        return new OAuthRequest(v, url);
    }


	/**
	 * The real execution of the command. Performs the basic rest interaction
	 */
	@Override
	public void execute(Context c) {
        ServerInteractionHelper.getInstance(c).enableHttpResponseCache(c); // this looks to be the best place

        try{
            executeStrategy(mFirstStrategy, c);

            for(Parcelable p : mStrategies){
                executeStrategy((RestServerRequest)p, c);
            }
            notifyResult("Ok",  c);

        } catch (PostmanException e) {
            notifyError(e.getMessage(), c);
            return;
        }

    }


    private void executeStrategy(RestServerRequest s, Context c) throws PostmanException {
        try{
            OAuthRequest request = getRequest(s.getVerb(), s.getUrl());
            s.addParamsToRequest(request);
            String signer = s.getOAuthSigner();
            if(signer != null){
                OAuthServiceInfo authService  = OAuthHelper.getInstance().getRegisteredService(signer, c);
                authService.getService().signRequest(authService.getAccessToken(), request);
            }
            Response response = request.send();
            handleResponse(s, response.getCode(), response, c);
        }catch(OAuthException e){
               if(e.getCause().getMessage().equals("No authentication challenges found")){
                   // TODO Invalidate token ?
               }
               notifyError(e.getMessage(), c);
               return;
        }
    }



    private void handleResponse(RestServerRequest strategy, int statusCode, Response response, Context c) throws PostmanException {
        switch(statusCode){
            case 200:
                if(response != null){
                    try {
                        strategy.processHttpResult(response, c);
                    }catch(ResultParseException e){
                        notifyError("Failed to parse result " + e.getMessage(), c);
                        Log.e(Constants.LOG_TAG, "Result parse failed: " + response);
                    }
                }
            break;
            case 204:
            break;
            case 404:
                throw new PostmanException("Not found");
            case 401:
                throw new PostmanException("No permission");
                // TODO Invalidate token ??
            default:
                throw new PostmanException("Generic error " + statusCode);
        }
    }
}
