var jb3PasteFileTemplate = '\
<form name="pasteFileForm" class="c-fieldset" onsubmit="{ submit }">\
        <div class="o-form-element">\
                <input name="pfile" class="c-field c-field--label" type="file"></input>\
        </div>\
        <input type="submit" class="c-button c-button--info" >\
        <progress name="fileProgress" value="0" max="100"></progress>\
</form>\
<div if="{ pastedFileUrl }" class="c-card  c-card--success">\
  <div class="c-card__item c-card__item--divider">Pasted!</div>\
  <div class="c-card__item">\
    <p class="c-paragraph"><a class="c-link  jb3-pasted-url" href="{ pastedFileUrl }">{ pastedFileUrl }</a></p>\
  </div>\
</div>\
<div if="{ pastedFileError }" class="c-card  c-card--error">\
  <div class="c-card__item c-card__item--divider">Error :-(</div>\
  <div class="c-card__item">\
    <p class="c-paragraph">{ pastedFileError }</p>\
  </div>\
</div>\
';
var jb3PasteFileStyles = '\
';
var jb3PasteFileConstructor = function () {
    var self = this;
    self.clear = function () {
        self.pastedFileError = null;
        self.pastedFileUrl = null;
    };
    self.submit = function (event) {
        event.preventDefault();
        var xhr = new XMLHttpRequest();
        xhr.onprogress = function(e) {
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
            }
        };
        xhr.open("POST", "/api/paste/file");
        xhr.send(new FormData(event.target));
        return false;
    };
};

riot.tag('jb3-paste-file', jb3PasteFileTemplate, jb3PasteFileStyles, jb3PasteFileConstructor);