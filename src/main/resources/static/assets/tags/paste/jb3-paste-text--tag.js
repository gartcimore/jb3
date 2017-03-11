var jb3PasteTextConstructor = function () {
    var self = this;
    self.clear = function () {
        self.pastedTextError = null;
        self.pasted = null;
    };
    self.submit = function (event) {
        event.preventDefault();
        var xhr = new XMLHttpRequest();
        xhr.onreadystatechange = function (event) {
            if (xhr.readyState === 4) {
                if (xhr.status === 200) {
                    var data = JSON.parse(xhr.response);
                    self.pastedTextError = null;
                    self.pasted = data.url;
                } else {
                    self.pastedTextError = 'Error during text upload';
                    self.pasted = null;
                }
                self.trigger('paste-content-changed');
                self.update();
                if (self.pastedResult && self.pastedResult.scrollIntoView) {
                    self.pastedResult.scrollIntoView();
                }
            }
        };
        xhr.open("POST", "/api/paste/text");
        xhr.setRequestHeader('Content-Type', 'text/plain; charset=UTF-8');
        xhr.send(self.ptext.value);
        return false;
    };
    self.on('updated', function () {
        self.ptext.focus();
    });
};

var jb3PasteTextStyles = '\
';

var jb3PasteTextTemplate = '\
<form name="pasteTextForm" class="c-fieldset" onsubmit="{ submit }" accept-charset="UTF-8">\
        <div class="o-form-element">\
                <textarea name="ptext" class="c-field" type="text"></textarea>\
        </div>\
        <input type="submit" class="c-button c-button--info" >\
</form>\
<div name="pastedResult">\
    <div if="{ pasted }" class="c-card">\
      <div class="c-card__item c-card__item--divider c-card__item--success">Pasted!</div>\
      <div class="c-card__item">\
        <p class="c-paragraph"><a class="c-link jb3-pasted-url" href="{ pasted }" target="_blank">{ pasted }</a></p>\
      </div>\
    </div>\
    <div if="{ pastedTextError }" class="c-card">\
      <div class="c-card__item c-card__item--divider c-card__item--error">Error :-(</div>\
      <div class="c-card__item">\
        <p class="c-paragraph">{ pastedTextError }</p>\
      </div>\
    </div>\
</div>\
';

riot.tag('jb3-paste-text', jb3PasteTextTemplate, jb3PasteTextStyles, jb3PasteTextConstructor);