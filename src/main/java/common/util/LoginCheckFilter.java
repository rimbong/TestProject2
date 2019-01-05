package common.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * <pre>
 * Login 여부를 session 정보를 가지고 검사한다
 * Login 하지 않은 상태면 로그인 화면으로 보낸다.
 * Avoid Url은 , 로 구분하면 세션정보를 검사하지 않는다.
 * </pre>
 * 
 * @author In-Seong Hwang
 * @since 2018.11.09
 * 
 */
public class LoginCheckFilter implements Filter {
	ArrayList<String> urlList = null;
	
	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		System.out.println("Login Session 정보를 검사합니다");
		HttpServletRequest req = (HttpServletRequest) request;
		String url = req.getServletPath();
		HttpSession session = req.getSession(false);
		
		if (urlList.contains(url)) {
			System.out.println("Avoid Url");
			chain.doFilter(request, response);
		} else {

			if ( session == null || session.getAttribute("authUser") == null ) {
				HttpServletResponse res = (HttpServletResponse) response;
				System.out.println("Login Session 정보 존재하지 않음");
				RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/view/common/loginForm.jsp");
				dispatcher.forward(request, response);
			}
			else {
				
				System.out.println("Login Session 정보 존재");
				chain.doFilter(request, response);
			}
		}
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		
		String urls = filterConfig.getInitParameter("avoidUrls");
		StringTokenizer token = new StringTokenizer(urls, ",");
		urlList = new ArrayList<>();
		while (token.hasMoreTokens()) {
			urlList.add(token.nextToken());
		}
	}

}
