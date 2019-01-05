package mvc.controller.common;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mvc.command.CommandHandler;

public class MainPageController implements CommandHandler{
	private static final String FORM_VIEW = "/WEB-INF/view/common/loginForm.jsp";
	
	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		if(req.getMethod().equalsIgnoreCase("GET")) {
			return processForm(req,res);		
		}else if(req.getMethod().equalsIgnoreCase("POST")) {
			return processSubmit(req, res); 
		}else {
			res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
		}
		return null;
	}
	private String processForm(HttpServletRequest req, HttpServletResponse res) {
		return "/WEB-INF/view/common/main.jsp";		
	}
	private String processSubmit(HttpServletRequest req,HttpServletResponse res) {			
		return "/WEB-INF/view/common/main.jsp";		
	}
}
