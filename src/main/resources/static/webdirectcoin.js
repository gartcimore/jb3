function Webdirectcoin() {
}

Webdirectcoin.prototype.initWebsocket = function (url) {
    if (typeof WebSocket === "function") {
        importScripts("/webjars/stomp-websocket/2.3.1-1/stomp.js");
        this.connectWebsocket(url);
    } else {
        postMessage({type: "webdirectcoin_not_available"});
    }
};

Webdirectcoin.prototype.connectWebsocket = function (url) {
    var self = this;
    var stompClient = Stomp.client(url);
    stompClient.connect({}, function (frame) {
        console.log('WebDirectCoin connected: ' + frame);
        stompClient.subscribe('/topic/posts', function (postsMessage) {
            postMessage({type: "posts", posts: JSON.parse(postsMessage.body)});
        });
        stompClient.subscribe('/topic/debug', function () {
        });
        self.stompClient = stompClient;
        setInterval(function () {
            self.stompClient.send("/webdirectcoin/presence", {}, "plop");
        }, 20000);
        postMessage({type: "connected"});
    }, function (error) {
        console.log('WebDirectCoin error: ' + error + "\nTry to reconnect...");
        setTimeout(function () {
            self.connectWebsocket(url);
        }, 30000);
    }
    );
};

Webdirectcoin.prototype.onmessage = function (event) {
    switch (event.data.type) {
        case "send":
            if (this.stompClient) {
                this.stompClient.send("/webdirectcoin/" + event.data.destination, {}, JSON.stringify(event.data.body));
            }
            break;
        case "connect":
            this.initWebsocket(event.data.url);
            break;
    }
};

var webdirectcoin = new Webdirectcoin();

onmessage = function (event) {
    webdirectcoin.onmessage(event);
};

