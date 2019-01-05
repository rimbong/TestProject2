<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<c:set value="${result.articleResult}" var="articleResult" />
<c:set value="${result.contentResult}" var="contentResult" />
<c:set value="${result.articleList}" var="articleList" />

<c:set value="${hBox}" var="hBox" />

<!DOCTYPE html>
<html>
<head>
<title>게시글 읽기</title>
<%-- 적용 CSS --%>
<link type="text/css" rel="stylesheet" href="${CSS}/common/reset.css">
<%-- 적용 JS --%>
<script type="text/javascript" src="${JS}/common/common.js"></script>
<script type="text/javascript" src="${JS}/jquery/jquery-1.12.3.js"></script>
<script type="text/javascript" src="${JS}/jquery/jquery.form.js"></script>
</head>
<body>
	<table border="1">
		<tr>
			<td>번호</td>
			<td>${articleResult.bdSeq}</td>
		</tr>
		<tr>
			<td>작성자</td>
			<td>${articleResult.writerName}</td>
		</tr>
		<tr>
			<td>제목</td>
			<td><c:out value='${articleResult.bdTitle}' /></td>
		</tr>
		<tr>
			<td>내용</td>
			<!--  백단이 아닌 html에서 enter 제어하기 별로 좋은 방법은 아니다. -->
			<td style="white-space: pre;"><c:out value="${contentResult.bdContent}" escapeXml="true" /></td>
		</tr>
		<tr>
			<td colspan="2"><c:set var="pageNo" value="${empty hBox.curPage ? '1' : hBox.curPage}" /> <a href="list.do?pageNo=${pageNo}">[목록]</a> <c:if test="${hBox.memberId == articleResult.writerId}">
					<a href="modify.do?bdSeq=${articleResult.bdSeq}">[게시글수정]</a>
					<a href="delete.do?bdSeq=${articleResult.bdSeq}">[게시글삭제]</a>
				</c:if></td>
		</tr>
	</table>
	<input type="hidden" id="bdSeq" name="bdSeq" value="<c:out value="${articleResult.bdSeq}"/>">
	<!--  댓글 시작 -->
	<!--  게시판 댓글 쓰기 영역 -->
	<div class="panel panel-default">
		<div class="panel-body">
			<div class="col-lg-6">
				<textarea class="form-control" id="replMemo1" name="replMemo" maxlength="500" placeholder="댓글을 달아주세요"></textarea>
			</div>
			<div class="col-lg-6">
				<button class="btn btn-outline btn-primary" onclick="fn_formSubmit()">저장</button>

			</div>
		</div>
	</div>
	<!--  게시판 댓글리스트 시작 -->
	<div id="articleList">
		<c:forEach var="articleList" items="${articleList}" varStatus="status">

			<div class="panel panel-default replyParent<c:out value="${articleList.replPrt}"/>" 
					id="replItem<c:out value="${articleList.replSeq}"/>" 
					style="margin-left: <c:out value="${20*articleList.replDept}"/>px;">
				<div class="panel-body">
					<div class="photoTitle">
						<div>
							<c:out value="${articleList.replWriter}" />
							<c:out value="${articleList.replDate}" />
							<c:if test='${articleList.replMemberSeq==sessionScope.memberSeq}'>
								<a href="javascript:fn_replyDelete('<c:out value="${articleList.replSeq}"/>')" title="삭제"><span class="text-muted">삭제</span></a>
								<a href="javascript:fn_replyUpdate('<c:out value="${articleList.replSeq}"/>')" title="수정"><span class="text-muted">수정</span></a>
							</c:if>
							<!--  숨겨진 dialog 나오게해준다. -->
							<a href="javascript:fn_replyReply('<c:out value="${articleList.replSeq}"/>')" title="답글"><span class="text-muted">답글</span></a>
						</div>
						<!--  깊이 1 댓글 출력 -->
						<div id="repl<c:out value="${articleList.replSeq}"/>" >
							<div class="content" style="white-space: pre;"><c:out value="${articleList.replMemo}" escapeXml="true" /></div>
						</div>
						
					</div>
				</div>
			</div>

		</c:forEach>
	</div>
	<!--  게시판 댓글 쓰기 영역  보이지 않으며 이후에 댓글을 수정 할 때 동적으로 쓰여진다.-->
	<div id="replDiv" style="width: 99%; display:none">			 
			<input type="hidden" id="replSeq2" name="replSeq">
			<div class="col-lg-6">
				<textarea class="form-control" id="replMemo2" name="replMemo2" rows="3" maxlength="500" style="height: 300px" ></textarea>
			</div>
			<div class="col-lg-2 pull-left">
   				<button class="btn btn-outline btn-primary" onclick="fn_replyUpdateSave()">저장</button>
   				<button class="btn btn-outline btn-primary" onclick="fn_replyUpdateCancel()">취소</button>
			</div>
	</div>
	<!--  숨겨져 있는 게시판 댓글의 댓글 쓰기 영역 -->
	<div id="replDialog" style="width: 99%; display: none">
		<input type="hidden" id="replSeq3" name="replSeq">
		<input type="hidden" id="replPrt3" name="replPrt">
		<div class="col-lg-6">
			<textarea class="form-control" id="replMemo3" name="replMemo3" rows="3" maxlength="500" style="width: 300px;height: 300px"></textarea>
		</div>
		<div class="col-lg-2 pull-left">
			<button class="btn btn-outline btn-primary" onclick="fn_replyReplySave()">저장</button>
			<button class="btn btn-outline btn-primary" onclick="fn_replyReplyCancel()">취소</button>
		</div>
	</div>
</body>
<script type="text/javascript">
var updateReplSeq = updateReplMemo = null;
	function fn_formSubmit() {
		$.ajax({
			url : "${HOME}/article/repl-save.do",
			dataType : "html",
			type : "post",
			data : {
				bdSeq : $("#bdSeq").val(),
				replMemo : $("#replMemo1").val()
			},
			success : function(result) {
				if (result !== "") {
					$("#replMemo1").val("");
					$("#articleList").append(result);

				} else {
					alert('error');
				}
			}
		})
	}
	function fn_replyUpdate(replSeq){
		hideDiv("#replDialog");
		
		if (updateReplSeq) {
			$("#replDiv").appendTo(document.body);
			$("#repl"+updateReplSeq).find('.content').text(updateReplMemo);
		} 
		updateReplSeq   = replSeq;
		
		updateReplMemo = $("#repl"+replSeq).find('.content').text();
		
		$("#replSeq2").val(replSeq);		
		$("#replMemo2").val(updateReplMemo);
		$("#repl"+replSeq).find('.content').text("");
		$("#replDiv").appendTo($("#repl"+replSeq));
		$("#replDiv").show();
		$("#replMemo2").focus();
	}
	function fn_replyUpdateSave(){
		if ( ! chkInputValue("#replMemo2", "댓글 내용을 채워주세요")) return;
		
		$.ajax({
			url: "/article/repl-save.do", 
			type:"post", 
			data : {bdSeq:$("#bdSeq").val(), replSeq: updateReplSeq, replMemo: $("#replMemo2").val()},
			success: function(result){
				if (result!=="") {
					$("#replDiv").appendTo(document.body);
					$("#replDiv").hide();
					$("#repl"+updateReplSeq).find('.content').text($("#replMemo2").val());
					
				} else{
					alert("댓글작성중 예기치 못한 에러발생");
				}
				updateReplSeq = updateReplMemo = null;
			}
		})
	}
	function fn_replyUpdateCancel(){
		hideDiv("#replDiv");
		
		$("#repl"+updateReplSeq).find('.content').text(updateReplMemo);
		updateReplSeq = updateReplMemo = null;
	}
	function fn_replyDelete(replSeq){
		if (!confirm("정말로 삭제하시겠습니까?")) {
			return;
		}
		$.ajax({
			url: "/article/repl-delete.do",
			type:"post", 
			data: {"replSeq": replSeq},
			success: function(result){
				if (result=="OK") {
					$("#replItem"+reno).remove();
					alert("삭제");
				} else
				if (result=="Fail") {
					alert("실패");				
				}
			}
		})
	}	
	function fn_replyReply(replSeq){
		$("#replDialog").show();
		if (updateReplSeq) {
			fn_replyUpdateCancel();
		}
		$("#replPrt3").val(replSeq);
		$("#replMemo3").val("");		
		$("#replDialog").appendTo($("#repl"+replSeq));
		$("#replMemo3").focus();
	}
	function fn_replyReplySave(){
		if ( ! chkInputValue("#replMemo3", "댓글 내용을 채워주세요")) return;

		$.ajax({
			url: "${HOME}/article/repl-save.do",
			type:"post", 
			data : {
				bdSeq:$("#bdSeq").val(),
				replSeq: $("#replSeq3").val(),
				replPrt: $("#replPrt3").val(),
				replMemo: $("#replMemo3").val()				
			},
			dataType : "html",
			success: function(result){
				if (result!=="") {
					var parent = $("#replPrt3").val();
					var parentNodes = $(".replPrt"+parent);
					if (parentNodes.length===0) {
						$("#replItem"+parent).after(result);
					}else {
						parentNodes.last().after(result);
					}
					hideDiv("#replDialog");					
					$("#replMemo3").val("");
				} else{
					alert("실패");
				}
			}
		})	
	}
	function fn_replyReplyCancel(){
		$("#replDialog").hide();		
	}
	function hideDiv(id){
		$(id).hide();
		$(id).appendTo(document.body);
	}
</script>


</html>