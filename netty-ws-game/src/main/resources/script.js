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
        canvasHeight = canvas.height;

    var ctx = canvas.getContext('2d');

    var socket = new WebSocket(WS_URL);
    socket.onopen = function (e) {
        joinGame('Test' + Math.random());
    };
    canvas.addEventListener('click', function (event) {
        var x = event.pageX - canvas.offsetLeft,
            y = event.pageY - canvas.offsetTop;
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
                    users.push({'x': coords[0], 'y': coords[1]});
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