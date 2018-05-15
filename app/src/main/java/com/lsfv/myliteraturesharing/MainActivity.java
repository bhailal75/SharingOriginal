package com.lsfv.myliteraturesharing;

import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lsfv.myliteraturesharing.Adapter.BookListAdapter;
import com.lsfv.myliteraturesharing.Adapter.DownloadBookTask;
import com.lsfv.myliteraturesharing.AsyncTasks.AsyncResponse;
import com.lsfv.myliteraturesharing.AsyncTasks.WebserviceCall;
import com.lsfv.myliteraturesharing.Helper.Config;
import com.lsfv.myliteraturesharing.Helper.Utils;
import com.lsfv.myliteraturesharing.model.AudiobookBean;
import com.lsfv.myliteraturesharing.model.BookListModel;
import com.lsfv.myliteraturesharing.model.ChapterListBean;
import com.lsfv.myliteraturesharing.model.ChapterListBeanEntityManager;
import com.lsfv.myliteraturesharing.model.ChapterListModel;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;

import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, BookListAdapter.BookClickListner {

    AVLoadingIndicatorView pvHome;
    TextView tvLoading;
    SwipeRefreshLayout refreshLayout;
    BookListModel model;
    String s, type = "r";
    private String downloadMp3Url;
    private String downloadMp3ChapterName;
    int[] img = {R.drawable.ic_right_arrow};
    ArrayList<AudiobookBean> audiobookList;
    private static final int PERMISSION_REQUEST_CODE = 200;
    private SearchView search;
    private RecyclerView listView;
    private LinearLayoutManager linearLayoutManager;
    private BookListAdapter booklistAdapter;
    private ArrayList<ChapterListBean> chapterList;
    private String bkname;
    private ArrayList<ChapterListBean> chapterDownload;
    private int temp = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (!checkPermission()) {
            requestPermission();
        } else {
            //Toast.makeText(MainActivity.this, "Permission already granted", Toast.LENGTH_SHORT).show();
        }

        listView = (RecyclerView) findViewById(R.id.Book_list);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        pvHome = (AVLoadingIndicatorView) findViewById(R.id.pv_home);
        tvLoading = (TextView) findViewById(R.id.tv_loading);
        audiobookList = new ArrayList<>();
        Swipe();
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        listView.setLayoutManager(linearLayoutManager);
        booklistAdapter = new BookListAdapter(this, audiobookList, this);
        listView.setAdapter(booklistAdapter);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(true);
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                    }
                }, 2000);
                Swipe();
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        MenuItem searchMenuItem = menu.findItem(R.id.search);
        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        search = (SearchView) searchMenuItem.getActionView();
        MenuItemCompat.setOnActionExpandListener(searchMenuItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                        toggleSoftInput(InputMethodManager.SHOW_FORCED,
                                InputMethodManager.HIDE_IMPLICIT_ONLY);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                search.clearFocus();
                Swipe();
                return true;
            }
        });
        return true;
    }

    private void Search(String s) {
        String[] keys = new String[]{"mode", "audio_search"};
        String[] values = new String[]{"audioSearch", s};
        String jsonRequest = Utils.createJsonRequest(keys, values);
        String URL = Config.MAIN_URL;
        new WebserviceCall(MainActivity.this, URL, jsonRequest, "Getting BookList...!!", false, new AsyncResponse() {
            @Override
            public void onCallback(String response) {
                Log.d("jjj", response);
                audiobookList.clear();
                model = new Gson().fromJson(response, BookListModel.class);
                if (model.getStatus().equalsIgnoreCase("1")) {
                    for (int i = 0; i < model.getAudiobook().size(); i++) {
                        audiobookList.add(model.getAudiobook().get(i));
                    }
                    Log.i("TAG", "search record: " + audiobookList.size());
                    booklistAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(MainActivity.this, "" + message, Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refres:
                refreshLayout.setRefreshing(true);
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                    }
                }, 2000);
                Swipe();
                break;
            case R.id.search:
                search.setIconifiedByDefault(false);
                search.setQueryHint("Search Here");
                search.requestFocus();
                search.setImeOptions(6);
                search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String s) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String s) {
                        if (s.length() > 0) {
                            Search(s);
                        } else {
                            Swipe();
                        }
                        return false;
                    }
                });
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_changepin) {
            Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_download) {
            Intent intent = new Intent(getApplicationContext(), DownloadFileActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_contact) {
            Intent intent = new Intent(getApplicationContext(), ContactUsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            getSharedPreferences("profile", Context.MODE_PRIVATE).edit().clear().apply();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void Swipe() {
        pvHome.setVisibility(View.VISIBLE);
        tvLoading.setVisibility(View.VISIBLE);
        listView.setVisibility(View.GONE);
        String[] keys = new String[]{"mode"};
        String[] values = new String[]{"testgetAudiobook"};
        String jsonRequest = Utils.createJsonRequest(keys, values);
        String URL = Config.MAIN_URL;
        new WebserviceCall(MainActivity.this, URL, jsonRequest, "Getting BookList...!!", false, new AsyncResponse() {
            @Override
            public void onCallback(String response) {
                Log.d("myapp", response);
                model = new Gson().fromJson(response, BookListModel.class);
                if (model.getStatus().equalsIgnoreCase("1")) {
                    if (audiobookList != null && audiobookList.size() > 0) {
                        audiobookList.clear();
                    }
                    pvHome.setVisibility(View.GONE);
                    tvLoading.setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);
                    for (int i = 0; i < model.getAudiobook().size(); i++) {
                        audiobookList.add(model.getAudiobook().get(i));
                    }
                    booklistAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onFailure(String message) {
                pvHome.setVisibility(View.GONE);
                tvLoading.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "" + message, Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, CALL_PHONE}, PERMISSION_REQUEST_CODE);
    }

    private boolean checkPermission() {
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(), CALL_PHONE);

        return result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if (locationAccepted) {

                    }
                    //Toast.makeText(this, "Permission Granted, Now you can access storage.", Toast.LENGTH_SHORT).show();
                    else {
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
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    public void onBookClick(int i) {
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        if (model.getAudiobook() != null && model.getAudiobook().get(i).getAudio_book_id() != null) {
            String s = model.getAudiobook().get(i).getAudio_book_id();
            String bkname = model.getAudiobook().get(i).getAudio_book_description().toString();
            SharedPreferences preferences = getApplicationContext().getSharedPreferences("bookid", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("id", s);
            editor.commit();
            Intent intent = new Intent(getApplicationContext(), ChapterListActivity.class);
            intent.putExtra("bookname", bkname);
            startActivity(intent);
        }
    }

    @Override
    public void onDownloadBookClick(int i) {
        String s = model.getAudiobook().get(i).getAudio_book_id();
        bkname = model.getAudiobook().get(i).getAudio_book_description().toString();
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("bookid", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("id", s);
        editor.commit();
        getBookChapterforDownloadOnly();

    }

    private void getBookChapterforDownloadOnly() {
        SharedPreferences preferences = getSharedPreferences("bookid", Context.MODE_PRIVATE);
        s = preferences.getString("id", null);
        String[] keys = new String[]{"mode", "audio_book_id", "file_recent"};
        String[] values = new String[]{"getchapter", s, "a"};
        String jsonRequest = Utils.createJsonRequest(keys, values);

        String URL = Config.MAIN_URL;
        new WebserviceCall(this, URL, jsonRequest, "Getting chapterList...!!", false, new AsyncResponse() {
            @Override
            public void onCallback(String response) {
                chapterList = new ArrayList<ChapterListBean>();
                final ChapterListModel model = new Gson().fromJson(response, ChapterListModel.class);
                if (chapterList != null)
                    chapterList.clear();
                if (model.getStatus().equalsIgnoreCase("1")) {
                    chapterList.addAll(model.getChapterList());

                    if (chapterList != null && chapterList.size() > 0) {
                        ChapterListBeanEntityManager chapterListBeanEntityManager = new ChapterListBeanEntityManager();
                        ChapterListBean chapterListBean = new ChapterListBean();

                        if (chapterListBeanEntityManager.count() > 0) {
                            chapterDownload = (ArrayList<ChapterListBean>) chapterListBeanEntityManager.select().asList();
                        }

                        temp = 0;
                        if (chapterDownload != null && chapterDownload.size() > 0) {
                            for (int i = 0; i < chapterList.size(); i++) {
                                for (int j = 0; j < chapterDownload.size(); j++) {
                                    if (chapterList.get(i).getChapter_id() == chapterDownload.get(j).getChapter_id()
                                             && chapterList.get(i).getChapter_desc().equals(chapterDownload.get(j).getChapter_desc()))
                                        temp++;
                                }
                            }
                        }

                        if (temp != chapterList.size()) {
                            for (int j = 0; j < chapterList.size(); j++) {
                                downloadMp3Url = chapterList.get(j).getChapter_file();
                                downloadMp3ChapterName = chapterList.get(j).getChapter_desc();
                                new DownloadBookTask(MainActivity.this, chapterList, bkname);
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Already Book Downloaded", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(MainActivity.this, "" + message, Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        unregisterReceiver();
    }
}
