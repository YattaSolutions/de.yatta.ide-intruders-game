/* Simple JavaScript Inheritance
 * By John Resig http://ejohn.org/
 * MIT Licensed.
 */
// Inspired by base2 and Prototype
(function() {
  var initializing = false,
    fnTest = /xyz/.test(function() {
      xyz;
    })
      ? /\b_super\b/
      : /.*/;

  // The base Class implementation (does nothing)
  this.Class = function() { };

  // Create a new Class that inherits from this class
  Class.extend = function(prop) {
    var _super = this.prototype;

    // Instantiate a base class (but only create the instance,
    // don't run the init constructor)
    initializing = true;
    var prototype = new this();
    initializing = false;

    // Copy the properties over onto the new prototype
    for (var name in prop) {
      // Check if we're overwriting an existing function
      prototype[name] =
        typeof prop[name] == "function" &&
          typeof _super[name] == "function" &&
          fnTest.test(prop[name])
          ? (function(name, fn) {
            return function() {
              var tmp = this._super;

              // Add a new ._super() method that is the same method
              // but on the super-class
              this._super = _super[name];

              // The method only need to be bound temporarily, so we
              // remove it when we're done executing
              var ret = fn.apply(this, arguments);
              this._super = tmp;

              return ret;
            };
          })(name, prop[name])
          : prop[name];
    }

    // The dummy class constructor
    function Class() {
      // All construction is actually done in the init method
      if (!initializing && this.init) this.init.apply(this, arguments);
    }

    // Populate our constructed prototype object
    Class.prototype = prototype;

    // Enforce the constructor to be what we expect
    Class.prototype.constructor = Class;

    // And make this class extendable
    Class.extend = arguments.callee;

    return Class;
  };
})();

// ###################################################################
// shims
//
// ###################################################################
(function() {
  var requestAnimationFrame =
    window.requestAnimationFrame ||
    window.mozRequestAnimationFrame ||
    window.webkitRequestAnimationFrame ||
    window.msRequestAnimationFrame;
  window.requestAnimationFrame = requestAnimationFrame;
})();

(function() {
  if (!window.performance.now) {
    window.performance.now = !Date.now
      ? function() {
        return new Date().getTime();
      }
      : function() {
        return Date.now();
      };
  }
})();

// ###################################################################
// Constants
//
// ###################################################################
var IS_CHROME =
  /Chrome/.test(navigator.userAgent) && /Google Inc/.test(navigator.vendor);
var CANVAS_WIDTH = 640;
var CANVAS_HEIGHT = 640;
var CANVAS_BG_STYLE = "#F5F5F5";
var FONT_COLOR = "#353535";
var BULLET_COLOR = "#353535";
var BUG_EXPLOSION_COLOR = "#008009";
var SPACESHIP_COLOR = "#660000";
var SPRITE_SHEET_SRC =
  "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAD8AAADrCAYAAADe8E7mAAAACXBIWXMAAAsSAAALEgHS3X78AAACpUlEQVR4nO3dQXKDMAyFYdLplZL7r3MpuupMFrEQtmWE3v/t01iNbCxA8Nj3fYv2er26vuT9fj8ih/YT+cezkw4+LO17U70lYgr89n7wM7jouRk1hu7gjwYzW8Q/mznv1frvR/7iltYYvJnBoU7V4YKXYVX36JkCpL2qw7TPnOotrPYOBK+qu6q7alfX0rM2kfaqTpW0FDaFkPYWCpuiSHsLhU1RU8/brzref/s+trcnTb1oEbnru/xyVbZjPtvbAVPT/tOMKRCdXdLb27BfvjWwTJYEf9u0Z3tb1JJb0f55039VtvHLr9bKgNXrCzu8mTynt1u/8Mhne3AOT1XogjeSritqCtJe1WHa36Wk/cQVGwdKWu8AMk8BTmCeFJb2M87bR2faqeCzTQHaTAawyRmV4W6sHuztVXGct1DYFEXaWyhsimKH14M2k5sj7UdR2NwQwasieFUEr4o2E1W0mXjRZlIIbSYW2kwmDiwT7sm5cgARWO0daDNRRZuJKtpMVLHgRaHNJDHSXhXH+Wi8zSQhFrwIvM0kGG0mA5jzXjz0txB2eBYe+lsUaW/hclVRBK+KNhNVHOq8KGwKIe0tFDZFkfYWCpuiaDNRRZuJF20mhdBmYqHNZOLAMqHN5MoBRGC1d6DNRBVtJqpoM1HFgheFNpPESHsLD/0tipLWOwAe+lsIbzPx4m0mzsHMRpvJZDz0VxXBq+JtJqpIewuFTVHs8HrQZnJzpP0oCpsbInhVBK+K4FXRZqKKNhMv2kwKoc3EQpvJxIFlwj05Vw4gAqu9A20mqmgzUUWbiSoWvCi0mSRG2qtaUtV9822jwyZnIYJXRfCqCF4Vwau6xburqOoCELyqx/P5lK1pSXtVBK+K4FURvCqCV0XwqgheFcGr0g1+27Y/VWz8dnQl3RUAAAAASUVORK5CYII=";
var LEFT_KEY = 37;
var RIGHT_KEY = 39;
var SHOOT_KEY = 32; 
var START_KEY = 32;
// key codes: 'Space' = 32, 'x' = 88, 'enter' = 13
var PLAYER_CLIP_RECT = { x: 0, y: 203, w: 63, h: 32 };
var BUG_BOTTOM_ROW = [
  { x: 7, y: 0, w: 48, h: 49 },
  { x: 7, y: 49, w: 48, h: 51 },
];
var BUG_MIDDLE_ROW = [
  { x: 7, y: 151, w: 48, h: 51 },
  { x: 7, y: 100, w: 48, h: 51 },
];
var BUG_TOP_ROW = [
  { x: 7, y: 100, w: 48, h: 51 },
  { x: 7, y: 151, w: 48, h: 51 },
];
var BUG_X_MARGIN = 50;
var BUGS_PER_COLUMN = 9;
var BUG_ROWS = 4;
var BUG_SQUAD_WIDTH = BUGS_PER_COLUMN * BUG_X_MARGIN;

// ###################################################################
// Utility functions & classes
//
// ###################################################################
function getRandomArbitrary(min, max) {
  return Math.random() * (max - min) + min;
}

function getRandomInt(min, max) {
  return Math.floor(Math.random() * (max - min + 1)) + min;
}

function clamp(num, min, max) {
  return Math.min(Math.max(num, min), max);
}

function valueInRange(value, min, max) {
  return value <= max && value >= min;
}

function checkRectCollision(A, B) {
  var xOverlap =
    valueInRange(A.x, B.x, B.x + B.w) || valueInRange(B.x, A.x, A.x + A.w);

  var yOverlap =
    valueInRange(A.y, B.y, B.y + B.h) || valueInRange(B.y, A.y, A.y + A.h);
  return xOverlap && yOverlap;
}

var Point2D = Class.extend({
  init: function(x, y) {
    this.x = typeof x === "undefined" ? 0 : x;
    this.y = typeof y === "undefined" ? 0 : y;
  },

  set: function(x, y) {
    this.x = x;
    this.y = y;
  },
});

var Rect = Class.extend({
  init: function(x, y, w, h) {
    this.x = typeof x === "undefined" ? 0 : x;
    this.y = typeof y === "undefined" ? 0 : y;
    this.w = typeof w === "undefined" ? 0 : w;
    this.h = typeof h === "undefined" ? 0 : h;
  },

  set: function(x, y, w, h) {
    this.x = x;
    this.y = y;
    this.w = w;
    this.h = h;
  },
});

// ###################################################################
// Globals
//
// ###################################################################
var canvas = null;
var ctx = null;
var spriteSheetImg = null;
var bulletImgs = [];
var keyStates = null;
var prevKeyStates = null;
var lastTime = 0;
var player = null;
var bugs = [];
var particleManager = null;
var updateBugLogic = false;
var bugDirection = -1;
var bugYDown = 0;
var bugCount = 0;
var wave = 1;
var hasGameStarted = false;

// ###################################################################
// Entities
//
// ###################################################################
var BaseSprite = Class.extend({
  init: function(img, x, y) {
    this.img = img;
    this.position = new Point2D(x, y);
    this.scale = new Point2D(1, 1);
    this.bounds = new Rect(x, y, this.img.width, this.img.height);
    this.doLogic = true;
  },

  update: function(dt) { },

  _updateBounds: function() {
    this.bounds.set(
      this.position.x,
      this.position.y,
      ~~(0.5 + this.img.width * this.scale.x),
      ~~(0.5 + this.img.height * this.scale.y)
    );
  },

  _drawImage: function() {
    ctx.drawImage(this.img, this.position.x, this.position.y);
  },

  draw: function(resized) {
    this._updateBounds();

    this._drawImage();
  },
});

var SheetSprite = BaseSprite.extend({
  init: function(sheetImg, clipRect, x, y) {
    this._super(sheetImg, x, y);
    this.clipRect = clipRect;
    this.bounds.set(x, y, this.clipRect.w, this.clipRect.h);
  },

  update: function(dt) { },

  _updateBounds: function() {
    var w = ~~(0.5 + this.clipRect.w * this.scale.x);
    var h = ~~(0.5 + this.clipRect.h * this.scale.y);
    this.bounds.set(this.position.x - w / 2, this.position.y - h / 2, w, h);
  },

  _drawImage: function() {
    ctx.save();
    ctx.transform(
      this.scale.x,
      0,
      0,
      this.scale.y,
      this.position.x,
      this.position.y
    );
    ctx.drawImage(
      this.img,
      this.clipRect.x,
      this.clipRect.y,
      this.clipRect.w,
      this.clipRect.h,
      ~~(0.5 + -this.clipRect.w * 0.5),
      ~~(0.5 + -this.clipRect.h * 0.5),
      this.clipRect.w,
      this.clipRect.h
    );
    ctx.restore();
  },

  draw: function(resized) {
    this._super(resized);
  },
});

var Player = SheetSprite.extend({
  init: function() {
    this._super(
      spriteSheetImg,
      PLAYER_CLIP_RECT,
      CANVAS_WIDTH / 2,
      CANVAS_HEIGHT - 70
    );
    this.scale.set(0.85, 0.85);
    this.lives = 5;
    this.xVel = 0;
    this.bullets = [];
    this.bulletDelayAccumulator = 0;
    this.score = 0;
  },

  reset: function() {
    this.lives = 5;
    this.score = 0;
    this.position.set(CANVAS_WIDTH / 2, CANVAS_HEIGHT - 70);
  },

  shoot: function() {
    var bullet = new Bullet(
      this.position.x,
      this.position.y - this.bounds.h / 2,
      1,
      800
    );
    this.bullets.push(bullet);
  },

  handleInput: function() {
    if (isKeyDown(LEFT_KEY)) {
      this.xVel = -175;
    } else if (isKeyDown(RIGHT_KEY)) {
      this.xVel = 175;
    } else this.xVel = 0;

    if (wasKeyPressed(SHOOT_KEY)) {
      if (this.bulletDelayAccumulator > 0.5) {
        this.shoot();
        this.bulletDelayAccumulator = 0;
      }
    }
  },

  updateBullets: function(dt) {
    for (var i = this.bullets.length - 1; i >= 0; i--) {
      var bullet = this.bullets[i];
      if (bullet.alive) {
        bullet.update(dt);
      } else {
        this.bullets.splice(i, 1);
        bullet = null;
      }
    }
  },

  update: function(dt) {
    // update time passed between shots
    this.bulletDelayAccumulator += dt;

    // apply x vel
    this.position.x += this.xVel * dt;

    // cap player position in screen bounds
    this.position.x = clamp(
      this.position.x,
      this.bounds.w / 2,
      CANVAS_WIDTH - this.bounds.w / 2
    );
    this.updateBullets(dt);
  },

  draw: function(resized) {
    this._super(resized);

    // draw bullets
    for (var i = 0, len = this.bullets.length; i < len; i++) {
      var bullet = this.bullets[i];
      if (bullet.alive) {
        bullet.draw(resized);
      }
    }
  },
});

var Bullet = BaseSprite.extend({
  init: function(x, y, direction, speed) {
    var img = bulletImgs[Math.floor(Math.random()*bulletImgs.length)];
    this._super(img, x, y);
    this.direction = direction;
    this.speed = speed;
    this.alive = true;
  },

  update: function(dt) {
    this.position.y -= this.speed * this.direction * dt;

    if (this.position.y < 0) {
      this.alive = false;
    }
  },

  draw: function(resized) {
    this._super(resized);
  },
});

var Enemy = SheetSprite.extend({
  init: function(clipRects, x, y) {
    this._super(spriteSheetImg, clipRects[0], x, y);
    this.clipRects = clipRects;
    this.scale.set(0.5, 0.5);
    this.alive = true;
    this.onFirstState = true;
    this.stepDelay = 1; // try 2 secs to start with...
    this.stepAccumulator = 0;
    this.doShoot - false;
    this.bullet = null;
  },

  toggleFrame: function() {
    this.onFirstState = !this.onFirstState;
    this.clipRect = this.onFirstState ? this.clipRects[0] : this.clipRects[1];
  },

  shoot: function() {
    this.bullet = new Bullet(
      this.position.x,
      this.position.y + this.bounds.w / 2,
      -1,
      450
    );
  },

  update: function(dt) {
    this.stepAccumulator += dt;

    if (this.stepAccumulator >= this.stepDelay) {
      if (this.position.x < this.bounds.w / 2 + 20 && bugDirection < 0) {
        updateBugLogic = true;
      }
      if (
        bugDirection === 1 &&
        this.position.x > CANVAS_WIDTH - this.bounds.w / 2 - 20
      ) {
        updateBugLogic = true;
      }
      if (this.position.y > CANVAS_WIDTH - 50) {
        reset();
      }

      var fireTest = Math.floor(Math.random() * (this.stepDelay + 1));
      if (getRandomArbitrary(0, 1000) <= 5 * (this.stepDelay + 1)) {
        this.doShoot = true;
      }
      this.position.x += 10 * bugDirection;
      this.toggleFrame();
      this.stepAccumulator = 0;
    }
    this.position.y += bugYDown;

    if (this.bullet !== null && this.bullet.alive) {
      this.bullet.update(dt);
    } else {
      this.bullet = null;
    }
  },

  draw: function(resized) {
    this._super(resized);
    if (this.bullet !== null && this.bullet.alive) {
      this.bullet.draw(resized);
    }
  },
});

var ParticleExplosion = Class.extend({
  init: function() {
    this.particlePool = [];
    this.particles = [];
  },

  draw: function() {
    for (var i = this.particles.length - 1; i >= 0; i--) {
      var particle = this.particles[i];
      particle.moves++;
      particle.x += particle.xunits;
      particle.y += particle.yunits + particle.gravity * particle.moves;
      particle.life--;

      if (particle.life <= 0) {
        if (this.particlePool.length < 100) {
          this.particlePool.push(this.particles.splice(i, 1));
        } else {
          this.particles.splice(i, 1);
        }
      } else {
        ctx.globalAlpha = particle.life / particle.maxLife;
        ctx.fillStyle = particle.color;
        ctx.fillRect(particle.x, particle.y, particle.width, particle.height);
        ctx.globalAlpha = 1;
      }
    }
  },

  createExplosion: function(
    x,
    y,
    color,
    number,
    width,
    height,
    spd,
    grav,
    lif
  ) {
    for (var i = 0; i < number; i++) {
      var angle = Math.floor(Math.random() * 360);
      var speed = Math.floor((Math.random() * spd) / 2) + spd;
      var life = Math.floor(Math.random() * lif) + lif / 2;
      var radians = (angle * Math.PI) / 180;
      var xunits = Math.cos(radians) * speed;
      var yunits = Math.sin(radians) * speed;

      if (this.particlePool.length > 0) {
        var tempParticle = this.particlePool.pop();
        tempParticle.x = x;
        tempParticle.y = y;
        tempParticle.xunits = xunits;
        tempParticle.yunits = yunits;
        tempParticle.life = life;
        tempParticle.color = color;
        tempParticle.width = width;
        tempParticle.height = height;
        tempParticle.gravity = grav;
        tempParticle.moves = 0;
        tempParticle.alpha = 1;
        tempParticle.maxLife = life;
        this.particles.push(tempParticle);
      } else {
        this.particles.push({
          x: x,
          y: y,
          xunits: xunits,
          yunits: yunits,
          life: life,
          color: color,
          width: width,
          height: height,
          gravity: grav,
          moves: 0,
          alpha: 1,
          maxLife: life,
        });
      }
    }
  },
});

// ###################################################################
// Initialization functions
//
// ###################################################################
function initCanvas() {
  // create our canvas and context
  canvas = document.getElementById("game-canvas");
  ctx = canvas.getContext("2d");

  // turn off image smoothing
  setImageSmoothing(false);

  // create our main sprite sheet img
  spriteSheetImg = new Image();
  spriteSheetImg.src = SPRITE_SHEET_SRC;
  preDrawImages();

  // add event listeners and initially resize
  window.addEventListener("resize", resize);
  document.addEventListener("keydown", onKeyDown);
  document.addEventListener("keyup", onKeyUp);
}

function preDrawImages() {
  // '0', resolution 5 x 10
  bulletImgs = [];
  var img = new Image();
  img.src = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAUAAAAKCAYAAAB8OZQwAAAAMUlEQVQI12NkYGBgMDU1/c8ABadPn2ZkhAmcPn0azkZRBeMzMWABNBOEuQBGM2JzPACEvxQm6nrehwAAAABJRU5ErkJggg==";
  bulletImgs.push(img);
  // '1', resolution 5 x 10
  img = new Image();
  img.src = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAUAAAAKCAYAAAB8OZQwAAAAJklEQVQI12NggAJTU9P/MDYTugADAwMDE7oACsDQjg5oIciIzUkAcV0IJqLp3/kAAAAASUVORK5CYII=";
  bulletImgs.push(img);
}

function setImageSmoothing(value) {
  this.ctx["imageSmoothingEnabled"] = value;
  this.ctx["mozImageSmoothingEnabled"] = value;
  this.ctx["oImageSmoothingEnabled"] = value;
  this.ctx["webkitImageSmoothingEnabled"] = value;
  this.ctx["msImageSmoothingEnabled"] = value;
}

function initGame() {
  dirtyRects = [];
  bugs = [];
  player = new Player();
  particleManager = new ParticleExplosion();
  setupBugFormation();
  drawBottomHud();
}

function setupBugFormation() {
  bugCount = 0;
  for (var i = 0, len = BUG_ROWS * BUGS_PER_COLUMN; i < len; i++) {
    var gridX = i % BUGS_PER_COLUMN;
    var gridY = Math.floor(i / BUGS_PER_COLUMN);
    var clipRects;
    switch (gridY) {
      case 0:
      case 1:
        clipRects = BUG_BOTTOM_ROW;
        break;
      case 2:
        //case 3:
        clipRects = BUG_MIDDLE_ROW;
        break;
      default:
        clipRects = BUG_TOP_ROW;
        break;
    }
    bugs.push(
      new Enemy(
        clipRects,
        CANVAS_WIDTH / 2 -
        BUG_SQUAD_WIDTH / 2 +
        BUG_X_MARGIN / 2 +
        gridX * BUG_X_MARGIN,
        CANVAS_HEIGHT / 3.25 - gridY * 40
      )
    );
    bugCount++;
  }
}

function reset() {
  bugs = [];
  setupBugFormation();
  player.reset();
}

function init() {
  initCanvas();
  keyStates = [];
  prevKeyStates = [];
  resize();
}

// ###################################################################
// Helpful input functions
//
// ###################################################################
function isKeyDown(key) {
  return keyStates[key];
}

function wasKeyPressed(key) {
  return !prevKeyStates[key] && keyStates[key];
}

// ###################################################################
// Drawing & Update functions
//
// ###################################################################
function updateBugs(dt) {
  if (updateBugLogic) {
    updateBugLogic = false;
    bugDirection = -bugDirection;
    bugYDown = 25;
  }

  for (var i = bugs.length - 1; i >= 0; i--) {
    var bug = bugs[i];
    if (!bug.alive) {
      bugs.splice(i, 1);
      bug = null;
      bugCount--;
      if (bugCount < 1) {
        wave++;
        setupBugFormation();
      }
      return;
    }

    bug.stepDelay = (bugCount * 10 - wave * 10) / 1000;
    if (bug.stepDelay <= 0.1) {
      bug.stepDelay = 0.1;
    }
    bug.update(dt);

    if (bug.doShoot) {
      bug.doShoot = false;
      bug.shoot();
    }
  }
  bugYDown = 0;
}

function resolveBulletEnemyCollisions() {
  var bullets = player.bullets;

  for (var i = 0, len = bullets.length; i < len; i++) {
    var bullet = bullets[i];
    for (var j = 0, alen = bugs.length; j < alen; j++) {
      var bug = bugs[j];
      if (checkRectCollision(bullet.bounds, bug.bounds)) {
        bug.alive = bullet.alive = false;
        particleManager.createExplosion(
          bug.position.x,
          bug.position.y,
          BUG_EXPLOSION_COLOR,
          30,
          3,
          3,
          4,
          0.3,
          50
        );
        player.score += 10;
      }
    }
  }
}

function resolveBulletPlayerCollisions() {
  for (var i = 0, len = bugs.length; i < len; i++) {
    var bug = bugs[i];
    if (
      bug.bullet !== null &&
      checkRectCollision(bug.bullet.bounds, player.bounds)
    ) {
      if (player.lives === 0) {
        hasGameStarted = false;
      } else {
        bug.bullet.alive = false;
        particleManager.createExplosion(
          player.position.x,
          player.position.y,
          SPACESHIP_COLOR,
          80,
          5,
          5,
          10,
          0.005,
          40
        );
        player.position.set(CANVAS_WIDTH / 2, CANVAS_HEIGHT - 70);
        player.lives--;
        break;
      }
    }
  }
}

function resolveCollisions() {
  resolveBulletEnemyCollisions();
  resolveBulletPlayerCollisions();
}

function updateGame(dt) {
  player.handleInput();
  prevKeyStates = keyStates.slice();
  player.update(dt);
  updateBugs(dt);
  resolveCollisions();
}

function drawIntoCanvas(width, height, drawFunc) {
  var canvas = document.createElement("canvas");
  canvas.width = width;
  canvas.height = height;
  var ctx = canvas.getContext("2d");
  drawFunc(ctx);
  return canvas;
}

function fillText(text, x, y, color, fontSize) {
  if (typeof color !== "undefined") ctx.fillStyle = color;
  if (typeof fontSize !== "undefined") ctx.font = fontSize + "px 'Hiro Mono'";
  ctx.fillText(text, x, y);
}

function fillCenteredText(text, x, y, color, fontSize) {
  if (typeof fontSize !== "undefined") ctx.font = fontSize + "px 'Hiro Mono'";
  var metrics = ctx.measureText(text);
  fillText(text, x - metrics.width / 2, y, color, fontSize);
}

function fillRightAlignedText(text, x, y, color, fontSize) {
  if (typeof fontSize !== "undefined") ctx.font = fontSize + "px 'Hiro Mono'";
  var metrics = ctx.measureText(text);
  fillText(text, x - metrics.width, y, color, fontSize);
}

function fillBlinkingText(text, x, y, blinkFreq, color, fontSize) {
  if (~~(0.5 + Date.now() / blinkFreq) % 2) {
    fillCenteredText(text, x, y, color, fontSize);
  }
}

function drawBottomHud() {
  ctx.fillStyle = SPACESHIP_COLOR;
  ctx.fillRect(0, CANVAS_HEIGHT - 30, CANVAS_WIDTH, 2);
  fillText(player.lives + " x ", 10, CANVAS_HEIGHT - 7.5, FONT_COLOR, 20);
  ctx.drawImage(
    spriteSheetImg,
    player.clipRect.x,
    player.clipRect.y,
    player.clipRect.w,
    player.clipRect.h,
    60,
    CANVAS_HEIGHT - 23,
    player.clipRect.w * 0.5,
    player.clipRect.h * 0.5
  );
  fillRightAlignedText(
    "Debugging score: " + player.score,
    CANVAS_WIDTH,
    CANVAS_HEIGHT - 7.5
  );
}

function drawBugs(resized) {
  for (var i = 0; i < bugs.length; i++) {
    var bug = bugs[i];
    bug.draw(resized);
  }
}

function drawGame(resized) {
  player.draw(resized);
  drawBugs(resized);
  particleManager.draw();
  drawBottomHud();
}

function drawStartScreen() {
  fillCenteredText(
    "Eclipse Intruders",
    CANVAS_WIDTH / 2,
    CANVAS_HEIGHT / 2.75,
    FONT_COLOR,
    36
  );
  fillCenteredText(
    "Fight the bugs!",
    CANVAS_WIDTH / 2,
    CANVAS_HEIGHT / 2.2,
    FONT_COLOR,
    30
  );
  fillBlinkingText(
    "Press 'Space' to shoot",
    CANVAS_WIDTH / 2,
    CANVAS_HEIGHT / 1.8,
    900,
    FONT_COLOR,
    30
  );
}

function animate() {
  var now = window.performance.now();
  var dt = now - lastTime;
  if (dt > 100) dt = 100;
  if (wasKeyPressed(START_KEY) && !hasGameStarted) {
    initGame();
    hasGameStarted = true;
  }

  if (hasGameStarted) {
    updateGame(dt / 1000);
  }

  ctx.fillStyle = CANVAS_BG_STYLE;
  ctx.fillRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
  if (hasGameStarted) {
    drawGame(false);
  } else {
    drawStartScreen();
  }
  lastTime = now;
  requestAnimationFrame(animate);
}

// ###################################################################
// Event Listener functions
//
// ###################################################################
function resize() {
  var w = window.innerWidth;
  var h = window.innerHeight;

  // calculate the scale factor to keep a correct aspect ratio
  var scaleFactor = Math.min(w / CANVAS_WIDTH, h / CANVAS_HEIGHT);

  if (IS_CHROME) {
    canvas.width = CANVAS_WIDTH * scaleFactor;
    canvas.height = CANVAS_HEIGHT * scaleFactor;
    setImageSmoothing(false);
    ctx.transform(scaleFactor, 0, 0, scaleFactor, 0, 0);
  } else {
    // resize the canvas css properties
    canvas.style.width = CANVAS_WIDTH * scaleFactor + "px";
    canvas.style.height = CANVAS_HEIGHT * scaleFactor + "px";
  }
}

function onKeyDown(e) {
  e.preventDefault();
  keyStates[e.keyCode] = true;
}

function onKeyUp(e) {
  e.preventDefault();
  keyStates[e.keyCode] = false;
}

// ###################################################################
// Start game!
//
// ###################################################################
window.onload = function() {
  init();
  animate();
};