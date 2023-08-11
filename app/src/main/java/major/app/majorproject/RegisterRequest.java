package major.app.majorproject;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import static major.app.majorproject.R.drawable.register;

/**
 * Created by Harnirvair Singh on 10/20/2016.
 */

public class RegisterRequest extends StringRequest{
    private static final String REGISTER_REQUEST_URL=SaveSharedPreference.URL+"/register.php";
    private Map<String, String> params;

    public RegisterRequest(String username,String password, String primaryno, String alternateno, String email, Response.Listener<String> listener){

        super(Method.POST, REGISTER_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("username", username);
        params.put("password",password);
        params.put("primaryno",primaryno);
        params.put("alternateno",alternateno);
        params.put("email",email);

    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
