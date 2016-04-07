package com.example.fshen4.CS442_Places_Near_Me;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.util.ArrayList;
import android.graphics.Color;
/**
 * Created by fshen4 on 16-4-2.
 */
public class PlaceArrayAdapter extends BaseAdapter {
    private Context mycontext;
    private LayoutInflater inflater;
    private final ArrayList<Place> items ;

    public PlaceArrayAdapter(Context context, ArrayList<Place> objects) {
        // TODO Auto-generated constructor stub
        mycontext = context;
        inflater = LayoutInflater.from(context);
        items = objects;
    }
    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Place getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub


        ViewHolder holder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listview_item, null);
            holder = new ViewHolder();
            holder.tvContent = (TextView) convertView.findViewById(R.id.tvContent);
            holder.btCall = (Button) convertView.findViewById(R.id.btCall);
            holder.btDirection = (Button) convertView.findViewById(R.id.btDirection);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        final Place itemInfo = items.get(position);
        holder.tvContent.setText(itemInfo.getName() + "\n" + itemInfo.getVicinity());
        holder.tvContent.setTextColor(Color.WHITE);
        holder.tvContent.setBackgroundColor(Color.DKGRAY);

        /*
        String addressLine = getItem(position).getName();
        addressLine += "\n";
        addressLine += getItem(position).getVicinity();
        addressLine += "\n";
        addressLine += getItem(position).getPhone();
        addressLine += "\n";
        addressLine += getItem(position).getLatitude() + "," + getItem(position).getLongitude();

        TextView rowAddress = new TextView(mycontext);

        if(items.get(position) != null )
        {
            rowAddress.setTextColor(Color.WHITE);
            rowAddress.setText(addressLine);
            rowAddress.setBackgroundColor(Color.RED);
            int color = Color.argb( 200, 255, 64, 64 );
            rowAddress.setBackgroundColor( color );

        }
        return rowAddress;
        */

        holder.btCall.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent phoneIntent = new Intent("android.intent.action.CALL",
                    Uri.parse("tel:" + itemInfo.getPhone()));
                mycontext.startActivity(phoneIntent);
            }
        });
        holder.btDirection.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent phoneIntent = new Intent("android.intent.action.CALL",
                        Uri.parse("tel:" + itemInfo.getPhone()));
            }
        });

        return convertView;
    }

    public class ViewHolder {
        public TextView tvContent;
        public Button btCall;
        public Button btDirection;
    }

}
