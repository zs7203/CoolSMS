package toy.hellozs.com.coolsms;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import toy.hellozs.com.coolsms.bean.Tag;

public class TagRecyclerViewAdapter extends RecyclerView.Adapter<TagRecyclerViewAdapter.MyViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

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
    public int getItemViewType(int position) {
        return position == 0 ? TYPE_HEADER : TYPE_ITEM;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(context).inflate(R.layout.tag_header, parent, false);
            return new MyViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.tag_item, parent, false);
            return new MyViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        if (position == 0) {
            return;
        }
        String title = tags.get(position - 1).getTitle();
        holder.title.setText(title);
        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onTagClick(tags.get(position - 1).getHref());
            }
        });
    }

    @Override
    public int getItemCount() {
        return tags == null ? 0 : tags.size() + 1;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView title;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.id_tag_title);
        }
    }

    public void updateList(List<Tag> tags) {
        this.tags = tags;
    }

}
