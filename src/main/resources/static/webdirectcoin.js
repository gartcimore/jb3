function Webdirectcoin() {
}

Webdirectcoin.prototype.initWebsocket = function (url) {
    if (typeof WebSocket === "function") {
        this.connectWebsocket(url);
    } else {
        postMessage({type: "webdirectcoin_not_available"});
    }
};

Webdirectcoin.prototype.presenceMessage = JSON.stringify({status:"plop"});

Webdirectcoin.prototype.connectWebsocket = function (url) {
    var self = this;
    this.client = new WebSocket(url);
    this.client.onopen = function(event) {
    	postMessage({type: "connected"});
    }
    this.client.onmessage = function(event) {
    	console.log(event.data);
    	postMessage({type: "posts", posts: JSON.parse(event.data)});
    }
};

Webdirectcoin.prototype.onmessage = function (event) {
    switch (event.data.type) {
        case "send":
            if (this.client) {
                var message = {};
                message[event.data.destination] = event.data.body;
            	this.client.send(JSON.stringify(message));
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

