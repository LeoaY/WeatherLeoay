package util;

/**
 * Created by leoay on 2016/12/15.
 */

public interface HttpCallbackListener {

    void onFinish(String response);
    void onError(Exception e);


}
