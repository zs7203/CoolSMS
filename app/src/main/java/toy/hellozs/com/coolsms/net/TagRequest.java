package toy.hellozs.com.coolsms.net;

import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import toy.hellozs.com.coolsms.bean.Tag;

/**
 * Created by Administrator on 2016/2/20.
 */
public abstract class TagRequest extends Request<List<Tag>> {

    public TagRequest(String url) {
        super(Method.GET, url, null);
    }

    @Override
    protected Response<List<Tag>> parseNetworkResponse(NetworkResponse response) {

        List<Tag> tags = new ArrayList<>();

        try {
            String html = new String(response.data, "utf-8");
            Document document = Jsoup.parse(html);
            Elements categorys = document.select("div.homePageCategoryList");
            for (Element category: categorys) {
                for (Element rowTag : category.select("a")) {
                    String title =  rowTag.text();
                    String href = rowTag.attr("href");
                    tags.add(new Tag(title, href));
                }
            }
            return Response.success(tags, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }

    }

    public void enQueue(RequestQueue queue) {
        queue.add(this);
    }

}
