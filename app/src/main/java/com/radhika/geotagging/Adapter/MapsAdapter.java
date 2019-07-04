package com.radhika.geotagging.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.radhika.geotagging.Activities.MapsActivity;
import com.radhika.geotagging.Models.Maps;
import com.radhika.geotagging.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MapsAdapter extends RecyclerView.Adapter<MapsAdapter.MapsHolder> {

    private List<Maps> lstMaps = new ArrayList<>();

    @NonNull
    @Override
    public MapsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.maps_item, parent, false);
        return new MapsHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MapsHolder holder, int position) {
        Maps currentMaps = lstMaps.get(position);
        holder.tvAddress.setText(currentMaps.getAddress());
        holder.tvLatitude.setText(String.valueOf(currentMaps.getLatitude()));
        holder.tvLongitude.setText(String.valueOf(currentMaps.getLongitude()));
        if (!TextUtils.isEmpty(currentMaps.getImage())) {
            File imgFile = new File(currentMaps.getImage());
            if (imgFile.exists()) {
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                holder.imgMarker.setImageBitmap(myBitmap);
            } else {
                holder.imgMarker.setImageResource(R.drawable.app_icon);
            }
        } else {
            holder.imgMarker.setImageResource(R.drawable.app_icon);
        }
    }

    public void setMaps(List<Maps> maps) {
        this.lstMaps = maps;
        notifyDataSetChanged();
    }

    public Maps getMapAt(int position) {
        return this.lstMaps.get(position);
    }

    @Override
    public int getItemCount() {
        return lstMaps.size();
    }

    public class MapsHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView imgMarker;
        private TextView tvAddress, tvLatitude, tvLongitude;
        private Context context;

        public MapsHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            imgMarker = itemView.findViewById(R.id.img_marker);
            tvAddress = itemView.findViewById(R.id.tv_Address);
            tvLatitude = itemView.findViewById(R.id.tv_latitude);
            tvLongitude = itemView.findViewById(R.id.tv_longitude);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getLayoutPosition();
            Maps maps = lstMaps.get(position);
            Bundle bundle = new Bundle();
            bundle.putSerializable("maps", maps);
            Intent intent = new Intent(context, MapsActivity.class);
            intent.putExtras(bundle);
            ((Activity) context).startActivityForResult(intent, 2);
        }
    }
}
