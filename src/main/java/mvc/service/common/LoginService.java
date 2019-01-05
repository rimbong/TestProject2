package mvc.service.common;

import java.sql.Connection;
import java.sql.SQLException;

import common.dao.CommonDao;
import common.exception.LoginFailException;
import common.jdbc.connection.ConnectionProvider;
import mvc.model.HBox;
/**
 * <pre>
 * 로그인 서비스
 * </pre>
 * 
 * @author In-Seong Hwang
 * @since 2018.11.06            
 *
 */
public class LoginService {
	private CommonDao commonDao = CommonDao.getInstance();
	/**
	 * <pre>
	 * LoginService 로직  HBox를 받아 DB를 selct하여 존재할경우 HBox를 리턴한다
	 * </pre>
	 * 
	 * @author In-Seong Hwang
	 * @since 2018.11.08
	 * @param HBox : 각종 로그인 정보를 담고있는 객체
	 *            
	 * @return result : DB 데이터를 담고 있다.
	 */
	public HBox login(HBox hBox) {
		HBox result=null;
		try(Connection conn = ConnectionProvider.getConnection()) {
			result = commonDao.selectById(conn, hBox);
			if(result == null) {
				throw new LoginFailException();				
			}
			if(!matchPassword(hBox, result)) {
				throw new LoginFailException();
			}			
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return result;
	}
	public boolean matchPassword(HBox hBox,HBox result) {
		return hBox.getString("password").equals(result.getString("password"));
	}
	
}
