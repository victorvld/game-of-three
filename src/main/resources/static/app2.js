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
        stompClient.subscribe('/queue/connect/player2', handleGameEvent);
        stompClient.send("/app/connect/player2", {}, {});
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
        startingPlayer: "player2",
        initialNumber: randomNumber,
        mode: "manual"
    });
    setStarted(true);
    stompClient.send("/app/start", {}, payload);
}

function move(move) {
    var payload = JSON.stringify({
        move: move,
    });
    hideButtons();
    displayOpponentTurnMessage();
    stompClient.send("/app/move", {}, payload);
}

function handleGameEvent(message) {
    var data = JSON.parse(message.body);
    var currentNumber = data.currentNumber;
    var playerTurn = data.playerTurn;
    displayCurrentNumber(currentNumber);
    var winner = data.winner;
    if (winner !== null) {
        displayWinnerMessage(winner);
        displayReloadGame();
    } else if (playerTurn === "player2") {
        displayButtons();
        displayYourTurnMessage();
    } else if (playerTurn === "player1") {
        displayOpponentTurnMessage();
        hideButtons();
    }
}

function displayCurrentNumber(currentNumber) {
    $("#currentNumber").text(currentNumber);
}

function displayButtons() {
    $("#moveButtonsRow").show()
}

function hideButtons() {
    $("#moveButtonsRow").hide()
}

function displayYourTurnMessage() {
    $("#boardMessage").text("It's your turn selec {-1,0,1} so that the current number is divisible by 3.");
}

function displayOpponentTurnMessage() {
    $("#boardMessage").text("It's the turn of your opponent.");
}

function displayWinnerMessage(winner) {
    $("#boardMessage").text("Game Concluded. The winner is " + winner);
}

function displayReloadGame() {
    $("#reloadGameRow").show();
}

function sendMessage() {
    stompClient.send("/app/chat", {}, JSON.stringify({'content': $("#content").val()}));
}

function showMessage(message) {
    $("#messages").append("<tr><td>" + message + "</td></tr>");
}

function reloadGame() {
    location.reload();
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendMessage(); });
    $( "#start" ).click(function() { start(); });
    $( "#reloadGame" ).click(function() { reloadGame(); });
});
