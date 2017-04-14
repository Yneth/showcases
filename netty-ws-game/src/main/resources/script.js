(function () {
    var WS_URL = "ws://localhost:8082/ws";

    var canvas = document.getElementById("canvas");

//    canvas.width = document.body.clientWidth;
    canvas.width = 1904;
//    canvas.height = document.body.clientHeight;
    canvas.height = 952;
    canvasWidth = canvas.width;
    canvasHeight = canvas.height;

    var ctx = canvas.getContext("2d");

    var socket = new WebSocket(WS_URL);
    socket.onopen = function (e) {
        joinGame("Test")
    }
    socket.onmessage = function (e) {
        var data = e.data.split(":");
        var cmd = data[0];
        switch (cmd) {
            case "0": {
                var userPositions = data[1].split(';');
                ctx.clearRect(0, 0, canvasWidth, canvasHeight);
                for (var i = 0; i < userPositions.length; i++) {
                    var userPos = userPositions[i].split(',');
                    ctx.beginPath();
                    ctx.arc(+userPos[0], +userPos[1], 20, 0, 2 * Math.PI);
                    console.log(userPos[0] + "   " + userPos[1]);
                    ctx.stroke();
                }
                break;
            }
        }
    }

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