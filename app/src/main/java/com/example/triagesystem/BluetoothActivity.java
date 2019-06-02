package com.example.triagesystem;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static java.lang.String.valueOf;

public class BluetoothActivity extends AppCompatActivity {

    private TextView status;
    private Button btnConnect;
    private ListView listView;
    private TextView textEx;
    private Dialog dialog;
    private TextInputLayout inputLayout;
    private ArrayAdapter<String> chatAdapter;
    private ArrayList<String> chatMessages;
    private BluetoothAdapter bluetoothAdapter;

    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_OBJECT = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final String DEVICE_OBJECT = "device_name";


    private static final int REQUEST_ENABLE_BLUETOOTH = 1;
    private ChatController chatController;
    private BluetoothDevice connectingDevice;
    private ArrayAdapter<String> discoveredDevicesAdapter;
    List<String> dataArray = new ArrayList<String>();
    TextView puls;
    TextView syspress;
    TextView diapress;
    TextView breath;
    TextView capilar;
    TextView walking;
    TextView answer;
    TextView colorTriage;
    int colour = 0;
    int category =0;


    TextView timer;
    private int seconds;
    private boolean running;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide(); //<< this

        setContentView(R.layout.activity_main);
        findViewsByIds();

        //check device support bluetooth or not
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available!", Toast.LENGTH_SHORT).show();
            finish();
        }

        //show bluetooth devices dialog when click connect button
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPrinterPickDialog();
            }
        });
        running = true;

        //set chat adapter
        chatMessages = new ArrayList<>();
        chatAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, chatMessages);
//        listView.setAdapter(chatAdapter);
        //textt.setText(chatMessages.get(chatMessages.size()-1));

        runTimer();

    }

    private Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case ChatController.STATE_CONNECTED:
                            setStatus("Connected to: " + connectingDevice.getName());
                            btnConnect.setEnabled(true);
                            break;
                        case ChatController.STATE_CONNECTING:
                            setStatus("Connecting...");
                            btnConnect.setEnabled(true);
                            break;
                        case ChatController.STATE_LISTEN:
                        case ChatController.STATE_NONE:
                            setStatus("Not connected");
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;

                    String writeMessage = new String(writeBuf);
                    chatMessages.add("Me: " + writeMessage);
                    chatAdapter.notifyDataSetChanged();
                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;

                    String readMessage = new String(readBuf, 0, msg.arg1);
                    dataArray = Arrays.asList(readMessage.trim().split(","));

                    //textEx.setText(readMessage);

                    puls.setText(dataArray.get(0));
                    syspress.setText(dataArray.get(1));
                    diapress.setText(dataArray.get(2));
                    breath.setText(dataArray.get(3));
                    capilar.setText(dataArray.get(4));
                    walking.setText("0");
                    answer.setText("0");
                    colorTriage.setBackgroundColor(colour);

                    TriageHelper();
                    textEx.setText(valueOf(category));
                    chatMessages.add(connectingDevice.getName() + ":  " + readMessage);
                    chatAdapter.notifyDataSetChanged();
                    break;
                case MESSAGE_DEVICE_OBJECT:
                    connectingDevice = msg.getData().getParcelable(DEVICE_OBJECT);
                    Toast.makeText(getApplicationContext(), "Connected to " + connectingDevice.getName(),
                            Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString("toast"),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
            return false;
        }
    });


    // funkcja automatycznie sprawdzająca triage
    public void TriageHelper() {
        if (walking.getText().equals("1")) {
            category = 1;
            colour = Color.GREEN;
        } else {
            if (answer.getText().equals("1")) {
                if (Integer.valueOf(puls.getText().toString()) > 0) {
                    if (Integer.valueOf(breath.getText().toString()) < 30) {
                        category = 2;
                        colour = Color.YELLOW;
                    } else {
                        category = 3;
                        colour = Color.RED;
                    }
                } else {
                    category = 3;
                    colour = Color.RED;

                }
            } else {
                category = 4;
                colour = Color.BLACK;

            }
        }
    }


    public void showPrinterPickDialog() {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.layout_bluetooth);
        dialog.setTitle("Bluetooth Devices");

        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
        bluetoothAdapter.startDiscovery();

        //Initializing bluetooth adapters
        ArrayAdapter<String> pairedDevicesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        discoveredDevicesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);

        //locate listviews and attatch the adapters
        ListView listView = (ListView) dialog.findViewById(R.id.pairedDeviceList);
        ListView listView2 = (ListView) dialog.findViewById(R.id.discoveredDeviceList);
        listView.setAdapter(pairedDevicesAdapter);
        listView2.setAdapter(discoveredDevicesAdapter);

        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(discoveryFinishReceiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(discoveryFinishReceiver, filter);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        // If there are paired devices, add each one to the ArrayAdapter
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                pairedDevicesAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        } else {
            pairedDevicesAdapter.add(getString(R.string.none_paired));
        }

        //Handling listview item click event
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                bluetoothAdapter.cancelDiscovery();
                String info = ((TextView) view).getText().toString();
                String address = info.substring(info.length() - 17);

                connectToDevice(address);
                dialog.dismiss();
            }

        });

        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                bluetoothAdapter.cancelDiscovery();
                String info = ((TextView) view).getText().toString();
                String address = info.substring(info.length() - 17);

                connectToDevice(address);
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }

    private void setStatus(String s) {
        status.setText(s);
    }

    private void connectToDevice(String deviceAddress) {
        bluetoothAdapter.cancelDiscovery();
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);
        chatController.connect(device);
    }

    private void findViewsByIds() {
        status = (TextView) findViewById(R.id.status);
        btnConnect = (Button) findViewById(R.id.btn_connect);
//        listView = (ListView) findViewById(R.id.list);
        textEx = (TextView) findViewById(R.id.example);

        ///// VIEWS OF ACTIVITY MAIN LAYOUT
        puls = (TextView) findViewById(R.id.puls);
        syspress = (TextView) findViewById(R.id.syspress);
        diapress = (TextView) findViewById(R.id.diapress);
        breath = (TextView) findViewById(R.id.breath);
        capilar = (TextView) findViewById(R.id.capilar);
        walking = (TextView) findViewById(R.id.walking);
        answer = (TextView) findViewById(R.id.answer);
        colorTriage = (TextView) findViewById(R.id.triageauto);

        ////////////////////////////////////

//
//        inputLayout = (TextInputLayout) findViewById(R.id.input_layout);

//        View btnSend = findViewById(R.id.btn_send);
//
//        btnSend.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (inputLayout.getEditText().getText().toString().equals("")) {
//                    Toast.makeText(BluetoothActivity.this, "Please input some texts", Toast.LENGTH_SHORT).show();
//                } else {
//                    //TODO: here
////                    sendMessage(inputLayout.getEditText().getText().toString()+ seconds);
////                    inputLayout.getEditText().setText("");
//                }
//            }
//        });
    }

    private void runTimer(){
        final TextView tmr = findViewById(R.id.time);
        final TextView exText =(TextView)findViewById(R.id.example);

        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                int secon = seconds;

                String time = String.format("%d",secon);
                tmr.setText(time);
                if(running){
                    seconds++;
                }
                handler.postDelayed(this,1000);
                //inputLayout.getEditText().setText("");
            }
        });
    }



                public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BLUETOOTH:
                if (resultCode == Activity.RESULT_OK) {
                    chatController = new ChatController(this, handler);
                } else {
                    Toast.makeText(this, "Bluetooth still disabled, turn off application!", Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    private void sendMessage(String message) {
        if (chatController.getState() != ChatController.STATE_CONNECTED) {
            Toast.makeText(this, "Connection was lost!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (message.length() > 0) {
            byte[] send = message.getBytes();
            chatController.write(send);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BLUETOOTH);
        } else {
            chatController = new ChatController(this, handler);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (chatController != null) {
            if (chatController.getState() == ChatController.STATE_NONE) {
                chatController.start();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (chatController != null)
            chatController.stop();
    }



    private final BroadcastReceiver discoveryFinishReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    discoveredDevicesAdapter.add(device.getName() + "\n" + device.getAddress());
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                if (discoveredDevicesAdapter.getCount() == 0) {
                    discoveredDevicesAdapter.add(getString(R.string.none_found));
                }
            }
        }
    };
}