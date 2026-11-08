package com.familycall.test;
import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebSettings;
import android.webkit.JavascriptInterface;
import android.widget.Toast;
public class MainActivity extends Activity {
 WebView web;
 @Override public void onCreate(Bundle b) {
  super.onCreate(b);
  web=new WebView(this); setContentView(web);
  WebSettings s=web.getSettings(); s.setJavaScriptEnabled(true); s.setDomStorageEnabled(true);
  web.addJavascriptInterface(new Bridge(),"Native");
  web.loadUrl("file:///android_asset/index.html");
 }
 class Bridge {
  @JavascriptInterface public void voice() {runOnUiThread(()->{
   Intent i=getPackageManager().getLaunchIntentForPackage("com.google.android.apps.googlevoice");
   if(i!=null)startActivity(i);else Toast.makeText(MainActivity.this,"Google Voice unavailable",Toast.LENGTH_LONG).show();
  });}
  @JavascriptInterface public void dial(String number) {runOnUiThread(()->{
   if(!number.matches("\\+?[0-9]{7,15}")) {Toast.makeText(MainActivity.this,"Invalid number",Toast.LENGTH_SHORT).show();return;}
   try{startActivity(new Intent(Intent.ACTION_DIAL,Uri.parse("tel:"+number)));}
   catch(Exception e){Toast.makeText(MainActivity.this,"No dialer available",Toast.LENGTH_LONG).show();}
  });}
  @JavascriptInterface public void exit() {runOnUiThread(()->finish());}
 }
}
