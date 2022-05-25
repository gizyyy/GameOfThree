var stompClient = null;

function setConnected(connected) {
	$("#connect").prop("disabled", connected);
	$("#disconnect").prop("disabled", !connected);
	if (connected) {
		$("#conversation").show();
		$("#nameArea").show();
	}
	else {
		$("#conversation").hide();
		$("#nameArea").hide();
	}
	$("#logs").html("");
}

function connect() {
	var socket = new SockJS('/gs-guide-websocket');
	stompClient = Stomp.over(socket);
	stompClient.connect({}, function(frame) {
		setConnected(true);
		console.log('Connected: ' + frame);
		stompClient.subscribe('/topic/logs', function(message) {
			showLog(message.body);
		});
		stompClient.subscribe('/user/queue/special', function(message) {
			const obj = JSON.parse(message.body);
			showSpecial(obj.message);
			if (obj.turn !== 'DONE') {
				$("#turn").val(obj.turn);
				var isRobot = $("#robotPlayer").val();
				if (obj.message === "Number is being generated") {
					$("#role").val("PLAYER1");
					if (obj.playerName !== '') {
						$("#playerNameDisplayArea").text(obj.playerName);
					}
					$("#playerNameDisplayArea").attr("style", "display:block");
				} else if (obj.message === "Please wait") {
					$("#role").val("PLAYER2");
					if (obj.playerName !== '') {
						$("#playerNameDisplayArea").text(obj.playerName);
					}
					$("#playerNameDisplayArea").attr("style", "display:block");

					if (obj.turn === "PLAYER2S_TURN") {
						$("#guessAreaWithButtons").attr("style", "display:block");
					} else {
						$("#guessAreaWithButtons").attr("style", "display:none");
					}
				} else {
					if (obj.turn === "PLAYER1S_TURN" && $("#role").val() === "PLAYER1") {
						if (isRobot) {
							stompClient.send("/app/play", {}, JSON.stringify({ 'move': null }));
						} else {
							$("#guessAreaWithButtons").attr("style", "display:block");
						}
					}
					else if (obj.turn === "PLAYER2S_TURN" && $("#role").val() === "PLAYER2") {
						if (isRobot) {
							stompClient.send("/app/play", {}, JSON.stringify({ 'move': null }));
						} else {
							$("#guessAreaWithButtons").attr("style", "display:block");
						}
					} else {
						$("#guessAreaWithButtons").attr("style", "display:none");
					}
				}
			}
		});
	});
}

function disconnect() {
	if (stompClient !== null) {
		stompClient.disconnect();
	}
	setConnected(false);
	console.log("Disconnected");
	$("#specialArea").empty();
	$("#logs").empty();
	$("#playerNameDisplayArea").text("");
	$("#playerNameDisplayArea").attr("style", "display:none");
	$("#turn").val("");
	$("#role").val("");
}

function sendName() {
	var robot = false;
	if ($('#robot').is(":checked")) {
		robot = true;
		$("#robotPlayer").val(robot);
	}
	stompClient.send("/app/register", {}, JSON.stringify({ 'name': $("#name").val(), 'robot': robot }));
	$("#nameArea").attr("style", "display:none");
}

function sendButtonInput(message) {
	stompClient.send("/app/play", {}, JSON.stringify({ 'move': message }));
	$("#guessAreaWithButtons").attr("style", "display:block");
}

function showLog(message) {
	$("#logs").append("<tr><td>" + message + "</td></tr>");
	if ($("#role").val() === '' || $("#role").val() === 'PLAYER1') {
		stompClient.send("/app/log", {}, message);
	}

}

function showSpecial(message) {
	$("#specialArea").append("<tr><td>" + message + "</td></tr>");
}

$(function() {
	$("form").on('submit', function(e) {
		e.preventDefault();
	});
	$("#connect").click(function() { connect(); });
	$("#disconnect").click(function() { disconnect(); });
	$("#send").click(function() { sendName(); });
	$("#minusOne").click(function() { sendButtonInput("MINUSONE"); });
	$("#zero").click(function() { sendButtonInput("ZERO"); });
	$("#plusOne").click(function() { sendButtonInput("PLUSONE"); });
});

