package com.minsung.examples.trafficlight;

import android.Manifest;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseSettings;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.iid.FirebaseInstanceId;
import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.BeaconTransmitter;
import java.util.Arrays;
import java.util.UUID;


public class MainActivity extends AppCompatActivity implements MyFirebaseMessagingService.OnTimeChangeListener{

    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private static final String TAG = "sampleCreateBeacon" ;
    private static final int RED = 1;
    private static final int YELLOW =2;
    private static final int GREEN =3;

    private ImageView iv_red;
    private ImageView iv_yellow;
    private ImageView iv_green;
    private EditText et_sec;
    private Button btn_sec;
    private Button btn_reset;
    private TextView tv_timeLeft;
    private InputMethodManager imm;

    private int curLight;
    private Long sec;
    private Long timeLeft;

    private CountDownTimer countDownTimer;

    private Beacon beacon;
    private Long[] data;
    private Long[] data2;

    private String bluetoothID = "2f234454-cf6d-4a0f-adf2-f4911b";
    BeaconTransmitter beaconTransmitter;
    BeaconParser beaconParser;

    Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        iv_red = (ImageView) findViewById(R.id.iv_red);
        iv_yellow = (ImageView) findViewById(R.id.iv_yellow);
        iv_green = (ImageView)findViewById(R.id.iv_green);
        et_sec = (EditText) findViewById(R.id.et_sec);
        btn_sec = (Button) findViewById(R.id.btn_sec);
        btn_reset = (Button) findViewById(R.id.btn_reset);
        tv_timeLeft = (TextView) findViewById(R.id.tv_timeLeft);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        data = new Long[2];
        data2 = new Long[2];

        mHandler = new Handler();


        DataManager dataManager = new DataManager(getApplicationContext());
        // set blueth
        if(dataManager.getBleID().equals("")) {
            String uuid = UUID.randomUUID().toString().replace("-", "");
            bluetoothID += uuid.substring(0, 6);
            System.out.println("------------blueID : " + bluetoothID);
            dataManager.setBleID(bluetoothID);
        }
        else{
            bluetoothID = dataManager.getBleID();
        }
        System.out.println("----------------------id:"+dataManager.getBleID());

        if (FirebaseInstanceId.getInstance().getToken() != null) {
            Log.d(TAG, "token = " + FirebaseInstanceId.getInstance().getToken());
        }




        setOnClickListener();

        // 퍼미션 체크
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("이 앱은 위치 접근 권한을 필요로합니다");
                builder.setMessage("비콘 기능을 활성화 하기 위해서는 권한 설정에 동의해주세요");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                        }
                    }
                });
                builder.show();
            }
        }

        beaconParser = new BeaconParser()
                .setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25");
        beaconTransmitter = new BeaconTransmitter(getApplicationContext(), beaconParser);


    }

    private void setOnClickListener() {
        iv_red.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iv_red.setVisibility(View.VISIBLE);
                iv_yellow.setVisibility(View.INVISIBLE);
                iv_green.setVisibility(View.INVISIBLE);
                curLight = RED;
            }
        });
        iv_yellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iv_yellow.setVisibility(View.VISIBLE);
                iv_red.setVisibility(View.VISIBLE);
                iv_green.setVisibility(View.VISIBLE);
            }
        });
        iv_green.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iv_green.setVisibility(View.VISIBLE);
                iv_yellow.setVisibility(View.INVISIBLE);
                iv_red.setVisibility(View.INVISIBLE);
                curLight = GREEN;
            }
        });

        btn_sec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();
                btn_reset.setClickable(false);
                btn_sec.setClickable(false);

                if(!et_sec.getText().toString().equals("")) {
                    sec = Long.parseLong(et_sec.getText().toString());



                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {

                            System.out.println(bluetoothID);
                            beacon = new Beacon.Builder()
                                    .setId1(bluetoothID)
                                    .setId2(String.valueOf(curLight))   // 현재 light 상태
                                    .setId3(String.valueOf(sec))        // 시간 설정
                                    .setManufacturer(0x0118)
                                    .setTxPower(-59)
                                    .setDataFields(Arrays.asList(new Long[] {0l}))
                                    //.setDataFields(Arrays.asList(data))
                                    .build();

                            boolean success=false;
                            AdvertiseCallbackListener advertiseCallbackListener = new AdvertiseCallbackListener(success);

                            beaconTransmitter.startAdvertising(beacon, advertiseCallbackListener);
// new AdvertiseCallbackListener() {
//                                @Override
//                                public void onStartSuccess(AdvertiseSettings settingsInEffect) {
//                                    super.onStartSuccess(settingsInEffect);
//                                    Log.d(TAG, "-------------------onStartSuccess: ");
//                                    success = true;
//
//                                }
//
//
//                                @Override
//                                public void onStartFailure(int errorCode) {
//                                    super.onStartFailure(errorCode);
//                                    Log.d(TAG, "--------------------onStartFailure: " + errorCode);
//                                }
//                            });
                            success = advertiseCallbackListener.flag;
                            if(success){
                                beaconTransmitter.stopAdvertising();
                            }
                        }
                    });

                    timeLeft = sec*1000;
                    setCountDownTimer();
                }
                else{
                    Toast.makeText(MainActivity.this, "초를 입력해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iv_yellow.setVisibility(View.VISIBLE);
                iv_red.setVisibility(View.VISIBLE);
                iv_green.setVisibility(View.VISIBLE);
                curLight = 0;
            }
        });
    }

    public void setCountDownTimer() {
        tv_timeLeft.setText(String.valueOf(timeLeft / 1000));
        countDownTimer = new CountDownTimer(timeLeft,1000) {
            @Override
            public void onTick(long l) {
                if(timeLeft>0) {
                    tv_timeLeft.setText(String.valueOf(timeLeft / 1000));

                    if(timeLeft<=5000){
                        iv_yellow.setVisibility(View.VISIBLE);
                        iv_green.setVisibility(View.INVISIBLE);
                        iv_red.setVisibility(View.INVISIBLE);
                    }
                }
                timeLeft -= 1000;
            }

            @Override
            public void onFinish() {
                tv_timeLeft.setText(String.valueOf(0));
                iv_yellow.setVisibility(View.INVISIBLE);
                if(curLight==RED){
                    iv_red.setVisibility(View.INVISIBLE);
                    iv_green.setVisibility(View.VISIBLE);
                }
                else if(curLight==GREEN){
                    iv_green.setVisibility(View.INVISIBLE);
                    iv_red.setVisibility(View.VISIBLE);
                }
                btn_reset.setClickable(true);
                btn_sec.setClickable(true);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        beaconTransmitter.stopAdvertising();
                    }
                });

            }
        };
        countDownTimer.start();
    }

    // 퍼미션 요청후 callback
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("", "coarse location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }

                    });
                    builder.show();
                }
                return;
            }
        }
    }

    private void hideKeyboard(){
        imm.hideSoftInputFromWindow(et_sec.getWindowToken(),0);
    }

    @Override
    public void onChange(int time) {
        timeLeft = (long)time*1000;
    }




}
