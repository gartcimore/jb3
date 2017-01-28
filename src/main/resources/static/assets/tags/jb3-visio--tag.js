var jb3VisioTemplate = '\
<form class="c-input-group" if="{ nextRTCReady && !localVideoStream }" onsubmit="{ joinConversation }" >\
    <div class="o-field">\
        <input name="conversationId" placeholder="Type room name" list="rooms" class="c-field" oninput="{ changeConversationId }">\
        <datalist id="rooms">\
            <option each="{ rooms }" value="{ rname }">\
        </datalist>\
    </div>\
    <input if="{ conversationId.value }" type="submit" class="c-button c-button--info" value="Join">\
</form>\
<div class="c-input-group" if="{ localVideoStream }">\
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
    self.reset = function() {
        self.rooms = jb3_common.getRooms();
        self.conversationId.value = URI(window.location).search(true).room || '';
        self.logs = [];
        self.remoteMoules = {};
        self.localVideoStream = null;
        var rtcCoinURL = URI();
        rtcCoinURL = rtcCoinURL.protocol(rtcCoinURL.protocol() === "https" ? "wss" : "ws").path("/rtcoin");
        self.nextRTC = new NextRTC({
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
        self.nextRTC.onReady = function() {
            self.nextRTCReady = true;
            self.update();
        };
        self.nextRTC.on('created', function (nextRTC, event) {
            console.log(JSON.stringify(event));
            self.logs.push('Room with id ' + event.content + ' has been created, share it with your friend to start videochat');
        });
        self.nextRTC.on('joined', function (nextRTC, event) {
            console.log(JSON.stringify(event));
            self.logs.push('You have been joined to conversation ' + event.content);
        });
        self.nextRTC.on('newJoined', function (nextRTC, event) {
            console.log(JSON.stringify(event));
            self.logs.push('Member with id ' + event.from + ' has joined conversation');
        });
        self.nextRTC.on('localStream', function (member, stream) {
            self.localVideoStream = URL.createObjectURL( stream.stream );
            self.update();
        });
        self.nextRTC.on('remoteStream', function (member, stream) {
            self.remoteMoules[stream.member] = { stream: URL.createObjectURL( stream.stream ) };
            self.update();
        });
        self.nextRTC.on('left', function (nextRTC, event) {
            console.log(JSON.stringify(event));
            delete self.remoteMoules[event.from];
            self.logs.push(event.from + " left!");
            self.update();
        });
        
    };    
    self.changeConversationId = function() {
        self.update();
    }
    self.joinConversation = function () {
        history.pushState(self.conversationId.value, self.conversationId.value, URI(window.location).setSearch('room', self.conversationId.value));
        self.nextRTC.join(self.conversationId.value);
    };
    self.leaveConversation = function () {
        self.nextRTC.leave();
        self.reset();
    };
    self.reset();
}
;

riot.tag('jb3-visio',
        jb3VisioTemplate,
        jb3VisioConstructor);