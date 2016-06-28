function Restocoin() {
    this.roomsRefreshInterval = {};
}

Restocoin.prototype.init = function () {
    postMessage({type: "connected"});
};

Restocoin.prototype.onmessage = function (event) {
    switch (event.data.type) {
        case "send":
            switch (event.data.destination) {
                case "post":
                    this.post(event.data.body);
                    break;
                case "get":
                    this.startRefreshRoom(event.data.body.room);
                    break;
            }
            break;
        case "connect":
            this.init();
            break;
    }
};

Restocoin.prototype.startRefreshRoom = function (room) {
    var self = this;
    self.refreshRoom(room);
    if (!self.roomsRefreshInterval[room]) {
        self.roomsRefreshInterval[room] = setInterval(function () {
            self.refreshRoom(room);
        }, 30000);
    }
};

Restocoin.prototype.refreshRoom = function (room) {
    var request = new XMLHttpRequest();
    request.onreadystatechange = function () {
        if (this.readyState === 4 && this.status === 200) {
            postMessage({type: "posts", posts: JSON.parse(this.responseText)});
        }
    };
    request.open('GET', "/restocoin/get?room=" + room, true);
    request.overrideMimeType('application/json');
    request.send();
};

Restocoin.prototype.post = function (message) {
    var request = new XMLHttpRequest();
    request.onreadystatechange = function () {
        if (this.readyState === 4 && this.status === 200) {
            postMessage({type: "posts", posts: JSON.parse(this.responseText)});
        }
    };
    request.open('POST', "/restocoin/post", true);
    request.overrideMimeType('application/json');
    if (typeof FormData === "function") {
        var formData = new FormData();
        formData.append("room", message.room);
        formData.append("nickname", message.nickname);
        formData.append("message", message.message);
        formData.append("auth", message.auth);
        request.send(formData);
    } else {
        var urlEncodedData = ("room=" + encodeURIComponent(message.room) + "&nickname=" + encodeURIComponent(message.nickname) + "&auth=" + message.auth + "&message=" + message.message).replace(/%20/g, '+');
        request.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
        request.setRequestHeader('Content-Length', urlEncodedData.length);
        request.send(urlEncodedData);
    }

};

var restocoin = new Restocoin();

onmessage = function (event) {
    restocoin.onmessage(event);
};
