var jb3PasteTextTemplate = '\
<form name="pasteTextForm" class="c-fieldset" onsubmit="{ submit }">\
        <div class="o-form-element">\
                <textarea name="ptext" class="c-field" type="text"></textarea>\
        </div>\
        <input type="submit" class="c-button c-button--info" >\
</form>\
<div if="{ pastedTextUrl }" class="c-card  c-card--success">\
  <div class="c-card__item c-card__item--divider">Pasted!</div>\
  <div class="c-card__item">\
    <p class="c-paragraph"><a class="c-link jb3-pasted-url" href="{ pastedTextUrl }" target="_blank">{ pastedTextUrl }</a></p>\
  </div>\
</div>\
<div if="{ pastedTextError }" class="c-card  c-card--error">\
  <div class="c-card__item c-card__item--divider">Error :-(</div>\
  <div class="c-card__item">\
    <p class="c-paragraph">{ pastedTextError }</p>\
  </div>\
</div>\
';
var jb3PasteTextStyles = '\
';
var jb3PasteTextConstructor = function () {
    var self = this;
    self.clear = function () {
        self.pastedTextError = null;
        self.pastedTextUrl = null;
    };
    self.submit = function (event) {
        event.preventDefault();
        var xhr = new XMLHttpRequest();
        xhr.onreadystatechange = function (event) {
            if (xhr.readyState === 4) {
                if (xhr.status === 200) {
                    var data = JSON.parse(xhr.response);
                    self.pastedTextError = null;
                    self.pastedTextUrl = data.url;
                } else {
                    self.pastedTextError = 'Error during text upload';
                    self.pastedTextUrl = null;
                }
                self.update();
            }
        };
        xhr.open("POST", "/api/paste/text");
        xhr.send(new FormData(event.target));
        return false;
    };
};

riot.tag('jb3-paste-text', jb3PasteTextTemplate, jb3PasteTextStyles, jb3PasteTextConstructor);