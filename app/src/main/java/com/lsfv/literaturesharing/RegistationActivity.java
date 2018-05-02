package com.lsfv.literaturesharing;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lsfv.literaturesharing.AsyncTasks.AsyncResponse;
import com.lsfv.literaturesharing.AsyncTasks.WebserviceCall;
import com.lsfv.literaturesharing.Helper.Compressor;
import com.lsfv.literaturesharing.Helper.Config;
import com.lsfv.literaturesharing.Helper.NotiUtil;
import com.lsfv.literaturesharing.Helper.Utils;
import com.lsfv.literaturesharing.model.RegistationModel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RegistationActivity extends AppCompatActivity {
    Button button, send_otp;
    EditText number, fname, lname, email, pass, cpass, et_otp;
    Button resend;
    Uri picUri;
    ImageView imageView;
    Button addPhoto;
    Spinner dob_dd, dob_mm, dob_yyyy;
    LinearLayout s_layout;
    int randomPin;
    String otp;
    Bitmap myBitmap,bitmap,compressorbitmap;
    AlertDialog.Builder bu1;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    private ByteArrayOutputStream stream;
    private String imagepath = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registation);
        getSupportActionBar().hide();

        if (checkAndRequestPermissions()) {
            // carry on the normal flow, as the case of  permissions  granted.
        }
        button = (Button) findViewById(R.id.btn_sinin);
        imageView = (ImageView) findViewById(R.id.add_photo);
        number = (EditText) findViewById(R.id.number);
        addPhoto = (Button) findViewById(R.id.btn_add_photo);
        s_layout = (LinearLayout) findViewById(R.id.spi_layout);
        dob_dd = (Spinner) findViewById(R.id.spi_dd);
        dob_mm = (Spinner) findViewById(R.id.spi_mm);
        dob_yyyy = (Spinner) findViewById(R.id.spi_yyyy);

        fname = (EditText) findViewById(R.id.firstname);
        lname = (EditText) findViewById(R.id.lastname);
        email = (EditText) findViewById(R.id.email);
        pass = (EditText) findViewById(R.id.password);
        cpass = (EditText) findViewById(R.id.con_password);
        send_otp = (Button) findViewById(R.id.send_otp);
        et_otp = (EditText) findViewById(R.id.enter_otp);
        resend = (Button) findViewById(R.id.resend);

        ArrayList<String> years = new ArrayList<String>();
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        years.add("YYYY");
        for (int i = 1930; i < thisYear; i++) {
            years.add(String.valueOf(i));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, years);
        dob_yyyy.setAdapter(adapter);

        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        final String token = pref.getString("regId", null);
        send_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (number.getText().toString().length() < 10) {
                    number.setError("Enter Valid Number");
                } else if (number.getText().toString().length() == 0) {
                    number.setError("Enter Mobile Number");
                } else {
                    button.setVisibility(View.VISIBLE);
                    addPhoto.setVisibility(View.VISIBLE);
//                    imageView.setVisibility(View.VISIBLE);
//                    s_layout.setVisibility(View.VISIBLE);
//                    dob_dd.setVisibility(View.VISIBLE);
//                    dob_mm.setVisibility(View.VISIBLE);
//                    dob_yyyy.setVisibility(View.VISIBLE);
                    fname.setVisibility(View.VISIBLE);
                    lname.setVisibility(View.VISIBLE);
//                    email.setVisibility(View.VISIBLE);
                    pass.setVisibility(View.VISIBLE);
                    cpass.setVisibility(View.VISIBLE);
                    send_otp.setVisibility(View.GONE);
                    et_otp.setVisibility(View.VISIBLE);
                    resend.setVisibility(View.VISIBLE);
                    randomPin = (int) (Math.random() * 9000) + 1000;
                    otp = String.valueOf(randomPin);

                    String n = number.getText().toString();


                    sendSms(n);


                }
            }
        });

        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String n = number.getText().toString();
                sendSms(n);
            }
        });

        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(getPickImageChooserIntent(), 200);
            }
        });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!et_otp.getText().toString().equalsIgnoreCase(otp)) {
                    et_otp.setError("Enter Valid otp ");
                } else if (fname.getText().toString().length() == 0) {
                    fname.setError("Enter Firstname");
                } else if (lname.getText().toString().length() == 0) {
                    lname.setError("Enter Lastname");
                }
//                else if (email.getText().toString().length() == 0) {
//                    email.setError("Enter EmailAddress");
//                }
            else if (pass.getText().toString().length() == 0) {
                    pass.setError("Enter Password");
                }
                else if (cpass.getText().toString().length() == 0) {
                    cpass.setError("Enter Confirm Password ");
                } else if (!pass.getText().toString().equalsIgnoreCase(cpass.getText().toString())) {
                    pass.setError("Password is not match");
                } else {
                    String mobile_number = number.getText().toString();
                    String password = pass.getText().toString();
                    String birthdate = dob_dd.getSelectedItem().toString() + "/" + dob_mm.getSelectedItem().toString() + "/" + dob_yyyy.getSelectedItem().toString();
                    String firstname = fname.getText().toString();
                    String lastname = lname.getText().toString();
                    String mail = email.getText().toString();
                    String imgsrting = Utils.setImage(myBitmap);
                    Log.d("base64", imgsrting);
                    String abc = imgsrting.trim().replace("\n", "");
//                    String[] keys = new String[]{"mode", "mobile_num", "firstname", "lastname", "email", "birthdate", "password", "user_type", "certy_image", "deviceToken"};
//                    String[] values = new String[]{"userRegister", mobile_number, firstname, lastname, mail, birthdate, password, "user", abc, token};

                    String[] keys = new String[]{"mode", "mobile_num", "firstname", "lastname", "password", "user_type", "certy_image", "deviceToken"};
                    String[] values = new String[]{"userRegister", mobile_number, firstname, lastname, password, "user", abc, token};

                    String jsonRequest = Utils.createJsonRequest(keys, values);
                    Log.d("json", jsonRequest);
                    String URL = Config.MAIN_URL;
                    new WebserviceCall(RegistationActivity.this, URL, jsonRequest, "Signing Up...!!", true, new AsyncResponse() {
                        @Override
                        public void onCallback(String response) {
                            Log.d("myapp", response);
                            RegistationModel model = new Gson().fromJson(response, RegistationModel.class);

                            //int s=model.getStatus();

                            if (model.getStatus() == 1) {
                                // Create custom dialog object
                                final Dialog dialog = new Dialog(RegistationActivity.this);
                                // Include dialog.xml file
                                dialog.setContentView(R.layout.custom_alert_dialog);

                                TextView textView = (TextView) dialog.findViewById(R.id.msg);
                                Button buttonNo = (Button) dialog.findViewById(R.id.nobtn);
                                Button buttonOk = (Button) dialog.findViewById(R.id.okbtn);

                                textView.setText(model.getMessage());

                                buttonNo.setVisibility(View.GONE);


                                buttonOk.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(RegistationActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                        RegistationActivity.this.finish();
                                    }
                                });

                                dialog.show();
                            } else {
                                // Create custom dialog object
                                final Dialog dialog = new Dialog(RegistationActivity.this);
                                // Include dialog.xml file
                                dialog.setContentView(R.layout.custom_alert_dialog);

                                TextView textView = (TextView) dialog.findViewById(R.id.msg);
                                Button buttonNo = (Button) dialog.findViewById(R.id.nobtn);
                                Button buttonOk = (Button) dialog.findViewById(R.id.okbtn);

                                textView.setText(model.getMessage());

//                                textView.setText();

                                buttonNo.setVisibility(View.GONE);


                                buttonOk.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialog.dismiss();
                                    }
                                });

                                dialog.show();
                            }
                        }

                        @Override
                        public void onFailure(String message) {
                        }
                    }).execute();
                }
            }
        });
    }

    public Intent getPickImageChooserIntent() {

        // Determine Uri of camera image to save.
        Uri outputFileUri = getCaptureImageOutputUri();

        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = getPackageManager();

        // collect all camera intents
        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            allIntents.add(intent);
        }

        // collect all gallery intents
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : listGallery) {
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            allIntents.add(intent);
        }

        // the main intent is the last in the list (fucking android) so pickup the useless one
        Intent mainIntent = allIntents.get(allIntents.size() - 1);
        for (Intent intent : allIntents) {
            if (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                mainIntent = intent;
                break;
            }
        }
        allIntents.remove(mainIntent);

        // Create a chooser from the main intent
        Intent chooserIntent = Intent.createChooser(mainIntent, "Select source");

        // Add all other intents
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

        return chooserIntent;
    }

    private Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        File getImage = getExternalCacheDir();
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "profile.png"));
        }
        return outputFileUri;
    }

    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        //img.recycle();
        return rotatedImg;
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 0) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }


    /**
     * Get the URI of the selected image from {@link #getPickImageChooserIntent()}.<br/>
     * Will return the correct URI for camera and gallery image.
     *
     * @param data the returned data of the activity result
     */
    public Uri getPickImageResultUri(Intent data) {
        boolean isCamera = true;
        if (data != null) {
            String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }
        return isCamera ? getCaptureImageOutputUri() : data.getData();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on scren orientation
        // changes
        outState.putParcelable("pic_uri", picUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        picUri = savedInstanceState.getParcelable("pic_uri");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (resultCode == Activity.RESULT_OK && requestCode == 200) {
            picUri = getPickImageResultUri(data);
            try {
                if (picUri != null) {
                    addPhoto.setVisibility(View.GONE);
                    imageView.setVisibility(View.VISIBLE);
                    myBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), picUri);
                    myBitmap = rotateImageIfRequired(myBitmap, RegistationActivity.this, picUri);
                    myBitmap = getResizedBitmap(myBitmap, 500);

                    imageView.setImageBitmap(myBitmap);
                }else{

                    try {
                        Uri uri = data.getData();
                        addPhoto.setVisibility(View.GONE);
                        imageView.setVisibility(View.VISIBLE);
                        bitmap = (Bitmap) data.getExtras().get("data");
                        Uri tempUri = getImageUri(getApplicationContext(), bitmap);
                        File finalFile = new File(getRealPathFromURI(tempUri));
                        compressorbitmap = new Compressor(this).compressToBitmap(finalFile);
//            byte[] bytesProfile = Utility.convertBitmapToByteArray(bitmap);
                        byte[] bytesProfile = stream.toByteArray();
                        imagepath = NotiUtil.saveBitmapOnSDCard(this, "Image_" + String.valueOf(System.currentTimeMillis()) + ".jpeg", bytesProfile);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    imageView.setImageBitmap(compressorbitmap);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        stream = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    private static Bitmap rotateImageIfRequired(Bitmap img, Context context, Uri selectedImage) throws IOException {
        if (selectedImage.getScheme().equals("content")) {
            String[] projection = {MediaStore.Images.ImageColumns.ORIENTATION};
            Cursor c = context.getContentResolver().query(selectedImage, projection, null, null, null);
            if (c.moveToFirst()) {
                final int rotation = c.getInt(0);
                c.close();
                return rotateImage(img, rotation);
            }
            return img;
        } else {
            ExifInterface ei = new ExifInterface(selectedImage.getPath());
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return rotateImage(img, 90);
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return rotateImage(img, 180);
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return rotateImage(img, 270);
                default:
                    return img;
            }
        }
    }

    public void sendSms(String n) {
        //     http://1.rapidsms.co.in/api/push.json?apikey=5a28fdcd4c3a9&route=clientsms&sender=LSFVIU&mobileno=8905982801&text=hii
        // Construct data
        String username = "blindaudiobook@gmail.com";
        String message = "Dear User, Your Verification code is: " + otp + " : Thank You";
        String sender = "TXTLCL";
        String numbers = n;
        String hashkey = "f2492dc102a260162236ed300114f39af78cce0692173d7730805a007efdfa98";

        String URL = "http://1.rapidsms.co.in/api/push.json?apikey=5a28fdcd4c3a9&route=clientsms&sender=LSFVIU&mobileno=" + numbers + "&text=" + message;
        // String URL = "https://api.textlocal.in/send/?"+"username="+username+"&hash="+hashkey+"&sender="+sender+"&numbers="+numbers+"&message="+message;

        WebView webView = new WebView(this);
        webView.loadUrl(URL);
    }

    @Override
    public void onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("otp"));
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase("otp")) {
                final String message = intent.getStringExtra("message");
                et_otp.setText(message);
            }
        }
    };

    private boolean checkAndRequestPermissions() {
        int permissionSendMessage = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.SEND_SMS);
        int receiveSMS = ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECEIVE_SMS);
        int readSMS = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_SMS);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (receiveSMS != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.RECEIVE_MMS);
        }
        if (readSMS != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.READ_SMS);
        }
        if (permissionSendMessage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.SEND_SMS);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                    REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

}
