package com.example.emergency;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter<DataModel> implements View.OnClickListener{

    private ArrayList<DataModel> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView txtName;
        TextView txtCity;
        TextView txtDate;
        TextView txtCategory;
        ImageView imgPicture;
    }

    public CustomAdapter(ArrayList<DataModel> data, Context context) {
        super(context, R.layout.viewtemplate, data);
        this.dataSet = data;
        this.mContext=context;

    }

    @Override
    public void onClick(View v) {

        int position=(Integer) v.getTag();
        Object object= getItem(position);
        DataModel dataModel=(DataModel)object;

//        switch (v.getId())
//        {
//            case R.id.item_info:
//                Snackbar.make(v, "Release date " +dataModel.getFeature(), Snackbar.LENGTH_LONG)
//                        .setAction("No action", null).show();
//                break;
//        }
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        DataModel dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.viewtemplate, parent, false);
            viewHolder.txtName =convertView.findViewById(R.id.vName);
            viewHolder.txtCity =convertView.findViewById(R.id.vCity);
            viewHolder.txtDate =convertView.findViewById(R.id.vDate);
            viewHolder.txtCategory = convertView.findViewById(R.id.vCategory);
            viewHolder.imgPicture=convertView.findViewById(R.id.vPicture);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

//        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
//        result.startAnimation(animation);
//        lastPosition = position;

        viewHolder.txtName.setText(dataModel.getName());
        viewHolder.txtCity.setText(dataModel.getCity());
        viewHolder.txtDate.setText(dataModel.getDate());
//        viewHolder.txtCategory.setOnClickListener(this);
        viewHolder.txtCategory.setText(dataModel.getCategory());
        Glide.with(mContext).load(dataModel.getImage()).into(viewHolder.imgPicture);

        // Return the completed view to render on screen
        return convertView;
    }
}
