package mvc.service.common;

import java.sql.Connection;
import java.sql.SQLException;

import common.dao.CommonDao;
import common.exception.DuplicateIdException;
import common.jdbc.JdbcUtil;
import common.jdbc.connection.ConnectionProvider;
import mvc.model.HBox;
/**
 * <pre>
 * 회원가입 서비스
 * </pre>
 * 
 * @author In-Seong Hwang
 * @since 2018.11.06            
 *
 */
public class JoinService {
	
	private CommonDao commonDao = CommonDao.getInstance();
	
	/**
	 * <pre>
	 * hBox를  받아 DB에 정보가 있는지 확인하고 이후에 없다면
	 * 넣어준다
	 * </pre>
	 * 
	 * @author In-Seong Hwang
	 * @since 2018.11.06            
	 * @param HBox
	 * 
	 */	
	public void join(HBox hBox) {
		Connection conn = null;
		HBox result = null;
		try {
			conn = ConnectionProvider.getConnection();
			conn.setAutoCommit(false);
			
			result = commonDao.selectById(conn, hBox);
			if(result!=null) {
				JdbcUtil.rollback(conn);
				throw new DuplicateIdException();
			}
			commonDao.insert(conn, hBox);
			conn.commit();
		}catch (SQLException e) {			
		}
	}	
	/**
	 * <pre>
	 * errors에 각각 key값에 대해 비어있는지 체크유무	 * 
	 * </pre>
	 * 
	 * @author In-Seong Hwang
	 * @since 2018.11.06
	 * @param errors : HBox가 비어있는지 관한 true false
	 *   hBox : key 대응하는 값을 체크하기위한 객체 
	 * 
	 */	
	public void validate(HBox errors,HBox hBox) {
		checkEmpty(errors, hBox,"memberId");
		checkEmpty(errors, hBox,"name");
		checkEmpty(errors, hBox,"password");
		checkEmpty(errors, hBox,"confirmPassword");
		if(!errors.containsKey("confirmPassword")) {
			if(!isPasswordEqualToConfirm(hBox)) {
				errors.set("notMatch", Boolean.TRUE);
			}
		}		
	}
	/**
	 * <pre>
	 * key에 해당하는 value가 없을 경우 errors객체안에 key에 대한 true를 등록해준다.  
	 * </pre>
	 * 
	 * @author In-Seong Hwang
	 * @since 2018.11.06
	 * @param hBox : key 에 대응하는 value가 존재한느지 체크
	 * 		errors key에 대응하는 valu가 비어있을 경우 true를 넣어준다.	 
	 * 
	 */
	private void checkEmpty(HBox errors,HBox hBox,String key) {
		if(hBox.isEmpty(key)) {
			errors.set(key, Boolean.TRUE);
		}		
	}	
	/**
	 * <pre>
	 * 입력한 2개의 패스워드가 동일한지 체크 ( AJAX를 통해 구현 예정)  
	 * </pre>
	 * 
	 * @author In-Seong Hwang
	 * @since 2018.11.06
	 * @param hBox : 패스워드 체크할 객체
	 * @return true : paswword가 null 이아니며 2개의 패스워드가 동일
	 * 
	 */
	public boolean isPasswordEqualToConfirm(HBox hBox) {
		return hBox.getString("password") != null && hBox.getString("confirmPassword").equals(hBox.getString("password"));
	}
	
}
