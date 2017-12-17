package live.periodico.tendencias.hoy.noticias.noticiasdehoy;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends Activity {

    WebView web;
    Context context = this;
    static InputStream is = null;
    static JSONArray jObj = null;
    static String json = "";


    InterstitialAd mInterstitialAd;
    private AdView mAdView;
    private Context _cont;

    ProgressBar progres;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        web = (WebView) findViewById(R.id.web);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //              Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                web.loadUrl(Constants.url);
            }
        });


        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(Constants.intert);
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
                // beginPlayingGame();
            }
        });

        requestNewInterstitial();


        // Load an ad into the AdMob banner view.
        _cont = this;
        mAdView = new AdView(this);
        mAdView.setAdUnitId(Constants.banner);
        if(isTabletDevice()){
            // Poniendole el tamano del ADS
            AdSize customAdSize = new AdSize(468, 60);
            mAdView.setAdSize(customAdSize);
        }else{
            mAdView.setAdSize(AdSize.SMART_BANNER);
        }
        FrameLayout layout = (FrameLayout) findViewById(R.id.unity);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        //       params.addRule(FrameLayout.ALIGN_PARENT_BOTTOM, -1);
        layout.addView(mAdView, params);
        mAdView.loadAd(new AdRequest.Builder().build());





        final SharedPreferences[] sharpref = {getSharedPreferences("calificya", Context.MODE_PRIVATE)};



        sharpref[0] = getPreferences(Context.MODE_PRIVATE);
        Boolean valor = sharpref[0].getBoolean ("Mydata", false);


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        getJson("https://noticiasconttroll.blogspot.com/");

        progres = (ProgressBar) findViewById(R.id.progres);

        web = (WebView) findViewById(R.id.web);
        // Activo JavaScript
        web.loadUrl(Constants.url);
        //       web.getSettings().setUserAgentString("Mozilla/5.0 (X11; Linux i686) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.16 Safari/537.36");
        web.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress)
            {
                //Make the bar disappear after URL is loaded, and changes string to Loading...
                //     setTitle("Loading...");
                //         setProgress(progress * 100); //Make the bar disappear after URL is loaded

                // Return the app name after finish loading
                if(progress == 100)
                    //                  setTitle(R.string.app_name);
                    progres.setVisibility(View.INVISIBLE);
                mInterstitialAd.isLoaded();
                mInterstitialAd.show();
            }
        });







//     WHATSAPP 1
        web.getSettings().setDomStorageEnabled(true);
        web.setWebViewClient(new WebViewClient());
        web.getSettings().setGeolocationEnabled(true);
        //使用javascript
        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setDomStorageEnabled(true);
        //       web.setInitialScale(zoom);
//        web.getSettings().setSupportZoom(true);
//        web.getSettings().setBuiltInZoomControls(true);
//        web.getSettings().setDisplayZoomControls(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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


    @Override
    public void onBackPressed() {
        // no vamos a hacer nada aquí
    }




    public static void getJson(String Url){

        try{

            try {
                // Getting JSON string from URL ------ Used JSON Array froam Android
                JSONArray json = getJSONFromUrlBlogger(Url);
                for (int i = 0; i < json.length(); i++) {
                    JSONObject c = json.getJSONObject(i);


                    Constants.esconder =  c.getBoolean("Esconder");
                    Constants.url = c.getString("Url");

                    Constants.intert = c.getString("Intert");
                    Constants.banner = c.getString("Banner");



                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }


    public static JSONArray getJSONFromUrlBlogger(String url) {


        try
        {
            String Soure = getHtml(url);
            String[] items = Soure.toString().split("ACJSON");
            json = items[1];

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        // try parse the string to a JSON object
        try {
            jObj = new JSONArray(json);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }

        // return JSON String
        return jObj;

    }

    public static String getHtml(String url) throws IOException {
        // Build and set timeout values for the request.
        URLConnection connection = (new URL(url)).openConnection();
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(10000);
        connection.connect();

        // Read and store the result line by line then return the entire string.
        InputStream in = connection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder html = new StringBuilder();
        for (String line; (line = reader.readLine()) != null; ) {
            html.append(line);
        }
        in.close();

        return html.toString();
    }



    public void Calificanos(){
        Constants.calificanosya = true;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Si te ha Gustado nuestra App, Te Invito a Valorar Esta Aplicación" +" "+
                "¡Gracias por tu Apoyo!")
                .setTitle("Valorar la Aplicación")
                .setCancelable(false)
                .setNegativeButton("¡No Gracias!",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();

                            }
                        })
                .setPositiveButton("Valorar Ahora",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //          TransferirDinero(); // metodo que se debe implementar
                                Intent e = new Intent(Intent.ACTION_VIEW);
                                e.setData(Uri.parse("https://play.google.com/store/apps/details?id="+BuildConfig.APPLICATION_ID));
                                startActivity(e);

                                SharedPreferences sharpref = getPreferences(Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharpref.edit();
                                editor.putBoolean("Mydata", true);
                                editor.commit();

                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }



    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                //  .addTestDevice("ca-app-pub-6504482341147597/7234681067")
                .addTestDevice(Constants.intert)

                .build();

        mInterstitialAd.loadAd(adRequest);


    }




    public boolean isTabletDevice() {

        TelephonyManager telephony = (TelephonyManager) _cont
                .getSystemService(Context.TELEPHONY_SERVICE);
        int type = telephony.getPhoneType();

        if (type == TelephonyManager.PHONE_TYPE_NONE) {
            return true;

        }
        return false;
    }
}
