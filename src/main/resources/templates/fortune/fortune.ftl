<#macro showFortune fortune>
<div class="jb3-fortune">
    <span class="jb3-fortune-title"><span class="jb3-fortune-time">${(fortune.time.getMillis()?c)!}</span> <a href="/fortune/${fortune.id}">#${fortune.id}</a></span>
    <div class="jb3-posts">
    <#list fortune.posts as post>
        <div id="${fortune.id}-${post.id}" data-postid="${post.id}" class="jb3-post">
            <span class="jb3-post-time">${post.time.getMillis()?c}</span>
            <span class="jb3-post-nickname">${post.nickname}</span>
            <span class="jb3-post-message">${post.message}</span>
        </div>
    </#list>
    </div>
</div>
</#macro>