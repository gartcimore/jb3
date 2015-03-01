jb3 = {
    init: function () {
        var self = this;
        var controlsMessage = $('#jb3-controls-message');
        var controlsRoom = $('#jb3-controls-room');
        var controlsNickname = $('#jb3-controls-nickname');
        controlsRoom.append(
                $.map(localStorage.rooms ? JSON.parse(localStorage.rooms) : [], function (v, k) {
                    return $("<option>").val(v.rname).text(v.rname);
                })
                );
        controlsRoom.val(localStorage.selectedRoom);
        controlsRoom.change(function () {
            $('#jb3-posts').empty();
            localStorage.selectedRoom = controlsRoom.val();
            self.refreshMessages();
        });
        controlsMessage.bind('keypress', function (event) {
            if (event.altKey) {
                if (self.handleAltShortcut(event.key)) {
                    event.stopPropagation();
                    event.preventDefault();
                }
            } else if (event.keyCode === 13) {
                self.postMessage(controlsNickname.val(), controlsMessage.val(), controlsRoom.val());
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
    highlightPostAndReplies: function (postId, showPopup) {
        var post = $('#' + postId);
        post.addClass("jb3-highlight");
        if(showPopup) {
            $('#jb3-post-popup').html(post.html()).css('display', 'block');
        }
        $(".jb3-cite[data-ref='" + post.attr('id') + "']").addClass("jb3-highlight");
    },
    unhighlightPostAndReplies: function (postId) {
        var post = $('#' + postId);
        post.removeClass("jb3-highlight");
        $(".jb3-cite[data-ref='" + post.attr('id') + "']").removeClass("jb3-highlight");
        $('#jb3-post-popup').hide();
    },
    postMessage: function (nickname, message, room) {
        var self = this;
        $.ajax({
            type: "POST",
            url: "/api/post",
            data: {message: message, nickname: nickname, room: room},
            success: function () {
                self.refreshMessages();
            }
        });
    },
    refreshMessages: function (pollInterval) {
        var self = this;
        var data = {};
        var room = $('#jb3-controls-room').val();
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
        var postContainer = $('#jb3-posts-container');
        var wasAtbottom = postContainer.scrollTop() + postContainer.innerHeight() >= postContainer[0].scrollHeight;
        $.each(data, function (_, value) {
            self.onMessage(value);
        }
        );
        self.updateNorloges();
        self.sortMessages();
        if (wasAtbottom) {
            postContainer.scrollTop(postContainer.prop("scrollHeight"));
        }
    },
    onMessage: function (message) {
        var messagesContainer = $('#jb3-posts');
        var existingMessageDiv = messagesContainer.find('#' + message.id);
        if (existingMessageDiv.length === 0) {
            var timeSpan = $('<span/>').addClass('jb3-post-time').text(moment(message.time).format(this.norlogeFormat));
            var nickSpan = $('<span/>').addClass('jb3-post-nickname').html(message.nickname);
            var messageSpan = $('<span/>').addClass('jb3-post-message').html(jb3_common.formatMessage(message.message));
            var messageDiv = $('<div/>').attr('id', message.id).addClass('jb3-post').attr('time', message.time).append(timeSpan).append(nickSpan).append(messageSpan);
            messagesContainer.append(messageDiv);
        }
    },
    sortMessages: function () {
        $('#jb3-posts').find('.jb3-post').sort(function (a, b) {
            return $(a).attr('time') - $(b).attr('time');
        }).appendTo('#jb3-posts');
    },
    updateNorloges: function () {
        $('.jb3-cite').each(function () {
            var cite = $(this);
            var referencedNorloge = $('#' + cite.data('ref')).find('.jb3-post-time');
            if (referencedNorloge.length > 0) {
                cite.text(referencedNorloge.text());
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
                this.insertTextInMessageControl('♪ <i>' + this.getSelectedText() + '</i> ♪', 5);
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