var jb3MoulesRosterTemplate = '\
<select id="moules" class="c-card__item" size="{ Math.max(2, Math.min(Object.keys(moulesNicknames).length, 10)) }">\
    <option each="{ nickname, status in moulesNicknames }" title="{ status }">{ nickname }</option>\
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
    this.moulesNicknames = {};
    this.on('presence', (msg) => {
        if(msg.status) {
            this.moulesNicknames[msg.nickname] = {
                    status: msg.status,
                    expiration: moment().add(5, "minutes")                                 
            };
        }
        this.update();
    });
    setInterval(() => {
        let now = moment();
        for(let nickname in this.moulesNicknames) {
            if(this.moulesNicknames[nickname].expiration.isAfter(now)) {
                delete this.moulesNicknames[nickname];
            }
        }        
    }, 5 * 60 * 1000);
}

riot.tag('jb3-moules-roster', jb3MoulesRosterTemplate, jb3MoulesRosterStyles, jb3MoulesRosterConstructor);