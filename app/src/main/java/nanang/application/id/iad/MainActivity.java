package nanang.application.id.iad;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Toast;



import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;

import nanang.application.id.customfonts.MyTextView;
import nanang.application.id.fragment.DashboardFragment;
import nanang.application.id.fragment.DownloadFragment;
import nanang.application.id.fragment.EditAsetFragment;
import nanang.application.id.fragment.EditDesaFragment;
import nanang.application.id.fragment.HomeFragment;
import nanang.application.id.fragment.IsianAsetFragment;
import nanang.application.id.fragment.LoginFragment;
import nanang.application.id.fragment.ManageFragment;
import nanang.application.id.fragment.ManageLokalFragment;
import nanang.application.id.fragment.PengaturanFragment;
import nanang.application.id.fragment.RegistrasiFragment;
import nanang.application.id.fragment.TentangFragment;
import nanang.application.id.libs.CommonUtilities;
import nanang.application.id.libs.ConnectionDetector;
import nanang.application.id.libs.DatabaseHandler;
import nanang.application.id.libs.GalleryFilePath;
import nanang.application.id.model.aset;
import nanang.application.id.model.user;
import nanang.application.id.semutsoft.LocationHelper;

public class MainActivity extends AppCompatActivity {

    final int REQUEST_FROM_GALLERY = 1;
    final int REQUEST_FROM_CAMERA = 2;
    final int REQUEST_pdf = 11;
    final int REQUEST_FROM_LOGIN = 3;
    private String KEY_IMAGE = "image";
    private String KEY_NAME = "name";
    private String KEY_QTY = "qty";
    private String KEY_LAT = "lat";
    private String KEY_LNG = "long";
    public double lat, lng;
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    static String tag_json_obj = "json_obj_req";
    private LocationHelper locationHelper;

    public Context context,appContext;
    ConnectionDetector cd;
    DatabaseHandler db;
    user data;

    Typeface fonts1, fonts2;

    Dialog dialog_logout;
    MyTextView btn_no, btn_yes;

    Dialog dialog_informasi;
    MyTextView btn_ok;
    MyTextView text_title;
    MyTextView text_message;

    Dialog dialog_pilih_gambar;
    MyTextView from_camera, from_galery, from_pdf;

    String mImageCapturePath;
    String id_selected_aset;
    public static String action_add;

    Dialog dialog_delete_aset;
    MyTextView btn_delete_aset_no, btn_delete_aset_yes;

    public static final int progress_bar_type = 0;
    private ProgressDialog pDialog;

    public static int menu_selected = 0;
    static String upload_gambar, upload_info, upload_more;
    static int upload_status, upload_size;

    static InputStream inStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appContext = getApplicationContext(); // correct
        context = MainActivity.this;
        cd = new ConnectionDetector(context);
        db = new DatabaseHandler(context);
        db.createTable();

        setContentView(R.layout.activity_main);
        data = CommonUtilities.getLoginUser(context);

        fonts1 = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Regular.ttf");
        fonts2 = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Semibold.ttf");

        dialog_informasi = new Dialog(context);
        dialog_informasi.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_informasi.setCancelable(true);
        dialog_informasi.setContentView(R.layout.informasi_dialog);

        btn_ok = (MyTextView) dialog_informasi.findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dialog_informasi.dismiss();
            }
        });
        btn_ok.setTypeface(fonts2);

        text_title = (MyTextView) dialog_informasi.findViewById(R.id.text_title);
        text_message = (MyTextView) dialog_informasi.findViewById(R.id.text_dialog);
        text_message.setTypeface(fonts1);

        dialog_pilih_gambar = new Dialog(context);
        dialog_pilih_gambar.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_pilih_gambar.setCancelable(true);
        dialog_pilih_gambar.setContentView(R.layout.pilih_gambar_dialog);

        from_galery = (MyTextView) dialog_pilih_gambar.findViewById(R.id.txtFromGalley);
        from_galery.setTypeface(fonts1);
        from_galery.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                dialog_pilih_gambar.dismiss();
                fromGallery();
            }
        });

        from_camera = (MyTextView) dialog_pilih_gambar.findViewById(R.id.txtFromCamera);
        from_camera.setTypeface(fonts1);
        from_camera.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                dialog_pilih_gambar.dismiss();
                fromCamera();
            }
        });


        from_pdf = (MyTextView) dialog_pilih_gambar.findViewById(R.id.txtPDFDownload);
        from_pdf.setTypeface(fonts1);
        from_pdf.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dialog_pilih_gambar.dismiss();
                displayView(11);

            }
        });

        dialog_logout = new Dialog(context);
        dialog_logout.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_logout.setCancelable(true);
        dialog_logout.setContentView(R.layout.signout_dialog);

        btn_yes = (MyTextView) dialog_logout.findViewById(R.id.btn_yes);
        btn_yes.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dialog_logout.dismiss();

                CommonUtilities.setLoginUser(context, new user(0, "", "", "", "", "", "", "", "", "", "", "", ""));
                data = CommonUtilities.getLoginUser(context);
                displayView(0);

                boolean currentlyTracking = AsetService.is_running;
                if(currentlyTracking) {
                    stopTracking();
                }
            }
        });
        btn_yes.setTypeface(fonts2);

        btn_no = (MyTextView) dialog_logout.findViewById(R.id.btn_no);
        btn_no.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dialog_logout.dismiss();

            }
        });
        btn_no.setTypeface(fonts2);


        dialog_delete_aset = new Dialog(context);
        dialog_delete_aset.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_delete_aset.setCancelable(true);
        dialog_delete_aset.setContentView(R.layout.delete_aset_dialog);

        btn_delete_aset_yes = (MyTextView) dialog_delete_aset.findViewById(R.id.btn_yes);
        btn_delete_aset_yes.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dialog_delete_aset.dismiss();
                if(action_add.equalsIgnoreCase("server")) {
                    new prosesDeleteAset().execute();
                } else {
                    db.deleteAsetlist(id_selected_aset);
                    Toast.makeText(context, "Hapus data lokal berhasil.", Toast.LENGTH_LONG).show();
                    loadDataAsetLokal();
                }
            }
        });
        btn_delete_aset_yes.setTypeface(fonts2);

        btn_delete_aset_no = (MyTextView) dialog_delete_aset.findViewById(R.id.btn_no);
        btn_delete_aset_no.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dialog_delete_aset.dismiss();

            }
        });
        btn_delete_aset_no.setTypeface(fonts2);

        if(data.getId()==0) {
            displayView(0);
        } else {
            displayView(3);
        }

        boolean currentlyTracking = AsetService.is_running;
        if (data.getId()>0 && !currentlyTracking) {
            startTracking();
        }

        if(data.getId()==0 && currentlyTracking) {
            stopTracking();
        }
    }

    public void selectImage() {
        dialog_pilih_gambar.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog_pilih_gambar.show();
    }

    /*private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().start(this);
    }*/

    private void fromGallery() {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_FROM_GALLERY);
    }

    private void fromCamera() {

        Intent intent = new Intent(context, AmbilFotoActivity.class);
        startActivityForResult(intent, REQUEST_FROM_CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_FROM_CAMERA:
                    if(action_add.equalsIgnoreCase("server")) {
                        prosesUpload(data.getStringExtra("path"));
                    } else {
                        mImageCapturePath = data.getStringExtra("path");
                        displayView(4);
                    }


                    break;
                case REQUEST_FROM_GALLERY:
                    Uri selectedUri = data.getData();
                    if(action_add.equalsIgnoreCase("server")) {
                        prosesUpload(GalleryFilePath.getPath(context, selectedUri));
                    } else {
                        mImageCapturePath = GalleryFilePath.getPath(context, selectedUri);
                        displayView(4);
                    }

                    break;

                case REQUEST_pdf:
                    displayView(11);
                    break;
            }
        }
    }

    public void loadDataDesa() {
        EditDesaFragment.webView.loadUrl("javascript:setDataDesa('" + data.getKecamatan() + "', '" + data.getDesa() + "', '" + data.getLokal() + "', '" + data.getKades() + "', '" + data.getSekdes() + "', '" + data.getPengurus() + "', '" + data.getAlamatdesa() + "');");
    }

    public void addLokalAset() {
        action_add = "lokal";
        selectImage();
    }

    public void editLokalAset(String id) {
        action_add = "lokal";
        id_selected_aset = id;
        displayView(6);
    }

    public void deleteLokalAset(String id) {
        action_add = "lokal";
        id_selected_aset = id;
        dialog_delete_aset.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog_delete_aset.show();
    }


    public void addAset() {
        if(cd.isConnectingToInternet()) {
            action_add = "server";
            selectImage();
        } else {
            text_message.setText("Tidak ada koneksi internet.");
            text_title.setText("KESALAHAN");
            dialog_informasi.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog_informasi.show();
        }
    }

    public void editAset(String id) {
        action_add = "server";
        id_selected_aset = id;
        displayView(6);
    }

    public void deleteAset(String id) {
        action_add = "server";
        id_selected_aset = id;
        dialog_delete_aset.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog_delete_aset.show();
    }

    public void openDialogLogout() {
        dialog_logout.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog_logout.show();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

            if(menu_selected>3) {
                displayView(3);
            } else {
                setResult(RESULT_OK, new Intent());
                finish();
            }

            return false;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the form; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.form.menu_detail, form);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(RESULT_OK, new Intent());
                finish();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStop() {
        try {
            unregisterReceiver(mHandleloadGcmResponseReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        try {
            unregisterReceiver(mHandleloadGcmResponseReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onDestroy();
    }

    @Override
    protected void onResume() {

        registerReceiver(mHandleloadGcmResponseReceiver,  new IntentFilter("com.application.siapp.GCM_RESPONSE"));

        super.onResume();
    }

    private final BroadcastReceiver mHandleloadGcmResponseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");

            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        }
    };

    public void displayView(int position) {
        menu_selected = position;

        Fragment fragment = null;
        switch (position) {
            case 0:

                fragment = new LoginFragment();
                break;

            case 1:

                fragment = new RegistrasiFragment();
                break;

            case 3:

                //fragment = new HomeFragment();
                fragment = new DashboardFragment();
                break;

            case 4:

                fragment = new IsianAsetFragment();
                //fragment = new UploadFragment();
                break;

            case 5:

                fragment = new ManageFragment();
                break;

            case 6:

                fragment = new EditAsetFragment();
                break;

            case 7:

                fragment = new PengaturanFragment();
                break;

            case 8:

                fragment = new TentangFragment();
                break;

            case 9:

                fragment = new ManageLokalFragment();
                break;

            case 10:

                fragment = new EditDesaFragment();

                break;

            case 11:
                fragment = new DownloadFragment();
                break;

            case 12:
                //fragment = new DashboardFragment();
                fragment = new HomeFragment();
                break;
            default:

                break;
        }


        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
        }
    }

    public void prosesGantiPassword(String pass0, String pass1, String pass2) {
        if(!pass1.equalsIgnoreCase(pass2)) {
            text_message.setText("Silakan ulangi password baru.");
            text_title.setText("KESALAHAN");
            dialog_informasi.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog_informasi.show();
        } else {
            new prosesGantiPassword().execute(pass0, pass1, pass2);
        }
    }

    public  void gagalRegistrasi(String message) {
        text_message.setText(message);
        text_title.setText("GAGAL");
        dialog_informasi.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog_informasi.show();
    }

    class prosesGantiPassword extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            PengaturanFragment.load_masking.setVisibility(View.VISIBLE);
        }

        @Override
        protected JSONObject doInBackground(String... urls) {


            try {
                String url = CommonUtilities.SERVER_HOME_URL + "/server/services/publik/ganti_password.php";
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(url);

                MultipartEntity reqEntity = new MultipartEntity();
                reqEntity.addPart("pass0", new StringBody(urls[0]));
                reqEntity.addPart("pass1", new StringBody(urls[1]));
                reqEntity.addPart("pass2", new StringBody(urls[2]));
                reqEntity.addPart("action", new StringBody("setpass"));
                reqEntity.addPart("user", new StringBody(data.getEmail()));

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

                JSONObject jobj = new JSONObject("{\"result\": " + json + "}");
                JSONArray jarry = jobj.isNull("result") ? null : jobj.getJSONArray("result");
                if (jarry != null) {
                    return jarry.getJSONObject(0);
                }

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
            PengaturanFragment.load_masking.setVisibility(View.GONE);

            String error = "Proses ganti password gagal.";

            if(result!=null) {
                try {
                    error = result.isNull("error") ? "" : result.getString("error");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if(error.length()>0) {
                text_message.setText(error);
                text_title.setText("LOGIN GAGAL");
                dialog_informasi.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog_informasi.show();
            } else {
                Toast.makeText(context, "Proses Ganti Password Berhasil!", Toast.LENGTH_SHORT).show();
                displayView(3);
            }

        }
    }

    public void prosesLogin(String user_id, String password) {
        new prosesLogin().execute(user_id, password);
    }

    class prosesLogin extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            LoginFragment.load_masking.setVisibility(View.VISIBLE);
        }

        @Override
        protected JSONObject doInBackground(String... urls) {


            try {
                String url = CommonUtilities.SERVER_HOME_URL + "/server/services/publik/login.php";
                //String url = CommonUtilities.LOGIN_URL;

                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(url);

                httppost.addHeader("X-Api-Key" , CommonUtilities.API_KEY); //not implemented yet

                MultipartEntity reqEntity = new MultipartEntity();
                reqEntity.addPart("user", new StringBody(urls[0]));
                reqEntity.addPart("pass", new StringBody(urls[1]));
                reqEntity.addPart("action", new StringBody("getAuth"));

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

                JSONObject jobj = new JSONObject("{\"result\": " + json + "}");
                JSONArray jarry = jobj.isNull("result") ? null : jobj.getJSONArray("result");
                if (jarry != null) {
                    return jarry.getJSONObject(0);
                }

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
            LoginFragment.load_masking.setVisibility(View.GONE);

            String error = "Proses login gagal.";
            JSONObject user_login = null;

            if(result!=null) {
                try {
                    error = result.isNull("error")?error:result.getString("error");
                    user_login = result.isNull("0")?null:result.getJSONObject("0");

                    if(user_login!=null) {
                        int id = user_login.isNull("id")?0:user_login.getInt("id");
                        String nama = user_login.isNull("jeneng")?"":user_login.getString("jeneng");
                        String email = user_login.isNull("user")?"":user_login.getString("user");
                        String kecamatan = user_login.isNull("kecamatan")?"":user_login.getString("kecamatan");
                        String kabupaten = user_login.isNull("kabupaten")?"":user_login.getString("kabupaten");
                        String propinsi = user_login.isNull("provinsi")?"":user_login.getString("provinsi");
                        String desa = user_login.isNull("first_name")?"??":user_login.getString("first_name");
                        String lokal = user_login.isNull("lokal")?"":user_login.getString("lokal");
                        String kades = user_login.isNull("kades")?"":user_login.getString("kades");
                        String sekdes = user_login.isNull("sekdes")?"":user_login.getString("sekdes");
                        String pengurus = user_login.isNull("pengurus")?"":user_login.getString("pengurus");
                        String alamatdesa = user_login.isNull("alamatdesa")?"":user_login.getString("alamatdesa");
                        String photo = user_login.isNull("pass")?"":user_login.getString("pass");
                        CommonUtilities.toKEN = user_login.isNull("token")?"":user_login.getString("token");

                        SharedPreferences.Editor editor = getSharedPreferences("TokenPref", MODE_PRIVATE).edit();
                        editor.putString("token", CommonUtilities.toKEN);
                        editor.apply();


                        data = new user(id, nama, email, kecamatan, kabupaten, propinsi, desa, lokal, kades, sekdes, pengurus, alamatdesa, photo);
                        CommonUtilities.setLoginUser(context, data);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if(user_login==null) {
                text_message.setText(error);
                text_title.setText("LOGIN GAGAL");
                dialog_informasi.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog_informasi.show();
            } else {
                Toast.makeText(context, "Proses Login Berhasil!", Toast.LENGTH_SHORT).show();
                displayView(3);

                boolean currentlyTracking = AsetService.is_running;
                if (!currentlyTracking) {
                    startTracking();
                }
            }

        }
    }



    public void editDataAset() {
        if(action_add.equalsIgnoreCase("server")) {
            new editDataAset().execute();
        } else {
            aset data_aset = db.getDataAset(id_selected_aset);
            mImageCapturePath = data_aset.getGambar();
            EditAsetFragment.webView.loadUrl("javascript:setDataAset('" + data_aset.getJenisbarang() + "', '" + data_aset.getKodebarang() + "', '" + data_aset.getIdentitasbarang() + "',  '" + data_aset.getGambar() + "');");
        }
    }

    class editDataAset extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            EditAsetFragment.load_masking.setVisibility(View.VISIBLE);
        }

        @Override
        protected JSONObject doInBackground(String... urls) {

            try {
                String url = CommonUtilities.SERVER_HOME_URL + "/server/services/publik/load_text_sekolah_edit.php?id="+id_selected_aset;
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(url);

                MultipartEntity reqEntity = new MultipartEntity();
                reqEntity.addPart("action", new StringBody("getDetail"));

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
            String err_message = "Gagal edit data.";

            if(result!=null) {
                try {
                    err_message = "Data aset tidak ditemukan.";

                    JSONArray topics = result.isNull("topics")?null:result.getJSONArray("topics");
                    if(topics!=null) {
                        for(int i=0; i<topics.length(); i++) {
                            JSONObject rec = topics.getJSONObject(i);
                            int id = rec.isNull("id")?0:rec.getInt("id");
                            String jenisbarang = rec.isNull("jenisbarang") ? "" : rec.getString("jenisbarang");
                            String kodebarang = rec.isNull("kodebarang") ? "" : rec.getString("kodebarang");
                            String identitasbarang = rec.isNull("identitasbarang") ? "" : rec.getString("identitasbarang");
                            String jumlah_barang = rec.isNull("jumlah_barang") ? "" : rec.getString("jumlah_barang");
                            String apbdesa = rec.isNull("apbdesa") ? "" : rec.getString("apbdesa");
                            String lain = rec.isNull("lain") ? "" : rec.getString("lain");
                            String kekayaan = rec.isNull("kekayaan") ? "" : rec.getString("kekayaan");
                            String tanggal_asset = rec.isNull("tanggal_asset") ? "" : rec.getString("tanggal_asset");
                            String keterangan = rec.isNull("keterangan") ? "" : rec.getString("keterangan");
                            String gambar =  rec.isNull("logox") ? "" : rec.getString("logox");
                            if(id>0) {
                                err_message = "";
                                EditAsetFragment.webView.loadUrl("javascript:setDataAset('" + jenisbarang + "', '" + kodebarang + "', '" + identitasbarang + "', '" + jumlah_barang + "', '" + apbdesa + "', '" + lain + "', '" + kekayaan + "', '" + tanggal_asset + "', '" + keterangan + "', '"+CommonUtilities.SERVER_HOME_URL+"/smc/file/"+gambar+"');");
                            }
                            break;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            EditAsetFragment.load_masking.setVisibility(View.GONE);
            if(err_message.length()>0) {
                displayView(5);
                text_message.setText(err_message);
                text_title.setText("KESALAHAN");
                dialog_informasi.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog_informasi.show();
            }
        }
    }

    class prosesDeleteAset extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ManageFragment.load_masking.setVisibility(View.VISIBLE);
        }

        @Override
        protected JSONObject doInBackground(String... urls) {

            try {
                String url = CommonUtilities.SERVER_HOME_URL + "/server/services/publik/deletez.php";
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(url);

                MultipartEntity reqEntity = new MultipartEntity();
                reqEntity.addPart("id", new StringBody(id_selected_aset));
                reqEntity.addPart("action", new StringBody("'delete'"));

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

                JSONObject jobj = new JSONObject(json);

                return jobj;

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
            String message = "Proses hapus data gagal.";

            try {
                success = result.isNull("success")?false:result.getBoolean("success");
                message = result.isNull("message")?"":result.getString("message");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            ManageFragment.load_masking.setVisibility(View.GONE);
            if(success) {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                loadDataAset();
            } else {
                text_message.setText(message);
                text_title.setText(success?"BERHASIL":"GAGAL");
                dialog_informasi.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog_informasi.show();
            }
        }
    }


    public void prosesSaveAset(String jenisbarang, String kodebarang, String identitasbarang) {
        if(action_add.equalsIgnoreCase("server")) {
            new prosesSaveAset(jenisbarang, kodebarang, identitasbarang).execute();
        } else {
            aset data = new aset(0, jenisbarang, kodebarang, identitasbarang, mImageCapturePath);
            db.insertDataAset(data);
            Toast.makeText(context, "Simpan data lokal berhasil.", Toast.LENGTH_LONG).show();
            displayView(9);
        }

    }

    public class prosesSaveAset extends AsyncTask<String, Void, JSONObject> {
        String LNG, LAT;
        String jenisbarang;
        String kodebarang;
        String identitasbarang;

        prosesSaveAset(String jenisbarang, String lat, String lng) {
            this.jenisbarang = jenisbarang;
            this.kodebarang = lat;
            this.identitasbarang = lng;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            IsianAsetFragment.load_masking.setVisibility(View.VISIBLE);
        }

        @Override
        protected JSONObject doInBackground(String... urls) {

            try {

                locationHelper = new LocationHelper(getApplicationContext());
                if (locationHelper.isCanGetLocation()) {
                    if (locationHelper != null) {


                        lat = locationHelper.getLat();
                        lng = locationHelper.getLng();

                        LNG = String.valueOf(lng);
                        LAT = String.valueOf(lat);

                    }
                }

                String url = CommonUtilities.SERVER_HOME_URL + "/smc/android_upload_text_sekolah.php?lat="+lat+"&lng="+lng;
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(url);





                MultipartEntity reqEntity = new MultipartEntity();
                reqEntity.addPart("user_id", new StringBody(data.getId()+""));
                reqEntity.addPart("gambar", new StringBody(upload_gambar));
                reqEntity.addPart("nama", new StringBody(jenisbarang));
                reqEntity.addPart("lat", new StringBody(kodebarang));
                reqEntity.addPart("lang", new StringBody(identitasbarang));


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
            String message = "Proses kirim gagal.";

            try {
                success = result.isNull("success")?false:result.getBoolean("success");
                message = result.isNull("message")?"":result.getString("message");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            IsianAsetFragment.load_masking.setVisibility(View.GONE);
            if(success) {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                displayView(3);
            } else {
                text_message.setText(message);
                text_title.setText(success?"BERHASIL":"GAGAL");
                dialog_informasi.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog_informasi.show();
            }

        }
    }

    public void prosesEditAset(String jenisbarang, String kodebarang, String identitasbarang, String jumlah_barang, String apbdesa, String lain, String kekayaan, String tanggal_asset, String keterangan) {
        if(action_add.equalsIgnoreCase("server")) {
            new prosesEditAset(jenisbarang, kodebarang, identitasbarang, jumlah_barang, apbdesa, lain, kekayaan, tanggal_asset, keterangan).execute();
        } else {
            aset data = new aset(Integer.parseInt(id_selected_aset), jenisbarang, kodebarang, identitasbarang, mImageCapturePath);
            db.insertDataAset(data);
            Toast.makeText(context, "Update data lokal berhasil.", Toast.LENGTH_LONG).show();
            displayView(9);
        }

    }

    public class prosesEditAset extends AsyncTask<String, Void, JSONObject> {

        String jenisbarang;
        String kodebarang;
        String identitasbarang;
        String jumlah_barang;
        String apbdesa;
        String lain;
        String kekayaan;
        String tanggal_asset;
        String keterangan;

        prosesEditAset(String jenisbarang, String kodebarang, String identitasbarang, String jumlah_barang, String apbdesa, String lain, String kekayaan, String tanggal_asset, String keterangan) {
            this.jenisbarang = jenisbarang;
            this.kodebarang = kodebarang;
            this.identitasbarang = identitasbarang;
            this.jumlah_barang = jumlah_barang;
            this.apbdesa = apbdesa;
            this.lain = lain;
            this.kekayaan = kekayaan;
            this.tanggal_asset = tanggal_asset;
            this.keterangan = keterangan;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            EditAsetFragment.load_masking.setVisibility(View.VISIBLE);
        }

        @Override
        protected JSONObject doInBackground(String... urls) {

            try {
                String url = CommonUtilities.SERVER_HOME_URL + "/smc/android_upload_text_sekolah_edit.php";
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(url);

                MultipartEntity reqEntity = new MultipartEntity();
                reqEntity.addPart("user_id", new StringBody(data.getId()+""));
                reqEntity.addPart("id", new StringBody(id_selected_aset));
                reqEntity.addPart("jenisbarang", new StringBody(jenisbarang));
                reqEntity.addPart("kodebarang", new StringBody(kodebarang));
                reqEntity.addPart("identitasbarang", new StringBody(identitasbarang));

                reqEntity.addPart("jenisbarang", new StringBody(jenisbarang));
                reqEntity.addPart("kodebarang", new StringBody(kodebarang));
                reqEntity.addPart("identitasbarang", new StringBody(identitasbarang));

                reqEntity.addPart("jumlah_barang", new StringBody(jumlah_barang));
                reqEntity.addPart("apbdesa", new StringBody(apbdesa));
                reqEntity.addPart("lain", new StringBody(lain));

                reqEntity.addPart("kekayaan", new StringBody(kekayaan));
                reqEntity.addPart("tanggal_asset", new StringBody(tanggal_asset));
                reqEntity.addPart("keterangan", new StringBody(keterangan));


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
            String message = "Proses edit data aset gagal.";

            try {
                success = result.isNull("success")?false:result.getBoolean("success");
                message = result.isNull("message")?"":result.getString("message");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            EditAsetFragment.load_masking.setVisibility(View.GONE);
            if(success) {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                displayView(5);
            } else {
                text_message.setText(message);
                text_title.setText(success?"BERHASIL":"GAGAL");
                dialog_informasi.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog_informasi.show();
            }

        }
    }


    public void prosesUpdateDesa(String kecamatan, String desa, String lokal, String kades, String sekdes, String pengurus, String alamatdesa) {
        new prosesUpdateDesa(kecamatan, desa, lokal, kades, sekdes, pengurus, alamatdesa).execute();
    }

    public class prosesUpdateDesa extends AsyncTask<String, Void, JSONObject> {

        String kecamatan;
        String desa;
        String lokal;
        String kades;
        String sekdes;
        String pengurus;
        String alamatdesa;

        prosesUpdateDesa(String kecamatan, String desa, String lokal, String kades, String sekdes, String pengurus, String alamatdesa) {
            this.kecamatan = kecamatan;
            this.desa = desa;
            this.lokal = lokal;
            this.kades = kades;
            this.sekdes = sekdes;
            this.pengurus = pengurus;
            this.alamatdesa = alamatdesa;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            EditDesaFragment.load_masking.setVisibility(View.VISIBLE);
        }

        @Override
        protected JSONObject doInBackground(String... urls) {

            try {
                String url = CommonUtilities.SERVER_HOME_URL + "/smc/android_edit_desa.php";
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(url);

                MultipartEntity reqEntity = new MultipartEntity();
                reqEntity.addPart("user", new StringBody(data.getEmail()));
                reqEntity.addPart("kecamatan", new StringBody(kecamatan));
                reqEntity.addPart("desa", new StringBody(desa));
                reqEntity.addPart("lokal", new StringBody(lokal));
                reqEntity.addPart("kades", new StringBody(kades));
                reqEntity.addPart("sekdes", new StringBody(sekdes));
                reqEntity.addPart("pengurus", new StringBody(pengurus));
                reqEntity.addPart("alamatdesa", new StringBody(alamatdesa));

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
            String message = "Proses edit data aset gagal.";

            try {
                success = result.isNull("success")?false:result.getBoolean("success");
                message = result.isNull("message")?"":result.getString("message");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            EditDesaFragment.load_masking.setVisibility(View.GONE);
            if(success) {
                CommonUtilities.setLoginUser(context, new user(
                        data.getId(),
                        data.getNama(),
                        data.getEmail(),
                        kecamatan,
                        data.getKabupaten(),
                        data.getPropinsi(),
                        desa,
                        lokal,
                        kades,
                        sekdes,
                        pengurus,
                        alamatdesa,
                        data.getPhoto()
                ));

                data = CommonUtilities.getLoginUser(context);
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                displayView(3);
            } else {
                text_message.setText(message);
                text_title.setText(success?"BERHASIL":"GAGAL");
                dialog_informasi.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog_informasi.show();
            }

        }
    }

    public void loadDataAset() {
        new loadDataAset().execute();
    }

    public class loadDataAset extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ManageFragment.load_masking.setVisibility(View.VISIBLE);
            ManageFragment.webView.loadUrl("javascript:resetTabel();");
        }

        @Override
        protected JSONObject doInBackground(String... urls) {

            try {
                String url = CommonUtilities.SERVER_HOME_URL + "/server/services/publik/tabelDesa.php";
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(url);

                MultipartEntity reqEntity = new MultipartEntity();
                reqEntity.addPart("user", new StringBody(data.getEmail()));
                reqEntity.addPart("action", new StringBody("getDetail"));

                httppost.setEntity(reqEntity);
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity resEntity = response.getEntity();
                InputStream is = resEntity.getContent();

                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;


                while ((line = reader.readLine()) != null) {
                    //Log.i("Line", line+"\n");
                    sb.append(line + "\n");
                }
                is.close();
                String json = sb.toString();

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

            ManageFragment.load_masking.setVisibility(View.GONE);

            if (result!=null) {
                try {
                    JSONArray data = result.isNull("topics")?null:result.getJSONArray("topics");
                    if(data!=null) {
                        for(int i=0; i<data.length(); i++) {
                            JSONObject rec = data.getJSONObject(i);
                            int id = rec.isNull("id")?0:rec.getInt("id");
                            String jenisbarang = rec.isNull("jenisbarang")?"":rec.getString("jenisbarang");
                            String kodebarang = rec.isNull("kodebarang")?"":rec.getString("kodebarang");
                            String jumlah_barang = rec.isNull("jumlah_barang")?"":rec.getString("jumlah_barang");

                            ManageFragment.webView.loadUrl("javascript:setTabel('"+id+"','"+jenisbarang+"','"+kodebarang+"','"+jumlah_barang+"');");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                ManageFragment.webView.loadUrl("javascript:showTabel();");
            }

        }
    }




    public void loadDataAsetLokal() {
        ManageLokalFragment.webView.loadUrl("javascript:resetTabel();");
        for (aset data: db.getAsetlist()) {
            ManageLokalFragment.webView.loadUrl("javascript:setTabel('"+data.getId()+"','"+data.getJenisbarang()+"','"+data.getKodebarang()+"');");
        }
        ManageLokalFragment.webView.loadUrl("javascript:showTabel();");
    }

    public void prosesUpload(String pathFile) {
        try {
            File file = new File ( pathFile );
            int file_size = (int) file.length();
            inStream = new BufferedInputStream( new FileInputStream( file ));

            //webView.loadUrl("javascript:setProses('"+kolom_index+"', '"+upload_dest+"', 0, '"+file.getName()+"', "+file_size+", 1, 'Start Upload', '');");
            new prosesUploadChunkFile(0, pathFile, "../../../smc/file", "png|jpg").execute();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    class prosesUploadChunkFile extends AsyncTask<String, Void, JSONObject> {

        int CHUNK_SIZE = 1024 * 1024 * 1; // 1MB
        String pathFile;
        String destination;
        String ext;
        int start;
        int end;
        int file_size;

        prosesUploadChunkFile(int start, String pathFile, String destination, String ext) {
            this.pathFile = pathFile;
            this.start = start;
            this.destination = destination;
            this.ext = ext;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            DashboardFragment.load_masking.setVisibility(View.VISIBLE);
        }

        @Override
        protected JSONObject doInBackground(String... urls) {
            JSONObject result = null;
            //String url = CommonUtilities.SERVER_HOME_URL  + "/server/services/publik/upload.php";
            String url = "http://nanangprogrammer.000webhostapp.com/server/server/services/publik/upload.php";
            //String url = "http://aset.lpkpd.org/server/services/publik/upload.php";
            //String url = "http://nanangrustianto.com/server/server/services/publik/upload.php";
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
                    CommonUtilities.nama_file_upload = upload_gambar;
                    upload_status = result.isNull("status")?upload_status:result.getInt("status");
                    upload_size   = result.isNull("size")?upload_size:result.getInt("size");
                    upload_info   = result.isNull("info")?upload_info:result.getString("info");
                    upload_more   = result.isNull("more")?upload_more:result.getString("more");

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(end<file_size && upload_status==1) {
                    new prosesUploadChunkFile(end, pathFile, destination, ext).execute();
                } else {
                    try {
                        inStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    DashboardFragment.load_masking.setVisibility(View.GONE);
                    if(upload_status!=1) {
                        text_message.setText(upload_info);
                        text_title.setText("UPLOAD GAGAL");
                        dialog_informasi.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog_informasi.show();
                    } else {
                        Toast.makeText(context, "Upload file berhasil!", Toast.LENGTH_SHORT).show();
                        displayView(4);
                    }

                }

            } else {
                //new prosesUploadChunkFile(start, pathFile, destination, ext).execute();
                text_message.setText("Tidak ada koneksi internet.");
                text_title.setText("UPLOAD GAGAL");
                dialog_informasi.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog_informasi.show();
            }
        }
    }

    private void startTracking() {
        Log.d("ASET DATA", "startTracking");

        CommonUtilities.setCurentlyTracking(context, true);
        Intent i = new Intent(context, AsetService.class);
        i.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
        startService(i);
    }

    private void stopTracking() {
        Log.d("ASET DATA", "stopTracking");
        CommonUtilities.setCurentlyTracking(context, false);
        Intent i = new Intent(context, AsetService.class);
        i.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
        startService(i);
    }


    /**
     * Showing Dialog
     * */
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case progress_bar_type: // we set this to 0
                pDialog = new ProgressDialog(this);
                pDialog.setMessage("Downloading file. Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setMax(100);
                pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pDialog.setCancelable(true);
                pDialog.show();
                return pDialog;
            default:
                return null;
        }
    }

    public void DownloadFileFromURL(String file_url, String url_download) {

        new DownloadFileFromURL(file_url, url_download).execute();

    }

    /**
     * Background Async Task to download file
     * */


    public class DownloadFileFromURL extends AsyncTask<String, String, String> {

        String namaFile="";
        String url_download = "";
        String imagePath = "";

        DownloadFileFromURL(String nama_file, String url_download) {
            this.namaFile = nama_file;
            this.url_download = url_download;
            this.imagePath = Environment.getExternalStorageDirectory().toString() + "/aset1_" + namaFile;
        }

        public void setNamaFile(String namaFile) {
            this.namaFile = namaFile;
        }

        public String getNamaFile() {
            return namaFile;
        }



        /**
         * Before starting background threa
         * Show Progress Bar Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(progress_bar_type);
        }

        /**
         * Downloading file in background thread
         * */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(url_download);
                URLConnection conection = url.openConnection();
                conection.connect();
                // this will be useful so that you can show a tipical 0-100% progress bar
                int lenghtOfFile = conection.getContentLength();

                // download the file
                InputStream input = new BufferedInputStream(url.openStream(), 8192);



                // Output stream
                OutputStream output = new FileOutputStream(imagePath);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress(""+(int)((total*100)/lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        /**
         * Updating progress bar
         * */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            pDialog.setProgress(Integer.parseInt(progress[0]));
        }

        /**
         * After completing background task
         * Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded
            dismissDialog(progress_bar_type);

            // Displaying downloaded image into image view
            // Reading image path from sdcard

            // setting downloaded into image view  //mas file pdf ga bisa di view pakai image View
           //gak usah di view dulu,
            // mas kalau id desa waktu login di rekam tidak ?

            // DownloadFragment.my_image.setImageDrawable(Drawable.createFromPath(imagePath));

            MediaScannerConnection.scanFile(context,
                    new String[] { imagePath }, null,
                    new MediaScannerConnection.OnScanCompletedListener() {

                        @Override
                        public void onScanCompleted(String path, Uri uri) {
                            // TODO Auto-generated method stub

                        }
                    });
        }

    }
}
