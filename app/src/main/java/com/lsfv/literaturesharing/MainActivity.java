package com.lsfv.literaturesharing;

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
import com.lsfv.literaturesharing.Adapter.BookListAdapter;
import com.lsfv.literaturesharing.AsyncTasks.AsyncResponse;
import com.lsfv.literaturesharing.AsyncTasks.WebserviceCall;
import com.lsfv.literaturesharing.Helper.Config;
import com.lsfv.literaturesharing.Helper.Utils;
import com.lsfv.literaturesharing.model.BookListModel;
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
//    ListView listView;
    int[] img={R.drawable.ic_right_arrow};
    ArrayList<BookListModel.AudiobookBean> audiobookList;
    private static final int PERMISSION_REQUEST_CODE = 200;
    private SearchView search;
    private RecyclerView listView;
    private LinearLayoutManager linearLayoutManager;
    private BookListAdapter booklistAdapter;


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

//        listView=(ListView)findViewById(R.id.Book_list);
        listView = (RecyclerView) findViewById(R.id.Book_list);
        refreshLayout=(SwipeRefreshLayout)findViewById(R.id.swipe_refresh);
        pvHome = (AVLoadingIndicatorView) findViewById(R.id.pv_home);
        tvLoading = (TextView) findViewById(R.id.tv_loading);

        audiobookList=new ArrayList<>();
        Swipe();

        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        listView.setLayoutManager(linearLayoutManager);
        booklistAdapter = new BookListAdapter(this, audiobookList, this);
        listView.setAdapter(booklistAdapter);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(true);
                ( new Handler()).postDelayed(new Runnable() {
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
             // Create custom dialog object
//            final Dialog dialog = new Dialog(MainActivity.this);
//            // Include dialog.xml file
//            dialog.setContentView(R.layout.custom_alert_dialog);
//
//            TextView textView=(TextView)dialog.findViewById(R.id.msg);
//            Button buttonNo=(Button)dialog.findViewById(R.id.nobtn);
//            Button buttonOk=(Button)dialog.findViewById(R.id.okbtn);
//
//            textView.setText("Are you sure you want to exit?");
//
//            buttonNo.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    dialog.dismiss();
//                }
//            });
//
//
//            buttonOk.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent intent = new Intent(Intent.ACTION_MAIN);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    intent.addCategory(Intent.CATEGORY_HOME);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);
//                    dialog.dismiss();
//                }
//            });
//
//            dialog.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search, menu);

        MenuItem searchMenuItem = menu.findItem(R.id.search);
        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        search = (SearchView) searchMenuItem.getActionView();


        MenuItemCompat.setOnActionExpandListener(searchMenuItem, new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).
                                toggleSoftInput(InputMethodManager.SHOW_FORCED,
                                        InputMethodManager.HIDE_IMPLICIT_ONLY);
                        return true;
                    }

                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
//                        search.setImeOptions(6);
//                        search.setFocusable(false);
                        search.clearFocus();
                        Swipe();

                        return true;
                    }
                });
//        search.setQueryHint("Search Here");

//        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String s) {
//
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String s) {
//                //  Toast.makeText(MainActivity.this, "!!"+s, Toast.LENGTH_SHORT).show();
//                if (s.length()>0){
//                    Search(s);
//                }else {
//                    Swipe();
//                }
//                return false;
//            }
//        });

        return true;
    }

    private void Search(String s) {

        String[]keys=new String[]{"mode","audio_search"};
        String[]values=new String[]{"audioSearch", s};
        String jsonRequest= Utils.createJsonRequest(keys,values);

        String URL = Config.MAIN_URL;
        new WebserviceCall(MainActivity.this, URL, jsonRequest, "Getting BookList...!!", false, new AsyncResponse() {
            @Override
            public void onCallback(String response) {
                Log.d("jjj",response);
                audiobookList.clear();
                model = new Gson().fromJson(response,BookListModel.class);
                if (model.getStatus().equalsIgnoreCase("1"))
                {

                    for (int i=0;i<model.getAudiobook().size();i++)
                    {
                        audiobookList.add(model.getAudiobook().get(i));
                    }
                    Log.i("TAG", "search record: "+ audiobookList.size());
                    booklistAdapter.notifyDataSetChanged();

                               /* SearchBookListAdapter adapter=new SearchBookListAdapter(getContext(),R.layout.booklist_cus_layout,audiobookList,img);
                                listView.setAdapter(adapter);*/
//                    BookListAdapter adapter=new BookListAdapter(MainActivity.this,R.layout.booklist_cus_layout,audiobookList,img);
//                    listView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(MainActivity.this, ""+message, Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()){
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
//                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.showSoftInput(search, InputMethodManager.SHOW_IMPLICIT);
                search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String s) {

                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String s) {
                          Toast.makeText(MainActivity.this, "!!"+s, Toast.LENGTH_SHORT).show();
                        if (s.length()>0){
                            Search(s);
                        }else {
                            Swipe();
                        }
                        return false;
                    }
                });
                break;
        }

        //noinspection SimplifiableIfStatement
       /* if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_changepin) {
            Intent intent=new Intent(getApplicationContext(),ProfileActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_download) {
            Intent intent=new Intent(getApplicationContext(),DownloadFileActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_contact) {
            Intent intent=new Intent(getApplicationContext(),ContactUsActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_logout) {
            getSharedPreferences("profile", Context.MODE_PRIVATE).edit().clear().apply();
            Intent intent=new Intent(MainActivity.this,LoginActivity.class);
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
        String[]keys=new String[]{"mode"};
        String[]values=new String[]{"testgetAudiobook"};
        String jsonRequest= Utils.createJsonRequest(keys,values);

        String URL = Config.MAIN_URL;
        new WebserviceCall(MainActivity.this, URL, jsonRequest, "Getting BookList...!!", false, new AsyncResponse() {
            @Override
            public void onCallback(String response) {
                Log.d("myapp",response);
                model = new Gson().fromJson(response,BookListModel.class);
//                Toast.makeText(getApplicationContext(),"Refreshing" , Toast.LENGTH_SHORT).show();
                if (model.getStatus().equalsIgnoreCase("1"))
                {
                    pvHome.setVisibility(View.GONE);
                    tvLoading.setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);
                    for (int i=0;i<model.getAudiobook().size();i++)
                    {
                        audiobookList.add(model.getAudiobook().get(i));
                    }
                    booklistAdapter.notifyDataSetChanged();
//                    BookListAdapter adapter=new BookListAdapter(MainActivity.this,R.layout.booklist_cus_layout,audiobookList,img);
//                    listView.setAdapter(adapter);

                }
//                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                        String s=model.getAudiobook().get(i).getAudio_book_id();
//                        String bkname=model.getAudiobook().get(i).getAudio_book_description().toString();
//                        SharedPreferences preferences=getApplicationContext().getSharedPreferences("bookid", Context.MODE_PRIVATE);
//                        SharedPreferences.Editor editor=preferences.edit();
//                        editor.putString("id",s);
//                        editor.commit();
//                        Intent intent=new Intent(getApplicationContext(),ChapterListActivity.class);
//                        intent.putExtra("bookname",bkname);
//                        startActivity(intent);
//
//                    }
//                });
            }

            @Override
            public void onFailure(String message) {
                pvHome.setVisibility(View.GONE);
                tvLoading.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, ""+message, Toast.LENGTH_SHORT).show();
            }
        }).execute();



    }
    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE,CALL_PHONE}, PERMISSION_REQUEST_CODE);
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

                    if (locationAccepted)
                    {

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
        String s=model.getAudiobook().get(i).getAudio_book_id();
        String bkname=model.getAudiobook().get(i).getAudio_book_description().toString();
        SharedPreferences preferences=getApplicationContext().getSharedPreferences("bookid", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putString("id",s);
        editor.commit();
        Intent intent=new Intent(getApplicationContext(),ChapterListActivity.class);
        intent.putExtra("bookname",bkname);
        startActivity(intent);

    }
}
