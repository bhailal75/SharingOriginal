package com.lsfv.literaturesharing;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


import com.google.gson.Gson;
import com.lsfv.literaturesharing.Adapter.ChapterListAdapter;
import com.lsfv.literaturesharing.AsyncTasks.AsyncResponse;
import com.lsfv.literaturesharing.AsyncTasks.WebserviceCall;
import com.lsfv.literaturesharing.Fragment.AllChapterFragment;
import com.lsfv.literaturesharing.Fragment.RecentChepterFragment;
import com.lsfv.literaturesharing.Helper.Config;
import com.lsfv.literaturesharing.Helper.Utils;
import com.lsfv.literaturesharing.Model.ChapterListBean;
import com.lsfv.literaturesharing.Model.ChapterListModel;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class ChapterListActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 200;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    AVLoadingIndicatorView pvChapter;
    TextView tvLoading;
    String s, type = "r";
    ArrayList<ChapterListBean> chapterList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getIntent().getStringExtra("bookname"));

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        pvChapter = (AVLoadingIndicatorView) findViewById(R.id.pv_chapter);
        tvLoading = (TextView) findViewById(R.id.tv_loading);
        tabLayout = (TabLayout) findViewById(R.id.tabs);

        Swipe();
        tabLayout.setupWithViewPager(viewPager);


//        setupViewPager(viewPager);

        if (!checkPermission()) {
            requestPermission();

        } else {
            //Toast.makeText(ChapterListActivity.this, "Permission already granted", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new RecentChepterFragment(chapterList), "ASCENDING");
        adapter.addFragment(new AllChapterFragment(chapterList), "DESCENDING");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    private boolean checkPermission() {
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);

        return result1 == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if (locationAccepted) {
                        //Toast.makeText(this, "Permission Granted, Now you can access storage.", Toast.LENGTH_SHORT).show();
                    } else {
                        //Toast.makeText(this, "Permission Denied, You cannot access storage.", Toast.LENGTH_SHORT).show();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(ACCESS_NETWORK_STATE)) {
                                showMessageOKCancel("You need to allow access the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE},
                                                            PERMISSION_REQUEST_CODE);
                                                }
                                            }
                                        });
                                return;
                            }
                        }

                    }
                }
                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(ChapterListActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        finish();
    }

    private void Swipe() {
        pvChapter.setVisibility(View.VISIBLE);
        tvLoading.setVisibility(View.VISIBLE);
        SharedPreferences preferences = this.getSharedPreferences("bookid", Context.MODE_PRIVATE);
        s=preferences.getString("id",null);
        String[]keys=new String[]{"mode","audio_book_id","file_recent"};
        String[]values=new String[]{"getchapter",s,"r"};
        String jsonRequest= Utils.createJsonRequest(keys,values);

        String URL = Config.MAIN_URL;
        new WebserviceCall(ChapterListActivity.this, URL, jsonRequest, "Getting chapterList...!!", false, new AsyncResponse() {
            @Override
            public void onCallback(String response) {
                Log.d("myapp",response);
                final ChapterListModel model = new Gson().fromJson(response,ChapterListModel.class);
                if (model.getStatus().equalsIgnoreCase("1"))
                {
                    tvLoading.setVisibility(View.GONE);
                    pvChapter.setVisibility(View.GONE);
                    chapterList=new ArrayList<>();
                    for (int i=0;i<model.getChapterList().size();i++)
                    {
                        chapterList.add(model.getChapterList().get(i));
                    }

//                    chapterListAdapter=new ChapterListAdapter(getContext(),R.layout.chapterlist_cus_layout,chapterList,type);
//                    listView.setAdapter(chapterListAdapter);
                    if (chapterList != null)
                    {
                        setupViewPager(viewPager);
                    }
                }
            }

            @Override
            public void onFailure(String message) {
                pvChapter.setVisibility(View.GONE);
                tvLoading.setVisibility(View.GONE);
                // Toast.makeText(getContext(), ""+message, Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }

}
