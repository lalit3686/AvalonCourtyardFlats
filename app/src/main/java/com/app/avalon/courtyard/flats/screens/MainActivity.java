package com.app.avalon.courtyard.flats.screens;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.app.avalon.courtyard.flats.R;
import com.app.avalon.courtyard.flats.adapter.OwnerInfoAdapter;
import com.app.avalon.courtyard.flats.application.MyApplication;
import com.app.avalon.courtyard.flats.beans.OwnerDetails;
import com.app.avalon.courtyard.flats.commons.AppConstants;
import com.app.avalon.courtyard.flats.commons.Utils;
import com.app.avalon.courtyard.flats.listeners.CustomOnItemClickListener;
import com.google.firebase.database.DataSnapshot;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Lalit.Poptani on 9/26/2016.
 */

public class MainActivity extends BaseActivity implements View.OnClickListener, CustomOnItemClickListener, SearchView.OnQueryTextListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView listViewInfo;
    private OwnerInfoAdapter ownerInfoAdapter;
    private FloatingActionButton fabAdd;
    private List<OwnerDetails> ownerDetailsList = new ArrayList<OwnerDetails>();
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        Utils.getBlockInfo();
        initComponents();
        addListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerEventBus();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unRegisterEventBus();
    }

    @Subscribe
    public void onEvent(LinkedList<Object> event){

        int eventType = (int) event.get(0);
        DataSnapshot dataSnapshot = (DataSnapshot) event.get(1);

        switch (eventType){
            case AppConstants.MESSAGE_ADDED:
                ownerInfoAdapter.messageAdded(dataSnapshot);
                break;
            case AppConstants.MESSAGE_UPDATED:
                ownerInfoAdapter.messageUpdated(dataSnapshot);

                break;
            case AppConstants.MESSAGE_DELETED:
                ownerInfoAdapter.messageDeleted(dataSnapshot);
                break;
        }
    }

    private void registerEventBus(){
        MyApplication.getEventBusInstance().register(this);
    }

    private void unRegisterEventBus(){
        MyApplication.getEventBusInstance().unregister(this);
    }

    private void setUpToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void setUpRecyclerView(){
        listViewInfo = (RecyclerView) findViewById(R.id.listViewInfo);
        listViewInfo.setHasFixedSize(true);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        listViewInfo.setLayoutManager(mLinearLayoutManager);
    }

    private void setUpFloatingActionButton(){
        fabAdd = (FloatingActionButton) findViewById(R.id.fabAdd);
    }

    @Override
    public void initComponents(){
        setUpToolbar();
        setUpRecyclerView();
        setUpFloatingActionButton();
    }

    @Override
    public void addListeners(){
        fabAdd.setOnClickListener(this);
        setListAdapter();
    }

    private void setListAdapter(){
        ownerInfoAdapter = new OwnerInfoAdapter(ownerDetailsList, this);
        listViewInfo.setAdapter(ownerInfoAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        searchView.setOnQueryTextListener(this);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_support:
                startActivity(new Intent(context, SupportActivity.class));
                break;
            case R.id.action_about_me:
                startActivity(new Intent(context, AboutMeActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fabAdd:
                break;
        }
    }

    @Override
    public void onItemClick(View v, int position, Object item) {
        OwnerDetails details = (OwnerDetails) item;

        if(details.CellOne != null){
            showCallDialog(details.Name, details.CellOne);
        }
        else if(details.CellTwo != null){
            showCallDialog(details.Name, details.CellOne);
        }
        else{
            Snackbar.make(v, "Sorry, can't call!", Snackbar.LENGTH_LONG).show();
        }
    }

    private void showCallDialog(String ownerName, final String cellNo){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Do you wish to call "+ownerName+"?");
        builder.setPositiveButton("Call", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + cellNo));
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setCancelable(true);
        builder.create().show();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        ownerInfoAdapter.getFilter().filter(newText);
        return false;
    }
}
