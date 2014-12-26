<#macro showFortune fortune>
<div class="jb3-fortune">
    <div class="jb3-posts">
    <#list fortune.posts as post>
        <div id="${post.id}" class="jb3-post">
            <span class="jb3-post-time">${post.time?string["yyyy/MM/dd#HH:mm.ss"]}</span>
            <span class="jb3-post-nickname">${post.nickname}</span>
            <span class="jb3-post-message">${post.message}</span>
        </div>
    </#list>
    </div>
</div>
</#macro>