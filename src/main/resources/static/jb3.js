jb3 = {
    init: function () {
        var self = this;
        var controlsMessage = $('#jb3-controls-message');
        self.controlsRoom = $('#jb3-controls-room');
        var controlsNickname = $('#jb3-controls-nickname');
        var rooms = jb3_common.getRooms();
        self.controlsRoom.append(
                $.map(rooms, function (v, k) {
                    return $("<option>").val(v.rname).text(v.rname);
                })
                );
        self.controlsRoom.attr("size", rooms.length + 1);
        self.controlsRoom.val(URI(window.location).search(true).room || localStorage.selectedRoom || self.controlsRoom.find('option:first').val());
        self.controlsRoom.change(function () {
            $('#jb3-posts').empty();
            localStorage.selectedRoom = self.controlsRoom.val();
            self.refreshMessages();
        });
        controlsMessage.bind('keypress', function (event) {
            if (event.altKey) {
                if (self.handleAltShortcut(event.key)) {
                    event.stopPropagation();
                    event.preventDefault();
                }
            } else if (event.keyCode === 13) {
                self.postMessage(controlsNickname.val(), controlsMessage.val(), self.controlsRoom.val());
                controlsMessage.val('');
            }
        });
        $('#jb3-posts').on('click', '.jb3-post-time', function (e) {
            var postId = $(e.target).parent().attr('id');
            if (postId) {
                controlsMessage.val(controlsMessage.val() + '#' + postId + ' ');
                controlsMessage.focus();
            }
        });
        $('#jb3-posts').on({
            mouseenter: function (event) {
                self.highlightPostAndReplies($(event.target).data('ref'), true);
            },
            mouseleave: function (event) {
                self.unhighlightPostAndReplies($(event.target).data('ref'));
            },
            click: function (event) {
                var postContainer = $('#jb3-posts-container');
                var quoted = $('#' + $(event.target).data('ref'));
                if (quoted.length > 0) {
                    postContainer.scrollTop(quoted[0].offsetTop - event.clientY + postContainer.offset().top + 10);
                }
            }
        }, ".jb3-cite");
        $('#jb3-posts').on({
            mouseenter: function (event) {
                self.highlightPostAndReplies($(event.target).parent().attr('id'), false);
            },
            mouseleave: function (event) {
                self.unhighlightPostAndReplies($(event.target).parent().attr('id'));
            }
        }, ".jb3-post-time");
        self.initNickname();
        self.initWebsockets();
        self.refreshMessages(30000);
    },
    norlogeFormat: "HH:mm:ss",
    initNickname: function () {
        var controlsNickname = $('#jb3-controls-nickname');
        if (localStorage.nickname) {
            controlsNickname.val(localStorage.nickname);
        } else {
            $.ajax({
                type: "POST",
                url: "/api/random-nickname",
                success: function (data) {
                    localStorage.nickname = data.nickname;
                    controlsNickname.val(localStorage.nickname);
                }
            });
        }
        controlsNickname.change(function () {
            localStorage.nickname = controlsNickname.val();
        });
    },
    initWebsockets: function () {
        var self = this;
        var socket = new SockJS('/plop');
        var stompClient = Stomp.over(socket);
        stompClient.connect({}, function (frame) {
            console.log('Connected: ' + frame);
            stompClient.subscribe('/topic/posts', function (postsMessage) {
                self.onNewMessages(JSON.parse(postsMessage.body));
            });
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
    postMessage: function (nickname, message, room) {
        var self = this;
        $.ajax({
            type: "POST",
            url: "/api/post",
            data: {message: message, nickname: nickname, room: room},
            success: function (data) {
                self.onNewMessages(data);
            }
        });
    },
    refreshMessages: function (pollInterval) {
        var self = this;
        var data = {};
        var room = self.controlsRoom.val();
        if (room) {
            data.room = room;
        }
        $.ajax({
            dataType: "json",
            type: "GET",
            url: "/api/get",
            data: data,
            success: function (data) {
                self.onNewMessages(data);
            },
            complete: function () {
                if (pollInterval) {
                    setTimeout(function () {
                        self.refreshMessages(pollInterval);
                    }, pollInterval);
                }
            },
            timeout: 15000
        });
    },
    onNewMessages: function (data) {
        var self = this;
        var userNickname = $('#jb3-controls-nickname').val();
        var postContainer = $('#jb3-posts-container');
        var wasAtbottom = postContainer.scrollTop() + postContainer.innerHeight() >= postContainer[0].scrollHeight;
        var messagesContainer = $('#jb3-posts');
        $.each(data, function (_, value) {
            self.onMessage(messagesContainer, userNickname, value);
        }
        );
        self.updateNorloges();
        if (wasAtbottom) {
            postContainer.scrollTop(postContainer.prop("scrollHeight"));
        }
    }
    , isCurrentRoom: function (room) {
        return this.controlsRoom.val() === room;
    }
    , messageTemplate: '<div id="{{id}}" class="jb3-post{{postIsMine}}" data-time="{{time}}"><span class="jb3-post-icon"></span><span class="jb3-post-time">{{norloge}}</span><span class="jb3-post-nickname">{{nickname}}</span><span class="jb3-post-message">{{{message}}}</span></div>'
    , onMessage: function (messagesContainer, userNickname, message) {
        var existingMessageDiv = messagesContainer.find('#' + message.id);
        if (existingMessageDiv.length === 0 && this.isCurrentRoom(message.room)) {
            var messageDiv = $(Mustache.render(this.messageTemplate, {
                id: message.id,
                time: message.time,
                norloge: moment(message.time).format(this.norlogeFormat),
                nickname: message.nickname,
                message: jb3_common.formatMessage(message.message),
                postIsMine: message.nickname === userNickname ? " jb3-post-is-mine" : ""
            }));
            messageDiv.find(".jb3-bigorno").each(function () {
                var text = $(this).text();
                if (text === "moules" || text.localeCompare(userNickname, 'fr', {usage: 'search', sensitivity: 'base', ignorePunctuation: true}) === 0) {
                    messageDiv.addClass("jb3-post-is-bigorno");
                    return false;
                }
            });
            this.insertMessageDiv(messagesContainer, messageDiv, message);
        }
    },
    insertMessageDiv: function (messagesContainer, messageDiv, message) {
        var t = message.time;
        var inserted = false;
        messagesContainer.find('.jb3-post').each(function (_, m) {
            var msg = $(m);
            if (t < msg.data('time')) {
                msg.before(messageDiv);
                inserted = true;
                return false;
            }
        });
        if (!inserted) {
            messagesContainer.append(messageDiv);
        }
    },
    updateNorloges: function () {
        $('.jb3-cite').each(function () {
            var cite = $(this);
            var cited = $('#' + cite.data('ref'));
            var citedNorloge = cited.find('.jb3-post-time');
            if (citedNorloge.length > 0) {
                cite.text(citedNorloge.text());
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
                this.insertTextInMessageControl('/fortune ');
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
    }
};
jb3.init();
