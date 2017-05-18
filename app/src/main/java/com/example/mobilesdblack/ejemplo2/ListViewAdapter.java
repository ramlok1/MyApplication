package com.example.mobilesdblack.ejemplo2;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by MobileSD Black on 15/08/2016.
 */
public class ListViewAdapter extends ArrayAdapter<Valoracion> {

        private AppCompatActivity activity;
        private List<Valoracion> movieList;

        public ListViewAdapter(AppCompatActivity context, int resource, List<Valoracion> objects) {
            super(context, resource, objects);
            this.activity = context;
            this.movieList = objects;
        }

        @Override
        public Valoracion getItem(int position) {
            return movieList.get(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.modelo, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
                //holder.ratingBar.getTag(position);
            }

            holder.ratingBar.setOnRatingBarChangeListener(onRatingChangedListener(holder, position));

            holder.ratingBar.setTag(position);
            holder.ratingBar.setRating(getItem(position).getRatingStar());
            holder.movieName.setText(getItem(position).getName());

            return convertView;
        }

        private RatingBar.OnRatingBarChangeListener onRatingChangedListener(final ViewHolder holder, final int position) {
            return new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                    Valoracion item = getItem(position);
                    item.setRatingStar(v);

                }
            };
        }

        private static class ViewHolder {
            private RatingBar ratingBar;
            private TextView movieName;

            public ViewHolder(View view) {
                ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);
                movieName = (TextView) view.findViewById(R.id.txt_titulo);
            }
        }

}
