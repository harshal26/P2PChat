package com.example.p2pchat579.activity;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.AlgorithmParameters;

import javax.crypto.spec.IvParameterSpec;

import com.example.p2pchat579.MyConstants;
import com.example.p2pchat579.R;
import com.example.p2pchat579.Util;
import com.example.p2pchat579.R.id;
import com.example.p2pchat579.R.layout;
import com.example.p2pchat579.R.menu;
import com.example.p2pchat579.crypto.Encryption;
import com.example.p2pchat579.crypto.MyEncryptedData;
import com.example.p2pchat579.service.ChattingService;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChattingActivity extends Activity {
	
	BluetoothAdapter bluetoothAdapter;
	Context context;
	
	
	private static final String TAG = "BluetoothChat";
    private static final boolean D = true;


    
 // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
	
	//object for chat service
	private ChattingService mChatService = null;
	
	
	
	 // Layout Views
    private TextView mTitle;
    private ListView mConversationView;
    private EditText mOutEditText;
    private Button mSendButton,photoButton;

	
	
	
	// Name of the connected device
    private String mConnectedDeviceName = null;
    // Array adapter for the conversation thread
    private ArrayAdapter<String> mConversationArrayAdapter;
    // String buffer for outgoing messages
    private StringBuffer mOutStringBuffer;
  
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chatactivity);
		//get default adapter
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		
		//check availability for bluetooth
		 if (bluetoothAdapter == null) {
	            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
	            finish();
	            return;
	        }
		 
		 mTitle = (TextView)findViewById(R.id.title);
		 
		
	}
	
	
	public void onStart() {
		super.onStart();
       
        // If BT is not on, request for it to be enabled.       
        if (!bluetoothAdapter.isEnabled()) {
           enableBluetooth();       
        } 
        	
        	//setup the chat session
        	if (mChatService == null) 
        	setupChat();
        
    }
	
	
	@Override
    public synchronized void onResume() {
        super.onResume();
        if(D) Log.e(TAG, "+ ON RESUME +");
      
        
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == MyConstants.STATE_NONE) {
              // Start the Bluetooth chat services
              mChatService.start();
            }
        }
    }
	
	
	 @Override
	    public void onDestroy() {
	        super.onDestroy();
	        // Stop the Bluetooth chat services
	        if (mChatService != null) mChatService.stop();
	        if(D) Log.e(TAG, "--- ON DESTROY ---");
	    }
	
	
	//request for enabling bluetooth
	public void enableBluetooth()
	{
			Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, MyConstants.REQUEST_ENABLE_BT );			
	}
	
	
	
	 private void ensureDiscoverable() {
	        if(D) Log.d(TAG, "ensure discoverable");
	        if (bluetoothAdapter.getScanMode() !=
	            BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
	            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
	            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
	            startActivity(discoverableIntent);
	        }
	    }

	
	 
	 /**
	     * Sends a message.
	     * @param message  A string of text to send.
	     */
	    private void sendMessage(String message) {
	        // Check that we're actually connected before trying anything
	        if (mChatService.getState() != MyConstants.STATE_CONNECTED) {
	            Toast.makeText(this, "Not connected...", Toast.LENGTH_SHORT).show();
	            return;
	        }
	        // Check that there's actually something to send
	        if (message.length() > 0) {
	            // Get the message bytes and tell the BluetoothChatService to write
	            byte[] send = message.getBytes();
	            
	            //encryption
	            Encryption e = new Encryption("My Secret");
	            MyEncryptedData data=null;
	            try {
					data = e.encrypt(send);
					Log.d("Normal Data",new String(send));
					Log.d("Encrypted data:", new String(data.getData()));
					IvParameterSpec spec = new IvParameterSpec(data.getIV());
					Log.d("Decrypted Data",new String(e.decrypt(data.getData(),spec)));
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	            try {
					mChatService.write(Util.serialize(data), MyConstants.MESSAGE_WRITE_TEXT);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					Log.d("Exception in serializing",e1.toString());
				}
	            //mChatService.write(data.getData(),BluetoothChat.MESSAGE_WRITE_TEXT);
	            // Reset out string buffer to zero and clear the edit text field
	            mOutStringBuffer.setLength(0);
	            mOutEditText.setText(mOutStringBuffer);
	        }
	    }
	    
	    /**
	     * Sends a Image File.
	     * @param url  A path of file to send.
	     */
	    public void sendImage(String url)
	    {
	    	 if (mChatService.getState() != MyConstants.STATE_CONNECTED) {
		            Toast.makeText(this, "Not connected...", Toast.LENGTH_SHORT).show();
		            return;
		        }
	    	try {
	    		 //mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	    		 Bitmap bm = BitmapFactory.decodeFile(url);
	    		 Log.d("Bitmap",bm.toString());
	    		 ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    		 bm.compress(Bitmap.CompressFormat.JPEG, 100,baos); //bm is the bitmap object
	    		 baos.flush();
	    		 byte[] send = baos.toByteArray();
	    		 Toast.makeText(getBaseContext(), "Length:"+String.valueOf(send.length), Toast.LENGTH_SHORT).show();
	    		 mChatService.write(send,MyConstants.MESSAGE_WRITE_PICTURE);
	    		 } catch (Exception e) {
	    		 Log.d("Exception in sending image",e.toString());
	    		 
	    		 }
	    }
	    
	    
	 
	 
	    
	    // The action listener for the EditText widget, to listen for the return key
	    private TextView.OnEditorActionListener mWriteListener =
	        new TextView.OnEditorActionListener() {
	        public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
	            // If the action is a key-up event on the return key, send the message
	            if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
	                String message = view.getText().toString();
	                sendMessage(message);
	            }
	            if(D) Log.i(TAG, "END onEditorAction");
	            return true;
	        }
	    };
	    
	    
	    
	    
	
	//setting up a chat
	    private void setupChat() {
	        Log.d(TAG, "setupChat()");
	        // Initialize the array adapter for the conversation thread
	        mConversationArrayAdapter = new ArrayAdapter<String>(this, R.layout.message);
	        mConversationView = (ListView) findViewById(R.id.in);
	        mConversationView.setAdapter(mConversationArrayAdapter);
	        // Initialize the compose field with a listener for the return key
	        mOutEditText = (EditText) findViewById(R.id.edit_text_out);
	        mOutEditText.setOnEditorActionListener(mWriteListener);
	        // Initialize the send button with a listener that for click events
	        mSendButton = (Button) findViewById(R.id.button_send);
	        mSendButton.setOnClickListener(new OnClickListener() {
	            public void onClick(View v) {
	                // Send a message using content of the edit text widget
	                TextView view = (TextView) findViewById(R.id.edit_text_out);
	                String message = view.getText().toString();
	                sendMessage(message);
	            }
	        });
	        // Initialize the BluetoothChatService to perform bluetooth connections
	        mChatService = new ChattingService(this, mHandler);
	        // Initialize the buffer for outgoing messages
	        mOutStringBuffer = new StringBuffer("");
	        photoButton = (Button)findViewById(R.id.photoButton);
	        
	        photoButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					//sendImage();
					
					 // Allow user to pick an image from Gallery or other
                    // registered apps
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    startActivityForResult(intent, MyConstants.CHOOSE_FILE_RESULT_CODE);
				}
			});
	     
	       
	    }
	    
	    
	    public void showPeers()
	    {
	    	 Intent serverIntent = new Intent(this, ListAllDeviceActivity.class);
	           startActivityForResult(serverIntent, MyConstants.REQUEST_CONNECT_DEVICE);
	    }
	    
	    
	    
	    // The Handler that gets information back from the BluetoothChatService
	    private final Handler mHandler = new Handler() {
	        @Override
	        public void handleMessage(Message msg) {
	            switch (msg.what) {
	            case MyConstants.MESSAGE_STATE_CHANGE:
	                if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
	                switch (msg.arg1) {
	                case MyConstants.STATE_CONNECTED:
	                	
	                    mTitle.setText("Connected To  ");
	                    mTitle.append(mConnectedDeviceName);
	                    mConversationArrayAdapter.clear();
	                    break;
	                case MyConstants.STATE_CONNECTING:
	                    mTitle.setText("Connecting...");
	                    break;
	                case MyConstants.STATE_LISTEN:
	                case MyConstants.STATE_NONE:
	                    mTitle.setText("Not Connected");
	                    break;
	                }
	                break;
	            case MyConstants.MESSAGE_WRITE:
	                byte[] writeBuf = (byte[]) msg.obj;
	                // construct a string from the buffer
	                if(msg.arg2 == MyConstants.MESSAGE_WRITE_TEXT)
	                {
	                	MyEncryptedData data=null;
	                try {
						data = (MyEncryptedData)Util.deserialize(writeBuf);
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						Log.d("Exception in deserializing", e.toString());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						Log.d("Exception in deserializing", e.toString());
					} 
	                String writeMessage = new String(data.getData());
	                mConversationArrayAdapter.add("Me:  " + writeMessage);
	                }
	                else if(msg.arg2 == MyConstants.MESSAGE_WRITE_PICTURE)
	                {
	                	 mConversationArrayAdapter.add("Me:  Image Sent" );
	                }
	                else
	                	 mConversationArrayAdapter.add("Me:  Nothing Got" );
	                break;
	            case MyConstants.MESSAGE_READ:
	                byte[] readBuf = (byte[]) msg.obj;
	                Log.d("RECEIVED BUFFER LENGTH",""+readBuf.length);
	               // construct a string from the valid bytes in the buffer
	                int type = Util.fromByteArray(new byte[]{readBuf[0],readBuf[1],readBuf[2],readBuf[3]});
	               if(type == MyConstants.MESSAGE_WRITE_TEXT)
	               {
	               String readMessage = new String(readBuf,8,msg.arg1);
	               MyEncryptedData data;
	               String finalMessage="default";
	               try {
					data = (MyEncryptedData)Util.deserialize(readMessage.getBytes());
					IvParameterSpec spec = new IvParameterSpec(data.getIV());
					finalMessage = new String(new Encryption("My Secret").decrypt(data.getData(),spec));
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					Log.d("Exception in deserializing",e.toString());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Log.d("Exception in deserializing",e.toString());
				}catch(Exception e)
				{
					Log.d("Exception from My Secret",e.toString());
					e.printStackTrace();
				}
	               Log.d("Received bytes length", ""+msg.arg1);
	               mConversationArrayAdapter.add(mConnectedDeviceName+":  " + finalMessage);
	                
	               }
	               else  if(type == MyConstants.MESSAGE_WRITE_PICTURE)
	               {
	            	   //byte[] imageBuffer = new byte[1024*1024*8];
	                   int pos = 0;
	            	   File dir= new File("sdcard/MyChat");
	            	   dir.mkdir();
	            	   final File f = new File(dir.getAbsolutePath(),"chat-" + System.currentTimeMillis()
	            			   + ".jpeg");

	            	   Log.d("Received bytes length", ""+msg.arg1);

	            	   try {
	            		   f.createNewFile();
	            	   } catch (IOException e) {
	            		   // TODO Auto-generated catch block
	            		   e.printStackTrace();
	            	   }
	            	   try {
	            		   Util.copyFile(readBuf,8,msg.arg1, new FileOutputStream(f));
	            		   mConversationArrayAdapter.add(mConnectedDeviceName+": Image Received");
	            	   } catch (FileNotFoundException e) {
	            		   // TODO Auto-generated catch block
	            		   e.printStackTrace();
	            	   }

	            	   Log.d("BlueTooth", "Bluetooth : copying files " + f.toString());
	               }
	               //Log.d("BUFFER LENGTH",""+readBuf.length);
	              //  Bitmap bmp = BitmapFactory.decodeByteArray(readBuf, 0, msg.arg1);
	               // mConversationArrayAdapter.add(mConnectedDeviceName+":  IMAGE GOT" +bmp.toString());
	                
	                break;
	            case MyConstants.MESSAGE_DEVICE_NAME:
	                // save the connected device's name
	                mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
	                Toast.makeText(getApplicationContext(), "Connected to "
	                               + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
	                
	               
	                break;
	            case MyConstants.MESSAGE_TOAST:
	                Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
	                               Toast.LENGTH_SHORT).show();
	                break;
	            }
	        }
	    };
	    
	    
	    
	    @Override
	    public void onActivityResult(int requestCode, int resultCode, Intent data) {
	        Log.d(TAG, "onActivityResult " + resultCode);
	        switch (requestCode) {
	        case MyConstants.REQUEST_CONNECT_DEVICE:
	            // When DeviceListActivity returns with a device to connect
	            if (resultCode == Activity.RESULT_OK) {
	                // Get the device MAC address
	                String address = data.getExtras()
	                                     .getString(ListAllDeviceActivity.EXTRA_DEVICE_ADDRESS);
	                // Get the BLuetoothDevice object
	                BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
	                // Attempt to connect to the device
	                mChatService.connect(device);
	            }
	            break;
	        case MyConstants.REQUEST_ENABLE_BT:
	            // When the request to enable Bluetooth returns
	            if (resultCode == Activity.RESULT_OK) {
	                // Bluetooth is now enabled, so set up a chat session
	                setupChat();
	              
	            } else {
	                // User did not enable Bluetooth or an error occured
	                Log.d(TAG, "BT not enabled");
	                Toast.makeText(this, "Bluetooth cannot be enabled.", Toast.LENGTH_SHORT).show();
	                finish();
	            }
	            break;
	            
	            
	        case MyConstants.CHOOSE_FILE_RESULT_CODE:
	        	Log.d("In third case","SELECT IMAGE");
	        	 if (resultCode == Activity.RESULT_OK) {
	        	Uri uri = data.getData(); //data would be uri of selected file
	        	Log.d("URL",uri.toString());
	        	sendImage(Util.getRealPathFromURI(uri,this));
	        	 }
	        	 else {
		                // User did not enable Bluetooth or an error occured
		                Log.d(TAG, "File cannot be selected");
		                Toast.makeText(this, "Error in selecting Image", Toast.LENGTH_SHORT).show();
		                finish();
		            }
		            
	        }
	    }

	
	 @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	        MenuInflater inflater = getMenuInflater();
	        inflater.inflate(R.menu.main_menu, menu);
	        return true;
	    }
	    @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	        switch (item.getItemId()) {
	        case R.id.scan:
	            // Launch the DeviceListActivity to see devices and do scan
	            Intent serverIntent = new Intent(this, ListAllDeviceActivity.class);
	            startActivityForResult(serverIntent, MyConstants.REQUEST_CONNECT_DEVICE);
	            return true;
	        case R.id.discoverable:
	            // Ensure this device is discoverable by others
	            ensureDiscoverable();
	            return true;
	        }
	        return false;
	    }
}
