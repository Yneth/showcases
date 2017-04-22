(function () {
    'use strict';
    var WS_PROTOCOL = location.protocol === 'https:' ? 'wss' : 'ws';
    var WS_URL = WS_PROTOCOL + '://' + location.hostname + (location.port ? ':' + location.port : '') + '/ws';
    var canvas = document.getElementById('canvas');
    var buffer = document.createElement('canvas');

    var users = [],
        bullets = [];

    canvas.width = document.body.clientWidth;
    buffer.width = document.body.clientWidth;

    canvas.height = document.body.clientHeight;
    buffer.height = document.body.clientHeight;

    var canvasWidth = canvas.width,
        canvasHeight = canvas.height,
        cameraScale = Math.min(canvasWidth, canvasHeight),
        boundingRect = canvas.getBoundingClientRect();

    var ctx = canvas.getContext('2d');

    var socket = new WebSocket(WS_URL);
    socket.binaryType = 'arraybuffer';
    socket.onopen = function (e) {
        joinGame('Test' + Math.random() * 100);
    };
    canvas.addEventListener('click', function (event) {
        // http://www.html5canvastutorials.com/advanced/html5-canvas-mouse-coordinates/
        var rect = boundingRect;
        var x = Math.round((event.clientX - rect.left) / (rect.right - rect.left) * canvasWidth),
            y = Math.round((event.clientY - rect.top) / (rect.bottom - rect.top) * canvasHeight);
        var worldPos = toWorld(x, y);
        sendPosition(worldPos.x, worldPos.y);
    }, false);

    socket.onmessage = function (e) {
        var data = new Uint8Array(e.data);
        if (data[0] != 0) {
            return;
        }
        users = [];
        bullets = [];
        var i;
        var playerCount = data[2];
        for (i = 3; i < playerCount * 4 + 2; i += 4) {
            var x = (data[i] << 8) | data[i + 1];
            var y = (data[i + 2] << 8) | data[i + 3];

            users.push(toViewport(x, y));
        }
        for (; i < data.length - 3; i += 4) {
            var x = (data[i] << 8) | data[i + 1];
            var y = (data[i + 2] << 8) | data[i + 3];

            bullets.push(toViewport(x, y));
        }
    };

    function joinGame(username) {
        var packet = new Uint8Array(22);
        if (username.length > 21) {
            username = username.slice(0, 21);
        }
        packet[0] = 100;
        packet.set(username, 1);

        socket.send(packet);
    }

    function leaveGame() {
        socket.send('leave:');
    }

    function sendPosition(x, y) {
        socket.send(new Uint8Array([0, x >> 8, x & 0xFF, y >> 8, y & 0xFF]));
    }

    function shoot() {
        socket.send(new Uint8Array([1]));
    }

    function draw() {
        ctx.clearRect(0, 0, canvasWidth, canvasHeight);
        for (var i = 0; i < users.length; i++) {
            ctx.beginPath();
            ctx.arc(users[i].x, users[i].y, 20, 0, 2 * Math.PI);
            ctx.stroke();
        }
        for (var i = 0; i < bullets.length; i++) {
            ctx.beginPath();
            ctx.arc(bullets[i].x, bullets[i].y, 5, 0, 2 * Math.PI);
            ctx.stroke();
        }
        window.requestAnimationFrame(draw);
    }

    window.requestAnimationFrame(draw);

    window.addEventListener('keypress', function () {
        shoot();
    });

    function toWorld(x, y) {
        x = x + (cameraScale / 2) - (cameraScale / 2); // add camera pos AND subtract viewport offset
        x = x / (cameraScale * 2); // divide by viewport size IE normalize
        x = Math.round(x * 1000); // multiply to server coords

        y = y + (cameraScale / 2) - (cameraScale / 2);
        y = y / (cameraScale * 2);
        y = Math.round(y * 1000);
        return {'x': x, 'y': y};
    }

    function toViewport(x, y) {
        x = x / 10000; // normalize
        x = x * 2 * cameraScale; // to world viewport size
        x = x - (cameraScale / 2); // to camera pos
        x = x + (cameraScale / 2); // add viewport offset

        y = y / 10000; // normalize
        y = y * 2 * cameraScale; // to world viewport size
        y = y - (cameraScale / 2); // to camera pos
        y = y + (cameraScale / 2); // add viewport offset
        return {'x': x, 'y': y};
    }
})();