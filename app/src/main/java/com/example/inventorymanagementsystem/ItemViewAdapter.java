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

public class ItemViewAdapter extends BaseAdapter {

    private Context context;
    private  int layout;
    private ArrayList<Item> itemList;
    private View view;

    public ItemViewAdapter(Context context, int layout, ArrayList<Item> itemList) {
        this.context = context;
        this.layout = layout;
        this.itemList =itemList;
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
        ImageView imageView;
        TextView textName, textQuantity;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        View itemrow = view;
        ViewHolder holder = new ViewHolder();

        if(itemrow == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            itemrow = inflater.inflate(layout, null);

            holder.imageView = (ImageView) itemrow.findViewById(R.id.supplierimageView);
            holder.textName = (TextView) itemrow.findViewById(R.id.listViewTitle);
            holder.textQuantity = (TextView) itemrow.findViewById(R.id.quantity);
            itemrow.setTag(holder);
        }else{
            holder = (ViewHolder) itemrow.getTag();
        }

        Item item = itemList.get(position);

        holder.textName.setText(item.getName());
        holder.textQuantity.setText(item.getQuantity());

        byte[] decodedString = Base64.decode(item.getImagePath(), Base64.DEFAULT);
        Bitmap bmp = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        holder.imageView.setImageBitmap(bmp);
        //Glide.with(context).load(supplier.getImagePath()).into(holder.imageView);
        //byte[] supplierImage = supplier.getImagePath();
        //Bitmap bitmap = BitmapFactory.decodeByteArray(supplierImage, 0, supplierImage.length);
        //holder.imageView.setImageBitmap(bitmap);
        return itemrow;
    }
}
