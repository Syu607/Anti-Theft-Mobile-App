package major.app.majorproject;

import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.*;
import android.os.*;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.*;
import com.android.volley.toolbox.Volley;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static major.app.majorproject.R.id.textView;


public class UserAreaActivity extends AppCompatActivity {
    private LocationManager locationManager;
    private LocationListener locationListener;
    TextView welcomeMsg;
    private String imei="";
    private String latitude="";
    private String longitude = "";
    private String address = "";
    private String PhoneModel="";
    TextView lldetails;
    public static String username_global;
    private Button btnSendSMS;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


            setContentView(R.layout.activity_user_area);


        final TextView tvUsername = (TextView) findViewById(R.id.tvusername);
        final TextView tvPrimaryNo = (TextView) findViewById(R.id.tvprimarymobileNo);
        final TextView tvAlternateNo = (TextView) findViewById(R.id.tvAlternateMobile);
        final TextView tvEmail = (TextView) findViewById(R.id.tvEmail);
        final TextView tvIMEI = (TextView) findViewById(R.id.tvIMEI);
        final TextView tvPhoneModel = (TextView) findViewById(R.id.tvPhoneModel);
        final TextView tvWebsite= (TextView) findViewById(R.id.hyperlink_textview);
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        try {
            imei = telephonyManager.getDeviceId().toString();
            tvIMEI.setText(imei);
            PhoneModel=Build.MANUFACTURER+" "+Build.MODEL;
            tvPhoneModel.setText(PhoneModel);
        }
        catch(Exception E){
            E.printStackTrace();
            tvIMEI.setText("Permissions not granted.");
            tvPhoneModel.setText("Permissions not Granted");
        }
        welcomeMsg = (TextView) findViewById(R.id.tvWelcomeMessage);
        tvWebsite.setText(SaveSharedPreference.URL);
        tvWebsite.setSelected(true);
        lldetails=(TextView) findViewById(R.id.lldetails);
        lldetails.setMovementMethod(new ScrollingMovementMethod());
        Button logoutButton=(Button)findViewById(R.id.buttonLogOut);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveSharedPreference.setUserName(UserAreaActivity.this,"");
                Intent intent=new Intent(UserAreaActivity.this, MainActivity.class);
                startActivity(intent);

            }
        });

        //takePhoto();

        Intent intent = getIntent();
        username_global = intent.getStringExtra("username");
        final String primaryno = intent.getStringExtra("primaryno");
        final String alternateno = intent.getStringExtra("alternateno");
        String email=intent.getStringExtra("email");

        // Display user details
        final String message = username_global + ", Welcome to your User Area.";
        welcomeMsg.setText(message);
        btnSendSMS = (Button) findViewById(R.id.btnSendSMS);

        tvUsername.setText(username_global);
        tvPrimaryNo.setText(primaryno);
        tvAlternateNo.setText(alternateno);
        tvEmail.setText(email);
        btnSendSMS.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //sendSMS(alternateno,"Greetings "+username+"! Your IMEI Number is "+imei+" recorded from "+PhoneModel+". Your Location has also been recorded. You will get the updated Information on "+alternateno.toString()+".");
                /*SMS Will be sent Alternate Number*/
                SmsManager sms=SmsManager.getDefault();
                PendingIntent sentPI;
                String SENT="SMS_SENT";
                sentPI=PendingIntent.getBroadcast(getApplicationContext(),0,new Intent(SENT),0);
                String mymessage="Greetings "+username_global+"! Your IMEI Number is "+imei+" recorded from "+PhoneModel+". You will get the updated Information on "+alternateno.toString()+".";
                sms.sendTextMessage(alternateno,null,mymessage,sentPI,null);
                System.out.println(mymessage);
                System.out.println("Sending SMS");
            }
        });

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                lldetails.setText(gotLocation(location));
                latitude = String.valueOf(location.getLatitude());
                longitude = String.valueOf(location.getLongitude());
                //SET LOCATION in DATABASE
                address=gotLocation(location);
                //Store Values in the database (IMEI NO, Location, Latitude, Longitude)
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                    }
                };

                IMLocRequest registerRequest = new IMLocRequest(username_global,address,latitude,longitude, imei, responseListener);
                RequestQueue queue= Volley.newRequestQueue(UserAreaActivity.this);
                queue.add(registerRequest);



            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates("gps", 5000, 0, locationListener);

    //Store Values in the database (IMEI NO, Location, Latitude, Longitude)
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
            }
        };

        IMLocRequest registerRequest = new IMLocRequest(username_global,address,latitude,longitude, imei, responseListener);
        RequestQueue queue= Volley.newRequestQueue(UserAreaActivity.this);
        queue.add(registerRequest);


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
            Intent intent = new Intent(UserAreaActivity.this, About.class);
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void sendSMS(String phoneNumber, String message) {
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, null, null);
    }

    public String gotLocation(Location location) {
        if (location != null) {
            Geocoder gcd = new Geocoder(getApplicationContext(),
                       Locale.getDefault());
            List<Address> addresses;
            try {

                addresses = gcd.getFromLocation(location.getLatitude(),
                        location.getLongitude(), 1);

                if (addresses.size() > 0) {
                    String s = addresses.get(0).getAddressLine(0) + ", "
                            + addresses.get(0).getSubLocality() + ", "
                            + addresses.get(0).getSubAdminArea() + " - "
                            + addresses.get(0).getPostalCode() + "\n"
                            + "City: "
                            + addresses.get(0).getLocality() + "\n "
                            + "State: "
                            + addresses.get(0).getAdminArea() + "\n"
                            + "Country code: "
                            + addresses.get(0).getCountryCode() + "\n"
                            + "Country name: "
                            + addresses.get(0).getCountryName() + "\n"
                            + "\n";


                    return s;
                }

            } catch (IOException e) {

                e.printStackTrace();
                return "some Error";
            }

        }
        return "No string";
    }

    public void openHiddenCamera (View view){
        Intent intent=new Intent(UserAreaActivity.this, HiddenCamera.class);
        UserAreaActivity.this.startActivity(intent);
    }
    public void onBackPressed(){
        new AlertDialog.Builder(UserAreaActivity.this)
                .setTitle("Log Out?")
                .setMessage("Are you sure you want to Log Out?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        SaveSharedPreference.setUserName(UserAreaActivity.this,"");
                        Intent intent=new Intent(UserAreaActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
