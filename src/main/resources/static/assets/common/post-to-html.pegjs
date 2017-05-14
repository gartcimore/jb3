post
 = items:postItem*
 { return items.join(""); }
 
postItem
 = url
 / totoz
 / canard
 / bigorno
 / norloge
 / spoiler
 / .

url
 = protocol:$((("http" "s"?) / "ftp") "://") domain:$([^< \t\r\n/])+ remaining:$([^< \t\r\n])*
 { 
    var fullUrl = protocol + domain + remaining;
    if( /(ogg|mp3|wav)$/i.test(remaining) ) {
        return '<audio src="' + fullUrl + '" controls preload="none" title="' + fullUrl + '"></audio>';
    } else {
        return '<a href="' + fullUrl + '" target="_blank">'+ domain +'</a>';
    }
 }
 
canard
= canard:$("\\_" teteCanard "&lt;" / "&gt;" teteCanard "_/")
 {return '<span class="jb3-duck"/>' + canard + '</span>';}
 
teteCanard
 = [oO0ô°øòó@]

norloge
 = "#" norloge:$[a-zA-Z0-9_]+
 {
 	var formattedNorloge = null;
 	if( options.postStore && options.norlogeFormatter ) {
 		var cited = options.postStore.findOne(norloge);
 		if(cited) {
			formattedNorloge = options.norlogeFormatter.format(cited);
 		}
 	}
    if(!formattedNorloge) {
    	formattedNorloge = '#' + norloge;
    }
 	return '<span class="jb3-cite jb3-cite-raw" data-ref="' + norloge + '">' + formattedNorloge + '</span>'
  }

bigorno
 = spaces:$(inputStart / whitespaces) s2:whitespaces? bigorno:$[a-zA-Z0-9-_]+ "&lt;" &(whitespaces / [<[] / !.)
 { return spaces + '<span class="jb3-bigorno">' + bigorno + '&lt;</span>';}

spoiler
 = openSpoiler / closeSpoiler

openSpoiler
 = "<spoiler>"
{ return '<mark class="jb3-spoiler">'; }

closeSpoiler
 = "</spoiler>"
{ return '</mark>'; }

totoz
  = first:"[:" totoz:$[^\]]+ third:"]"
  { return '<span class="jb3-totoz">' + totoz + '</span>' }
  
whitespaces
 = [ \t\r\n]

inputStart
 = & { return location().start.offset == 0; }