(function () {
    'use strict';
    var WS_PROTOCOL = location.protocol === 'https:' ? 'wss' : 'ws';
    var WS_URL = WS_PROTOCOL + '://' + location.hostname + (location.port ? ':' + location.port: '') + '/ws';
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
        cameraScale = Math.min(canvasWidth, canvasHeight);

    var ctx = canvas.getContext('2d');

    var socket = new WebSocket(WS_URL);
    socket.onopen = function (e) {
        joinGame('Test' + Math.random());
    };
    canvas.addEventListener('click', function (event) {
        // http://www.html5canvastutorials.com/advanced/html5-canvas-mouse-coordinates/
        var rect = canvas.getBoundingClientRect();
        var x = Math.round((event.clientX - rect.left) / (rect.right - rect.left) * canvasWidth),
            y = Math.round((event.clientY - rect.top) / (rect.bottom - rect.top) * canvasHeight);

        x = x + (cameraScale / 2) - (cameraScale / 2); // add camera pos AND subtract viewport offset
        x = x / (cameraScale * 2); // divide by viewport size IE normalize
        x = Math.round(x * 1000); // multiply to server coords
        y = y + (cameraScale / 2) - (cameraScale / 2);
        y = y / (cameraScale * 2);
        y = Math.round(y * 1000);
        console.log(x + ' ' + y);
        sendPosition(x, y);
    }, false);

    socket.onmessage = function (e) {
        var data = e.data.split(':');
        var cmd = data[0];
        switch (cmd) {
            case '0':
            {
                var positions = data[1].split('|'),
                    userPositions = positions[0].split(';'),
                    bulletPositions = positions[1].split(';');
                users = [];
                bullets = [];
                userPositions.forEach(function (p) {
                    var coords = p.split(',');

                    var x = coords[0] / 1000; // normalize
                    x = x * 2 * cameraScale; // to world viewport size
                    x = x - (cameraScale / 2); // to camera pos
                    x = x + (cameraScale / 2); // add viewport offset

                    var y = coords[1] / 1000; // normalize
                    y = y * 2 * cameraScale; // to world viewport size
                    y = y - (cameraScale / 2); // to camera pos
                    y = y + (cameraScale / 2); // add viewport offset
                    console.log(coords);
                    console.log(x + ',' + y);
                    users.push({'x': x, 'y': y});
                });
                bulletPositions.forEach(function (b) {
                    var coords = b.split(',');
                    bullets.push({'x': coords[0], 'y': coords[1]});
                });
                break;
            }
        }
    };

    function joinGame(username) {
        socket.send('join:' + username);
    }

    function leaveGame() {
        socket.send('leave:');
    }

    function sendPosition(x, y) {
        socket.send('0:' + x + ',' + y);
    }

    function shoot() {
        socket.send("1:");
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
})();