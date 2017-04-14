(function () {
    var WS_URL = "ws://localhost:8082/ws";

    var socket = new WebSocket(WS_URL);
    socket.onmessage = function (e) {
        console.log(e.data);
    }
    setTimeout(function () {
        joinGame("Test");
    }, 5000);

    var canvas = document.getElementById("canvas");

    canvas.width = document.body.clientWidth;
    canvas.height = document.body.clientHeight;
    canvasW = canvas.width;
    canvasH = canvas.height;

    var ctx = canvas.getContext("2d");
    setInterval(function () {
        ctx.beginPath();
        ctx.arc(100, 100, 40, 0, 2 * Math.PI);
        ctx.stroke();
    }, 30);

    function joinGame(username) {
        socket.send("join:" + username);
    }

    function leaveGame() {
        socket.send("leave:");
    }

    function sendPosition(x, y) {
        socket.send("0:" + x + "," + y);
    }
})();