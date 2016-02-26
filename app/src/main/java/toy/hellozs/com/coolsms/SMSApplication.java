package toy.hellozs.com.coolsms;

import android.app.Application;
import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.List;

import toy.hellozs.com.coolsms.bean.Tag;
import toy.hellozs.com.coolsms.net.TagRequest;

/**
 * Created by Administrator on 2016/2/20.
 */
public class SMSApplication extends Application{

    private static List<Tag> allTags;
    private static RequestQueue queue;
    private static List<CategoryFragment> fragments = new ArrayList<>();

    public static SMSApplication from(Context context){
        return (SMSApplication)context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        queue = Volley.newRequestQueue(this);
        preloadAllTags();
    }

    private void preloadAllTags(){
        new TagRequest("http://www.xiha360.com/") {
            @Override
            protected void deliverResponse(List<Tag> response) {
                allTags = response;
                onTagsUpdate();
            }
        }.enQueue(queue);
    }

    public RequestQueue getRequestQueue(){
        return this.queue;
    }

    public static List<Tag> getAllTags(){
        if (allTags == null)
            return null;
        return new ArrayList(allTags);
    }

    public static void fragmentRegister(CategoryFragment frag){
        fragments.add(frag);
    }

    public void onTagsUpdate(){
        for (CategoryFragment frag: fragments) {
            frag.updateTags();
        }
    }

}
