package com.unicorn.csp.volley;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.unicorn.csp.utils.ToastUtils;
import com.unicorn.csp.volley.toolbox.VolleyErrorHelper;


public class MyVolley {

    private static RequestQueue mRequestQueue;
    private static ImageLoader mImageLoader;

    private MyVolley() {
        // no instances
    }

    static public void init(Context context) {
        mRequestQueue = Volley.newRequestQueue(context);

        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        mImageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> mCache = new LruCache<>(cacheSize);
            public void putBitmap(String url, Bitmap bitmap) {
                mCache.put(url, bitmap);
            }
            public Bitmap getBitmap(String url) {
                return mCache.get(url);
            }
        });

    }

    public static void addRequest(Request request){

        getRequestQueue().add(request);
    }

    public static RequestQueue getRequestQueue() {
        if (mRequestQueue != null) {
            return mRequestQueue;
        } else {
            throw new IllegalStateException("RequestQueue not initialized");
        }
    }

    public static ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader != null) {
            return mImageLoader;
        } else {
            throw new IllegalStateException("ImageLoader not initialized");
        }
    }

    public static Response.ErrorListener getDefaultErrorListener() {

        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                ToastUtils.show(VolleyErrorHelper.getErrorMessage(volleyError));
            }
        };
    }

}