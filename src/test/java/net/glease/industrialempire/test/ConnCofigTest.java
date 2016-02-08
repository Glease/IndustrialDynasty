package net.glease.industrialempire.test;

public class ConnCofigTest {
	public static void main(String[] args) {
		for (byte j = 1; j <8;j++) {
			long l = 0;
			if(j==4)
				continue;
			for(int i = 0; i< 20;i++){
				l += ((long)j) << i*3;
			}
			System.out.print("0x");
			System.out.print(Long.toHexString(l));
			System.out.print("l,  //");
			System.out.println(Long.toBinaryString(j));
		}
		System.out.println(Long.toHexString(1l<<62));
	}
}
