<%@page import="common.jdbc.connection.ConnectionProvider"%>
<%@page import="java.sql.SQLException"%>

<%@page import="java.sql.Connection"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
<%
	Connection  conn=null;
	try{
		conn = ConnectionProvider.getConnection();
		out.print("커넥션 성공");
	}catch(SQLException e){
		out.println("실패" + e.getMessage());	
		
	}finally{
		if(conn != null) conn.close();
	}
	%>
</body>
</html>