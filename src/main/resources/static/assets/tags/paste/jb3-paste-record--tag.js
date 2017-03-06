var jb3PasteRecordConstructor = function () {
    var self = this;
    self.clear = function () {
        self.pastedRecordError = null;
        self.pasted = null;
    };
    this.toggleMic = function () {
        if (self.mediaRecorder) {
            var mediaRecorder = self.mediaRecorder;
            self.mediaRecorder = null;
            mediaRecorder.stream.getTracks().forEach(function (track) {
                track.stop();
            });
        } else {
            var constraints = {audio: true, video: false};
            navigator.mediaDevices.getUserMedia(constraints).then(function (mediaStream) {
                self.mediaRecorder = new MediaRecorder(mediaStream);
                self.recordedChunks = [];
                self.mediaRecorder.ondataavailable = function (e) {
                    self.recordedChunks.push(e.data);
                };
                self.mediaRecorder.onstop = function () {
                    self.recordedBlob = new Blob(self.recordedChunks, {'type': 'audio/ogg; codecs=opus'});
                    self.recordedChunks = [];
                    self.recordedAudio.src = window.URL.createObjectURL(self.recordedBlob);
                    self.recordedAudio.play();
                };
                self.update();
            }, function () {
                console.log('No audio recording device available.');
            }).catch(function (err) {
                console.log(err.name + ": " + err.message);
            });
        }
    };
    this.toggleRecord = function () {
        if (self.mediaRecorder && self.mediaRecorder.state === "recording") {
            self.mediaRecorder.stop();
        } else {
            self.recordedChunks = [];
            self.mediaRecorder.start();
        }
    };
    this.stopRecord = function () {
        if (self.mediaRecorder) {
            self.mediaRecorder.stop();
        }
    };
    this.uploadRecord = function () {
        if (self.recordedBlob) {
            var formData = new FormData();
            formData.append("pfile", self.recordedBlob, "record.ogg");
            var xhr = new XMLHttpRequest();
            xhr.onprogress = function (e) {
                var percentComplete = (e.loaded / e.total) * 100;
                self.recordProgress.value = percentComplete;
            };
            xhr.onreadystatechange = function (event) {
                if (xhr.readyState === 4) {
                    if (xhr.status === 200) {
                        var data = JSON.parse(xhr.response);
                        self.pastedRecordError = null;
                        self.pasted = data.url;
                    } else {
                        self.pastedRecordError = 'Error during record upload';
                        self.pasted = null;
                    }
                    self.trigger('paste-content-changed');
                    self.update();
                    if (self.pastedResult && self.pastedResult.scrollIntoView) {
                        self.pastedResult.scrollIntoView();
                    }
                }
            };
            xhr.open("POST", "/api/paste/file");
            xhr.send(formData);
        }
    };
};

var jb3PasteRecordStyles = '\
';

var jb3PasteRecordTemplate = '\
<div class="o-form-element">\
    <button class="c-button" onclick="{ toggleMic }">Switch { mediaRecorder ? "off" : "on" } mic</button>\
    <button if="{ mediaRecorder }" class="c-button" onclick="{ toggleRecord }">\
        { mediaRecorder && mediaRecorder.state == "recording" ? "Stop" : "Record" }\
        <img style="width:1em; height:1em;" if="{ mediaRecorder && mediaRecorder.state == \'recording\'}" src="/assets/icons/audio.svg" alt="recording">\
    </button>\
</div>\
<div class="o-form-element">\
    <audio name="recordedAudio" controls></audio>\
</div>\
<div class="o-form-element">\
    <button class="c-button c-button--info" onclick="{ uploadRecord }" >Upload</button>\
    <progress name="recordProgress" value="0" max="100"></progress>\
</div>\
<div name="pastedResult">\
    <div if="{ pasted }" class="c-card">\
          <div class="c-card__item c-card__item--divider c-card__item--success">Pasted!</div>\
          <div class="c-card__item">\
            <p class="c-paragraph"><a class="c-link  jb3-pasted-url" href="{ pasted }">{ pasted }</a></p>\
          </div>\
    </div>\
    <div if="{ pastedRecordError }" class="c-card">\
      <div class="c-card__item c-card__item--divider c-card__item--error">Error :-(</div>\
      <div class="c-card__item">\
        <p class="c-paragraph">{ pastedRecordError }</p>\
      </div>\
    </div>\
</div>\
';

riot.tag('jb3-paste-record', jb3PasteRecordTemplate, jb3PasteRecordStyles, jb3PasteRecordConstructor);