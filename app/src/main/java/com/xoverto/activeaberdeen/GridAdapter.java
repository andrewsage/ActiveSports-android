package com.xoverto.activeaberdeen;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by andrew on 29/12/14.
 */
public class GridAdapter extends BaseAdapter {
    private List<GridOption> list;
    private Context context;

    public GridAdapter(Context context, List<GridOption> newList){
        this.context = context;
        this.list = newList;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public GridOption getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View gridCell;

        //if (convertView == null) {
        gridCell = new View(context);
        gridCell = inflater.inflate(R.layout.home_cell, null);

        TextView txtName = (TextView) gridCell.findViewById(R.id.friend_name);
        txtName.setText(getItem(position).getName());

        ImageView profileImgView = (ImageView) gridCell.findViewById(R.id.profileImg);
        //ImageLoaderHelper.getImageLoader(context).displayImage(getItem(position).getImageUrl(), profileImgView);
        //} else {
        //    gridCell = (View) convertView;
        //}

        return gridCell;
    }
}
