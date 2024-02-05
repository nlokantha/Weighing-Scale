package com.example.weightscale;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DeviceListViewAdapter extends RecyclerView.Adapter<DeviceListViewAdapter.DeviceListViewHolder> {
    Context context;

    public DeviceListViewAdapter(Context context, List<BluetoothDevice> deviceList) {
        this.context = context;
        this.deviceList = deviceList;
    }

    private List<BluetoothDevice> deviceList;
    @NonNull
    @Override
    public DeviceListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.devicelist_row_items, parent, false);
        return new DeviceListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceListViewHolder holder, int position) {
        BluetoothDevice device = deviceList.get(position);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        holder.text_bluetoothName.setText(device.getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                String name = device.getName().toString();
                String address=device.getAddress().toString();
                Intent intent=new Intent(context, MainActivity.class);
                intent.putExtra("name",name);
                intent.putExtra("address",address);
                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return deviceList.size();
    }

    public static class DeviceListViewHolder extends RecyclerView.ViewHolder{
        TextView text_bluetoothName;

        public DeviceListViewHolder(@NonNull View itemView) {
            super(itemView);
            text_bluetoothName=itemView.findViewById(R.id.text_bluetoothName);
        }
    }
}
