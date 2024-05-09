var stompClient = null;
const MAX_VALUE = 100;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#messages").html("");
}

function setStarted(started) {
    $("#start").prop("disabled", started);
}

function connect() {
    var socket = new SockJS('/game');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        // I will subscribe to game messages to be notified about the game status.
        stompClient.subscribe('/topic/messages', function (message) {
        	showMessage(JSON.parse(message.body).content);
        });
        stompClient.subscribe('/user/queue/errors', function (message) {
            showMessage(JSON.parse(message.body).content);
        });
        // If the other player start the game or I start the game I will get notifications here. The Game board will be handle according this subscription.
        stompClient.subscribe('/topic/game', handleGameEvent);
        stompClient.subscribe('/queue/connect/player1', handleGameEvent);
        stompClient.send("/app/connect/player1", {}, {});
    });
}

function disconnect() {
    if (stompClient != null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function start() {
    var randomNumber = Math.floor(Math.random() * (MAX_VALUE - 2 + 1)) + 2;
    var payload = JSON.stringify({
        startingPlayer: "player1",
        initialNumber: randomNumber,
        mode: "manual"
    });
    setStarted(true);
    stompClient.send("/app/start", {}, payload);
}

function handleGameEvent(message) {
    var data = JSON.parse(message.body);
    var currentValue = parseInt(data.currentValue);
    var turn = data.turn;

    displayCurrentNumber(currentValue);
    if (turn === "player1") {
        displayButtons();
    } else if (turn === "player2") {
        displayOpponentTurnMessage();
    }
}

function displayCurrentNumber(number) {
    // Assuming you have a DOM element with id "currentNumber" to display the number
    $("#currentNumber").text(number);
}

function displayButtons() {
    // Show the buttons for player 1
    $("#buttons").css("display", "block");
}

function displayOpponentTurnMessage() {
    // Display message for player 2
    $("#opponentMessage").text("It's the turn of your opponent.");
}

function sendMessage() {
    stompClient.send("/app/chat", {}, JSON.stringify({'content': $("#content").val()}));
}

function move(pileId) {
    stompClient.send("/app/move", {}, pileId);
}

function showMessage(message) {
    $("#messages").append("<tr><td>" + message + "</td></tr>");
}

function drawGameBoard(message) {
    $("#gameStatus").text(message.gameStatus);
    $("#winner").text(message.winner);
    piles = message.piles;
    for (i=0; i<14; i++) {
    	$("#"+i).text(piles[i]);
    }
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendMessage(); });
    $( "#start" ).click(function() { start(); });
});
