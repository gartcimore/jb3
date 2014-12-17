<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<board site="${board.site}" <#if timezone??>timezone="${board.timezone}"</#if>>
<#list board.posts as post>
    <#attempt>
    <post id="${(post.id?c!)}" time="${post.time}">
        <info>${(post.info)!}</info>
        <message>${(post.message)!}</message>
        <login/>
    </post>
    <#recover>
        <!-- Invalid post ${(post.id?c)!} -->
    </#attempt>
</#list>
</board>