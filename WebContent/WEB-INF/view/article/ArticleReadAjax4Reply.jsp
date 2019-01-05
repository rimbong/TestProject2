<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<c:set value="${result.articleRepl}" var="articleRepl" />

<div class="panel panel-default replyParent<c:out value="${articleRepl.replPrt}"/>" id="replItem<c:out value="${articleRepl.replSeq}"/>" style="margin-left: <c:out value="${20*articleRepl.replDept}"/>px;">
	<div class="panel-body">

		<div class="photoTitle">
			<div>
				<c:out value="${articleRepl.replWriter}" />
				<c:out value="${articleRepl.replDate}" />
				<c:if test='${articleRepl.replMemberSeq==sessionScope.memberSeq}'>
					<a href="javascript:fn_replyDelete('<c:out value="${articleRepl.replSeq}"/>')" title="삭제"><span class="text-muted">삭제</span></a>
					<a href="javascript:fn_replyUpdate('<c:out value="${articleRepl.replSeq}"/>')" title="수정"><span class="text-muted">수정</span></a>
				</c:if>
				<!--  숨겨진 dialog 나오게해준다. -->
				<a href="javascript:fn_replyReply('<c:out value="${articleRepl.replSeq}"/>')" title="답글"><span class="text-muted">답글</span></a>
			</div>
			<div id="repl<c:out value="${articleRepl.replSeq}"/>">
				<div class="content" style="white-space: pre;"><c:out value="${articleRepl.replMemo}" escapeXml="true" /></div>
			</div>
			<!--  깊이 1 댓글 출력 -->
		</div>
	</div>
</div>