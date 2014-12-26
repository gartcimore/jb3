<#import "fortune.ftl" as fortuneMacros />
<!DOCTYPE html>
<html>
    <head>
        <title>jb3 fortunes</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <meta name="robots" content="noindex,nofollow">
        <link rel="stylesheet" type="text/css" href="/jb3-common.css" />
        <link rel="stylesheet" type="text/css" href="/jb3-fortune.css" />
        <link rel="icon" type="image/png" href="favicon.png" />
    </head>
    <body>
    <form id="fortune-search-form">
        <input name="content" type="text" value="${(rq.content)!}"></input>
        <input type="submit"></input>
    </form>
    <#if fortunes?? >
    <div class="jb3-fortunes">
        <#list fortunes as fortune>
        <@fortuneMacros.showFortune fortune />
        </#list>
    </div>
    </#if>
    <div class="jb3-fortune-pager">
        <#if rq.page &gt; 0 >
        <form>
            <input name="content" type="hidden" value="${(rq.content)!}"></input>
            <input name="page" type="hidden" value="${(rq.page - 1)!}"></input>
            <input type="submit" value="Précédents"></input>
        </form>
        </#if>
        <#if fortunes?? && fortunes?has_content >
        <form>
            <input name="content" type="hidden" value="${(rq.content)!}"></input>
            <input name="page" type="hidden" value="${(rq.page + 1)!}"></input>
            <input type="submit" value="Suivants"></input>
        </form>
        </#if>
    </div>
    </body>
</html>
