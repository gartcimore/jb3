jb3_common = {
    formatMessage: function (message) {
        try {
            var parser = new DOMParser();
            var doc = parser.parseFromString('<span class="jb3-post-message">' + message + '</span>', 'text/xml');
            var cites = doc.getElementsByTagName('c');
            while (cites.length) {
                var cite = cites[0];
                var citeSpan = doc.createElement('span');
                citeSpan.textContent = cite.textContent;
                citeSpan.setAttribute('class', 'jb3-cite jb3-cite-raw');
                cite.parentNode.replaceChild(citeSpan, cite);
            }
            var totozs = doc.getElementsByTagName('z');
            while(totozs.length) {
                var totoz = totozs[0];
                var totozA = doc.createElement('a');
                totozA.textContent = '[:' + totoz.textContent + ']';
                totozA.setAttribute('class', 'jb3-totoz');
                totoz.parentNode.replaceChild(totozA, totoz);
            }
            var bigornos = doc.getElementsByTagName('h');
            while(bigornos.length) {
                var bigorno = bigornos[0];
                var birgornoSpan = doc.createElement('a');
                birgornoSpan.textContent = bigorno.textContent + '<';
                birgornoSpan.setAttribute('class', 'jb3-bigorno');
                bigorno.parentNode.replaceChild(birgornoSpan, bigorno);
            }
            var serializer = new XMLSerializer();
            var post = serializer.serializeToString(doc);
            return post;
        } catch(e) {
            console.log(e);
            return "CENSURAI";
        }
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
