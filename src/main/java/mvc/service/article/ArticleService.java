package mvc.service.article;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.ibatis.session.SqlSession;

import common.dao.ArticleDao;
import common.exception.ArticleContentNotFoundException;
import common.exception.ArticleNotFoundException;
import common.jdbc.JdbcUtil;
import common.jdbc.connection.ConnectionProvider;
import common.mybatis.MybatisApp;
import common.util.PagingMo;
import mvc.model.HBox;
import mvc.model.HBoxList;

/**
 * <pre>
 *  
 * ArticleService 게시판관련 모든 서비스가 있다.
 * </pre>
 * 
 * @author In-Seong Hwang
 * @since 2018.11.13
 * @return HBox
 */
public class ArticleService {
	private ArticleDao articleDao = ArticleDao.getInstance();
	private PagingMo pagingMo = new PagingMo();

	/**
	 * <pre>
	 *  
	 * 게시판 쓰기 기능을 제공하는 서비스
	 * </pre>
	 * 
	 * @author In-Seong Hwang
	 * @since 2018.11.13
	 * @return HBox
	 */
	public HBox write(HBox hBox) {
		HBox result = new HBox();
		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			result = articleDao.insert(conn, hBox);
			if (result.getString("bqSeq") == null) {
				throw new RuntimeException("faile to inser article");
			}
			result = articleDao.insertContent(conn, hBox);
			if (result.getString("bdContent") == null) {
				throw new RuntimeException("fail to insert article_content");
			}
			conn.commit();

			return result;
		} catch (SQLException e) {
			JdbcUtil.close(conn);

		} catch (RuntimeException e) {
			e.printStackTrace();
		} finally {
			JdbcUtil.close(conn);
		}
		return null;
	}

	/**
	 * <pre>
	 *  
	 * 게시판 페이지와 리스트를 저장한다.
	 * </pre>
	 * 
	 * @author In-Seong Hwang
	 * @since 2018.11.13
	 * @return result : 게시글 총개수 , 게시판 리스트와 , 페이징 문자열을 저장한다. curPage와 rowSize만 있으면
	 *         리스트를 원하는 개수만큼 뽑을 수 있다. curPage와 blockSize,totalCnt 있으면 페이징 처리 가능
	 */
	public HBox getArticlePage(HBox hBox) {
		HBox result = new HBox();
		SqlSession sqlSession = null;
		
		System.out.println("#------------------------------[ 페이징 & 리스트 로직 시작]------------------------------#");
		try (Connection conn = ConnectionProvider.getConnection()) {
			System.out.println("[1]");
			sqlSession = MybatisApp.getSessionFactory().openSession();
			System.out.println("[2]");
			// [1]초기화 파라미터
			// curPage : 시작 페이지 , rowSize : 페이지의 row 개수 , startRow : 시작페이지 (0부터 시작함)
			hBox.setIfEmpty("curPage", "1");
			hBox.setIfEmpty("rowSize", "5");
			hBox.setIfEmpty("blockSize", "5");
			hBox.set("startRow", (hBox.getInt("curPage") - 1) * hBox.getInt("rowSize"));
			// [2] 게시글 총개수 조회
			int totalCnt = articleDao.selectCount(conn);
			result.set("totalCnt", totalCnt);
			// [3] 게시글 리스트 가져오기
			System.out.println("[3]");
			HBoxList<HBox> articleList = articleDao.selectList(sqlSession, hBox);
			result.set("articleList", articleList);
			System.out.println("[4]");
			// [4] 페이징
			String paging = pagingMo.getPagingPrint(totalCnt, hBox.getInt("curPage"), hBox.getInt("rowSize"),
					hBox.getInt("blockSize"), "getPage");
			result.set("paging", paging);

			
		} catch (Exception e) {
			e.getMessage();
			e.printStackTrace();
		} finally {
			// 정상 코드
		}
		System.out.println("#------------------------------[ 페이징 & 리스트 로직 종료]------------------------------#");
		return result;
	}

	public void validate(HBox errors, HBox hBox) {
		if (hBox.isEmpty("bdTitle")) {
			errors.put("bdTitle", Boolean.TRUE);
		}
	}

	/**
	 * <pre>
	 *  
	 * 하나의 게세판에 대해 상세한 정보를 가져온다.
	 * </pre>
	 * 
	 * @author In-Seong Hwang
	 * @since 2018.11.15
	 * @return result : 게시글 상세 정보와 내용을 담은 HBox
	 */
	public HBox getArticle(HBox hBox, boolean increaseOrNone) {
		HBox result = new HBox();
		try (Connection conn = ConnectionProvider.getConnection()) {
			//[1] 게시글에 대한 정보
			HBox articleResult = articleDao.selectById(conn, hBox);
			if (articleResult == null) {
				throw new ArticleNotFoundException();
			}
			//[2] 게시글에 대한 내용
			HBox contentResult = articleDao.selectByIdContent(conn, hBox);
			if (contentResult == null) {
				throw new ArticleContentNotFoundException();
			}
			//[3] 조회수 증가
			if (increaseOrNone) {
				articleDao.increaseReadCount(conn, hBox);
			}	
			//[4] 댓글 읽어 오기
			HBoxList<HBox> articleList = articleDao.selectArticleReplyList(conn, hBox);
			//[5] 파일 읽어 오기
			result.set("articleResult", articleResult);
			result.set("contentResult", contentResult);
			result.set("articleList",articleList);
			return result;
		} catch (SQLException e) {
			throw new RuntimeException();
		}

	}

	/**
	 * <pre>	 *  
	 * 게시판에 대한 댓글정보(초기댓글,기존댓글의 수정)를 DB에 insert한다.
	 * 이후 insert된 DB정보를 읽어와 return 한다.
	 * </pre>
	 * 
	 * @author In-Seong Hwang
	 * @since 2018.11.17
	 * @return 삽입된 댓글을 다시 갱신된 DB로부터 읽어와 result에 담아 리턴한다.
	 * @param hBox : 뷰로부터 받아온 각각의 파라미터 정보가 담겨 있다.
	 */
	public HBox insertArticleReply(HBox hBox) {
		HBox result = new HBox();
		try (Connection conn = ConnectionProvider.getConnection()) {
			//[1] 초기 댓글인경우			
			if (hBox.getInt("replSeq") == 0 ) { 
				//(1) 대댓글 인경우
				if (hBox.getInt("replPrt") != 0) {				
					HBox replInfo = articleDao.selectArticleReplyParent(conn, hBox);
					hBox.set("bdSeq",replInfo.getInt("bdSeq"));
					hBox.set("replDept",replInfo.getInt("replDept"));
					hBox.set("replOrder",replInfo.getInt("replOrder"));				
					articleDao.updateArticleReplyOrder(conn, hBox);				
				} 
				//(2) 일반댓글 인경우
				else { 				
					Integer replOrder = articleDao.selectArticleReplyMaxOrder(conn, hBox);
					hBox.set("replOrder", replOrder);
				}
				Integer replSeq = articleDao.selectArticleReplyMaxReplSeq(conn,hBox);
				hBox.set("replSeq",replSeq);
				int cnt = articleDao.insertArticleReply(conn, hBox);	
				
				if (cnt < 1) {
					System.out.println("댓글 삽입 실패");
				}	
			//[2] 댓글 수정 일경우
			}else { 
				articleDao.updateArticleReply(conn, hBox);				
			}
			HBox articleReply = articleDao.selectArticleReplyOne(conn, hBox);
			result.set("articleRepl",articleReply);			
			return result;
		} catch (SQLException e) {
			throw new RuntimeException();		
		}
	}
	/**
	 * <pre>  
	 * 댓글을 삭제 한다. 단 자식댓글이 없어야 한다.
	 * </pre>
	 * 
	 * @author In-Seong Hwang
	 * @since 2018.11.23
	 * @return 
	 * @param hBox : 뷰로부터 받아온 각각의 파라미터 정보가 담겨 있다.
	 */
	public Boolean deleteArticleReply(HBox hBox) {	
		Integer cnt = null;
		try(Connection conn = ConnectionProvider.getConnection()) {
			System.out.println("[1]");
			cnt = articleDao.selectArticleChildCount(conn, hBox);
			if (cnt>0) {
				System.out.println("[*]");
				return false;
			}
			System.out.println("[2]");
			articleDao.updateBoardReplyOrder4Delete(conn, hBox);
			System.out.println("[3]");
			articleDao.deleteArticleReply(conn, hBox);
			return true;
		}catch(SQLException e) {
			throw new RuntimeException();
		}
		
		
	}
}
