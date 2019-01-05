package mvc.controller.bbs;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mvc.command.CommandHandler;
import mvc.model.HBox;
import mvc.service.article.ArticleService;

public class ListArticleController implements CommandHandler{
	private ArticleService articleService = new ArticleService();
	
	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		HBox hBox = (HBox)req.getAttribute("hBox");
		HBox result = articleService.getArticlePage(hBox);		
		req.setAttribute("result", result);		
		return "/WEB-INF/view/article/listArticle.jsp";		
	}

}
