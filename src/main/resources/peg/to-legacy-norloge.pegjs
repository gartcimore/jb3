{
    var norlogeConverter = {};
    norlogeConverter.convertIdNorloge = function(id) {
        if(options.norlogeConverter) {
            var converted = options.norlogeConverter.convertIdNorloge(id);
            if(converted) {
                return converted;
            }
        }
        return "#" + id;
    };
}

post
 = items:postItem*
 { return items.join(""); }
 
postItem
 = url
 / totoz
 / norloge
 / .

url
 = $(protocol:$((("http" "s"?) / "ftp") "://") url:$([^< \t\r\n])+)

norloge
 = idNorloge

idNorloge
 = "#" id:$[a-zA-Z0-9_]+
 { return norlogeConverter.convertIdNorloge(id); }

totoz
  = $( "[:" totoz:$[^\]]+ "]" )
 
whitespaces
 = [ \t\r\n]
