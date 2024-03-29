function showOverlay(showSignInLink) {
  document.getElementById("game").style.display = "none";
  document.getElementById("start-page").style.display = "block";
  if (showSignInLink === false) {
    document.getElementById("sign-in-btn").style.visibility = "hidden";
  } else {
    document.getElementById("sign-in-btn").style.visibility = "visible";
  }
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
