
var jb3PasteSketchConstructor = function () {
    var self = this;
    self.clear = function () {
        self.sketchpad.clear();
        self.pastedSketchError = null;
        self.pastedSketchUrl = null;
    };
    this.sketchpad = new Sketchpad(this.sketchCanvasContainer, {
        width: 512,
        height: 384,
        line: {
            color: '#000',
            size: 5
        }
    });
    document.addEventListener('paste', function (event) {
        var items = (event.clipboardData || event.originalEvent.clipboardData).items || [];
        for (var i = 0; i < items.length; i++) {
            if (items[i].type.indexOf("image") === 0) {
                var file = items[i].getAsFile();
                if (file) {
                    var img = new Image();
                    img.addEventListener('load', function () {
                        self.sketchpad.setBackground(img);
                    });
                    img.src = window.URL.createObjectURL(file);
                }
            }
        }
        event.preventDefault();
    });
    this.changeBackgroundFromFile = function (e) {
        var files = e.target.files || [];
        for (var i = 0; i < files.length; i++) {
            var file = files[i];
            var img = new Image();
            img.addEventListener('load', function () {
                self.sketchpad.setBackground(img);
            });
            img.src = window.URL.createObjectURL(file);
        }
        event.preventDefault();
    };
    this.pasteBackground = function () {
        document.execCommand('paste');
    };
    this.undoSketch = function () {
        this.sketchpad.undo();
    };
    this.redoSketch = function () {
        this.sketchpad.redo();
    };
    this.changeSketchColor = function (e) {
        this.sketchpad.setLineColor(e.target.value);
    };
    this.changeSketchPenSize = function (e) {
        this.sketchpad.setLineSize(e.target.value);
    };
    this.uploadSketch = function () {
        this.sketchpad.canvas.toBlob(function (blob) {
            if(self.pasteSketchProgress.scrollIntoView) {
                self.pasteSketchProgress.scrollIntoView();
            }
            var formData = new FormData();
            formData.append("pimage", blob, "sketch.png");
            var xhr = new XMLHttpRequest;
            xhr.onprogress = function (e) {
                var percentComplete = (e.loaded / e.total) * 100;
                self.pasteSketchProgress.value = percentComplete;
            };
            xhr.onreadystatechange = function (event) {
                if (xhr.readyState === 4) {
                    if (xhr.status === 200) {
                        var data = JSON.parse(xhr.response);
                        self.pastedSketchError = null;
                        self.pastedSketchUrl = data.url;
                    } else {
                        self.pastedSketchError = 'Error during image upload';
                        self.pastedSketchUrl = null;
                    }
                    self.update();
                    if (self.pastedResult && self.pastedResult.scrollIntoView) {
                        self.pastedResult.scrollIntoView();
                    }
                }
            };
            xhr.open("POST", "/api/paste/image");
            xhr.send(formData);
        }, "image/png");
    };
};

var jb3PasteSketchStyles = '\
.jb3-paste-sketch-tools {\
    margin-bottom: 10px;\
}\
.jb3-paste-sketch-container {\
    margin:auto;\
    max-width: 100%;\
}\
.jb3-paste-sketch-container canvas {\
    border: 1px solid black;\
    margin:auto;\
    max-width: 100%;\
}\
';

var jb3PasteSketchTemplate = '\
<div name="pasteSketchForm" class="c-fieldset">\
            <input class="c-field" type="file" onchange="{ changeBackgroundFromFile }"></input>\
    <div class="o-form-element">\
        <div class="jb3-paste-sketch-tools">\
            <input type="color" value="#000" onchange="{ changeSketchColor }"></input>\
            <input min="0" max="32" value="5" type="range" onchange="{ changeSketchPenSize }"></input>\
            <button class="c-button" onclick="{ undoSketch }">&cularr;</button>\
            <button class="c-button" onclick="{ redoSketch }">&curarr;</button>\
            <button class="c-button" onclick="{ clear }">Clear</button>\
            <button class="c-button c-button--info" onclick="{ uploadSketch }" >Upload</button>\
        </div>\
        <div name="sketchCanvasContainer" class="jb3-paste-sketch-container" width="512" height="384"></div>\
    </div>\
    <progress name="pasteSketchProgress" value="0" max="100"></progress>\
</div>\
<div name="pastedResult">\
    <div if="{ pastedSketchUrl }" class="c-card">\
      <div class="c-card__item c-card__item--divider c-card__item--success">Pasted!</div>\
      <div class="c-card__item">\
        <p class="c-paragraph"><a class="c-link  jb3-pasted-url" href="{ pastedSketchUrl }" target="_blank">{ pastedSketchUrl }</a></p>\
      </div>\
    </div>\
    <div if="{ pastedSketchError }" class="c-card">\
      <div class="c-card__item c-card__item--divider c-card__item--error">Error :-(</div>\
      <div class="c-card__item">\
        <p class="c-paragraph">{ pastedSketchError }</p>\
      </div>\
    </div>\
</div>\
';
riot.tag('jb3-paste-sketch', jb3PasteSketchTemplate, jb3PasteSketchStyles, jb3PasteSketchConstructor);