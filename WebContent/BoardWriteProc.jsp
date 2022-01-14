<%@page import="model.BoardDAO"%>
<%@ page language="java" contentType="text/html; charset=EUC-KR"
	pageEncoding="EUC-KR"%>
<!DOCTYPE html>
<html>
<body>
	<%
	request.setCharacterEncoding("euc-kr");
	%>

	<!-- 게시글 작성한 데이터를 한번에 읽어드림 -->
	<jsp:useBean id="boardBean" class="model.BoardBean">
		<!-- java에서 Class명 참조변수명 과정-->
		<jsp:setProperty name="boardBean" property="*" /><!-- 내장객체에(request/Session)등에 저장된 변수명과 Bean클래스의 -->
	</jsp:useBean>
<%
	//데이터 베이스 쪽으로 빈클래스를 넘겨줌
	BoardDAO bdao = new BoardDAO();
	//데이터 저장 메소드 호출
	bdao.insertBoard(boardBean);
	
	//게시글 저장후 전체 게시글 보기
	response.sendRedirect("BoardList.jsp");
%>
</body>
</html>