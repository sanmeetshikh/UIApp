/*
 * Copyright 2011 Greg Milette and Adam Stroud
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * 		http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package root.gast.playground.speech;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import root.gast.playground.R;
import root.gast.speech.activation.SpeechActivationService;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.util.Log;
import root.gast.playground.speech.SpeechRecognitionLauncher;


/**
 * For displaying the results of the Speech pending intent
 * 
 * @author Greg Milette &#60;<a
 *         href="mailto:gregorym@gmail.com">gregorym@gmail.com</a>&#62;
 */
public class SpeechRecognitionResultsActivity extends Activity
{
    private static final String TAG = "SpeechRecognitionResultsActivity";

    /**
     * for passing in the input
     */
    public static String WHAT_YOU_ARE_TRYING_TO_SAY_INTENT_INPUT =
            "WHAT_YOU_ARE_TRYING_TO_SAY_INPUT";
    public static int CALL_REQUEST =1;
    public static int NOTHING_REQUEST = 2;
    public static int NO_PACKAGE_FOUND = 5;
    public static int MESSAGE_REQUEST = 6;
    public static String number="";
    public static String message = "";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "SpeechRecognition Pending intent received");
        init();
    }
    

    private void init()
    {
        if (getIntent() != null)
        {

            if (getIntent().hasExtra(RecognizerIntent.EXTRA_RESULTS))
            {
                List<String> results =
                        getIntent().getStringArrayListExtra(
                                RecognizerIntent.EXTRA_RESULTS);
                for(String result:results)
                {
                	if(result.toLowerCase().contains("cancel"))
                	{
                		break;
                	}
                	else if(result.toLowerCase().contains("navigate"))
	                {
	                	String[] resultArray=result.split(" ");
	                	ArrayList<String> resultArrayList = new ArrayList<String>(Arrays.asList(resultArray));
	                	int posOfnavigate = resultArrayList.indexOf("navigate");
	                	List<String> destinationList = resultArrayList.subList(posOfnavigate+2, resultArrayList.size());
	                	String destination="";
	                	for (String s : destinationList)
	                	{
	                		destination += s + " ";
	                	}
	                	//finish();
	                	String uri = String.format(Locale.ENGLISH, "geo:0,0?q=%s", destination);
	                	Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
	                	startActivityForResult(intent, 0);
	                	break;
	                }
	                else if(result.toLowerCase().contains("camera"))
	                {
	                	//finish();
	                	try{
	                	Intent cameraIntent = getPackageManager().getLaunchIntentForPackage("com.saycheese");
	                	startActivityForResult(cameraIntent, 0);
	                	}
	                	catch(Exception e)
	                	{
	                		//catch exception
	                	}
	                	break;
	                }
	                else if(result.toLowerCase().contains("time"))
	                {
	                	//finish();
	                	try{
	                		
	                		Calendar timeNow = Calendar.getInstance();
	                		int hour =timeNow.get(Calendar.HOUR);
	                		int minutes =timeNow.get(Calendar.MINUTE);
	                		int ampm = timeNow.get(Calendar.AM_PM);
	                		Intent i = new Intent(this, TakeUserResponse.class);
		                	i.putExtra("HOUR", Integer.toString(hour));
		                	i.putExtra("MIN", Integer.toString(minutes));
		                	i.putExtra("AMPM", Integer.toString(ampm));
		                	i.putExtra("ActivationType", "TIME");
		                	this.startActivityForResult(i, NO_PACKAGE_FOUND);
	                		
	                	}
	                	catch(Exception e)
	                	{
	                		//catch exception
	                	}
	                	break;
	                }
	                else if(result.toLowerCase().contains("search"))
	                {
	                	String[] resultArray=result.split(" ");
	                	ArrayList<String> resultArrayList = new ArrayList<String>(Arrays.asList(resultArray));
	                	int posOfFor = resultArrayList.indexOf("for");
	                	List<String> searchList = resultArrayList.subList(posOfFor+1, resultArrayList.size());
	                	String searchString="";
	                	for (String s : searchList)
	                	{
	                		searchString += s + " ";
	                	}
	                	String uri = String.format(Locale.ENGLISH, "http://www.google.com/#q=%s", searchString);
	                	
	                	Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
	                	startActivityForResult(intent, 3);
	                	break;
	                }
	                else if(result.toLowerCase().contains("call"))
	                {
	                	String[] resultArray=result.split(" ");
	                	ArrayList<String> resultArrayList = new ArrayList<String>(Arrays.asList(resultArray));
	                	int posOfCall = resultArrayList.indexOf("call");
	                	List<String> nameList = resultArrayList.subList(posOfCall+1, resultArrayList.size());
	                	String name="";
	                	for (String s : nameList)
	                	{
	                		name += s + " ";
	                	}
	                	String number = getPhoneNumber(name, this);
	                	this.number = number;
	                	Log.d(TAG, "Name: "+name+" Number: "+number);
	                	Intent i = new Intent(this, TakeUserResponse.class);
	                	i.putExtra("NAME", name);
	                	i.putExtra("NUMBER", number);
	                	i.putExtra("ActivationType", "CALL");
	                	this.startActivityForResult(i, CALL_REQUEST);
	                	break;
	                	
	                }
	                else if(result.toLowerCase().contains("text"))
	                {
	                	result = result.toLowerCase();
	                	String[] resultArray=result.split(" ");
	                	ArrayList<String> resultArrayList = new ArrayList<String>(Arrays.asList(resultArray));
	                	int posOfText = resultArrayList.indexOf("text");
	                	int posOfMessage = resultArrayList.indexOf("message");
	                	List<String> nameList = resultArrayList.subList(posOfText+1, posOfMessage);
	                	List<String> messageList = resultArrayList.subList(posOfMessage+1, resultArrayList.size());
	                	String name="";
	                	String message = "";
	                	for (String s : nameList)
	                	{
	                		name += s + " ";
	                	}
	                	for (String s : messageList)
	                	{
	                		message += s + " ";
	                	}
	                	if(name.equals("") || message.equals("")) 
	                	{
	                		Intent i = new Intent(this, TakeUserResponse.class);
		                	i.putExtra("ActivationType", "MESSAGE_ERROR");
		                	this.startActivityForResult(i, CALL_REQUEST);
	                		break;
	                	}
	                	
	                	String number = getPhoneNumber(name, this);
	                	this.number = number;
	                	this.message = message;
	                	Log.d(TAG, "Name: "+name+" Number: "+number+" Message: "+message);
	                	Intent i = new Intent(this, TakeUserResponse.class);
	                	i.putExtra("NAME", name);
	                	i.putExtra("NUMBER", number);
	                	i.putExtra("MESSAGE", message);
	                	i.putExtra("ActivationType", "MESSAGE");
	                	this.startActivityForResult(i, MESSAGE_REQUEST);
	                	break;
	                	
	                }
	                else if(result.toLowerCase().contains("play"))
	                {
	                	String[] resultArray=result.split(" ");
	                	ArrayList<String> resultArrayList = new ArrayList<String>(Arrays.asList(resultArray));
	                	int posOfPlay = resultArrayList.indexOf("play");
	                	List<String> nameList = resultArrayList.subList(posOfPlay+1, resultArrayList.size());
	                	String name="";
	                	for (String s : nameList)
	                	{
	                		name += s + " ";
	                	}
	                	if(name.equals(""))
	                	{
	                		break;
	                	}
	                	String id = "";
	                	if(name.toLowerCase().equals("music"))
	                		id = getIdForSong("", this);
	                	else
	                		id = getIdForSong(name, this);
	                	//Intent intent = new Intent("android.intent.action.MUSIC_PLAYER");
	                	//startActivity(intent);
	                	if(id!=null)
	                	{
	                	Uri uri = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);   
	                	Intent it = new Intent(Intent.ACTION_VIEW, uri);   
	                	startActivity(it);
	                	}
	                	else
	                	{
	                		Intent i = new Intent(this, TakeUserResponse.class);
		                	i.putExtra("SONG_NAME", name);
		                	i.putExtra("ActivationType", "SONG");
		                	this.startActivityForResult(i, NO_PACKAGE_FOUND);
		                	break;
	                	}
	                	break;
	                }
	                else if(result.toLowerCase().contains("weather"))
	                {
	                	String uri = String.format(Locale.ENGLISH, "http://www.google.com/#q=weather");
	                	
	                	Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
	                	startActivityForResult(intent, 3);
	                	break;
	                	
	                }
	                else if(result.toLowerCase().contains("open"))
	                {
	                	
	                	String[] resultArray=result.split(" ");
	                	ArrayList<String> resultArrayList = new ArrayList<String>(Arrays.asList(resultArray));
	                	int posOfOpen = resultArrayList.indexOf("open");
	                	List<String> nameList = resultArrayList.subList(posOfOpen+1, resultArrayList.size());
	                	String appName="";
	                	for (String s : nameList)
	                	{
	                		appName += s + " ";
	                	}
	                	
	                	if(appName.equals(""))
	                	{
	                		break;
	                	}
	                	String packageName = getAppPackage(appName);
	                	
	                	if(packageName!=null)
	                	{
	                		Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);
		                	startActivityForResult(intent, 0);
	                		break;
	                	}
	                	
	                	else
	                	{
	                		Intent i = new Intent(this, TakeUserResponse.class);
		                	i.putExtra("APP_NAME", appName);
		                	i.putExtra("ActivationType", "PACKAGE");
		                	this.startActivityForResult(i, NO_PACKAGE_FOUND);
		                	break;
	                	}
	                }
	                else
	                {
	                	Intent i = new Intent(this, TakeUserResponse.class);
	                	i.putExtra("ActivationType", "NOTHING");
	                	this.startActivityForResult(i, NOTHING_REQUEST);
	                	break;
	                }
                }

            }
            else
            {
                // if RESULT_EXTRA is not present, the recognition had an
                // error
                DialogInterface.OnClickListener onClickFinish =
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which)
                            {
                                finish();
                            }
                        };
                AlertDialog a =
                        new AlertDialog.Builder(this)
                                .setTitle(
                                        getResources().getString(
                                                R.string.d_info))
                                .setMessage(
                                        getResources()
                                                .getString(
                                                 R.string.
                                                 speechRecognitionFailed))
                                .setPositiveButton(
                                        getResources().getString(R.string.d_ok),
                                        onClickFinish).create();
                a.show();
            }
        }
    }
    
    public String getPhoneNumber(String name, Context context) {
    	String ret = null;
    	name = name.trim();
    	String selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" like'%" + name +"%'";
    	String[] projection = new String[] { ContactsContract.CommonDataKinds.Phone.NUMBER};
    	Cursor c = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
    	        projection, selection, null, null);
    	if (c.moveToFirst()) {
    	    ret = c.getString(0);
    	}
    	c.close();
    	if(ret==null)
    	    ret = "Unsaved";
    	return ret;
    	}
    
    public String getIdForSong(String name, Context context)
    {
    	 String ret = null;
    	name = name.trim();
    	String selection = MediaStore.Audio.Media.DISPLAY_NAME +" like'%" + name +"%'";
        String[] projection = new String[] { MediaStore.Audio.Media._ID};

        Cursor c = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
    	        projection, selection, null, null);
        
        if (c.moveToFirst()) {
    	    ret = c.getString(0);
    	}
    	c.close();

    	return ret;
    }
    
    
    
    public String getAppPackage(String appName) {
    	appName = appName.trim();
    	final PackageManager pm = getPackageManager();
    	//get a list of installed apps.
    	List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
    	
    	for (ApplicationInfo packageInfo : packages) {
    		Log.d(TAG, "Application Name :"+((String)pm.getApplicationLabel(packageInfo)));
    		if(((String)pm.getApplicationLabel(packageInfo)).toLowerCase().contains(appName.toLowerCase()))
    		{
	    	    Log.d(TAG, "Installed package :" + packageInfo.packageName);
	    	    if(pm.getLaunchIntentForPackage(packageInfo.packageName)!=null)
	    	    {
	    	    	return packageInfo.packageName;
	    	    }
	    	    //Log.d(TAG, "Launch Activity :" + pm.getLaunchIntentForPackage(packageInfo.packageName));
    		}
    	    
    	}
    	return null;
    }
    
    public void
    onWindowFocusChanged(boolean hasFocus)
    {
    		super.onWindowFocusChanged(hasFocus);
    		if(hasFocus)
    		{
	    		String activationType = SpeechActivationServicePlay.getActivationType();
	        	Intent i = SpeechActivationService.makeStartServiceIntent(this, activationType);
	        	this.startService(i);
	        	finish();
    		}
    }
    
    @Override
    protected void
            onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CALL_REQUEST)
        {
            if (resultCode == RESULT_OK)
            {
            	if (data != null)
                {
            		if(data.hasExtra("USER_RESPONSE"))
            		{
            			if(data.getStringExtra("USER_RESPONSE").equals("yes"))
            			{
            				if(!number.equals("")&&!number.equals("Unsaved"))
            				{
            					String uri = String.format(Locale.ENGLISH, "tel:%s",number);
            					Intent callIntent = new Intent(Intent.ACTION_CALL);
            					callIntent.setData(Uri.parse(uri));
            					startActivity(callIntent);
            				}
            			}
            		}
                }
            }
        }

        if (requestCode == MESSAGE_REQUEST)
        {
            if (resultCode == RESULT_OK)
            {
            	if (data != null)
                {
            		if(data.hasExtra("USER_RESPONSE"))
            		{
            			if(data.getStringExtra("USER_RESPONSE").equals("yes"))
            			{
            				if(!number.equals("")&&!number.equals("Unsaved"))
            				{
            					Uri uri = Uri.parse("smsto:"+number);   
            					Intent it = new Intent(Intent.ACTION_SENDTO, uri);   
            					it.putExtra("sms_body", message);   
            					startActivity(it);
            				}
            			}
            		}
                }
            }
        }
    }
}
