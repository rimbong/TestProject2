package mvc.service.common;

import java.sql.Connection;
import java.sql.SQLException;

import common.dao.CommonDao;
import common.exception.InvalidPasswordException;
import common.exception.MemberNotFoundException;
import common.jdbc.JdbcUtil;
import common.jdbc.connection.ConnectionProvider;
import mvc.model.HBox;
/**
 * <pre>
 * HBox를 받아 curPassword와  DB와 정보를 받은 password 확인후 일치한다면 
 * 비밀번호를 newPassword로 바꿔준다.  
 * </pre>
 * 
 * @author In-Seong Hwang
 * @since 2018.11.10
 * @param hBox : 입력 받은 정보가 들어있다
 * 			result : DB 내부에 있는 정보가 들어있다.
 */
public class ChangePasswordService {
	private CommonDao commonDao = CommonDao.getInstance();
	private LoginService loginService = new LoginService();
	public void changePassword(HBox hBox) {
		Connection conn=null;
		try {
			conn = ConnectionProvider.getConnection();
			conn.setAutoCommit(false);		
			HBox result = commonDao.selectById(conn, hBox);
			//[1] password  < -  curPassword 로 파싱
			hBox.set("password",hBox.getString("curPassword"));
			if(result == null) {
				throw new MemberNotFoundException();
			}
			if(!loginService.matchPassword(hBox, result)) {
				throw new InvalidPasswordException();
			}
			//[2] password  < -  newPassword 로 파싱
			hBox.set("password",hBox.getString("newPassword"));
			commonDao.update(conn, hBox);			
			conn.commit();
		}catch (SQLException e) {
			JdbcUtil.rollback(conn);
			throw new RuntimeException(e);
		}finally {
			JdbcUtil.close(conn);
		}
	}
}
