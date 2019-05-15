const WebSocket = require("ws");

const wss = new WebSocket.Server({ port: 9000 });

console.log("Starting server...");

let mockScore = Math.floor(Math.random() * 100);

wss.on("connection", function connection(ws) {
    console.log("Client connected");
    ws.on("message", function incoming(message) {
        console.log("received:");
        let parsedMsg = JSON.parse(message);
        console.log(parsedMsg);

        // ws.send(message); // echo back to client

        // Send a high score update (for testing)
        if (parsedMsg.action == "addPlayer") {
            ws.send(
                JSON.stringify({
                    action: "highscoreUpdate",
                    id: parsedMsg.id,
                    score: mockScore
                })
            );
        }
        mockScore = Math.floor(Math.random() * 100);
    });

    ws.on("close", function close(ws) {
        console.log("Client disconnected");
    });
});
