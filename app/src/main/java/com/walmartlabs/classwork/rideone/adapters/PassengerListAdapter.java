package com.walmartlabs.classwork.rideone.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.walmartlabs.classwork.rideone.R;
import com.walmartlabs.classwork.rideone.models.Ride;
import com.walmartlabs.classwork.rideone.models.User;
import com.walmartlabs.classwork.rideone.util.Utils;

import java.util.List;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static com.walmartlabs.classwork.rideone.models.User.Status.PASSENGER;

/**
 * Created by dmaskev on 11/8/15.
 */
public class PassengerListAdapter extends ArrayAdapter<User> {

//    private Ride ride;

    public interface PassengerListListener {
        void onAccept(User user, ViewHolder vh);
        void onRemove(User user, ViewHolder vh);
        void onPhoneCall(User user, ViewHolder vh);
    }

    public class ViewHolder {
        public ImageView ivAccept;
        public ImageView ivRemove;
        public ImageView ivPhone;
        public TextView tvUsername;
        public TextView tvAccept;
    }

    private PassengerListListener listener;

    public PassengerListAdapter(Context ctx, List<User> riders, PassengerListListener listener) {
        super(ctx, 0, riders);
        this.listener = listener;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.passenger_list_item, parent, false);
            ViewHolder vh = new ViewHolder();
            vh.ivAccept = (ImageView) convertView.findViewById(R.id.ivAccept);
            vh.ivRemove = (ImageView) convertView.findViewById(R.id.ivRemove);
            vh.ivPhone = (ImageView) convertView.findViewById(R.id.ivPhone);
            vh.tvUsername = (TextView) convertView.findViewById(R.id.tvUsername);
            vh.tvAccept = (TextView) convertView.findViewById(R.id.tvAccept);
            convertView.setTag(vh);
        }

        final ViewHolder vh = (ViewHolder) convertView.getTag();
        final User user = getItem(position);
        vh.tvUsername.setText(user.getFirstName() + " " + user.getLastName());
        vh.ivPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onPhoneCall(user, vh);
            }
        });

        vh.ivRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onRemove(user, vh);
            }
        });

        vh.ivAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onAccept(user, vh);
            }

        });

        int acceptVisibility = (user.getStatus() == PASSENGER ? INVISIBLE : VISIBLE);
        vh.ivAccept.setVisibility(acceptVisibility);
        vh.tvAccept.setVisibility(acceptVisibility);


        return convertView;
    }
}
