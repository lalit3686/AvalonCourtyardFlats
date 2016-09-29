package com.app.avalon.courtyard.flats.application;

import android.app.Application;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

public class MyApplication extends Application{

	private static final String TAG = MyApplication.class.getSimpleName();
	private static MyApplication instance;
	private static Gson gson;

	@Override
	public void onCreate() {
		super.onCreate();
		
		instance = this;
	}
	
	public static MyApplication getApplicationInstance() {
		return instance;
	}

	public static Gson getGsonInstance(){
		if(gson == null){
			gson = new Gson();
		}
		return gson;
	}

	public static EventBus getEventBusInstance(){
		return EventBus.getDefault();
	}
}
