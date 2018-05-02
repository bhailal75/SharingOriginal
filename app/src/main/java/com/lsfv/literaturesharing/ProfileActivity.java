package com.lsfv.literaturesharing;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lsfv.literaturesharing.AsyncTasks.AsyncResponse;
import com.lsfv.literaturesharing.AsyncTasks.WebserviceCall;
import com.lsfv.literaturesharing.Helper.Config;
import com.lsfv.literaturesharing.Helper.Utils;
import com.lsfv.literaturesharing.model.ChangePasswordModel;

public class ProfileActivity extends AppCompatActivity {
int u_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        GetData();

        final EditText cpass = (EditText) findViewById(R.id.c_pass);
        final EditText npass = (EditText) findViewById(R.id.n_pass);
        Button submit = (Button) findViewById(R.id.change);


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String c_pass = cpass.getText().toString();
                final String n_pass = npass.getText().toString();

                String[] keys = new String[]{"mode", "user_id", "password", "newPassword"};
                String[] values = new String[]{"changepassword", String.valueOf(u_id), c_pass, n_pass};
                String jsonRequest = Utils.createJsonRequest(keys, values);

                String URL = Config.MAIN_URL;
                new WebserviceCall(ProfileActivity.this, URL, jsonRequest, "Change Password...!!", true, new AsyncResponse() {
                    @Override
                    public void onCallback(String response) {
                        Log.d("myapp", response);
                        ChangePasswordModel model = new Gson().fromJson(response, ChangePasswordModel.class);
                        Toast.makeText(ProfileActivity.this, "" + model.getMessage(), Toast.LENGTH_SHORT).show();
                        if (model.getStatus() == 1) {
                            Intent intent=new Intent(ProfileActivity.this,MainActivity.class);
                            startActivity(intent);
                            finish();
                        }

                    }

                    @Override
                    public void onFailure(String message) {
                        Toast.makeText(ProfileActivity.this, "" + message, Toast.LENGTH_SHORT).show();
                    }
                }).execute();
            }
        });

    }



    public void GetData(){
        SharedPreferences preferences=this.getSharedPreferences("profile", Context.MODE_PRIVATE);
        u_id=preferences.getInt("id",0);
    }


    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
