(function () {
    var WS_URL = "ws://localhost:8082/ws";

    var canvas = document.getElementById("canvas");
    var buffer = document.createElement("canvas");

    var users = [];

//    canvas.width = document.body.clientWidth;
    canvas.width = 1904;
    buffer.width = 1904;
//    canvas.height = document.body.clientHeight;
    canvas.height = 952;
    buffer.height = 952;
    canvasWidth = canvas.width;
    canvasHeight = canvas.height;

    var ctx = canvas.getContext("2d");
    var bufferCtx = buffer.getContext("2d");

    var socket = new WebSocket(WS_URL);
    socket.onopen = function (e) {
        joinGame("Test")
    }
    canvas.addEventListener('click', function (event) {
        var x = event.pageX - canvas.offsetLeft,
            y = event.pageY - canvas.offsetTop;
        sendPosition(x, y);
    }, false);

    socket.onmessage = function (e) {
        var data = e.data.split(":");
        var cmd = data[0];
        switch (cmd) {
            case "0": {
                var positions = data[1].split(';');
                users = [];
                positions.forEach(function (p) {
                    var coords = p.split(',');
                    users.push({'x': coords[0], 'y': coords[1]});
                });
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

    function draw() {
        bufferCtx.clearRect(0, 0, canvasWidth, canvasHeight);
        for (var i = 0; i < users.length; i++) {
            bufferCtx.beginPath();
            bufferCtx.arc(users[i].x, users[i].y, 20, 0, 2 * Math.PI);
            bufferCtx.stroke();
        }
        ctx.clearRect(0, 0, canvasWidth, canvasHeight);
        ctx.drawImage(buffer, 0, 0);

        window.requestAnimationFrame(draw);
    }

    window.requestAnimationFrame(draw);
})();