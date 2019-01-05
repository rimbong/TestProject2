/*
 * 모든 페이지에 대해서 사용할 수 있는 자바스크립트 작성 
 */

/**
 * <pre>
 *   페이지 이동에 대한 form action
 * </pre>
 * @author In-seong Hwang
 * @since 2018. 11. 15
 * @param movePage : Move Page Number
 */
 function getPage(movePage) {	 
	var form = $('form');
	form.find('#curPage').val(movePage);
	form.attr({'method':'POST','action':form.find('#action').val()});	
	form.submit();
 };
 /**
  * <pre>
  *   id값을 통한 input 값 체크
  * </pre>
  * @author In-seong Hwang
  * @since 2018. 11. 21
  * @param id : 체크할 인풋 id  alert : alert 메시지 내용
  */
function chkInputValue(id, msg){
	if ( $.trim($(id).val()) == "" || $.trim($(id).val()) == null) {
		alert(msg+"을(를) 입력해주세요.");
		$(id).focus();
		return false;
	}
	return true;
}
/**
 * <pre>
 *   Textarea에 text를 담기위해 html을 적절하게 치환해준다.
 * </pre>
 * @author In-seong Hwang
 * @since 2018. 11. 21
 * @param 
 */
function html2Text(str) {
    str = str.replace(/&nbsp;/gi, " ");
    return str.replace(/<br>/gi, "\n");
}
 
 