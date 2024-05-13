# Code Challenge Decision-Making

---

Please find below the main decisions I have made during the development of the code challenge,
don't hesitate to ask me any questions about them.

### 1. Communication Protocol Selection
- **Objective**: Determine the appropriate communication protocol for client-server interaction.
- **Decision**: WebSocket over REST
    - **Reasoning**:  I found bidirectional communication between client and server more appropriate for the context of a game.

### 2. Integration of STOMP Messages
- **Objective**: Determine what was the best option to integrate websockets into the spring boot application.
- **Decision**: I found STOMP messages very useful for the integration. 
    - **Reasoning**: STOMP messages are a simple and easy-to-use protocol for working with WebSockets.
    - It provides out-of-the-box support functionality for subscribing and sending messages.
    - I specially liked the Annotated Controller to handle messages.

### 3. Client Interface Selection
- **Objective**: Choose an appropriate interface for the game client.
- **Decision**: Transition from CLI to GUI
    - **Reasoning**: CLI proved overly complex due to multithreading, thus opted for GUI simplicity.

### 4. GUI Development
- **Objective**: Create a user-friendly interface for the game.
- **Action**: Utilize JavaScript for GUI construction.
- **Reasoning**: JS chosen for its simplicity and the size of the program.

### 5. Data Management Strategy
- **Objective**: Determine the strategy for managing game data.
- **Decision**: In-Memory Game State
    - **Reasoning**: Opted for simplicity and time-saving, bypassing event sourcing and CQRS for direct in-memory storage.

---
