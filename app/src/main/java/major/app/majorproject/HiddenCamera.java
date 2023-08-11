package major.app.majorproject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.*;
import android.hardware.Camera;
import android.os.*;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.Surface;
import android.view.TextureView;
import android.widget.FrameLayout;
import android.widget.Toast;
import com.android.volley.*;
import com.android.volley.toolbox.*;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Harnirvair Singh on 11/17/2016.
 */

public class HiddenCamera extends AppCompatActivity implements TextureView.SurfaceTextureListener {
    private static final String TAG = "";
    private Camera mCamera;
    private TextureView mTextureView;
    private String encoded_string, image_name,username;
    private Bitmap bitmap;
    private File file_uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTextureView = new TextureView(this);
        mTextureView.setSurfaceTextureListener(this);
        setContentView(mTextureView);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        mCamera =openFrontFacingCameraGingerbread();
       setCameraDisplayOrientation(this,1,mCamera);

        Camera.Size previewSize = mCamera.getParameters().getPreviewSize();
        Camera.Parameters params = mCamera.getParameters();
        //List<Camera.Size> supportedSizes = params.getSupportedPictureSizes();

        List sizes = params.getSupportedPictureSizes();
        Camera.Size result = null;
        for (int i=0;i<sizes.size();i++){
            result = (Camera.Size) sizes.get(i);
            Log.i("PictureSize", "Supported Size. Width: " + result.width + "height : " + result.height);
        }
        params.setJpegQuality(75);
        params.setPictureSize(640, 480);
       mCamera.setParameters(params);
              mTextureView.setLayoutParams(new FrameLayout.LayoutParams(
                640,480 , Gravity.CENTER));

        try {
            mCamera.setPreviewTexture(surface);
        } catch (IOException t) {
        }

        mCamera.startPreview();
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        mCamera.takePicture(null,null,jpegCallback);

                        // takepicture runs after 2 seconds
                    }
                },
                2000
        );
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
  return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }


    //FUNCTION For Normal Stability of Orientation of Camera
    public static void setCameraDisplayOrientation(Activity activity,
                                                   int cameraId, android.hardware.Camera camera) {

        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();

        android.hardware.Camera.getCameraInfo(cameraId, info);

        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;

        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }
    //FUNCTION for opening front Camera otherwise Back camera will open by Default
    private Camera openFrontFacingCameraGingerbread() {
        int cameraCount = 0;
        Camera cam = null;

        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();
        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                try {
                    cam = Camera.open(camIdx);
                } catch (RuntimeException e) {
                }
            }
        }

        return cam;
    }

    //Function to CLICK PHOTO (PICTURE CALLBACK)
    Camera.PictureCallback jpegCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera)
        {
            FileOutputStream outStream;
            File myDir;
            try {
                myDir = new File(getCacheDir(), "folder");
                myDir.mkdir();
                System.out.println("PATH"+myDir);
                // set your directory path here

                outStream = new FileOutputStream(myDir+"/img.jpg");
                outStream.write(data);
                outStream.close();
                /* WRITING DATE AND TIME */
                Bitmap src = BitmapFactory.decodeFile(myDir+"/img.jpg"); // the original file is cuty.jpg i added in resources
                Bitmap dest = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Bitmap.Config.ARGB_8888);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String dateTime = sdf.format(Calendar.getInstance().getTime()); // reading local time in the system

                Canvas cs = new Canvas(dest);
                Paint tPaint = new Paint();
                tPaint.setTextSize(15);
                tPaint.setColor(Color.YELLOW);
                tPaint.setStyle(Paint.Style.FILL);
                cs.drawBitmap(src, 0f, 0f, null);
                float height = tPaint.measureText("yY");
                cs.drawText("Captured by Android Phone Theft Security App on "+dateTime, 20f, height+2f, tPaint);
                try {
                    dest.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(new File(myDir+"/img.jpg")));
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                Log.d(TAG, "onPictureTaken - wrote bytes: " + data.length);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally
            {
                camera.stopPreview();
                camera.release();

                Toast.makeText(getApplicationContext(), "Image snapshot Done",Toast.LENGTH_LONG).show();

               new Encode_image().execute();
            }

            Log.d(TAG, "onPictureTaken - jpeg");


        }
    };

    //SENDING IMAGE ONLINE

    private class Encode_image extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            file_uri=new File(getCacheDir(),"folder/img.jpg");
        System.out.println("IN ENCODE IMAGE---"+file_uri);
            bitmap = BitmapFactory.decodeFile(file_uri.toString());
            System.out.println("IN ENCODE IMAGE---"+file_uri);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            bitmap.recycle();

            byte[] array = stream.toByteArray();
            encoded_string = Base64.encodeToString(array, 0);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            makeRequest();
        }
    }

    private void makeRequest() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, SaveSharedPreference.URL+"/savepicture.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String Time=String.valueOf(System.currentTimeMillis());
                image_name="IMG_"+Time+".jpg";

                HashMap<String,String> map = new HashMap<>();


                map.put("encoded_string",encoded_string);
                map.put("image_name",image_name);
                map.put("username",UserAreaActivity.username_global);
                System.out.println("USERNAME--"+UserAreaActivity.username_global);
                return map;
            }
        };
        requestQueue.add(request);
    }

}
