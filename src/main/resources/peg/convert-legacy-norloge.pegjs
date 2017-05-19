{
    var norlogeConverter = {};
    norlogeConverter.convertFullNorloge = function(y, m, d, h, mi, s, bouchot) {
        if(options.norlogeConverter) {
            var converted = options.norlogeConverter.convertFullNorloge(y, m, d, h, mi, s, bouchot);
            if(converted) {
                return converted;
            }
        }
        return y + "-" + m + "-" + d + h + ':' + mi  + ':' + s + (bouchot ? "@" + bouchot : "");
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
 = protocol:$((("http" "s"?) / "ftp") "://") url:$([^< \t\r\n])+
 { return protocol.concat(url); }

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
 = digits: [0-9]+
 { return digits.join(""); }
 
norlogeMonth
 = first: [0-1] last: [0-9]
 { return first + last; }

norlogeDay
 = first: [0-3] last: [0-9]
 { return first + last; }

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
 = first: [0-2] last: [0-9]
 { return first + last; }
 
norlogeMinutes
 = first: [0-5] last: [0-9]
 { return first + last; }
 
norlogeSeconds
 = first: [0-5] last: [0-9]
 { return first + last; }

totoz
  = "[:" totoz:$[^\]]+ "]"
  { return "[:".concat(totoz).concat("]");}
 
whitespaces
 = [ \t\r\n]
