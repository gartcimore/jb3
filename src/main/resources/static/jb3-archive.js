jb3_archive = {
    init: function () {
        var self = this;
        $('.jb3-post-time').each(function () {
            var timeSpan = $(this);
            var time = moment(parseInt(timeSpan.text()));
            var formattedTime = time.local().format(self.norlogeFormat);
            timeSpan.text(formattedTime);
        });
        $('.jb3-post-message').each(function () {
            var messageSpan = $(this);
            messageSpan.html(jb3_common.formatMessage(messageSpan.text()));
        });
        $('.jb3-cite').each(function () {
            var cite = $(this);
            var referencedNorloge = $('#' + cite.data('ref')).find('.jb3-post-time');
            if (referencedNorloge.length > 0) {
                cite.text(referencedNorloge.text());
            }
        });
        $('#archive-search-from').val(moment(parseInt($('#archive-search-from-hidden').val())).local().format(self.norlogeFormat));
        $('#archive-search-to').val(moment(parseInt($('#archive-search-to-hidden').val())).local().format(self.norlogeFormat));
        $('#archive-search-from').change(function (event) {
            var input = $(event.target);
            var time = moment(input.val(), self.norlogeFormat);
            $('#archive-search-from-hidden').val(time.valueOf());
        });
        $('#archive-search-to').change(function (event) {
            var input = $(event.target);
            var time = moment(input.val(), self.norlogeFormat);
            $('#archive-search-to-hidden').val(time.valueOf());
        });
        $('.jb3-posts').on({
            mouseenter: function (event) {
                self.highlightPostAndReplies($(event.target).data('ref'));
            },
            mouseleave: function (event) {
                self.unhighlightPostAndReplies($(event.target).data('ref'));
            }
        }, ".jb3-cite");

        $('.jb3-posts').on({
            mouseenter: function (event) {
                self.highlightPostAndReplies($(event.target).parent().attr('id'));
            },
            mouseleave: function (event) {
                self.unhighlightPostAndReplies($(event.target).parent().attr('id'));
            }
        }, ".jb3-post-time");
        jb3_common.initTotozLazyLoading();
    },
    norlogeFormat: "YYYY/MM/DD#HH:mm:ss",
    highlightPostAndReplies: function (postId) {
        var post = $('#' + postId);
        post.addClass("jb3-highlight");
        $(".jb3-cite[data-ref='" + post.attr('id') + "']").addClass("jb3-highlight");
    },
    unhighlightPostAndReplies: function (postId) {
        var post = $('#' + postId);
        post.removeClass("jb3-highlight");
        $(".jb3-cite[data-ref='" + post.attr('id') + "']").removeClass("jb3-highlight");
    }
};
jb3_archive.init();