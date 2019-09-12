const WEBDIRECTCOIN_RECONNECT_DELAY = 1000;
const WEBDIRECTCOIN_PRESENCE_DELAY = 1000;

class WebdirectcoinPresenceMessage {
	constructor(nickname) {
		this.presence = {
			status : "plop",
		}
		if(nickname) {
			this.presence.nickname = nickname;
		}
	}	
}

class Webdirectcoin {
	
	constructor() {
		this.presenceMessage = JSON.stringify(new WebdirectcoinPresenceMessage());
	}

initWebsocket(url) {
	this.url = url;
	if (typeof WebSocket === "function") {
		this.connectWebsocket();
	} else {
		postMessage({
			webdirectcoin_not_available: true
		});
	}
}

connectWebsocket() {
	clearInterval(this.presenceInterval);
	this.client = new WebSocket(this.url);
	this.client.onopen = (event) => {
        console.log("webdirectcoin connected");
        postMessage({
                connected: true
        });
        this.presenceInterval = setInterval(() => {
                this.client.send(this.presenceMessage);
        }, WEBDIRECTCOIN_PRESENCE_DELAY);
	};
	this.client.onerror = (event) => {
        console.log("webdirectcoin websocket error:\n" + JSON.stringify(event));
	};
	this.client.onclose = (event) => {
        postMessage({
                disconnected: true
        });
        this.reconnect();
	};
	this.client.onmessage = (event) => {
        postMessage(JSON.parse(event.data));
    };
};

reconnect() {
	console.log("webdirectcoin will try to reconnect in " + WEBDIRECTCOIN_RECONNECT_DELAY + "ms");
	setTimeout(() => {
		this.connectWebsocket();
	}, WEBDIRECTCOIN_RECONNECT_DELAY);
}

onmessage(event) {
	switch (event.data.type) {
	case "send":
		if (this.client) {
			let message = {};
			message[event.data.destination] = event.data.body;
			let msg = JSON.stringify(message);
			this.client.send(msg);
		}
		break;
        case "nickname":
        	this.presenceMessage = JSON.stringify(new WebdirectcoinPresenceMessage(event.data.nickname));
            break;        
	case "connect":
		this.initWebsocket(event.data.url);
		break;
	}
}

}

let webdirectcoin = new Webdirectcoin();

onmessage = (event) => {
	webdirectcoin.onmessage(event);
};
