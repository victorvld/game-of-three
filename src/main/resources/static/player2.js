var stompClient = null;
const MAX_VALUE = 100;
const AUTOMATIC_MODE_TIMEOUT = 2000;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    $("#start").prop("disabled", !connected);
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
    disableGameModeToggle();
}

function gameMode() {
    const label = $('#toggleSwitch').is(':checked') ? 'Manual' : 'Auto';
    $('.form-check-label').text(label);
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
        initialNumber: randomNumber
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
    disableStartButton();
    disableGameModeToggle();
    disableDisconnectButton();
    var resolution = data.resolution;
    if (resolution === "player2") {
        displayCurrentNumberAsWin(currentNumber);
        displayWinnerMessage(resolution);
        displayReloadGame();
    } else if (resolution === "player1") {
        displayCurrentNumberAsLose(currentNumber);
        displayWinnerMessage(resolution);
        displayReloadGame();
    } else if(resolution === "draw") {
        displayCurrentNumberAsDraw();
        displayDrawMessage();
        displayReloadGame();}
    else if (playerTurn === "player2") {
            if (!$('#toggleSwitch').is(':checked')) {
                displayCurrentNumber(currentNumber);
                displayYourTurnAutomaticModeMessage();
                var adjustedNumber = adjustToDivisibleByThree(currentNumber);
                setTimeout(function() {
                    move(adjustedNumber);
                }, AUTOMATIC_MODE_TIMEOUT);
            } else {
                displayCurrentNumber(currentNumber);
                displayButtons();
                displayYourTurnMessage();
            }
    } else if (playerTurn === "player1") {
        displayCurrentNumber(currentNumber);
        displayOpponentTurnMessage();
        hideButtons();
    }
}

function adjustToDivisibleByThree(number) {
    const remainder = number % 3;
    let adjustment = 0;
    if (remainder === 1) {
        adjustment = -1;
    } else if (remainder === 2) {
        adjustment = 1;
    }
    return adjustment;
}

function sendMessage() {
    stompClient.send("/app/chat", {}, JSON.stringify({'content': $("#content").val()}));
}

function reloadGame() {
    location.reload();
}

$(function () {
    $("#toggleSwitch").change(function() {
        if ($(this).is(':checked')) {
            $('.form-check-label').text('Manual');
        } else {
            $('.form-check-label').text('Auto');
        }
    });
    $("form").on('submit', function (e) { e.preventDefault(); });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendMessage(); });
    $( "#start" ).click(function() { start(); });
    $( "#reloadGame" ).click(function() { reloadGame(); });
});
