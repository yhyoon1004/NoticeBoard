<%@page import="model.BoardBean"%>
<%@page import="java.util.Vector"%>
<%@page import="model.BoardDAO"%>
<%@ page language="java" contentType="text/html; charset=EUC-KR"
	pageEncoding="EUC-KR"%>
<!DOCTYPE html>
<html>
<body>
	<%
	//��ü �Խñ��� ������ JSP������ �����;���
	BoardDAO bdao = new BoardDAO();

	//��ü �Խñ��� ���� �޾��ִ� �ҽ�
	Vector<BoardBean> vec = bdao.getAllBoard();
	%>


	<center>

		<h2>��ü �Խñ� ����</h2>
		<table width="700" border="1" bgcolor="skyblue">
			<tr height="40">
				<td width="50" align="center">��ȣ</td>
				<td width="320" align="center">����</td>
				<td width="100" align="center">�ۼ���</td>
				<td width="150" align="center">�ۼ���</td>
				<td width="80" align="center">��ȸ��</td>
			</tr>

			<%
			for (int i = 0; i < vec.size(); i++) {
				BoardBean bean = vec.get(i);//
			%>
			<tr height="40">
				<td width="50" align="center"><%=i + 1%></td>
				<td width="320" align="center">
				<a href="BoardInfo.jsp?num=<%=bean.getNum()%>" style="text-decoration:none;"> 
				 <%
 if (bean.getRe_step() > 1) {
 	for (int j = 0; j < (bean.getRe_step() - 1) * 5; j++) {
 %>&nbsp; <%
 }
 }
 %> <%=bean.getSubject()%>
				</a></td>
				<td width="100" align="center"><%=bean.getWriter()%></td>
				<td width="150" align="center"><%=bean.getReg_date()%></td>
				<td width="80" align="center"><%=bean.getReadcount()%></td>
			</tr>
			<%
			}
			%>

			<tr height="40">
				<td align="center" colspan="5">
					<button type="button" value="�۾���"
						onclick="location.href='BoardWriteForm.jsp'">�۾���</button>
				</td>
			</tr>
		</table>

	</center>
</body>
</html>