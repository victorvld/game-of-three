function displayCurrentNumber(currentNumber) {
    $("#currentNumber").text(currentNumber);
}

function displayCurrentNumberAsWin() {
    $("#currentNumber").text("Win");
}

function displayCurrentNumberAsLose() {
    $("#currentNumber").text("Lose");
}

function displayCurrentNumberAsDraw() {
    $("#currentNumber").text("Draw");
}

function displayButtons() {
    $("#moveButtonsRow").css("visibility", "visible");
}

function hideButtons() {
    $("#moveButtonsRow").css("visibility", "hidden");
}

function disableStartButton() {
    $("#start").prop("disabled", true);
}

function disableGameModeToggle() {
    $("#toggleSwitch").prop("disabled", true);
}

function disableDisconnectButton() {
    $("#disconnect").prop("disabled", true);
}

function displayYourTurnMessage() {
    $("#boardMessage").text("Your turn: Choose a number from {-1, 0, 1} to make the current number divisible by 3.");
}

function displayYourTurnAutomaticModeMessage() {
    $("#boardMessage").text("Automatic mode: {-1, 0, 1} has been selected to make the current number divisible by 3.");
}

function displayOpponentTurnMessage() {
    $("#boardMessage").text("Opponent's turn: Waiting for their move.");
}

function displayWinnerMessage(resolution) {
    $("#boardMessage").text("Game Over: " + resolution + " is the winner!");
}

function displayDrawMessage() {
    $("#boardMessage").text("Game Over: It's a draw.");
}

function displayReloadGame() {
    $("#reloadGame").css("visibility", "visible");
}

function showMessage(message) {
    $("#messages").append("<tr><td>" + message + "</td></tr>");
}
