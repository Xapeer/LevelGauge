package com.example.levelgauge;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.BLUETOOTH_CONNECT;
import static android.Manifest.permission.BLUETOOTH_SCAN;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;


import com.itextpdf.text.pdf.PdfWriter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    SwitchCompat switchCompat;

    private BluetoothAdapter bluetoothAdapter;
    private Button btnScan;
    private Button btnDisconnect;
    private Button btnStop;
    private boolean mScanning;
    private static final long SCAN_PERIOD = 10000;
    private final Map<BluetoothDevice, Integer> mBtDevices = new HashMap<>();
    private TableLayout mTableDevices;
    public TextView tvStatusTop;
    private TextView tvReceivedData;
    private TextView tvReceivedData2;
    private Button btn_menustatus;
    private Button btn_phonenumber;
    private Button btn_settings;
    private Button btn_usersettings;
    private Button btn_getimei;
    private Button btn_testsendtoserver;
    public String settings;
    private int login_count = 0;
    private int data_receive_flag = 0;
    private int count_send_times = 0;
    private EditText entercorrection;

    private ImageButton btn_sendtodevice;
    private String path;
    public String temp_path = "/storage/emulated/0/";
    public String file_path;
    public String test;
    private static final int LONG_DELAY = 3500; // 3.5 seconds
    private static final int SHORT_DELAY = 1500;
    ProgressDialog progressDialog;
    // Tag used for logging
    private static final String TAG = "MainActivity";
    private Button btn_sentdata;
    private Button login;
    // BLE
    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothLeScanner mBtScanner = null;
    public BluetoothGatt mBluetoothGatt;
    private Handler mHandler = new Handler();
    private final HashMap<String, BluetoothGattCharacteristic> mGattCharacteristics = new HashMap<>();
    private final HashMap<String, String> gattAttributes = new HashMap<>();
    public final String ACTION_GATT_CONNECTED = "ACTION_GATT_CONNECTED";
    public final String ACTION_GATT_DISCONNECTED = "ACTION_GATT_DISCONNECTED";
    public final String ACTION_GATT_SERVICES_DISCOVERED = "ACTION_GATT_SERVICES_DISCOVERED";
    public final String TEST_MESSAGE = "TEST_MESSAGE";
    private static final String ACTION_SCAN_TIMEOUT = "ACTION_SCAN_TIMEOUT";
    private static final String ACTION_DEVICE_NOT_FOUND = "ACTION_DEVICE_NOT_FOUND";
    public final String ACTION_DATA_AVAILABLE = "ACTION_DATA_AVAILABLE";
    public final String ACTION_WRITE_SUCCESS = "ACTION_WRITE_SUCCESS";
    private static final int FILE_SELECT_CODE = 0;
    public String fileName = "test.txt";
    //public final String ACTION_CHECK_CHARACTERISTICS = "ACTION_CHECK_CHARACTERISTICS";
    // Request codes
    private static final int REQUEST_ACCESS_COARSE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE = 1;
    private final static int MY_PERMISSIONS_REQUEST_ENABLE_BT = 2;
    private final static int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 3;
    private final static int REQUEST_CODE_LOGINPASS = 7;
    private final static int REQUEST_CODE_USERSETTINGS = 8;
    private final static int REQUEST_CODE_CURRENTSERVERPASS = 9;
    private Uri baseDocumentTreeUri;
    //public static String LED_SERVICE = "c4913d0c-65d6-11eb-ae93-0242ac130002";
    public static String LED_SERVICE = "F0001110-0451-4000-B000-000000000000";
    public static String BUTTON_SERVICE = "F0001120-0451-4000-B000-000000000000";
    public static String DATA_SERVICE = "F0001130-0451-4000-B000-000000000000";
    public static String LED0_STATE = "F0001111-0451-4000-B000-000000000000";
    public static String LED1_STATE = "F0001112-0451-4000-B000-000000000000";
    public static String BUTTON0_STATE = "F0001121-0451-4000-B000-000000000000";
    public static String BUTTON1_STATE = "F0001122-0451-4000-B000-000000000000";
    public static String STRING_CHAR = "F0001131-0451-4000-B000-000000000000";
    public static String STREAM_CHAR = "F0001132-0451-4000-B000-000000000000";
    private Handler scanDelayedHandler;

    public int noOfRows;
    public int noOfColumns;
    public float[][] matrix;
    public static int gl_recei_len;
    public static String a;
    public static int tmparr_len;
    public static int get_flag;
    public static int switch_flag = 0;
    public final String DIR_SD = "Urovnemer";
    public final String FILENAME_SD = "History.txt";
    public int checksenttimes = 0;
    public String filerowstring = "7";
    public int amountofcells;
    public int curRow = 1;
    public int curCol = 1;
    public int startsending = 0;
    public int stop_handler = 0;
    public int btn_disable = 0;
    public int count_time = 0;
    public int cancel_Timer = 0;
    public int user_flag = 0;
    public int admin_flag = 0;
    public int userRights = 0;
    public String currentString;
    public double b = 0.00;
    public static int data = 0;
    public String formattedString;
    public int author = 0;
    public int fileid = 0;
    public String file_name;
    //public final UUID UUID_SVR_MAIN_SERVICE_DESCRIPTOR = UUID.fromString("c4913d0c-65d6-11eb-ae93-0242ac130002"); // UUID for notification descriptor
    public final UUID UUID_CHARACTERISTIC_UPDATE_NOTIFICATION_DESCRIPTOR = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"); // UUID for notification descriptor
    public final static UUID UUID_BUTTON_SERVICE = UUID.fromString(BUTTON_SERVICE);
    public final static UUID UUID_LED_SERVICE = UUID.fromString(LED_SERVICE);
    public final static UUID UUID_LED0_STATE = UUID.fromString(LED0_STATE);
    public final static UUID UUID_LED1_STATE = UUID.fromString(LED1_STATE);
    //public final UUID UUID_DATA_CHARACTERISTIC = UUID.fromString("F0001131-0451-4000-B000-000000000000"); // UUID for DATA SERVICE
    public File log;
    // Intent extras
    public final static String EXTRA_DATA = "EXTRA_DATA";
    private final String DEVICE_NAME = "Urovnemer";
    private final Queue<BluetoothGattCharacteristic> characteristicQueue = new LinkedList<>();
    private final Queue<BluetoothGattDescriptor> descriptorWriteQueue = new LinkedList<>();
    private final ArrayList<ScanFilter> mScanFilters = new ArrayList<>();
    private ScanSettings mScanSettings;
    String[] permissions_ble = {"android.permission.BLUETOOTH_SCAN"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_getimei = findViewById(R.id.btn_getimei);
        entercorrection = findViewById(R.id.entercorrection);
        btn_sendtodevice = findViewById(R.id.btn_sendtodevice);
        btn_menustatus = findViewById(R.id.btn_menustatus);
        btn_usersettings = findViewById(R.id.btn_usersettings);
        invisible();
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.txt_layout);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(entercorrection.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            requestPermissions(permissions_ble, 777);
        }

        if(ContextCompat.checkSelfPermission(this, BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED){

        }

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "BLE not supported", Toast.LENGTH_SHORT).show();
            finish();
        }

        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();


        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth not supported", Toast.LENGTH_SHORT).show();
            finish();
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                // Show explanation on why this is needed
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("This app needs location access");
                builder.setMessage("Please grant location access so this app can discover bluetooth devices");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        // Prompt the user once explanation has been shown
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                                REQUEST_ACCESS_COARSE);
                    }
                });
                builder.show();

            } else {
                // Prompt user for location access
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                        REQUEST_ACCESS_COARSE);
            }
        }
        //if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
        //    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
        //        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        //    } else {
        //        // Prompt user for location access
        //        ActivityCompat.requestPermissions(this,
        //               new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
        //                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        //    }
        //}

        btn_usersettings = findViewById(R.id.btn_usersettings);
        btn_testsendtoserver = findViewById(R.id.btn_testsendtoserver);
        switchCompat = findViewById(R.id.switchon);
        tvReceivedData = findViewById(R.id.tvReceivedData);
        tvReceivedData2 = findViewById(R.id.tvReceivedData2);
        tvStatusTop = findViewById(R.id.tvStatusTop);
        mTableDevices = findViewById(R.id.devicesFound);
        btnStop = findViewById(R.id.btnStop);
        btnScan = findViewById(R.id.btnScan);

        /*
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRegisterWindow();
            }
        });
         */
        btn_usersettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBluetoothGatt == null) {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "ПОЖАЛУЙСТА ПОДКЛЮЧИТЕСЬ К УСТРОЙСТВУ",
                            Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }

                showSettingsWindow();
            }
        });


        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanLeDevice(!mScanning);
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanLeDevice(false);
            }
        });


        btn_settings = findViewById(R.id.btn_enter);
        btn_settings.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (mBluetoothGatt == null) {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "ПОЖАЛУЙСТА ПОДКЛЮЧИТЕСЬ К УСТРОЙСТВУ",
                            Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }
                mBluetoothGatt.requestMtu(50);
                try {
                    Thread.sleep(100); //Приостанавливает поток на 1 секунду
                } catch (Exception e) {

                }
                Dialog Menudialog = new Dialog(MainActivity.this);
                Menudialog.setContentView(R.layout.dialog_scroll);
                Button btn_data = Menudialog.findViewById(R.id.btndata);
                btn_data.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Menudialog.dismiss();
                        try {
                            Thread.sleep(1000); //Приостанавливает поток на 1 секунду
                        } catch (Exception e) {

                        }
                        if (mBluetoothGatt == null) {
                            Toast toast = Toast.makeText(getApplicationContext(),
                                    "ПОЖАЛУЙСТА ПОДКЛЮЧИТЕСЬ К УСТРОЙСТВУ",
                                    Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return;
                        }
                        BluetoothGattService led_service = mBluetoothGatt.getService(UUID_LED_SERVICE);

                        if (get_flag == 3) {
                            BluetoothGattCharacteristic tmpChar = led_service.getCharacteristic(UUID_LED0_STATE);
                            byte[] value = new byte[1];
                            value[0] = (byte) ('x' & 0xFF);
                            tmpChar.setValue(value);
                            tmpChar.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                            writeCharacteristic(tmpChar);
                            ShowProgressDialog();
                            return;
                        }
                        Date date = new Date();
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-hh-mm");
                        file_name = formatter.format(date);
                        fileid = 1;
                        data_receive_flag = 2;
                        BluetoothGattCharacteristic tmpChar = led_service.getCharacteristic(UUID_LED0_STATE);
                        byte[] value = new byte[1];
                        value[0] = (byte) ('d' & 0xFF);
                        tmpChar.setValue(value);
                        tmpChar.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                        writeCharacteristic(tmpChar);
                        ShowProgressDialog();

                    }
                });

                Button btn_testsendtoserver = Menudialog.findViewById(R.id.btn_testsendtoserver);
                btn_testsendtoserver.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BluetoothGattService led_service = mBluetoothGatt.getService(UUID_LED_SERVICE);
                        if (led_service == null) {
                            return;
                        }
                        BluetoothGattCharacteristic tmpChar = led_service.getCharacteristic(UUID_LED0_STATE);
                        tvReceivedData.setText("");
                        tvReceivedData2.setText("");
                        switch_flag = 1;
                        force_false_status(switchCompat);
                        Menudialog.dismiss();
                        String send_log = "t";
                        tmpChar.setValue(send_log);
                        tmpChar.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                        writeCharacteristic(tmpChar);
                        entercorrection.onEditorAction(EditorInfo.IME_ACTION_DONE);
                        byte[] value = new byte[1];
                        //value[0] = (byte) ('n' & 0xFF);
                        tmpChar.setValue(value);
                        tmpChar.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                        writeCharacteristic(tmpChar);
                        runTimer();
                    }
                });
                Button btn_getimei = Menudialog.findViewById(R.id.btn_getimei);
                btn_getimei.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch_flag = 1;
                        force_false_status(switchCompat);
                        tvReceivedData.setText("");
                        tvReceivedData2.setText("");
                        Menudialog.dismiss();
                        BluetoothGattService led_service = mBluetoothGatt.getService(UUID_LED_SERVICE);
                        if (led_service == null) {
                            return;
                        }
                        BluetoothGattCharacteristic tmpChar = led_service.getCharacteristic(UUID_LED0_STATE);
                        String send_log = "i";
                        tmpChar.setValue(send_log);
                        tmpChar.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                        writeCharacteristic(tmpChar);
                        //entercorrection.onEditorAction(EditorInfo.IME_ACTION_DONE);
                        //byte[] value = new byte[1];
                        //value[0] = (byte) ('n' & 0xFF);
                        //tmpChar.setValue(value);
                        //tmpChar.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                        //writeCharacteristic(tmpChar);
                    }
                });


                SwitchCompat switchCompat = Menudialog.findViewById(R.id.switchon);
                switch_status(switchCompat, Menudialog);
                Menudialog.show();


            }
        });



        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());

        // Services
        gattAttributes.put(LED_SERVICE.toLowerCase(), "Led Service");
        gattAttributes.put(BUTTON_SERVICE.toLowerCase(), "Button Service");
        gattAttributes.put(DATA_SERVICE.toLowerCase(), "Data Service");
        // Characteristics
        gattAttributes.put(LED0_STATE.toLowerCase(), "Led0 State");
        gattAttributes.put(LED1_STATE.toLowerCase(), "Led1 State");
        gattAttributes.put(BUTTON0_STATE.toLowerCase(), "Button0 State");
        gattAttributes.put(BUTTON1_STATE.toLowerCase(), "Button1 State");
        gattAttributes.put(STRING_CHAR.toLowerCase(), "String char");
        gattAttributes.put(STREAM_CHAR.toLowerCase(), "Stream char");

        btnDisconnect = findViewById(R.id.btnDisconnect);
        btnDisconnect.setVisibility(View.INVISIBLE);
        btnStop.setVisibility(View.INVISIBLE);
        btnDisconnect.setEnabled(false);
        btnDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disconnect();
            }
        });

        ScanFilter filter = new ScanFilter.Builder().setDeviceName(DEVICE_NAME).build();
        //mScanFilters.add(filter);

        // Configure default scan settings
        mScanSettings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_POWER).build();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, MY_PERMISSIONS_REQUEST_ENABLE_BT);
        }
    }

    @Override
    protected void onDestroy() {
        disconnect();


        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        } else if (requestCode == REQUEST_ACCESS_COARSE && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        } else if (requestCode == FILE_SELECT_CODE && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            path = uri.getPath().split(":")[1];
            file_path = temp_path + path;
            //ReadExcel();
            //((TextView) findViewById(R.id.tv_filepath)).setText(""+ file_path );
            //btn_folder.setText(file_path);
        } else if (requestCode == REQUEST_CODE_LOGINPASS && resultCode == RESULT_OK) {
            String loginpassfromact = data.getStringExtra("Loginpass");
            char test = loginpassfromact.charAt(0);
            String c = String.valueOf(test);
            c.toLowerCase(Locale.ROOT);
            tvReceivedData.setText(c);
            String a = "A";
            String u = "U";
            if (c.equals(a)) {
                tvReceivedData.setText("ADMIN INTERFACE");
                admin_flag = 1;
                user_flag = 0;
                userRights = 1;
                adminRights();
            } else if (c.equals(u)) {
                tvReceivedData.setText("USER INTERFACE");
                admin_flag = 0;
                user_flag = 1;
                userRights = 2;
                userRights();
            }
            BluetoothGattService led_service = mBluetoothGatt.getService(UUID_LED_SERVICE);
            if (led_service == null) {
                return;
            }
            BluetoothGattCharacteristic tmpChar = led_service.getCharacteristic(UUID_LED0_STATE);
            String send_log = "l" + loginpassfromact;
            tmpChar.setValue(send_log);
            tmpChar.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
            writeCharacteristic(tmpChar);
        } else if (requestCode == REQUEST_CODE_USERSETTINGS && resultCode == RESULT_OK) {
            String settings_value = data.getStringExtra("user_settings");
            tvReceivedData.setText(settings_value);
            BluetoothGattService led_service = mBluetoothGatt.getService(UUID_LED_SERVICE);
            if (led_service == null) {
                return;
            }
            BluetoothGattCharacteristic tmpChar = led_service.getCharacteristic(UUID_LED0_STATE);
            tmpChar.setValue(settings_value);
            tmpChar.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
            writeCharacteristic(tmpChar);
        }else if(requestCode == REQUEST_CODE_CURRENTSERVERPASS && resultCode == RESULT_OK){
            Bundle arguments = getIntent().getExtras();
            String name = arguments.get("current_server_pass").toString();
            BluetoothGattService led_service = mBluetoothGatt.getService(UUID_LED_SERVICE);
            if (led_service == null) {
                return;
            }
            BluetoothGattCharacteristic tmpChar = led_service.getCharacteristic(UUID_LED0_STATE);
            tmpChar.setValue(name);
            tmpChar.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
            writeCharacteristic(tmpChar);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_ACCESS_COARSE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Coarse location permission granted");
            } else {
                // Access location was not granted. Display a warning.
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Functionality limited");
                builder.setMessage("Since location access has not been granted, this app will not display any bluetooth scan results.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                    }
                });
                builder.show();
            }
        }
        if(requestCode == 777){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //Toast.makeText(this, "Download CODE", Toast.LENGTH_SHORT).show();
            }
            else{
                //Toast.makeText(this, "Download Cancel", Toast.LENGTH_SHORT).show();
            }
        }


        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }


    //    private class IterateDevicesTask extends TimerTask {
//
//        @Override
//        public void run() {
//            broadcastUpdate(ACTION_TIMER_TIMEOUT);
//        }
//    }
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            final String action = intent.getAction();
            switch (action) {
                case ACTION_GATT_CONNECTED:
                    Zeroingvariables();
                    tvStatusTop.setText("Соединен");
                    tvReceivedData.setText("");
                    tvReceivedData2.setText("");
                    btnDisconnect.setEnabled(true);
                    btnScan.setVisibility(View.INVISIBLE);
                    btnStop.setVisibility(View.INVISIBLE);
                    btnDisconnect.setVisibility(View.VISIBLE);
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            stopConnectionProgressDialog();
                            showRegisterWindow();
                        }//ЗАКРЫТЬ МЕНЮ ДИАЛОГ
                        //Menudialog.dismiss();
                    }, 3000);
                    ShowProgressDialog();
                    break;
                case ACTION_GATT_DISCONNECTED:
//                    stopTimer();
//                    dataCharacteristic = null;
                    invisible();
                    btnDisconnect.setVisibility(View.INVISIBLE);
                    tvStatusTop.setText("Разъединен");
                    btnDisconnect.setEnabled(false);
                    tvReceivedData.setText("");
                    tvReceivedData2.setText("");
                    btnScan.setVisibility(View.VISIBLE);
                    break;
                case ACTION_GATT_SERVICES_DISCOVERED:
//                    BluetoothGattService dataService = getGattServiceByUuid(UUID_DATA_SERVICE);
//                    if (dataService != null) {
//                        dataCharacteristic = dataService.getCharacteristic(UUID_DATA_CHARACTERISTIC);
//                        if (dataCharacteristic != null) {
//                            timer = new Timer();
//                            timer.schedule(new IterateDevicesTask(), 0, 1000);
//                        }
//
//                    }
                    initializeGattServiceUIElements(getSupportedGattServices());
                    break;
                case ACTION_DATA_AVAILABLE:
                    byte[] receivedMess = intent.getByteArrayExtra(EXTRA_DATA);
                    String receivedValueStr = "";
                    gl_recei_len = receivedMess.length;
                    System.out.println(gl_recei_len);
                    if (receivedMess.length > 0) {
                        StringBuilder dis = getDistance(receivedMess);
                        receivedValueStr = dis + "";
                    }


                    if (get_flag == 1) {

                        //ПОЛУЧИЛ СТРОКУ И СОХРАНИЛ В ФАЙЛ

                        get_flag = 0;
                        sentsuccess();
                        try {
                            savetobd(receivedValueStr);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else if (get_flag == 2) {
                        get_flag = 0;
                    } else if (get_flag == 3) {
                        get_flag = 0;
                        if (switch_flag == 1) {
                            BluetoothGattService led_service = mBluetoothGatt.getService(UUID_LED_SERVICE);
                            BluetoothGattCharacteristic tmpChar = led_service.getCharacteristic(UUID_LED0_STATE);
                            byte[] value = new byte[1];
                            value[0] = (byte) ('z' & 0xFF);
                            tmpChar.setValue(value);
                            tmpChar.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                            writeCharacteristic(tmpChar);
                            switch_flag = 0;
                            tvReceivedData.setText("");
                            tvReceivedData2.setText("");
                        } else {
                            if (author == 1) {
                                author = 0;
                                System.out.println(receivedValueStr);
                                Integer first_element_of_receivedValueStr = Integer.valueOf(receivedValueStr.charAt(0));
                                if (first_element_of_receivedValueStr == 48) {
                                    System.out.println(first_element_of_receivedValueStr);
                                    test = receivedValueStr.substring(1, 8);
                                    System.out.println(test);
                                    String[] output = test.split("v");
                                    StringBuffer sb = new StringBuffer(output[1]);
                                    sb.insert(1, ".");
                                    tvReceivedData.setText(output[0] + "mm" + "       " + sb + "V");
                                } else {
                                    String[] output = receivedValueStr.split("v");
                                    StringBuffer sb = new StringBuffer(output[1]);
                                    sb.insert(1, ".");
                                    tvReceivedData.setText(output[0] + "mm" + "       " + sb + "V");
                                }
                            } else if (author == 2) {
                                author = 0;
                                ///////////////////////////////////////////////
                                ///////////////////////////////////////////////
                                //NEW VERSION FOR RASHID////////////////////////////////////
                                currentString = receivedValueStr;
                                System.out.println(currentString);

                                int index_of_d = currentString.indexOf('d');
                                int index_of_v = currentString.indexOf('v');
                                int index_of_N = currentString.indexOf('N');
                                int index_of_E = currentString.indexOf('E');
                                int index_of_D = currentString.indexOf('D');

                                String distance = currentString.substring(index_of_d + 1, index_of_v);

                                // All string separators (',' '.' '-') are removed before sending,
                                // so we need to add them back

                                // Parse voltage string
                                String voltage = currentString.substring(index_of_v + 1, index_of_N);
                                if (voltage.length() == 3) {
                                    voltage = voltage.substring(0,2) + "," + voltage.substring(2);
                                }else {
                                    voltage = voltage.substring(0,1) + "," + voltage.substring(1);
                                }

                                // Parse coordinates strings
                                String north = currentString.substring(index_of_N + 1, index_of_E);
                                String east = currentString.substring(index_of_E + 1, index_of_D);
                                north = north.substring(0,2) + "." + north.substring(2);
                                east = east.substring(0,2) + "." + east.substring(2);

                                // Parse date string
                                String date = currentString.substring(index_of_D + 1, index_of_D + 11);
                                date = FormatDate(date);

                                String displayText = "Глубина: " + distance + " cм" + "\n" +
                                                     "Напряжение: " + voltage + " в" + "\n" +
                                                     "N: " + north + "\n" +
                                                     "E: " + east + "\n" +
                                                     "Дата: " + date;

                                tvReceivedData.setText(displayText);

                                ///////////////////////////////////////////////////////////////
                                ///////////////////////////////////////////////////////////////
                                ///////////////////////////////////////////////////////////////
                            }

                            BluetoothGattService led_service = mBluetoothGatt.getService(UUID_LED_SERVICE);
                            BluetoothGattCharacteristic tmpChar = led_service.getCharacteristic(UUID_LED0_STATE);
                            byte[] value = new byte[1];
                            value[0] = (byte) ('n' & 0xFF);
                            tmpChar.setValue(value);
                            tmpChar.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                            writeCharacteristic(tmpChar);
                            get_flag = 0;
                            switch_flag = 0;
                        }
                    } else if (get_flag == 4) {
                        //////////////////////////////////////////////////////////
                        //ПОЛУЧАЕМ ДАТУ С СЕРВЕРА ПОСЛЕ УСПЕШНОЙ ТЕСТОВОЙ ОТПРАВКИ
                        //////////////////////////////////////////////////////////
                        count_time = 0;
                        get_flag = 0;
                        StringBuffer sb = new StringBuffer(receivedValueStr);
                        sb.insert(2, "/");
                        sb.insert(5, "/");
                        sb.insert(8, "-");
                        sb.insert(11, ":");
                        stopConnectionProgressDialog();
                        cancel_Timer = 1;
                        tvReceivedData.setText(sb);
                        //stop_handler = 1;
                    } else if (get_flag == 5) {
                        get_flag = 0;
                        StringBuffer sb = new StringBuffer(receivedValueStr);
                        sb.insert(5, "  ");
                        sb.insert(12, "  ");
                        tvReceivedData.setText(sb);
                    } else if (get_flag == 6) {
                        get_flag = 0;
                        //btn_testsendtoserver.setEnabled(false);
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                tvReceivedData.setText("");
                                tvReceivedData2.setText("");
                            }//ЗАКРЫТЬ МЕНЮ ДИАЛОГ
                            //Menudialog.dismiss();
                            //
                        }, 3000);
                        tvReceivedData.setText("Устройство занято");
                        switch_flag = 1;
                        force_false_status(switchCompat);
                    }else if(get_flag == 8){
                        get_flag = 0;
                        tvReceivedData.setText(receivedValueStr);
                    }
                    else {
                        //sentstop();
                        //StopProgressDialog();
                    }
                    break;
//              case ACTION_TIMER_TIMEOUT:
//                    if (dataCharacteristic != null) {
//                        readCharacteristic(dataCharacteristic);
//                    }
//                    break;
                //case ACTION_CHECK_CHARACTERISTICS:
                //    int checkData = intent.getIntExtra(EXTRA_DATA, 777);
                //    ((TextView) findViewById(R.id.textView2)).setText(String.valueOf(checkData));
                //    //((TextView) findViewById(R.id.textView2)).setText(""+ checkData );
                //    break;


                case TEST_MESSAGE:
                    tvReceivedData.setText("MTU success !");
                    break;
            }

        }
    };

    //    private void stopTimer() {
//        timer.cancel();
//        timer.purge();
//    }

    public String FormatDate(String inputDate){

        // Set input-output date patterns
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyMMddHHmm");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yy-MM-dd HH:mm");

        // Parse input date string
        LocalDateTime dateTime = LocalDateTime.parse(inputDate, inputFormatter);

        // Generate and return result
        return dateTime.format(outputFormatter);
    }

    public void switch_status(SwitchCompat switchCompat, Dialog Menudialog) {
        switchCompat.setChecked(false);
        SharedPreferences sharedPreferences = getSharedPreferences("save", MODE_PRIVATE);
        switchCompat.setChecked(sharedPreferences.getBoolean("value", false));
        switchCompat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switchCompat.isChecked()) {
                    data_receive_flag = 1;
                    SharedPreferences.Editor editor = getSharedPreferences("save", MODE_PRIVATE).edit();
                    editor.putBoolean("value", true);
                    editor.apply();
                    switchCompat.setChecked(true);
                    BluetoothGattService led_service = mBluetoothGatt.getService(UUID_LED_SERVICE);
                    BluetoothGattCharacteristic tmpChar = led_service.getCharacteristic(UUID_LED0_STATE);
                    byte[] value = new byte[1];
                    value[0] = (byte) ('n' & 0xFF);
                    tmpChar.setValue(value);
                    tmpChar.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                    writeCharacteristic(tmpChar);
                    switch_flag = 0;
                    Menudialog.dismiss();
                } else {
                    SharedPreferences.Editor editor = getSharedPreferences("save", MODE_PRIVATE).edit();
                    editor.putBoolean("value", false);
                    editor.apply();
                    switchCompat.setChecked(false);
                    tvReceivedData.setText("");
                    tvReceivedData2.setText("");
                    BluetoothGattService led_service = mBluetoothGatt.getService(UUID_LED_SERVICE);
                    BluetoothGattCharacteristic tmpChar = led_service.getCharacteristic(UUID_LED0_STATE);
                    byte[] value = new byte[1];
                    value[0] = (byte) ('z' & 0xFF);
                    tmpChar.setValue(value);
                    tmpChar.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                    writeCharacteristic(tmpChar);
                    switch_flag = 1;
                    data_receive_flag = 0;
                    Menudialog.dismiss();
                }
            }
        });
    }

    public void force_false_status(SwitchCompat switchCompat) {
        //switchCompat.setChecked(false);
        SharedPreferences.Editor editor = getSharedPreferences("save", MODE_PRIVATE).edit();
        editor.putBoolean("value", false);
        editor.apply();

    }

    private StringBuilder getDistance(byte[] arr) {
        byte[] tmpArr = new byte[gl_recei_len];
        char ch = 0;
        StringBuilder strBuilder = new StringBuilder();
        for (int i = 0; i < gl_recei_len; i++) {
            tmpArr[i] = arr[i];
        }
        tmparr_len = tmpArr.length;


        if (tmpArr[0] == 86) {
            tvReceivedData.setText("ЛОГИРОВАНИЕ УСПЕШНО");
        } else if (tmpArr[0] == 87) {
            tvReceivedData.setText("НЕ ВЕРНЫЙ ПАРОЛЬ");
            login_count++;

            if (login_count == 3) {
                login_count = 0;
                disconnect();
            } else {
                showRegisterWindow();
            }

        } else if (tmpArr[0] == 116) {
            System.out.println("GETT DATA");
            for (int i = 1; i < 11; i++) {
                ch = (char) tmpArr[i];

                //System.out.println(i);
                //System.out.println(ch);
                strBuilder.append(ch);
            }
            get_flag = 4;

            return strBuilder;
        } else if (tmpArr[0] == 78) {
            tvReceivedData.setText("NAME SUCCESS CHANGED");
        } else if (tmpArr[0] == 80 && tmpArr.length < 2) {
            tvReceivedData.setText("PASSWORD SUCCESS CHANGED");
        } else if (tmpArr[0] == 67) {
            tvReceivedData.setText("SERVER PASS SUCCESS CHANGED");
        } else if (tmpArr[0] == 80 && tmpArr[1] == 80 && tmpArr[2] == 80){
            for (int i = 3; i < 12; i++) {
                ch = (char) tmpArr[i];

                //System.out.println(i);
                //System.out.println(ch);
                strBuilder.append(ch);
            }
            get_flag = 8;
            return strBuilder;
        }else if(tmpArr[0] == 69 && tmpArr[1] == 69 && tmpArr[2] == 69){
            tvReceivedData.setText("Невозможно получить ID");
        }

        else if (tmpArr[0] == 100) {
            author = 2;
            //tmparr_len = 32;
            //int a = tmpArr[14];
            //b = (a + 294) / 100;
            //formattedString = String.format("%.02d", b);
            if (data_receive_flag == 1) {
                get_flag = 3;
            } else if (data_receive_flag == 2) {
                //ПРОГРАММА ДЛЯ РАШИДА ПЛАТЫ

                get_flag = 1;
            }

        } else if (tmpArr[0] == 98 && tmpArr[1] == 98 && tmpArr[2] == 98) {
            get_flag = 6;
           /* new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    tvReceivedData.setText("");
                                                                }//ЗАКРЫТЬ МЕНЮ ДИАЛОГ
                                                                //Menudialog.dismiss();
                                                                //
                                                            }, 10000);

            */
        } else if (tmpArr[0] == 105) {
            for (int i = 1; i < 17; i++) {
                ch = (char) tmpArr[i];

                //System.out.println(i);
                //System.out.println(ch);
                strBuilder.append(ch);
            }
            get_flag = 5;
            return strBuilder;
        } else if (tmpArr[0] == 117) {
            author = 1;
            System.out.println("GETT DATA");

            for (int i = 1; i < 9; i++) {
                ch = (char) tmpArr[i];

                //System.out.println(i);
                //System.out.println(ch);
                strBuilder.append(ch);
            }
            get_flag = 3;
            return strBuilder;
        } else if (tmpArr[15] == 88 || tmpArr[15] == 86) {
            //ПРОГРАММА ДЛЯ ГЕОРГИЯ ПЛАТЫ
            tmparr_len = 16;
            author = 1;
            int a = tmpArr[14];
            b = (a + 294) / 100;
            //formattedString = String.format("%.02d", b);
            get_flag = 1;
        } else if (tmpArr[28] == 86 || tmpArr[28] == 88) {
            tmparr_len = 32;

            get_flag = 1;
        } else if (tmpArr[1] == 70 && tmpArr[5] == 70 && tmpArr[11] == 70) {
            get_flag = 0;
            StopProgressDialog();
            sentstop();
        } else if (tmpArr[0] == 55 && tmpArr[1] == 55) {
            //ShowProgressDialog();
            get_flag = 2;
        }


        for (int i = 0; i < tmparr_len; i++) {
            ch = (char) tmpArr[i];

            //System.out.println(i);
            //System.out.println(ch);
            strBuilder.append(ch);
        }
        //System.out.println(strBuilder);
        return strBuilder;
    }

    private void ShowProgressDialog() {
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setTitle("");
        progressDialog.setMessage("Пожалуйста, подождите");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void startConnectionProgressDialog() {
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setTitle("");
        progressDialog.setMessage("Пожалуйста, подождите");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public void buttonCreateFile(View view) throws IOException {
        Intent intent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            intent = new Intent(Intent.ACTION_CREATE_DOCUMENT, MediaStore.Downloads.EXTERNAL_CONTENT_URI);
            intent.setType("text/plain");
            this.startActivity(intent);
        }

    }

    private void savetobd(String testtext) throws IOException {
        StorageManager storageManager = (StorageManager) getSystemService(STORAGE_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            StorageVolume storageVolume = storageManager.getStorageVolumes().get(0);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
                Date date = Calendar.getInstance().getTime();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-hh-mm");
                String format = formatter.format(date);
                if(startsending == 1){
                    File file = new File(path,  format + count_send_times + "history.txt");
                    FileWriter fileWriter = new FileWriter(file, true);
                    String text = testtext + "\n";
                    fileWriter.write(text);
                    fileWriter.close();
                }
                else if(startsending == 0){
                    File file = new File(path, format + count_send_times  + "history.txt");
                    if (!file.exists()) {
                        System.out.println("We had to make a new file.");
                        file.createNewFile();
                        startsending = 1;
                    }
                }

            }
            else{
                try{
                    //File root = new File(Environment.getExternalStorageDirectory(), DIR_SD);
                    // getExternalStorageDirectory() requires permission to write
                    // getExternalFilesDir() doesn't and uses app specific dir instead (/Android/data/com.example.appname)
                    File root = getExternalFilesDir(null);

                    Date date = new Date();
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-hh-mm");
                    String format = formatter.format(date);

                    File log = new File(root, format + count_send_times + "history.txt");

                    if (!log.exists()) {
                        System.out.println("We had to make a new file.");
                        log.createNewFile();
                    }

                    FileWriter fileWriter = new FileWriter(log, true);
                    String text = testtext + "\n";
                    fileWriter.write(text);
                    fileWriter.close();

                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void stopConnectionProgressDialog() {
        progressDialog.dismiss();
    }

    private void runTimer() {

        startConnectionProgressDialog();
        Timer mTimer = new Timer();
        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                if (count_time < 300) {
                    count_time++;
                    if (cancel_Timer == 1) {
                        mTimer.cancel();
                        //tvReceivedData.setText("Таймер остановлен");
                        cancel_Timer = 0;
                    }


                    //tvReceivedData.setText(count_time);
                } else {
                    count_time = 0;
                    mTimer.cancel();
                    stopConnectionProgressDialog();
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            tvReceivedData.setText("");
                            tvReceivedData2.setText("");
                        }//ЗАКРЫТЬ МЕНЮ ДИАЛОГ
                        //Menudialog.dismiss();
                        //
                    }, 10000);
                    tvReceivedData.setText("Нет ответа от сервера");

                }
            }
        };
        mTimer.schedule(tt, 0, 1000);
        //timer.cancel();


    }

    private void sentserverProgressDialog() {
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setTitle("Соединение с сервером");
        progressDialog.setMessage("Пожалуйста, подождите");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void stopSentServerProgressDialog() {
        progressDialog.dismiss();
        Toast toast = Toast.makeText(getApplicationContext(),
                "Соединение с сервером Прошло успешно",
                Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    private void StopProgressDialog() {
        progressDialog.dismiss();
        Toast toast = Toast.makeText(getApplicationContext(),
                "Данные успешно сохранены на устройстве",
                Toast.LENGTH_LONG);

        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
        startsending = 0;
        data_receive_flag = 0;
        count_send_times++;
    }

    private void immediatelycloseProgDialog() {
        progressDialog.dismiss();
    }

    private void StopProgressDialogSendFile() {

        progressDialog.dismiss();
        Toast toast = Toast.makeText(getApplicationContext(),
                "Данные успешно отправлены на устройстве",
                Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();

    }

    private void invisible() {
        //tv_menustatus.setVisibility(View.INVISIBLE);
        btn_menustatus.setVisibility(View.INVISIBLE);
        btn_sendtodevice.setVisibility(View.INVISIBLE);
        entercorrection.setText(null);
        entercorrection.setVisibility(View.INVISIBLE);
    }

    private void makevissible() {
        //tv_menustatus.setTextSize(20);
        //tv_menustatus.setBackgroundColor(Color.parseColor("#FFFFFF"));
        //tv_menustatus.setVisibility(View.VISIBLE);
        btn_menustatus.setVisibility((View.VISIBLE));
        entercorrection.setVisibility(View.VISIBLE);
        btn_sendtodevice.setVisibility(View.VISIBLE);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////// ЛОГИРОВАНИЕ  /////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////
    private void showRegisterWindow() {
        userRights = 0;
        Intent intent = new Intent(this, Login_activity.class);
        startActivityForResult(intent, REQUEST_CODE_LOGINPASS);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////



    ///////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////ИЗМЕНЯТЬ ДАННЫЕ   /////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////
    private void showSettingsWindow() {
        Intent intent = new Intent(this, Settings_activity.class);
        intent.putExtra("Userrights", userRights);
        setResult(RESULT_OK, intent);
        startActivityForResult(intent, REQUEST_CODE_USERSETTINGS);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////

    public void closekeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void sentsuccess() {
        BluetoothGattService led_service = mBluetoothGatt.getService(UUID_LED_SERVICE);
        BluetoothGattCharacteristic tmpChar = led_service.getCharacteristic(UUID_LED0_STATE);
        byte[] value = new byte[1];
        value[0] = (byte) ('s' & 0xFF);
        tmpChar.setValue(value);
        tmpChar.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
        writeCharacteristic(tmpChar);
    }

    private void sentstop() {
        BluetoothGattService led_service = mBluetoothGatt.getService(UUID_LED_SERVICE);
        BluetoothGattCharacteristic tmpChar = led_service.getCharacteristic(UUID_LED0_STATE);
        byte[] value = new byte[1];
        value[0] = (byte) ('x' & 0xFF);
        tmpChar.setValue(value);
        tmpChar.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
        writeCharacteristic(tmpChar);
    }

    private void requestLocationPermission() {

    }

    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permission Needed")
                    .setMessage("This permission needed")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }
    }

    private void requestWritingPermision() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }


    }

    private void invalidateScanButton() {
        if (!mScanning) {
            btnScan.setText("Сканировать");
        } else {
            btnScan.setVisibility(View.INVISIBLE);
            btnStop.setVisibility(View.VISIBLE);
        }
    }

    private void initializeGattServiceUIElements(List<BluetoothGattService> gattServices) {
//        String uuid;
//        String serviceName;

        mGattCharacteristics.clear();

        Set<String> discoveredServiceUuids = new HashSet<>();
        for (BluetoothGattService s : gattServices) {
            discoveredServiceUuids.add(s.getUuid().toString());
            for (BluetoothGattCharacteristic c : s.getCharacteristics()) {
                discoveredServiceUuids.add(c.getUuid().toString());
            }
        }
    }

    private void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "Bluetooth not initialized");
            return;
        }
        // Queue the characteristic to read, since several reads are done on startup
        characteristicQueue.add(characteristic);
        // If there is only 1 item in the queue, then read it. If more than 1, it is handled
        // asynchronously in the callback
        if ((characteristicQueue.size() == 1)) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_DENIED)
                if (Build.VERSION.SDK_INT >= 31) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 100);
                    return;
                }
            mBluetoothGatt.readCharacteristic(characteristic);

        }
    }

    private void writeCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_DENIED)
            if (Build.VERSION.SDK_INT >= 31) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 100);
                return;
            }
        mBluetoothGatt.writeCharacteristic(characteristic);
    }

    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enable) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "Bluetooth not initialized");
            return;
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_DENIED)
            if (Build.VERSION.SDK_INT >= 31) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 100);
                return;
            }
        // Enable/disable notification
        mBluetoothGatt.setCharacteristicNotification(characteristic, enable);

        // Write descriptor for notification
        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID_CHARACTERISTIC_UPDATE_NOTIFICATION_DESCRIPTOR);
        descriptor.setValue(enable ? BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE : new byte[]{0x00, 0x00});
        writeGattDescriptor(descriptor);
    }

    private void writeGattDescriptor(BluetoothGattDescriptor d) {
        // Add descriptor to the write queue
        descriptorWriteQueue.add(d);
        // If there is only 1 item in the queue, then write it. If more than 1, it will be handled
        // in the onDescriptorWrite callback
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_DENIED)
            if (Build.VERSION.SDK_INT >= 31) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 100);
                return;
            }
        if (descriptorWriteQueue.size() == 1) {
            mBluetoothGatt.writeDescriptor(d);
        }
    }

    private List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null) {
            return null;
        }
        return mBluetoothGatt.getServices();
    }

    private void Zeroingvariables(){
        switch_flag = 0;
        stop_handler = 0;
        count_time = 0;
        cancel_Timer = 0;
    }



    private BluetoothGattService getGattServiceByUuid(UUID uuid) {
        if (mBluetoothGatt == null) {
            return null;
        }

        return mBluetoothGatt.getService(uuid);
    }
    final Runnable scanRunnable = new Runnable() {
        @Override
        public void run() {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_DENIED)
                if (Build.VERSION.SDK_INT >= 31) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 100);
                    return;
                }
            mScanning = false;
            mBtScanner.stopScan(mScanCallback);
            broadcastUpdate(ACTION_DEVICE_NOT_FOUND);
            Log.e("Scan device", "Scan STOPPED");
        }
    };

    private void scanLeDevice(final boolean enable) {
        if (mBtScanner == null) {
            mBtScanner = mBluetoothAdapter.getBluetoothLeScanner();
        }

        if (enable) {
            mBtDevices.clear();
            scanDelayedHandler = new Handler(Looper.getMainLooper());
            scanDelayedHandler.postDelayed(scanRunnable, 10000);

            mScanning = true;
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_DENIED)
                if (Build.VERSION.SDK_INT >= 31) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 100);
                    return;
                }
            mBtScanner.startScan(mScanFilters, mScanSettings, mScanCallback);

        }
        else {
            mScanning = false;
            mBtScanner.stopScan(mScanCallback);


            //////////////////////////////////////////////////////////////////
            //НАДПИСЬ СКАНИРОВАНИЕ ОСТАНОВЛЕННО ПОСЛЕ ПОДКЛЮЧЕНИЯ К УСТРОЙСТВУ
            //////////////////////////////////////////////////////////////////

            /*
            Toast toast = Toast.makeText(getApplicationContext(),
                    "СКАНИРОВАНИЕ ОСТАНОВЛЕНО",
                    Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            */
            ////////////////////////////////////////////////////////////////////
            ////////////////////////////////////////////////////////////////////


            btnStop.setVisibility(View.INVISIBLE);
            btnScan.setVisibility(View.VISIBLE);
        }
        invalidateScanButton();
    }

    private final ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            final BluetoothDevice btDevice = result.getDevice();
            if (btDevice == null) {
                Log.e("ScanCallback", "Could not get bluetooth device");
                return;
            }

            String macAddress = btDevice.getAddress();
            for (BluetoothDevice dev : mBtDevices.keySet()) {
                if (dev.getAddress().equals(macAddress)) {
                    return;
                }


            }
            mBtDevices.put(btDevice, result.getRssi());
            updateDeviceTable();

        }
    };

    private void updateDeviceTable() {
        mTableDevices.removeAllViews();
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_DENIED)
            if (Build.VERSION.SDK_INT >= 31) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 100);
                return;
            }
        for (final BluetoothDevice savedDevice : mBtDevices.keySet()) {

            // Get RSSI of this device
            int rssi = mBtDevices.get(savedDevice);

            // Create a new row
            final TableRow tr = new TableRow(MainActivity.this);
            tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT));
            tr.setGravity(Gravity.CENTER);

            // Add Text view for rssi
            TextView tvRssi = new TextView(MainActivity.this);
            tvRssi.setText(rssi + " dBm");
            TableRow.LayoutParams params = new TableRow.LayoutParams(0);
            params.setMargins(20, 0, 20, 0);
            tvRssi.setLayoutParams(params);

            TextView tvEmpty1 = new TextView(MainActivity.this);
            tvEmpty1.setText("");
            tvEmpty1.setLayoutParams(new TableRow.LayoutParams(1));

            // Add Text view for device, displaying name and address
            TextView tvDevice = new TextView(MainActivity.this);
            String devName = savedDevice.getName() != null ? savedDevice.getName() : "Unidentified";
            //Поменять название на устройство

            //if (devName.contains("Urov")) {

            tvDevice.setText(devName + "\r\n" + savedDevice.getAddress());
            tvDevice.setLayoutParams(new TableRow.LayoutParams(2));
            tvDevice.setGravity(Gravity.CENTER);

            TextView tvEmpty2 = new TextView(MainActivity.this);
            tvEmpty2.setText("");
            tvEmpty2.setLayoutParams(new TableRow.LayoutParams(3));

            // Add a connect button to the right
            Button b = new Button(MainActivity.this);
            b.setBackgroundColor(getResources().getColor(R.color.white));
            b.setTextColor(getResources().getColor(R.color.cryola));
            b.setText("ПОДКЛЮЧИТЬ");
            b.setGravity(Gravity.CENTER);

            // Create action when clicking the connect button
            b.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    connectToDevice(savedDevice);
                    force_false_status(switchCompat);
                }
            });

            // Add items to the row
            tr.addView(tvRssi);
            //tr.addView(tvEmpty1);
            tr.addView(tvDevice);
            //tr.addView(tvEmpty2);
            tr.addView(b);
            //}




            // Add row to the table layout
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mTableDevices.addView(tr);
                }
            });
        }
    }

    public void connectToDevice(BluetoothDevice btDevice) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_DENIED)
            if (Build.VERSION.SDK_INT >= 31) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 100);
                return;
            }
        if (mBluetoothGatt == null) {
            mBluetoothGatt = btDevice.connectGatt(this, false, mGattCallback);
            mTableDevices.removeAllViews();
            // Stop scanning
            if (mScanning) {
                scanLeDevice(false);
            }
        }
    }

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        private void enableButtonNotifications(BluetoothGatt gatt) {
            // Loop through the characteristics for the button service
            //for (BluetoothGattCharacteristic characteristic : gatt.getService(UUID_SVR_MAIN_SERVICE_DESCRIPTOR).getCharacteristics()) {
            for (BluetoothGattCharacteristic characteristic : gatt.getService(UUID_BUTTON_SERVICE).getCharacteristics()) {
                // Enable notification on the characteristic
                final int charaProp = characteristic.getProperties();
                if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                    setCharacteristicNotification(characteristic, true);
                    //((TextView) findViewById(R.id.textView2)).setText("" + gatt.getService(UUID_BUTTON_SERVICE).getCharacteristics());
                    // ((TextView) findViewById(R.id.textView2)).setText("0"+ characteristic.getDescriptors());
                }
            }
            //((TextView) findViewById(R.id.textView2)).setText("1" + tmpChar.getValue());
            // Enable notification on the characteristic
            //UUID test = tmpChar();
            //tmpChar.setValue(0,0,0);
            //mBluetoothGatt.writeCharacteristic(tmpChar);
            //List<BluetoothGattDescriptor> descrip = tmpChar.getDescriptors();
            //((TextView) findViewById(R.id.textView2)).setText("Test"+ tmpChar);
            //((TextView) findViewById(R.id.textView2)).setText("Test"+ tmpChar.getValue());
            //broadcastUpdate(ACTION_CHECK_CHARACTERISTICS,tmpChar);

        }
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_DENIED)
                if (Build.VERSION.SDK_INT >= 31) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 100);
                    return;
                }
            String intentAction;
            switch (newState) {
                case BluetoothProfile.STATE_CONNECTED:
                    intentAction = ACTION_GATT_CONNECTED;
                    broadcastUpdate(intentAction);
                    Log.i("gattCallback", "STATE_CONNECTED");
                    gatt.discoverServices();

                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    Log.i("gattCallback", "STATE_DISCONNECTED");
                    intentAction = ACTION_GATT_DISCONNECTED;
                    broadcastUpdate(intentAction);
                    // Close connection completely after disconnect, to be able
                    // to start clean.
                    if (mBluetoothGatt != null) {
                        mBluetoothGatt.close();
                        mBluetoothGatt = null;
                    }
                    break;
                default:
                    Log.e("gattCallback", "STATE_OTHER");
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.i(TAG, "onServicesDiscovered: " + status);

            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
                enableButtonNotifications(gatt);


            } else {
                Log.w(TAG, "onServicesDiscovered received with error: " + status);
            }
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(TEST_MESSAGE);
                enableButtonNotifications(gatt);

            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_DENIED)
                if (Build.VERSION.SDK_INT >= 31) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 100);
                    return;
                }
            characteristicQueue.remove();
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            } else {
                Log.d(TAG, "onCharacteristicRead error: " + status);
            }

            // Handle the next element from the queues
            if (characteristicQueue.size() > 0)
                mBluetoothGatt.readCharacteristic(characteristicQueue.element());
            else if (descriptorWriteQueue.size() > 0)
                mBluetoothGatt.writeDescriptor(descriptorWriteQueue.element());
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                // Broadcast data written to the data service string characteristic
                if ((UUID.fromString(STRING_CHAR)).equals(characteristic.getUuid())) {
                    broadcastUpdate(ACTION_WRITE_SUCCESS);
                }
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            // Broadcast the received notification
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_DENIED)
                if (Build.VERSION.SDK_INT >= 31) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 100);
                    return;
                }
            if (status != BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, "Callback: Error writing GATT Descriptor: " + status);
            }

            // Pop the item that we just finishing writing
            descriptorWriteQueue.remove();

            // Continue handling items if there is more in the queues
            if (descriptorWriteQueue.size() > 0)
                mBluetoothGatt.writeDescriptor(descriptorWriteQueue.element());
            else if (characteristicQueue.size() > 0)
                mBluetoothGatt.readCharacteristic(characteristicQueue.element());
        }
    };

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action, final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);
        final byte[] data = characteristic.getValue();
        if (data != null && data.length > 0) {
            intent.putExtra(EXTRA_DATA, data);
        }
        sendBroadcast(intent);
    }

    private IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_GATT_CONNECTED);
        intentFilter.addAction(ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(ACTION_SCAN_TIMEOUT);
        intentFilter.addAction(ACTION_DEVICE_NOT_FOUND);
        intentFilter.addAction(ACTION_DATA_AVAILABLE);
        intentFilter.addAction(ACTION_WRITE_SUCCESS);
        return intentFilter;
    }

    private void userRights(){
        btn_usersettings.setVisibility(View.INVISIBLE);
    }

    private void adminRights(){
        btn_usersettings.setVisibility(View.VISIBLE);
    }

    public void disconnect() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_DENIED)
            if (Build.VERSION.SDK_INT >= 31) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 100);
                return;
            }
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "Bluetooth not initialized");
            return;
        }
        force_false_status(switchCompat);
        mBluetoothGatt.disconnect();
    }
}