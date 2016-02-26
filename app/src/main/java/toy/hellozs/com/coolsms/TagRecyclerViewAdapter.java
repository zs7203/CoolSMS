package toy.hellozs.com.coolsms;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import toy.hellozs.com.coolsms.bean.Tag;

/**
 * Created by Administrator on 2016/2/20.
 */
public class TagRecyclerViewAdapter extends RecyclerView.Adapter<TagRecyclerViewAdapter.MyViewHolder> {

    private Context context;
    private List<Tag> tags;
    private TagClickListener listener;

    public interface TagClickListener {
        void onTagClick(String href);
    }

    public TagRecyclerViewAdapter(Context context, List<Tag> tags, TagClickListener listener) {
        this.context = context;
        this.tags = tags;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.tag_item, parent, false);
        MyViewHolder vh = new MyViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        String title = tags.get(position).getTitle();
        holder.title.setText(title);
        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onTagClick(tags.get(position).getHref());
            }
        });
    }

    @Override
    public int getItemCount() {
        return tags == null ? 0 : tags.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView title;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.id_tag_title);
        }
    }

    public void updateList(List<Tag> tags){
        this.tags = tags;
    }

}
