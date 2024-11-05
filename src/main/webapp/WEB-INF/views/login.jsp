<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
    
<!doctype html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="description" content="">
<meta name="author" content="Mark Otto, Jacob Thornton, and Bootstrap contributors">
<meta name="generator" content="Hugo 0.108.0">
<title>DDIT BOARD LIST</title>
<link rel="canonical" href="https://getbootstrap.com/docs/5.3/examples/album/">
<link href="${pageContext.request.contextPath }/resources/assets/dist/css/bootstrap.min.css" rel="stylesheet">
<style>
.bd-placeholder-img {
	font-size: 1.125rem;
	text-anchor: middle;
	-webkit-user-select: none;
	-moz-user-select: none;
	user-select: none;
}

@media ( min-width : 768px) {
	.bd-placeholder-img-lg {
		font-size: 3.5rem;
	}
}

.b-example-divider {
	height: 3rem;
	background-color: rgba(0, 0, 0, .1);
	border: solid rgba(0, 0, 0, .15);
	border-width: 1px 0;
	box-shadow: inset 0 .5em 1.5em rgba(0, 0, 0, .1), inset 0 .125em .5em
		rgba(0, 0, 0, .15);
}

.b-example-vr {
	flex-shrink: 0;
	width: 1.5rem;
	height: 100vh;
}

.bi {
	vertical-align: -.125em;
	fill: currentColor;
}

.nav-scroller {
	position: relative;
	z-index: 2;
	height: 2.75rem;
	overflow-y: hidden;
}

.nav-scroller .nav {
	display: flex;
	flex-wrap: nowrap;
	padding-bottom: 1rem;
	margin-top: -1px;
	overflow-x: auto;
	text-align: center;
	white-space: nowrap;
	-webkit-overflow-scrolling: touch;
}
</style>

<!-- 넘겨받은 메세지가 있을 때만 자바스크립트를 실행해도록 할 것임 -->
<c:if test="${not empty message }">
	<script type="text/javascript">
		alert("${message}");	
	</script>
	<c:remove var="message" scope="request"/>
</c:if>

</head>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.7.0/jquery.min.js"></script>
<body>
	<header>
		<div class="collapse bg-dark" id="navbarHeader">
		</div>
		<div class="navbar navbar-dark bg-dark shadow-sm">
			<div class="container"></div>
		</div>
	</header>
	<main>
		<section class="py-1 text-center container" style="margin-top: 150px;">
			<div class="row py-lg-4">
				<div class="col-lg-6 col-md-8 mx-auto">
					<h1 class="fw-light">DDIT 로그인</h1>
				</div>
			</div>
		</section>
		<section class="py-1 text-center container">
		
				<div class="login-box mx-auto" style="width: 600px;"> 
					<div class="card">
						<div class="card-body login-card-body"> 
							<h2 class="login-box-msg">  
								<b>DDIT</b> LOGIN
							</h2>
				
							<form action="/loginCheck.do" method="post" id="signForm">
								<div class="input-group mb-3">
									<input type="text" class="form-control" name="userId" id="userId" placeholder="아이디를 입력해주세요">
								</div> 
								<div class="input-group mb-3"> 
									<input type="password" class="form-control" name="userPw" id="userPw" placeholder="비밀번호를 입력해주세요">
								</div>  
									<button type="button" id="signinBtn" class="btn btn-primary">로그인</button>  
									<button type="button" class="btn btn-info" onclick="location.href='/ddit/board/signup.do'">회원가입</button>
							</form> 
				
						</div>
					</div>
				</div>
			
		</section>
	</main>
	<script src="${pageContext.request.contextPath }/resources/assets/dist/js/bootstrap.bundle.min.js"></script>
</body>

<script>
$(function() {
	let signinBtn = $('#signinBtn');	// 로그인 버튼
	let signForm = $('#signForm');		// 로그인 Form

	signinBtn.on('click', function() {
		let userId = $('#userId').val();	// 아이디 값
		let userPw = $('#userPw').val();	// 비밀번호 값
		
 		if (userId == null || userId == "") {
			alert("아이디를 입력해주세요!");
			return false;
		}
		
		if (userPw == null || userPw == "") {
			alert("비밀번호를 입력해주세요!");
			return false;
		} 
		signForm.submit();
	});
	
});
</script>