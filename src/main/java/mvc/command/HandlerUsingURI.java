package mvc.command;

import java.io.FileReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import mvc.model.HBox;
/**
 * <pre>
 * Handler  매핑시킬 객체를 분기하기위한 최초 Handler 이다.
 * commandHandlerURL.properties 파일을 로드한후 대응하는 핸들러 객체를 미리 만들어놓는다.
 * 이후 commandHandlerMap 에 각각 key & value로 매핑 시킨다.
 * </pre>
 * 
 * @author In-Seong Hwang
 * @since 2018.11.06	 *
 *            
 * 
 */
public class HandlerUsingURI extends HttpServlet {	

	private static final long serialVersionUID = 1L;
	
	// <커맨드, 핸들러인스턴스> 매핑 정보 저장
    private Map<String, CommandHandler> commandHandlerMap = 
    		new HashMap<>();
    
    public void init() throws ServletException {
    	System.out.println("[1] commandHandlerURL.properties file 로드");
        //[1] commandHandlerURL.properties 파일을 로드한다.
        String configFile = getInitParameter("configFile");
        Properties prop = new Properties();
        String configFilePath = getServletContext().getRealPath(configFile);
        try (FileReader fis = new FileReader(configFilePath)) {
            prop.load(fis); 
        } catch (IOException e) {
            throw new ServletException(e);
        }
        //[2] 로드된 프로퍼티 파일에서 key 값과 그에 대응하는 value값을 가져온다.
        Iterator<Object> keyIter = prop.keySet().iterator();
        while (keyIter.hasNext()) {
            String command = (String) keyIter.next();
            String handlerClassName = prop.getProperty(command);
            try {
            	//[3] value에 해당하는 즉 URL에 해당하는 클래스를 미리 객체화시킨다.
                Class<?> handlerClass = Class.forName(handlerClassName);
                CommandHandler handlerInstance = 
                        (CommandHandler) handlerClass.newInstance();
             //[4] command = prolperites의 key 값 --- handlerInstance =  value에 해당하는 객체
                commandHandlerMap.put(command, handlerInstance); 
               
            } catch (ClassNotFoundException | InstantiationException 
            		| IllegalAccessException e) {
                throw new ServletException(e);
            }
        }
        System.out.println("[2] 핸들러 객체 생성 완료 ");
    }
    //[4] 요청이 들어오면 doGet 또는 doPost를 실행하게 된다.
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	interceptor(request,response);
        process(request, response);
    }

    protected void doPost(HttpServletRequest request,
    HttpServletResponse response) throws ServletException, IOException {
    	interceptor(request,response);
        process(request, response);
    }
    /**
     * <pre>
     *	spring의 interceptor 역할을 하는 메소드 
     *	각 파리미터와 요청 URI값들을 저장하며 세션역시 저장한다.
     * </pre> 
     * @author In-Seong Hwang
     * @since 2018.11.11  
     * 
     */
	private void interceptor(HttpServletRequest request, HttpServletResponse response) {
		HBox hBox = new HBox();
		String uri = request.getRequestURI();
		Map<String, String[]> requestMap = request.getParameterMap();
		Iterator<String> it = requestMap.keySet().iterator();

		// [1] Request 객체로 부터 Prameter 정보를 hBox로 셋팅함.
		while (it.hasNext()) {
			try {
				String key = (String) it.next();
				if ((requestMap.get(key) instanceof String[])) {
					String[] values = (String[]) requestMap.get(key);

					if (values != null && values.length == 1) {
						hBox.set(key, values[0]);
					} else {
						hBox.set(key, values);
					}
				} else {
					hBox.set(key, requestMap.get(key));
				}
			} catch (Exception ex) {
			}
		}
		// [2] Header 정보를 읽어와 hBox에 설정함.
		Enumeration<String> headerNames = request.getHeaderNames();
		HBox header = new HBox();
		while (headerNames.hasMoreElements()) {
			String headerName = (String) headerNames.nextElement();
			header.set(headerName, request.getHeader(headerName));
		}
		// [3] 필요정보 hBox객체에 저장함
		hBox.set("remoteHost", request.getRemoteHost()); // Client Host
		hBox.set("remoteAddr", request.getRemoteAddr()); // Client Host IP
		hBox.set("remoteURI", uri); // Client 요청 URI
		hBox.set("remoteMethod", request.getMethod()); // Client 요청 Method (GET, POST)

		// [4] session에  저장할것들 저장함
		HttpSession session = request.getSession(false);
		
		if (session != null) {			
			hBox.setIfEmpty("authUser", session.getAttribute("authUser"));
			hBox.setIfEmpty("memberSeq", session.getAttribute("memberSeq"));
			hBox.setIfEmpty("memberId", session.getAttribute("memberId"));			
			hBox.setIfEmpty("name", session.getAttribute("name"));
			hBox.setIfEmpty("regDate", session.getAttribute("regDate"));			
		}
		request.setAttribute("hBox", hBox);
	}
	/**
	 * <pre>
	 * 요청 URI에 매핑되는 핸들러 객체의 process를 수행하여 적절한 viewPage를 가져온다.
	 * 이후 해당 페이지로 forwading 시킨다 
	 * </pre>
	 * 
	 * @author In-Seong Hwang
	 * @since 2018.11.06	 *
	 *            
	 * @return 
	 */
    private void process(HttpServletRequest request,
    			HttpServletResponse response) throws ServletException, IOException {    	
		String command = request.getRequestURI();   
		if (command.indexOf(request.getContextPath()) == 0) {
			command = command.substring(request.getContextPath().length()); // /xxx.do			
		}
        CommandHandler handler = commandHandlerMap.get(command);
        if (handler == null) {
            handler = new NullHandler();
        }
        String viewPage = null;
        // [*] 객체화 된 핸들러의 process를 호출한후 보여줄 viewPage를 받아온다.
        try {
            viewPage = handler.process(request, response);
            System.out.println("viewPage:"+viewPage);
        } catch (Throwable e) {
            throw new ServletException(e);
        }
        if (viewPage != null) {
	        RequestDispatcher dispatcher = request.getRequestDispatcher(viewPage);
	        dispatcher.forward(request, response);
        }
    }
}
