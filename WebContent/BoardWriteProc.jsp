<%@page import="model.BoardDAO"%>
<%@ page language="java" contentType="text/html; charset=EUC-KR"
	pageEncoding="EUC-KR"%>
<!DOCTYPE html>
<html>
<body>
	<%
	request.setCharacterEncoding("euc-kr");
	%>

	<!-- �Խñ� �ۼ��� �����͸� �ѹ��� �о�帲 -->
	<jsp:useBean id="boardBean" class="model.BoardBean">
		<!-- java���� Class�� ���������� ����-->
		<jsp:setProperty name="boardBean" property="*" /><!-- ���尴ü��(request/Session)� ����� ������� BeanŬ������ -->
	</jsp:useBean>
<%
	//������ ���̽� ������ ��Ŭ������ �Ѱ���
	BoardDAO bdao = new BoardDAO();
	//������ ���� �޼ҵ� ȣ��
	bdao.insertBoard(boardBean);
	
	//�Խñ� ������ ��ü �Խñ� ����
	response.sendRedirect("BoardList.jsp");
%>
</body>
</html>