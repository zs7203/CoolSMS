package toy.hellozs.com.coolsms;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import toy.hellozs.com.coolsms.bean.Message;
import toy.hellozs.com.coolsms.net.MessageRequest;

public class ChooseMsgActivity extends AppCompatActivity {

    public static final String MESSAGE_URL = "url";
    private String pageURL;
    private int currentPage = 1;
    private ListView mListView;
    private CoordinatorLayout mCoordinatorLayout;
    private LayoutInflater mInflater;
    private List<Message> mMsgs = new ArrayList<>();
    private ArrayAdapter<Message> mAdapter;
    private SMSApplication mApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_msg);
        mApplication = SMSApplication.from(this);
        pageURL = getIntent().getStringExtra(ChooseMsgActivity.MESSAGE_URL);
        initView();
        new MessageRequest(pageURL) {
            @Override
            protected void deliverResponse(List<Message> response) {
                mMsgs = response;
                mListView.setAdapter(mAdapter = new ArrayAdapter<Message>(ChooseMsgActivity.this, -1, mMsgs) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        if (convertView == null)
                            convertView = mInflater.inflate(R.layout.message_item, parent, false);
                        TextView content = (TextView) convertView.findViewById(R.id.id_tv_content);
                        Button bt_send = (Button) convertView.findViewById(R.id.id_bt_to_send);
                        final String message = getItem(position).getContent();
                        content.setText("       " + message);
                        bt_send.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(ChooseMsgActivity.this, SendMsgActivity.class);
                                intent.putExtra(SendMsgActivity.MSG_TO_SEND, message);
                                startActivity(intent);
                            }
                        });
                        return convertView;
                    }
                });
            }
        }.enQueue(mApplication.getRequestQueue());
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.id_msglayout);
        mInflater = LayoutInflater.from(this);
        mListView = (ListView) findViewById(R.id.id_listview);
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //停止滑动，且在列表底部
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && view.getLastVisiblePosition() == (view.getCount() - 1)) {
                    Snackbar.make(mCoordinatorLayout, "是否加载更多？", Snackbar.LENGTH_LONG)
                            .setAction("下一页", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    loadMoreMsg();
                                }
                            }).show();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                }
            }
    );
}

    private void loadMoreMsg() {
        new MessageRequest(pageURL + ++currentPage + "/") {
            @Override
            protected void deliverResponse(List<Message> response) {
                if (response.size() < 1) {
                    Snackbar.make(mCoordinatorLayout, "以上是所有的短信，没有更多了！", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                mAdapter.addAll(response);
            }
        }.enQueue(mApplication.getRequestQueue());
    }

}
