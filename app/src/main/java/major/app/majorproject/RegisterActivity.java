package major.app.majorproject;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class RegisterActivity extends AppCompatActivity {


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            setContentView(R.layout.register_activity);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Sign Up");
            final EditText etUsername = (EditText) findViewById(R.id.etUsername);
            final EditText etPassword = (EditText) findViewById(R.id.etPassword);
            final EditText etPrimaryNo = (EditText) findViewById(R.id.etMainNo);
            final EditText etAlternateNo = (EditText) findViewById(R.id.etAltNo);
            final EditText etEmail = (EditText) findViewById(R.id.etEmail);
            final Button bRegister = (Button) findViewById(R.id.bRegisterNew);
            bRegister.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final String username = etUsername.getText().toString();
                    final String password = etPassword.getText().toString();
                    final String primaryno = etPrimaryNo.getText().toString();
                    final String alternateno = etAlternateNo.getText().toString();
                    final String email = etEmail.getText().toString();
                    if(username.equals("")){
                        etUsername.setError("Please fill in the Username");
                    }else if(password.equals("")){
                        etPassword.setError("Please fill in the Password");
                     }
                    else if(primaryno.equals("")){
                        etPrimaryNo.setError("Please fill in your Primary no");
                    }
                    else if(alternateno.equals("")){
                        etAlternateNo.setError("Please fill in your Alternate no");
                    }
                    else if(email.equals("")){
                        etEmail.setError("Please fill in your Email ID");
                    }
                    else if(!isEmailValid(email)){
                        etEmail.setError("Invalid Email ID");
                    }
                    else if(!TextUtils.isDigitsOnly(primaryno) || primaryno.length()<10){
                        etPrimaryNo.setError("Invalid Number");
                    }
                    else if(!TextUtils.isDigitsOnly(alternateno) || alternateno.length()<10){
                        etAlternateNo.setError("Invalid Number");
                    }
                    else{
                        Response.Listener<String> responseListener = new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonResponse = new JSONObject(response);
                                    boolean success = jsonResponse.getBoolean("success");
                                    boolean userexists = true;

                                    userexists = jsonResponse.getBoolean("userexists");

                                    if (userexists == true && success == false) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                        builder.setMessage("This username already exists.").setNegativeButton("Ok", null).create().show();
                                    }
                                    if (success) {
                                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                        RegisterActivity.this.startActivity(intent);
                                        Toast.makeText(getApplicationContext(),"Registration Successful!",Toast.LENGTH_SHORT).show();
                                    }

                                } catch (Exception E) {
                                }
                            }
                        };

                        RegisterRequest registerRequest = new RegisterRequest(username, password, primaryno, alternateno, email, responseListener);
                        RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                        queue.add(registerRequest);
                    }
                }
            });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.about) {
            Intent intent = new Intent(RegisterActivity.this, About.class);
            startActivity(intent);

            return true;
        }
        if(id==android.R.id.home){
            this.finish();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }
    //Check email is valid or not
    public static boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }
}
