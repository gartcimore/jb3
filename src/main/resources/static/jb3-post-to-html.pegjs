post
 = items:postItem*
 { return items.join(""); }
 
postItem
 = url
 / totoz
 / canard
 / bigorno
 / norloge
 / .

url
 = protocol:$((("http" "s"?) / "ftp") "://") domain:$([^ \t\r\n/])+ remaining:$([^ \t\r\n])*
 { return '<a href="' + protocol + domain + remaining + '" target="_blank">'+ domain +'</a>';}
 
canard
= canard:$("\\_" teteCanard "&lt;" / "&gt;" teteCanard "_/")
 {return '<span class="jb3-duck"/>' + canard + '</span>';}
 
teteCanard
 = [oO0ô°øòó@]

norloge
 = "#" norloge:$[a-zA-Z0-9_]+
 { return '<span class="jb3-cite jb3-cite-raw" data-ref="' + norloge + '">#' + norloge + '</span>' }

bigorno
 = spaces:$(inputStart / whitespaces) s2:whitespaces? bigorno:$[a-zA-Z0-9-_]+ "&lt;" &(whitespaces / [<[] / !.)
 { return spaces + '<span class="jb3-bigorno">' + bigorno + '&lt;</span>';}

totoz
  = first:"[:" totoz:$[^\]]+ third:"]"
  { return '<a class="jb3-totoz">' + totoz + '</a>' }
  
whitespaces
 = [ \t\r\n]

inputStart
 = & { return location().start.offset == 0; }