package com.example.p2pchat579.crypto;

import java.nio.charset.Charset;
import java.security.AlgorithmParameters;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import android.util.Base64;

public class Encryption {

	private final static String SECURITY_ALGO = "AES";
	private String secret; 
	
	
	public Encryption(String secret){
		this.secret = secret;
	}
	
	
	
	
	private MyEncryptedData encryptData(byte[] key, byte[] data) throws Exception {
	    SecretKeySpec skeySpec = new SecretKeySpec(key,SECURITY_ALGO);
	    Cipher mCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	   
	    mCipher.init(Cipher.ENCRYPT_MODE, skeySpec);
	    byte[] encrypteddata = mCipher.doFinal(data);
	    
	    //get the initial vector
	    AlgorithmParameters parameters = mCipher.getParameters();
	    byte[] iv = parameters.getParameterSpec(IvParameterSpec.class).getIV();
	    
	    //set IV with data and return
	    return new MyEncryptedData(encrypteddata, iv);
	}

	private byte[] decryptData(byte[] key, byte[] encrypted, IvParameterSpec iv) throws Exception {
	    SecretKeySpec skeySpec = new SecretKeySpec(key, SECURITY_ALGO);
	    Cipher mCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	    mCipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
	    byte[] decryptedData = mCipher.doFinal(encrypted);
	    return decryptedData;
	}
	
	private byte[] generateKey() throws Exception{
	
		byte[] keyStart = secret.getBytes("utf-8");
		KeyGenerator gen = KeyGenerator.getInstance(SECURITY_ALGO);		
		SecureRandom secRandom = SecureRandom.getInstance("SHA1PRNG", "Crypto");
		secRandom.setSeed(keyStart);
		gen.init(128, secRandom); //keysize = 128 for AES
		SecretKey secretkey = gen.generateKey();
		byte[] key = secretkey.getEncoded();
		return key;		
	}
	
	
	public MyEncryptedData encrypt(byte[] data) throws Exception{
		return encryptData(generateKey(),data);
	}
	public byte[] decrypt(byte[] encryptedData, IvParameterSpec iv) throws Exception{
		return decryptData(generateKey(),encryptedData, iv);
	}
}
