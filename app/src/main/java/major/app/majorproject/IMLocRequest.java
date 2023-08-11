package major.app.majorproject;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Harnirvair Singh on 10/26/2016.
 */

public class IMLocRequest extends StringRequest{
    private static final String LOGIN_REQUEST_URL=SaveSharedPreference.URL+"/location.php";
    private Map<String, String> params;
    public IMLocRequest(String username, String location, String latitude, String longitude,String imei, Response.Listener<String> listener){
        super(Request.Method.POST, LOGIN_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("username", username);
        params.put("latitude", latitude);
        params.put("longitude", longitude);
        params.put("location",location);
        params.put("imei",imei);
    }
    @Override
    public Map<String, String> getParams(){
        return params;
    }
}
