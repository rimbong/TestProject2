package common.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import common.jdbc.JdbcUtil;
import mvc.model.HBox;
/**
 * <pre> 
 * User에 대한 CRUD를 구현한 DAO
 * </pre>
 * 
 * @author In-Seong Hwang
 * @since 2018.11.06 
 * @return CommonDao 인스턴스
 */
public class CommonDao {
	
	/**
	 * <pre>
	 * 쓰레드 세이프한 싱글톤 패턴
	 * 클래스안에 클래스(Holder)를 두어 JVM의 Class loader 매커니즘과 Class가 로드되는 시점을 이용하는 방법
	 * </pre>
	 * 
	 * @author In-Seong Hwang
	 * @since 2018.11.06 
	 * @return CommonDao 인스턴스
	 */
	private CommonDao() {}
	private static class CommonDaoHolder{
		public static final CommonDao instance = new CommonDao();
	}
	public static CommonDao getInstance() {
		return CommonDaoHolder.instance;
	}

	/**
	 * <pre>
	 * MEMBERID를 검색하여 HBox 형태로 리턴
	 * </pre>
	 * 
	 * @author In-Seong Hwang
	 * @since 2018.11.06        
	 * @return HBox
	 */
	
	public HBox selectById(Connection conn, HBox hBox) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {			
			pstmt = conn.prepareStatement(
					"select * from TB_MEMBER where MEMBER_ID = ?");
			pstmt.setString(1, hBox.getString("memberId"));
			rs = pstmt.executeQuery();
			HBox result= null;
			if (rs.next()) {
				result = new HBox();
				result.set("memberSeq", rs.getString("MEMBER_SEQ"));
				result.set("memberId", rs.getString("MEMBER_ID"));
				result.set("name",rs.getString("MEMBER_NAME"));
				result.set("password",rs.getString("MEMBER_PWD"));
				result.set("regDate",toDate(rs.getDate("REG_DATE")));	
			}
			
			return result;
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}
	private Date toDate(Date date) {
		return date == null ? null : new Date(date.getTime());
	}
	/**
	 * <pre>
	 * HBox를 받아 DB에 넣어주는 로직
	 * </pre>
	 * 
	 * @author In-Seong Hwang
	 * @since 2018.11.06	          
	 * @return 
	 */	
	public void insert(Connection conn, HBox hBox) throws SQLException {
		try (PreparedStatement pstmt = 
				conn.prepareStatement("INSERT INTO TB_MEMBER VALUES(NULL,?,?,?,now())")) {
			pstmt.setString(1, hBox.getString("password"));	
			pstmt.setString(2, hBox.getString("memberId"));
			pstmt.setString(3, hBox.getString("name"));
					
			pstmt.executeUpdate();
		}
	}
	/**
	 * <pre>
	 * HBox를 받아 DB를 update 해주는 로직 
	 * </pre>
	 * 
	 * @author In-Seong Hwang
	 * @since 2018.11.06	          
	 * @return 
	 */
	public void update(Connection conn,HBox hBox) throws SQLException{
		try (PreparedStatement pstmt = conn.prepareStatement(
				"UPDATE TB_MEMBER SET MEMBER_PWD=? WHERE MEMBER_ID=?")){
			pstmt.setString(1, hBox.getString("password"));
			pstmt.setString(2, hBox.getString("memberId"));	
			pstmt.executeUpdate();
		}
	}
	

}
