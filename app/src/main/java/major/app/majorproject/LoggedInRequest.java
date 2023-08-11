package major.app.majorproject;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Harnirvair Singh on 10/22/2016.
 */

public class LoggedInRequest extends StringRequest {
    private static final String LOGIN_REQUEST_URL=SaveSharedPreference.URL+"/loggedin.php";
    private Map<String, String> params;
    public LoggedInRequest(String username, Response.Listener<String> listener){
        super(Request.Method.POST, LOGIN_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("username", username);
    }
    @Override
    public Map<String, String> getParams(){
        return params;
    }
}
