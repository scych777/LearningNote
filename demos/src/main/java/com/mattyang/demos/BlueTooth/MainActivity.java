package com.mattyang.demos.BlueTooth;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mattyang.demos.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    int REQUEST_ENABLE_BT = 10;
    BluetoothAdapter mBluetoothAdapter;
    TextView mBluetoothStatus;
    TextView mReadBuffer;
    Button mScanBtn;
    Button mOffBtn;
    Button mDiscoverBtn;
    Button mListPairedDevicesBtn;
    ListView mDevicesListView;
    private ArrayAdapter<String> mBTArrayAdapter;
    private Set<BluetoothDevice> mPairedDeivces;
    private BluetoothSocket mBTSocket = null;
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private ConnectedThread mConnectedThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBluetoothStatus = (TextView) findViewById(R.id.status);
        mReadBuffer = (TextView) findViewById(R.id.rx);
        mScanBtn = (Button) findViewById(R.id.bluetooth_on);
        mOffBtn = (Button) findViewById(R.id.bluetooth_off);
        mDiscoverBtn = (Button) findViewById(R.id.discover_new_devices);
        mListPairedDevicesBtn = (Button) findViewById(R.id.show_paired_devices);
        mDevicesListView = (ListView) findViewById(R.id.listview);
        mBTArrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mDevicesListView.setAdapter(mBTArrayAdapter);
        mDevicesListView.setOnItemClickListener(mDeviceClickListener);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},1);
        }

        if(mBluetoothAdapter == null){
            mBluetoothStatus.setText("Status: Bluetooth not found");
        }else{
            mScanBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!mBluetoothAdapter.isEnabled()){
                        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(intent,REQUEST_ENABLE_BT);
                        mBluetoothStatus.setText("Status: Bluetooth enabled");
                    }else{
                    }
                }
            });
            mOffBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mBluetoothAdapter.disable();
                    mBluetoothStatus.setText("Status: Bluetooth disabled");
                }
            });
            mListPairedDevicesBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mBTArrayAdapter.clear();
                    mPairedDeivces = mBluetoothAdapter.getBondedDevices();
                    if(mBluetoothAdapter.isEnabled()){
                        for(BluetoothDevice device: mPairedDeivces){
                            mBTArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                        }
                    }else{
                        Toast.makeText(MainActivity.this,"Ops, you don't have bonded devices",Toast.LENGTH_SHORT).show();
                    }
                }
            });
            mDiscoverBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mBluetoothAdapter.isDiscovering()){
                        mBluetoothAdapter.cancelDiscovery();
                        Toast.makeText(MainActivity.this,"Discovery stopped",Toast.LENGTH_SHORT).show();
                    }else{
                        if(mBluetoothAdapter.isEnabled()){
                            mBTArrayAdapter.clear();
                            mBluetoothAdapter.startDiscovery();
                            Toast.makeText(MainActivity.this,"Discovery started",Toast.LENGTH_SHORT).show();
                            registerReceiver(blReceiver,new IntentFilter(BluetoothDevice.ACTION_FOUND));
                        }
                        else{
                            Toast.makeText(MainActivity.this,"Bluetooth isn't open",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_ENABLE_BT){
            if(resultCode == RESULT_OK){
                mBluetoothStatus.setText("Enabled");
            }else{
                mBluetoothStatus.setText("Disabled");
            }
        }
    }

    private final BroadcastReceiver blReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(device != null) {
                    mBTArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                    mBTArrayAdapter.notifyDataSetChanged();
                }
            }
        }
    };

    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            if(!mBluetoothAdapter.isEnabled()){
                Toast.makeText(MainActivity.this,"Bluetooth not on",Toast.LENGTH_SHORT).show();
                return;
            }
            mBluetoothStatus.setText("Connecting...");
            String info = ((TextView)view).getText().toString();
            final String address = info.substring(info.length() - 17);
            final String name = info.substring(0,info.length() - 17);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    boolean fail = false;
                    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                    try {
                        mBTSocket = createBluetoothSocket(device);
                    } catch (IOException e) {
                        e.printStackTrace();
                        fail = true;
                        Toast.makeText(MainActivity.this,"Socket creation fialed",Toast.LENGTH_SHORT).show();
                    }
                    try {
                        mBTSocket.connect();
                    } catch (IOException e) {
                        try {
                            fail = true;
                            mBTSocket.close();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mBluetoothStatus.setText("Connection failed");
                                }
                            });
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                    if(fail == false){
                        mConnectedThread = new ConnectedThread(mBTSocket);
                        mConnectedThread.start();
                    }
                }
            }).start();
        }
    };

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException{
        try {
            final Method m = device.getClass().getMethod("createInsercureRFcommSocketToServiceRecord", UUID.class);
            return (BluetoothSocket) m.invoke(device,BTMODULEUUID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }

    byte[] value = new byte[1024];
    private class ConnectedThread extends Thread{
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        private ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run(){
            byte[] buffer = new byte[1024];
            int bytes;
            while (true){
                try {
                    bytes = mmInStream.available();
                    if(bytes != 0){
                        buffer = new byte[1024];
                        SystemClock.sleep(100);
                        bytes = mmInStream.available();
                        bytes = mmInStream.read(buffer,0,bytes);
                        value = buffer;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    mReadBuffer.setText(new String((byte[])value,"UTF-8"));
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }

        public void write(String input){
            byte[] bytes = input.getBytes();
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void cancel(){
            try {
                mmSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(blReceiver);
    }
}
