<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!-- resources/templates/board_list.html -->
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>게시판 목록</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
</head>
<body class="container mt-5">
    <h2>📋 게시판 목록</h2>

    <table class="table table-bordered table-striped mt-4">
        <thead class="table-dark">
            <tr>
                <th>ID</th>
                <th>제목</th>
                <th>내용</th>
                <th>삭제</th>
            </tr>
        </thead>
        <tbody>
            <tr th:each="board : ${boards}">
                <td th:text="${board.id}"></td>
                <td th:text="${board.title}"></td>
                <td th:text="${board.content}"></td>
                <td>
                    <form th:action="@{'/boards/' + ${board.id}}" method="post">
                        <input type="hidden" name="_method" value="delete"/>
                        <button type="submit" class="btn btn-danger btn-sm">삭제</button>
                    </form>
                </td>
            </tr>
        </tbody>
    </table>

    <h4 class="mt-5">📝 게시글 작성</h4>
    <form action="/boards" method="post">
        <div class="mb-3">
            <label for="title" class="form-label">제목</label>
            <input type="text" name="title" class="form-control" required>
        </div>
        <div class="mb-3">
            <label for="content" class="form-label">내용</label>
            <textarea name="content" class="form-control" rows="4" required></textarea>
        </div>
        <button type="submit" class="btn btn-primary">작성</button>
    </form>
</body>
</html>
