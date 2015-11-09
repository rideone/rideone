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
            convertView.setTag(vh);
        }

        final ViewHolder vh = (ViewHolder) convertView.getTag();
        final User user = getItem(position);
        vh.tvUsername.setText(user.getFirstName() + " " + user.getLastName());
        vh.ivPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onPhoneCall(user, vh);
//                Intent callIntent = new Intent(Intent.ACTION_CALL);
//                callIntent.setData(Uri.parse("tel:" + user.getPhone()));
//                Context context = PassengerListAdapter.this.getContext();
//                if (Utils.checkCallPermission(context)) {
//                    context.startActivity(callIntent);
//                } else {
//                    Toast.makeText(context, "Phone call is not permitted", Toast.LENGTH_LONG).show();
//                }

            }
        });

        vh.ivRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onRemove(user, vh);
//                //TODO: alert with confirmation
//                PassengerListAdapter.this.remove(user);
//
//                //Inefficient removal
//                List<User> passengers = ride.getPassengers();
//                boolean isRemoved = passengers.remove(user);
//                if(!isRemoved) {
//                    List<User> waitlist = ride.getWaitList();
//                    waitlist.remove(user);
            }
        });

        vh.ivAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onAccept(user, vh);
//                ivAccept.setVisibility(View.INVISIBLE);
//
//                //Inefficient removal
//                List<User> waitlist = ride.getWaitList();
//                waitlist.remove(user);
//                List<User> passengers = ride.getPassengers();
//                passengers.add(user);

            }

        });

        int acceptVisibility = (user.getStatus() == PASSENGER ? INVISIBLE : VISIBLE);
        vh.ivAccept.setVisibility(acceptVisibility);


        return convertView;
    }
}
