jb3_fortune = {
    init: function () {
        var self = this;
        $('.jb3-post-time,.jb3-fortune-time').each(function () {
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
            var referencedNorloge = $('.jb3-post[data-postid=' + cite.data('ref') + ']').first().find('.jb3-post-time');
            if (referencedNorloge.length > 0) {
                cite.text(referencedNorloge.text());
            }
        });
        $('#fortune-search-from').val(moment(parseInt($('#fortune-search-from-hidden').val())).local().format(self.norlogeFormat));
        $('#fortune-search-to').val(moment(parseInt($('#fortune-search-to-hidden').val())).local().format(self.norlogeFormat));
        $('#fortune-search-from').change(function (event) {
            var input = $(event.target);
            var time = moment(input.val(), self.norlogeFormat);
            $('#fortune-search-from-hidden').val(time.valueOf());
        });
        $('#fortune-search-to').change(function (event) {
            var input = $(event.target);
            var time = moment(input.val(), self.norlogeFormat);
            $('#fortune-search-to-hidden').val(time.valueOf());
        });
    },
    norlogeFormat: "YYYY/MM/DD#HH:mm:ss"
};
jb3_fortune.init();