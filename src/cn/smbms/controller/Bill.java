package cn.smbms.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

//@Controller
public class Bill {
	@RequestMapping("/jsp/billquery")
	private String BillAdd(){
		return "/jsp/billquery";
	}
}
