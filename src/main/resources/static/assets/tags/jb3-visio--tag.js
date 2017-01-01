var jb3VisioTemplate = '\
<div>\
    Conversation id:<input name="conversationId" type="text"/>\
    <button onclick="{ createConversation }">Create</button>\
    <button onclick="{ joinConversation }">Join</button>\
    <button onclick="{ leaveConversation }">Leave</button>\
</div>\
<div>\
    <jb3-visio-local-video if="{ localVideoStream }" name="localVideo" stream="{ localVideoStream }"></jb3-visio-local-video>\
    <div each="{ name, moule in remoteMoules }">\
        <jb3-visio-remote-video name="{ name }" stream="{ moule.stream }"></jb3-visio-local-video>\
    </div>\
</div>\
<div>\
    <ul each="{ log in logs }">{ log }</ul>\
</div>\
';
function jb3VisioConstructor(opts) {
    var self = this;
    self.logs = [];
    self.remoteMoules = {};
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
        self.logs.push('Room with id ' + event.content + ' has been created, share it with your friend to start videochat');
    });
    this.nextRTC.on('joined', function (nextRTC, event) {
        console.log(JSON.stringify(event));
        self.logs.push('You have been joined to conversation ' + event.content);
    });
    this.nextRTC.on('newJoined', function (nextRTC, event) {
        console.log(JSON.stringify(event));
        self.logs.push('Member with id ' + event.from + ' has joined conversation');
    });
    this.nextRTC.on('localStream', function (member, stream) {
        self.localVideoStream = URL.createObjectURL( stream.stream );
        self.update();
    });
    this.nextRTC.on('remoteStream', function (member, stream) {
        self.remoteMoules[stream.member] = { stream: URL.createObjectURL( stream.stream ) };
        self.update();
    });
    this.nextRTC.on('left', function (nextRTC, event) {
        console.log(JSON.stringify(event));
        delete self.remoteMoules[event.from];
        self.logs.push(event.from + " left!");
    });
    this.createConversation = function () {
        this.nextRTC.create(self.conversationId.value);
    };
    this.createBroadcastConversation = function () {
        this.nextRTC.create(self.conversationId.value, {type: 'BROADCAST'});
    };
    this.joinConversation = function () {
        this.nextRTC.join(self.conversationId.value);
    };
    this.leaveConversation = function () {
        this.nextRTC.leave();
    };
}
;

riot.tag('jb3-visio',
        jb3VisioTemplate,
        jb3VisioConstructor);