const { ipcRenderer } = require("electron"); //, remote

// WebSocket connection
console.log("Trying to connect to websocket server");
var wsClient = new WebSocket("ws://localhost:9000"); //open on localhost port 9000

wsClient.addEventListener("open", event => {
    //send message on connection opened
    console.log("Connected to ws server");
});

wsClient.addEventListener("message", event => {
    //log message received
    console.log("Received from server:");
    console.log(JSON.parse(event.data));
});

// REMINDER: Handle socket close at some point!!!

ipcRenderer.on("sendToServer", (event, msg) => {
    wsClient.send(JSON.stringify(msg));
});

document.querySelector(".quit").addEventListener("click", () => {
    ipcRenderer.send("quit", event);
});
