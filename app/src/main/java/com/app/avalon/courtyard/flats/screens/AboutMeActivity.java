package com.app.avalon.courtyard.flats.screens;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.avalon.courtyard.flats.BuildConfig;
import com.app.avalon.courtyard.flats.R;
import com.app.avalon.courtyard.flats.application.MyApplication;
import com.app.avalon.courtyard.flats.beans.AboutMeDetails;
import com.app.avalon.courtyard.flats.commons.Utils;
import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.Subscribe;

/**
 * Created by Lalit.Poptani on 9/28/2016.
 */

public class AboutMeActivity extends BaseActivity implements View.OnClickListener {

    private FloatingActionButton fabEmail;
    private TextView textViewAboutMeInfo, textViewName, textViewAddress, textViewMobile, textViewEmail;
    private ImageView imageViewProfilePic;
    private AboutMeDetails aboutMeDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_me);

        Utils.getAboutMeInfo();
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

    private void registerEventBus(){
        MyApplication.getEventBusInstance().register(this);
    }

    private void unRegisterEventBus(){
        MyApplication.getEventBusInstance().unregister(this);
    }

    @Subscribe
    public void onEvent(AboutMeDetails aboutMeDetails){
        if(aboutMeDetails != null){
            this.aboutMeDetails = aboutMeDetails;
            updateViews();
            showUserProfilePicture();
        }
    }

    @Override
    public void initComponents() {
        fabEmail = (FloatingActionButton) findViewById(R.id.fabEmail);
        textViewAboutMeInfo = (TextView) findViewById(R.id.textViewAboutMeInfo);
        textViewName = (TextView) findViewById(R.id.textViewName);
        textViewAddress = (TextView) findViewById(R.id.textViewAddress);
        textViewMobile = (TextView) findViewById(R.id.textViewMobile);
        textViewEmail = (TextView) findViewById(R.id.textViewEmail);
        imageViewProfilePic = (ImageView) findViewById(R.id.imageViewProfilePic);
    }

    private void updateViews(){
        textViewName.setText(aboutMeDetails.Name);
        textViewAddress.setText(aboutMeDetails.address);
        textViewEmail.setText(aboutMeDetails.mailTo);
        textViewMobile.setText(aboutMeDetails.MobileNo);
        textViewAboutMeInfo.setText(aboutMeDetails.message);
    }

    @Override
    public void addListeners() {
        fabEmail.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Snackbar.make(v, "Click Send Button to Open Mail App", Snackbar.LENGTH_LONG).setAction("Send", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", aboutMeDetails.mailTo, null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback on Madhav App");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "App Version : " + BuildConfig.VERSION_NAME);
                startActivity(Intent.createChooser(emailIntent, "Send email..."));
            }
        });
    }

    private void showUserProfilePicture() {
        Glide.with(this).load(aboutMeDetails.profilePic != null ? aboutMeDetails.profilePic : "")
                .crossFade().override(400, 400).placeholder(R.mipmap.ic_launcher).into(imageViewProfilePic);
    }
}
