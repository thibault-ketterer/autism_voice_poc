package org.lankou.autism_voice_poc;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import android.util.Log;
// import android.speech.RecognizerIntent;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;


public class welcome_screen extends AppCompatActivity implements TextToSpeech.OnInitListener {

    TextView TextViewTop;
    TextView TextResult;
    ImageView imageView;

    private String[] words = { "le chien", "un chaat", "un camion", "une poupée", "une voiture", "Flash MacQueen" };
    // maman papa chloé famille maison papa maison maman école julie

    private TextToSpeech textToSpeechSystem;
    //status check code
    private static final int MY_DATA_CHECK_CODE = 12;

    //setup TTS
    public void onInit(int initStatus) {

        //check for successful instantiation
        if (initStatus == TextToSpeech.SUCCESS) {
            if(textToSpeechSystem.isLanguageAvailable(Locale.FRANCE)==TextToSpeech.LANG_AVAILABLE)
                textToSpeechSystem.setLanguage(Locale.FRANCE);
        }
        else if (initStatus == TextToSpeech.ERROR) {
            Toast.makeText(this, "Sorry! Text To Speech failed...", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //check for TTS data
        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        imageView = (ImageView) findViewById(R.id.imageView);
        TextViewTop = (TextView) findViewById(R.id.textViewTop);

        TextResult = (TextView) findViewById(R.id.txt_res);

        Button btnDO = (Button) findViewById(R.id.btnDO);
        btnDO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speakWords("bonjour");
                TextViewTop.setText("super");
                //loadImage("chien");
            }
        });

        Button btnSpeech = (Button) findViewById(R.id.buttonSpeech);
        btnSpeech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //TODO make it an option
                // speakWords("dit chien");
                Random rand = new Random();
                String randomElement = words[rand.nextInt(words.length)];
                speakWords("dit,\" je veux, " + randomElement + "\"");

                TextViewTop.setText("SST start");
                getSpeechInput();
            }
        });
        // cannot speak for now, not initialized
    }

    //speak the user text
    private void speakWords(String speech) {

        //speak straight away
        textToSpeechSystem.speak(speech, TextToSpeech.QUEUE_FLUSH, null, null);
        while (textToSpeechSystem.isSpeaking()){
            Log.v("TTS","Do something or nothing while speaking..");
            try {
                Thread.sleep(100); //TODO is it the right way to lock app ?
            } catch(InterruptedException e)
            {
                //ignore or quit TODO
            }
        }
    }


    public void getSpeechInput() {

        // audio.stop();
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 10);
        } else {
            Toast.makeText(this, "Your Device Don't Support Speech Input", Toast.LENGTH_SHORT).show();
        }
    }

    private String loadImageFromGoogle(String qry) throws Exception {

        TextViewTop.setText("ENTER");
        String key="AIzaSyA-cBj9h2J6fBbiNg5FxQ86HYFQpFYNA7Q";
        //String qry="Android";
        URL url = new URL(
                "https://www.googleapis.com/customsearch/v1?key="+key+ "&cx=013036536707430787589:_pqjad5hr1a&q="+ qry + "&alt=json");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
        BufferedReader br = new BufferedReader(new InputStreamReader(
                (conn.getInputStream())));
        TextViewTop.setText("OK");

        String output;
        String link;
        link = "";
        System.out.println("Output from Server .... \n");

        TextViewTop.setText("getting from server");
        while ((output = br.readLine()) != null) {

            if(output.contains("\"link\": \"")){
                link = output.substring(output.indexOf("\"link\": \"")+("\"link\": \"").length(), output.indexOf("\","));
                System.out.println(link);       //Will print the google search links
                TextViewTop.setText("got link[" + link);
            }
        }
        conn.disconnect();
        return(link);
    }

    private void loadImage(String text){


        // new DownloadImageTask(imageView).execute("https://cdn.journaldev.com/wp-content/uploads/2012/11/java-catch-multiple-exceptions-rethrow-exceptions.png");

        Log.v("BAHHHHHH", "enter load image");
        //String url = "http://i.imgur.com/DvpvklR.png";
        String url;
        if("chat".equals(text)){
            url = "http://www.lankou.org/autism_voice_poc/chat.png";
        } else if("chien".equals(text)){
            url = "http://www.lankou.org/autism_voice_poc/chien.png";
        } else if("camion".equals(text)){
            url = "http://www.lankou.org/autism_voice_poc/camion.jpg";
        } else if("banane".equals(text)){
            url = "http://www.lankou.org/autism_voice_poc/banane.jpg";
        } else if("flash".equals(text)){
            url = "http://www.lankou.org/autism_voice_poc/flash.jpg";
        } else {
            Log.v("DEBUG", text);
            // url = "http://www.lankou.org/autism_voice_poc/get_image?q=" + text;
            url = "http://www.lankou.org:14333/get_image?q=" + text;
//
//            url = "http://i.imgur.com/DvpvklR.png";
        }
        /* try {
            url = loadImageFromGoogle(text);
        } catch(Exception e) {
            Log.v("BAHHHHHH", e.toString());
            e.printStackTrace();

        } */

        Glide.with(this)

                //.load("https://40.media.tumblr.com/f49e56a443aecd533fb53d55a1cf1408/tumblr_nsc4fht5ol1u3hv5ko1_1280.jpg")
                .load(url)

                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String s, Target<GlideDrawable> glideDrawableTarget, boolean b) {
                        Log.d("Glide", "Error in Glide listener");
                        if (e != null) {
                            e.printStackTrace();
                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable glideDrawable, String s, Target<GlideDrawable> glideDrawableTarget, boolean b, boolean b2) {
                        return false;
                    }
                })
                .override(600, 600)
                .placeholder(android.R.drawable.ic_menu_call)
                .error(android.R.drawable.ic_delete)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate()
                .into(imageView);


        // https://stackoverflow.com/questions/5776851/load-image-from-url
        // https://stackoverflow.com/questions/15356473/create-imageviews-dynamically-inside-a-loop
        // ?? https://gist.github.com/alexzaitsev/75c9b36dfcffd545c676
    }


    /*private void createImages(String text) {
        LinearLayout layout = (LinearLayout) findViewById(R.id.imageLayout);
        for (int i = 0; i < 10; i++) {
            ImageView image = new ImageView(this);
            image.setLayoutParams(new android.view.ViewGroup.LayoutParams(80, 60));
            image.setMaxHeight(20);
            image.setMaxWidth(20);

            // Adds the view to the layout
            layout.addView(image);
        }
    }*/

    /*
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus) {
            speakWords("bonjour ! Bienvenue, appuie sur le bouton 2");
        }
    }
    called after every focus
    */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 10:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    TextResult.setText(result.get(0));
                    //for (int i = 0; i < result.size(); i++) {
                    Log.v("oAR", "--------- recognized[" + result + "]");
                    //}

                    /* ------------------ confidence display --------------- */

                    // The confidence array
                    float[] confidence = data.getFloatArrayExtra(
                            RecognizerIntent.EXTRA_CONFIDENCE_SCORES);

                    // The confidence results
                    for (int i = 0; i < confidence.length; i++) {
                        if (confidence[i] != 0.0) {
                            Log.v("oAR", "confidence[" + i + "] = " + confidence[i]);
                        }
                    }

                    // remove "je veux"
                    String sanitizedResult = result.get(0).toLowerCase();
                    if (sanitizedResult.startsWith("je veux")) {
                        Log.v("oAR", "je veux matched index[" + sanitizedResult.indexOf("je veux") + "]");
                        sanitizedResult = sanitizedResult.substring(sanitizedResult.indexOf("je veux") + "je veux".length()).trim();
                    }
                    Log.v("oAR", "string is now[" + sanitizedResult + "]");

                    loadImage(sanitizedResult);
                    TextViewTop.setText("SST end");
                }
                break;
            case MY_DATA_CHECK_CODE:
                if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                    //the user has the necessary data - create the TTS
                    textToSpeechSystem = new TextToSpeech(this, this);
                } else {
                    //no data - install it now
                    Intent installTTSIntent = new Intent();
                    installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                    startActivity(installTTSIntent);
                }
                break;
        }
    }


    public void onDestroy() {
        // Don't forget to shutdown!
        if (textToSpeechSystem != null) {
            textToSpeechSystem.stop();
            textToSpeechSystem.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_welcome_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

// TTS https://code.tutsplus.com/tutorials/android-sdk-using-the-text-to-speech-engine--mobile-8540
// https://stackoverflow.com/questions/30706780/texttospeech-deprecated-speak-function-in-api-level-21

// glide http://bumptech.github.io/glide/doc/download-setup.html
// https://github.com/bumptech/glide
// https://bumptech.github.io/glide/ref/samples.html
// https://bumptech.github.io/glide/
// https://github.com/codepath
// https://bumptech.github.io/glide/doc/caching.html
// https://bumptech.github.io/glide/javadocs/400/com/bumptech/glide/load/engine/DiskCacheStrategy.html

// google image search https://github.com/Trinitok/Google-Image-Search-with-Java-/blob/master/GoogleImageSearch.java
// https://stackoverflow.com/questions/36002916/downloading-images-from-google-image-search-using-java