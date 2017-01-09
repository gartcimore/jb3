var jb3VisioTemplate = '\
<div class="c-input-group">\
    <div class="o-field">\
        <input name="conversationId" list="rooms" class="c-field">\
        <datalist id="rooms">\
            <option each="{ rooms }" value="{ rname }">\
        </datalist>\
    </div>\
    <button onclick="{ createConversation }" class="c-button  c-button--brand" >Create</button>\
    <button onclick="{ joinConversation }" class="c-button  c-button--info" >Join</button>\
    <button onclick="{ leaveConversation }" class="c-button c-button--warning" >Leave</button>\
</div>\
<div class="o-grid  o-grid--wrap">\
    <div class="o-grid__cell">\
        <jb3-visio-local-video if="{ localVideoStream }" name="localVideo" stream="{ localVideoStream }"></jb3-visio-local-video>\
    </div>\
    <div class="o-grid__cell" each="{ name, moule in remoteMoules }">\
        <jb3-visio-remote-video name="{ name }" stream="{ moule.stream }"></jb3-visio-local-video>\
    </div>\
</div>\
<div>\
    <ul each="{ log in logs }">{ log }</ul>\
</div>\
';
function jb3VisioConstructor(opts) {
    var self = this;
    self.rooms = jb3_common.getRooms();
    self.rooms.unshift( { rname: opts.defaultroom } );
    self.conversationId.value = URI(window.location).search(true).room;
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
                {urls: "stun:turn.bci.im"},
                {urls: "turn:turn.bci.im", credential: "ornottobe", username: "jb3" }
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
        history.pushState(self.conversationId.value, self.conversationId.value, URI(window.location).setSearch('room', self.conversationId.value));
        this.nextRTC.create(self.conversationId.value);
    };
    this.createBroadcastConversation = function () {
        history.pushState(self.conversationId.value, self.conversationId.value, URI(window.location).setSearch('room', self.conversationId.value));
        this.nextRTC.create(self.conversationId.value, {type: 'BROADCAST'});
    };
    this.joinConversation = function () {
        history.pushState(self.conversationId.value, self.conversationId.value, URI(window.location).setSearch('room', self.conversationId.value));
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