package nanang.application.id.iad;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import nanang.application.id.libs.CommonUtilities;
import nanang.application.id.libs.ConnectionDetector;
import nanang.application.id.libs.DatabaseHandler;
import nanang.application.id.model.aset;
import nanang.application.id.model.user;

public class AsetService extends Service {

    private static final String TAG = "AsetService";

    public static boolean is_running = false;
    Handler handler_tracking = new Handler();

    static InputStream inStream;
    static String upload_gambar, upload_info, upload_more;
    static int upload_status, upload_size;

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();

    }

    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent.getAction().equals(Constants.ACTION.STARTFOREGROUND_ACTION)) {

            user data_user = CommonUtilities.getLoginUser(getApplicationContext());
            handler_tracking.postDelayed(mTrackingTimeTask, 1000*20);
            Intent notificationIntent = new Intent(this, MainActivity.class);
            notificationIntent.setAction(Constants.ACTION.MAIN_ACTION);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                    notificationIntent, 0);

            Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
            Notification notification = new NotificationCompat.Builder(this)
                    .setContentTitle("SeMUTSoft App")
                    //.setTicker("TutorialsFace Music Player")
                    .setContentText("Trucking "+CommonUtilities.toTitleCase(data_user.getDesa().toLowerCase()))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                    .setContentIntent(pendingIntent)
                    .setOngoing(true).build();

            is_running = true;
            startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, notification);

        } else if (intent.getAction().equals(Constants.ACTION.STOPFOREGROUND_ACTION)) {
            try {
                handler_tracking.removeCallbacks(mTrackingTimeTask);
            } catch (Exception e) {
                e.printStackTrace();
            }

            is_running = false;
            stopForeground(true);
            stopSelf();
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

        try {
            handler_tracking.removeCallbacks(mTrackingTimeTask);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Runnable mTrackingTimeTask = new Runnable() {
        public void run() {

            /*Intent i = new Intent("com.application.siapp.GCM_RESPONSE");
            i.putExtra("message", "CEK ASET!");
            sendBroadcast(i);*/

            ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
            if(cd.isConnectingToInternet()) {
                aset data = new DatabaseHandler(getApplicationContext()).getDataAset();
                if (data != null) {
                    try {
                        File file = new File ( data.getGambar() );
                        inStream = new BufferedInputStream( new FileInputStream( file ));
                        new prosesUploadChunkFile(data, 0, data.getGambar(), "../../../smc/file", "png|jpg").execute();

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        new submitAset(data, "").execute();
                    }
                } else {
                    handler_tracking.postDelayed(this, 1000 * 20);
                }
            } else {
                handler_tracking.postDelayed(this, 1000 * 20);
            }
        }
    };

    class submitAset extends AsyncTask<String, String, JSONObject> {

        aset data_aset;
        String file_name;

        submitAset(aset data_aset, String file_name) {
            this.data_aset = data_aset;
            this.file_name = file_name;
        }

        @Override
        protected JSONObject doInBackground(String... arg0) {
            try {

                String url = CommonUtilities.SERVER_HOME_URL + "/smc/android_upload_text_sekolah.php";
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(url);

                user data_user = CommonUtilities.getLoginUser(getApplicationContext());
                MultipartEntity reqEntity = new MultipartEntity();
                reqEntity.addPart("user_id", new StringBody(data_user.getId()+""));
                reqEntity.addPart("gambar", new StringBody(file_name));
                reqEntity.addPart("nama", new StringBody(data_aset.getJenisbarang()));
                reqEntity.addPart("lat", new StringBody(data_aset.getKodebarang()));
                reqEntity.addPart("lang", new StringBody(data_aset.getIdentitasbarang()));

                httppost.setEntity(reqEntity);
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity resEntity = response.getEntity();
                InputStream is = resEntity.getContent();

                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;


                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();
                String json = sb.toString();
                System.out.println(json);

                return new JSONObject(json);

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(JSONObject result) {

            boolean success = false;
            try {
                success = result.isNull("success")?false:result.getBoolean("success");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
            if(success) {
                new DatabaseHandler(getApplicationContext()).deleteAsetlist(data_aset.getId()+"");
                handler_tracking.postDelayed(mTrackingTimeTask, 1000);
            } else if(cd.isConnectingToInternet()) {
                new submitAset(data_aset, file_name).execute();
            } else {
                handler_tracking.postDelayed(mTrackingTimeTask, 1000 * 20);
            }
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    class prosesUploadChunkFile extends AsyncTask<String, Void, JSONObject> {

        aset data;
        int CHUNK_SIZE = 1024 * 1024 * 1; // 1MB
        String pathFile;
        String destination;
        String ext;
        int start;
        int end;
        int file_size;

        prosesUploadChunkFile(aset data, int start, String pathFile, String destination, String ext) {
            this.pathFile = pathFile;
            this.start = start;
            this.destination = destination;
            this.ext = ext;
            this.data = data;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... urls) {
            JSONObject result = null;
            String url = "http://nanangprogrammer.000webhostapp.com/server/server/services/publik/upload.php";
            File file = new File ( pathFile );
            String file_name = file.getName();
            file_size = (int) file.length();
            end = (start+CHUNK_SIZE)>=file_size?file_size:(start+CHUNK_SIZE);

            try {

                byte[] temporary = new byte[end-start]; //Temporary Byte Array
                inStream.read(temporary, 0, end-start);

                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(url);
                MultipartEntity reqEntity = new MultipartEntity();

                ByteArrayInputStream arrayStream = new ByteArrayInputStream(temporary);
                reqEntity.addPart("ax_file_input", new InputStreamBody(arrayStream, pathFile));
                reqEntity.addPart("ax-file-path", new StringBody(destination));
                reqEntity.addPart("ax-allow-ext", new StringBody(ext==null?"":ext));
                reqEntity.addPart("ax-file-name", new StringBody(file_name));
                reqEntity.addPart("ax-max-file-size", new StringBody("10G"));
                reqEntity.addPart("ax-start-byte", new StringBody(end+""));
                reqEntity.addPart("ax-last-chunk", new StringBody(end==file_size?"true":"false"));

                httppost.setEntity(reqEntity);
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity resEntity = response.getEntity();

                InputStream is = resEntity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();

                Log.i("UPLOAD CHUNK", sb.toString());
                result = new JSONObject(sb.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            upload_gambar = "";
            upload_status = 0;
            upload_size = 0;
            upload_info = "";
            upload_more = "";

            if(result!=null) {
                try {
                    upload_gambar = result.isNull("name")?upload_gambar:result.getString("name");
                    upload_status = result.isNull("status")?upload_status:result.getInt("status");
                    upload_size   = result.isNull("size")?upload_size:result.getInt("size");
                    upload_info   = result.isNull("info")?upload_info:result.getString("info");
                    upload_more   = result.isNull("more")?upload_more:result.getString("more");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(end<file_size && upload_status==1) {
                    new prosesUploadChunkFile(data, end, pathFile, destination, ext).execute();
                } else {
                    try {
                        inStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if(upload_status!=1) {
                        handler_tracking.postDelayed(mTrackingTimeTask, 1000 * 20);
                    } else {
                        new submitAset(data, upload_gambar).execute();
                    }

                }

            } else {
                handler_tracking.postDelayed(mTrackingTimeTask, 1000 * 20);
            }
        }
    }
}