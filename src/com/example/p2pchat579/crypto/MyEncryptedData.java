package com.example.p2pchat579.crypto;

import java.io.Serializable;

public class MyEncryptedData implements Serializable {

		private byte[] message;
		private byte[] IV;
		
		public MyEncryptedData(byte[] data, byte[] iv) {
			this.message = data;
			this.IV = iv;
		}
		public  byte[] getData() {
			return message;
		}
		public byte[] getIV() {
			return IV;
		}
		
	
	
}
