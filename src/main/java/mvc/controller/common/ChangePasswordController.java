package mvc.controller.common;

import static org.hamcrest.CoreMatchers.instanceOf;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import common.exception.InvalidPasswordException;
import common.exception.MemberNotFoundException;
import mvc.command.CommandHandler;
import mvc.model.HBox;
import mvc.service.common.ChangePasswordService;

/**
 * <pre>
 * session 영역 authUser의 memberId를 parm으로 받은후 hBox에 저장한다
 * curPassword 와 newPassword를 hBox에 저장시켜준다.
 * 비밀번호를 newPassword로 바꿔준다.  
 * </pre>
 * 
 * @author In-Seong Hwang
 * @since 2018.11.10
 * @param hBox : 입력 받은 정보가 들어있다
 * 			result : DB 내부에 있는 정보가 들어있다.
 */

@SuppressWarnings("unused")
public class ChangePasswordController implements CommandHandler {
	private static final long serialVersionUID = 1L;
	private static final String FORM_VIEW="/WEB-INF/view/common/changePwdForm.jsp";   
	private ChangePasswordService changePasswordService = new ChangePasswordService();
	
	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		if(req.getMethod().equalsIgnoreCase("GET")) {
			return processForm(req,res);
		}else if(req.getMethod().equalsIgnoreCase("POST")) {
			return processSubmit(req, res);
		}else {
			res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
			return null;
		}	
	}
	private String processForm(HttpServletRequest req, HttpServletResponse res) {
		return FORM_VIEW;
	}	
	private String processSubmit(HttpServletRequest req , HttpServletResponse res) throws ServletException, IOException {
		HBox parm = (HBox) req.getSession().getAttribute("authUser");	
		HBox hBox = (HBox)req.getAttribute("hBox");
		hBox.set("memberId",parm.getString("memberId"));
		
		HBox errors = new HBox();
		req.setAttribute("errors", errors);
		if(hBox.getString("curPassword") == null || hBox.isEmpty("curPassword")){
			errors.set("curPassword",Boolean.TRUE);
		}
		if(hBox.getString("newPassword") == null || hBox.isEmpty("newPassword")){
			errors.set("newPassword",Boolean.TRUE);
		}
		if(!errors.isEmpty()) {
			return FORM_VIEW;
		}
		try {					
			changePasswordService.changePassword(hBox);			
			return "/WEB-INF/view/common/changePwdSuccess.jsp";			
		}catch (InvalidPasswordException e) {
			errors.set("badCurPwd",Boolean.TRUE);
			return FORM_VIEW;			
		}catch(MemberNotFoundException e) {
			res.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return null;
		}		
	}

}
