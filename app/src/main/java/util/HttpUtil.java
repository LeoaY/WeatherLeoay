package util;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by leoay on 2016/12/15.
 * 遍历全国省市县数据
 */

public class HttpUtil {

    public static void sendHttpRequest(final String address, final HttpCallbackListener httpCallbackListener){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection httpURLConnection = null;
                try{
                    URL url = new URL(address);
                    httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("GET");
                    httpURLConnection.setConnectTimeout(8000);
                    httpURLConnection.setReadTimeout(8000);
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while((line = bufferedReader.readLine()) != null){
                        stringBuilder.append(line);
                    }
                    if(httpCallbackListener != null){
                        //回调onFinish()方法
                        httpCallbackListener.onFinish(stringBuilder.toString());
                    }
                } catch (Exception e) {
                    if(httpCallbackListener != null){
                        //回调onError方法
                        httpCallbackListener.onError(e);
                    }
                } finally {
                    if(httpURLConnection != null){
                        httpURLConnection.disconnect();
                    }
                }
            }
        }).start();
    }
}
