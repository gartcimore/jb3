jb3_common = {
    formatMessage: function (message) {
        var formattedMessage = message.replace(/<c>/g, '<span class="jb3-cite jb3-cite-raw">');
        formattedMessage = formattedMessage.replace(/<\/c>/g, '</span>');
        
        formattedMessage = formattedMessage.replace(/<z>/g, '<a class="jb3-totoz">[:');
        formattedMessage = formattedMessage.replace(/<\/z>/g, ']</a>');
        
        formattedMessage = formattedMessage.replace(/<h>/g, '<span class="jb3-bigorno">');
        formattedMessage = formattedMessage.replace(/<\/h>/g, '&lt;</span>');
        
        return formattedMessage;
    },
    getRooms: function () {
        try {
            return JSON.parse(localStorage.rooms);
        } catch (e) {
            return [{recid: 1, rname: "batavie"}, {recid: 2, rname: "dlfp"}, {recid: 3, rname: "euromussels"}, {recid: 4, rname: "hadoken"}, {recid: 5, rname: "sveetch"}, {recid: 6, rname: "up"}];
        }
    },
    initTotozLazyLoading: function () {
        $('.jb3-posts').on({
            mouseenter: function (event) {
                var totoz = $(event.target);
                if (totoz.find('img').length === 0) {
                    var totozImg = totoz.text().replace(/\[\:([^\t\)\]]+)\]/, '<img src="https://nsfw.totoz.eu/img/$1"/>');
                    totoz.append(totozImg);
                }
            },
            mouseleave: function (event) {
            }
        }, ".jb3-totoz");
    }
};
