package common.util;

/**
 * <pre>
 *   페이징 모듈 Class
 * </pre>
 * 
 * @author In-seong Hwang
 * @since 2018. 11. 15
 
 */

public class PagingMo {
	/**
	 * <pre>
	 *   페이징 모듈 Class
	 * </pre>
	 * 
	 * @author In-seong Hwang
	 * @since 2018. 11. 15
	 * @param total : 게시글 리트스 총개수 (페이지 개수 구하기위한것)
	 * 		rowSize : 보여줄 게시글 리스트개수( 페이지 개수를 구하기 위한것)
	 * 		curPage : 보여줄 페이지 번호
	 * 		pageName: 요청 URL (자바스크립트로 동적 구현)
	 * 		blockSize: 보여줄 페이지 개수 (5개면 < 1 2 3 4 5 > )
	 * @return result : 페이징 처리 완료된 문자열
	 */
	public String getPagingPrint(int total,int curPage,int rowSize,int blockSize,String pageName) {
		StringBuilder result = new StringBuilder();
		try {
			int startPage;
			int endPage;
			int totalPage;
			int beforePage;
			int afterPage;			
			// [1] 유효성 검사
			if(rowSize<1) rowSize= 10;
			if(blockSize<1) blockSize = 10;
			if(curPage < 1 ) curPage =1 ;
			// [2] 페이징 요소 처리
			if(total == 0) {
				totalPage = 1;
				startPage = 1;
				endPage = 1;
			}else {
				totalPage = total/rowSize;			
				if(total % rowSize >0 ) {
					totalPage++;
				}				
				startPage = ((curPage-1)/blockSize )*blockSize +1;
				endPage = startPage + blockSize -1;
			}
			if(endPage > totalPage) endPage =totalPage;
			if(curPage <= 1) {
				beforePage = curPage;				
			}else {
				beforePage = curPage - 1;
			}
			if(curPage == totalPage) {
				afterPage = totalPage;
			}else {
				afterPage = curPage + 1;
			}
			// [3] 동적으로 페이지를 그려준다
			// beforePage
			result.append("<ul>");
			result.append("<li><a class='pagingPrev' href ='javascript:" + pageName + "(" );
			result.append(beforePage);
			result.append(")'>");
			result.append("< </a></li>");
			
			for(int i = startPage ; i<=endPage ; i++) {
				if(i== curPage) {
					result.append("<li><a class='currentPage' href='javascript:" + pageName + "(");
				}else {
					result.append("<li><a href='javascript:" + pageName + "(");
				}
				result.append(i);
				result.append(")'>");
				result.append(i);
				result.append("</a></li>");
			}
			
			//afterPage			
			result.append("<li><a class='pagingNext' href='javascript:" + pageName + "(");
			result.append(afterPage);
			result.append(")'>");
			result.append("> </a></li>");
			result.append("</ul>");
			
		}catch (Exception e) {
			e.printStackTrace();
		}		
		return result.toString();
	}
}
