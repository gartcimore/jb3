jb3_fortune = {
    init: function () {
        var self = this;
        $('.jb3-post-time,.jb3-fortune-time').each(function () {
            var timeSpan = $(this);
            var time = moment.utc(timeSpan.text(), self.norlogeFormat);
            var formattedTime = time.local().format(self.norlogeFormat);
            timeSpan.text(formattedTime);
        });
        $('#fortune-search-from').val(moment.utc($('#fortune-search-from-hidden').val(),self.norlogeFormat).local().format(self.norlogeFormat));
        $('#fortune-search-to').val(moment.utc($('#fortune-search-to-hidden').val(),self.norlogeFormat).local().format(self.norlogeFormat));
        $('#fortune-search-from').change(function (event) {
            var input = $(event.target);
            var time = moment(input.val(), self.norlogeFormat);
            var formattedTime = time.utc().format(self.norlogeFormat);
            $('#fortune-search-from-hidden').val(formattedTime);
        });
        $('#fortune-search-to').change(function (event) {
            var input = $(event.target);
            var time = moment(input.val(), self.norlogeFormat);
            var formattedTime = time.utc().format(self.norlogeFormat);
            $('#fortune-search-to-hidden').val(formattedTime);
        });
    },
    norlogeFormat: "YYYY/MM/DD#HH:mm:ss"
};
jb3_fortune.init();