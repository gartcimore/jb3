jb3 = {
    init: function () {
        var self = this;
        Handlebars.registerHelper('time2norloge', function (time) {
            return moment(time).format(self.norlogeFormat);
        });
        self.messageTemplate = Handlebars.compile($("#message-template").html());
        self.newMessages = [];
        self.controlsMessage = $('#jb3-controls-message');
        self.controlsRoom = $('#jb3-controls-room');
        self.controlsNickname = $('#jb3-controls-nickname');
        self.rooms = {};
        self.rooms[self.controlsRoom.val()] = {};
        jb3_common.getRooms().forEach(function (room) {
            self.rooms[room.rname] = {};
        });
        var uri = URI(window.location);
        var roomInURI = uri.search(true).room;
        if (roomInURI) {
            self.rooms[roomInURI] = {};
        }
        self.controlsRoom.empty().append(
                Object.keys(self.rooms).sort().map(function (room) {
            return new Option(room, room);
        })
                );
        var roomInDomain = uri.domain().slice(0, -uri.tld().length - 1);
        roomInDomain = self.rooms[roomInDomain] && roomInDomain;
        self.controlsRoom.attr("size", self.controlsRoom.find('option').length);
        self.controlsRoom.val(roomInURI || roomInDomain || localStorage.selectedRoom || self.controlsRoom.find('option:first').val());
        var postsContainer = document.getElementById('jb3-posts-container');
        for (var room in self.rooms) {
            var postsDivForRoom = document.createElement("div");
            postsDivForRoom.dataset.room = room;
            postsDivForRoom.className += "jb3-posts";
            if (room != self.controlsRoom.val()) {
                postsDivForRoom.setAttribute('style', 'display:none')
            }
            postsContainer.appendChild(postsDivForRoom);
            self.rooms[room].postsDiv = postsDivForRoom;
        }

        $('#jb3-visio-link').attr('href', "/visio?room=" + self.controlsRoom.val());
        if (roomInURI === self.controlsRoom.val()) {
            $('#jb3-roster').hide();
            $('header').hide();
            $("#jb3-layout").css('top', '0px');
        }
        self.controlsRoom.change(function () {
            var selectedRoom = localStorage.selectedRoom = self.controlsRoom.val();
            $('.jb3-posts[data-room!="' + selectedRoom + '"]').hide();
            $('.jb3-posts[data-room="' + selectedRoom + '"]').show();
            self.scrollPostsContainerToBottom();
            self.trollometre.update(selectedRoom);
            $('#jb3-visio-link').attr('href', "/visio?room=" + selectedRoom);
        });
        self.controlsMessage.bind('keypress', function (event) {
            if (event.altKey) {
                if (self.handleAltShortcut(event.key)) {
                    event.stopPropagation();
                    event.preventDefault();
                }
            } else if (event.keyCode === 13) {
                self.postCurrentMessage();
            }
        });
        if ($('header').css('display') === 'block') {
            $('#jb3-show-controls').html("&slarr;");
        } else {
            $('#jb3-show-controls').html("&equiv;");
        }
        $('#jb3-show-controls').click(function () {
            var header = $('header');
            var layout = $('#jb3-layout');
            var button = $('#jb3-show-controls');
            if (header.css('display') === 'block') {
                header.css('display', 'none');
                layout.css('top', '0px');
                button.html("&equiv;");
            } else {
                header.css('display', 'block');
                layout.css('top', '57px');
                button.html("&slarr;");
            }
            var roster = $('#jb3-roster');
            if (roster.css('display') === 'flex') {
                roster.css('display', 'none');
            } else {
                roster.css('display', 'flex');
            }
        });
        $("#jb3-controls-message-post").click(function () {
            self.postCurrentMessage();
        });
        $("#jb3-controls-message-attach").click(function () {
            self.pasteModal.trigger('show');
        });
        $('.jb3-posts').on('click', '.jb3-post-time', function (e) {
            var postId = $(e.target).parent().attr('id');
            if (postId) {
                self.insertTextWithSpacesAroundInMessageControl('#' + postId);
            }
        });
        $('.jb3-posts').on('click', '.jb3-post-nickname', function (e) {
            var nickname = $(e.target).text();
            if (nickname) {
                self.insertTextWithSpacesAroundInMessageControl(nickname + '<');
            }
        });
        $('.jb3-posts').on({
            click: function (event) {
                var button = $(event.target);
                var post = button.parents('.jb3-post');
                var revisions = $('#' + post.attr('id') + '-revisions');
                self.revisionsModal.trigger('show', revisions.html());
            }
        }, ".jb3-revisions-button");
        $('.jb3-posts').on({
            click: function (event) {
                var button = $(event.target);
                var post = button.parents('.jb3-post');
                self.insertTextInMessageControl('/revise #' + post.attr('id') + ' ');
            }
        }, ".jb3-post-is-mine .jb3-revise-button");
        $('.jb3-posts').on({
            click: function (event) {
                var spoiler = $(event.target);
                spoiler.toggleClass('jb3-revealed-spoiler');
            }
        }, ".jb3-spoiler");
        jb3_common.initHighlight();
        jb3_common.initTotozLazyLoading();
        self.initNickname();
        self.coin = new Worker("/assets/coincoin/webdirectcoin.js");
        self.coin.onmessage = function (event) {
            self.onCoinMessage(event);
        };
        var url = URI();
        var wurl = new URI({
            protocol: url.protocol() === "https" ? "wss" : "ws",
            hostname: url.hostname(),
            port: url.port(),
            path: "/webdirectcoin"
        });
        self.coin.postMessage({type: "connect", url: wurl.toString()});
        self.updateMessages();
        self.initTrollometre();
        setTimeout(function () {
            self.refreshDlfpToken();
        }, 1000);
        setInterval(function () {
            self.refreshDlfpToken();
        }, 60 * 60 * 1000);
    },
    postCurrentMessage: function () {
        var selectedRoom = this.controlsRoom.val();
        var auth = localStorage.getItem("dlfp-auth")
        if (this.checkAuth(auth, selectedRoom)) {
            this.postMessage(this.controlsNickname.val(), this.controlsMessage.val(), selectedRoom, auth);
            this.controlsMessage.val('');
        }
    },
    checkAuth: function (auth, selectedRoom) {
        if (selectedRoom === 'dlfp') {
            if (this.checkIfDlfpTokenIsExpired(auth)) {
                window.location.href = "/dlfp/connect";
                return false;
            }
        }
        return true;
    },
    checkIfDlfpTokenIsExpired: function (authStr) {
        if (!authStr) {
            return true;
        }
        var auth = JSON.parse(authStr);
        if (!auth.expires_timestamp) {
            return true;
        }
        return auth.expires_timestamp < Date.now();
    },
    updateMessages: function () {
        var self = this;
        self.onNewMessages(self.newMessages.splice(0, 500));
        setTimeout(function () {
            self.updateMessages();
        }, 1000);
    },
    onCoinMessage: function (event) {
        var self = this;
        var message = event.data;
        if (message.posts) {
            self.newMessages = self.newMessages.concat(message.posts);
        }
        if (message.disconnected) {
            self.moulesRoster.trigger('clear-presences');
        }
        if (message.connected) {
            self.moulesRoster.trigger('clear-presences');
            self.refreshMessages();
            self.coin.postMessage({type: "nickname", nickname: jb3_common.getNickname()});
        }
        if (message.presence) {
            self.moulesRoster.trigger('presence', message.presence);
        }
        if (message.webdirectcoin_not_available) {
            console.log("webdirectcoin is not available");
        }
        if (message.norloge) {
            self.updateCite(message.norloge);
        }
    },
    norlogeFormat: "HH:mm:ss",
    norlogeFullFormat: "YYYY-MM-DD HH:mm:ss",
    initNickname: function () {
        var self = this;
        self.controlsNickname.val(jb3_common.getNickname());
        self.controlsNickname.change(function () {
            jb3_common.setNickname(self.controlsNickname.val());
            self.coin.postMessage({type: "nickname", nickname: jb3_common.getNickname()});
        });
        riot.mount('jb3-raw');
        riot.mount('jb3-modal');
        self.moulesRoster = riot.mount('jb3-moules-roster')[0];
        self.revisionsModal = riot.mount('jb3-revisions-modal')[0];
        self.pasteModal = riot.mount('jb3-paste-modal')[0];
        self.pasteModal.on('pasted', function (pastedText) {
            self.insertTextInMessageControl(pastedText);
        });
    },
    highlightPostAndReplies: function (postId, showPopup) {
        var post = $('#' + postId);
        post.addClass("jb3-highlight");
        if (showPopup) {
            $('#jb3-post-popup-content').html(post.html());
        }
        $(".jb3-cite[data-ref='" + post.attr('id') + "']").addClass("jb3-highlight");
    },
    unhighlightPostAndReplies: function (postId) {
        var post = $('#' + postId);
        post.removeClass("jb3-highlight");
        $(".jb3-cite[data-ref='" + post.attr('id') + "']").removeClass("jb3-highlight");
        $('#jb3-post-popup-content').empty();
    },
    postMessage: function (nickname, message, room, auth) {
        this.coin.postMessage({type: "send", destination: "post", body: {message: message, nickname: nickname, room: room, auth: auth}});
    },
    refreshMessages: function () {
        var selectedRoom = this.controlsRoom.val();
        this.refreshRoom(selectedRoom);
        for (var room in this.rooms) {
            if (room !== selectedRoom) {
                this.refreshRoom(room);
            }
        }
    },
    refreshRoom: function (room) {
        this.coin.postMessage({type: "send", destination: "get", body: {room: room}});
    },
    isPostsContainerAtBottom: function () {
        var postContainer = $('#jb3-posts-container');
        return Math.ceil(postContainer.scrollTop() + postContainer.innerHeight()) >= postContainer[0].scrollHeight;
    },
    scrollPostsContainerToBottom: function () {
        var postContainer = $('#jb3-posts-container');
        postContainer.scrollTop(postContainer.prop("scrollHeight"));
    },
    onNewMessages: function (data) {
        if (data && data.length > 0) {
            var self = this;
            var userNickname = $('#jb3-controls-nickname').val();
            var wasAtbottom = self.isPostsContainerAtBottom();
            for (var d in data) {
                var message = data[d];
                this.trollometre.feed(message);
                self.onMessage(userNickname, message);
            }
            self.updateNorloges();
            self.trollometre.update(this.controlsRoom.val());
            if (wasAtbottom) {
                self.scrollPostsContainerToBottom();
            }
        }
    }
    , onMessage: function (userNickname, message) {
        message.message = jb3_post_to_html.parse(message.message);
        message.postIsMine = message.nickname === userNickname || (message.room && message.nickname === localStorage.getItem(message.room + '-login')) ? " jb3-post-is-mine" : "";
        message.postIsBigorno = message.message.search(new RegExp("(moules|" + RegExp.escape(userNickname) + ")&lt;", "i")) >= 0 ? " jb3-post-is-bigorno" : "";
        var messageDiv = this.messageTemplate(message);
        this.insertMessageDiv(messageDiv, message);
    },
    insertMessageDiv: function (messageDiv, message) {
        var existingDiv = document.getElementById(message.id);
        if (!existingDiv) {
            var container = this.rooms[message.room].postsDiv;
            var dates = container.getElementsByClassName("jb3-posts-date");
            var day = moment(message.time);
            var date = null;
            for (var d = 0; d < dates.length; ++d) {
                if (moment(dates[d].dataset.date).isSame(day, 'day')) {
                    date = dates[d];
                    break;
                }
            }
            if(!date) {
                date = document.createElement('div');
                date.classList.add("jb3-posts-date");
                date.dataset.date = day.format("YYYY-MM-DD");
                var dateTitle = document.createElement('time');
                dateTitle.appendChild(document.createTextNode(day.format("dddd D MMMM YYYY")));
                date.appendChild(dateTitle);
                container.insertAdjacentElement('beforeend', date);
            }
            var t = message.time;
            var posts = date.getElementsByClassName('jb3-post');
            for (var p = 0; p < posts.length; ++p) {
                var post = posts[p];
                if (t < post.dataset.time) {
                    post.insertAdjacentHTML('beforebegin', messageDiv);
                    return;
                }
            }
            date.insertAdjacentHTML('beforeend', messageDiv);
        } else {
            existingDiv.outerHTML = messageDiv;
        }

    },
    updateNorloges: function () {
        var self = this;
        $('.jb3-cite-raw').each(function () {
            var cite = $(this);
            var postId = cite.data('ref');
            var cited = $('#' + postId);
            var citedNorloge = cited.find('.jb3-post-time');
            if (citedNorloge.length > 0) {
                cite.text(citedNorloge.text());
                cite.removeClass('jb3-cite-raw');
            } else {
                self.coin.postMessage({type: "send", destination: "getNorloge", body: {messageId: postId}});
            }
            if (cited.hasClass('jb3-post-is-mine')) {
                cited.addClass('jb3-cited-by-me');
                cite.addClass('jb3-cite-mine');
                cite.closest('.jb3-post').addClass('jb3-post-is-reply-to-mine');
            } else {
                cited.addClass('jb3-cited');
            }
        });
    },
    handleAltShortcut: function (keychar) {
        switch (keychar) {
            case 'o':
                this.insertTextInMessageControl('_o/* <b>BLAM</b>! ');
                return true;
            case 'm':
                this.insertTextInMessageControl('====> <b>Moment ' + this.getSelectedText() + '</b> <====', 16);
                return true;
            case 'f':
                this.insertTextInMessageControl('\u03C6');
                return true;
            case 'b':
                this.insertTextInMessageControl('<b>' + this.getSelectedText() + '</b>', 3);
                return true;
            case 'i':
                this.insertTextInMessageControl('<i>' + this.getSelectedText() + '</i>', 3);
                return true;
            case 'u':
                this.insertTextInMessageControl('<u>' + this.getSelectedText() + '</u>', 3);
                return true;
            case 's':
                this.insertTextInMessageControl('<s>' + this.getSelectedText() + '</s>', 3);
                return true;
            case 't':
                this.insertTextInMessageControl('<tt>' + this.getSelectedText() + '</tt>', 4);
                return true;
            case 'c':
                this.insertTextInMessageControl('<code>' + this.getSelectedText() + '</code>', 6);
                return true;
            case 'd':
                this.insertTextInMessageControl('<spoiler>' + this.getSelectedText() + '</spoiler>', 9);
                return true;
            case 'p':
                this.insertTextInMessageControl('_o/* <b>paf!</b> ');
                return true;
            case 'a':
                this.insertTextInMessageControl('\u266A <i>' + this.getSelectedText() + '</i> \u266A', 5);
                return true;
        }
        return false;
    },
    getSelectedText: function () {
        var controlsMessage = document.getElementById("jb3-controls-message");
        if (controlsMessage) {
            return controlsMessage.value.substring(controlsMessage.selectionStart, controlsMessage.selectionEnd);
        } else {
            return"";
        }
    },
    insertTextInMessageControl: function (text, pos) {
        var control = document.getElementById("jb3-controls-message");
        if (!pos) {
            pos = text.length;
        }
        var selectionEnd = control.selectionStart + pos;
        control.value = control.value.substring(0, control.selectionStart) + text + control.value.substr(control.selectionEnd);
        control.focus();
        control.setSelectionRange(selectionEnd, selectionEnd);
    },
    insertTextWithSpacesAroundInMessageControl: function (text) {
        var control = document.getElementById("jb3-controls-message");
        var textBefore = control.value.substring(0, control.selectionStart);
        if (/.*\S$/.test(textBefore)) {
            textBefore = textBefore.concat(" ");
        }
        var textAfter = control.value.substr(control.selectionStart);
        var firstPart = textBefore.concat(text).concat(' ');
        var caretPos = firstPart.length;
        control.value = firstPart.concat(textAfter);
        control.focus();
        control.setSelectionRange(caretPos, caretPos);
    },
    updateCite: function (norloge) {
        var self = this;
        $(".jb3-cite-raw[data-ref='" + norloge.messageId + "']").each(function () {
            var cite = $(this);
            cite.text(moment(norloge.time).format(self.norlogeFullFormat));
            cite.removeClass('jb3-cite-raw');
        });
    },
    initTrollometre: function () {
        this.trollometre = new Trollometre(document.getElementById("trollometre"));
    },
    refreshDlfpToken: function () {
        var dlfpAuth = localStorage.getItem("dlfp-auth");
        if (dlfpAuth) {
            var xhr = new XMLHttpRequest();
            xhr.onreadystatechange = function (event) {
                if (xhr.readyState === 4) {
                    if (xhr.status === 200) {
                        localStorage.setItem("dlfp-auth", xhr.response);
                    }
                }
            };
            var token = JSON.parse(dlfpAuth);
            var body = new FormData();
            body.append("token", token.refresh_token);
            xhr.open("POST", "/dlfp/refresh-token");
            xhr.send(body);
        }
    }
};
jb3.init();
