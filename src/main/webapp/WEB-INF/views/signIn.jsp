<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>게임 로그인</title>
    <link rel="stylesheet" type="text/css" href="../css/styles.css">
</head>
<body>
    <div class="login-container">
        <h2>게임 로그인</h2>

        <!-- 메시지 출력 (성공 또는 오류 메시지) -->
        <% 
            String message = request.getParameter("message");
            if (message != null) {
        %>
            <p class="message success-message"><%= message %></p>
        <% } %>

        <!-- 로그인 폼 -->
        <form class="login-form" action="/mvc/user/signIn" method="post">
            <div class="form-group">
                <label for="username">사용자 이름</label>
                <input type="text" id="username" name="username" value="야스오1" required>
            </div>
            <div class="form-group">
                <label for="password">비밀번호</label>
                <input type="password" id="password" name="password" value="asd123" required>
            </div>
            <button type="submit">로그인</button>
        </form>
        
        <!-- 회원가입 버튼 -->
        <div class="signup-link">
            <a href="/mvc/user/signUp">회원가입</a>
        </div>
    </div>
</body>
</html>