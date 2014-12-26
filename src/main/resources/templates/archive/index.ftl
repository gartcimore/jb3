<!DOCTYPE html>
<html>
    <head>
        <title>jb3 archives</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <meta name="robots" content="noindex,nofollow">
        <link rel="stylesheet" type="text/css" href="/jb3-common.css" />
        <link rel="stylesheet" type="text/css" href="/jb3-archive.css" />
        <link rel="icon" type="image/png" href="/favicon.png" />
    </head>
    <body>
    <form id="archive-search-form">
        <label for="archive-search-from">De</label>
        <input id="archive-search-from" name="from" type="text" value="${rq.from.toDate()?string["yyyy/MM/dd#HH:mm:ss"]}"></input>
        <label for="archive-search-to">à</label>
        <input id="archive-search-to" name="to" type="text" value="${rq.to.toDate()?string["yyyy/MM/dd#HH:mm:ss"]}"></input>
        <label for="archive-search-content">Filtre:</label>
        <input id="archive-search-content" name="content" type="text" value="${(rq.content)!}"></input>
        <input type="submit"></input>
    </form>
    <#if posts?? >
    <div class="jb3-posts">
        <#list posts as post>
        <div id="${post.id}" class="jb3-post">
            <span class="jb3-post-time">${post.time?string["yyyy/MM/dd#HH:mm:ss"]}</span>
            <span class="jb3-post-nickname">${post.nickname}</span>
            <span class="jb3-post-message">${post.message}</span>
        </div>
        </#list>
    </div>
    </#if>
    <div class="jb3-archive-pager">
        <#if rq.page &gt; 0 >
        <form>
            <input name="from" type="hidden" value="${(rq.from.toDate()?datetime)!}"></input>
            <input name="to" type="hidden" value="${(rq.to.toDate()?datetime)!}"></input>
            <input name="content" type="hidden" value="${(rq.content)!}"></input>
            <input name="page" type="hidden" value="${(rq.page - 1)!}"></input>
            <input type="submit" value="Précédents"></input>
        </form>
        </#if>
        <#if posts?? && posts?has_content >
        <form>
            <input name="from" type="hidden" value="${(rq.from.toDate()?datetime)!}"></input>
            <input name="to" type="hidden" value="${(rq.to.toDate()?datetime)!}"></input>
            <input name="content" type="hidden" value="${(rq.content)!}"></input>
            <input name="page" type="hidden" value="${(rq.page + 1)!}"></input>
            <input type="submit" value="Suivants"></input>
        </form>
        </#if>
    </div>
    </body>
</html>
