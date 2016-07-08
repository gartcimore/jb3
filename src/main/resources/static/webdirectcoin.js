function Webdirectcoin() {
}

Webdirectcoin.prototype.reconnectDelay = 1000;
Webdirectcoin.prototype.presenceDelay = 10000;

Webdirectcoin.prototype.initWebsocket = function(url) {
	this.url = url;
	if (typeof WebSocket === "function") {
		this.connectWebsocket();
	} else {
		postMessage({
			type : "webdirectcoin_not_available"
		});
	}
};

Webdirectcoin.prototype.presenceMessage = {
	presence : {
		status : "plop"
	}
};

Webdirectcoin.prototype.connectWebsocket = function() {
	var self = this;
	clearInterval(self.presenceInterval);
	this.client = new WebSocket(self.url);
	this.client.onopen = function(event) {
		console.log("webdirectcoin connected");
		postMessage({
			type : "connected"
		});
		self.presenceInterval = setInterval(function() {
			self.client.send(JSON.stringify(self.presenceMessage));
		}, self.presenceDelay);
	}
	this.client.onerror = function(event) {
		console.log("webdirectcoin websocket error:\n" + JSON.stringify(event));
	}
	this.client.onclose = function(event) {
		self.reconnect();
	}
	this.client.onmessage = function(event) {
                var message = JSON.parse(event.data);
                if(Array.isArray(message)) {
                    postMessage({
                            type : "posts",
                            posts : message
                    });
                } else if(message.presence){
                    postMessage({
                            type : "presence",
                            mouleId : message.mouleId,
                            presence: message.presence
                    });
                }
            };
};

Webdirectcoin.prototype.reconnect = function() {
	var self = this;
	console.log("webdirectcoin will try to reconnect in " + self.reconnectDelay + "ms");
	setTimeout(function() {
		self.connectWebsocket();
	}, self.reconnectDelay);
}

Webdirectcoin.prototype.onmessage = function(event) {
	switch (event.data.type) {
	case "send":
		if (this.client) {
			var message = {};
			message[event.data.destination] = event.data.body;
			this.client.send(JSON.stringify(message));
		}
		break;
        case "nickname":
            this.presenceMessage.presence.nickname = event.data.nickname;
            break;        
	case "connect":
		this.initWebsocket(event.data.url);
		break;
	}
};

var webdirectcoin = new Webdirectcoin();

onmessage = function(event) {
	webdirectcoin.onmessage(event);
};
