var jb3PasteFileConstructor = function () {
    var self = this;
    self.clear = function () {
        self.pastedFileError = null;
        self.pastedFileUrl = null;
    };
    self.submit = function (event) {
        event.preventDefault();
        var xhr = new XMLHttpRequest();
        xhr.onprogress = function (e) {
            var percentComplete = (e.loaded / e.total) * 100;
            self.fileProgress.value = percentComplete;
        };
        xhr.onreadystatechange = function (event) {
            if (xhr.readyState === 4) {
                if (xhr.status === 200) {
                    var data = JSON.parse(xhr.response);
                    self.pastedFileError = null;
                    self.pastedFileUrl = data.url;
                } else {
                    self.pastedFileError = 'Error during file upload';
                    self.pastedFileUrl = null;
                }
                self.update();
                if (self.pastedResult && self.pastedResult.scrollIntoView) {
                    self.pastedResult.scrollIntoView();
                }
            }
        };
        xhr.open("POST", "/api/paste/file");
        xhr.send(new FormData(event.target));
        return false;
    };
};
var jb3PasteFileStyles = '\
';
var jb3PasteFileTemplate = '\
<form name="pasteFileForm" class="c-fieldset" onsubmit="{ submit }">\
        <div class="o-form-element">\
                <input name="pfile" class="c-field c-field--label" type="file"></input>\
        </div>\
        <input type="submit" class="c-button c-button--info" >\
        <progress name="fileProgress" value="0" max="100"></progress>\
</form>\
<div name="pastedResult">\
    <div if="{ pastedFileUrl }" class="c-card">\
      <div class="c-card__item c-card__item--divider c-card__item--success">Pasted!</div>\
      <div class="c-card__item">\
        <p class="c-paragraph"><a class="c-link  jb3-pasted-url" href="{ pastedFileUrl }" target="_blank">{ pastedFileUrl }</a></p>\
      </div>\
    </div>\
    <div if="{ pastedFileError }" class="c-card">\
      <div class="c-card__item c-card__item--divider c-card__item--error">Error :-(</div>\
      <div class="c-card__item">\
        <p class="c-paragraph">{ pastedFileError }</p>\
      </div>\
    </div>\
</div>\
';

riot.tag('jb3-paste-file', jb3PasteFileTemplate, jb3PasteFileStyles, jb3PasteFileConstructor);