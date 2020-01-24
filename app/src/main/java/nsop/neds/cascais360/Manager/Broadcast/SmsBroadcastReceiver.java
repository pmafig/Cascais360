package nsop.neds.cascais360.Manager.Broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;

import nsop.neds.cascais360.Manager.Variables;
import nsop.neds.cascais360.Settings.Settings;
import nsop.neds.cascais360.ValidateSMSTokenActivity;

public class SmsBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        //h+6G4vdG51b

        if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
            Bundle extras = intent.getExtras();
            Status status = (Status) extras.get(SmsRetriever.EXTRA_STATUS);
            Intent verify = new Intent(context, ValidateSMSTokenActivity.class);

            switch(status.getStatusCode()) {
                case CommonStatusCodes.SUCCESS:
                    String message = (String) extras.get(SmsRetriever.EXTRA_SMS_MESSAGE);

                    String[] token = message.trim().replace(Settings.SmsHash, "").trim().split(" ");

                    verify.putExtra(Variables.SMSToken, token[token.length - 1].trim());

                    context.startActivity(verify);
                    break;
                case CommonStatusCodes.TIMEOUT:
                    context.startActivity(verify);
                    break;
            }
        }
    }
}