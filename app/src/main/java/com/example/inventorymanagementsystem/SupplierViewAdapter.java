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

public class SupplierViewAdapter extends BaseAdapter {

    private Context context;
    private  int layout;
    private ArrayList<Supplier> supplierList;
    private View view;

    public SupplierViewAdapter(Context context, int layout, ArrayList<Supplier> supplierList) {
        this.context = context;
        this.layout = layout;
        this.supplierList = supplierList;
    }

    @Override
    public int getCount() {
        return supplierList.size();
    }

    @Override
    public Object getItem(int position) {
        return supplierList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private  class ViewHolder{
        ImageView imageView;
        TextView textName, textComments;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        View row = view;
        ViewHolder holder = new ViewHolder();

        if(row == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);

            holder.imageView = (ImageView) row.findViewById(R.id.supplierimageView);
            holder.textName = (TextView) row.findViewById(R.id.listViewTitle);
            holder.textComments = (TextView) row.findViewById(R.id.listViewComment);
            row.setTag(holder);
        }else{
            holder = (ViewHolder) row.getTag();
        }

        Supplier supplier = supplierList.get(position);

        holder.textName.setText(supplier.getName());
        holder.textComments.setText(supplier.getComments());
        byte[] decodedString = Base64.decode(supplier.getImagePath(), Base64.DEFAULT);
        Bitmap bmp = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        holder.imageView.setImageBitmap(bmp);
        //byte[] supplierImage = supplier.getImagePath();
        //Bitmap bitmap = BitmapFactory.decodeByteArray(supplierImage, 0, supplierImage.length);
        //holder.imageView.setImageBitmap(bitmap);
        return row;
    }
}