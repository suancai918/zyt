package cn.smbms.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import cn.smbms.pojo.Provider;
import cn.smbms.pojo.User;
import cn.smbms.service.provider.ProviderService;
import cn.smbms.service.provider.ProviderServiceImpl;
import cn.smbms.tools.Constants;

import com.mysql.jdbc.StringUtils;

@Controller
public class ProviderController {
	@Autowired
	private ProviderService providerService;
	public void setProviderService(ProviderService providerService) {
		this.providerService = providerService;
	}

	//查看供应商
	@RequestMapping("jsp/providerquery")
	private String query(@RequestParam(value="queryProName",required=false)String queryProName,
			@RequestParam(value="queryProCode",required=false)String queryProCode,
			HttpServletRequest request) {
		if(StringUtils.isNullOrEmpty(queryProName)){
			queryProName = "";
		}
		if(StringUtils.isNullOrEmpty(queryProCode)){
			queryProCode = "";
		}
		List<Provider> providerList = new ArrayList<Provider>();
		providerList = providerService.getProviderList(queryProName,queryProCode);
		request.setAttribute("providerList", providerList);
		request.setAttribute("queryProName", queryProName);
		request.setAttribute("queryProCode", queryProCode);
		return "/jsp/providerlist";
	}
	//添加供应商
	@RequestMapping("jsp/provideradd")
	private String add(HttpServletRequest request,MultipartFile proidpic, Provider provider){
		provider.setCreatedBy(((User)request.getSession().getAttribute(Constants.USER_SESSION)).getId());
		provider.setCreationDate(new Date());
		//获取用户上传的文件的文件名
		String fileName = proidpic.getOriginalFilename();
		//获取文件的后缀
		String suffix = fileName.substring(fileName.lastIndexOf("."));
		long size = proidpic.getSize(); //获取文件的大小（字节）
		 //在此处判断后缀是否符合要求
		Random ran= new Random();
		//使用随机数+当前时间毫秒数+后缀 生成新的文件名
		String newName = ran.nextInt(1000000)+""+System.currentTimeMillis()+suffix;
		//获取当前项目用于保存文件的文件夹的绝对路径
		String savePath = request.getServletContext().getRealPath("idpics");
		//根据保存路径和新文件名创建一个用于保存的文件对象
		File saveFile = new File(savePath,newName);
		try {
			proidpic.transferTo(saveFile);
			provider.setPidpic(newName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		boolean flag = false;
		flag = providerService.add(provider);
		if(flag){
			return "redirect:providerquery";
		}else{
			return "provideradd";
		}
	}
	//修改
	@RequestMapping(value="jsp/providermodify/{id}.html",method=RequestMethod.GET)
	private String getProviderById(@PathVariable("id")String id,HttpServletRequest request) {
		if(!StringUtils.isNullOrEmpty(id)){
			Provider provider = providerService.getProviderById(id);
			request.setAttribute("provider", provider);
			return "jsp/providermodify";
		}
			return "redirect:providerquery";
	}
	//修改
	@RequestMapping(value="jsp/providermodify",method=RequestMethod.POST)
	private String getProviderById(Provider provider,HttpServletRequest request) {
		provider.setModifyBy(((User)request.getSession().getAttribute(Constants.USER_SESSION)).getId());
		provider.setModifyDate(new Date());
		boolean flag = false;
		flag = providerService.modify(provider);
		if(flag){
			return "redirect:providerquery";
		}else{
			return "jsp/providermodify";
		}
	}
}
