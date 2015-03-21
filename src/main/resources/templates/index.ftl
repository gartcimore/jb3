<!DOCTYPE html>
<html>
    <head>
        <title>jb3 frontend</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <meta name="robots" content="noindex,nofollow">
        <link rel="stylesheet" type="text/css" href="jb3-common.css" />
        <link rel="stylesheet" type="text/css" href="jb3-frontend.css" />
        <link rel="stylesheet" type="text/css" href="/webjars/bootstrap/3.3.4/css/bootstrap.css"></script>
        <link rel="icon" type="image/png" href="/favicon.png" />
    </head>
    <body>
        <div id="jb3-layout">
            <div id="jb3-layout-horizontal">
                <div id="jb3-roster">
                    <a href="/rooms">Rooms</a>
                    <select id="jb3-controls-room"><option></option></select>
                    <div id="jb3-menu">
                        <a id="jb3-archives-menu-item" href="/archive" target="_blank">Archives</a>
                        <a id="jb3-fortunes-menu-item" href="/fortune" target="_blank">Fortunes</a>
                    </div>
                </div>
                <div id="jb3-layout-vertical">
                    <div id="jb3-posts-container">
                        <div id="jb3-posts" class="jb3-posts"></div>
                    </div>
                    <div id="jb3-controls">
                        <input id="jb3-controls-message" type="text" spellcheck="true"></input>
                        <label for="jb3-controls-nickname">Nickname:</label>
                        <input id="jb3-controls-nickname" type="text"></input>
                        <div id="jb3-post-popup">
                            <div id="jb3-post-popup-content"></div>
                    </div>
                </div>
            </div>
        </div>
    </body>
    <script src="/webjars/jquery/1.11.1/jquery.js" defer></script>
    <script src="/webjars/bootstrap/3.3.4/js/bootstrap.js" defer></script>
    <script src="/webjars/mustachejs/0.8.2/mustache.js" defer></script>
    <script src="/webjars/URI.js/1.14.1/URI.js" defer></script>
    <script src="/webjars/momentjs/2.8.3/moment.js" defer/></script>
    <script src="/webjars/stomp-websocket/2.3.1-1/stomp.js" defer/></script>
    <script src="/webjars/sockjs-client/0.3.4-1/sockjs.js" defer/></script>
    <script src="/jb3-common.js" defer/></script>
    <script src="/jb3.js" defer/></script>
</html>
