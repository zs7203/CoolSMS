package toy.hellozs.com.coolsms.biz;

import android.app.PendingIntent;
import android.telephony.SmsManager;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by Administrator on 2015/12/2.
 */
public class SMSBiz {

    public int sendMsg(String number, String msg, PendingIntent sendPi, PendingIntent deliverPi) {

        SmsManager manager = SmsManager.getDefault();
        ArrayList<String> contents = manager.divideMessage(msg);

        for (String content : contents) {
            manager.sendTextMessage(number, null, content, sendPi, deliverPi);
        }

        return contents.size();
    }

    public int sendMsgs(Set<String> numbers, String msg, PendingIntent sendPi, PendingIntent deliverPi) {

        int result = 0;
        for (String number : numbers) {
            int count = sendMsg(number, msg, sendPi, deliverPi);
            result += count;
        }
        return result;
    }


}
