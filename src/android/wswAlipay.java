package cordova-plugin-wswAlipay;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class echoes a string called from JavaScript.
 */
public class wswAlipay extends CordovaPlugin {

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("aliLogin")) {
            return aliLogin(args, callbackContext);
        } else if (action.equals("aliPay")) {
            return aliPay(args, callbackContext);
        } 

        return false;
    }

    protected boolean aliLogin(CordovaArgs args, CallbackContext callbackContext) {
       
        params = args.getJSONObject(0);

        Log.i(TAG, "aliLogin request has been sent successfully.");
        sendNoResultPluginResult(callbackContext);
        return true;
    }

     protected boolean aliPay(CordovaArgs args, CallbackContext callbackContext) {
        Log.i(TAG, "aliPay request has been sent successfully.");
        sendNoResultPluginResult(callbackContext);

        return true;
    }

      private void sendNoResultPluginResult(CallbackContext callbackContext) {
        // save current callback context
        currentCallbackContext = callbackContext;

        // send no result and keep callback
        PluginResult result = new PluginResult(PluginResult.Status.NO_RESULT);
        result.setKeepCallback(true);
        callbackContext.sendPluginResult(result);
    }
}
