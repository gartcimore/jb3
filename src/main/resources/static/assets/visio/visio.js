function Rtcoin() {
    var rtcCoinURL = URI();
    rtcCoinURL = rtcCoinURL.protocol(rtcCoinURL.protocol() === "https" ? "wss" : "ws").path("/rtcoin");
    this.nextRTC = new NextRTC({
        wsURL: rtcCoinURL,
        mediaConfig: {
            video: true,
            audio: true
        },
        peerConfig: {
            iceServers: [
                {urls: "stun:23.21.150.121"},
                {urls: "stun:stun.l.google.com:19302"},
                {urls: "turn:numb.viagenie.ca", credential: "webrtcdemo", username: "louis@mozilla.com"}
            ],
            iceTransportPolicy: 'all',
            rtcpMuxPolicy: 'negotiate'
        }
    });
    this.nextRTC.on('created', function (nextRTC, event) {
        console.log(JSON.stringify(event));
        $('#log').append('<li>Room with id ' + event.content + ' has been created, share it with your friend to start videochat</li>');
    });
    this.nextRTC.on('joined', function (nextRTC, event) {
        console.log(JSON.stringify(event));
        $('#log').append('<li>You have been joined to conversation ' + event.content + '</li>');
    });
    this.nextRTC.on('newJoined', function (nextRTC, event) {
        console.log(JSON.stringify(event));
        $('#log').append('<li>Member with id ' + event.from + ' has joined conversation</li>');
    });
    this.nextRTC.on('localStream', function (member, stream) {
        var dest = $("#template").clone().prop({id: 'local'});
        $("#container").append(dest);
        dest[0].srcObject = stream.stream;
    });
    this.nextRTC.on('remoteStream', function (member, stream) {
        var dest = $("#template").clone().prop({id: stream.member});
        $("#container").append(dest);
        dest[0].srcObject = stream.stream;
    });
    this.nextRTC.on('left', function (nextRTC, event) {
        console.log(JSON.stringify(event));
        $('#' + event.from).remove();
        $('#log').append('<li>' + event.from + " left!</li>");
    });
};

Rtcoin.prototype.createConversation = function () {
    var convId = $('#convId').val();
    this.nextRTC.create(convId);
};
Rtcoin.prototype.createBroadcastConversation = function () {
    var convId = $('#convId').val();
    this.nextRTC.create(convId, {type: 'BROADCAST'});
};
Rtcoin.prototype.joinConversation = function () {
    var convId = $('#convId').val();
    this.nextRTC.join(convId);
};
Rtcoin.prototype.leaveConversation = function () {
    this.nextRTC.leave();
};

rtcCoin = new Rtcoin();