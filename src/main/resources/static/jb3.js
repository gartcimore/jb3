jb3 = {
    init: function () {
        var self = this;
        var controlsMessage = $('#jb3-controls-message');
        var controlsNickname = $('#jb3-controls-nickname');
        controlsMessage.bind('keypress', function (event) {
            if (event.altKey) {
                if (self.handleAltShortcut(event.key)) {
                    event.stopPropagation();
                    event.preventDefault();
                }
            } else if (event.keyCode === 13) {
                self.postMessage(controlsNickname.val(), controlsMessage.val());
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
                self.highlightPostAndReplies($(event.target).data('ref'));
            },
            mouseleave: function (event) {
                self.unhighlightPostAndReplies($(event.target).data('ref'));
            }
        }, ".jb3-cite");
        $('#jb3-posts').on({
            mouseenter: function (event) {
                self.highlightPostAndReplies($(event.target).parent().attr('id'));
            },
            mouseleave: function (event) {
                self.unhighlightPostAndReplies($(event.target).parent().attr('id'));
            }
        }, ".jb3-post-time");
        self.initNickname();
        self.refreshMessages(30000);
    },
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
        controlsNickname.change(function() {
            localStorage.nickname = controlsNickname.val();
        });
    }
    ,
    highlightPostAndReplies: function (postId) {
        var post = $('#' + postId);
        post.addClass("jb3-highlight");
        $(".jb3-cite[data-ref='" + post.attr('id') + "']").addClass("jb3-highlight");
    },
    unhighlightPostAndReplies: function (postId) {
        var post = $('#' + postId);
        post.removeClass("jb3-highlight");
        $(".jb3-cite[data-ref='" + post.attr('id') + "']").removeClass("jb3-highlight");
    },
    postMessage: function (nickname, message) {
        var self = this;
        $.ajax({
            type: "POST",
            url: "/api/post",
            data: {message: message, nickname: nickname},
            success: function () {
                self.refreshMessages();
            }
        });
    },
    refreshMessages: function (pollInterval) {
        var self = this;
        $.ajax({
            dataType: "json",
            type: "GET",
            url: "/api/get",
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
        $.each(data, function (index, value) {
            self.onMessage(value);
        }
        );
        self.sortMessages();
        var postContainer = $('#jb3-posts-container');
        postContainer.scrollTop(postContainer.prop("scrollHeight"));
    },
    onMessage: function (message) {
        var messagesContainer = $('#jb3-posts');
        var existingMessageDiv = messagesContainer.find('#' + message.id);
        if (existingMessageDiv.length === 0) {
            var isoTime = new Date(message.time).toISOString();
            var timeSpan = $('<span/>').addClass('jb3-post-time').text(isoTime.substr(11, 8)).attr("title", isoTime);
            var nickSpan = $('<span/>').addClass('jb3-post-nickname').html(message.nickname);
            var formattedMessage = message.message.replace(/(\s|^)#(\w+)/g, '$1<span class="jb3-cite" data-ref="$2">#$2</span>');
            formattedMessage = formattedMessage.replace(/(\s|^)(https?:\/\/\S+)/gi, '$1<a href="$2" target="_blank" rel="nofollow">[url]</a>');
            formattedMessage = formattedMessage.replace(/(\s|^)(ftp:\/\/\S+)/gi, '$1<a href="$2" target="_blank" rel="nofollow">[url]</a>');
            formattedMessage = formattedMessage.replace(/(\s|^)\[\:([a-zA-Z0-9-_ ]*)\]/g, '$1<a class="jb3-totoz">[:$2]<img src="http://sfw.totoz.eu/gif/$2.gif"/></a>');
            var messageSpan = $('<span/>').addClass('jb3-post-message').html(formattedMessage);
            var messageDiv = $('<div/>').attr('id', message.id).addClass('jb3-post').attr('time', message.time).append(timeSpan).append(nickSpan).append(messageSpan);
            messagesContainer.append(messageDiv);
        }
    },
    sortMessages: function () {
        $('#jb3-posts').find('.jb3-post').sort(function (a, b) {
            return $(a).attr('time') - $(b).attr('time');
        }).appendTo('#jb3-posts');
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