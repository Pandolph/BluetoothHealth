package com.test.BTClient;

import java.io.IOException;
import java.io.OutputStream;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.os.Handler;
import android.os.Message;

public class DataDisplay extends Activity {

    private TextView dis;       //����������ʾ���
    private ScrollView sv;      //��ҳ���
	public static final int RESULT_CODE=1;
    private Button returen =(Button)findViewById(R.id.Button03);
    private Button quit = (Button)findViewById(R.id.Button06);
    private Button send = (Button)findViewById(R.id.Button02);
    BluetoothSocket _socket = null;      //����ͨ��socket
    private EditText edit0 = (EditText)findViewById(R.id.Edit0);   //�õ��������
	@Override
 
	protected void onCreate(Bundle savedInstanceState) {
	    edit0 = (EditText)findViewById(R.id.Edit0);   //�õ��������
	    sv = (ScrollView)findViewById(R.id.ScrollView01);  //�õ���ҳ���
	    dis = (TextView) findViewById(R.id.in);      //�õ�������ʾ���   
		// TODO Auto-generated method stub

		super.onCreate(savedInstanceState);

	    setContentView(R.layout.data);

	    //�ӵ�һ��activity���ղ���

	    Intent intent = getIntent();

	    Bundle bundle = intent.getExtras();

	    String str = bundle.getString("str");

	    //���ʱ���ز�������һ��activity
	    returen.setOnClickListener(listener);
	}
    /** Called when the user clicks the Send button */
    public void sendMessage(View view) {
//        Intent intent = new Intent(this, DataDisplay.class);
//        EditText editText = (EditText) findViewById(R.id.edit_message);
//        String message = editText.getText().toString();
//        intent.putExtra(EXTRA_MESSAGE, message);
//        startActivity(intent);
    }     
                                     
    //���Ͱ�����Ӧ
    public void onSendButtonClicked(View v){
    	int i=0;
    	int n=0;
    	try{
    		OutputStream os = _socket.getOutputStream();   //�������������
    		byte[] bos = edit0.getText().toString().getBytes();
    		for(i=0;i<bos.length;i++){
    			if(bos[i]==0x0a)n++;
    		}
    		byte[] bos_new = new byte[bos.length+n];
    		n=0;
    		for(i=0;i<bos.length;i++){ //�ֻ��л���Ϊ0a,�����Ϊ0d 0a���ٷ���
    			if(bos[i]==0x0a){
    				bos_new[n]=0x0d;
    				n++;
    				bos_new[n]=0x0a;
    			}else{
    				bos_new[n]=bos[i];
    			}
    			n++;
    		}    		
    		os.write(bos_new);	
    	}catch(IOException e){  		
    	}  	
    }

	private OnClickListener listener = new OnClickListener() {

		@Override

		public void onClick(View v) {

			// TODO Auto-generated method stub

			Intent intent = new Intent();

			intent.putExtra("back", "come from second activiy");

			setResult(RESULT_CODE, intent);

			finish();

		}

	};

}