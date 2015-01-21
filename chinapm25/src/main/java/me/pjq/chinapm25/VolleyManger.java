package me.pjq.chinapm25;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

/**
 * Network resuqest/response manager, use {@link com.android.volley.toolbox.Volley}
 * <p/>
 * Created by pengjianqing on 1/9/15.
 */
public class VolleyManger {
    private static final String TAG = VolleyManger.class.getSimpleName();
    private static VolleyManger volleyManger;
    private static Context context = null;
    private RequestQueue requestQueue;

    VolleyManger() {
        requestQueue = Volley.newRequestQueue(context);
    }

    public void setDebug(boolean isDebug){
        VolleyLog.DEBUG = isDebug;
    }

    public static void init(Context c) {
        context = c;
    }

    public static VolleyManger getInstance() {
        if (null == volleyManger) {
            volleyManger = new VolleyManger();
        }

        return volleyManger;
    }

    public void getPM25(final OnResponse<String> onResponse){
        String url = "http://ef.pjq.me/download/pm25/all_city/pm25_all.txt";
        getString(url, onResponse);
    }

    /**
     * Send http get to url
     *
     * @param url
     * @param onResponse
     */
    public void get(final String url, final OnResponse onResponse) {
        EFLogger.i(TAG, "get url = " + url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                EFLogger.i(TAG, url + ", response = " + response.toString());

                onResponse.onResponse(null, response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                EFLogger.i(TAG, url + ", error = " + error);
                onResponse.onResponse(error, null);
            }
        });

        addTimeout(request);
        requestQueue.add(request);
    }

    /**
     * Send http get to url
     *
     * @param url
     * @param onResponse
     */
    public void getString(final String url, final OnResponse<String> onResponse) {
        EFLogger.i(TAG, "get url = " + url);
        StringReq request = new StringReq(Request.Method.GET, url,  new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                EFLogger.i(TAG, url + ", response = " + response.toString());

                onResponse.onResponse(null, response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                EFLogger.i(TAG, url + ", error = " + error);
                onResponse.onResponse(error, null);
            }
        });

        request.setCharset("utf-8");
        addTimeout(request);
        requestQueue.add(request);
    }

    public void post(final String url, JSONObject body, final OnResponse onResponse){
        EFLogger.i(TAG, "get url = " + url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, body, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                EFLogger.i(TAG, url + ", response = " + response.toString());

                onResponse.onResponse(null, response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                EFLogger.i(TAG, url + ", error = " + error);
                onResponse.onResponse(error, null);
            }
        });

        addTimeout(request);
        requestQueue.add(request);
    }


    private void addTimeout(Request request) {
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        request.setRetryPolicy(policy);
    }

    public void destroy() {
        requestQueue.cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                return true;
            }
        });
        requestQueue.stop();
        requestQueue = null;
    }

    public interface OnResponse<T> {
        public void onResponse(Object error, T t);
    }
}
