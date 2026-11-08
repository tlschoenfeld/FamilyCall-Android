package com.familycall.test;
import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;
public class VoiceAccessibilityService extends AccessibilityService {
 private final Handler handler=new Handler(Looper.getMainLooper());
 private long lastNotice=0;
 @Override public void onAccessibilityEvent(AccessibilityEvent event) {
  if(event==null || event.getPackageName()==null || !event.getPackageName().toString().equals("com.google.android.apps.googlevoice")) return;
  String approved=getSharedPreferences("familycall_test",MODE_PRIVATE).getString("pending_number","");
  if(approved.isEmpty())return;
  AccessibilityNodeInfo root=getRootInActiveWindow();
  if(root==null)return;
  boolean found=containsNumber(root,approved,0);
  root.recycle();
  if(found && System.currentTimeMillis()-lastNotice>6000) {
   lastNotice=System.currentTimeMillis();
   handler.post(()->Toast.makeText(this,"FamilyCall: approved number detected in Google Voice. Tap Call manually for this test.",Toast.LENGTH_LONG).show());
  }
 }
 private boolean containsNumber(AccessibilityNodeInfo node,String number,int depth){
  if(node==null||depth>25)return false;
  CharSequence t=node.getText();
  if(t!=null && normalize(t.toString()).contains(normalize(number)))return true;
  for(int i=0;i<node.getChildCount();i++){
   AccessibilityNodeInfo child=node.getChild(i);
   if(child!=null){boolean yes=containsNumber(child,number,depth+1);child.recycle();if(yes)return true;}
  }
  return false;
 }
 private String normalize(String s){return s.replaceAll("[^0-9]","");}
 @Override public void onInterrupt(){}
}
