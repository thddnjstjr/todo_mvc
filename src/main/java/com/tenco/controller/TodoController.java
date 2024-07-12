package com.tenco.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import com.tenco.model.TodoDAO;
import com.tenco.model.TodoDAOImpl;
import com.tenco.model.TodoDTO;
import com.tenco.model.UserDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/todo/*")
public class TodoController extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private TodoDAO todoDAO;

	public TodoController() {
		todoDAO = new TodoDAOImpl();
	}

	// http://localhost:8080/mvc/todo/list (권장x)
	// http://localhost:8080/mvc/todo/formzzz
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String action = request.getPathInfo();
		// 로그인한 사용자만 접근을 허용하도록 설계
		HttpSession session = request.getSession();
		UserDTO principal = (UserDTO) session.getAttribute("principal");
		if (principal == null) {
			response.sendRedirect("/mvc/user/signIn?message=invalid");
			return;
		}
		System.out.println("action : " + action);
		
		switch (action) {
		case "/list":
			todoListPage(request, response,principal.getId());
			break;
		case "/delete":
			int id = Integer.parseInt(request.getParameter("id"));
			todoDAO.deleteTodo(id, principal.getId());
			todoListPage(request, response, principal.getId());
			break;
		case "/detail":
			todoDetailPage(request, response, principal.getId());
			break;
		case "/todoForm":
			todoFormPage(request, response, principal.getId());
		default:
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
	}
	private void todoFormPage(HttpServletRequest request, HttpServletResponse response, int id) {
		try {
			request.getRequestDispatcher("/WEB-INF/views/todoForm.jsp").forward(request, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// http://localhost:8080/mvc/todo/detail?id=1;
	private void todoDetailPage(HttpServletRequest request, HttpServletResponse response, int principalId) throws IOException {
		
		try {
			
			// todo - pk - 1 , 3 , 5 (야스오)
			// todo - pk - 2 , 4 , 6 (홍길동)
			int todoId = Integer.parseInt(request.getParameter("id"));
			TodoDTO dto = todoDAO.getTodoById(todoId);
			if(dto.getUserId() == principalId) {
				// 상세보기 화면으로 이동 처리
				request.setAttribute("todo", dto);
				request.getRequestDispatcher("/WEB-INF/views/todoDetail.jsp").forward(request, response);
			} else {
				// 권한이 없습니다 or 잘못된 접근입니다.
				response.setContentType("text/html; charset=UTF-8");
				PrintWriter out = response.getWriter();
				out.println("<script> alert('권한이 없습니다'); history.back(); </script>");
			}
			// dto(userId) / 
			
		} catch (Exception e) {
			response.sendRedirect(request.getContextPath() + "/todo/list?error=invalid");
		}
		
		
		
	}

	// http://localhost:8080/mvc/todo/list
	private void todoListPage(HttpServletRequest request, HttpServletResponse response,int principalId)
			throws IOException, ServletException {
		// request.getPathinfo() --> URL 요청에 있어 데이터 추출
		// request.getParameter() --> URL 요청에 있어 데이터 추출

		// request.getAttribute() --> 뷰를 내릴 속성에 값을 담아서 뷰로 내릴 때 사용
		List<TodoDTO> list = todoDAO.getTodosByUserId(principalId);
		request.setAttribute("list", list);
		// todoList.jsp 페이지로 내부에서 이동 처리
		request.getRequestDispatcher("/WEB-INF/views/todoList.jsp").forward(request, response);

	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// TODO - 추가 예정
		HttpSession session = request.getSession();
		UserDTO principal = (UserDTO) session.getAttribute("principal");
		// principal -- null 이라면 --> 로그인 페이지 이동 처리
		if (principal == null) {
			response.sendRedirect("/user/signIn");
		}
		String action = request.getPathInfo();
		System.out.println(action);
		switch (action) {
		case "/list":
			todoListPage(request, response,principal.getId());
			break;
		case "/delete":
			int id = Integer.parseInt(request.getParameter("id"));
			todoDAO.deleteTodo(id, principal.getId());
			todoListPage(request, response,principal.getId());
			break;
		case "/add":
			addTodo(request, response, principal.getId());
			todoListPage(request, response,principal.getId());
			break;
		case "/update":
			updateTodo(request, response, principal.getId());
			break;
		default:
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			break;
		}
	}
	/**
	 * todo - 수정 기능
	 * @param request
	 * @param response
	 * @param principalId
	 * @throws IOException
	 */
	private void updateTodo(HttpServletRequest request, HttpServletResponse response, int principalId) throws IOException {
		
		try {
			int todoId = Integer.parseInt(request.getParameter("id"));
			TodoDTO todo = new TodoDTO();
			todo.setId(todoId);
			todo.setTitle(request.getParameter("title"));
			todo.setDescription(request.getParameter("description"));
			todo.setDueDate(request.getParameter("dueDate"));
			todo.setCompleted("on".equals(request.getParameter("completed")));
			todoDAO.updateTodo(todo, principalId);
			
		} catch (Exception e) {
			response.setContentType("text/html; charset=UTF-8");
			PrintWriter out = response.getWriter();
			out.println("<script> alert('잘못된 요청입니다'); history.back(); </script>");
		}
		
		response.sendRedirect(request.getContextPath() + "/todo/list");
	}

	private void addTodo(HttpServletRequest request, HttpServletResponse response, int principalId) {
		TodoDTO todo = new TodoDTO();
		todo.setTitle(request.getParameter("title"));
		todo.setDescription(request.getParameter("description"));
		todo.setDueDate(request.getParameter("dueDate"));
		todo.setCompleted("on".equals(request.getParameter("completed")));
		System.out.println("버튼은" + request.getParameter("completed"));
		todoDAO.addTodo(todo, principalId);

		// checkBox는 여러개 선택 가능한 태그 : String[] 배열로 선언 했음
		// 이번에 checkBox 하나만 사용 중
		// 체크박스가 선택되지 않았으면 null을 반환하고 체크가 되어 있다면 on 으로 넘어 온다.

	}

}
