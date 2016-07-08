jb3 = {
    init: function () {
        var self = this;
        Handlebars.registerHelper('time2norloge', function (time) {
            return moment(time).format(self.norlogeFormat);
        });
        self.messageTemplate = Handlebars.compile($("#message-template").html());
        self.newMessages = [];
        self.messagesContainer = document.getElementById('jb3-posts');
        self.hiddenMessagesContainer = document.getElementById('jb3-hidden-posts');
        self.controlsMessage = $('#jb3-controls-message');
        self.controlsRoom = $('#jb3-controls-room');
        self.controlsNickname = $('#jb3-controls-nickname');
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
        var roomInURI = URI(window.location).search(true).room;
        self.controlsRoom.val(roomInURI || localStorage.selectedRoom || self.controlsRoom.find('option:first').val());
        if (roomInURI === self.controlsRoom.val()) {
            $('#jb3-roster').hide();
        }
        self.controlsRoom.change(function () {
            var selectedRoom = localStorage.selectedRoom = self.previouslySelectedRoom = self.controlsRoom.val();
            var selectedRoomPosts = $('.jb3-post[data-room="' + selectedRoom + '"]').detach();
            var unselectedRoomPosts = $('.jb3-post[data-room!="' + selectedRoom + '"]').detach();
            unselectedRoomPosts.appendTo(self.hiddenMessagesContainer);
            selectedRoomPosts.appendTo(self.messagesContainer);
            self.scrollPostsContainerToBottom();
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
        $("#jb3-controls-message-container button").click(function() {
        	self.postCurrentMessage();
        });
        $('#jb3-posts').on('click', '.jb3-post-time', function (e) {
            var postId = $(e.target).parent().attr('id');
            if (postId) {
                self.controlsMessage.val(self.controlsMessage.val() + '#' + postId + ' ');
                self.controlsMessage.focus();
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
        $('#jb3-posts').on({
            click: function (event) {
                var button = $(event.target);
                var post = button.parents('.jb3-post');
                var revisions = $('#' + post.attr('id') + '-revisions');
                $.colorbox({html: revisions.html(), title: 'Revisions'});
            }
        }, ".jb3-revisions-button");
        $('#jb3-posts').on({
            click: function (event) {
                var link = $(event.target);
                var href = link.attr('href');
                if (href) {
                    if (/\.(png|jpe?g)$/i.test(href)) {
                        $.colorbox({href: href, maxWidth: "90%", maxHeight: "90%"});
                        event.stopPropagation();
                        event.preventDefault();
                    }
                }
            }
        }, "a");
        $('#jb3-posts').on({
            click: function (event) {
                var button = $(event.target);
                var post = button.parents('.jb3-post');
                self.insertTextInMessageControl('/revise #' + post.attr('id') + ' ');
            }
        }, ".jb3-revise-button");
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
    postCurrentMessage: function() {
        var selectedRoom = this.controlsRoom.val();
        this.postMessage(this.controlsNickname.val(), this.controlsMessage.val(), selectedRoom, this.rooms[selectedRoom].auth);
        this.controlsMessage.val('');
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
        switch (event.data.type) {
            case "posts":
                self.newMessages = self.newMessages.concat(event.data.posts);
                break;
            case "connected":
                self.refreshMessages();
                self.coin.postMessage({type: "nickname", nickname: localStorage.nickname});
                break;
            case "presence":
            	self.updateMoulePresence(event.data);
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
    norlogeFullFormat: "YYYY/MM/DD#HH:mm:ss",
    initNickname: function () {
    	var self = this;
        if (localStorage.nickname) {
            self.controlsNickname.val(localStorage.nickname);
        } else {
            $.ajax({
                type: "POST",
                url: "/api/random-nickname",
                success: function (data) {
                    localStorage.nickname = data.nickname;
                    self.controlsNickname.val(localStorage.nickname);
                }
            });
        }
        self.controlsNickname.change(function () {
            localStorage.nickname = self.controlsNickname.val();
            self.coin.postMessage({type: "nickname", nickname: localStorage.nickname});
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
    updateMoulePresence: function(presenceMsg) {
    	$('#moule-' + presenceMsg.mouleId).remove();
    	if(presenceMsg.presence.nickname) {
    		$('#moules').append('<li id="moule-'+ presenceMsg.mouleId + '" class="c-list__item">'+presenceMsg.presence.nickname+'<li>');
    	}
	},
    onNewMessages: function (data) {
        if (data && data.length > 0) {
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
    , onMessage: function (userNickname, message) {
        message.message = jb3_post_to_html.parse(message.message);
        var room = this.rooms[message.room];
        message.postIsMine = message.nickname === userNickname || (room && message.nickname === room.login) ? " jb3-post-is-mine" : "";
        message.postIsBigorno = message.message.search(new RegExp("(moules|" + RegExp.escape(userNickname) + ")&lt;", "i")) >= 0 ? " jb3-post-is-bigorno" : "";
        var container = this.controlsRoom.val() === message.room ? this.messagesContainer : this.hiddenMessagesContainer;
        var messageDiv = this.messageTemplate(message);
        this.insertMessageDiv(container, messageDiv, message);
    },
    insertMessageDiv: function (container, messageDiv, message) {
        var existingDiv = document.getElementById(message.id);
        if (!existingDiv) {
            var t = message.time;
            var posts = container.getElementsByClassName('jb3-post');
            for (var p = 0; p < posts.length; ++p) {
                var post = posts[p];
                if (t < post.dataset.time) {
                    post.insertAdjacentHTML('beforebegin', messageDiv);
                    return;
                }
            }
            container.insertAdjacentHTML('beforeend', messageDiv);
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
                var request = new XMLHttpRequest();
                request.onreadystatechange = function () {
                    if (this.readyState === 4 && this.status === 200) {
                    	try {
	                        var message = JSON.parse(this.responseText);
	                        cite.text(moment(message.time).format(self.norlogeFullFormat));
                    	} catch(e){
                    		cite.removeClass('jb3-cite');
                    	}
                    	finally {
	                        cite.removeClass('jb3-cite-raw');
                    	}
                    }
                };
                request.open('GET', "/restocoin/get/" + postId, true);
                request.overrideMimeType('application/json');
                request.send();
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
