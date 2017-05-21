{
    var norlogeConverter = {};
    norlogeConverter.convertFullNorloge = function(y, m, d, h, mi, s, bouchot) {
        if(options.norlogeConverter) {
            var converted = options.norlogeConverter.convertFullNorloge(y, m, d, h, mi, s, bouchot);
            if(converted) {
                return converted;
            }
        }
        return y + "-" + m + "-" + d + 'T' + h + ':' + mi  + ':' + s + (bouchot ? "@" + bouchot : "");
    };
    norlogeConverter.convertLongNorloge = function(m, d, h, mi, s, bouchot) {
        if(options.norlogeConverter) {
            var converted = options.norlogeConverter.convertLongNorloge(m, d, h, mi, s, bouchot);
            if(converted) {
                return converted;
            }
        }
        return m + "/" + d + "#" + h + ':' + mi  + ':' + s + (bouchot ? "@" + bouchot : "");
    };
    norlogeConverter.convertNormalNorloge = function(h, mi, s, bouchot) {
        if(options.norlogeConverter) {
            var converted = options.norlogeConverter.convertNormalNorloge(h, mi, s, bouchot);
            if(converted) {
                return converted;
            }
        }
        return h + ':' + mi  + ':' + s + (bouchot ? "@" + bouchot : "");
    };
    norlogeConverter.convertShortNorloge = function(h, mi, bouchot) {
        if(options.norlogeConverter) {
            var converted = options.norlogeConverter.convertShortNorloge(h, mi, bouchot);
            if(converted) {
                return converted;
            }
        }
        return h + ':' + mi  + ':' + (bouchot ? "@" + bouchot : "");
    };
    norlogeConverter.convertIdNorloge = function(id, bouchot) {
        if(options.norlogeConverter) {
            var converted = options.norlogeConverter.convertIdNorloge(id, bouchot);
            if(converted) {
                return converted;
            }
        }
        return "#" + id + (bouchot ? "@" + bouchot : "");
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
 = idNorloge / fullNorloge / longNorloge / normalNorloge / shortNorloge
 
bouchot
 = "@" b:$[a-z]+
 { return b; }

idNorloge
 = "#" id:$[a-zA-Z0-9_]+ b:bouchot?
 { return norlogeConverter.convertIdNorloge(id, b); }

fullNorloge
 = y: norlogeYear "-" m: norlogeMonth "-" d:norlogeDay "T" h:norlogeHours ":" mi:norlogeMinutes ":" s:norlogeSeconds b:bouchot?
 {
 return norlogeConverter.convertFullNorloge(y, m, d, h, mi, s, b);
 }
 
longNorloge
 = m: norlogeMonth "/" d:norlogeDay "#" h:norlogeHours ":" mi:norlogeMinutes ":" s:norlogeSeconds b:bouchot?
 {
 return norlogeConverter.convertLongNorloge(m, d, h, mi, s, b);
 }
 
norlogeYear
 = $( [0-9]+ )
 
norlogeMonth
 = $( [0-1] [0-9] )

norlogeDay
 = $( [0-3] [0-9] )

normalNorloge
 = h:norlogeHours ":" mi:norlogeMinutes ":" s:norlogeSeconds b:bouchot?
 {
 return norlogeConverter.convertNormalNorloge(h, mi, s, b);
 }
 
shortNorloge
 = h:norlogeHours ":" mi:norlogeMinutes b:bouchot?
 {
 return norlogeConverter.convertShortNorloge(h, mi, b);
 }

norlogeHours
 = $( [0-2] [0-9] )
 
norlogeMinutes
 = $( [0-5] [0-9] )
 
norlogeSeconds
 = $([0-5] [0-9])

totoz
  = $( "[:" totoz:$[^\]]+ "]" )
 
whitespaces
 = [ \t\r\n]
