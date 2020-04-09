package com.linln.admin.system.controller;

import sun.misc.IOUtils;

import java.net.Socket;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
public class MsgThread extends Thread{
	
	private Socket sc;
		
	public MsgThread(Socket sc) {
	this.sc = sc;
		this.start();
	}
	 @Override
	 public void run() {
			try {

					BufferedReader br = new BufferedReader(new InputStreamReader(sc.getInputStream()));
					String msg = br.readLine();
					System.out.println(msg);


			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally{
				try {
					sc.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
	 }

}
