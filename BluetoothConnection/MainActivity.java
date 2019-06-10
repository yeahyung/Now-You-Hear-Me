package com.example.geonsu.neckband;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.UUID;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
/*
@author :
 @version :
 @throws :
 @deprecated :
 @param :
 @return :
 @serial :
 @see :
 @since :
*/
/*
진동 발생은 우노에서 다 처리(사운드 센서값 계산해서 설정한 임계값보다 크면 진동)
 임계값을 안드로이드에서 임계값 보내면 바로 반영하도록 -> 안드로이드에서 보내는 값을 받는 변수를 임계값으로 설정
 */

public class MainActivity extends Activity {
    private static final String TAG = "bluetooth2";

    Button btn1, btn2, btn3, btn4; //버튼들
    TextView txtArduino; //아두이노에서 들어오는 텍스트 (초기값 android_arduino bluetooth test)
    EditText andToArd;
    RelativeLayout rlayout; //전체 relative layout
    Handler h;

    final int RECEIVE_MESSAGE = 1;        // 핸들러 상태 확인용 변수
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder sb = new StringBuilder();
    //private static int flag = 0;

    private ConnectedThread mConnectedThread;

    // SPP UUID service
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // 블루투스 모듈의 MAC 주소
    private static String address = "98:D3:71:FD:5B:D6";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        btn1 = (Button) findViewById(R.id.btn1);
        btn2 = (Button) findViewById(R.id.btn2);
        btn3 = (Button) findViewById(R.id.btn3);
        btn4 = (Button) findViewById(R.id.btn4);
        andToArd = findViewById(R.id.typeArea);

        txtArduino = (TextView) findViewById(R.id.txtArduino);
        rlayout = (RelativeLayout) findViewById(R.id.layout);
        h = new Handler() {

            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case RECEIVE_MESSAGE: //1인 경우
                        byte[] readBuf = (byte[]) msg.obj;
                        String strIncom = new String(readBuf, 0, msg.arg1); //들어오는 문자열
                        sb.append(strIncom);
                        int endOfLineIndex = sb.indexOf("\r\n"); //문자열 끝나는 곳 index
                        if (endOfLineIndex > 0) { //들어오는 문자열이 있다면
                            String sbprint = sb.substring(0, endOfLineIndex);
                            sb.delete(0, sb.length());
                            txtArduino.setText("Data from Arduino: " + sbprint);//결과적으로 sbprint가 아두이노에서 BTSerial.println(""); 해서 넘긴것
                            /*
                            if(flag%4==3){
                                rlayout.setBackgroundColor(Color.rgb(255, 255, 255));
                            }
                            else if(flag%4==1){
                                rlayout.setBackgroundColor(Color.rgb(255, 0, 0));
                            }
                            else if(flag%4==2){
                                rlayout.setBackgroundColor(Color.rgb(0, 255, 0));
                            }
                            else if(flag%4==0){
                                rlayout.setBackgroundColor(Color.rgb(0, 0, 255));
                            }
                            flag++; //메시지 핸들 한번 할때마다 화면 색깔 바뀜. 데이터 넘어오는 주기 체크 할 때 참고
                            */
                            if(sbprint.equals("a")){
                                rlayout.setBackgroundColor(Color.rgb(255, 0, 0));
                            }
                            else if(sbprint.equals("b")){
                                rlayout.setBackgroundColor(Color.rgb(0, 255, 0));
                            }
                            else if(sbprint.equals("c")){
                                rlayout.setBackgroundColor(Color.rgb(0, 0, 255));
                            }
                            btn1.setEnabled(true);
                            btn2.setEnabled(true);
                            btn3.setEnabled(true);
                            btn4.setEnabled(true);

                        }
                        break;
                }
            };
        };

        btAdapter = BluetoothAdapter.getDefaultAdapter();       // get Bluetooth adapter
        checkBTState(); //블루투스 지원 기기인지, 블루투스 기능이 켜져있는지 확인

        btn1.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                mConnectedThread.write(andToArd.getText().toString()); //누르면 editText에 입력한 거 전송
                //mConnectedThread.write("code from mainActivity");
                //Toast.makeText(getBaseContext(), "Turn on First LED", Toast.LENGTH_SHORT).show();
            }
        });
        btn2.setOnClickListener(new OnClickListener() {
            public void onClick(View v) { //누르면 2전송
                mConnectedThread.write("2");
                //Toast.makeText(getBaseContext(), "Turn on Second LED", Toast.LENGTH_SHORT).show();
            }
        });
        btn3.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                mConnectedThread.write("3");//누르면 3 전송
                //Toast.makeText(getBaseContext(), "Turn on Third LED", Toast.LENGTH_SHORT).show();
            }
        });
        btn4.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                mConnectedThread.write("0"); //누르면 0 전송
                //Toast.makeText(getBaseContext(), "Turn on all LEDs", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        try {
            final Method  m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", new Class[] { UUID.class });
            return (BluetoothSocket) m.invoke(device, MY_UUID);
        } catch (Exception e) {
            Log.e(TAG, "Could not create Insecure RFComm Connection",e);
        }

        return  device.createRfcommSocketToServiceRecord(MY_UUID);
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d(TAG, "...onResume - try connect...");

        // Set up a pointer to the remote node using it's address.
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        // Two things are needed to make a connection:
        //   A MAC address, which we got above.
        //   A Service ID or UUID.  In this case we are using the
        //     UUID for SPP.

        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            errorExit("Fatal Error", "In onResume() and socket create failed: " + e.getMessage() + ".");
        }

        // Discovery is resource intensive.  Make sure it isn't going on
        // when you attempt to connect and pass your message.
        btAdapter.cancelDiscovery();

        // Establish the connection.  This will block until it connects.
        Log.d(TAG, "...Connecting...");
        try {
            btSocket.connect();
            Log.d(TAG, "....Connection ok...");
            Toast.makeText(getApplicationContext(),"connection is done",Toast.LENGTH_LONG);
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {
                errorExit("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
            }
        }

        // Create a data stream so we can talk to server.
        Log.d(TAG, "...Create Socket...");

        mConnectedThread = new ConnectedThread(btSocket);
        mConnectedThread.start();
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.d(TAG, "...In onPause()...");

        try {
            btSocket.close();
        } catch (IOException e2) {
            errorExit("Fatal Error", "In onPause() and failed to close socket." + e2.getMessage() + ".");
        }
    }

    private void checkBTState() {
        //블루투스 지원 여부, 켜져있는지 확인
        if(btAdapter==null) { //블루투스 미지원 기기인 경우
            errorExit("Fatal Error", "Device doesn't support Bluetooth");
        } else {
            if (btAdapter.isEnabled()) {
                Log.d(TAG, "...Bluetooth ON...");
            } else {
                //블루투스를 실행시킨다
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    private void errorExit(String title, String message){
        Toast.makeText(getBaseContext(), title + " - " + message, Toast.LENGTH_LONG).show();
        finish();
    }

    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[256];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);        // Get number of bytes and message in "buffer"
                    h.obtainMessage(RECEIVE_MESSAGE, bytes, -1, buffer).sendToTarget();     // Send to message queue Handler
                } catch (IOException e) {
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(String message) {
            Log.d(TAG, "...Data to send: " + message + "...");
            byte[] msgBuffer = message.getBytes();
            try {
                mmOutStream.write(msgBuffer);
            } catch (IOException e) {
                Log.d(TAG, "...Error data send: " + e.getMessage() + "...");
            }
        }
    }
}
