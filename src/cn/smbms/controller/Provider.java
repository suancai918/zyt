package cn.smbms.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class Provider {

	@RequestMapping("providerquery")
	public String doLogin(){
		return "/jsp/providerquery";
	}
}
