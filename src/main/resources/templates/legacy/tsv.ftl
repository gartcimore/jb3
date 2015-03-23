<#list board.posts as post>
${(post.id?c!)}${"\t"}${post.time}${"\t"}${(post.info)!}${"\t"}${"\t"}${(post.message)!}
</#list>