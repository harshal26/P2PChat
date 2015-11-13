package com.example.p2pchat579;

public class MyConstants {
	public static final int CHOOSE_FILE_RESULT_CODE = 3; 
	
	// Constants that indicate the current connection state
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // listening for incoming connections
    public static final int STATE_CONNECTING = 2; //  initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  //  connected to a remote device
    
    
 // Intent request codes
    public static final int REQUEST_CONNECT_DEVICE = 0;
    public static final int REQUEST_ENABLE_BT = 1;
	
	
	
    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    
    
    
    //WRITE_MESSAGE TYPE
    public static final int MESSAGE_WRITE_TEXT = 1;
    public static final int MESSAGE_WRITE_PICTURE = 2;
    
    
}
