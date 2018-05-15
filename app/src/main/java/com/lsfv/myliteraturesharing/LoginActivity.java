package com.lsfv.myliteraturesharing;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lsfv.myliteraturesharing.AsyncTasks.AsyncResponse;
import com.lsfv.myliteraturesharing.AsyncTasks.WebserviceCall;
import com.lsfv.myliteraturesharing.Helper.Config;
import com.lsfv.myliteraturesharing.Helper.Utils;
import com.lsfv.myliteraturesharing.model.ForgotPassModel;
import com.lsfv.myliteraturesharing.model.LoginModel;

public class LoginActivity extends AppCompatActivity {
    EditText number,pass;
    Button submit;
    LoginModel model;
    Button newuser;
    TextView forgotpass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();

        number=(EditText)findViewById(R.id.e_number);
        pass=(EditText)findViewById(R.id.e_password);
        submit=(Button)findViewById(R.id.btn_sinin);
        forgotpass=(TextView)findViewById(R.id.forgot_password);
        newuser=(Button) findViewById(R.id.new_user);

        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        final String token = pref.getString("regId", null);
        //Toast.makeText(this, ""+token, Toast.LENGTH_SHORT).show();

        newuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),RegistationActivity.class);
                startActivity(intent);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (number.getText().toString().length()==0)
                {
                    number.setError("Enter Mobile Number");
                }
                else if (pass.getText().toString().length()==0)
                {
                    pass.setError("Enter Password");
                }
                else  {
                    String mobile_number = number.getText().toString();
                    final String password = pass.getText().toString();

                    String[]keys=new String[]{"mode","mobile_num","password","deviceToken"};
                    String[]values=new String[]{"loginUser",mobile_number,password,token};
                    String jsonRequest= Utils.createJsonRequest(keys,values);

                    String URL = Config.MAIN_URL;
                    new WebserviceCall(LoginActivity.this, URL, jsonRequest, "Sign In...!!", true, new AsyncResponse() {
                        @Override
                        public void onCallback(String response) {
                            Log.d("myapp",response);
                            model = new Gson().fromJson(response,LoginModel.class);
 //                           Toast.makeText(LoginActivity.this,model.getMessage() , Toast.LENGTH_SHORT).show();
                            /*AlertDialog.Builder bu=new AlertDialog.Builder(LoginActivity.this);
                            bu.setMessage(model.getMessage());
                            bu.setTitle("Login");
                            bu.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                }
                            });
                            bu.show();*/
                            if (model.getStatus()==1)
                            {
                                // Create custom dialog object
                                final Dialog dialog = new Dialog(LoginActivity.this);
                                // Include dialog.xml file
                                dialog.setContentView(R.layout.custom_alert_dialog);

                                TextView textView=(TextView)dialog.findViewById(R.id.msg);
                                Button buttonNo=(Button)dialog.findViewById(R.id.nobtn);
                                Button buttonOk=(Button)dialog.findViewById(R.id.okbtn);

                                textView.setText(model.getMessage());

                                buttonNo.setVisibility(View.GONE);

                                SetData();
                                buttonOk.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                        startActivity(intent);
                                        LoginActivity.this.finish();
                                    }
                                });

                                dialog.show();
                            }
                            else if (model.getStatus()==0)
                            { // Create custom dialog object
                                final Dialog dialog = new Dialog(LoginActivity.this);
                                // Include dialog.xml file
                                dialog.setContentView(R.layout.custom_alert_dialog);

                                TextView textView=(TextView)dialog.findViewById(R.id.msg);
                                Button buttonNo=(Button)dialog.findViewById(R.id.nobtn);
                                Button buttonOk=(Button)dialog.findViewById(R.id.okbtn);

                                textView.setText(model.getMessage());

                                buttonNo.setVisibility(View.GONE);


                                buttonOk.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                       dialog.dismiss();
                                    }
                                });

                                dialog.show();

                                //Toast.makeText(LoginActivity.this, model.getMessage(), Toast.LENGTH_SHORT).show();
                                number.setText("");
                                pass.setText("");
                                /*Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                startActivity(intent);*/
                            }
                            /*else
                            {
                                Toast.makeText(LoginActivity.this, "User not Approved please contact to admin", Toast.LENGTH_SHORT).show();
                            }*/
                        }

                        @Override
                        public void onFailure(String message) {
                            Toast.makeText(LoginActivity.this, ""+message, Toast.LENGTH_SHORT).show();
                        }
                    }).execute();
                }
            }
        });

        forgotpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Dialog dialog=new Dialog(LoginActivity.this);
                dialog.setContentView(R.layout.custom_dialog_forgot_password);
                dialog.setTitle("Forgot Password");
                DisplayMetrics metrics = getResources().getDisplayMetrics();
                int width = metrics.widthPixels;
                int height = metrics.heightPixels;
                dialog.getWindow().setLayout((6 * width)/7, (int) ((2 * height)/7.6));

                final EditText email=(EditText) dialog.findViewById(R.id.forgot_email);
                Button submit=(Button)dialog.findViewById(R.id.submit);
                    submit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String mail=email.getText().toString();

                            String[]keys=new String[]{"mode","email"};
                            String[]values=new String[]{"forgotPassword",mail};
                            String jsonRequest= Utils.createJsonRequest(keys,values);

                            String URL = Config.MAIN_URL;
                            new WebserviceCall(LoginActivity.this, URL, jsonRequest, "Forgot Password...!!", true, new AsyncResponse() {
                                @Override
                                public void onCallback(String response) {
                                    Log.d("myapp",response);
                                  ForgotPassModel mod = new Gson().fromJson(response,ForgotPassModel.class);
                                    //                           Toast.makeText(LoginActivity.this,model.getMessage() , Toast.LENGTH_SHORT).show();
                                    if (mod.getStatus()==1)
                                    {
                                        Toast.makeText(LoginActivity.this, ""+mod.getMessage(), Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }

                                }

                                @Override
                                public void onFailure(String message) {
                                    Toast.makeText(LoginActivity.this, ""+message, Toast.LENGTH_SHORT).show();
                                }
                            }).execute();
                        }
                    });

                dialog.show();
            }
        });

    }

    public void SetData(){
        SharedPreferences preferences=this.getSharedPreferences("profile", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();

        editor.putString("number",model.getUserDetails().getMobile_num());
        editor.putString("dob",model.getUserDetails().getBirthdate());
        editor.putInt("id",model.getUserDetails().getUser_id());
        editor.putString("name",model.getUserDetails().getFirstname()+" "+model.getUserDetails().getLastname());
        editor.putString("email",model.getUserDetails().getEmail());
        editor.commit();
    }

//    @Override
//    public void onBackPressed() {
//        // Create custom dialog object
//        final Dialog dialog = new Dialog(LoginActivity.this);
//        // Include dialog.xml file
//        dialog.setContentView(R.layout.custom_alert_dialog);
//
//        TextView textView=(TextView)dialog.findViewById(R.id.msg);
//        Button buttonNo=(Button)dialog.findViewById(R.id.nobtn);
//        Button buttonOk=(Button)dialog.findViewById(R.id.okbtn);
//
//        textView.setText("Are you sure you want to exit?");
//
//        buttonNo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialog.dismiss();
//            }
//        });
//
//
//        buttonOk.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(Intent.ACTION_MAIN);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                intent.addCategory(Intent.CATEGORY_HOME);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
//                dialog.dismiss();
//            }
//        });
//
//        dialog.show();
//    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences prefs = getSharedPreferences("X", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("lastActivity", getClass().getName());
        editor.commit();
    }
}
