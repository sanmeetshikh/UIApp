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
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
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
    public static String number="";

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
	                if(result.toLowerCase().contains("navigate"))
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
    
    public boolean checkAppRunning(String app)
    {
    	ActivityManager activityManager = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
        for(int i = 0; i < procInfos.size(); i++){
            if(procInfos.get(i).processName.equals(app)) {
                return true;
            }
        }
        return false;
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
    }
}
