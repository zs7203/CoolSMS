package toy.hellozs.com.coolsms;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import toy.hellozs.com.coolsms.bean.Tag;

public class CategoryFragment extends Fragment implements TagRecyclerViewAdapter.TagClickListener{
    private static final String ARG_CATEGORY = "category";
    private int mCategory = -1;
    private List<Tag> mTags = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private TagRecyclerViewAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private SMSApplication mApplication;


    public CategoryFragment() {
        // Required empty public constructor
    }

    public static CategoryFragment newInstance(int index) {
        CategoryFragment fragment = new CategoryFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_CATEGORY, index);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCategory = getArguments().getInt(ARG_CATEGORY);
        }
        mApplication = SMSApplication.from(getActivity());
        mApplication.fragmentRegister(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.id_tag_recyclerview);
        mLayoutManager = new GridLayoutManager(getActivity(),3);
        getCategoryTags();
        mAdapter = new TagRecyclerViewAdapter(getActivity(),mTags, this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        return view;
    }

    //    获取该分类下的标签
    private void getCategoryTags() {
        List<Tag> allTagsCopy = mApplication.getAllTags();
        if (allTagsCopy == null || allTagsCopy.isEmpty()) {
            Toast.makeText(getActivity(),"正在加载，请稍候.." , Toast.LENGTH_SHORT).show();
            return;
        }
        switch (mCategory) {
            case 0:
                mTags = allTagsCopy.subList(0, 31);
                break;
            case 1:
                mTags = allTagsCopy.subList(31, 86);
                break;
            case 2:
                mTags = allTagsCopy.subList(86, 111);
                break;
            case 3:
                mTags = allTagsCopy.subList(111, 189);
                break;
            case 4:
                mTags = allTagsCopy.subList(189, 223);
                break;
            case 5:
                mTags = allTagsCopy.subList(223, 314);
                break;
        }

    }

    @Override
    public void onTagClick(String href) {
        Intent intent = new Intent(getActivity(),ChooseMsgActivity.class);
        intent.putExtra(ChooseMsgActivity.MESSAGE_URL, href);
        startActivity(intent);
    }

    public void updateTags() {
        getCategoryTags();
        mAdapter.updateList(mTags);
        mAdapter.notifyDataSetChanged();
    }
}