package com.example.weightscale;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private Button btn_deviceList,btn_copy;
    public static BluetoothAdapter bluetoothAdapter;
    private TextView text_device;
    UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothSocket mSocket;
    private BluetoothDevice mDevice;
    private InputStream inputStream;
    private DecimalFormat decimalFormat;
    public static final String TAG="demo";
    public static final int REQUEST_BLUETOOTH = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: " + Thread.currentThread().getId());
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        initView();
//        enableBluetooth();
        enableBlue();
        getandsetIntent();
        btn_deviceList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DeviceListActivity.class);
                startActivity(intent);
            }
        });

        btn_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard= (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("EditText", text_device.getText().toString());
                clipboard.setPrimaryClip(clip);

                clip.getDescription();

                Toast.makeText(MainActivity.this, "Text is Copied", Toast.LENGTH_SHORT).show();

//                String dataToCopy = text_device.getText().toString();
//
//                // Start the service to copy data in the background
//                Intent serviceIntent = new Intent(MainActivity.this, ClipboardCopyService.class);
//                serviceIntent.putExtra("data", dataToCopy);
//                startService(serviceIntent);
            }
        });

    }

    private void initView() {
        btn_deviceList = findViewById(R.id.btn_deviceList);
        btn_copy = findViewById(R.id.btn_copy);
        text_device = findViewById(R.id.text_device);
    }

    public void enableBlue(){
        if (bluetoothAdapter == null){
            Toast.makeText(this, "Device Bluetooth not available", Toast.LENGTH_SHORT).show();
            return;
        } else if (!bluetoothAdapter.isEnabled()) {
            if (ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.BLUETOOTH},REQUEST_BLUETOOTH);
            }else {
                Intent intent =new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivity(intent);
            }

        }
    }

//    public void enableBluetooth() {
//        if (bluetoothAdapter == null) {
//            Toast.makeText(this, "Device Bluetooth not available", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        if (!bluetoothAdapter.isEnabled()) {
//            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED ||
//                    ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.BLUETOOTH_CONNECT}, 105);
//            } else {
//                Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                startActivityForResult(enableBluetooth, 101);
//            }
//        }
//    }

    private void getandsetIntent() {
        if (getIntent() != null && getIntent().hasExtra("name") && getIntent().hasExtra("address")) {
            text_device.setText(getIntent().getStringExtra("name"));
            mDevice = bluetoothAdapter.getRemoteDevice(getIntent().getStringExtra("address"));

            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            try {
                mSocket = mDevice.createRfcommSocketToServiceRecord(uuid);
                mSocket.connect();
                if (mSocket.isConnected()) {
                    Toast.makeText(MainActivity.this, "Connected", Toast.LENGTH_SHORT).show();
                    inputStream = mSocket.getInputStream();
                    decimalFormat = new DecimalFormat("00.00");
//                    startReadingThread();
                    Reading2();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

//    private void startReadingThread() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                byte[] buffer = new byte[1024];
//
//                while (true) {
//                    try {
//                        int bytesRead = inputStream.read(buffer);
//                        if (bytesRead != -1) {
//                            String receivedData = new String(buffer, 0, bytesRead, StandardCharsets.UTF_8).trim();
//                            Log.d("Bluetooth", "Raw received data: " + receivedData);
//
//                            try {
//                                double value = Double.parseDouble(receivedData);
//                                receivedData = decimalFormat.format(value);
//                                Log.d("Value Of the - ", "Formatted value: " + receivedData);
//
//                                String finalReceivedData = receivedData;
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        Log.d("Bluetooth", "Updating UI with: " + finalReceivedData);
//                                        Log.d(TAG, "run: " + finalReceivedData);
//                                        text_device.setText(finalReceivedData);
//
//                                        if (value >3) {
//                                            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
//                                            ClipData clip = ClipData.newPlainText("EditText", finalReceivedData);
//                                            clipboard.setPrimaryClip(clip);
////                                            Toast.makeText(MainActivity.this, "Text is Copied", Toast.LENGTH_SHORT).show();
//                                            Log.d(TAG, "run:  Thread ..........."+ Thread.currentThread().getId());
//                                        }
//                                    }
//                                });
//                            } catch (NumberFormatException ex) {
//                                Log.e("Bluetooth", "Error parsing received data", ex);
//                            }
//                        } else {
//                            break;
//                        }
//                    } catch (IOException e) {
//                        Log.e("Bluetooth", "Error reading from Bluetooth socket", e);
//                        break;
//                    }
//                }
//            }
//        }).start();
//    }

//    private void Reading(){
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                byte[] buffer = new byte[1024];
//                StringBuilder receivedData = new StringBuilder();
//                while (true){
//                    try {
//                        int bytesRead = inputStream.read(buffer);
//                        if (bytesRead != -1) { // Check for end of stream
//                            String data = new String(buffer, 0, bytesRead,"UTF-8");
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    text_device.setText(data);
//                                }
//                            });
//                        }
//                    } catch (IOException e) {
//                        throw new RuntimeException(e);
//                    }
//                }
//
//            }
//        }).start();
//    }
    private void Reading2() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] buffer = new byte[1024];
                StringBuilder receivedData = new StringBuilder();

                while (true) {
                    try {
                        int bytesRead = inputStream.read(buffer);
                        if (bytesRead != -1) { // Check for end of stream
                            String dataChunk = new String(buffer, 0, bytesRead, "UTF-8");
                            receivedData.append(dataChunk);

                            // Check if the complete value is received (e.g., "04.82")
                            if (receivedData.toString().endsWith("\n")) { // Assuming the message ends with a newline character
                                StringBuilder finalReceivedData = receivedData;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        text_device.setText(finalReceivedData.toString().trim());
                                        copyToClipboard(String.valueOf(finalReceivedData));

                                    }
                                });
                                // Reset StringBuilder for the next message
                                receivedData = new StringBuilder();
                            }
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }).start();
    }

    private void copyToClipboard(String data) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", data);
        clipboard.setPrimaryClip(clip);
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (mSocket != null) {
                mSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
