jb3 = {
    init: function () {
        var self = this;
        var controlsMessage = $('#jb3-controls-message');
        self.controlsRoom = $('#jb3-controls-room');
        var controlsNickname = $('#jb3-controls-nickname');
        var rooms = jb3_common.getRooms();
        self.rooms = {};
        self.rooms[self.controlsRoom.val()] = {};
        self.controlsRoom.append(
                $.map(rooms, function (v, k) {
                    self.rooms[v.rname] = {
                        auth: v.rauth
                    };
                    return $("<option>").val(v.rname).text(v.rname);
                })
                );
        self.controlsRoom.attr("size", rooms.length + 1);
        self.controlsRoom.val(URI(window.location).search(true).room || localStorage.selectedRoom || self.controlsRoom.find('option:first').val());
        self.controlsRoom.change(function () {
            var selectedRoom = localStorage.selectedRoom = self.previouslySelectedRoom = self.controlsRoom.val();
            $('.jb3-post[data-room!="' + selectedRoom + '"]').hide();
            $('.jb3-post[data-room="' + selectedRoom + '"]').show();
            self.scrollPostsContainerToBottom();
        });
        controlsMessage.bind('keypress', function (event) {
            if (event.altKey) {
                if (self.handleAltShortcut(event.key)) {
                    event.stopPropagation();
                    event.preventDefault();
                }
            } else if (event.keyCode === 13) {
                var selectedRoom = self.controlsRoom.val();
                self.postMessage(controlsNickname.val(), controlsMessage.val(), selectedRoom, self.rooms[selectedRoom].auth);
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
        var socket = new SockJS('/webdirectcoin');
        var stompClient = Stomp.over(socket);
        stompClient.connect({}, function (frame) {
            console.log('WebDirectCoin connected: ' + frame);
            stompClient.subscribe('/topic/posts', function (postsMessage) {
                self.onNewMessages(JSON.parse(postsMessage.body));
            });
            self.stompClient = stompClient;
            self.refreshMessages();
        }, function (error) {
            console.log('WebDirectCoin error: ' + error + "\nTry to reconnect...");
            setTimeout(function () {
                self.initWebsockets();
            }, 30000);
        }
        );
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
        var data = {message: message, nickname: nickname, room: room, auth: auth};
        this.stompClient.send("/webdirectcoin/post", {}, JSON.stringify(data));
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
        var data = {room: room};
        this.stompClient.send("/webdirectcoin/get", {}, JSON.stringify(data));
    },
    isPostsContainerAtBottom: function () {
        var postContainer = $('#jb3-posts-container');
        return postContainer.scrollTop() + postContainer.innerHeight() >= postContainer[0].scrollHeight;
    },
    scrollPostsContainerToBottom: function () {
        var postContainer = $('#jb3-posts-container');
        postContainer.scrollTop(postContainer.prop("scrollHeight"));
    },
    onNewMessages: function (data) {
        var self = this;
        var userNickname = $('#jb3-controls-nickname').val();
        var wasAtbottom = self.isPostsContainerAtBottom();
        var messagesContainer = $('#jb3-posts');
        for(var d in data) {
            self.onMessage(messagesContainer, userNickname, data[d]);
        }
        self.updateNorloges();
        if (wasAtbottom) {
            self.scrollPostsContainerToBottom();
        }
    }
    , isCurrentRoom: function (room) {
        return this.controlsRoom.val() === room;
    }
    , messageTemplate: '<div id="{{id}}" class="jb3-post{{postIsMine}}{{postIsBirgorno}}" data-room="{{{room}}}" data-time="{{time}}"{{postStyle}}><span class="jb3-post-icon"></span><span class="jb3-post-time">{{norloge}}</span><span class="jb3-post-nickname">{{nickname}}</span><span class="jb3-post-message">{{{message}}}</span></div>'
    , onMessage: function (messagesContainer, userNickname, message) {
        if (!document.getElementById(message.id)) {
            var messageDiv = Mustache.render(this.messageTemplate, {
                id: message.id,
                time: message.time,
                room: message.room,
                norloge: moment(message.time).format(this.norlogeFormat),
                nickname: message.nickname,
                message: jb3_common.formatMessage(message.message),
                postIsMine: message.nickname === userNickname ? " jb3-post-is-mine" : "",
                postIsBirgorno: message.message.search(new RegExp("(moules|"+userNickname+")&lt;", "i")) >= 0 ? " jb3-post-is-bigorno" : "",
                postStyle: this.isCurrentRoom(message.room) ? "" : " style=display:none" 
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
