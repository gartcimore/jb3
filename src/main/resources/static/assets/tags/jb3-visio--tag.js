var jb3VisioTemplate = '\
<form class=""o-fieldset" if="{ nextRTCReady && !localVideoStream }" onsubmit="{ joinConversation }" >\
    <label class="c-label o-form-element">\
        Room:\
        <input name="conversationId" placeholder="Type room name" list="rooms" class="c-field o-form-element" oninput="{ changeConversationId }">\
        <datalist id="rooms">\
            <option each="{ rooms }" value="{ rname }">\
        </datalist>\
    </label>\
    <label class="c-label o-form-element">\
        Share:\
        <select name="mediaType" class="c-field o-form-element" onchange="{ changeMediaType }">\
            <optgroup>\
                <option value="audio-and-video">Audio and Video</option>\
                <option value="audio-only">Audio only</option>\
                <option value="video-only">Video only</option>\
            </optgroup>\
            <optgroup>\
                <option value="application">Application</option>\
                <option value="screen">Screen</option>\
                <option value="window">Window</option>\
            <optgroup>\
        </select>\
    </label>\
    <div class="c-label o-form-element">\
        <input if="{ conversationId.value }" type="submit" class="c-button c-button--info" value="Join">\
    </div>\
</form>\
<div class="o-grid  o-grid--wrap" >\
    <div class="o-grid__cell">\
        <jb3-visio-local-video if="{ localVideoStream }" name="localVideo" stream="{ localVideoStream }" nickname="{ jb3_common.getNickname() }"></jb3-visio-local-video>\
    </div>\
    <div class="o-grid__cell" each="{ mouleName, moule in remoteMoules }">\
        <jb3-visio-remote-video id="{ mouleName }" stream="{ moule.stream }" nickname="{ moule.nickname }"></jb3-visio-local-video>\
    </div>\
</div>\
<div class="c-input-group u-letter-box--medium" if="{ localVideoStream }">\
   <button onclick="{ leaveConversation }" class="c-button c-button--warning" >Leave</button>\
</div>\
<div class="c-card" if="{ logs.length > 0 }">\
    <div class="c-card__item c-card__item--brand">Room events</div>\
    <div class="c-card__item" each="{ log in logs }">{ log }</div>\
</div>\
';
function jb3VisioConstructor(opts) {
    var self = this;
    self.getMediaConfig = function() {
        switch(self.mediaType.value) {
            case "audio-only":
                return { audio: true };
            case "video-only":
                return { video: true };
            case "application":
                return {video: {mediaSource: 'application'}};
            case "screen":
                return {video: {mediaSource: 'screen'}};
            case "window":
                return {video: {mediaSource: 'window'}};
            default:
            case "audio-and-video":
                return { video: true, audio: true };
        }  
    };
    self.changeMediaType = function(event) {
        self.nextRTC.mediaConfig = self.getMediaConfig();
    }
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
            mediaConfig: self.getMediaConfig(),
            peerConfig: {
                iceServers: [
                    {urls: "stun:turn.devnewton.fr"},
                    {urls: "turn:turn.devnewton.fr", credential: "ornottobe", username: "jb3" }
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
            self.nextRTC.request('text', null, self.conversationId.value, {nickname: jb3_common.getNickname()});
        });
        self.nextRTC.on('newJoined', function (nextRTC, event) {
            console.log(JSON.stringify(event));
            self.logs.push('Member with id ' + event.from + ' has joined conversation');
            self.nextRTC.request('text', null, self.conversationId.value, {nickname: jb3_common.getNickname()});
        });
        self.nextRTC.on('localStream', function (member, stream) {
            self.localVideoStream = stream.stream;
            self.update();
        });
        self.nextRTC.on('remoteStream', function (member, stream) {
            self.remoteMoules[stream.member] = self.remoteMoules[stream.member] || {}; 
            self.remoteMoules[stream.member].stream = stream.stream;
            self.update();
        });
        self.nextRTC.on('text', function (nextRTC, event) {
            if(event.custom.nickname) {
                self.remoteMoules[event.from] = self.remoteMoules[event.from] || {};
                self.remoteMoules[event.from].nickname = event.custom.nickname;
                self.update();
            }
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
    window.addEventListener('storage', function(event) {
        if(event.key === 'nickname' && self.nextRTC && self.conversationId.value) {
            self.nextRTC.request('text', null, self.conversationId.value, {nickname: event.newValue});
            self.update();
        }
    }, false);
    self.reset();
}
;

riot.tag('jb3-visio',
        jb3VisioTemplate,
        jb3VisioConstructor);