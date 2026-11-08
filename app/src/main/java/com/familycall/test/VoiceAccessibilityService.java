package com.familycall.test;
import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;
import java.util.ArrayList;
public class VoiceAccessibilityService extends AccessibilityService {
 private final Handler handler=new Handler(Looper.getMainLooper());
 private long lastNotice=0;
 @Override public void onAccessibilityEvent(AccessibilityEvent event) {
  if(event==null||event.getPackageName()==null||!"com.google.android.apps.googlevoice".contentEquals(event.getPackageName()))return;
  SharedPreferences prefs=getSharedPreferences("familycall_test",MODE_PRIVATE);
  String approved=prefs.getString("pending_number","");
  long started=prefs.getLong("pending_time",0);
  if(approved.isEmpty()||System.currentTimeMillis()-started>30000)return;
  AccessibilityNodeInfo root=getRootInActiveWindow();
  if(root==null)return;
  ArrayList<String> editable=new ArrayList<>();
  ArrayList<AccessibilityNodeInfo> callButtons=new ArrayList<>();
  inspect(root,editable,callButtons,0);
  String expected=digits(approved);
  boolean exact=editable.size()==1 && expected.equals(digits(editable.get(0)));
  if(exact && prefs.getBoolean("auto_call",false) && callButtons.size()==1) {
   // Consume authorization before clicking, preventing repeated clicks on accessibility events.
   prefs.edit().remove("pending_number").remove("pending_time").apply();
   boolean clicked=callButtons.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
   notice(clicked?"FamilyCall: automatic Call tap attempted.":"FamilyCall: Call button could not be activated.");
  } else if(System.currentTimeMillis()-lastNotice>6000) {
   lastNotice=System.currentTimeMillis();
   if(exact)notice("FamilyCall: exact number confirmed. Auto Call is off or button not uniquely identified.");
   else notice("FamilyCall: number not verified in editable dialer. No automatic call.");
  }
  for(AccessibilityNodeInfo n:callButtons)n.recycle();
  root.recycle();
 }
 private void inspect(AccessibilityNodeInfo node,ArrayList<String> editable,ArrayList<AccessibilityNodeInfo> buttons,int depth){
  if(node==null||depth>25)return;
  if(node.isEditable() && node.getText()!=null)editable.add(node.getText().toString());
  CharSequence d=node.getContentDescription();
  CharSequence t=node.getText();
  String label=d!=null?d.toString().trim():(t!=null?t.toString().trim():"");
  if(node.isClickable() && node.isEnabled() && (label.equalsIgnoreCase("Call")||label.equalsIgnoreCase("Place call")))buttons.add(AccessibilityNodeInfo.obtain(node));
  for(int i=0;i<node.getChildCount();i++){
   AccessibilityNodeInfo child=node.getChild(i);
   if(child!=null){inspect(child,editable,buttons,depth+1);child.recycle();}
  }
 }
 private String digits(String s){return s.replaceAll("[^0-9]","");}
 private void notice(String s){handler.post(()->Toast.makeText(this,s,Toast.LENGTH_LONG).show());}
 @Override public void onInterrupt(){}
}
