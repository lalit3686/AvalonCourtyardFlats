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
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.app.avalon.courtyard.flats.R;
import com.app.avalon.courtyard.flats.adapter.OwnerInfoAdapter;
import com.app.avalon.courtyard.flats.application.MyApplication;
import com.app.avalon.courtyard.flats.beans.OwnerDetails;
import com.app.avalon.courtyard.flats.commons.AppConstants;
import com.app.avalon.courtyard.flats.commons.FirebaseUtils;
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
    private List<String> callList = new ArrayList<String>();
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        FirebaseUtils.getBlockInfo();
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void setUpRecyclerView(){
        listViewInfo = (RecyclerView) findViewById(R.id.list_info);
        listViewInfo.setHasFixedSize(true);
        LinearLayoutManager infoLinearLayoutManager = new LinearLayoutManager(this);
        infoLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        listViewInfo.setLayoutManager(infoLinearLayoutManager);
    }

    private void setUpFloatingActionButton(){
        fabAdd = (FloatingActionButton) findViewById(R.id.fab_add);
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
        setInfoListAdapter();
    }

    private void setInfoListAdapter(){
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
                //startActivity(new Intent(context, SupportActivity.class));
                break;
            case R.id.action_about_me:
                startActivity(new Intent(context, AboutMeActivity.class));
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_add:
                break;
        }
    }

    @Override
    public void onItemClick(View v, int position, Object item) {
        OwnerDetails details = (OwnerDetails) item;

        List<String> listItems = new ArrayList<>();
        if(details.CellOne != null && !TextUtils.isEmpty(details.CellOne)){
            listItems.add(details.CellOne);
        }
        if(details.CellTwo != null && !TextUtils.isEmpty(details.CellTwo)){
            listItems.add(details.CellTwo);
        }

        if(listItems.size() > 0){
            CharSequence[] items = new CharSequence[listItems.size()];
            showCallDialog(details.Name, listItems.toArray(items));
        }
        else{
            Snackbar.make(v, "Sorry, "+details.Name+" deson't have any communication number!", Snackbar.LENGTH_LONG).show();
        }
    }

    private void callIntent(CharSequence cellNo){
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + cellNo));
        startActivity(intent);
    }

    private void showCallDialog(String ownerName, final CharSequence[] items){
        final CharSequence[] selectedCellNo = new CharSequence[1];
        selectedCellNo[0] = items[0];

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Do you wish to call "+ownerName+"?");
        builder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                switch (which){
                    case 0:
                        selectedCellNo[0] = items[which];
                        break;
                    case 1:
                        selectedCellNo[0] = items[which];
                        break;
                }

            }
        });
        builder.setPositiveButton("Call", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                callIntent(selectedCellNo[0]);
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
