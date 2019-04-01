const WebSocket = require("ws");

const wss = new WebSocket.Server({ port: 9000 });

console.log("Starting server...");

wss.on("connection", function connection(ws) {
    console.log("Client connected");
    ws.on("message", function incoming(message) {
        console.log("received:");
        console.log(JSON.parse(message));

        ws.send(message); // echo back to client
    });
});
