jb3_common = {
    getRooms: function () {
        try {
            return JSON.parse(localStorage.rooms);
        } catch (e) {
            return [{rname: "batavie"}, {rname: "dlfp"}, {rname: "euromussels"}, {rname: "moules"}, {rname: "sveetch"}, {rname: "nimage"}];
        }
    },
    initTotozLazyLoading: function () {
        $('.jb3-posts').on({
            mouseenter: function (event) {
                var totoz = $(event.target);
                if (totoz.find('img').length === 0) {
                    var totozImg = '<img src="https://nsfw.totoz.eu/img/' + totoz.text() + '"/>';
                    totoz.append(totozImg);
                }
            },
            mouseleave: function (event) {
            }
        }, ".jb3-totoz");
    }
};

RegExp.escape = function(str) {
    return str.replace(/[.*+?^${}()|[\]\\]/g, "\\$&");
};