/*
 * Copyright 2012 Greg Milette and Adam Stroud
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

import java.util.List;

import root.gast.playground.R;
import root.gast.speech.SpeechRecognizingAndSpeakingActivity;
import root.gast.speech.tts.TextToSpeechUtils;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;


/**
 * Starts a speech recognition dialog and then sends the results to
 * {@link SpeechRecognitionResultsActivity}
 * 
 * @author Greg Milette &#60;<a
 *         href="mailto:gregorym@gmail.com">gregorym@gmail.com</a>&#62;
 */
public class TakeUserResponse extends
        SpeechRecognizingAndSpeakingActivity
{
    private static final String TAG = "SpeechRecognitionLauncher";

    private static final String ON_DONE_PROMPT_TTS_PARAM = "ON_DONE_PROMPT";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }
    @Override
    public void onSuccessfulInit(TextToSpeech tts)
    {
    	
        super.onSuccessfulInit(tts);
        Intent intent = getIntent();
        if(intent.getStringExtra("ActivationType").equals("CALL"))
        {
	        String name = intent.getStringExtra("NAME");
	        String number = intent.getStringExtra("NUMBER");
	        prompt("Would you like to call "+name+" on "+number);
        }
        if(intent.getStringExtra("ActivationType").equals("NOTHING"))
        {
	        prompt("I am sorry this is not a valid command");
        }
        if(intent.getStringExtra("ActivationType").equals("PACKAGE"))
        {
        	String appName = intent.getStringExtra("APP_NAME");
	        prompt("Could not find an application called "+appName);
        }
        if(intent.getStringExtra("ActivationType").equals("SONG"))
        {
        	String songName = intent.getStringExtra("SONG_NAME");
	        prompt("Could not find the song "+songName);
        }
    }

    public void prompt(String promptString)
    {
        Log.d(TAG, "Speak prompt");
        getTts().speak(promptString,
                TextToSpeech.QUEUE_FLUSH,
                TextToSpeechUtils.makeParamsWith(ON_DONE_PROMPT_TTS_PARAM));
    }


    /**
     * super class handles registering the UtteranceProgressListener
     * and calling this
     */
    @Override
    public void onDone(String utteranceId)
    {
    	Intent intent = getIntent();
        if(intent.getStringExtra("ActivationType").equals("CALL"))
        {
	        if (utteranceId.equals(ON_DONE_PROMPT_TTS_PARAM))
	        {
	            Intent recognizerIntent =
	                    new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
	            recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
	                    RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
	            recognizerIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, 
	                    getString(R.string.speech_launcher_prompt));
	            recognize(recognizerIntent);
	        }
        }
        else
        	finish();
        
    }

    @Override
    protected void
            onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE)
        {
            if (resultCode == RESULT_OK)
            {
            	if (data != null)
                {

                    if (data.hasExtra(RecognizerIntent.EXTRA_RESULTS))
                    {
                        List<String> results =
                                data.getStringArrayListExtra(
                                        RecognizerIntent.EXTRA_RESULTS);
                        Intent userResponse = new Intent();
                        for(String result:results)
                        {
        	                if(result.toLowerCase().contains("yes")||result.toLowerCase().contains("ya")||result.toLowerCase().contains("yup"))
        	                {
        	                	userResponse.putExtra("USER_RESPONSE", "yes");
        	                	break;
        	                }
        	                else if(result.toLowerCase().contains("no") || result.toLowerCase().contains("nopes") || result.toLowerCase().contains("cancel"))
        	                {
        	                	userResponse.putExtra("USER_RESPONSE", "no");
        	                	break;
        	                }
        	                else
        	                {
        	                	userResponse.putExtra("USER_RESPONSE", "no");
    	                		break;
        	                }
                        }
                        
                        setResult(RESULT_OK, userResponse);
                    }
                }
            }
        }
        finish();
    }

    @Override
    protected void receiveWhatWasHeard(List<String> heard,
            float[] confidenceScores)
    {
        // satisfy abstract class, this class handles the results directly
        // instead of using this method
    }
}
