package com.example.demo1.services;

import java.util.Random;

public class MainService {

	public String generateRandomValue(){
		Random r = new Random();
        StringBuilder sb = new StringBuilder();
        while (sb.length() < 30) {
            sb.append(Integer.toHexString(r.nextInt()));
        }

        return sb.toString().substring(0, 30);
	}
}
