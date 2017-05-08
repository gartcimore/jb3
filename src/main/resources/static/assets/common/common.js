jb3_common = {
    getNickname: function () {
        return localStorage.nickname || (localStorage.nickname = this.randomNickname());
    },
    setNickname: function(nickname) {
        localStorage.nickname = nickname;
    },
    randomNickname: function () {
        var letters = ["aeiouy", 'bcdfghjklmnpqrstvwxz'];
        var nicknameLength = 3 + Math.floor(Math.random() * 5);
        var lettersIndex = Math.floor(Math.random() * letters.length);
        var nickname = "";
        for (var l = 0; l < nicknameLength; ++l) {
            var c = letters[lettersIndex].charAt(Math.floor(Math.random() * letters[lettersIndex].length));
            nickname = nickname.concat(c);
            lettersIndex = (lettersIndex + 1) % letters.length;
        }
        return nickname;
    },
    getRooms: function () {
        var rooms;
        try {
            rooms = JSON.parse(localStorage.rooms);
        } catch (e) {
        }
        if (!rooms) {
            rooms = this.getDefaultRooms();
        }
        return rooms;
    },
    getDefaultRooms: function () {
        return [{rname: "dlfp"}, {rname: "euromussels"}, {rname: "moules"}, {rname: "sveetch"}, {rname: "taab"}, {rname: "batavie"}];
    },
    initTotozLazyLoading: function () {
        $('.jb3-posts').on({
            mouseenter: function (event) {
                var totoz = $(event.target);
                if (totoz.find('img').length === 0) {
                    var totozImg = '<img src="/totoz/img/' + encodeURI(totoz.text()) + '"/>';
                    totoz.append(totozImg);
                }
            },
            mouseleave: function (event) {
            }
        }, ".jb3-totoz");
    },
    initHighlight: function () {
        var self = this;
        $('.jb3-posts').on({
            mouseenter: function (event) {
                self.highlightPostAndReplies($(event.target).data('ref'), true);
            },
            mouseleave: function (event) {
                self.unhighlightPostAndReplies($(event.target).data('ref'));
            },
            click: function (event) {
                var postContainer = $('#jb3-posts-container');
                var postId = $(event.target).data('ref');
                var quoted = $('#' + postId);
                if (quoted.length > 0) {
                    postContainer.scrollTop(quoted[0].offsetTop - event.clientY + postContainer.offset().top + 10);
                } else {
                    window.open("/archives/post/" + postId, "_blank");
                }
            }
        }, ".jb3-cite");
        $('.jb3-posts').on({
            mouseenter: function (event) {
                self.highlightPostAndReplies($(event.target).parent().attr('id'), false);
            },
            mouseleave: function (event) {
                self.unhighlightPostAndReplies($(event.target).parent().attr('id'));
            }
        }, ".jb3-post-time");
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
    }
};

RegExp.escape = function (str) {
    return str.replace(/[.*+?^${}()|[\]\\]/g, "\\$&");
};