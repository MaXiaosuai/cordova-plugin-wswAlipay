package ma.xiaoshuai.cordova.wswAlipay;

import android.annotation.SuppressLint;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.alipay.sdk.app.AuthTask;
import com.alipay.sdk.app.PayTask;

import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.os.Handler;

import java.util.Map;


/**
 * This class echoes a string called from JavaScript.
 */
public class wswAlipay extends CordovaPlugin {
    protected static CallbackContext currentCallbackContext;
    public static final String TAG = "Cordova.Plugin.wswAlipay";
    public static final String ERROR_INVALID_PARAMETERS = "参数格式错误";
    final static int SDK_PAY_FLAG = 1001;
    final static int SDK_AUTH_FLAG = 2;

  CallbackContext callbackContext;
    @Override
    public boolean execute(String action, CordovaArgs args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("aliLogin")) {
            return aliLogin(args, callbackContext);
        } else if (action.equals("aliPay")) {
            return aliPay(args, callbackContext);
        }else if (action.equals("jumpPay")) {
          return jumpPay(args, callbackContext);
        }

        return false;
    }

    @SuppressLint("LongLogTag")
    protected boolean aliLogin(CordovaArgs args, CallbackContext callbackContext) {
      JSONObject params = null;
      currentCallbackContext = callbackContext;
      try {
        params = args.getJSONObject(0);
        String sign = params.getString("sign");
        Log.i(TAG, "aliLogin request has been sent successfully.");
        Runnable authRunnable = new Runnable() {
          @Override
          public void run() {
            // 构造AuthTask 对象
            AuthTask authTask = new AuthTask(cordova.getActivity());
            // 调用授权接口
            // AuthTask#authV2(String info, boolean isShowLoading)，
            // 获取授权结果。
            Map<String, String> result = authTask.authV2(sign, true);
            // 将授权结果以 Message 的形式传递给 App 的其它部分处理。
            // 对授权结果的处理逻辑可以参考支付宝 SDK Demo 中的实现。
            Message msg = new Message();
            msg.what = SDK_AUTH_FLAG;
            msg.obj = result;
            mAuthHandler.sendMessage(msg);
          }
        };
        Thread authThread = new Thread(authRunnable);
        authThread.start();

        return true;

      } catch (JSONException e) {
        e.printStackTrace();
        callbackContext.error(ERROR_INVALID_PARAMETERS);
        return  false;
      }
    }

  private static final Handler mAuthHandler = new Handler() {
    @SuppressWarnings("unused")
    public void handleMessage(Message msg) {
      switch (msg.what) {
        case SDK_AUTH_FLAG: {
          AuthResult authResult = new AuthResult((Map<String, String>) msg.obj, true);
          String resultStatus = authResult.getResultStatus();
          // 判断resultStatus 为“9000”且result_code
          // 为“200”则代表授权成功，具体状态码代表含义可参考授权接口文档
          if (TextUtils.equals(resultStatus, "9000") && TextUtils.equals(authResult.getResultCode(), "200")) {
            currentCallbackContext.success(authResult.getAuthCode());

          } else {
            // 其他状态值则为授权失败
            currentCallbackContext.error(ERROR_INVALID_PARAMETERS);
          }
          break;
        }
        default:
          break;
      }
    };
  };


      @SuppressLint("LongLogTag")
      protected boolean jumpPay(CordovaArgs args, CallbackContext callbackContext) {
        Log.i(TAG, "aliPay request has been sent successfully.");
        JSONObject params = null;
        currentCallbackContext = callbackContext;
        try {
          params = args.getJSONObject(0);
          String url = params.getString("url");
          String pamurl = params.getString("pamurl");

          String payInfo = URLEncoder.encode(pamurl);
          Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url+ "?" + payInfo));
          cordova.getContext().startActivity(intent);
          callbackContext.success("跳转支付宝小程序");

          return true;

        } catch (JSONException e) {
          e.printStackTrace();
          callbackContext.error(ERROR_INVALID_PARAMETERS);
          return  false;
        }


      }
     @SuppressLint("LongLogTag")
     protected boolean aliPay(CordovaArgs args, CallbackContext callbackContext) {
        Log.i(TAG, "aliPay request has been sent successfully.");
       JSONObject params = null;
       currentCallbackContext = callbackContext;
       try {
         params = args.getJSONObject(0);
         String sign = params.getString("sign");
         Runnable payRunnable = new Runnable() {

           @Override
           public void run() {
             PayTask alipay = new PayTask(cordova.getActivity());
             Map<String, String> result = alipay.payV2(sign, true);
             Message msg = new Message();
             msg.what = SDK_PAY_FLAG;
             msg.obj = result;
             mHandler.sendMessage(msg);

             Log.i(TAG, "aliresult--->" + result);
           }
         };
         // 必须异步调用
         Thread payThread = new Thread(payRunnable);
         payThread.start();

         return true;

       } catch (JSONException e) {
         e.printStackTrace();
         callbackContext.error(ERROR_INVALID_PARAMETERS);
         return  false;
       }
    }

  /**
   * 支付宝状态
   * 9000 订单支付成功
   * 8000 正在处理中，支付结果未知（有可能已经支付成功），请查询商户订单列表中订单的支付状态
   * 4000 订单支付失败
   * 5000 重复请求
   * 6001 用户中途取消
   * 6002 网络连接出错
   * 6004 支付结果未知（有可能已经支付成功），请查询商户订单列表中订单的支付状态
   * 其它   其它支付错误
   */
  private static final Handler mHandler = new Handler() {
    @SuppressLint("HandlerLeak")
    public void handleMessage(Message msg) {
      switch (msg.what) {
        case SDK_PAY_FLAG: {
          PayResult payResult = new PayResult((Map<String, String>) msg.obj);
          /**
           对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
           */
          String resultInfo = payResult.getResult();// 同步返回需要验证的信息
          String resultStatus = payResult.getResultStatus();
          // 判断resultStatus 为9000则代表支付成功
          if (TextUtils.equals(resultStatus, "9000")) {
            // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
            currentCallbackContext.success("支付成功");

          } else if (TextUtils.equals(resultStatus, "6001")) {
            // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
            currentCallbackContext.error("取消支付");

          } else {
            // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
            currentCallbackContext.error("支付失败");
          }
          break;
        }
      }
    }


  };


}
