package com.softdesign.devintensive.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.softdesign.devintensive.R;

import java.util.List;

/**
 * Created by Ageev Evgeny on 16.07.2016.
 */
public class RepositoriesAdapter extends BaseAdapter {
    private List<String> mRepoList;
    private Context mContext;
    private LayoutInflater mInflater;

    public RepositoriesAdapter(Context context, List<String> repoList) {
        mRepoList = repoList;
        mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mRepoList.size();
    }

    @Override
    public Object getItem(int pos) {
        return pos;
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        View itemView = convertView;

        if (itemView == null) {
            itemView = mInflater.inflate(R.layout.item_repositories_list, null);
        }

        final String curRepo = mRepoList.get(pos);
        TextView repoName = (TextView) itemView.findViewById(R.id.github_hint_txt);
        TextView repoAddr = (TextView) itemView.findViewById(R.id.github_txt);
        repoAddr.setText(curRepo);

//        ImageView showRepo = (ImageView) itemView.findViewById(R.id.github_iv);
//        showRepo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                openGithub(curRepo);
//            }
//        });

        return itemView;
    }

//    public void openGithub(String repoAddr) {
//        if (repoAddr == null || repoAddr.isEmpty()) return;
//
//        int pos = repoAddr.indexOf("http://");
//        if (pos != 0) {
//            repoAddr = "http://" + repoAddr;
//        }
//        Log.d("ADDR", "ADDR - " + repoAddr);
//        Uri address = Uri.parse(repoAddr);
//        Intent intent = new Intent(Intent.ACTION_VIEW, address);
//        if (intent.resolveActivity(mContext.getPackageManager()) != null) {
//            mContext.startActivity(intent);
//        }
//    }
}
