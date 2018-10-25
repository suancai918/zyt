package cn.smbms.controller;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONArray;
import com.mysql.jdbc.StringUtils;

import cn.smbms.pojo.Role;
import cn.smbms.pojo.User;
import cn.smbms.service.role.RoleService;
import cn.smbms.service.role.RoleServiceImpl;
import cn.smbms.service.user.UserService;
import cn.smbms.service.user.UserServiceImpl;
import cn.smbms.tools.Constants;
import cn.smbms.tools.PageSupport;

@Controller
public class LoginController {
	@Autowired
	private RoleService roleService;
	@Autowired
	private UserService userService;
	public void setRoleService(RoleService roleService) {
		this.roleService = roleService;
	}
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	//注销
	@RequestMapping("jsp/logout.do")
	public String doLoginout(HttpServletRequest request){
		request.getSession().removeAttribute(Constants.USER_SESSION);
		//response.sendRedirect(request.getContextPath()+"/login.jsp");
		return "redirect:/login";
	}
	//登录
	@RequestMapping(value="/login.do",method=RequestMethod.POST)
	public String doLogin(String userCode,String userPassword,HttpServletRequest request) throws Exception{
		System.out.println("login ============ " );
		//调用service方法，进行用户匹配
		User user = userService.login(userCode,userPassword);
		if(null != user){//登录成功
			//放入session
			request.getSession().setAttribute(Constants.USER_SESSION, user);
			//页面跳转（frame.jsp）
			//response.sendRedirect("jsp/frame.jsp");
			return "redirect:jsp/frame";
		}else{
			//页面跳转（login.jsp）带出提示信息--转发
			//request.setAttribute("error", "用户名或密码不正确");
			//request.getRequestDispatcher("login.jsp").forward(request, response);
			//return "login";
			throw new Exception("用户名密码不正确");
		}
	}
	@RequestMapping("jsp/userquery")
	public String UserQuery(@RequestParam(value="queryname",required=false)String queryUserName,
			@RequestParam(value="queryUserRole",required=false)String temp,
			@RequestParam(value="pageIndex",required=false)String pageIndex,
			HttpServletRequest request){
		//查询用户列表
		int queryUserRole = 0;
		List<User> userList = null;
		//设置页面容量
    	int pageSize = Constants.pageSize;
    	//当前页码
    	int currentPageNo = 1;
		System.out.println("queryUserName servlet--------"+queryUserName);  
		System.out.println("queryUserRole servlet--------"+queryUserRole);  
		System.out.println("query pageIndex--------- > " + pageIndex);
		if(queryUserName == null){
			queryUserName = "";
		}
		if(temp != null && !temp.equals("")){
			queryUserRole = Integer.parseInt(temp);
		}
		
    	if(pageIndex != null){
    		currentPageNo = Integer.valueOf(pageIndex);
    	}
    	//总数量（表）	
    	int totalCount	= userService.getUserCount(queryUserName,queryUserRole);
    	//总页数
    	PageSupport pages=new PageSupport();
    	pages.setCurrentPageNo(currentPageNo);
    	pages.setPageSize(pageSize);
    	pages.setTotalCount(totalCount);
    	int totalPageCount = pages.getTotalPageCount();
    	//控制首页和尾页
    	if(currentPageNo < 1){ 
    		currentPageNo = 1;
    	}else if(currentPageNo > totalPageCount){
    		currentPageNo = totalPageCount;
    	}
		userList = userService.getUserList(queryUserName,queryUserRole,currentPageNo, pageSize);
		request.setAttribute("userList", userList);
		List<Role> roleList = null;
		roleList = roleService.getRoleList();
		request.setAttribute("roleList", roleList);
		request.setAttribute("queryUserName", queryUserName);
		request.setAttribute("queryUserRole", queryUserRole);
		request.setAttribute("totalPageCount", totalPageCount);
		request.setAttribute("totalCount", totalCount);
		request.setAttribute("currentPageNo", currentPageNo);
		return "jsp/userlist";
	}
	//添加
	@RequestMapping(value="jsp/useradd",method=RequestMethod.POST)
	public String UserAdd(User user,MultipartFile useridpic,HttpServletRequest request) {
		System.out.println("add()================");
		user.setCreationDate(new Date());
		user.setCreatedBy(((User)request.getSession().getAttribute(Constants.USER_SESSION)).getId());
		//获取用户上传的文件的文件名
		String fileName = useridpic.getOriginalFilename();
		//获取文件的后缀
		String suffix = fileName.substring(fileName.lastIndexOf("."));
		long size = useridpic.getSize(); //获取文件的大小（字节）
		//在此处判断后缀是否符合要求
		Random ran = new Random();
		//使用随机数+当前时间毫秒数+后缀 生成新的文件名
		String newName = ran.nextInt(1000000)+""+System.currentTimeMillis()
						+ suffix;
		//获取当前项目用于保存文件的文件夹的绝对路径
		String savePath = request.getServletContext().getRealPath("idpics");
		//根据保存路径和新文件名创建一个用于保存的文件对象
		File saveFile = new File(savePath,newName);
		//保存文件
		try {
			useridpic.transferTo(saveFile);
			user.setIdpic(newName);
		} catch (Exception e) {
		// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(userService.add(user)){
			return "redirect:userquery";
		}else{
			return "useradd";
		}
	}
	//修改
	@RequestMapping(value="jsp/usermodify/{id}.html",method=RequestMethod.GET)
	public String usermodify(@PathVariable("id")String id,HttpServletRequest request){
		 if(!StringUtils.isNullOrEmpty(id)){
			//调用后台方法得到user对象			
			User user = userService.getUserById(id);
			request.setAttribute("user", user);
			return "jsp/usermodify";
		}
		 return "redirect:userquery";
	}
	//修改
	@RequestMapping(value="jsp/usermodify",method=RequestMethod.POST)
	public String usermodify(User user,HttpServletRequest request) {
		user.setModifyBy(((User)request.getSession().getAttribute(Constants.USER_SESSION)).getId());
		user.setModifyDate(new Date());
		if(userService.modify(user)){
			 return "redirect:userquery";
		}else{
			return "jsp/usermodify";
		}
	}
	//局部异常处理
	/*@ExceptionHandler//当该类中Controller方法出现异常时,调用此方法处理
	public String exception(Exception ex,HttpServletRequest request){
		request.setAttribute("error", ex.getMessage());
		return "error";
	}*/
	
	@RequestMapping(value="/jsp/getrolelist",method=RequestMethod.POST)
	@ResponseBody
	public Object getRoleList(){
		return roleService.getRoleList();	
	}
}
