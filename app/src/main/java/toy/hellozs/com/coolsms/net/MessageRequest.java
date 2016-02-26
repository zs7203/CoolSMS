package toy.hellozs.com.coolsms.net;


import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import toy.hellozs.com.coolsms.bean.Message;

/**
 * Created by Administrator on 2016/2/21.
 */
public abstract class MessageRequest extends Request<List<Message>> {

    private String url;

    public MessageRequest(String url) {
        super(Method.GET, url, null);
        this.url = url;
    }

    @Override
    protected Response<List<Message>> parseNetworkResponse(NetworkResponse response) {
        List<Message> msgs = new ArrayList<>();
        try {
            String html = new String(response.data,HttpHeaderParser.parseCharset(response.headers));
            Document document = Jsoup.parse(html);

            for (Element post : document.select("div#tagList p")) {
                String msg = post.text();
                msgs.add(new Message(msg));
            }
            return Response.success(msgs, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }

    public void enQueue(RequestQueue queue){
        queue.add(this);
    }
}
