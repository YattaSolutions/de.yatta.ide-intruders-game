function showOverlay(infoText) {
  document.getElementById("game").style.display = "none";
  document.getElementById("start-page").style.display = "flex";
}

function hideOverlay() {
  document.getElementById("game").style.display = "block";
  document.getElementById("start-page").style.display = "none";
  // start game
  init();
  animate();
}

function triggerSubscribe() {
  typeof subscribeGame == "function" && subscribeGame();
}

function triggerPurchase() {
  typeof purchaseGame == "function" && purchaseGame();
}

function triggerSignIn() {
  typeof signIn == "function" && signIn();
}

function triggerResetDemo() {
  typeof resetDemo == "function" && resetDemo();
}
