package com.test.BTClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import com.test.BTClient.DeviceListActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.SurfaceHolder.Callback;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class BTClient extends Activity {	
	private final static int REQUEST_CONNECT_DEVICE = 1;    //�궨���ѯ�豸���	
	private final static String MY_UUID = "00001101-0000-1000-8000-00805F9B34FB";   //SPP����UUID��	
	private InputStream is;    //������������������������
    private String smsg = "00";    //��ʾ�����ݻ���
    private String fmsg = "00";    //���������ݻ���    
    static int temp = 0;                //��ʱ�������ڱ�����յ�������    
    int READ = 1;                   //һ�����������ڴ���������Ϣ���е�ʶ����   
    static int i=0;//������ʾʱ��ѭ������
	final int HEIGHT=1000;   //���û�ͼ��Χ�߶�
    final int WIDTH=HEIGHT*45/32;    //��ͼ��Χ���
    final int X_OFFSET = 5;  //x�ᣨԭ�㣩��ʼλ��ƫ�ƻ�ͼ��Χһ�� 
    private int cx = WIDTH/10;  //ʵʱx������   
    int centerY = 1*HEIGHT /20;  //y���λ�� 
    private SurfaceHolder holder = null;    //��ͼʹ�ã����Կ���һ��SurfaceView
    private Paint paint = null;      //����
    SurfaceView surface = null;     //
    Timer timer = new Timer();       //һ��ʱ����ƵĶ������ڿ���ʵʱ��x�����꣬
    //ʹ�������������ʾ������ǰ����ɨ��
    TimerTask task = null;   //ʱ����ƶ����һ������            
    public String filename=""; //��������洢���ļ���
    BluetoothDevice _device = null;     //�����豸
    BluetoothSocket _socket = null;      //����ͨ��socket
    boolean _discoveryFinished = false;    
    boolean bRun = true;
    boolean bThread = false;	
    private BluetoothAdapter _bluetooth = BluetoothAdapter.getDefaultAdapter();    //��ȡ�����������������������豸
    Paint p = new Paint();
    int xtime=0;
    int lxtime=0;
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);   //���û���Ϊ������ main.xml        
               
        Button sin =(Button)findViewById(R.id.sin);         
        surface = (SurfaceView)findViewById(R.id.show);        
        //��ʼ��SurfaceHolder����
        holder = surface.getHolder();  
        holder.setFixedSize(WIDTH+5, HEIGHT+5);  //���û�����С��Ҫ��ʵ�ʵĻ�ͼλ�ô�һ�㣬�����ͻ�ͼ���򲢲�һ��
        paint = new Paint();  
		paint.setColor(Color.GREEN);  //�����ε���ɫ����ɫ�ģ��������������ɫ
        paint.setStrokeWidth(3);   

        
        //��Ӱ�ť������  ���TextView����
        holder.addCallback(new Callback() {  //��������ע�ͣ���ӻص�����
            public void surfaceChanged(SurfaceHolder holder,int format,int width,int height){ 
                drawBack(holder); 
                //���û����仰����ʹ���ڿ�ʼ���г���������Ļû�а�ɫ�Ļ�������
                //ֱ�����°�������Ϊ�ڰ������ж�drawBack(SurfaceHolder holder)�ĵ���
            } 
 
            public void surfaceCreated(SurfaceHolder holder) { 
                // TODO Auto-generated method stub 
            } 
 
            public void surfaceDestroyed(SurfaceHolder holder) { 
                // TODO Auto-generated method stub 
           
            }
        });  
                                                      
       //��Ӱ�ť������ ������ͼ�߳�
        sin.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				Intent intent = new Intent();
//				intent.setClass(BTClient.this, DataDisplay.class);
//				new DrawThread().start();  //�߳�����			
//				startActivity(intent);
 
			}        	
        }); 
        
       //����򿪱��������豸���ɹ�����ʾ��Ϣ����������
        if (_bluetooth == null){
        	Toast.makeText(this, "�޷����ֻ���������ȷ���ֻ��Ƿ����������ܣ�", Toast.LENGTH_LONG).show();
            finish();
            return;
        }        
        // �����豸���Ա�����  
       new Thread(){
    	   public void run(){
    		   if(_bluetooth.isEnabled()==false){
        		_bluetooth.enable();
    		   }
    	   }   	   
       }.start(); 
       
       
    }
             
    //���Ӱ�����Ӧ����
    public void onConnectButtonClicked(View v){ 
    	if(_bluetooth.isEnabled()==false){  //����������񲻿�������ʾ
    		Toast.makeText(this, " ��������...", Toast.LENGTH_LONG).show();
    		return;
    	}
    	    	
        //��δ�����豸���DeviceListActivity�����豸����
    	Button btn = (Button) findViewById(R.id.Button03);
    	
	    new DrawThread().start();  //�߳����� 
	    Log.i("drawthread","start");
	    
    	if(_socket==null){
    		Intent serverIntent = new Intent(this, DeviceListActivity.class); //��ת��������
    		startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);  //���÷��غ궨��
    	}
    	else{
    		 //�ر�����socket
    	    try{    	    	
    	    	is.close();
    	    	_socket.close();
    	    	_socket = null;
    	    	bRun = false;
    	    	btn.setText("����");
    	    }catch(IOException e){}   
    	}
    	return;
    }
        
    //���ջ�������ӦstartActivityForResult()
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	switch(requestCode){
    	case REQUEST_CONNECT_DEVICE:     //���ӽ������DeviceListActivity���÷���
    		// ��Ӧ���ؽ��
            if (resultCode == Activity.RESULT_OK) {   //���ӳɹ�����DeviceListActivity���÷���
                // MAC��ַ����DeviceListActivity���÷���
                String address = data.getExtras()
                                     .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                // �õ������豸���      
                _device = _bluetooth.getRemoteDevice(address); 
                // �÷���ŵõ�socket
                try{
                	_socket = _device.createRfcommSocketToServiceRecord(UUID.fromString(MY_UUID));
                }catch(IOException e){
                	Toast.makeText(this, "����ʧ�ܣ�", Toast.LENGTH_SHORT).show();
                }
                //����socket
            	Button btn = (Button) findViewById(R.id.Button03);
                try{
                	_socket.connect();
                	Toast.makeText(this, "����"+_device.getName()+"�ɹ���", Toast.LENGTH_SHORT).show();
                	btn.setText("�Ͽ�");
                }catch(IOException e){
                	try{
                		Toast.makeText(this, "����ʧ�ܣ�", Toast.LENGTH_SHORT).show();
                		_socket.close();
                		_socket = null;
                	}catch(IOException ee){
                		Toast.makeText(this, "����ʧ�ܣ�", Toast.LENGTH_SHORT).show();
                	}                	
                	return;
                }
                

				
                //�򿪽����߳�
                try{
            		is = _socket.getInputStream();   //�õ���������������
            		}catch(IOException e){
            			Toast.makeText(this, "��������ʧ�ܣ�", Toast.LENGTH_SHORT).show();
            			return;
            		}
            		if(bThread==false){
            			ReadThread.start();
            			bThread=true;
            		}else{
            			bRun = true;
            		}
            }
    		break;
    	default:break;
    	}
    }
    
 
    //���������߳�
    Thread ReadThread=new Thread(){
    	    	
    	public void run(){
    		int num = 0;
    		byte[] buffer = new byte[1024];
    		byte[] buffer_new = new byte[1024];
    		int i = 0;
    		int n = 0;
    		bRun = true;

    		//�����߳�
    		while(true){
    			try{
    				while(is.available()==0){
    					while(bRun == false){}
    				}
    				while(true){
    					num = is.read(buffer);         //��������
    					n=0;    					
    					String s0 = new String(buffer,0,num);
    					fmsg+=s0;    //�����յ�����
    					Log.i("fmsg",fmsg);
    					for(i=0;i<num;i++){
    						if((buffer[i] == 0x0d)&&(buffer[i+1]==0x0a)){
    							buffer_new[n] = 0x0a;
    							i++;
    						}else{
    							buffer_new[n] = buffer[i];
    						}
    						n++;
    					}
    					String s = new String(buffer_new,0,n);
    					
    					smsg+=s;   //д����ջ���
    					Log.i("smsg",smsg);
    					if(is.available()==0)break;  //��ʱ��û�����ݲ�����������ʾ
    				}
      	    		
    	    		}catch(IOException e){
    	    		}
    		}
    	}
    };
        
    public static int byteToInt(byte[] b){
     	  return (((int)b[0])+((int)b[1])*256);
     }
             
	//��ͼ�̣߳�ʵʱ��ȡtemp ��ֵ����yֵ
	public class DrawThread extends Thread {
		public void run() {
			// TODO Auto-generated method stub
			drawBack(holder);    //����������������
            if(task != null){ 
                task.cancel(); 
            } 
            task = new TimerTask() { //�½�����
                
                @Override 
                public void run() {                 	
                	//��ȡÿһ��ʵʱ��y����ֵ
                	//�����ע�ͣ����ǻ������Ҳ�                	
//                	String subString;
//                	subString=number.subString(i��i+1);//ֻ������2���ַ�                 	
//                	temp=Integer.parseInt(smsg);                	
//                	String str1=smsg.length()-2;                	
//                	char chs1=smsg.charAt(smsg.length()+i-31);
//                	char chs2=smsg.charAt(smsg.length()+i-30);
//                	char chs1[]=smsg.toCharArray();                	
//                	temp=((int)chs1[0])*256+(int)chs1[1];
                	char chs1=smsg.charAt(i);
                	char chs2=smsg.charAt(i+1);                	
                	temp=((int)chs1)*256+(int)chs2;
//                	temp=(int)chs1;
                	int cy = 4*HEIGHT/10 - temp; //ʵʱ��ȡ��temp��ֵ����Ϊ���ڻ�����˵
                	//�����Ͻ���ԭ�㣬������Ҫ��yֵ����Ҫ�ӻ����м俪ʼ����
                    Canvas canvas = holder.lockCanvas(new Rect(cx,cy-2,cx+2,cy+2)); 
                    //����������ֻ������Rect(cx,cy-2,cx+2,cy+2)����������ı䣬��С������
                    canvas.drawPoint(cx, cy, paint); //���
                    canvas.drawPoint(cx, cy+HEIGHT/2, paint); //���
                    canvas.drawPoint(cx+WIDTH/2, cy, paint); //���
                    canvas.drawPoint(cx+WIDTH/2, cy+HEIGHT/2, paint); //���
                    cx++;    //cx ������ ����������ʱ�����ͼ��   
                    if(i<(smsg.length()-3)){                    	
                    	i=i+2;
                    }else {
						i=0;
					}
                    holder.unlockCanvasAndPost(canvas);  //��������
                    
                    //��̬�ı�xʱ���������
                    if (xtime<=100){       //ÿ��1ms����xtime�ĵ�����ÿ��0.1s����lxtime�����������������
                    xtime++;
                    }else{
                    	xtime=0;
                    	lxtime++;
                    }                    
                    String lx1=String.valueOf(lxtime);
                    String lx2=String.valueOf(lxtime+1);
                    String lx3=String.valueOf(lxtime+2);
                                     
                    canvas.drawText(lx1,2*WIDTH/10+X_OFFSET,19*HEIGHT/20, p); //���������������
                    canvas.drawText(lx2,3*WIDTH/10+X_OFFSET,19*HEIGHT/20, p); //���������������       
                    canvas.drawText(lx3,4*WIDTH/10+X_OFFSET,19*HEIGHT/20, p); //���������������  
                    canvas.drawText("ʱ��/0.1s",5*WIDTH/10,19*HEIGHT/20, p); //���������������
                    canvas.drawText(lx1,7*WIDTH/10+X_OFFSET,19*HEIGHT/20, p); //���������������
                    canvas.drawText(lx2,8*WIDTH/10+X_OFFSET,19*HEIGHT/20, p); //���������������       
                    canvas.drawText(lx3,9*WIDTH/10+X_OFFSET,19*HEIGHT/20, p); //���������������  
                    canvas.drawText("ʱ��/0.1s",9*WIDTH/10-X_OFFSET,19*HEIGHT/20, p); //���������������                    
                    canvas.drawText(lx1,2*WIDTH/10+X_OFFSET,9*HEIGHT/20, p); //���������������
                    canvas.drawText(lx2,3*WIDTH/10+X_OFFSET,9*HEIGHT/20, p); //���������������       
                    canvas.drawText(lx3,4*WIDTH/10+X_OFFSET,9*HEIGHT/20, p); //���������������  
                    canvas.drawText("ʱ��/0.1s",5*WIDTH/10,9*HEIGHT/20, p); //���������������
                    canvas.drawText(lx1,7*WIDTH/10+X_OFFSET,9*HEIGHT/20, p); //���������������
                    canvas.drawText(lx2,8*WIDTH/10+X_OFFSET,9*HEIGHT/20, p); //���������������       
                    canvas.drawText(lx3,9*WIDTH/10+X_OFFSET,9*HEIGHT/20, p); //���������������  
                    canvas.drawText("ʱ��/0.1s",9*WIDTH/10-X_OFFSET,9*HEIGHT/20, p); //���������������  
                    
                    Log.i("lx1",lx1);
                    if(cx >=4*WIDTH/10){                       
                        cx=WIDTH/10;     //����������ͷ��ʼ��                   
                        drawBack(holder);  //����֮�����ԭ����ͼ�񣬴��¿�ʼ    
                    }                    
                } 
            }; 
            timer.schedule(task, 0,1); //��1ms��ִ��һ�θ�ѭ�����񻭳�ͼ�� 
            //��һ�����1ms����һ���㣬Ȼ��������ȥ 
            
		}	 
	}
	
    //���û�������ɫ������XY���λ��
    private void drawBack(SurfaceHolder holder){ 
        Canvas canvas= holder.lockCanvas(); //��������
        //���ư�ɫ���� 
        canvas.drawColor(Color.WHITE); 
        p.setColor(Color.BLACK); 
        p.setStrokeWidth(2);          
        //���������� 
       canvas.drawLine(X_OFFSET, HEIGHT*2/5, WIDTH, HEIGHT*2/5, p); //����X�� ǰ�ĸ�����������
       canvas.drawLine(WIDTH/10, X_OFFSET, WIDTH/10, HEIGHT, p); //����Y�� ǰ�ĸ���������ʼ����
       canvas.drawLine(X_OFFSET, HEIGHT*9/10, WIDTH, HEIGHT*9/10, p); //����X�� ǰ�ĸ�����������
       canvas.drawLine(WIDTH*3/5, X_OFFSET, WIDTH*3/5, HEIGHT, p); //����Y�� ǰ�ĸ���������ʼ����
       
       canvas.drawText("0",WIDTH/20,HEIGHT*4/10, p); //���������������
       canvas.drawText("100",WIDTH/20,HEIGHT*3/10, p); //���������������
       canvas.drawText("200",WIDTH/20,HEIGHT*2/10, p); //���������������
       canvas.drawText("300",WIDTH/20,HEIGHT*1/10, p); //���������������
       canvas.drawText("��ѹ/mV",WIDTH/20,HEIGHT*1/20, p); //���������������               
       canvas.drawText("0",WIDTH/20,HEIGHT*9/10, p); //���������������
       canvas.drawText("100",WIDTH/20,HEIGHT*8/10, p); //���������������
       canvas.drawText("200",WIDTH/20,HEIGHT*7/10, p); //���������������
       canvas.drawText("300",WIDTH/20,HEIGHT*6/10, p); //���������������
       canvas.drawText("��ѹ/mV",WIDTH/20,HEIGHT*10/20, p); //���������������    
       canvas.drawText("0",11*WIDTH/20,HEIGHT*4/10, p); //���������������
       canvas.drawText("100",11*WIDTH/20,HEIGHT*3/10, p); //���������������
       canvas.drawText("200",11*WIDTH/20,HEIGHT*2/10, p); //���������������
       canvas.drawText("300",11*WIDTH/20,HEIGHT*1/10, p); //���������������
       canvas.drawText("��ѹ/mV",11*WIDTH/20,HEIGHT*1/20, p); //���������������               
       canvas.drawText("0",11*WIDTH/20,HEIGHT*9/10, p); //���������������
       canvas.drawText("100",11*WIDTH/20,HEIGHT*8/10, p); //���������������
       canvas.drawText("200",11*WIDTH/20,HEIGHT*7/10, p); //���������������
       canvas.drawText("300",11*WIDTH/20,HEIGHT*6/10, p); //���������������
       canvas.drawText("��ѹ/mV",11*WIDTH/20,HEIGHT*10/20, p); //���������������                      
        holder.unlockCanvasAndPost(canvas);  //�������� ��ʾ����Ļ��
        holder.lockCanvas(new Rect(0,0,0,0)); //�����ֲ���������ط������ı�
        holder.unlockCanvasAndPost(canvas);          
    }
             
    //�رճ�����ô�����
    public void onDestroy(){
    	super.onDestroy();
    	if(_socket!=null)  //�ر�����socket
    	try{
    		_socket.close();
    	}catch(IOException e){}
    //	_bluetooth.disable();  //�ر���������
    }
            
    //���水����Ӧ����
    public void onSaveButtonClicked(View v){
    	Save();
    }
    
    //���������Ӧ����
    public void onClearButtonClicked(View v){
    	smsg="00";
    	fmsg="00";
//    	timer.cancel();
    	return;
    }
    
    //�˳�������Ӧ����
    public void onQuitButtonClicked(View v){
    	finish();
    }
    
    //���湦��ʵ��
	private void Save() {
		//��ʾ�Ի��������ļ���
		LayoutInflater factory = LayoutInflater.from(BTClient.this);  //ͼ��ģ�����������
		final View DialogView =  factory.inflate(R.layout.sname, null);  //��sname.xmlģ��������ͼģ��
		new AlertDialog.Builder(BTClient.this)
								.setTitle("�ļ���")
								.setView(DialogView)   //������ͼģ��
								.setPositiveButton("ȷ��",
								new DialogInterface.OnClickListener() //ȷ��������Ӧ����
								{
									public void onClick(DialogInterface dialog, int whichButton){
										EditText text1 = (EditText)DialogView.findViewById(R.id.sname);  //�õ��ļ����������
										filename = text1.getText().toString();  //�õ��ļ���
										
										try{
											if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){  //���SD����׼����
												
												filename =filename+".txt";   //���ļ���ĩβ����.txt										
												File sdCardDir = Environment.getExternalStorageDirectory();  //�õ�SD����Ŀ¼
												File BuildDir = new File(sdCardDir, "/data");   //��dataĿ¼���粻����������
												if(BuildDir.exists()==false)BuildDir.mkdirs();
												File saveFile =new File(BuildDir, filename);  //�½��ļ���������Ѵ������½��ĵ�
												FileOutputStream stream = new FileOutputStream(saveFile);  //���ļ�������
												stream.write(fmsg.getBytes());
												stream.close();
												Toast.makeText(BTClient.this, "�洢�ɹ���", Toast.LENGTH_SHORT).show();
											}else{
												Toast.makeText(BTClient.this, "û�д洢����", Toast.LENGTH_LONG).show();
											}
										
										}catch(IOException e){
											return;
										}																														
									}
								})
								.setNegativeButton("ȡ��",   //ȡ��������Ӧ����,ֱ���˳��Ի������κδ��� 
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int which) { 
									}
								}).show();  //��ʾ�Ի���
	} 
}