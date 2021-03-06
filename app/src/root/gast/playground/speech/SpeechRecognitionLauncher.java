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
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import root.gast.speech.activation.SpeechActivationService;
import root.gast.playground.speech.SpeechActivationServicePlay;

/**
 * Starts a speech recognition dialog and then sends the results to
 * {@link SpeechRecognitionResultsActivity}
 * 
 * @author Greg Milette &#60;<a
 *         href="mailto:gregorym@gmail.com">gregorym@gmail.com</a>&#62;
 */
public class SpeechRecognitionLauncher extends
        SpeechRecognizingAndSpeakingActivity
{
    private static final String TAG = "SpeechRecognitionLauncher";

    private static final String ON_DONE_PROMPT_TTS_PARAM = "ON_DONE_PROMPT";
    
    String selectedString="";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
    	Window window = this.getWindow();
    	window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
    	window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        super.onCreate(savedInstanceState);
    }
    @Override
    public void onSuccessfulInit(TextToSpeech tts)
    {
    	
        super.onSuccessfulInit(tts);
        
        String[] prompts = {getString(R.string.speech_launcher_prompt), "How can I help you?", "What can I do for you?",
        					"How can I assist you?", "I am at your service"};
        int selection = (int)(Math.random()*prompts.length);
        selectedString = prompts[selection];
        prompt(prompts[selection]);
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
        if (utteranceId.equals(ON_DONE_PROMPT_TTS_PARAM))
        {
            Intent recognizerIntent =
                    new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, 
            		selectedString);
            recognize(recognizerIntent);
        }
        
        
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
                Intent showResults = new Intent(data);
                showResults.setClass(this,
                        SpeechRecognitionResultsActivity.class);
                startActivity(showResults);
            }
        }
        //this.moveTaskToBack(true);
       // String activationType = SpeechActivationServicePlay.getActivationType();
       // Intent i = SpeechActivationService.makeStartServiceIntent(this, activationType);
       // this.startService(i);
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
