package mvc.controller.bbs;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mvc.command.CommandHandler;
import mvc.model.HBox;
import mvc.service.article.ArticleService;
/**
 * <pre> 
 * 게시글 bdSeq에 대한 댓글을 저장한다.
 * </pre>
 * 
 * @author In-Seong Hwang
 * @since 2018.11.17 
 *  
 */
public class ArticleReplySaveController implements CommandHandler{
	ArticleService articleService = new ArticleService(); 
	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		HBox hBox = (HBox)req.getAttribute("hBox");
		
	
		// 수정 및 삭제 권한 확인 (미구현)
		
		HBox result =  articleService.insertArticleReply(hBox);
		req.setAttribute("result", result);
		return "/WEB-INF/view/article/ArticleReadAjax4Reply.jsp";
	}
	

}
