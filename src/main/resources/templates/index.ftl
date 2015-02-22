<!DOCTYPE html>
<html>
    <head>
        <title>jb3 frontend</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <meta name="robots" content="noindex,nofollow">
        <link rel="stylesheet" type="text/css" href="jb3-common.css" />
        <link rel="stylesheet" type="text/css" href="jb3-frontend.css" />
        <link rel="icon" type="image/png" href="/favicon.png" />
    </head>
    <body>
        <div id="jb3-posts-container">
            <div id="jb3-post-popup"></div>
            <div id="jb3-posts"></div>
        </div>
        <div id="jb3-controls">
            <input id="jb3-controls-message" type="text" spellcheck="true"></input>
            <label for="jb3-controls-nickname">Nickname:</label>
            <input id="jb3-controls-nickname" type="text"></input>
            <div id="jb3-menu">
                <a id="jb3-archives-menu-item" href="/archive" target="_blank">Archives</a>
                <a id="jb3-fortunes-menu-item" href="/fortune" target="_blank">Fortunes</a>
            </div>
        </div>
    </body>
    <script src="/webjars/jquery/2.1.1/jquery.js" defer></script>
    <script src="/webjars/momentjs/2.8.3/moment.js" defer/></script>
    <script src="/jb3-common.js" defer/></script>
    <script src="/jb3.js" defer/></script>
</html>
