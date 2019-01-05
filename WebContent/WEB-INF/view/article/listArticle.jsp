<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<c:set value="${result.articleList }" var="articleList"/>
<c:set value="${hBox}" var="hBox"/>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>게시글 목록</title>

<%-- 적용 CSS --%>
<link type="text/css" rel="stylesheet" href="${CSS}/common/reset.css">
<%-- 적용 JS --%>
<script type="text/javascript" src="${JS}/common/common.js"></script>
<script type="text/javascript" src="${JS}/jquery/jquery-1.12.3.js"></script>
<script type="text/javascript" src="${JS}/jquery/jquery.form.js"></script>
</head>
<body>
<form name="articleForm" autocomplete="off">
<%-- INPUT HIDDEN 영역 시작 --%>
	<%-- 페이징을 위해서는 아래 형식의 curPage, action 필수(id값을 통해 페이징 시킴) --%>
	<input type="hidden" name="curPage" id="curPage" value="${hBox.curPage}" />
	<input type="hidden" name="action" id="action" value="${HOME}/article/list.do" />
<%-- INPUT HIDDEN 영역 종료 --%>
<table border="1">
	<tr>
		<td colspan="4"><a href="write.do">[게시글쓰기]</a></td>
	</tr>
	<tr>
		<td>번호</td>
		<td>제목</td>
		<td>작성자</td>
		<td>조회수</td>
	</tr>
<c:if test="${empty articleList }">
	<tr>
		<td colspan="4">게시글이 없습니다.</td>
	</tr>
</c:if>
<c:forEach var="articleList" items="${articleList}">
	<tr>
		<td>${articleList.bdSeq}</td>
		<td>
		<a href="list-detail.do?bdSeq=${articleList.bdSeq}&curPage=${articleList.curPage}">
		<c:out value="${articleList.bdTitle}"/>
		</a>
		</td>
		<td>${articleList.writerName}</td>
		<td>${articleList.readCnt}</td>
	</tr>
</c:forEach>
</table>

  <%-- 페이징 영역 시작 --%>
<div class="tablepaging-area">
	<div class="table-paging">${result.paging}</div>
</div>
<%-- 페이징 영역 종료 --%>
</form>
</body>
</html>