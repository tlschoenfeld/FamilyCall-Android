package com.familycall.test;
import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebSettings;
import android.webkit.WebChromeClient;
import android.webkit.JavascriptInterface;
import android.widget.Toast;
public class MainActivity extends Activity {
 WebView web;
 @Override public void onCreate(Bundle b) {
  super.onCreate(b);
  web=new WebView(this); setContentView(web);
  WebSettings s=web.getSettings(); s.setJavaScriptEnabled(true); s.setDomStorageEnabled(true);
  web.setWebChromeClient(new WebChromeClient());
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
  @JavascriptInterface public void dialVoice(String number) {runOnUiThread(()->{
   if(!number.matches("[+]?[0-9]{7,15}")) {Toast.makeText(MainActivity.this,"Invalid number",Toast.LENGTH_SHORT).show();return;}
   try {
    getSharedPreferences("familycall_test",MODE_PRIVATE).edit().putString("pending_number",number).putLong("pending_time",System.currentTimeMillis()).apply();
    Intent i=new Intent(Intent.ACTION_DIAL,Uri.parse("tel:"+number));
    i.setPackage("com.google.android.apps.googlevoice");
    startActivity(i);
   } catch(Exception e) {
    Toast.makeText(MainActivity.this,"Google Voice does not accept this dial request on this phone. No call launched.",Toast.LENGTH_LONG).show();
   }
  });}
  @JavascriptInterface public boolean autoCallEnabled(){return getSharedPreferences("familycall_test",MODE_PRIVATE).getBoolean("auto_call",false);}
  @JavascriptInterface public void setAutoCall(boolean enabled){getSharedPreferences("familycall_test",MODE_PRIVATE).edit().putBoolean("auto_call",enabled).apply();}
  @JavascriptInterface public void accessibilitySettings() {runOnUiThread(()->{
   startActivity(new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS));
  });}
  @JavascriptInterface public void exit() {runOnUiThread(()->finish());}
 }
}
