jb3_common = {
    formatMessage: function (message) {
        var formattedMessage = message.replace(/(\s|^)#(\w+)/g, '$1<span class="jb3-cite" data-ref="$2">#$2</span>');
        formattedMessage = formattedMessage.replace(/(\s|^)(https?:\/\/\S+)/gi, '$1<a href="$2" target="_blank" rel="nofollow">[url]</a>');
        formattedMessage = formattedMessage.replace(/(\s|^)(ftp:\/\/\S+)/gi, '$1<a href="$2" target="_blank" rel="nofollow">[url]</a>');
        formattedMessage = formattedMessage.replace(/\[\:([^\t\)\]]+)\]/g, '<a class="jb3-totoz">[:$1]<img src="https://nsfw.totoz.eu/gif/$1.gif"/></a>');
        formattedMessage = formattedMessage.replace(/(\s|^)([a-zA-Z0-9-_]*)&lt;(\s|$)/g, '$1<span class="jb3-bigorno">$2&lt;</span> ');
        return formattedMessage;
    },
    getRooms: function () {
        try {
            return JSON.parse(localStorage.rooms);
        } catch (e) {
            return [{recid: 1, rname: "batavie"}, {recid: 2, rname: "dlfp"}, {recid: 3, rname: "euromussels"}, {recid: 4, rname: "hadoken"}, {recid: 5, rname: "sveetch"}, {recid: 6, rname: "up"}];
        }
    }
};
