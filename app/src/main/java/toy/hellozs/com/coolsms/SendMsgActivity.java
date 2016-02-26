package toy.hellozs.com.coolsms;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.HashSet;

import toy.hellozs.com.coolsms.biz.SMSBiz;
import toy.hellozs.com.coolsms.util.FlowLayout;

public class SendMsgActivity extends AppCompatActivity {

    private String stamp = "[CoolSMS]";
    private static final int CODE_REQUEST = 1;
    public static final String MSG_TO_SEND = "msg";

    private EditText mEtMsg;
    private Button mBtAdd;
    private Button mBtCopy;
    ClipboardManager cbm;
    private FlowLayout mFlowContact;
    private FloatingActionButton mFabSend;
    private View mLayoutLoading;
    private LayoutInflater mInflater;
    private HashSet<String> mContactNames = new HashSet<>();

    private HashSet<String> mContactNumber = new HashSet<>();
    public static final String ACTION_SEND_MSG = "ACTION_SEND_MSG";
    public static final String ACTION_DELIVER_MSG = "ACTION_DELIVER_MSG";
    private PendingIntent mSendPi;
    private PendingIntent mDeliverPi;
    private BroadcastReceiver mSendBoBroadcastReceiver;
    private BroadcastReceiver mDeliverBroadcastReceiver;
    private SMSBiz mSMSBiz = new SMSBiz();
    private int mMsgCount;
    private int mMsgDeliveredCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_msg);

        initView();
        initData();
        initEvent();
        initReceiver();

    }

    private void initReceiver() {
        Intent sendIntent = new Intent(this.ACTION_SEND_MSG);
        mSendPi = PendingIntent.getBroadcast(this, 0, sendIntent, 0);
        Intent deliverIntent = new Intent(this.ACTION_DELIVER_MSG);
        mDeliverPi = PendingIntent.getBroadcast(this, 0, deliverIntent, 0);
        registerReceiver(mSendBoBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mMsgDeliveredCount++;
                if (getResultCode() == RESULT_OK) {
                    Log.e("TAG", "发送成功!" + (mMsgDeliveredCount + "/" + mMsgCount));
                } else {
                    Log.e("TAG", "发送失败。。");
                }
                Toast.makeText(SendMsgActivity.this, "发送成功!" + (mMsgDeliveredCount + "/" + mMsgCount), Toast.LENGTH_LONG).show();
            }
        }, new IntentFilter(ACTION_SEND_MSG));

        registerReceiver(mDeliverBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (getResultCode() == RESULT_OK) {
                    Log.e("TAG", "信息已送达！");
                } else {
                    Log.e("TAG", "联系人未收到。。");
                }
                Toast.makeText(SendMsgActivity.this, "对方已接收!" + (mMsgDeliveredCount + "/" + mMsgCount), Toast.LENGTH_LONG).show();
                if (mMsgCount == mMsgDeliveredCount) {
                    mLayoutLoading.setVisibility(View.GONE);
                }
            }
        }, new IntentFilter(ACTION_DELIVER_MSG));
        cbm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mSendBoBroadcastReceiver);
        unregisterReceiver(mDeliverBroadcastReceiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE_REQUEST) {
            if (resultCode == RESULT_OK) {
                Uri contactURI = data.getData();
                Cursor cursor = getContentResolver().query(contactURI, null, null, null, null);
                cursor.moveToFirst();
                String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                String number = getContactNumber(cursor);
                if (!TextUtils.isEmpty(number)) {
                    mContactNames.add(contactName);
                    mContactNumber.add(number);

                    addTag(contactName);
                }
            }
        }
    }

    private void addTag(String contactName) {
        TextView view = (TextView) mInflater.inflate(R.layout.tag, mFlowContact, false);
        view.setText(contactName);
        mFlowContact.addView(view);
    }

    private void initEvent() {
        mBtAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, CODE_REQUEST);
            }
        });
        mBtCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipData myClip;
                String text = mEtMsg.getText().toString();
                myClip = ClipData.newPlainText("text", text);
                cbm.setPrimaryClip(myClip);
                Snackbar.make(mFabSend, "短信已复制到剪贴板，您可以选择喜欢的方式发送内容。", Snackbar.LENGTH_LONG).show();
            }
        });
        mFabSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mContactNumber.size() == 0) {
                    Snackbar.make(mFabSend, "请至少选择一位联系人。", Snackbar.LENGTH_LONG).show();
                    return;
                }
                String msg = mEtMsg.getText().toString();
                if (TextUtils.isEmpty(msg)) {
                    Snackbar.make(mFabSend, "发送短信内容不能为空。", Snackbar.LENGTH_LONG).show();
                    return;
                }
                mLayoutLoading.setVisibility(View.VISIBLE);
                mMsgCount = mSMSBiz.sendMsgs(mContactNumber, msg + stamp, mSendPi, mDeliverPi);
                mMsgDeliveredCount = 0;
            }
        });
    }

    private String getContactNumber(Cursor cursor) {
        int numberCount = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
        String number = null;
        if (numberCount > 0) {
            int contactId = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            Cursor phoneCursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactId, null, null);
            phoneCursor.moveToFirst();
            number = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            phoneCursor.close();
        }
        cursor.close();
        return number;
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mEtMsg = (EditText) findViewById(R.id.id_et_content);
        mBtAdd = (Button) findViewById(R.id.id_bt_add);
        mBtCopy = (Button) findViewById(R.id.id_bt_copy);
        mFabSend = (FloatingActionButton) findViewById(R.id.id_bt_send);
        mFlowContact = (FlowLayout) findViewById(R.id.id_layout_flowcontact);
        mLayoutLoading = findViewById(R.id.id_layout_loading);
        mLayoutLoading.setVisibility(View.GONE);
        mInflater = LayoutInflater.from(this);
    }

    private void initData() {
        String msgToSend = getIntent().getStringExtra(SendMsgActivity.MSG_TO_SEND);
        mEtMsg.setText(msgToSend);
    }

}
