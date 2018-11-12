package com.mugua.enterprise.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Administrator on 2017/5/16.
 */

public class ImageAsyncTask {

    private String url;
    private OnCloseListener listener;
    public ImageAsyncTask(String url)
    {
        this.url = url;
    }

    public void data(OnCloseListener listener)
    {
        this.listener = listener;
        new DownloadImageTask().execute(url);
    }

    private InputStream OpenHttpConnection (String urlString) throws IOException
    {
        InputStream in = null;
        int response = -1;
        URL url = new URL(urlString);
        URLConnection conn = url.openConnection();
        if(!(conn instanceof HttpURLConnection))throw new IOException ("Not an HTTP connection");
        try{
            HttpURLConnection httpConn = (HttpURLConnection)conn;
            httpConn.setAllowUserInteraction(false);
            httpConn.setInstanceFollowRedirects(true);
            httpConn.setRequestMethod("GET");
            httpConn.connect();
            response = httpConn.getResponseCode();
            if(response == HttpURLConnection.HTTP_OK) {
                in = httpConn.getInputStream();
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return in;
    }

    private Bitmap DownloadImage(String URL)
    {
        Bitmap bitmap = null;
        InputStream in = null;
        try {
            in = OpenHttpConnection(URL);
            bitmap = BitmapFactory.decodeStream(in);
            if(in != null)
                in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private class DownloadImageTask extends AsyncTask<String , Void , Bitmap>
    {
        @Override
        protected Bitmap doInBackground(String... urls) {
            // TODO Auto-generated method stub
            return DownloadImage(urls[0]);
        }

        protected void onPostExecute(Bitmap result)
        {

            if(result != null)
            {
                listener.onClick(result);
            }
        }
    }

    public interface OnCloseListener{
        void onClick( Bitmap bitmap);
    }
}
