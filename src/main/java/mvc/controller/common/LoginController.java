package mvc.controller.common;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import common.exception.LoginFailException;
import mvc.command.CommandHandler;
import mvc.model.HBox;
import mvc.service.common.LoginService;
/**
 * <pre>
 * LoginController 로그인 요청을 받아서 서비스를 처리한다.
 * GET : loginform 을 보여준다.
 * POST : 조건에 따라 로그인 성공 또는 다시 loginform을 보여준다.
 * 성공 시 : authUser 세션과 비밀번호를 제외한 모든 것을 세션을 잡아준다.
 * </pre>
 * 
 * @author In-Seong Hwang
 * @since 2018.11.06	 * *            
 * 
 */
public class LoginController implements CommandHandler{
	
	private static final String FORM_VIEW = "/WEB-INF/view/common/loginForm.jsp";
	//private static final String FORM_VIEW = "/common/loginForm.jsp";
	private LoginService loginService = new LoginService();
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
	private String processForm(HttpServletRequest req , HttpServletResponse res) {
		System.out.println("login page move");
		return FORM_VIEW;
	}
	private String processSubmit(HttpServletRequest req , HttpServletResponse res) throws Exception {
		HBox hBox = (HBox)req.getAttribute("hBox");		
		
		HBox errors = new HBox();
		req.setAttribute("errors", errors);
		
		if(hBox.isEmpty("memberId")) {
			errors.set("memberId", Boolean.TRUE);
		}
		if(hBox.isEmpty("password")) {
			errors.set("password",Boolean.TRUE);
		}
		if(!errors.isEmpty()) {
			return FORM_VIEW; 
		}	
		try {
			HBox result = loginService.login(hBox);
			req.getSession().setAttribute("authUser", result);
			req.getSession().setAttribute("memberSeq", result.getString("memberSeq"));			
			req.getSession().setAttribute("memberId", result.getString("memberId"));
			req.getSession().setAttribute("name", result.getString("name"));
			req.getSession().setAttribute("regDate", result.getString("regDate"));
			return "/WEB-INF/view/common/main.jsp";
		} catch (LoginFailException e) {
			errors.set("idOrPwNotMatch",Boolean.TRUE);
			return FORM_VIEW;
		}
		
	}
}
