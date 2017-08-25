(function () {
    'use strict';
    var WS_PROTOCOL = location.protocol === 'https:' ? 'wss' : 'ws';
    var WS_URL = WS_PROTOCOL + '://' + location.hostname + (location.port ? ':' + location.port : '') + '/ws';
    var canvas = document.getElementById('canvas');
    var buffer = document.createElement('canvas');

    canvas.width = document.body.clientWidth;
    buffer.width = document.body.clientWidth;

    canvas.height = document.body.clientHeight;
    buffer.height = document.body.clientHeight;



    var canvasWidth = canvas.width,
        canvasHeight = canvas.height,
        cameraScale = Math.min(canvasWidth, canvasHeight),
        boundingRect = canvas.getBoundingClientRect(),
        scale = Math.min(canvasWidth / 1000, canvasHeight / 1000);

    var // camera = {'x': 250, 'y': 250},
        users = [],
        bullets = [],
        walls = [];

    var ctx = canvas.getContext('2d');

    var camera = new Camera(ctx);

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

        var cmd = data[0];
        var i = 1;
        switch (cmd) {
            case 0:
            {
                var x = (data[i] << 8) | data[i + 1];
                var y = (data[i + 2] << 8) | data[i + 3];
                // camera.x = (x / 10000) * cameraScale;
                // camera.y = (y / 10000) * cameraScale;

                var vec = camera.worldToScreen(x, y);
                camera.moveTo(vec.x, vec.y);
                break;
            }
            case 1:
            {
                users = [];
                while (i < data.length) {
                    var x = (data[i] << 8) | data[i + 1];
                    var y = (data[i + 2] << 8) | data[i + 3];

                    var player = toViewport(x, y);

                    player.rotation = {};
                    player.rotation.x = data[i + 4];
                    player.rotation.y = data[i + 5];
                    if (player.rotation.x > 10) {
                        player.rotation.x = -(player.rotation.x & ~(1 << 5));
                    }
                    if (player.rotation.y > 10) {
                        player.rotation.y = -(player.rotation.y & ~(1 << 5));
                    }
                    player.rotation.x /= 10;
                    player.rotation.y /= 10;

                    users.push(player);
                    i += 6;
                }
                break;
            }
            case 2:
            {
                bullets = [];
                while (i < data.length) {
                    var x = sh2int(data[i], data[i + 1]);
                    var y = sh2int(data[i + 2], data[i + 3]);

                    bullets.push(toViewport(x, y));
                    i += 4;
                }
                break;
            }
            case 3:
            {
                walls = [];
                while (i < data.length) {
                    var x = sh2int(data[i], data[i + 1]);
                    var y = sh2int(data[i + 2], data[i + 3]);

                    var width = sh2int(data[i + 4], data[i + 5]);
                    var height = sh2int(data[i + 6], data[i + 7]);

                    var wall = toViewport(x, y);
                    var bounds = toViewport(width, height);
                    wall.width = bounds.x;
                    wall.height = bounds.y;
                    walls.push(wall);

                    i += 8;
                }
                break;
            }
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

        // ctx.save();
        // ctx.scale(scale, scale);
        // ctx.translate(-camera.x, -camera.y);
        // ctx.translate(canvasWidth * 0.5, canvasHeight * 0.5);
        camera.begin();

        for (var i = 0; i < users.length; i++) {
            ctx.beginPath();
            ctx.arc(users[i].x, users[i].y, 20, 0, 2 * Math.PI);
            ctx.stroke();

            ctx.beginPath();
            var headX = users[i].x + users[i].rotation.x * 20;
            var headY = users[i].y + users[i].rotation.y * 20;

            ctx.moveTo(headX, headY);
            ctx.lineTo(users[i].x, users[i].y);
            ctx.stroke();
        }
        for (var i = 0; i < bullets.length; i++) {
            ctx.beginPath();
            ctx.arc(bullets[i].x, bullets[i].y, 5, 0, 2 * Math.PI);
            ctx.stroke();
        }
        for (var i = 0; i < walls.length; i++) {
            var wall = walls[i];
            ctx.rect(wall.x - wall.width / 2, wall.y - wall.height / 2, wall.width, wall.height);
            ctx.stroke();
        }
        // ctx.restore();
        camera.end();
        window.requestAnimationFrame(draw);
    }

    window.requestAnimationFrame(draw);

    window.addEventListener('keypress', function () {
        shoot();
    });

    function toWorld(x, y) {
        // x = x + camera.x;
        // x = x / (cameraScale * 2); // divide by viewport scale IE normalize
        // x = Math.round(x * 1000); // multiply to server coords
        //
        // y = y + camera.y;
        // y = y / (cameraScale * 2);
        // y = Math.round(y * 1000);
        var vec = camera.screenToWorld(x, y);
        vec.x = Math.round(vec.x * 1000);
        vec.y = Math.round(vec.y * 1000);
        return {'x': x, 'y': y};
    }

    function toViewport(x, y) {
        x = x / 10000; // normalize
        // x = x * 2 * cameraScale; // to world viewport scale

        y = y / 10000; // normalize
        // y = y * 2 * cameraScale; // to world viewport scale
        // return {'x': x, 'y': y};
        return camera.worldToScreen(x, y);
    }

    function sh2int(b0, b1) {
        return b0 << 8 | b1;
    }
})();