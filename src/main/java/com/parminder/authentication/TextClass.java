package com.parminder.authentication;

import java.util.Date;

public class TextClass {

	
	public static void main(String[] args) {
		Date d = new Date();
		Date d1 = new Date(d.getTime());
		System.out.println(d+" "+d1);
		System.out.println(d==d);
	}
}
