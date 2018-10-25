package cn.smbms.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.smbms.tools.Constants;

@Controller
public class UserController {
	@RequestMapping("jsp/frame")
	public String doLogin(){
		return "/jsp/frame";
	}
	@RequestMapping("/login")
	public String doLoginout(){
		return "/login";
	}
	//@RequestMapping("jsp/userquery")
	public String add(){
		return "/jsp/userquery";
	}
	//@RequestMapping("jsp/userquery")
	public String modify(){
		return "/jsp/userquery";
	}
}
