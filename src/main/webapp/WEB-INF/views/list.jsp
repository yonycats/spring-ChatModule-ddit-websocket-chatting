<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
    
<!doctype html>
<html>
<head>
<title>DDIT BOARD LIST</title>

<link href="${pageContext.request.contextPath }/resources/assets/dist/css/bootstrap.min.css" rel="stylesheet">
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.7.0/jquery.min.js"></script>

<style>
.display-none {
     display: none;
}

#chatRoom {
    height: 700px; /* 원하는 높이 설정 */
}

#chatMsgBody {
    flex: 1; /* 남은 공간을 차지하도록 설정 */
    max-height: 500px; /* 최대 높이 설정 */
    overflow-y: auto;   /* 수직 스크롤 활성화 */
    padding: 10px;      /* 패딩 추가 (옵션) */
    border: 1px solid #ddd; /* 테두리 추가 (옵션) */
    border-radius: 5px; /* 모서리 둥글게 (옵션) */
}
/* 스크롤바 스타일 */
#chatMsgBody::-webkit-scrollbar {
    width: 8px; /* 스크롤바의 너비 */
}

#chatMsgBody::-webkit-scrollbar-track {
    background: #f1f1f1; /* 스크롤바 트랙 색상 */
    border-radius: 10px; /* 트랙의 모서리 둥글게 */
}

#chatMsgBody::-webkit-scrollbar-thumb {
    background: #b4f1b7; /* 스크롤바 색상 */
    border-radius: 10px; /* 스크롤바의 모서리 둥글게 */
}

#chatMsgBody::-webkit-scrollbar-thumb:hover {
    background: #99d478; /* 스크롤바를 hover 했을 때 색상 */
}
</style>

</head>
<body>
	<header>
		<div class="collapse bg-dark" id="navbarHeader">
		</div>
		<div class="navbar navbar-dark bg-dark shadow-sm">
			<div class="ms-5 d-flex align-items-center" style="color: #F5FFFA;">
				<div class="ms-3 me-3">아이디 : ${memberVO.userId }</div>
				<div class="ms-3 me-3">이름 : ${memberVO.userName }</div> 
				<div class="ms-3 me-3">
					<form action="/logout.do" method="get">
						<input class="btn btn-primary" type="submit" value="로그아웃">
					</form>
				</div>
			</div>
		</div>
	</header>
	<main>

		<section class="py-1 text-center container" style="margin-top: 10px;">
			<div class="row py-lg-4">
				<div class="col-lg-6 col-md-8 mx-auto">
					<h1 class="fw-light">DDIT 채팅</h1>
				</div>
			</div>
		</section>
		
		
		<div class="row ms-2 me-2">
		
			<!-- 사원 목록 섹션 -->
			<section class="text-center container col-md-3">
			  	<input class="form-control" id="myInput" type="text" placeholder="Search..">
  				<br>
				<table class="table table-bordered"> 
					<thead class="table-dark"> 
						<tr>
							<th style="width: 10%">이름</th>
							<th style="width: 20%">부서</th>
							<th style="width: 10%"></th>
						</tr>
					</thead>
					<tbody id="myTable">
						<c:choose>
							<c:when test="${empty memberList }">
								<tr>
									<td colspan="3">사원이 없습니다.</td>									
								</tr>
							</c:when>
							<c:otherwise>
								<c:forEach items="${memberList }" var="memberVO">
									<tr>
										<td>${memberVO.userName }</td>
										<td>${memberVO.userDep }</td>
										<td><button class="btn btn-info" onclick="f_chatRoomAdd(${memberVO.userNo })">채팅</button></td>
									</tr>
								</c:forEach>
							</c:otherwise>	
						</c:choose>
					</tbody>
				</table>
			</section>
			<!-- 사원 목록 섹션 끝 -->
			
			<!-- 채팅방 리스트 섹션 -->
			<section class="text-center container col-md-6">
					<div class="login-box mx-auto"> 
						<div class="card">
							<div class="card-body login-card-body"> 
								<h2 class="login-box-msg">  
									<b>DDIT</b> 채팅방 리스트
								</h2>
									
									<table class="table table-bordered table-hover"> 
										<thead class="table-dark">
											<tr>
												<th style="width: 10%">프로필</th>
												<th style="width: 20%">방 이름</th>
												<th style="width: 10%">인원</th>
												<th style="width: 35%">최근 메시지</th>
												<th style="width: 25%">최근 날짜</th>
											</tr>
										</thead>
										<tbody id="chatRoomList"> 
											<c:choose>
												<c:when test="${empty roomList }">
													<tr>
														<td colspan="5">참여 중인 채팅방이 없습니다.</td>									
													</tr>
												</c:when>
												<c:otherwise>
													<c:forEach items="${roomList }" var="ChatRoomVO">
														<tr ondblclick="f_chat(${ChatRoomVO.roomNo })">
															<td><img width="60px" height="60px" src="${pageContext.request.contextPath}/resources/profile/${ChatRoomVO.roomImg }"></td>
															<td>${ChatRoomVO.roomName }
																<c:if test="${ChatRoomVO.unReadCount != 0 }">
																	<span class="badge bg-danger rounded-pill">${ChatRoomVO.unReadCount }</span>
																</c:if>
															</td>
															<td>${ChatRoomVO.roomPeople }</td>
															<c:choose>
																<c:when test="${empty ChatRoomVO.chatMsg }">
																	<td>메시지가 없습니다.</td>
																</c:when>
																<c:otherwise>
																	<td>${ChatRoomVO.chatMsg }</td>
																</c:otherwise>
															</c:choose>
															<td>${ChatRoomVO.chatDate }</td>
														</tr>
													</c:forEach>
												</c:otherwise>	
											</c:choose>
										</tbody>
									</table>
					
							</div>
						</div>
					</div>
			</section>
			<!-- 채팅방 리스트 섹션 끝 -->
			
			<!-- 채팅 섹션 -->
			<section class="text-center container col-md-3 display-none" id="chatRoom">
				<div class="card" style="height: 700px;">
					<div class="card-header"> 
						<div class="card-title">
							<div style="display: flex; justify-content: end;">
								<button id="close" class="btn btn-warning me-3" onclick="f_close()">닫기</button>
								<button id="close" class="btn btn-danger" onclick="f_exit()">채팅방 나가기</button>
							</div>
						</div>
						<div class="card-toolbar">
						</div>
					</div>
					
					<!-- 선택한 채팅방의 메시지를 동적으로 넣어줄 부분 -->
					<div class="card-body" id="chatMsgBody">
					</div>
					
					<div class="card-footer pt-4">
						<div id="chatMsg">
							<textarea class="form-control form-control-flush mb-3" rows="3" placeholder="채팅 메시지를 적어주세요"></textarea>
						</div>
					
						<div class="d-flex flex-stack">
							<div class="d-flex align-items-center me-2">
								<button class="btn btn-sm btn-icon btn-active-light-primary me-1" type="button" value="D"></button>
							</div>
						</div>
						<button id="chatSendBtn" class="btn btn-primary" type="button" data-kt-element="send">Send</button> 
					</div>
					
				</div>
			</section>
			<!-- 채팅 섹션 끝 -->
			
		</div>
		
	</main>
	<script src="${pageContext.request.contextPath }/resources/assets/dist/js/bootstrap.bundle.min.js"></script>
</body>

<script>
//------------------------------chat에 대한 script 시작------------------------------------------------------------------
//웹소켓
var websocket = null;
connect();

//채팅방 입장을 했을 때 호출되는 함수
function connect() {
	// 웹소켓 주소
	var wsUri = "ws://${pageContext.request.serverName}:${pageContext.request.serverPort}${pageContext.request.contextPath}/websocket/chat.do";
	// 소켓 객체 생성
	websocket = new WebSocket(wsUri);
	//웹 소켓에 이벤트가 발생했을 때 호출될 함수 등록 (오버라이딩)
	websocket.onopen = function(){
		console.log('info: connection opened.');
	}
}
websocket.onclose = function() {
    console.log('웹소켓 연결이 끊어졌습니다. 재연결합니다.');
    setTimeout(connect, 2000); // 3초 후 재연결 시도
};

//------------------------------chat에 대한 script 끝------------------------------------------------------------------

let roomNo = 0; // 글로벌 변수로 roomNo 선언
let userNo = 0; // 글로벌 변수로 userNo 선언

// 선택한 채팅방의 메시지를 동적으로 넣어줄 부분
let chatMsgBody = $('#chatMsgBody');

$(function() {
	// list.jsp 관련 메소드 - 사람 목록 검색하기
	$("#myInput").on("keyup", function() {
		var value = $(this).val().toLowerCase();
		$("#myTable tr").filter(function() {
			$(this).toggle($(this).text().toLowerCase().indexOf(value) > -1)
		});
	});
	
	// list.jsp 관련 메소드 - enter 누르면 메시지 전송
	$(document).on('keydown', '#chatMsg textarea', function(e){
		// enter와 shift키와 동시에 눌리지 않았을 때 동작 > 둘이 눌린다면 줄바꿈을 함, 엔터만 눌린다면 메시지를 보냄
	    if(e.keyCode == 13 && !e.shiftKey) { 
	        e.preventDefault(); // 엔터키가 입력되는 것을 막아준다. enter누를 때 줄바꿈 동작 막음
	     	// * 1 메시지 전송 (Enter를 눌렀을 때)
	        sendChatMessage();
	    }
	});
	
	// list.jsp 관련 메소드 - send 버튼을 누르면 메시지 전송
	$(document).on('click', '#chatSendBtn', function() {
		// * 1 메시지 전송 (Send 버튼을 눌렀을 때)
	    sendChatMessage();
	});

	// 웹소켓 관련 메소드 - 채팅 메시지 받기
	// * 2 메세지 수신 (웹소켓 서버로부터 메시지를 수신하는 함수)
	websocket.onmessage = function(evt) {
		 	
		    if(evt.data == "reload"){
		    	// 메시지 수신하면 채팅방 목록 다시 불러오기
		    	getChatRoomList();
		    	// 메시지를 수신하면 해당 채팅방의 메시지 목록 다시 불러오기
		    	chatMessageList(roomNo)
		    } else {
			 	let receive = evt.data.split(","); // evt.data 서버에서 전송된 메시지 데이터
		        
		        const chatVO = {
	                "userName" : receive[0],
	                "userNo" : receive[1],
		            "chatMsg" : receive[2],
		            "chatDate" : receive[3],
		            "unReadCount" : receive[4]
		        };
			 	// 내가 쓴 메시지인지 아닌지 구분하기 (왼쪽, 오른쪽 판별)
		        CheckLR(chatVO);
		    }
	}
});

// 웹소켓 관련 메소드 - 채팅 메시지 전송하기
// * 1 메시지 전송
function sendChatMessage(){
    let message = $('#chatMsg textarea').val();  // 현재 입력된 메세지를 담는다.
    
    // 공백 및 공백 문자열을 제거하고, 
    // 입력된 메시지가 공백 또는 문자열로만 이루어져 있을 때 함수 실행 멈춤 (return)
    // 정규 표현식 : /\s+/g로 모든 종류의 공백(스페이스, 탭 등)을 포함하여 연속된 공백도 하나의 공백으로 처리
    if(message.replace(/\s+/g, "").length === 0) {
        $('#chatMsg textarea').focus();
        return false;
    }
	
 	// 서버에 전달할 데이터 세팅
	const data = {
			 "roomNo" : roomNo,		// 글로벌 변수에서 roomNo 가져오기
			 "userName" : "${sessionScope.SessionInfo.userName}",
			 "userNo" : "${sessionScope.SessionInfo.userNo}",
			 "chatMsg" : message,
			 "type" : "msg"	// 데이터가 메시지일 때의 구분자
	 };
	
	// websocket 서버에 해당 data 전송
	websocket.send(JSON.stringify(data));
	
    // 메시지 보낸 후, 채팅 입력 textarea 비우기
    clearTextarea();
};

// 웹소켓 관련 메소드 - 선택한 채팅방 입장하기
// * 1 메시지 전송
function f_chat(chatRoomNo) {
	// 입장하면 display-none 클래스 제거
	$('#chatRoom').removeClass("display-none");
	
	chatMsgBody.html(""); // 기존 메시지 초기화
	
	console.log(chatRoomNo);
	roomNo = chatRoomNo;	// 선택한 roomNo를 글로벌 변수에 저장
	userNo = ${memberVO.userNo };
	
	// 방에 입장 시 해당 정보 서버에 전달
	const roomData = {
		 "roomNo" : roomNo,
		 "userName" : "${sessionScope.SessionInfo.userName}",
		 "userNo" : "${sessionScope.SessionInfo.userNo}",
		 "type" : "enter-room"
	}
	websocket.send(JSON.stringify(roomData));
};

// 웹소켓 관련 메소드 - 선택한 채팅방 닫기 버튼 누를 때
// * 1 메시지 전송
function f_close() {
	// 서버에 전달할 데이터 세팅 
	let roomData = {
		 "roomNo" : roomNo,
		 "userName" : "${sessionScope.SessionInfo.userName}",
		 "userNo" : "${sessionScope.SessionInfo.userNo}",
		 "type" : "close-room"	// 채팅방 닫기 했을 때의 구분자
	}
	// 웹소켓으로 데이터 보내기
	websocket.send(JSON.stringify(roomData));
	
	// 채팅방 숨기기
	$('#chatRoom').toggleClass("display-none");
};

// 웹소켓 관련 메소드 - 선택한 채팅방 나가기 버튼 누를 때
// * 1 메시지 전송
function f_exit() {
	// 웹소켓 연결이 되어있는지 확인
	if (websocket.readyState != WebSocket.OPEN) {
		connect();
	}
	
	// 서버에 전달할 데이터 세팅 
	let roomData = {
		 "roomNo" : roomNo,
		 "userName" : "${sessionScope.SessionInfo.userName}",
		 "userNo" : "${sessionScope.SessionInfo.userNo}",
		 "type" : "exit-room"	// 채팅방 나가기 했을 때의 구분자
	}
	// 웹소켓으로 데이터 보내기
	websocket.send(JSON.stringify(roomData));
	
	// 채팅방 숨기기
	$('#chatRoom').toggleClass("display-none");
	
	getChatRoomList();
};

// 웹소켓 관련 메소드 - 채팅방 메시지 목록 불러오기
function chatMessageList(roomNo){
	chatMsgBody.html(""); // 기존 메시지 초기화
	
	$.ajax({
		url: roomNo + "/chat.do",	// roomNo를 파라미터로 넣어 채팅방 메시지 불러오기
		type: "post",
		success: function(chatMsgList) {
			chatMsgList.forEach(function(chatVO) {
				// 내가 쓴 메시지인지 아닌지 구분하기 (왼쪽, 오른쪽 판별)
				CheckLR(chatVO);
			});
		}		
	});
};

// 웹소켓 관련 메소드 - 채팅방 목록 불러오기
function getChatRoomList() {
	console.log("getChatRoomList 들어옴");
	$.ajax({
		url: "/chat/chatList.do",
		type: "post",
		success: function(roomList) {
			// 채팅방 목록이 들어갈 위치 불러오기
			let chatRoomList = $('#chatRoomList');
			chatRoomList.html("");
			
			let html = "";
			
			roomList.forEach(function(ChatRoomVO) {
					html +=	"<tr ondblclick='f_chat(" + ChatRoomVO.roomNo + ")'>";
					html +=	"	<td><img width='60px' height='60px' src='${pageContext.request.contextPath}/resources/profile/" + ChatRoomVO.roomImg + "'></td>";
					html +=	"	<td>" + ChatRoomVO.roomName;
					if(ChatRoomVO.unReadCount > 0){
						html += "	<span class='badge bg-danger'>" + ChatRoomVO.unReadCount + "</span>";
					}
					html +=	"	</td>";
					html +=	"	<td>" + ChatRoomVO.roomPeople + "</td>";
					html +=	"	<td>" + ChatRoomVO.chatMsg + "</td>";
					html +=	"	<td>" + ChatRoomVO.chatDate + "</td>";
					html +=	"</tr>";
			});
			// 새로 reload한 채팅방 목록 다시 넣기
			chatRoomList.html(html);
		}
	});
}

// 웹소켓 관련 메소드 - 메시지를 보낸 사람이 자신인지 타인인지 체크해서 메시지 위치(왼쪽, 오른쪽) 분리하기
function CheckLR(chatVO) {
	// 메시지별 읽음 카운트 > 안읽음이 0이면 빈칸, 0이 아니면 숫자
	let unReadCount = "";
	if (chatVO.unReadCount != 0) {
		unReadCount = chatVO.unReadCount;
	}
	// chatDate 포맷팅 (시간 초 부분 자르기)
	let formattedChatDate = chatVO.chatDate.replace(/(\d{4}-\d{2}-\d{2} \d{2}:\d{2}).*/, '$1');
	
	if (chatVO.userNo == userNo) {	// 내가 쓴 메시지라면
		let html = "";
		html +=	"<div class='mt-3'>";
		html += "        <div class='d-flex flex-column align-items-end'>";
		html += "        <div class='fw-semibold' style='padding: 15px; border-radius: 5px; background-color: #f7e5d9;'>"; 
		html +=         	chatVO.chatMsg;
		html += "        </div>";
		html += "        <div>";
		html += 			unReadCount +" | "+ formattedChatDate;
		html += "        </div>";
		html += "    </div>";
		html += "</div>";
		
		chatMsgBody.append(html);
	} else {						// 다른 사람이 쓴 메시지라면
		let html = "";
		html +=	"<div class='d-flex justify-content-start mt-3' >";
		html += "    <div class='d-flex flex-column align-items-start'>";
	    html += "        <div class='d-flex align-items-center mb-2'>";
        html += "            <div><img width='30px' height='30px' src='${pageContext.request.contextPath}/resources/profile/profile.png'></div>";
        html += "            <div class='ms-3'>" + chatVO.userName + "</div>";
	    html += "        </div>";
	    html += "        <div class='fw-semibold' style='padding: 15px; border-radius: 5px; background-color: #d9edf7;'>";
	    html += "        	<div>";
	    html +=             	chatVO.chatMsg;      
		html += "            </div>";
	    html += "        </div>";
		html += "        	<div>";
		html += 				formattedChatDate +" | "+ unReadCount;
		html += "        	</div>";
	    html += "    </div>";
	    html += "</div>";
	    
	    chatMsgBody.append(html);
	}
	// 스크롤을 가장 아래로 고정하기
    chatMsgBody.scrollTop(chatMsgBody[0].scrollHeight);
};

// list.jsp 관련 메소드 - 1대 1 채팅방 새로 만들기
function f_chatRoomAdd(selectUserNo) {
	console.log(selectUserNo);
	let userNo = selectUserNo;
	let myNo = ${memberVO.userNo};
	
	if (userNo == myNo) {
		alert("자기 자신과는 1 대 1 채팅이 불가능합니다.");
		return false;
	}
	
	$.ajax({
		url: userNo + "/roomAdd.do",
		type: "post",
		success: function(map) {
			let chatRoomList = $('#chatRoomList');
			
			// 기존에 없던 채팅방이면 채팅방 리스트 다시 불러오기
			if (map.flag == 0) {	
				getChatRoomList();
			}
			// 기존에 있던 채팅방이면 기존의 채팅방 열기
			f_chat(map.roomNo);
		}
	});
}

// list.jsp 관련 메소드 - 메시지 보낸 후, 채팅 입력 textarea 비우기
function clearTextarea(){
	$('#chatMsg textarea').val("");
	return false;
};

</script>