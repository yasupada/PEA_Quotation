package com.yasupada.mobile.pea_quotation;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.projectth.mobile.securityarea.printer.BlueToothService;
import com.projectth.mobile.securityarea.printer.Device;
import com.projectth.mobile.securityarea.printer.PrintService;
import com.projectth.mobile.securityarea.printer.PrinterClass;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity {

    static final int REQUEST_IMAGE_DRIVER = 1;
    static final int REQUEST_IMAGE_LICENSE = 2;
    static final int REQUEST_IMAGE_OTHER = 3;

    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;

    private Boolean imgOneChange = false, imgTwoChange = false, imgOtherChange = false;

    File destinationDriver = null,destinationLicense = null,destinationOther = null;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    Button btnScanQr,btnSave,btnIn,btnOut,btnClear;
    Spinner spnProvince,spnWorking;
    private EditText edtLicense,edtIDNumber;
    private ProgressDialog pd;
    public ArrayList<CarInOut> arrCarIn;
    public CarInOutAdapter carInOutAdapter;
    public String m_current_card_code;
    TextView txvUserStatus,txvLicenseNo;
    EditText edtRemark;

    ImageView imgDriver,imgLicense,imgOther;
    ImageView imageView;

    Handler handler = null;
    public static Handler mhandler;

    String mDriverPhotoPath;
    String mLicensePhotoPath;
    String mOtherPhotoPath;

    private Thread tv_update;
    public static boolean checkState = true;
    // Member fields
    private BluetoothAdapter mBtAdapter;
    private ArrayAdapter<String> mPairedDevicesArrayAdapter = null;
    public static ArrayAdapter<String> mNewDevicesArrayAdapter = null;
    public static List<Device> deviceList=new ArrayList<Device>();
    Set<BluetoothDevice> pairedDevices ;//mBtAdapter.getBondedDevices();
    TextView textView_state;

    String print_Language = "TH";

    public byte[] getText(String textStr) {
        // TODO Auto-generated method stubbyte[] send;
        byte[] send = null;
        try {
            send = textStr.getBytes(print_Language);
        } catch (UnsupportedEncodingException e) {
            send = textStr.getBytes();
        }
        return send;
    }

    public void printQrCode(String message) {
        byte[] bt=getText(message);
        int msgSize = bt.length;
        byte[] btcmd = new byte[4+msgSize];
        if (msgSize > 0) {
            btcmd[0] = 0x1F;
            btcmd[1] = 0x11;
            btcmd[2] = (byte) (msgSize >>> 8);
            btcmd[3] = (byte) (msgSize & 0xff);
        }
        System.arraycopy(bt,0,btcmd,4,msgSize);
        PrintService.pl().write(btcmd);

    }

    private void initPreference() {
        sharedPreferences = getApplicationContext().getSharedPreferences(App.MY_PREFS, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        initPreference();

        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                MainActivity.this.requestPermissions(new String[] { Manifest.permission.CAMERA },1000);
            }

            return;
        }


        final ListView listview = (ListView) findViewById(R.id.list);
        txvUserStatus = findViewById(R.id.txvUserStatus);
        txvLicenseNo = findViewById(R.id.txv_license_no);
        btnClear = findViewById(R.id.btnClear);
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearForm();
            }
        });

        txvLicenseNo.setText("SWG-VSM : " + App.getInstance().LicenseKey);
        arrCarIn = new ArrayList<CarInOut>();
        txvUserStatus.setText(App.getInstance().Name + " " + App.getInstance().Surname);
        carInOutAdapter = new CarInOutAdapter(this,arrCarIn);
        listview.setAdapter(carInOutAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CarInOut ss = (CarInOut)parent.getItemAtPosition(position);
                m_current_card_code = ss.card_number;
                displayDialog(ss);
            }
        });


        edtRemark = (EditText)findViewById(R.id.edtRemark);
        edtLicense=(EditText)findViewById(R.id.edtLicense);
        edtIDNumber=(EditText)findViewById(R.id.edtIDNumber);
        spnWorking=(Spinner)findViewById(R.id.spnWorking);
        spnProvince=(Spinner)findViewById(R.id.spnProvince);
        imgDriver = (ImageView)findViewById(R.id.imgDriver);
        imgLicense = (ImageView)findViewById(R.id.imgLicense);
        imgOther = (ImageView)findViewById(R.id.imgOther);

        imgDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    dispatchTakePictureIntent(1);
            }
        });

        imgLicense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    dispatchTakePictureIntent(2);
            }
        });

        imgOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dispatchTakePictureIntent(3);

            }
        });

        textView_state = findViewById(R.id.textView_state);
        btnScanQr=findViewById(R.id.btnScanQR);
        btnScanQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,ScanQRActivity.class);
                intent.putExtra(App.PROCESS_ACTION,App.QR_LOGIN);
                startActivityForResult(intent,1001);
            }
        });

        btnSave=(Button)findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SaveCarIn saveCarIn = new SaveCarIn();
                saveCarIn.license_plate = edtLicense.getText().toString();
                saveCarIn.province = spnProvince.getSelectedItem().toString();
                saveCarIn.id_card =  edtIDNumber.getText().toString();
                saveCarIn.reason = spnWorking.getSelectedItem().toString();
                saveCarIn.remark = edtRemark.getText().toString();

                saveCarIn.execute();

            }
        });


//        btnIn=(Button)findViewById(R.id.btnSwitchIn);
//        btnIn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                CarOutInfo carOutInfo=new CarOutInfo();
//                carOutInfo.execute();
//            }
//        });

        // Get the local Bluetooth adapter
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        CarOutInfo carOutInfo = new CarOutInfo();
        carOutInfo.execute();

        try{
            mhandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MESSAGE_READ:
                        byte[] readBuf = (byte[]) msg.obj;
                        Log.e(TAG, "readBuf:" + readBuf[0]);
                        if (readBuf[0] == 0x13) {
                            PrintService.isFUll = true;
                            ShowMsg(getResources().getString(
                                    R.string.str_printer_state)
                                    + ":"
                                    + getResources().getString(
                                    R.string.str_printer_bufferfull));
                        } else if (readBuf[0] == 0x11) {
                            PrintService.isFUll = false;
                            ShowMsg(getResources().getString(
                                    R.string.str_printer_state)
                                    + ":"
                                    + getResources().getString(
                                    R.string.str_printer_buffernull));
                        } else if (readBuf[0] == 0x08) {
                            ShowMsg(getResources().getString(
                                    R.string.str_printer_state)
                                    + ":"
                                    + getResources().getString(
                                    R.string.str_printer_nopaper));
                        } else if (readBuf[0] == 0x01) {
                            ShowMsg(getResources().getString(R.string.str_printer_state)+":"+getResources().getString(R.string.str_printer_printing));
                        } else if (readBuf[0] == 0x04) {
                            ShowMsg(getResources().getString(
                                    R.string.str_printer_state)
                                    + ":"
                                    + getResources().getString(
                                    R.string.str_printer_hightemperature));
                        } else if (readBuf[0] == 0x02) {
                            ShowMsg(getResources().getString(
                                    R.string.str_printer_state)
                                    + ":"
                                    + getResources().getString(
                                    R.string.str_printer_lowpower));
                        } else {
                            String readMessage = new String(readBuf, 0, msg.arg1);
                            if (readMessage.contains("800"))// 80mm paper
                            {
                                PrintService.imageWidth = 72;
                                ShowMsg("80mm");
                            } else if (readMessage.contains("580"))// 58mm paper
                            {
                                PrintService.imageWidth = 48;
                                ShowMsg("58mm");
                            }else{
                                ShowMsg(readMessage);
                            }
                        }
                        break;
                    case MESSAGE_STATE_CHANGE:// 蓝牙连接状
                        switch (msg.arg1) {
                            case PrinterClass.STATE_CONNECTED:// 已经连接
                                ShowMsg(getResources().getString(R.string.str_succonnect));
                                break;
                            case PrinterClass.STATE_CONNECTING:// 正在连接
                                ShowMsg(getResources().getString(R.string.str_connecting));
                                break;
                            case PrinterClass.STATE_LISTEN:
                            case PrinterClass.STATE_NONE:
                                break;
                            case PrinterClass.SUCCESS_CONNECT:
                                PrintService.pl().write(new byte[] { 0x1b, 0x2b });// 检测打印机型号
                                break;
                            case PrinterClass.FAILED_CONNECT:
                                ShowMsg(getResources().getString(R.string.str_faileconnect));
                                break;
                            case PrinterClass.LOSE_CONNECT:
                                ShowMsg(getResources().getString(R.string.str_lose));
//                                Intent intent = new Intent();
//                                intent.setClass(MainActivity.this,
//                                        PrintSettingActivity.class);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//                                startActivity(intent);
                                break;
                        }
                        break;
                    case MESSAGE_WRITE:
                        break;
                }
                super.handleMessage(msg);
            }
        };

            handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
                        break;
                    case 1:// 扫描完毕
                        Device d = (Device) msg.obj;
                        if (d != null) {
                            if (deviceList == null) {
                                deviceList = new ArrayList<Device>();
                            }

                            if (!checkData(deviceList, d)) {
                                deviceList.add(d);
                            }
                        }
                        break;
                    case 2:// 停止扫描
                        break;
                }
            }
        };
            mhandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MESSAGE_READ:
                        byte[] readBuf = (byte[]) msg.obj;
                        Log.e("msg send", "readBuf:" + readBuf[0]);
                        if (readBuf[0] == 0x13) {
                            PrintService.isFUll = true;
                            ShowMsg(getResources().getString(
                                    R.string.str_printer_state)
                                    + ":"
                                    + getResources().getString(
                                    R.string.str_printer_bufferfull));
                        } else if (readBuf[0] == 0x11) {
                            PrintService.isFUll = false;
                            ShowMsg(getResources().getString(
                                    R.string.str_printer_state)
                                    + ":"
                                    + getResources().getString(
                                    R.string.str_printer_buffernull));
                        } else if (readBuf[0] == 0x08) {
                            ShowMsg(getResources().getString(
                                    R.string.str_printer_state)
                                    + ":"
                                    + getResources().getString(
                                    R.string.str_printer_nopaper));
                        } else if (readBuf[0] == 0x01) {
                            ShowMsg(getResources().getString(R.string.str_printer_state)+":"+getResources().getString(R.string.str_printer_printing));
                        } else if (readBuf[0] == 0x04) {
                            ShowMsg(getResources().getString(
                                    R.string.str_printer_state)
                                    + ":"
                                    + getResources().getString(
                                    R.string.str_printer_hightemperature));
                        } else if (readBuf[0] == 0x02) {
                            ShowMsg(getResources().getString(
                                    R.string.str_printer_state)
                                    + ":"
                                    + getResources().getString(
                                    R.string.str_printer_lowpower));
                        } else {
                            String readMessage = new String(readBuf, 0, msg.arg1);
                            if (readMessage.contains("800"))// 80mm paper
                            {
                                PrintService.imageWidth = 72;
                                ShowMsg("80mm");
                            } else if (readMessage.contains("580"))// 58mm paper
                            {
                                PrintService.imageWidth = 48;
                                ShowMsg("58mm");
                            }else{
                                ShowMsg(readMessage);
                            }
                        }
                        break;
                    case MESSAGE_STATE_CHANGE:// 蓝牙连接状
                        switch (msg.arg1) {
                            case PrinterClass.STATE_CONNECTED:// 已经连接
                                ShowMsg(getResources().getString(R.string.str_succonnect));
                                break;
                            case PrinterClass.STATE_CONNECTING:// 正在连接
                                ShowMsg(getResources().getString(R.string.str_connecting));
                                break;
                            case PrinterClass.STATE_LISTEN:
                            case PrinterClass.STATE_NONE:
                                break;
                            case PrinterClass.SUCCESS_CONNECT:
                                PrintService.pl().write(new byte[] { 0x1b, 0x2b });// 检测打印机型号
                                break;
                            case PrinterClass.FAILED_CONNECT:
                                ShowMsg(getResources().getString(R.string.str_faileconnect));
                                break;
                            case PrinterClass.LOSE_CONNECT:
                                ShowMsg(getResources().getString(R.string.str_lose));
//                                Intent intent = new Intent();
//                                intent.setClass(MainActivity.this,
//                                        PrintSettingActivity.class);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//                                startActivity(intent);
                                break;
                        }
                        break;
                    case MESSAGE_WRITE:
                        break;
                }
                super.handleMessage(msg);
            }
        };

            handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
                        break;
                    case 1:// 扫描完毕
                        Device d = (Device) msg.obj;
                        if (d != null) {
                            if (deviceList == null) {
                                deviceList = new ArrayList<Device>();
                            }

                            if (!checkData(deviceList, d)) {
                                deviceList.add(d);
                            }
                        }
                        break;
                    case 2:// 停止扫描
                        break;
                }
            }
        };

            PrintService.PrinterInit(0, this, mhandler, handler);

        }catch (Exception bex) {
            Log.e("Main",bex.getMessage());
        }

        tv_update = new Thread() {
            public void run() {
                while (true) {
                    if (checkState) {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {

                            e.printStackTrace();
                        }
                        textView_state.post(new Runnable() {
                            @Override
                            public void run() {

                                if (PrintService.pl() != null) {
                                    if (PrintService.pl().getState() == PrinterClass.STATE_CONNECTED) {
                                        textView_state.setText(MainActivity.this
                                                .getResources()
                                                .getString(
                                                        R.string.str_connected));
                                        //                        ShowMsg("Connected");

                                    } else if (PrintService.pl().getState() == PrinterClass.STATE_CONNECTING) {
                                        textView_state
                                                .setText(MainActivity.this
                                                        .getResources()
                                                        .getString(
                                                                R.string.str_connecting));
                                    } else if (PrintService.pl().getState() == PrinterClass.LOSE_CONNECT
                                            || PrintService.pl().getState() == PrinterClass.FAILED_CONNECT) {
                                        checkState = false;
                                        textView_state
                                                .setText(MainActivity.this
                                                        .getResources()
                                                        .getString(
                                                                R.string.str_disconnected));
//                                        Intent intent = new Intent();
//                                        intent.setClass(MainActivity.this,
//                                                PrintSettingActivity.class);
//                                        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//                                        startActivity(intent);
                                    } else {
                                        textView_state
                                                .setText(MainActivity.this
                                                        .getResources()
                                                        .getString(
                                                                R.string.str_disconnected));
                                    }
                                }
                            }
                        });
                    }
                }
            }
        };

        tv_update.start();

        // Get the local Bluetooth adapter
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        doDiscovery();
        // Get a set of currently paired devices
        pairedDevices = mBtAdapter.getBondedDevices();
        // If there are paired devices, add each one to the ArrayAdapter
        if (pairedDevices.size() > 0) {
            Iterator iter = pairedDevices.iterator();
            String deviceAddr = iter.next().toString();

            BlueToothService.strAddr = deviceAddr;
            PrintService.pl().connect(deviceAddr);
            mBtAdapter.cancelDiscovery();

        } //
    }

    private void clearForm() {
        edtRemark.setText("");
        edtLicense.setText("");
        edtIDNumber.setText("");
        spnProvince.setSelection(0);
        spnWorking.setSelection(0);
        imgDriver.setImageDrawable(getDrawable(R.drawable.logo));
        imgLicense.setImageDrawable(getDrawable(R.drawable.logo));
        imgOther.setImageDrawable(getDrawable(R.drawable.logo));

        imgOneChange = false;
        imgTwoChange = false;
        imgOtherChange = false;

        destinationDriver = null;
        destinationLicense = null;
        destinationOther = null;
    }

    private void doDiscovery() {

        // Indicate scanning in the title
        //      setProgressBarIndeterminateVisibility(true);
        //     setTitle(R.string.scanning);

        // Turn on sub-title for new devices
//        findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);

        // If we're already discovering, stop it

        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }

        // Request discover from BluetoothAdapter
        mBtAdapter.startDiscovery();

    }


    private void ShowMsg(String msg){
        //Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private void initPrinter() {

        ///////////////////////////////////////////////////////////////////
        mhandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MESSAGE_READ:
                        byte[] readBuf = (byte[]) msg.obj;
                        //Log.e(TAG, "readBuf:" + readBuf[0]);
                        if (readBuf[0] == 0x13) {
                            PrintService.isFUll = true;
                            ShowMsg(getResources().getString(
                                    R.string.str_printer_state)
                                    + ":"
                                    + getResources().getString(
                                    R.string.str_printer_bufferfull));
                        } else if (readBuf[0] == 0x11) {
                            PrintService.isFUll = false;
                            ShowMsg(getResources().getString(
                                    R.string.str_printer_state)
                                    + ":"
                                    + getResources().getString(
                                    R.string.str_printer_buffernull));
                        } else if (readBuf[0] == 0x08) {
                            ShowMsg(getResources().getString(
                                    R.string.str_printer_state)
                                    + ":"
                                    + getResources().getString(
                                    R.string.str_printer_nopaper));
                        } else if (readBuf[0] == 0x01) {
                            ShowMsg(getResources().getString(R.string.str_printer_state)+":"+getResources().getString(R.string.str_printer_printing));
                        } else if (readBuf[0] == 0x04) {
                            ShowMsg(getResources().getString(
                                    R.string.str_printer_state)
                                    + ":"
                                    + getResources().getString(
                                    R.string.str_printer_hightemperature));
                        } else if (readBuf[0] == 0x02) {
                            ShowMsg(getResources().getString(
                                    R.string.str_printer_state)
                                    + ":"
                                    + getResources().getString(
                                    R.string.str_printer_lowpower));
                        } else {
                            String readMessage = new String(readBuf, 0, msg.arg1);
                            if (readMessage.contains("800"))// 80mm paper
                            {
                                PrintService.imageWidth = 72;
                                ShowMsg("80mm");
                            } else if (readMessage.contains("580"))// 58mm paper
                            {
                                PrintService.imageWidth = 48;
                                ShowMsg("58mm");
                            }else{
                                ShowMsg(readMessage);
                            }
                        }
                        break;
                    case MESSAGE_STATE_CHANGE:// 蓝牙连接状
                        switch (msg.arg1) {
                            case PrinterClass.STATE_CONNECTED:// 已经连接
                                ShowMsg(getResources().getString(R.string.str_succonnect));
                                break;
                            case PrinterClass.STATE_CONNECTING:// 正在连接
                                ShowMsg(getResources().getString(R.string.str_connecting));
                                break;
                            case PrinterClass.STATE_LISTEN:
                            case PrinterClass.STATE_NONE:
                                break;
                            case PrinterClass.SUCCESS_CONNECT:
                                PrintService.pl().write(new byte[] { 0x1b, 0x2b });// 检测打印机型号
                                break;
                            case PrinterClass.FAILED_CONNECT:
                                ShowMsg(getResources().getString(R.string.str_faileconnect));
                                break;
                            case PrinterClass.LOSE_CONNECT:
                                ShowMsg(getResources().getString(R.string.str_lose));
//                                Intent intent = new Intent();
//                                intent.setClass(MainActivity.this,
//                                        PrintSettingActivity.class);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//                                startActivity(intent);
                                break;
                        }
                        break;
                    case MESSAGE_WRITE:
                        break;
                }
                super.handleMessage(msg);
            }
        };

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
                        break;
                    case 1:// 扫描完毕
                        Device d = (Device) msg.obj;
                        if (d != null) {
                            if (deviceList == null) {
                                deviceList = new ArrayList<Device>();
                            }

                            if (!checkData(deviceList, d)) {
                                deviceList.add(d);
                            }
                        }
                        break;
                    case 2:// 停止扫描
                        break;
                }
            }
        };




    }


    private boolean checkData(List<Device> list, Device d) {
        for (Device device : list) {
            if (device.deviceAddress.equals(d.deviceAddress)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1001&resultCode==RESULT_OK){
            App.getInstance().resultQRCode=data.getExtras().getString("QR_CODE");


            m_current_card_code=App.getInstance().resultQRCode;

            for (CarInOut cio: arrCarIn) {
                if( cio.card_number.trim().equals(m_current_card_code.trim())) {
                    displayDialog(cio);
                    break;
                }
            }

        }else if ((requestCode == REQUEST_IMAGE_DRIVER || requestCode == REQUEST_IMAGE_LICENSE || requestCode == REQUEST_IMAGE_OTHER) && resultCode == RESULT_OK) {

//            Bundle extras = data.getExtras();

            //          Bitmap imageBitmap = (Bitmap) extras.get("data");
            //        imageView.setImageBitmap(imageBitmap);

            galleryAddPic(requestCode);

            Bitmap thumbnail = null;
            try {

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;

                //Log.i("TAG",pFile.getAbsolutePath());
                if(REQUEST_IMAGE_DRIVER==requestCode) {
                    thumbnail = BitmapFactory.decodeFile(mDriverPhotoPath, options);
                    imgDriver.setImageBitmap(Util.scaleDown(thumbnail,400f,false));
                }else if(REQUEST_IMAGE_LICENSE==requestCode) {
                    thumbnail = BitmapFactory.decodeFile(mLicensePhotoPath, options);
                    imgLicense.setImageBitmap(Util.scaleDown(thumbnail,400f,false));
                }else if(REQUEST_IMAGE_OTHER==requestCode) {
                    thumbnail = BitmapFactory.decodeFile(mOtherPhotoPath, options);
                    imgOther.setImageBitmap(Util.scaleDown(thumbnail,400f,false));
                }

                //imgDriver.setImageBitmap(Util.scaleDown(thumbnail,400f,false));

                thumbnail = Util.scaleDown(thumbnail,1200f,false);

                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
                String currentDateandTime = sdf.format(new Date());

                File destination = null;
                FileOutputStream fo = null;

                ////////// case p //////////////
                if(requestCode==1) {
                    destinationDriver = new File(mDriverPhotoPath);
                    destination = destinationDriver;
                }else if(requestCode==2) {
                    destinationLicense = new File(mLicensePhotoPath);
                    destination = destinationLicense;
                }else if(requestCode==3) {
                    destinationOther = new File(mOtherPhotoPath);
                    destination = destinationOther;
                }

                //destination = new File(Environment.getExternalStorageDirectory(),"document_" + currentDateandTime + ".jpg");

                //destination = new File(Environment.getExternalStorageDirectory(),"document_" + currentDateandTime + ".jpg");

                //destination = new File(Environment.getExternalStorageDirectory(),"document_" + currentDateandTime + ".jpg");

                fo = new FileOutputStream(destination);
                fo.write(bytes.toByteArray());
                fo.close();



//                SaveDriverPic up = new SaveDriverPic();
//
//                up.sourceImageFile = destination.getAbsolutePath();
//                up.execute();


            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void displayDialog(CarInOut cio){
        // custom dialog
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.dialog_fee);
        //Dialog d = dialog.setView(new View(this)).create();

        // (That new View is just there to have something inside the dialog that can grow big enough to cover the whole screen.)

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;


        dialog.setTitle("ข้อมูลผู้เข้าพื้นที่");

        // set the custom dialog components - text, image and button


        Button btnAccept = (Button) dialog.findViewById(R.id.btnAccept);
        Button btnNotAccept = (Button) dialog.findViewById(R.id.btnNotAccept);
        Button btnSkipMoney = (Button) dialog.findViewById(R.id.btnSkipMoney);

        TextView txvCarTimeIn = dialog.findViewById(R.id.txvInDateTime);
        TextView txvCarTimeOut = dialog.findViewById(R.id.txvInDateTime);
        TextView txvProvince = dialog.findViewById(R.id.txvProvince);
        TextView txvIdCard = dialog.findViewById(R.id.txvIdCard);
        TextView txvLicense = dialog.findViewById(R.id.txvLicense);
        TextView txvWork = dialog.findViewById(R.id.txvWork);
        TextView txvRemark = dialog.findViewById(R.id.txvRemark);

        txvCarTimeIn.setText("เวลาเข้า: " + cio.time_in);
        //txvCarTimeOut.setText("เวลาออก: " + cio.time_out);
        //txvCarTimeOut.setText("เวลาออก: " + cio.time_out);
        txvLicense.setText("ทะเบียน: " + cio.license_plate);
        txvProvince.setText("จังหวัด: " + cio.province);
        txvIdCard.setText("เลขบัตรประชาชน: " + cio.id_card);
        txvWork.setText("ติดต่อ: " + cio.reason);
        txvRemark.setText("หมายเหตุ: " + cio.remark);

        // if button is clicked, close the custom dialog
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
               // Toast.makeText(MainActivity.this,"บันทึกรับเงินเรียบร้อย",Toast.LENGTH_SHORT).show();

                DoCarOut og=new DoCarOut();
                og.card_code=m_current_card_code;
                og.execute();
            }
        });

        // if button is clicked, close the custom dialog
        btnNotAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                DoCarOut og=new DoCarOut();
                og.card_code=m_current_card_code;
                og.execute();
            }
        });

        // if button is clicked, close the custom dialog
        btnSkipMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                //Toast.makeText(MainActivity.this,"บันทึกไม่เก็บเงิน และเปิดไม้กั้น",Toast.LENGTH_SHORT).show();


                DoCarOut og=new DoCarOut();
                og.card_code=m_current_card_code;
                og.execute();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void stampDialog(){
        // custom dialog
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.dialog_stamp);
        dialog.setTitle("Title...");

        // set the custom dialog components - text, image and button


        Button btnAccept = (Button) dialog.findViewById(R.id.btnAccept);
        Button btnNotAccept = (Button) dialog.findViewById(R.id.btnNotAccept);
        Button btnSkipMoney = (Button) dialog.findViewById(R.id.btnSkipMoney);

        // if button is clicked, close the custom dialog
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                //Toast.makeText(MainActivity.this,"บันทึกรับเงินเรียบร้อย",Toast.LENGTH_SHORT).show();

                DoCarOut og=new DoCarOut();
                og.card_code=m_current_card_code;
                og.execute();
            }
        });

        // if button is clicked, close the custom dialog
        btnNotAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                //Toast.makeText(MainActivity.this,"บันทึกไม่รับเงินเรียบร้อย",Toast.LENGTH_SHORT).show();

            }
        });

        // if button is clicked, close the custom dialog
        btnSkipMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                //Toast.makeText(MainActivity.this,"บันทึกไม่เก็บเงิน และเปิดไม้กั้น",Toast.LENGTH_SHORT).show();


                DoCarOut og=new DoCarOut();
                og.card_code=m_current_card_code;
                og.execute();
            }
        });

        dialog.show();
    }

    // Connection

    /**
     * Car Out Info
     */
    private class CarOutInfo extends AsyncTask<Void, Void, String> {
        String postUrl;



        @Override
        protected void onPreExecute() {
            // Create Show ProgressBar

            postUrl  = App.getInstance().SERVER_URI + "welcome/json_carin/?business_id=" + App.getInstance().BusinessId;
        }

        protected String doInBackground(Void... urls)   {
            client.setConnectTimeout(40, TimeUnit.SECONDS);
            client.setReadTimeout(40, TimeUnit.SECONDS);
            String result = null;
            try {
                RequestBody formBody = new FormEncodingBuilder()
//                        .add(App.APP_VERSION, sharedPreferences.getString(App.APP_VERSION,""))

                        .build();

                Request request = new Request.Builder()
                        .url(postUrl)
                        .post(formBody)
                        .build();

                Response response = client.newCall(request).execute();
                return response.body().string();

            } catch (IOException e) {
                e.printStackTrace();
            }

            return result;
        }

        protected void onPostExecute(String result)  {
            if(pd != null && pd.isShowing()){
                pd.dismiss();
                pd = null;
            }

            try {
                arrCarIn.clear();
                carInOutAdapter.notifyDataSetChanged();
                JSONObject jsonObj = new JSONObject(result);
                if(jsonObj.getJSONArray("items").length()>0) {

                    for(int i=0;i<jsonObj.getJSONArray("items").length();i++){
                        JSONObject jobj=jsonObj.getJSONArray("items").getJSONObject(i);
                        CarInOut carInOut=new CarInOut();
                        carInOut.id=jobj.getInt("id") ;
                        carInOut.name=jobj.getString("name") ;
                        carInOut.surname=jobj.getString("surname") ;
                        carInOut.card_number=jobj.getString("card_number") ;
                        carInOut.gate_in=jobj.getString("gate_in") ;
                        carInOut.user_in=jobj.getString("user_in") ;
                        carInOut.time_in=jobj.getString("time_in") ;
                        carInOut.gate_out=jobj.getString("gate_out") ;
                        carInOut.user_out=jobj.getString("user_out") ;
                        carInOut.time_out=jobj.getString("time_out") ;

                        carInOut.id_card = jobj.getString("id_card") ;
                        carInOut.remark = jobj.getString("remark") ;
                        carInOut.reason = jobj.getString("reason") ;
                        carInOut.license_plate = jobj.getString("license_plate") ;
                        carInOut.province = jobj.getString("province") ;


                        arrCarIn .add(carInOut);
                    }
                    carInOutAdapter.notifyDataSetChanged();

                }else{

                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("เข้าสู่ระบบ")
                            .setMessage("ไม่สามารถเข้าสู่ระบบ เนื่องจาก ชื่อผู้ใช้งาน หรือ รหัสผ่านไม่ถูกต้อง")
                            .setNeutralButton("ปิด",
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                            dialog.dismiss();
                                        }
                                    })
                            .show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        OkHttpClient client = new OkHttpClient();

    } // .End CarOutInfo

    /**
     * Car Out Info
     */
    private class OpenGate extends AsyncTask<Void, Void, String> {
        String postUrl;

        public String card_code;


        @Override
        protected void onPreExecute() {
            // Create Show ProgressBar

            postUrl  = App.getInstance().SERVER_URI + "welcome/assigngateout";
        }

        protected String doInBackground(Void... urls)   {
            client.setConnectTimeout(40, TimeUnit.SECONDS);
            client.setReadTimeout(40, TimeUnit.SECONDS);
            String result = null;
            try {
                RequestBody formBody = new FormEncodingBuilder()
//                        .add(App.APP_VERSION, sharedPreferences.getString(App.APP_VERSION,""))

                        .build();

                Request request = new Request.Builder()
                        .url(postUrl)
                        .post(formBody)
                        .build();

                Response response = client.newCall(request).execute();
                return response.body().string();

            } catch (IOException e) {
                e.printStackTrace();
            }

            return result;
        }

        protected void onPostExecute(String result)  {
            if(pd != null && pd.isShowing()){
                pd.dismiss();
                pd = null;
            }

            try {
                JSONObject jsonObj = new JSONObject(result);
                if(jsonObj.getString("status").equals("OK")) {

                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle(getResources().getString(R.string.message_info))
                            .setMessage("ทำรายการเรียบร้อย")
                            .setNeutralButton(getResources().getString(R.string.close),
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                            dialog.dismiss();
                                            CarOutInfo x=new CarOutInfo();
                                            x.execute();
                                        }
                                    })

                            .show();

                }else{

                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle(getResources().getString(R.string.warnning_info))
                            .setMessage(getResources().getString(R.string.network_process_err))
                            .setNeutralButton(getResources().getString(R.string.close),
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                            dialog.dismiss();

                                        }
                                    })

                            .show();

                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        OkHttpClient client = new OkHttpClient();

    } // .End OpenGate

    /**
     * Car Out Info
     */
    private class DoCarOut extends AsyncTask<Void, Void, String> {
        String postUrl;

        public String card_code;


        @Override
        protected void onPreExecute() {
            // Create Show ProgressBar

            postUrl  = App.getInstance().SERVER_URI + "welcome/carout/?card_number="+card_code;
        }

        protected String doInBackground(Void... urls)   {
            client.setConnectTimeout(40, TimeUnit.SECONDS);
            client.setReadTimeout(40, TimeUnit.SECONDS);
            String result = null;
            try {
                RequestBody formBody = new FormEncodingBuilder()
                        .build();

                Request request = new Request.Builder()
                        .url(postUrl)
                        .build();

                Response response = client.newCall(request).execute();
                return response.body().string();

            } catch (IOException e) {
                e.printStackTrace();
            }

            return result;
        }

        protected void onPostExecute(String result)  {
            if(pd != null && pd.isShowing()){
                pd.dismiss();
                pd = null;
            }

            try {
                JSONObject jsonObj = new JSONObject(result);
                if(jsonObj.getString("status").equals("OK")) {

                    // Toast.makeText(MainActivity.this,"บันทึกเรียบร้อย",Toast.LENGTH_SHORT).show();
                    Toast.makeText(MainActivity.this, "บันทึกรายการ ขาออก บัตรหมายเลข " +  m_current_card_code + " เรียบร้อย",Toast.LENGTH_LONG).show();


                    CarOutInfo carOutInfo = new CarOutInfo();
                    carOutInfo.execute();

//                    new AlertDialog.Builder(MainActivity.this)
//                            .setTitle("ข่าวสาร")
//                            .setMessage("ทำรายการเรียบร้อย")
//                            .setNeutralButton("ปิด",
//                                    new DialogInterface.OnClickListener() {
//
//                                        @Override
//                                        public void onClick(DialogInterface dialog,
//                                                            int which) {
//                                            dialog.dismiss();
//
//                                        }
//                                    })
//
//                            .show();

                }else{

                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("แจ้งเตือน")
                            .setMessage("ไม่สามารถทำรายการได้")
                            .setNeutralButton("ปิด",
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                            dialog.dismiss();
                                        }
                                    })

                            .show();

                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        OkHttpClient client = new OkHttpClient();

    } // .End CreditInfo


    public class CarInOutAdapter extends ArrayAdapter<CarInOut> {
        public CarInOutAdapter(Context context, ArrayList<CarInOut> users) {
            super(context, 0, users);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            // Get the data item for this position
            CarInOut item = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_car_in_out_item, parent, false);
            }

            // Lookup view for data population
            TextView txvTitle = (TextView) convertView.findViewById(R.id.txv);


            // Populate the data into the template view using the data object
            txvTitle.setText(item.card_number +" - " +item.time_in.split("-")[2].split(" ")[0] + "/" + item.time_in.split("-")[1] + "/" + ( Integer.parseInt(item.time_in.split("-")[0]) + 543 ));

            // Return the completed view to render on screen
            return convertView;
        }
    }// .End NewsAdapter

    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",  /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mDriverPhotoPath = image.getAbsolutePath();
        return image;
    }// End

    private File createImageFileLicense() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",  /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mLicensePhotoPath = image.getAbsolutePath();
        return image;
    }// End

    private File createImageFileOther() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",  /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mOtherPhotoPath = image.getAbsolutePath();
        return image;
    }// End

    static final int REQUEST_TAKE_PHOTO = 1;


    private void dispatchTakePictureIntent(int i) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                if(i==1){
                    photoFile = createImageFile();
                }else if (i==2) {
                    photoFile = createImageFileLicense();
                }else if (i==3) {
                    photoFile = createImageFileOther();
                }

            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,  BuildConfig.APPLICATION_ID + ".provider",  photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, i);
            } // End
            } catch (Exception ex) {
                Log.e("xxxxx",ex.getMessage());
                // Error occurred while creating the File

            }
        }
    }// End

    private void galleryAddPic(int i) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = null;
        if(i ==1) {
            f= new File(mDriverPhotoPath);
        }else if(i ==2) {
            f= new File(mLicensePhotoPath);
        }else if(i ==3) {
            f= new File(mOtherPhotoPath);
        }

        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private void setPicDriver() {
        // Get the dimensions of the View
        int targetW = imgDriver.getWidth();
        int targetH = imgDriver.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        imgDriver.setImageBitmap(bitmap);
    }
    ////////////////////////////////////////////////////////////


    /**
     * Send P picture to server
     */
    private class SaveDriverPic extends AsyncTask<String, String, String> {

        String sourceImageFile="";
        String trnCarInOutId = "";
        String businessId = "";
        String picFor = "1";
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(MainActivity.this);
            pd.setMessage("กำลังดำเนินการ...");
            pd.setCancelable(false);
            pd.show();

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... args) {

            String postUrl  = App.getInstance().SERVER_URI + "welcome/save_DriverPic/?business_id=" + businessId + "&trn_id=" + trnCarInOutId + "&pic_for=" + picFor ;

            File sourceFile = new File(sourceImageFile);
            OkHttpClient client = new OkHttpClient();
            client.setConnectTimeout(30, TimeUnit.SECONDS);
            client.setReadTimeout(30, TimeUnit.SECONDS);
            MediaType MEDIA_TYPE = sourceImageFile.endsWith("png") ?
                    MediaType.parse("image/png") : MediaType.parse("image/jpeg");

            RequestBody requestBody = new MultipartBuilder()
                    .type(MultipartBuilder.FORM)
                    .addFormDataPart("file_pic", sourceFile.getName(), RequestBody.create(MEDIA_TYPE, sourceFile))
                    .build();

            Request request = new Request.Builder()
                    .url(postUrl)
                    .post(requestBody)
                    .build();


            Response response = null;
            try {
                response = client.newCall(request).execute();

                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }

                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return "{}";
        }

        @Override
        protected void onPostExecute(String result) {
            if(pd.isShowing()){
                pd.dismiss();
                pd = null;
            }

            Log.i("result",result);


        }
    } // .End P_PicUpload


    /**
     * Send P picture to server
     */
    private class SaveCarIn extends AsyncTask<Void, Void, String> {
        String postUrl;
        String license_plate = "",province="",id_card="",reason="",remark="";

        @Override
        protected void onPreExecute() {
            // Create Show ProgressBar
            postUrl  = App.getInstance().SERVER_URI + "welcome/docarin/?business_id=" + App.getInstance().BusinessId + "&license_plate=" + license_plate+ "&province=" + province+ "&id_card=" + id_card+ "&reason=" + reason+ "&remark=" + remark ;
        }

        protected String doInBackground(Void... urls)   {
            client.setConnectTimeout(30, TimeUnit.SECONDS);
            client.setReadTimeout(30,TimeUnit.SECONDS);
            String result = null;
            try {
                RequestBody formBody = new FormEncodingBuilder()


                        .build();
                Request request = new Request.Builder()
                        .url(postUrl)
                        .post(formBody)
                        .build();


                Response response = client.newCall(request).execute();
                return response.body().string();

            } catch (IOException e) {
                e.printStackTrace();
            }

            return result;
        }

        protected void onPostExecute(String result)  {

            try {
                JSONObject jsonObj = new JSONObject(result);
                if(jsonObj.getString("status").equals("success")) {
                    JSONArray arr = jsonObj.getJSONArray("data");
                    JSONObject obj = arr.getJSONObject(0);
                    String card_number = obj.getString("card_number");
                    String trn_car_in_out_id = obj.getString("trn_car_in_out_id");


                    Calendar cal = Calendar.getInstance();
                    Date date=cal.getTime();
                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    String formattedDate=dateFormat.format(date);

                    String str=spnProvince.getAdapter().getItem(spnProvince.getSelectedItemPosition()) + " " + edtLicense.getText() +" "+formattedDate;


                    if(destinationDriver != null) {
                        SaveDriverPic sdp = new SaveDriverPic();
                        sdp.sourceImageFile = destinationDriver.getAbsolutePath();
                        sdp.trnCarInOutId = trn_car_in_out_id;
                        sdp.execute();
                    }

                    if(destinationLicense != null) {
                        SaveDriverPic sdp = new SaveDriverPic();
                        sdp.sourceImageFile = destinationLicense.getAbsolutePath();
                        sdp.trnCarInOutId = trn_car_in_out_id;
                        sdp.execute();
                    }

                    if(destinationOther != null) {
                        SaveDriverPic sdp = new SaveDriverPic();
                        sdp.sourceImageFile = destinationOther.getAbsolutePath();
                        sdp.trnCarInOutId = trn_car_in_out_id;
                        sdp.execute();
                    }

                    if (!PrintService.pl().IsOpen()) {
                        PrintService.pl().open(MainActivity.this);
                    }else{
                        try {
                            doDiscovery();
                            // Get a set of currently paired devices
                            pairedDevices = mBtAdapter.getBondedDevices();
                            // If there are paired devices, add each one to the ArrayAdapter
                            if (pairedDevices.size() > 0) {
                                Iterator iter = pairedDevices.iterator();
                                String deviceAddr = iter.next().toString();

                                BlueToothService.strAddr = deviceAddr;
                                PrintService.pl().connect(deviceAddr);
                                mBtAdapter.cancelDiscovery();
                            } //
                        }catch (Exception e){
                            Log.e("BluthErro",e.getMessage());
                        }
                    }

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    byte[] cmd_language = new byte[]{0x1B, 0x74, 0x32};

                    //PrintService.pl().write(cmd_language);
                    //PrintService.pl().printQrCode(card_number);

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                    String currentDateandTime = sdf.format(new Date());
                    // print master for customer.
                    String b = App.getInstance().HeaderPage + "\n\n" ;
                    byte[] bb = null;

                    b += "NO: " + card_number +"\n" +
                            "ทะเบียน:  " + spnProvince.getAdapter().getItem(spnProvince.getSelectedItemPosition()) + " " + edtLicense.getText() +"\n" +
                            "เวลาเข้า:  " + formattedDate + "\n" +
                            "วัตถุประสงค์: " + spnWorking.getAdapter().getItem(spnWorking.getSelectedItemPosition()) + "\n" +
                            "หมายเหตุ: " + edtRemark.getText() + " \n\n" +

                            "     ประทับตรา/ลายเซ็น \n" +
                            "   ------------------------\n\n\n\n   ------------------------\n\n" ;

                    try {
                        bb = b.getBytes("Cp874");
                        b = new String(bb,"Cp874");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }


                    PrintService.pl().write(bb);
                    Thread.sleep(500);
                    PrintService.pl().printBarCode(card_number);

                    b = App.getInstance().FootPage + "\n\n   ------- ฉีกตามรอยปุ ------\n\n" + "\n\n" ;

                    try {
                        bb = b.getBytes("Cp874");
                        b = new String(bb,"Cp874");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    PrintService.pl().write(bb);

                    initPrinter();

                }else{

                    new android.app.AlertDialog.Builder(MainActivity.this)
                            .setTitle(getResources().getString(R.string.warnning_info))
                            .setMessage(getResources().getString(R.string.network_process_err))
                            .setNeutralButton(getResources().getString(R.string.close),
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                            dialog.dismiss();


                                        }
                                    })

                            .show();
                }

            } catch (Exception e) {
                e.printStackTrace();

                new android.app.AlertDialog.Builder(MainActivity.this)
                        .setTitle(getResources().getString(R.string.warnning_info))
                        .setMessage(getResources().getString(R.string.network_process_err))
                        .setNeutralButton(getResources().getString(R.string.close),
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        dialog.dismiss();


                                    }
                                })

                        .show();
            }

            // load new list car in
            CarOutInfo carOutInfo = new CarOutInfo();
            carOutInfo.execute();

        }

        OkHttpClient client = new OkHttpClient();



    }

    /**
     * process car out of person out
     */
    private class SaveCarOut extends AsyncTask<Void, Void, String> {
        String postUrl;
        String card_number;

        @Override
        protected void onPreExecute() {
            // Create Show ProgressBar
            postUrl  = App.getInstance().SERVER_URI + "welcome/carout/?card_number=" + card_number  ;
        }

        protected String doInBackground(Void... urls)   {
            client.setConnectTimeout(30, TimeUnit.SECONDS);
            client.setReadTimeout(30,TimeUnit.SECONDS);
            String result = null;
            try {
                RequestBody formBody = new FormEncodingBuilder()

                        .build();
                Request request = new Request.Builder()
                        .url(postUrl)
                        .post(formBody)
                        .build();

                Response response = client.newCall(request).execute();
                return response.body().string();

            } catch (IOException e) {
                e.printStackTrace();
            }

            return result;
        }

        protected void onPostExecute(String result)  {

            try {
                JSONObject jsonObj = new JSONObject(result);
                if(jsonObj.getString("status").equals("OK")) {
                    // call save image
                    SaveDriverPic driverPic = new SaveDriverPic();


                }else{
                    new android.app.AlertDialog.Builder(MainActivity.this)
                            .setTitle(getResources().getString(R.string.warnning_info))
                            .setMessage(getResources().getString(R.string.network_process_err))
                            .setNeutralButton(getResources().getString(R.string.close),
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                            dialog.dismiss();
                                        }
                                    }).show();
                }

            } catch (Exception e) {
                e.printStackTrace();

                new android.app.AlertDialog.Builder(MainActivity.this)
                        .setTitle(getResources().getString(R.string.warnning_info))
                        .setMessage(getResources().getString(R.string.network_process_err))
                        .setNeutralButton(getResources().getString(R.string.close),
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        dialog.dismiss();


                                    }
                                })

                        .show();
            }



        }

        OkHttpClient client = new OkHttpClient();



    } // .End SaveCarOut
}
