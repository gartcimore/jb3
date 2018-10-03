var jb3MoulesRosterTemplate = '\
<select id="moules" class="c-card__item" size="{ Math.max(2, Math.min(Object.keys(moulesNicknames).length, 10)) }">\
    <option each="{ nickname, ids in moulesNicknames }">{ nickname }{ ids.size > 1 && " &times; " + ids.size }</option>\
</select>\
';

var jb3MoulesRosterStyles = '\
.jb3-moules-roster-multi-sup {\
    color: #ff5100;\
}\
#moules {\
    width: 100%;\
}\
';

function jb3MoulesRosterConstructor() {
    var self = this;
    self.moulesPresences = new Map();
    self.moulesNicknames = {};
    self.on('presence', function (msg) {
        if (msg.presence.nickname) {
            self.moulesPresences.set(msg.mouleId, msg.presence);
        } else {
            self.moulesPresences.delete(msg.mouleId);
        }
        self.moulesNicknames = {};
        self.moulesPresences.forEach(function (moule, mouleId) {
            var ids = self.moulesNicknames[moule.nickname] || new Set();
            ids.add(mouleId);
            self.moulesNicknames[moule.nickname] = ids;
        });
        self.update();
    });
    self.on('clear-presence', function () {
        self.moulesPresences.clear();
        self.moulesNicknames = {};
        self.update();
    });
}

riot.tag('jb3-moules-roster', jb3MoulesRosterTemplate, jb3MoulesRosterStyles, jb3MoulesRosterConstructor);