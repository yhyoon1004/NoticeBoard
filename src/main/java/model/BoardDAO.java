package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

public class BoardDAO {

	Connection con;
	PreparedStatement pstmt;
	ResultSet rs;

	// 데이터베이스의 커넥션풀을 사용하도록 설정하는 메서드
	public void getCon() {
		try {
			Context initctx = new InitialContext();// 외부에서 데이터를 읽어드릴려고

			Context envctx = (Context) initctx.lookup("java:comp/env");// 톰캣 서버에 정보를 담아놓은 곳
			DataSource ds = (DataSource) envctx.lookup("jdbc/pool");// 데이터소스를 담아 놓은 곳으로 이동
			// datasource
			con = ds.getConnection();// 데이터 소스를 기준으로 커넥션을 연결해주시오.
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 하나의 새로운 게시글이 넘어와서 저장되는 메서드
	public void insertBoard(BoardBean bean) {
		getCon();
		// 빈클래스에 넘어오지 않았던 데이터를 초기화 해주어야 합니다
		int ref = 0;// 글그룹을 의미 = 쿼리를 실행시켜서 가장 큰 ref값을 가져온후 +1을 해주면됨
		int re_step = 1;// 새글 = 부모글
		int re_level = 1;
		try {
			String refsql = "select max(ref) from board";// sql max함수ref중 가장 큰수 반환
			// 쿼리실행 객체
			pstmt = con.prepareStatement(refsql);
			rs = pstmt.executeQuery();// 쿼리실행결과 리턴
			if (rs.next()) {// 결과값이 있다면
				ref = rs.getInt(1) + 1;// 최대값에 +1을 더해서 글 그룹을 설정
			}
			// 실제로 게시글 전체값을 테이블에 저장
			String sql = "insert into board values(board_seq.NEXTVAL,?,?,?,?,sysdate,?,?,?,0,?)";
			// board_seq.NEXTVAL은 nextvalue라는 뜻으로 시퀀스에 들아가있는 값의 다음값 다음값을자동으로 매핑해서 리턴해줌
			pstmt = con.prepareStatement(sql);
			// ?에 값을 넣어줌
			pstmt.setString(1, bean.getWriter());
			pstmt.setString(2, bean.getEmail());
			pstmt.setString(3, bean.getSubject());
			pstmt.setString(4, bean.getPassword());
			pstmt.setInt(5, ref);
			pstmt.setInt(6, re_step);
			pstmt.setInt(7, re_level);
			pstmt.setString(8, bean.getContent());
			pstmt.executeUpdate();

			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 모든 게시글을 리턴해주는 메서드
	public Vector<BoardBean> getAllBoard() {
		// 리턴할 객체 선언
		Vector<BoardBean> v = new Vector<>();

		getCon();
		try {
			// 쿼리준비
			String sql = "select * from Board order by ref desc, re_step asc  ";
			// 쿼리를 실행할 객체 선언
			pstmt = con.prepareStatement(sql);
			// 쿼리 실행값 저장
			rs = pstmt.executeQuery();
			// 데이터 개수가 몇개인지 모르기에 반복문을 이용하여 데이터 추출
			while (rs.next()) {
				// 데이터를 패킹징( 가방 = BoardBean 클래스를 이용해서)
				BoardBean bean = new BoardBean();
				bean.setNum(rs.getInt(1));
				bean.setWriter(rs.getString(2));
				bean.setEmail(rs.getString(3));
				bean.setSubject(rs.getString(4));
				bean.setPassword(rs.getString(5));
				bean.setReg_date(rs.getDate(6).toString());
				bean.setRef(rs.getInt(7));
				bean.setRe_step(rs.getInt(8));
				bean.setRe_level(rs.getInt(9));
				bean.setReadcount(rs.getInt(10));
				bean.setContent(rs.getString(11));
				// 패키징한 데이터를 백터에 저장
				v.add(bean);
			}

			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return v;
	}

	// 하나의 게시글을 리턴하는 메서드
	public BoardBean getOneBoard(int num) {
		BoardBean bean = new BoardBean();
		getCon();
		try {

			// 조회수 증가쿼리
			String readsql = "update board set readcount = readcount+1 where num=?";
			pstmt = con.prepareStatement(readsql);
			pstmt.setInt(1, num);
			pstmt.executeUpdate();

			String sql = "select * from board where num=?";
			// 쿼리실행객체
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, num);
			// 쿼리 실행후 결과를 리턴
			rs = pstmt.executeQuery();
			if (rs.next()) {
				bean.setNum(rs.getInt(1));
				bean.setWriter(rs.getString(2));
				bean.setEmail(rs.getString(3));
				bean.setSubject(rs.getString(4));
				bean.setPassword(rs.getString(5));
				bean.setReg_date(rs.getDate(6).toString());
				bean.setRef(rs.getInt(7));
				bean.setRe_step(rs.getInt(8));
				bean.setRe_level(rs.getInt(9));
				bean.setReadcount(rs.getInt(10));
				bean.setContent(rs.getString(11));
			}
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return bean;
	}

	// 답변글이 저장되는 메서드
	public void reWriteBoard(BoardBean bean) {
		// 부모글그룹과 글레벨 글스텝을 읽어드림
		int ref = bean.getRef();
		int re_level = bean.getRe_level();
		int re_step = bean.getRe_step();
		System.out.println("글그룹 ref="+ref+"글그룹 re_level="+re_level+"글그룹 re_step="+re_step);
		getCon();
		try {
			//// 핵심 코드////
			// 부모글보다 큰 re_level의 값을 전부 1씩 증가시켜줌						글그룹		글 레벨
			String levelsql = "update board set re_level = re_level + 1 where ref = ? and re_level > ?";
			pstmt = con.prepareStatement(levelsql);
			pstmt.setInt(1, ref);
			pstmt.setInt(2, re_level);
			pstmt.executeUpdate();
			// 답변글 데이터를 저장
			String sql = "insert into board values(board_seq.NEXTVAL,?,?,?,?,sysdate,?,?,?,0,?)";
			pstmt=con.prepareStatement(sql);
			pstmt.setString(1, bean.getWriter());	
			pstmt.setString(2, bean.getEmail());
			pstmt.setString(3, bean.getSubject());
			pstmt.setString(4, bean.getPassword());
			pstmt.setInt(5, ref);//부모의 ref값을 넣어줌
			pstmt.setInt(6, re_step+1);//답글이기에 부모 글 re_step에 1을 더해
			pstmt.setInt(7, re_level+1);
			pstmt.setString(8, bean.getContent());
			pstmt.executeUpdate();
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	
	//BoardUpdate용 하나의 게시글을 리턴하는 메서드
	public BoardBean getOneUpdateBoard(int num) {
		BoardBean bean = new BoardBean();
		getCon();
		try {
			String sql = "select * from board where num=?";
			// 쿼리실행객체
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, num);
			// 쿼리 실행후 결과를 리턴
			rs = pstmt.executeQuery();
			if (rs.next()) {
				bean.setNum(rs.getInt(1));
				bean.setWriter(rs.getString(2));
				bean.setEmail(rs.getString(3));
				bean.setSubject(rs.getString(4));
				bean.setPassword(rs.getString(5));
				bean.setReg_date(rs.getDate(6).toString());
				bean.setRef(rs.getInt(7));
				bean.setRe_step(rs.getInt(8));
				bean.setRe_level(rs.getInt(9));
				bean.setReadcount(rs.getInt(10));
				bean.setContent(rs.getString(11));
			}
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return bean;
	}
	
}
