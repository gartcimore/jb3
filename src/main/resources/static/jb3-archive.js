jb3_archive = {
    init: function () {
        var self = this;
        $('.jb3-post-time').each(function () {
            var timeSpan = $(this);
            var time = moment.utc(timeSpan.text(), self.norlogeFormat);
            var formattedTime = time.local().format(self.norlogeFormat);
            timeSpan.text(formattedTime);
        });
        $('#archive-search-from').val(moment.utc($('#archive-search-from-hidden').val(),self.norlogeFormat).local().format(self.norlogeFormat));
        $('#archive-search-to').val(moment.utc($('#archive-search-to-hidden').val(),self.norlogeFormat).local().format(self.norlogeFormat));
        $('#archive-search-from').change(function (event) {
            var input = $(event.target);
            var time = moment(input.val(), self.norlogeFormat);
            var formattedTime = time.utc().format(self.norlogeFormat);
            $('#archive-search-from-hidden').val(formattedTime);
        });
        $('#archive-search-to').change(function (event) {
            var input = $(event.target);
            var time = moment(input.val(), self.norlogeFormat);
            var formattedTime = time.utc().format(self.norlogeFormat);
            $('#archive-search-to-hidden').val(formattedTime);
        });
    },
    norlogeFormat: "YYYY/MM/DD#HH:mm:ss"
};
jb3_archive.init();