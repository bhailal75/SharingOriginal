package com.lsfv.myliteraturesharing.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsMessage;

public class MySMSBroadCastReceiver extends BroadcastReceiver
{

    @Override
    public void onReceive(Context context, Intent intent)
    {
        // Get Bundle object contained in the SMS intent passed in
        Bundle bundle = intent.getExtras();
        SmsMessage[] smsm = null;
        String sms_str ="";

        if (bundle != null)
        {
            // Get the SMS message
            Object[] pdus = (Object[]) bundle.get("pdus");
            smsm = new SmsMessage[pdus.length];
            for (int i=0; i<smsm.length; i++){
                smsm[i] = SmsMessage.createFromPdu((byte[])pdus[i]);

               // sms_str += "\r\nMessage: ";
                sms_str += smsm[i].getMessageBody().toString();
               // sms_str+= "\r\n";

                String Sender = smsm[i].getOriginatingAddress();
                String[] s=Sender.split("-");
                Sender=s[1].trim();
                //Check here sender is yours
                if(Sender.equals("LSFVIU")){
                    String[] separated = sms_str.split(":");
                    String otpsms = separated[1].trim();
//                    Toast.makeText(context, ""+otpsms, Toast.LENGTH_SHORT).show();
                    Intent smsIntent = new Intent("otp");
                    smsIntent.putExtra("message",otpsms);

                    LocalBroadcastManager.getInstance(context).sendBroadcast(smsIntent);
                }




            }


        }
    }
}
