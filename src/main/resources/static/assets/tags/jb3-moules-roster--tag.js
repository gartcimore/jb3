var jb3MoulesRosterTemplate = '\
<ul id="moules" class="c-card__item c-list c-list--unstyled">\
    <li class="c-list__item" each="{ nickname, ids in moulesNicknames }">{ nickname }<sup if="{ ids.size > 1 }" class="jb3-moules-roster-multi-sup">{ ids.size }</sup></li>\
</ul>\
';

var jb3MoulesRosterStyles = '\
.jb3-moules-roster-multi-sup {\
    color: #ff5100;\
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