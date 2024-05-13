# Game Of Three


## Overview

Find development decisions [here](NOTES.md).

<p>Game of three is a very simple game played between two players.</p>
<p>The game starts with a random number and the players take turns to add one of {-1, 0, 1} to the number and then divide the number by 3.</p>
<p>The player who reaches the number 1 after the division wins the game.</p>

## Getting Started

### Prerequisites

Ensure you have the following tools installed on your machine:

- [Docker](https://www.docker.com/get-started): Containerization platform for building and sharing applications.

### Running the game server

1. **Clone the repository:**

    ```bash
    git clone https://github.com/victorvld/game-of-three.git
    cd game-of-three
    ```

2. **Deploy the application:**

   Build the docker image by running the following command:
   ```bash 
   docker build -t game-of-three . 
   ```
   Start the application by running the following command:
   ```bash 
   docker run -p 8080:8080 game-of-three 
   ```

3. **Players GUI**

    - Click [here](http://localhost:8080/index1.html?) to open Player1's GUI.
    - Click [here](http://localhost:8080/index2.html?) to open Player2's GUI.

## Usage

Congratulations! You are now ready to play the game, follow the steps below:
1. Connect Player1 to server by clicking on the `Connect` button.
2. Connect Player2 to server by clicking on the `Connect` button.
3. Start the game by clicking on the `Start New Game` button either in Player1 or Player 2 GUI.
4. Select Game Mode by clicking on the `Auto` or `Manual` button in the player GUI. (Auto mode will automatically make a move for the player)
5. Make a move by clicking on the `1, 0, -1` button in Player1 or Player2 GUI. 
6. Iterate the steps 4 until the game is over. 
7. Click on `Reload` to refresh the game and follow the previous steps if you want to play again.
