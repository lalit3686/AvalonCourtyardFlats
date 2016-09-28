package com.app.avalon.courtyard.flats.screens;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.app.avalon.courtyard.flats.R;
import com.app.avalon.courtyard.flats.adapter.OwnerInfoAdapter;
import com.app.avalon.courtyard.flats.listeners.CustomOnItemClickListener;

/**
 * Created by Lalit.Poptani on 9/28/2016.
 */

public class SupportActivity extends BaseActivity implements CustomOnItemClickListener{

    private RecyclerView listSupportInfo;
    private OwnerInfoAdapter ownerInfoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);

        initComponents();
        addListeners();
    }

    @Override
    public void initComponents() {
        listSupportInfo = (RecyclerView) findViewById(R.id.listSupportInfo);
        listSupportInfo.setHasFixedSize(true);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        listSupportInfo.setLayoutManager(mLinearLayoutManager);
    }

    @Override
    public void addListeners() {
        setListAdapter();
    }

    private void setListAdapter(){
        ownerInfoAdapter = new OwnerInfoAdapter(null, this);
        listSupportInfo.setAdapter(ownerInfoAdapter);
    }

    @Override
    public void onItemClick(View v, int position, Object item) {

    }
}
