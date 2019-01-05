package mvc.controller.bbs;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import common.exception.ArticleContentNotFoundException;
import common.exception.ArticleNotFoundException;
import mvc.command.CommandHandler;
import mvc.model.HBox;
import mvc.service.article.ArticleService;
/**
 * <pre> 
 * 게시글 하나 읽어서 상세정보 보여주는 Controller
 * </pre>
 * 
 * @author In-Seong Hwang
 * @since 2018.11.15 
 *  
 */
public class ReadArticleController implements CommandHandler {

	private ArticleService articleService = new ArticleService();
	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		HBox hBox = (HBox)req.getAttribute("hBox");
		try {
			HBox result =  articleService.getArticle(hBox, true);
			req.setAttribute("result", result);			
			return "/WEB-INF/view/article/list-detail.jsp";
		}catch (ArticleContentNotFoundException | ArticleNotFoundException e) {
			req.getServletContext().log("no article",e);
			res.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
		
	}
	
}
