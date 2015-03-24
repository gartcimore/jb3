<#list board.posts?reverse as post>
${(post.id?c!)}${"\t"}${post.time}${"\t"}${"\t"}${(post.info)!}${"\t"}${(post.message)!}
</#list>