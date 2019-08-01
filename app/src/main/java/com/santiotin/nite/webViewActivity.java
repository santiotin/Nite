package com.santiotin.nite;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.santiotin.nite.Models.Event;

public class webViewActivity extends AppCompatActivity {

    private int type;
    private Event event;

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        type = (int) getIntent().getIntExtra("type", 0);
        event = (Event) getIntent().getSerializableExtra("event");

        webView = (WebView) findViewById(R.id.webview);
        webView.setWebViewClient(new WebViewClient());


        if (event.getClub().equals("Sutton")) {
            webView.loadUrl("https://xceed.me/es/list/sutton-club-barcelona/event/barcelona/74039");
        }
        else if(event.getClub().equals("Bling Bling")) {
            webView.loadUrl("https://blingblingbcn.com/es/tickets");
        }
        else if(event.getClub().equals("Otto Zutz")){
            webView.loadUrl("https://www.ottozutz.com/events/reggaetown-thursdays-30?lang=es");
        }

        else if(event.getClub() == "Pacha"){
            //webView.loadUrl("https://pachabarcelona.es/es/events#!events/69350");
            //la url de arriba seria para uno de los eventos, pero no carga
            //Utilizamos la página principal de pacha
            webView.loadUrl("https://pachabarcelona.es/es/");

        }

        else{
            Toast.makeText(webViewActivity.this, "Entramos en el else", Toast.LENGTH_SHORT).show();
            //SI QUE ENTRAMOS
            webView.loadUrl("https://www.ottozutz.com/events/reggaetown-thursdays-30?lang=es");
        }

    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()){
            webView.goBack();
        }
        else{
            super.onBackPressed();
        }
    }
}
