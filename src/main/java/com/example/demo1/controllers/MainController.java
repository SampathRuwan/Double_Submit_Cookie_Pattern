package com.example.demo1.controllers;

import java.util.HashMap;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo1.models.CookieModel;
import com.example.demo1.models.User;
import com.example.demo1.services.*;

@Controller
public class MainController {
	private MainService serviceObj = new MainService();
	HashMap<String,String> cookieStore = new HashMap<String,String>();  

	@GetMapping("/")
	public String home(){
		return "login.html";
	}
	
	@PostMapping("/userCreds")
	public String submit(@ModelAttribute("User") User user, BindingResult result,
			HttpServletResponse response){
		
		if(user.getUserName().equals("admin") && user.getUserPwd().equals("admin")){
			
			//Generate random value for session cookie
			String ssId = serviceObj.generateRandomValue();
			
			Cookie c1 = new Cookie("ssId",ssId);
			c1.setMaxAge(600*600); //1 hour
			c1.setHttpOnly(true);
			c1.setSecure(false);	
			response.addCookie(c1);
			cookieStore.put("ssId", ssId);
			
			//Generate random value for csrf
			String csrf_value = serviceObj.generateRandomValue();
			//store CSRF in the browser
			Cookie c2 = new Cookie("csrf",csrf_value);
			c2.setMaxAge(600*600); //1 hour
			c2.setSecure(false);	
			c2.setHttpOnly(false);
			response.addCookie(c2);	
			
			return "redirect:data.html";
		}else
		
		return "redirect:errorPage.html";
	}
	
	@PostMapping("/submitAmount")
	public ResponseEntity<String> userDetails(@ModelAttribute("CookieModel") CookieModel cModel, BindingResult result,
			HttpServletResponse response, HttpServletRequest request){
		
		String res_c1 = null, res_csrf = null;
		//get cookies from request
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("ssId")) {
					//get session cookie
					res_c1 = cookie.getValue();
				} else if (cookie.getName().equals("csrf")) {
					//get csrf cookie
					res_csrf = cookie.getValue();
				}
			}
		}
	
		//check session cookie and csrf cookie and display status
		if (res_c1.equals(cookieStore.get("ssId")) && cModel.getCsrfToken().equals(res_csrf)) {
			return ResponseEntity.status(HttpStatus.OK).body("Tranaction Success !!");
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("error");
		}
		
	}
	
}
