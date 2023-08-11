package major.app.majorproject;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;

public class About extends AppCompatActivity {



        public int currentimageindex=0;
        Timer timer;
        TimerTask task;
        ImageView slidingimage;

        int[] IMAGE_IDS = {R.drawable.android, R.drawable.mysql, R.drawable.php, R.drawable.java, R.drawable.json};

        @Override
        protected void onCreate(Bundle savedInstanceState)
        {
            // TODO Auto-generated method stub
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_about);
            TextView websiteURL=(TextView)findViewById(R.id.hyperlink_textview);
            final android.os.Handler mHandler= new android.os.Handler();
            getSupportActionBar().setTitle("About");
            // Create runnable for posting
            final Runnable mUpdateResults = new Runnable() {
                public void run() {

                    AnimateandSlideShow();

                }
            };

            int delay = 200; // delay for 1 sec.

            int period = 2000; // repeat every 4 sec.

            Timer timer = new Timer();

            timer.scheduleAtFixedRate(new TimerTask() {

                public void run() {

                    mHandler.post(mUpdateResults);

                }

            }, delay, period);

        }

        public void onClick(View v) {

            finish();
            android.os.Process.killProcess(android.os.Process.myPid());
        }
        private void AnimateandSlideShow() {

            slidingimage = (ImageView)findViewById(R.id.imageViewAnimate);
            slidingimage.setImageResource(IMAGE_IDS[currentimageindex%IMAGE_IDS.length]);

            currentimageindex++;

        }

    }

