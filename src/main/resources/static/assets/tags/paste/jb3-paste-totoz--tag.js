var jb3PasteTotozTemplate = '\
<form name="pasteTotozForm" class="c-fieldset" onsubmit="{ submit }">\
        <div class="o-form-element">\
                <input name="terms" class="c-field c-field--label" type="text"></input>\
        </div>\
        <input type="submit" class="c-button c-button--info" value="Search" >\
</form>\
<div if="{ totozList }"class="o-grid  o-grid--wrap" >\
    <virtual each="{ totozList }" >\
        <figure data-name="{ name }" class="{ o-grid__cell: true, jb3-paste-totoz-selected: selected}" onclick="{ selectTotoz }">\
            <img src="/totoz/img/{ name }" />\
             <figcaption>{ name }</figcaption>\
        </figure>\
    </virtual>\
</div>\
<div if="{ totozError }" class="c-card  c-card--error">\
  <div class="c-card__item c-card__item--divider">Error :-(</div>\
  <div class="c-card__item">\
    <p class="c-paragraph">{ totozError }</p>\
  </div>\
</div>\
';
var jb3PasteTotozStyles = '\
    .jb3-paste-totoz-selected {\
        background-color: #e5eaec;\
        border-radius: 4px;\
    }\
';
var jb3PasteTotozConstructor = function () {
    var self = this;
    self.clear = function () {
        self.totozError = null;
        self.totozList = null;
        self.pastedTotoz = null;
    };
    self.submit = function (event) {
        event.preventDefault();
        var xhr = new XMLHttpRequest();
        xhr.onreadystatechange = function (event) {
            if (xhr.readyState === 4) {
                if (xhr.status === 200) {
                    self.totozList = JSON.parse(xhr.response);
                } else {
                    self.totozError = 'Error during totoz search';
                    self.totozList = null;
                }
                self.update();
            }
        };
        xhr.open("GET", "/api/paste/totoz/search?terms=" + encodeURIComponent(self.terms.value));
        xhr.send();
        return false;
    };
    self.selectTotoz = function(event) {
        self.pastedTotoz = "";
        self.totozList.forEach(function(totoz) {
            if(totoz.name === event.currentTarget.dataset.name) {
                totoz.selected = !totoz.selected;
            }
            if(totoz.selected) {
                self.pastedTotoz = self.pastedTotoz.concat('[:' + totoz.name + '] ');
            }
        });
        self.update();
    };
};

riot.tag('jb3-paste-totoz', jb3PasteTotozTemplate, jb3PasteTotozStyles, jb3PasteTotozConstructor);