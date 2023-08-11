package major.app.majorproject;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity  {
    Button b1, b2;
    TextView tv1;

    private static final String[] requiredPermissions = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.READ_PHONE_STATE
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        setContentView(R.layout.login_activity);

        if (Build.VERSION.SDK_INT > 22 && !hasPermissions(requiredPermissions)) {
           /*Camera and phone permissions not granted*/
            goToSettings();
            System.exit(1);
        }

        final EditText etUsername = (EditText) findViewById(R.id.etLoginUsername);
        final EditText etPassword = (EditText) findViewById(R.id.etLoginPassword);
        final Button bLogin = (Button) findViewById(R.id.bLogin);
        final TextView registerLink = (TextView) findViewById(R.id.tvRegister);
        final CheckBox rememberMeChecked=(CheckBox) findViewById(R.id.checkBox2);
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        TextView noConnectionWarning = (TextView) findViewById(R.id.noConnectionWarning);

        try{
        if (networkInfo.isConnected()) {
            noConnectionWarning.setVisibility(View.INVISIBLE);


            if (SaveSharedPreference.getUserName(MainActivity.this).length() != 0) {
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                           Toast.makeText(getApplicationContext(),"Welcome Back "+SaveSharedPreference.getUserName(MainActivity.this)+"!   ",Toast.LENGTH_SHORT).show();

                            JSONObject jsonResponse = new JSONObject(response);

                            //String name = jsonResponse.getString("name");
                            String primaryno = jsonResponse.getString("primaryno");
                            String alternateno = jsonResponse.getString("alternateno");
                            String email = jsonResponse.getString("email");
                            Intent intent = new Intent(MainActivity.this, UserAreaActivity.class);
                            //intent.putExtra("name", name);
                            intent.putExtra("primaryno", primaryno);
                            intent.putExtra("alternateno", alternateno);
                            intent.putExtra("email", email);
                            intent.putExtra("username", SaveSharedPreference.getUserName(MainActivity.this));


                            MainActivity.this.startActivity(intent);

                        } catch (Exception E) {
                                Toast.makeText(getApplicationContext(),"ERRORRRRRRRR",Toast.LENGTH_SHORT).show();
                            E.printStackTrace();
                        }
                    }
                };
                LoggedInRequest loggedInRequest = new LoggedInRequest(SaveSharedPreference.getUserName(MainActivity.this), responseListener);
                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                queue.add(loggedInRequest);
            }

            registerLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent registerIntent = new Intent(MainActivity.this, RegisterActivity.class);
                    MainActivity.this.startActivity(registerIntent);
                }
            });
            
            bLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final String username = etUsername.getText().toString();
                    final String password = etPassword.getText().toString();
                    if (username.equals("")) {
                        etUsername.setError("Please enter this field.");
                    } else if (password.equals("")) {
                        etPassword.setError("Please enter this field.");
                    } else {
                        Response.Listener<String> responseListener = new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonResponse = new JSONObject(response);
                                    boolean success = jsonResponse.getBoolean("success");
                                    if (success) {
                                        if(rememberMeChecked.isChecked()==true) {
                                            SaveSharedPreference.setUserName(MainActivity.this, username);
                                        }
                                        //String name = jsonResponse.getString("name");
                                        String primaryno = jsonResponse.getString("primaryno");
                                        String alternateno = jsonResponse.getString("alternateno");
                                        String email = jsonResponse.getString("email");
                                        Intent intent = new Intent(MainActivity.this, UserAreaActivity.class);
                                        //intent.putExtra("name", name);
                                        intent.putExtra("primaryno", primaryno);
                                        intent.putExtra("alternateno", alternateno);
                                        intent.putExtra("email", email);
                                        intent.putExtra("username", username);
                                        MainActivity.this.startActivity(intent);
                                    } else {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                        builder.setMessage("Login Failed").setNegativeButton("Retry", null).create().show();
                                    }

                                } catch (Exception E) {

                                }
                            }
                        };
                        LoginRequest loginRequest = new LoginRequest(username, password, responseListener);
                        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                        queue.add(loginRequest);
                    }
                }
            });

        }

    }catch(Exception E){
            registerLink.setEnabled(false);
            bLogin.setEnabled(false);
            etUsername.setEnabled(false);
            etPassword.setEnabled(false);
            noConnectionWarning.setVisibility(View.VISIBLE);
            System.out.println(E.getStackTrace());

        }
    }

    private void goToSettings() {
        Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
        myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
        myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(myAppSettings);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public boolean hasPermissions(@NonNull String... permissions) {
        for (String permission : permissions)
            if (PackageManager.PERMISSION_GRANTED != checkSelfPermission(permission))
                return false;
        return true;
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
            Intent intent = new Intent(MainActivity.this, About.class);
            startActivity(intent);

            return true;
        }
        if(id==R.id.home){
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);

            super.onBackPressed();

        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
   }

}
