package cn.smbms.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import cn.smbms.pojo.Bill;
import cn.smbms.pojo.Provider;
import cn.smbms.pojo.User;
import cn.smbms.service.bill.BillService;
import cn.smbms.service.bill.BillServiceImpl;
import cn.smbms.service.provider.ProviderService;
import cn.smbms.service.provider.ProviderServiceImpl;
import cn.smbms.tools.Constants;

import com.mysql.jdbc.StringUtils;

@Controller
public class Billcontroller {
	@Autowired
	private ProviderService providerService;
	@Autowired
	private BillService billService;
	
	public void setBillService(BillService billService) {
		this.billService = billService;
	}
	public void setProviderService(ProviderService providerService) {
		this.providerService = providerService;
	}
	//查看订单 
	@RequestMapping("jsp/billquery")
	private String BillQuery(@RequestParam(value="queryProductName",required=false)String queryProductName,
			@RequestParam(value="queryProviderId",required=false)String queryProviderId,
			@RequestParam(value="queryIsPayment",required=false)String queryIsPayment,
			HttpServletRequest request){
		List<Provider> providerList = new ArrayList<Provider>();
		providerList = providerService.getProviderList("","");
		request.setAttribute("providerList", providerList);
		if(StringUtils.isNullOrEmpty(queryProductName)){
			queryProductName = "";
		}
		List<Bill> billList = new ArrayList<Bill>();
		Bill bill = new Bill();
		if(StringUtils.isNullOrEmpty(queryIsPayment)){
			bill.setIsPayment(0);
		}else{
			bill.setIsPayment(Integer.parseInt(queryIsPayment));
		}
		
		if(StringUtils.isNullOrEmpty(queryProviderId)){
			bill.setProviderId(0);
		}else{
			bill.setProviderId(Integer.parseInt(queryProviderId));
		}
		bill.setProductName(queryProductName);
		billList = billService.getBillList(bill);
		request.setAttribute("billList", billList);
		request.setAttribute("queryProductName", queryProductName);
		request.setAttribute("queryProviderId", queryProviderId);
		request.setAttribute("queryIsPayment", queryIsPayment);
		return "jsp/billlist";
	}
	//添加订单
	@RequestMapping("jsp/billadd")
	private String BillAdd(Bill bill,HttpServletRequest request){
		bill.setCreatedBy(((User)request.getSession().getAttribute(Constants.USER_SESSION)).getId());
		bill.setCreationDate(new Date());
		boolean flag = false;
		flag = billService.add(bill);
		System.out.println("add flag -- > " + flag);
		if(flag){
			return "redirect:/jsp/billquery";
		}else{
			return "jsp/billadd";
		}
	}
	//修改订单GET方法
	@RequestMapping(value="jsp/billmodify/{id}.html",method=RequestMethod.GET)
	private String billmodify(@PathVariable("id")String id,HttpServletRequest request){
		if(!StringUtils.isNullOrEmpty(id)){
			Bill bill = billService.getBillById(id);
			request.setAttribute("bill", bill);
			return "jsp/billmodify";
		}
		return "redirect:/jsp/billquery";
	}
	//修改订单post方法
	@RequestMapping(value="jsp/billmodify",method=RequestMethod.POST)
	private String billmodify(Bill bill,HttpServletRequest request){
		System.out.println("modify===============");
		bill.setModifyBy(((User)request.getSession().getAttribute(Constants.USER_SESSION)).getId());
		bill.setModifyDate(new Date());
		boolean flag = false;
		flag = billService.modify(bill);
		if(flag){
			return "redirect:/jsp/billquery";
		}else{
			return "jsp/billmodify";
		}
	}
}
