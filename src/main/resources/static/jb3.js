jb3 = {
    init: function () {
        var self = this;
        self.newMessages = [];
        self.messagesContainer = document.getElementById('jb3-posts');
        var controlsMessage = $('#jb3-controls-message');
        self.controlsRoom = $('#jb3-controls-room');
        var controlsNickname = $('#jb3-controls-nickname');
        var rooms = jb3_common.getRooms();
        self.rooms = {};
        self.rooms[self.controlsRoom.val()] = {};
        self.controlsRoom.append(
                $.map(rooms, function (v, k) {
                    self.rooms[v.rname] = {
                        auth: v.rauth,
                        login: v.rlogin
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
        jb3_common.initTotozLazyLoading();
        self.initNickname();
        self.coin = new Worker("/webdirectcoin.js");
        self.coin.onmessage = function (event) {
            self.onCoinMessage(event);
        };
        var url = URI();
        url = url.protocol(url.protocol() === "https" ? "wss" : "ws").path("/webdirectcoin");
        self.coin.postMessage({type: "connect", url: url.toString()});
        self.updateMessages();
    },
    updateMessages: function() {
    	var self = this;
    	self.onNewMessages(self.newMessages.splice(0, 500));
        setTimeout(function() {
            self.updateMessages();
        }, 1000);
    },
    onCoinMessage: function (event) {
        var self = this;
        switch (event.data.type) {
            case "posts":
            	self.newMessages = self.newMessages.concat(event.data.posts);
                break;
            case "connected":
                self.refreshMessages();
                break;
            case "webdirectcoin_not_available":
                console.log("webdirectcoin is not available, use restocoin instead");
                self.coin.terminate();
                self.coin = new Worker("/restocoin.js");
                self.coin.onmessage = function (event) {
                    self.onCoinMessage(event);
                };
                self.coin.postMessage({type: "connect"});
                break;
        }
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
        return postContainer.scrollTop() + postContainer.innerHeight() >= postContainer[0].scrollHeight;
    },
    scrollPostsContainerToBottom: function () {
        var postContainer = $('#jb3-posts-container');
        postContainer.scrollTop(postContainer.prop("scrollHeight"));
    },
    onNewMessages: function (data) {
    	if(data && data.length > 0) {
	        var self = this;
	        var userNickname = $('#jb3-controls-nickname').val();
	        var wasAtbottom = self.isPostsContainerAtBottom();
	        for (var d in data) {
	            self.onMessage(userNickname, data[d]);
	        }
	        self.updateNorloges();
	        if (wasAtbottom) {
	            self.scrollPostsContainerToBottom();
	        }
    	}
    }
    , messageTemplate: '<div id="{{id}}" class="jb3-post{{postIsMine}}{{postIsBigorno}}" data-room="{{{room}}}" data-time="{{time}}"{{postStyle}}><span class="jb3-post-icon"></span><span class="jb3-post-time">{{norloge}}</span><span class="jb3-post-nickname">{{nickname}}</span><span class="jb3-post-message">{{{message}}}</span></div>'
    , onMessage: function (userNickname, message) {
        if (!document.getElementById(message.id)) {
            message.message = jb3_common.formatMessage(message.message);
            message.norloge = moment(message.time).format(this.norlogeFormat);
            message.postIsMine = message.nickname === userNickname || message.nickname === this.rooms[message.room].login ? " jb3-post-is-mine" : "";
            message.postIsBigorno = message.message.search(new RegExp("(moules|" + RegExp.escape(userNickname) + ")&lt;", "i")) >= 0 ? " jb3-post-is-bigorno" : "";
            message.postStyle = this.controlsRoom.val() === message.room ? "" : " style=display:none";
            var messageDiv = Mustache.render(this.messageTemplate, message);
            this.insertMessageDiv(messageDiv, message);
        }
    },
    insertMessageDiv: function (messageDiv, message) {
        var t = message.time;
        var posts = this.messagesContainer.getElementsByClassName('jb3-post');
        for (var p = 0; p < posts.length; ++p) {
            var post = posts[p];
            if (t < post.dataset.time) {
                post.insertAdjacentHTML('beforebegin', messageDiv);
                return;
            }
        }
        this.messagesContainer.insertAdjacentHTML('beforeend', messageDiv);
    },
    updateNorloges: function () {
        $('.jb3-cite-raw').each(function () {
            var cite = $(this);
            var cited = $('#' + cite.data('ref'));
            var citedNorloge = cited.find('.jb3-post-time');
            if (citedNorloge.length > 0) {
                cite.text(citedNorloge.text());
                cite.removeClass('jb3-cite-raw');
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
