jb3 = {
    init: function () {
        var self = this;

        var controlsMessage = $('#jb3-controls-message');
        var controlsNickname = $('#jb3-controls-nickname');
        controlsMessage.bind('keypress', function (e) {
            if (e.keyCode === 13) {
                self.postMessage(controlsNickname.val(), controlsMessage.val());
                controlsMessage.val('');
            }
        });
        
        $('#jb3-posts').on('click', '.jb3-post-time', function (e) {
            var postId = $(e.target).parent().attr('id');
            if(postId) {
                controlsMessage.val(controlsMessage.val() + '#' + postId + ' ');
                controlsMessage.focus();
            }
        });
        
        self.refreshMessages(30000);
    },
    postMessage: function (nickname, message) {
        var self = this;
        $.ajax({
            type: "POST",
            url: "/api/post",
            data: {message: message, nickname: nickname},
            success: function() {
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
            complete: function() {
                if(pollInterval) {
                    setTimeout(function() {
                        self.refreshMessages(pollInterval);
                    }, pollInterval );
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
            var timeSpan = $('<span/>').addClass('jb3-post-time').text(isoTime.substr(11,8)).attr("title",isoTime);
            var nickSpan = $('<span/>').addClass('jb3-post-nickname').html(message.nickname);
            var messageSpan = $('<span/>').addClass('jb3-post-message').html(message.message);
            var messageDiv = $('<div/>').attr('id', message.id).addClass('jb3-post').attr('time', message.time).append(timeSpan).append(nickSpan).append(messageSpan);
            messagesContainer.append(messageDiv);
        }
    },
    sortMessages: function () {
        $('#jb3-posts').find('.jb3-post').sort(function (a, b) {
            return $(a).attr('time') - $(b).attr('time');
        }).appendTo('#jb3-posts');
    }
};
jb3.init();


