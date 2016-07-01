function Webdirectcoin() {
}

Webdirectcoin.prototype.reconnectDelay = 2000;
Webdirectcoin.prototype.presenceDelay = 10000;

Webdirectcoin.prototype.initWebsocket = function(url) {
	if (typeof WebSocket === "function") {
		this.connectWebsocket(url);
	} else {
		postMessage({
			type : "webdirectcoin_not_available"
		});
	}
};

Webdirectcoin.prototype.presenceMessage = JSON.stringify({
	presence : {
		status : "plop"
	}
});

Webdirectcoin.prototype.connectWebsocket = function(url) {
	var self = this;
	clearInterval(self.presenceInterval);
	this.client = new WebSocket(url);
	this.client.onopen = function(event) {
		console.log("webdirectcoin connected");
		postMessage({
			type : "connected"
		});
		self.presenceInterval = setInterval(function() {
			self.client.send(self.presenceMessage);
		}, self.presenceDelay);
	}
	this.client.onclose = function(event) {
		console.log("webdirectcoin disconnected, try to reconnect in "
				+ self.reconnectDelay + "ms");
		setTimeout(function() {
			self.connectWebsocket(url);
		}, 2000);
	}
	this.client.onmessage = function(event) {
		postMessage({
			type : "posts",
			posts : JSON.parse(event.data)
		});
	}
};

Webdirectcoin.prototype.onmessage = function(event) {
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

onmessage = function(event) {
	webdirectcoin.onmessage(event);
};
