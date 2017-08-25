// window.game = window.game || {};
//
// (function () {
//     'use strict';
//
//     var Camera = function (context, params) {
//         params = params || {};
//
//         var viewport = {'x': context.canvas.width, 'y': context.canvas.height},
//             width = params.width || 1024,
//             height = params.height || 968;
//
//         var scale = 1.0,
//             rotation = 0.0,
//             position = {'x': 0, 'y': 0};
//
//         var toFollow;
//
//         var self = this;
//         return {
//             setScale: function (scale) {
//                 self.scale = scale;
//             },
//
//             setPosition: function (position) {
//                 self.position = position;
//             },
//
//             setRotation: function (rotation) {
//                 self.rotation = rotation;
//             },
//
//             update: function () {
//                 context.save();
//
//                 context.transform(-position.x, -position.y);
//                 context.rotate(rotation);
//                 context.scale(scale, scale);
//
//                 context.transform(viewport.x * 0.5, viewport.y * 0.5);
//                 context.scale(width / viewport.x, height / viewport.y);
//             },
//
//             follow: function (gameObject) {
//                 var position = gameObject.position;
//                 if (!position || !position.x || !position.y) {
//                     return;
//                 }
//                 self.toFollow = gameObject;
//             }
//         };
//     };
//
//     window.game.Camera = Camera;
// })();

(function() {

    var Camera = function(context, settings) {
        settings = settings || {};
        this.distance = 1000.0;
        this.lookat = [0,0];
        this.context = context;
        this.fieldOfView = settings.fieldOfView || Math.PI / 4.0;
        this.viewport = {
            left: 0,
            right: 0,
            top: 0,
            bottom: 0,
            width: 0,
            height: 0,
            scale: [1.0, 1.0]
        };
        this.updateViewport();
    };

    Camera.prototype = {
        begin: function() {
            this.context.save();
            this.applyScale();
            this.applyTranslation();
        },

        end: function() {
            this.context.restore();
        },

        applyScale: function() {
            this.context.scale(this.viewport.scale[0], this.viewport.scale[1]);
        },

        applyTranslation: function() {
            this.context.translate(-this.viewport.left, -this.viewport.top);
        },

        updateViewport: function() {
            this.aspectRatio = this.context.canvas.width / this.context.canvas.height;
            this.viewport.width = this.distance * Math.tan(this.fieldOfView);
            this.viewport.height = this.viewport.width / this.aspectRatio;
            this.viewport.left = this.lookat[0] - (this.viewport.width / 2.0);
            this.viewport.top = this.lookat[1] - (this.viewport.height / 2.0);
            this.viewport.right = this.viewport.left + this.viewport.width;
            this.viewport.bottom = this.viewport.top + this.viewport.height;
            this.viewport.scale[0] = this.context.canvas.width / this.viewport.width;
            this.viewport.scale[1] = this.context.canvas.height / this.viewport.height;
        },

        zoomTo: function(z) {
            this.distance = z;
            this.updateViewport();
        },
        
        moveTo: function(x, y) {
            this.lookat[0] = x;
            this.lookat[1] = y;
            this.updateViewport();
        },

        screenToWorld: function(x, y, obj) {
            obj = obj || {};
            obj.x = (x / this.viewport.scale[0]) + this.viewport.left;
            obj.y = (y / this.viewport.scale[1]) + this.viewport.top;
            return obj;
        },

        worldToScreen: function(x, y, obj) {
            obj = obj || {};
            obj.x = (x - this.viewport.left) * (this.viewport.scale[0]);
            obj.y = (y - this.viewport.top) * (this.viewport.scale[1]);
            return obj;
        }
    };

    this.Camera = Camera;
}).call(window);