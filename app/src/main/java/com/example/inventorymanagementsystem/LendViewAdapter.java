package com.example.inventorymanagementsystem;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class LendViewAdapter extends BaseAdapter {

    private Context context;
    private  int layout;
    private ArrayList<LendItem> itemList;
    private View view;

    public LendViewAdapter(Context context, int layout, ArrayList<LendItem> itemList) {
        this.context = context;
        this.layout = layout;
        this.itemList = itemList;
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private  class ViewHolder{
        TextView textName, textPersonN, textlentDate, textReturnDate, textComments;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        View lendrow = view;
        ViewHolder holder = new ViewHolder();

        if(lendrow == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            lendrow = inflater.inflate(layout, null);

            holder.textName = (TextView) lendrow.findViewById(R.id.lendViewName);
            holder.textPersonN = (TextView) lendrow.findViewById(R.id.lendViewPName);
            holder.textlentDate = (TextView) lendrow.findViewById(R.id.lendViewLentDate);
            holder.textReturnDate = (TextView) lendrow.findViewById(R.id.lendViewReaturnDate);
            holder.textComments = (TextView) lendrow.findViewById(R.id.lendViewComments);
            lendrow.setTag(holder);
        }else{
            holder = (ViewHolder) lendrow.getTag();
        }

        LendItem item = itemList.get(position);

        holder.textName.setText(item.getItemName());
        holder.textPersonN.setText(item.getPersonname());
        holder.textlentDate.setText(item.getLendDate());
        holder.textReturnDate.setText(item.getReturnDate());
        holder.textComments.setText(item.getLendComments());

        return lendrow;
    }
}
