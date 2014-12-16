<!DOCTYPE html>
<html>
    <head>
        <title>jb3 fortune</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <meta name="robots" content="noindex,nofollow">
        <link rel="stylesheet" type="text/css" href="jb3.css" />
        <link rel="icon" type="image/png" href="favicon.png" />
    </head>
    <body>
    <#if fortune?? >
        <div id="jb3-posts">
        <#list fortune.posts as post>
            <div id="${post.id}" class="jb3-post">
                <span class="jb3-post-time">${post.time?string["yyyy/MM/dd#HH:mm.ss"]}</span>
                <span class="jb3-post-nickname">${post.nickname}</span>
                <span class="jb3-post-message">${post.message}</span>
            </div>
        </#list>
        </div>
    <#else>
        Aucune fortune trouvée.
    </#if>        
    </body>
</html>
