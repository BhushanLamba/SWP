package wts.com.newdesigntask.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wts.com.newdesigntask.R;
import wts.com.newdesigntask.retrofit.RetrofitClient;

public class UploadbankDocumentsActivity extends AppCompatActivity {

    Button btnPancard, btnPassbook, btnSubmit;
    ImageView imgPancard, imgPassbook;

    String panCardUrl = "NA", passbookUrl = "NA";

    String imageType = "NA";
    final String PANCARD = "3", PASSBOOK = "6";

    private static final int FILE_PERMISSION = 2;
    private static final int CAMERA_REQUEST = 1;

    ArrayList<String> photoPaths;

    String userId, deviceId, deviceInfo;
    SharedPreferences sharedPreferences;

    String BENE_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploadbank_documents);

        initViews();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(UploadbankDocumentsActivity.this);
        userId = sharedPreferences.getString("userid", null);
        deviceId = sharedPreferences.getString("deviceId", null);
        deviceInfo = sharedPreferences.getString("deviceInfo", null);
        BENE_ID=getIntent().getStringExtra("bene_id");

        handleUploadButtonClicks();


        btnSubmit.setOnClickListener(v ->
        {
            if (!(
                    panCardUrl.equalsIgnoreCase("NA") ||

                            passbookUrl.equalsIgnoreCase("NA"))) {
                uploadBankDocumets();
            } else {
                Toast.makeText(UploadbankDocumentsActivity.this, "Please upload all documents first.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void uploadBankDocumets() {
        final android.app.AlertDialog pDialog = new android.app.AlertDialog.Builder(UploadbankDocumentsActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        Call<JsonObject> call= RetrofitClient.getInstance().getApi().uploadbankDetails(BENE_ID,passbookUrl,panCardUrl,"PAN");
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful())
                {
                    try {
                        JSONObject responseObject=new JSONObject(String.valueOf(response.body()));
                        String message=responseObject.getString("data");
                        pDialog.dismiss();
                        new AlertDialog.Builder(UploadbankDocumentsActivity.this)
                                .setMessage(message)
                                .setTitle("Message")
                                .setCancelable(false)
                                .setPositiveButton("ok", (dialogInterface, i) -> finish())
                                .show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        new AlertDialog.Builder(UploadbankDocumentsActivity.this)
                                .setMessage("Please try after some time.")
                                .setTitle("Message")
                                .setPositiveButton("ok",null)
                                .show();
                    }
                }
                else
                {
                    pDialog.dismiss();
                    new AlertDialog.Builder(UploadbankDocumentsActivity.this)
                            .setMessage("Please try after some time.")
                            .setTitle("Message")
                            .setPositiveButton("ok",null)
                            .show();
                }
            }


            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pDialog.dismiss();
                new AlertDialog.Builder(UploadbankDocumentsActivity.this)
                        .setMessage("Please try after some time.\n"+t.getMessage())
                        .setTitle("Message")
                        .setPositiveButton("ok",null)
                        .show();
            }
        });
    }

    private void handleUploadButtonClicks() {

        btnPancard.setOnClickListener(v ->
        {
            imageType = PANCARD;
            checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, FILE_PERMISSION);
        });


        btnPassbook.setOnClickListener(v ->
        {
            imageType = PASSBOOK;
            checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, FILE_PERMISSION);
        });

    }

    public void checkPermission(String writePermission, String readPermission, int requestCode) {

        if (ContextCompat.checkSelfPermission(UploadbankDocumentsActivity.this, writePermission) == PackageManager.PERMISSION_DENIED
                && ContextCompat.checkSelfPermission(UploadbankDocumentsActivity.this, readPermission) == PackageManager.PERMISSION_DENIED) {
            // Requesting the permission
            ActivityCompat.requestPermissions(UploadbankDocumentsActivity.this, new String[]{writePermission, readPermission}, requestCode);
        } else {
            chooseImage();
        }
    }

    public void chooseImage() {

       /* photoPaths = new ArrayList<>();
        FilePickerBuilder.getInstance()
                //.setSelectedFiles(photoPaths)
                .setActivityTitle("Please select media")
                .enableVideoPicker(true)
                .enableCameraSupport(true)
                .showGifs(false)
                .showFolderView(true)
                .enableSelectAll(false)
                .enableImagePicker(true)
                .setCameraPlaceholder(R.drawable.camera)
                .withOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .pickPhoto(this, PICK_IMAGE_REQUEST);*/

        photoPaths = new ArrayList<>();
        FilePickerBuilder.getInstance()
                .setSelectedFiles(photoPaths)
                .setActivityTitle("Please select media")
                .enableVideoPicker(true)
                .enableCameraSupport(true)
                .showGifs(true)
                .setMaxCount(1)
                .showFolderView(true)
                .enableSelectAll(false)
                .enableImagePicker(true)
                .setCameraPlaceholder(R.drawable.ic_camera)
                .withOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .pickPhoto(this, CAMERA_REQUEST);

    }

    @SuppressLint("SetTextI18n")
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (CAMERA_REQUEST == requestCode && resultCode == RESULT_OK && data != null) {
                photoPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA));
                String path = photoPaths.get(0);
                File file = new File(path);
                serverUpload(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void serverUpload(File myfile) {

        final android.app.AlertDialog pDialog = new android.app.AlertDialog.Builder(UploadbankDocumentsActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_progress_dialog, null);
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pDialog.setView(convertView);
        pDialog.setCancelable(false);
        pDialog.show();

        RequestBody reqFile;
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        if (myfile.toString().contains(".pdf")) {
            reqFile = RequestBody.create(MediaType.parse("application/pdf"), myfile);
        } else if (myfile.toString().contains(".jpg") || myfile.toString().contains(".jpeg") || myfile.toString().contains(".png")) {
            reqFile = RequestBody.create(MediaType.parse("image/*"), myfile);
        } else {
            pDialog.dismiss();
            Toast.makeText(this, "Please select only image file.", Toast.LENGTH_SHORT).show();
            return;
        }
        MultipartBody.Part body = MultipartBody.Part.createFormData("myFile", timeStamp + myfile.getName(), reqFile);


        Call<JsonObject> call = RetrofitClient.getInstance().getApi().uploadfileForSettlement( body);
        call.enqueue(new Callback<JsonObject>() {

            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(String.valueOf(response.body()));
                        String code = jsonObject.getString("status");

                        if (code.equalsIgnoreCase("200")) {

                            if (imageType.equalsIgnoreCase(PANCARD)) {
                                panCardUrl = jsonObject.getString("data");
                                imgPancard.setVisibility(View.VISIBLE);
                                Picasso.get().load("http://subapi.allcardfind.com/" + panCardUrl).into(imgPancard);
                            } else if (imageType.equalsIgnoreCase(PASSBOOK)) {
                                passbookUrl = jsonObject.getString("data");
                                imgPassbook.setVisibility(View.VISIBLE);
                                Picasso.get().load("http://subapi.allcardfind.com/" + passbookUrl).into(imgPassbook);
                            }
                            pDialog.dismiss();

                        } else {
                            pDialog.dismiss();
                            Toast.makeText(UploadbankDocumentsActivity.this, "Please try again", Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        pDialog.dismiss();
                        e.printStackTrace();
                        Toast.makeText(UploadbankDocumentsActivity.this, "Please try again", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    pDialog.dismiss();
                    Toast.makeText(UploadbankDocumentsActivity.this, "Please try again", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pDialog.dismiss();
                Toast.makeText(UploadbankDocumentsActivity.this, "Please try again", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void initViews() {

        btnPancard = findViewById(R.id.btn_pancard);
        btnPassbook = findViewById(R.id.btn_passbook);
        btnSubmit = findViewById(R.id.btn_submit);


        imgPancard = findViewById(R.id.img_pancard);
        imgPassbook = findViewById(R.id.img_passbook);
    }
}