riot
        .tag(
                'jb3-paste-form',
                '\
		<div class="c-tabs">\
		    <div class="c-tabs__headings">\
			    <div class="c-tab-heading { \'c-tab-heading--active\': selectedTab == \'text\' }" data-tab="text" onclick="{ selectTab }">Text</div>\
			    <div class="c-tab-heading { \'c-tab-heading--active\': selectedTab == \'image\' }" data-tab="image" onclick="{ selectTab }">Image</div>\
			    <div class="c-tab-heading { \'c-tab-heading--active\': selectedTab == \'sketch\' }" data-tab="sketch" onclick="{ selectTab }">Sketch</div>\
                            <div class="c-tab-heading { \'c-tab-heading--active\': selectedTab == \'record\' }" data-tab="record" onclick="{ selectTab }">Record</div>\
			    <div class="c-tab-heading { \'c-tab-heading--active\': selectedTab == \'file\' }" data-tab="file" onclick="{ selectTab }">File</div>\
		     </div>\
			<div class="c-tabs__tab { \'c-tabs__tab--active\': selectedTab == \'text\' }">\
                            <jb3-paste-text></jb3-paste-text>\
			</div>\
			<div class="c-tabs__tab { \'c-tabs__tab--active\': selectedTab == \'image\' }">\
				<form name="pasteImageForm" class="c-fieldset" action="/api/paste/image" method="post">\
					<div class="o-form-element">\
						<input name="pimage" type="file"></input>\
					</div>\
					<input type="submit" class="c-button c-button--info"" >\
				</form>\
				<div if="{ pastedImageUrl }" class="c-card  c-card--success">\
				  <div class="c-card__item c-card__item--divider">Pasted!</div>\
				  <div class="c-card__item">\
				    <p class="c-paragraph"><a class="c-link  jb3-pasted-url" href="{ pastedImageUrl }">{ pastedImageUrl }</a></p>\
				  </div>\
				</div>\
				<div if="{ pastedImageError }" class="c-card  c-card--error">\
				  <div class="c-card__item c-card__item--divider">Error :-(</div>\
				  <div class="c-card__item">\
				    <p class="c-paragraph">{ pastedImageError }</p>\
				  </div>\
				</div>\
			</div>\
                        <div class="c-tabs__tab { \'c-tabs__tab--active\': selectedTab == \'sketch\' }">\
				<div name="pasteSketchForm" class="c-fieldset">\
                                    <div class="o-form-element">\
                                        <div class="jb3-paste-sketch-tools">\
                                            <input name="sketchColor" type="color" value="#000" onchange="{ changeSketchColor }"></input>\
                                            <input name="sketchPenSize" min="0" max="32" value="5" type="range" onchange="{ changeSketchPenSize }"></input>\
                                            <button class="c-button" onclick="{ undoSketch }">&cularr;</button>\
                                            <button class="c-button" onclick="{ redoSketch }">&curarr;</button>\
                                        </div>\
                                        <div name="sketchCanvasContainer" class="jb3-paste-sketch-container" width="512" height="384"></div>\
                                    </div>\
                                    <button class="c-button c-button--info" onclick="{ uploadSketch }" >Upload</button>\
                                    <progress value="0" max="100"></progress>\
				</div>\
				<div if="{ pastedSketchUrl }" class="c-card  c-card--success">\
				  <div class="c-card__item c-card__item--divider">Pasted!</div>\
				  <div class="c-card__item">\
				    <p class="c-paragraph"><a class="c-link  jb3-pasted-url" href="{ pastedSketchUrl }">{ pastedSketchUrl }</a></p>\
				  </div>\
				</div>\
				<div if="{ pastedSketchError }" class="c-card  c-card--error">\
				  <div class="c-card__item c-card__item--divider">Error :-(</div>\
				  <div class="c-card__item">\
				    <p class="c-paragraph">{ pastedSketchError }</p>\
				  </div>\
				</div>\
			</div>\
                        <div class="c-tabs__tab { \'c-tabs__tab--active\': selectedTab == \'record\' }">\
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
                            <div if="{ pastedRecordUrl }" class="c-card  c-card--success">\
				  <div class="c-card__item c-card__item--divider">Pasted!</div>\
				  <div class="c-card__item">\
				    <p class="c-paragraph"><a class="c-link  jb3-pasted-url" href="{ pastedRecordUrl }">{ pastedRecordUrl }</a></p>\
				  </div>\
                            </div>\
                            <div if="{ pastedRecordError }" class="c-card  c-card--error">\
                              <div class="c-card__item c-card__item--divider">Error :-(</div>\
                              <div class="c-card__item">\
                                <p class="c-paragraph">{ pastedRecordError }</p>\
                              </div>\
                            </div>\
                        </div>\
			<div class="c-tabs__tab { \'c-tabs__tab--active\': selectedTab == \'file\' }">\
				<form name="pasteFileForm" class="c-fieldset" action="/api/paste/file" method="post">\
					<div class="o-form-element">\
						<input name="pfile" class="c-field c-field--label" type="file"></input>\
					</div>\
					<input type="submit" class="c-button c-button--info" >\
					<progress value="0" max="100"></progress>\
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
			</div>\
		</div>\
',
                '.jb3-paste-sketch-tools {\
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
',
                function(opts) {
                    var self = this;
                    this.selectedTab = 'text';
                        this.toggleMic = function() {
                            if(self.mediaRecorder) {
                                var mediaRecorder = self.mediaRecorder;
                                self.mediaRecorder = null;
                                mediaRecorder.stream.getTracks().forEach(function(track) {
                                    track.stop();
                                });
                            } else {
                                var constraints = { audio: true, video: false }; 
                                navigator.mediaDevices.getUserMedia(constraints).then(function(mediaStream) {
                                    self.mediaRecorder = new MediaRecorder(mediaStream);
                                    self.recordedChunks = [];
                                    self.mediaRecorder.ondataavailable = function(e) {
                                        self.recordedChunks.push(e.data);
                                    }
                                    self.mediaRecorder.onstop = function() {
                                        self.recordedBlob = new Blob(self.recordedChunks, { 'type' : 'audio/ogg; codecs=opus' });
                                        self.recordedChunks = [];
                                        self.recordedAudio.src = window.URL.createObjectURL(self.recordedBlob);
                                        self.recordedAudio.play();
                                    };
                                    self.update();
                                },function(){
                                    console.log('No audio recording device available.');
                                })
                                .catch(function(err) { 
                                    console.log(err.name + ": " + err.message);
                                });
                            }
                        };
                        this.toggleRecord = function() {
                            if(self.mediaRecorder && self.mediaRecorder.state == "recording") {
                                self.mediaRecorder.stop();
                            } else {
                                self.recordedChunks = [];
                                self.mediaRecorder.start();
                            }
                        };
                        this.stopRecord = function() {
                            if(self.mediaRecorder) {
                                this.mediaRecorder.onstop = function() {

                                }
                                self.mediaRecorder.stop();
                            }
                        };
                        this.uploadRecord = function() {
                            if(self.recordedBlob) {
                                var formData = new FormData();
                                formData.append("pfile", self.recordedBlob, "record.ogg");
                                var xhr = new XMLHttpRequest;
                                xhr.onprogress = function(e) {
                                    var percentComplete = (e.loaded / e.total) * 100;
                                    self.recordProgress.value = percentComplete;
                                };
                                xhr.onreadystatechange = function (event) {
                                if (xhr.readyState == 4) {
                                if (xhr.status == 200) {
                                var data = JSON.parse(xhr.response);
                                        self.pastedRecordError = null;
                                        self.pastedRecordUrl = data.url;
                                } else {
                                self.pastedRecordError = 'Error during record upload';
                                        self.pastedRecordUrl = null;
                                }
                                self.update();
                                }
                                };
                                xhr.open("POST", "/api/paste/file");
                                xhr.send(formData);
                            }
                        };
                        var sketchForm = $(self.pasteSketchForm);
                        this.sketchpad = new Sketchpad(this.sketchCanvasContainer, {
                        width: 512,
                                height: 384,
                                line: {
                                color: '#000',
                                        size: 5
                                }
                        });
                        this.undoSketch = function() {
                        this.sketchpad.undo();
                        };
                        this.redoSketch = function() {
                        this.sketchpad.redo();
                        };
                        this.changeSketchColor = function(e) {
                        this.sketchpad.setLineColor(e.target.value);
                        };
                        this.changeSketchPenSize = function(e) {
                        this.sketchpad.setLineSize(e.target.value);
                        };
                        this.uploadSketch = function() {
                        this.sketchpad.canvas.toBlob(function(blob) {
                        var formData = new FormData();
                                formData.append("pimage", blob, "sketch.png");
                                var xhr = new XMLHttpRequest;
                                xhr.onprogress = function(e) {
                                var percentComplete = (e.loaded / e.total) * 100;
                                        sketchForm.find('progress').val(percentComplete);
                                };
                                xhr.onreadystatechange = function (event) {
                                if (xhr.readyState == 4) {
                                if (xhr.status == 200) {
                                var data = JSON.parse(xhr.response);
                                        self.pastedSketchError = null;
                                        self.pastedSketchUrl = data.url;
                                } else {
                                self.pastedSketchError = 'Error during image upload';
                                        self.pastedSketchUrl = null;
                                }
                                self.update();
                                }
                                };
                                xhr.open("POST", "/api/paste/image");
                                xhr.send(formData);
                        }, "image/png");
                        };
                        self.selectTab = function(e) {
                        self.selectedTab = e.target.dataset.tab
                        };
                        self.clear = function() {
                            self.pastedTextError = null;
                            self.pastedTextUrl = null;
                            self.pastedImageError = null;
                            self.pastedImageUrl = null;
                            self.pastedSketchUrl = null;
                            self.pastedSketchError = null;
                            self.pastedRecordUrl = null;
                            self.pastedRecordError = null;
                            self.pastedFileError = null;
                            self.pastedFileUrl = null;
                            this.update();
                        }
                        $(self.pasteFileForm)
                        .ajaxForm(
                        {
                        success : function(data) {
                        self.pastedFileError = null;
                                self.pastedFileUrl = data.url;
                                self.update();
                        },
                                uploadProgress : function(event,
                                        position, total,
                                        percentComplete) {
                                $(self.pasteFileForm).find('progress')
                                        .val(percentComplete);
                                },
                                error : function() {
                                self.pastedFileError = 'Error during file upload';
                                        self.pastedFileUrl = null;
                                        self.update();
                                }
                        });
                        $(self.pimage)
                        .picEdit(
                        {
                        maxWidth : 'auto',
                                formSubmitted : function(xhr) {
                                if (xhr.status == 200) {
                                var data = JSON.parse(xhr.response);
                                        self.pastedImageError = null;
                                        self.pastedImageUrl = data.url;
                                } else {
                                self.pastedImageError = 'Error during image upload';
                                        self.pastedImageUrl = null;
                                }
                                self.update();
                                }
                        });
                });