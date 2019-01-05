package common.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.ibatis.session.SqlSession;

import common.jdbc.JdbcUtil;
import mvc.model.HBox;
/**
 * <pre> 
 * Article에 대한 CRUD를 구현한 DAO
 * </pre>
 * 
 * @author In-Seong Hwang
 * @since 2018.11.13 * 
 */
import mvc.model.HBoxList;

public class ArticleDao {
	/**
	 * <pre>
	 * 쓰레드 세이프한 싱글톤 패턴
	 * 클래스안에 클래스(Holder)를 두어 JVM의 Class loader 매커니즘과 Class가 로드되는 시점을 이용하는 방법
	 * </pre>
	 * 
	 * @author In-Seong Hwang
	 * @since 2018.11.13
	 * @return ArticleDao 인스턴스
	 */
	private ArticleDao() {
	}

	private static class ArticleDaoHolder {
		public static final ArticleDao instance = new ArticleDao();
	}

	public static ArticleDao getInstance() {
		return ArticleDaoHolder.instance;
	}

	/**
	 * <pre>
	 *  
	 * 게시글의 sequence와 Id ,name,title을 넣는다 이후 넣은 HBox를 다시 리턴한다.
	 * </pre>
	 * 
	 * @author In-Seong Hwang
	 * @since 2018.11.13
	 * @return HBox
	 */
	public HBox insert(Connection conn, HBox hBox) throws SQLException {
		PreparedStatement pstmt = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement(
					"	INSERT " + "	INTO TB_BD " + "	VALUES " + "	(" + "		NULL," + "		?," + "		?,"
							+ "		?," + "		?," + "		now()," + "		now()," + "		0," + "		0" + "	)");
			pstmt.setInt(1, hBox.getInt("memberSeq"));
			pstmt.setString(2, hBox.getString("memberId"));
			pstmt.setString(3, hBox.getString("name"));
			pstmt.setString(4, hBox.getString("bdTitle"));
			int insertCnt = pstmt.executeUpdate();
			if (insertCnt > 0) {
				stmt = conn.createStatement();
				rs = stmt.executeQuery("SELECT LAST_INSERT_ID() FROM TB_BD");
				if (rs.next()) {
					Integer bdSeq = rs.getInt(1);
					hBox.set("bdSeq", bdSeq);
					return hBox;
				}
			}
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(stmt);
			JdbcUtil.close(pstmt);
		}
		return null;
	}

	/**
	 * <pre>
	 *  
	 * 게시글의 content를 넣는다. 이후 HBox를 리턴하게 된다.
	 * </pre>
	 * 
	 * @author In-Seong Hwang
	 * @since 2018.11.13
	 * @return HBox
	 */
	public HBox insertContent(Connection conn, HBox hBox) throws SQLException {
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement("	INSERT " + "	INTO TB_BD_CONTENT " + "	VALUES (?,?)");
			pstmt.setString(1, hBox.getString("bdSeq"));
			pstmt.setString(2, hBox.getString("bdContent"));
			int insertedCnt = pstmt.executeUpdate();
			if (insertedCnt > 0) {
				return hBox;
			} else {
				return null;
			}

		} finally {
			JdbcUtil.close(pstmt);
		}
	}

	/**
	 * <pre>
	 *  
	 * 페이징을 위한 게시글의 개수 읽기
	 * </pre>
	 * 
	 * @author In-Seong Hwang
	 * @since 2018.11.15
	 * @return 게시글 개수
	 */
	public int selectCount(Connection conn) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement("	SELECT " + "	COUNT(*) " + "	FROM TB_BD");
			rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getInt(1);
			}
			return 0;
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}

	}

	/**
	 * <pre>
	 *  
	 * 게시글 리스트 뽑기
	 * </pre>
	 * 
	 * @author In-Seong Hwang
	 * @since 2018.11.15
	 * @param startRow : 시작 번호 (limit걸기위한것) rowSize : 리스트의 개수 전부 HBox에 담는다.
	 * @return HBoxList<HBox>
	 */
	public HBoxList<HBox> selectList(SqlSession sqlsession, HBox hBox) {
		
		List<Object> list = sqlsession.selectList("mybatis.article.selectList", hBox); 
		return new HBoxList<HBox>(list);
		
/*		PreparedStatement pstmt = null;
		ResultSet rs = null;
		HBoxList<HBox> result = null;
		try {
			result = new HBoxList<HBox>();
			pstmt = conn
					.prepareStatement("	SELECT * " + "	FROM TB_BD " + "	ORDER BY BD_SEQ DESC " + "	LIMIT ? , ?");
			pstmt.setInt(1, hBox.getInt("startRow"));
			pstmt.setInt(2, hBox.getInt("rowSize"));
			rs = pstmt.executeQuery();
			while (rs.next()) {
				HBox hBoxElement = new HBox();
				hBoxElement.set("bdSeq", rs.getInt("BD_SEQ"));
				hBoxElement.set("memberSeq", rs.getInt("MEMBER_SEQ"));
				hBoxElement.set("writerId", rs.getString("WRITER_ID"));
				hBoxElement.set("writerName", rs.getString("WRITER_NAME"));
				hBoxElement.set("bdTitle", rs.getString("BD_TITLE"));
				hBoxElement.set("regDate", rs.getString("REG_DATE"));
				hBoxElement.set("modDate", rs.getString("MOD_DATE"));
				hBoxElement.set("readCnt", rs.getString("READ_CNT"));
				hBoxElement.set("hitCnt", rs.getString("HIT_CNT"));
				result.set(hBoxElement);
			}
			return result;
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}*/
	}

	/**
	 * <pre>
	 *  
	 * 게시글 조회
	 * </pre>
	 * 
	 * @author In-Seong Hwang
	 * @since 2018.11.15
	 * @param HBox를 통해 게시글 SEQ 조회
	 * @return 하나의 게시글'정보'를 담은 HBox
	 */
	public HBox selectById(Connection conn, HBox hBox) throws SQLException {
		HBox hBoxElement = new HBox();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement("	SELECT * " + "	FROM TB_BD " + "	WHERE BD_SEQ = ?");
			pstmt.setInt(1, hBox.getInt("bdSeq"));
			rs = pstmt.executeQuery();
			if (rs.next()) {
				hBoxElement.set("bdSeq", rs.getInt("BD_SEQ"));
				hBoxElement.set("memberSeq", rs.getInt("MEMBER_SEQ"));
				hBoxElement.set("writerId", rs.getString("WRITER_ID"));
				hBoxElement.set("writerName", rs.getString("WRITER_NAME"));
				hBoxElement.set("bdTitle", rs.getString("BD_TITLE"));
				hBoxElement.set("regDate", rs.getString("REG_DATE"));
				hBoxElement.set("modDate", rs.getString("MOD_DATE"));
				hBoxElement.set("readCnt", rs.getString("READ_CNT"));
				hBoxElement.set("hitCnt", rs.getString("HIT_CNT"));
			}

			return hBoxElement;
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}

	/**
	 * <pre>
	 *  
	 * 게시글 조회수 증가
	 * </pre>
	 * 
	 * @author In-Seong Hwang
	 * @since 2018.11.15
	 * @param HBox를 통해 게시글 SEQ 조회
	 * @return 하나의 게시글'정보'를 담은 HBox
	 */
	public void increaseReadCount(Connection conn, HBox hBox) throws SQLException {
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement("	UPDATE tb_bd " + "	SET READ_CNT=READ_CNT+1 " + "	WHERE BD_SEQ = ?");
			pstmt.setInt(1, hBox.getInt("bdSeq"));
			int cnt = pstmt.executeUpdate();
			if (cnt < 0) {
				System.out.println("업데이트 실패");
			}
		} finally {
			JdbcUtil.close(pstmt);
		}
	}

	/**
	 * <pre>
	 *  
	 * 게시글 조회
	 * </pre>
	 * 
	 * @author In-Seong Hwang
	 * @since 2018.11.15
	 * @param HBox를 통해 게시글 SEQ 조회
	 * @return 하나의 게시글'내용'을 담은 HBox
	 */
	public HBox selectByIdContent(Connection conn, HBox hBox) throws SQLException {
		HBox hBoxElement = new HBox();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement("" + "	SELECT * " + "	FROM TB_BD_CONTENT " + "	WHERE BD_SEQ = ?");
			pstmt.setInt(1, hBox.getInt("bdSeq"));
			rs = pstmt.executeQuery();
			if (rs.next()) {
				hBoxElement.set("bdSeq", rs.getString("BD_SEQ"));
				hBoxElement.set("bdContent", rs.getString("BD_CONTENT"));
			}
			return hBoxElement;
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}

	/**
	 * <pre>
	 * 	 *  
	 * 게시글에 대한 댓글리스트 읽어오기
	 * </pre>
	 * 
	 * @author In-Seong Hwang
	 * @since 2018.11.17
	 * @param bdSeq를 담은 HBox
	 * @return 댓글리스트
	 */
	public HBoxList<HBox> selectArticleReplyList(Connection conn, HBox hBox) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		HBoxList<HBox> result = null;
		try {
			result = new HBoxList<>();
			pstmt = conn.prepareStatement("	SELECT BD_SEQ, " + "		REPL_SEQ, "
					+ "		MEMBER_NAME AS REPL_WRITER, " + "		REPL_DELE, " + "		REPL_MEMO, "
					+ "		DATE_FORMAT(REPL_DATE,'%Y-%m-%d %H:%i') AS REPL_DATE, " + "		REPL_PRT, "
					+ "		REPL_DEPT, " + "		REPL_ODR, " + "		RP.MEMBER_SEQ "
					+ "	FROM TB_REPL AS RP INNER JOIN TB_MEMBER AS MM " + "	ON RP.MEMBER_SEQ = MM.MEMBER_SEQ "
					+ "	WHERE BD_SEQ = ? " + "	AND REPL_DELE = 'N' " + "	ORDER BY REPL_ODR");
			pstmt.setInt(1, hBox.getInt("bdSeq"));
			rs = pstmt.executeQuery();
			while (rs.next()) {
				HBox hBoxElement = new HBox();
				hBoxElement.set("bdSeq", rs.getInt("BD_SEQ"));
				hBoxElement.set("replSeq", rs.getInt("REPL_SEQ"));
				hBoxElement.set("replWriter", rs.getString("REPL_WRITER"));
				hBoxElement.set("replDele", rs.getString("REPL_DELE"));
				hBoxElement.set("replMemo", rs.getString("REPL_MEMO"));
				hBoxElement.set("replDate", rs.getString("REPL_DATE"));
				hBoxElement.set("replPrt", rs.getInt("REPL_PRT"));
				hBoxElement.set("replDept", rs.getInt("REPL_DEPT"));
				hBoxElement.set("replOrder", rs.getInt("REPL_ODR"));
				hBoxElement.set("replMemberSeq", rs.getInt("RP.MEMBER_SEQ"));
				// hBoxElement.set("replMemo",UtilString.text2Html(hBoxElement.getString("replMemo")));
				result.set(hBoxElement);
			}
			return result;
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}

	/**
	 * <pre>
	 *  
	 * 댓글의 순서를 개편하기 위해 MaxOrder + 1 값을 구한다.
	 * </pre>
	 * 
	 * @author In-Seong Hwang
	 * @since 2018.11.15
	 * @param 댓글 정보가 담긴 HBox
	 * @return MaxReplOrder + 1 값
	 */
	@SuppressWarnings("null")
	public Integer selectArticleReplyMaxOrder(Connection conn, HBox hBox) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Integer maxorder = null;
		try {
			pstmt = conn.prepareStatement(
					"	SELECT IFNULL(MAX(REPL_ODR),0) + 1 " + "	FROM TB_REPL " + "	WHERE BD_SEQ = ?");
			pstmt.setInt(1, hBox.getInt("bdSeq"));
			rs = pstmt.executeQuery();

			if (rs.next()) {
				maxorder = rs.getInt(1);
			}

			return maxorder;
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}

	/**
	 * <pre>
	 *  
	 * 댓글 SEQ의 최댓값을 판변해서 +1 더한 값을 리턴한다.
	 * </pre>
	 * 
	 * @author In-Seong Hwang
	 * @since 2018.11.15
	 * @param 댓글 정보가 담긴 HBox
	 * @return MaxReplSEQ + 1 값
	 */
	public Integer selectArticleReplyMaxReplSeq(Connection conn, HBox hBox) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Integer maxReplSeq = null;
		try {
			pstmt = conn.prepareStatement("	SELECT IFNULL(MAX(REPL_SEQ),0) + 1 " + "	FROM TB_REPL ");
			rs = pstmt.executeQuery();
			if (rs.next()) {
				maxReplSeq = rs.getInt(1);
			}
			return maxReplSeq;
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}

	/**
	 * <pre>
	 *  
	 * 댓글을 담는다
	 * </pre>
	 * 
	 * @author In-Seong Hwang
	 * @since 2018.11.15
	 * @param 댓글 정보가 담긴 HBox
	 * @return MaxReplOrder + 1 값
	 */
	public int insertArticleReply(Connection conn, HBox hBox) throws SQLException {
		PreparedStatement pstmt = null;
		int cnt = 0;
		try {
			pstmt = conn.prepareStatement("	INSERT " + "	INTO TB_REPL " + "	(" + "		REPL_SEQ, "
					+ "		BD_SEQ, " + "		MEMBER_SEQ, " + "		REPL_MEMO, " + "		REPL_DATE, "
					+ "		REPL_DELE, " + "		REPL_PRT, " + "		REPL_DEPT, " + "		REPL_ODR" + "	)"
					+ "	VALUES" + "	(" + "		?," + "		?," + "		?," + "		?," + "		now()," + "		'N',"
					+ "		?," + "		?," + "		?" + "	)");

			pstmt.setInt(1, hBox.getInt("replSeq"));
			pstmt.setInt(2, hBox.getInt("bdSeq"));
			pstmt.setInt(3, hBox.getInt("memberSeq"));
			pstmt.setString(4, hBox.getString("replMemo"));

			if (hBox.getInt("replPrt") == 0) {

				pstmt.setInt(5, hBox.getInt("replSeq"));
				pstmt.setInt(6, 0);
			} else {
				pstmt.setInt(5, hBox.getInt("replPrt"));
				pstmt.setInt(6, hBox.getInt("replDept"));
			}

			pstmt.setInt(7, hBox.getInt("replOrder"));
			cnt = pstmt.executeUpdate();

			return cnt;
		} finally {
			JdbcUtil.close(pstmt);
		}
	}

	/**
	 * <pre>
	 *  
	 * 댓글SEQ을 이용해 댓글 하나 선택해온다
	 * </pre>
	 * 
	 * @author In-Seong Hwang
	 * @since 2018.11.17
	 * @param 댓글 SEQ가 있는 HBox
	 * @return
	 */
	public HBox selectArticleReplyOne(Connection conn, HBox hBox) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		HBox hBoxElement = null;
		try {
			pstmt = conn.prepareStatement(
					"SELECT REPL_SEQ, " + "		BD_SEQ, " + "		MEMBER_NAME AS REPL_WRITER, " + "		REPL_DELE, "
							+ "		REPL_MEMO, " + "		DATE_FORMAT(REPL_DATE,'%Y-%m-%d %H:%i') AS REPL_DATE, "
							+ "		REPL_PRT, " + "		REPL_DEPT, " + "		REPL_ODR, " + "		RP.MEMBER_SEQ "
							+ "FROM TB_REPL RP INNER JOIN TB_MEMBER MM " + "ON RP.MEMBER_SEQ = MM.MEMBER_SEQ "
							+ "WHERE REPL_DELE='N' AND REPL_SEQ=?");
			pstmt.setInt(1, hBox.getInt("replSeq"));
			rs = pstmt.executeQuery();
			if (rs.next()) {
				hBoxElement = new HBox();
				hBoxElement.set("replSeq", rs.getInt("REPL_SEQ"));
				hBoxElement.set("bdSeq", rs.getInt("BD_SEQ"));
				hBoxElement.set("replWriter", rs.getString("REPL_WRITER"));
				hBoxElement.set("replDele", rs.getString("REPL_DELE"));
				hBoxElement.set("replMemo", rs.getString("REPL_MEMO"));
				hBoxElement.set("replDate", rs.getString("REPL_DATE"));
				hBoxElement.set("replPrt", rs.getInt("REPL_PRT"));
				hBoxElement.set("replDept", rs.getInt("REPL_DEPT"));
				hBoxElement.set("replOrder", rs.getInt("REPL_ODR"));
				hBoxElement.set("replMemberSeq", rs.getInt("RP.MEMBER_SEQ"));
				// hBoxElement.set("replMemo",UtilString.text2Html(hBoxElement.getString("replMemo")));
			}
			return hBoxElement;
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}

	/**
	 * <pre>
	 *  
	 * 대댓글을 달기 위해 depth와 order 값을 계산하여 가져온다.
	 * </pre>
	 * 
	 * @author In-Seong Hwang
	 * @since 2018.11.21
	 * @param 댓글 SEQ가 있는 HBox
	 * @return 같은 부모 댓글에 대한 시퀀스와 깊이 + 1 , order +1 된값
	 */
	public HBox selectArticleReplyParent(Connection conn, HBox hBox) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		HBox hBoxElement = null;
		try {
			pstmt = conn.prepareStatement("SELECT BD_SEQ," + "		REPL_DEPT + 1 AS REPL_DEPT," + "		IFNULL("
					+ "				(SELECT MAX(REPL_ODR) " + "				FROM TB_REPL "
					+ "				WHERE REPL_PRT=RP.REPL_SEQ )" + "				, RP.REPL_ODR"
					+ "			  ) + 1  AS REPL_ODR " + "FROM TB_REPL RP " + "WHERE REPL_SEQ = ? ");
			pstmt.setInt(1, hBox.getInt("replPrt"));
			rs = pstmt.executeQuery();

			if (rs.next()) {
				hBoxElement = new HBox();
				hBoxElement.set("bdSeq", rs.getInt("BD_SEQ"));
				hBoxElement.set("replDept", rs.getInt("REPL_DEPT"));
				hBoxElement.set("replOrder", rs.getInt("REPL_ODR"));
			}
			return hBoxElement;
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}

	}

	/**
	 * <pre>
	 *  
	 * 대댓글을 삽입을 위해 대댓글의 order 값보다 큰 값은 +1 증가시킨다.
	 * </pre>
	 * 
	 * @author In-Seong Hwang
	 * @since 2018.11.21
	 * @param 댓글 SEQ가 있는 HBox
	 * @return
	 * @throws SQLException
	 */
	public void updateArticleReplyOrder(Connection conn, HBox hBox) throws SQLException {
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(
					"	UPDATE TB_REPL " + "	SET REPL_ODR=REPL_ODR + 1 " + "	WHERE BD_SEQ=? AND REPL_ODR >= ?");
			pstmt.setInt(1, hBox.getInt("bdSeq"));
			pstmt.setInt(2, hBox.getInt("replOrder"));
			pstmt.executeUpdate();
		} finally {
			JdbcUtil.close(pstmt);
		}

	}

	/**
	 * <pre>
	 *  
	 * 업데이트 된 댓글을 update 한다.
	 * </pre>
	 * 
	 * @author In-Seong Hwang
	 * @since 2018.11.21
	 * @param 댓글 SEQ가 있는 HBox
	 * @return
	 * @throws SQLException
	 */
	public void updateArticleReply(Connection conn, HBox hBox) throws SQLException {
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement("	UPDATE TB_REPL " + "	SET REPL_MEMO = ?, " + "	REPL_DATE = NOW() "
					+ "	WHERE REPL_SEQ = ? ");
			pstmt.setString(1, hBox.getString("replMemo"));
			pstmt.setInt(2, hBox.getInt("replSeq"));
			pstmt.executeUpdate();
		} finally {
			JdbcUtil.close(pstmt);
		}
	}

	/**
	 * <pre>
	 *  
	 * 삭제할 댓글이 자식을 가지고 있는지를 확인한다.
	 * </pre>
	 * 
	 * *
	 * 
	 * @author In-Seong Hwang
	 * @since 2018.11.23
	 * @param 댓글 SEQ가 있는 HBox
	 * @return
	 * @throws SQLException
	 */
	public int selectArticleChildCount(Connection conn, HBox hBox) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int cnt = 0;
		try {			
			pstmt = conn.prepareStatement(
					"	SELECT COUNT(*) "
					+ "	FROM TB_REPL " 
					+ "	WHERE REPL_PRT = ? "
					+ "	AND REPL_SEQ != REPL_PRT " 
					+ "	AND REPL_DELE = 'N'"
					);			
			pstmt.setInt(1, hBox.getInt("replSeq"));
			rs = pstmt.executeQuery();			
			if (rs.next()) {
				cnt = rs.getInt(1);
			}
			return cnt;
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}

	/**
	 * <pre> 
	 * 댓글을 삭제하기 위해 order를 조정한다.
	 * </pre>	 * 
	 * @author In-Seong Hwang
	 * @since 2018.11.23 
	 * @param 댓글 SEQ가 있는 HBox
	 * @return  
	 * @throws SQLException 
	 */
	public void updateBoardReplyOrder4Delete(Connection conn,HBox hBox) throws SQLException {
		PreparedStatement pstmt = null;
		try {
			pstmt= conn.prepareStatement(
					"	UPDATE TB_REPL AS RP1 "
					+ "	INNER JOIN TB_REPL AS RP2 "
					+ "	ON RP1.BD_SEQ = RP2.BD_SEQ "					
					+ "	AND RP1.REPL_ODR > RP2.REPL_ODR"
					+ "	AND RP1.REPL_DELE='N' "
					+ "	SET RP1.REPL_ODR = RP1.REPL_ODR - 1 "
					+ "	WHERE RP2.REPL_SEQ = ?"
					);
			pstmt.setInt(1, hBox.getInt("replSeq"));
			pstmt.executeUpdate();			
		}finally {
			JdbcUtil.close(pstmt);
		}
	}
	/**
	 * <pre> 
	 * 댓글을 삭제한다.
	 * </pre>	 * 
	 * @author In-Seong Hwang
	 * @since 2018.11.23 
	 * @param 댓글 SEQ가 있는 HBox
	 * @return  
	 * @throws SQLException 
	 */
	public void deleteArticleReply(Connection conn, HBox hBox) throws SQLException{
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(
					"	UPDATE TB_REPL "
					+ "	SET REPL_DELE = 'Y', "
					+ "	REPL_DATE = NOW() "
					+ "	WHERE REPL_SEQ = ? "
					);
			pstmt.setInt(1, hBox.getInt("replSeq"));
			pstmt.executeUpdate();
		}finally {
			JdbcUtil.close(pstmt);			
		}
			
	}
}
