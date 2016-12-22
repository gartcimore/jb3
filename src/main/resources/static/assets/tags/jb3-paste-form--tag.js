riot
        .tag(
                'jb3-paste-form',
                '\
		<div class="c-tabs">\
		    <div class="c-tabs__headings">\
			    <div class="c-tab-heading { \'c-tab-heading--active\': selectedTab == \'text\' }" data-tab="text" onclick="{ selectTab }">Text</div>\
			    <div class="c-tab-heading { \'c-tab-heading--active\': selectedTab == \'image\' }" data-tab="image" onclick="{ selectTab }">Image</div>\
			    <div class="c-tab-heading { \'c-tab-heading--active\': selectedTab == \'sketch\' }" data-tab="sketch" onclick="{ selectTab }">Sketch</div>\
			    <div class="c-tab-heading { \'c-tab-heading--active\': selectedTab == \'file\' }" data-tab="file" onclick="{ selectTab }">File</div>\
		     </div>\
			<div class="c-tabs__tab { \'c-tabs__tab--active\': selectedTab == \'text\' }">\
				<form name="pasteTextForm" class="c-fieldset" action="/api/paste/text" method="post">\
					<div class="c-form-element">\
						<textarea name="ptext" class="c-field" type="text"></textarea>\
					</div>\
					<input type="submit" class="c-button c-button--primary"" >\
				</form>\
				<div if="{ pastedTextUrl }" class="c-card  c-card--success">\
				  <div class="c-card__content c-card__content--divider">Pasted!</div>\
				  <div class="c-card__content">\
				    <p class="c-paragraph"><a class="c-link jb3-pasted-url" href="{ pastedTextUrl }">{ pastedTextUrl }</a></p>\
				  </div>\
				</div>\
				<div if="{ pastedTextError }" class="c-card  c-card--error">\
				  <div class="c-card__content c-card__content--divider">Error :-(</div>\
				  <div class="c-card__content">\
				    <p class="c-paragraph">{ pastedTextError }</p>\
				  </div>\
				</div>\
			</div>\
			<div class="c-tabs__tab { \'c-tabs__tab--active\': selectedTab == \'image\' }">\
				<form name="pasteImageForm" class="c-fieldset" action="/api/paste/image" method="post">\
					<div class="c-form-element">\
						<input name="pimage" type="file"></input>\
					</div>\
					<input type="submit" class="c-button c-button--primary"" >\
				</form>\
				<div if="{ pastedImageUrl }" class="c-card  c-card--success">\
				  <div class="c-card__content c-card__content--divider">Pasted!</div>\
				  <div class="c-card__content">\
				    <p class="c-paragraph"><a class="c-link  jb3-pasted-url" href="{ pastedImageUrl }">{ pastedImageUrl }</a></p>\
				  </div>\
				</div>\
				<div if="{ pastedImageError }" class="c-card  c-card--error">\
				  <div class="c-card__content c-card__content--divider">Error :-(</div>\
				  <div class="c-card__content">\
				    <p class="c-paragraph">{ pastedImageError }</p>\
				  </div>\
				</div>\
			</div>\
                        <div class="c-tabs__tab { \'c-tabs__tab--active\': selectedTab == \'sketch\' }">\
				<div name="pasteSketchForm" class="c-fieldset">\
                                    <div class="c-form-element">\
                                        <div class="jb3-paste-sketch-tools">\
                                            <a href="#jb3-paste-sketch-canvas" data-color="#000000" style="background: #000000;"></a>\
                                            <a href="#jb3-paste-sketch-canvas" data-color="#001290" style="background: #001290;"></a>\
                                            <a href="#jb3-paste-sketch-canvas" data-color="#0027FB" style="background: #0027FB;"></a>\
                                            <a href="#jb3-paste-sketch-canvas" data-color="#9B1708" style="background: #9B1708;"></a>\
                                            <a href="#jb3-paste-sketch-canvas" data-color="#9A2091" style="background: #9A2091;"></a>\
                                            <a href="#jb3-paste-sketch-canvas" data-color="#952FFC" style="background: #952FFC;"></a>\
                                            <a href="#jb3-paste-sketch-canvas" data-color="#FF3016" style="background: #FF3016;"></a>\
                                            <a href="#jb3-paste-sketch-canvas" data-color="#FF3592" style="background: #FF3592;"></a>\
                                            <a href="#jb3-paste-sketch-canvas" data-color="#FF3FFC" style="background: #FF3FFC;"></a>\
                                            <a href="#jb3-paste-sketch-canvas" data-color="#008F15" style="background: #008F15;"></a>\
                                            <a href="#jb3-paste-sketch-canvas" data-color="#009092" style="background: #009092;"></a>\
                                            <a href="#jb3-paste-sketch-canvas" data-color="#0094FC" style="background: #0094FC;"></a>\
                                            <a href="#jb3-paste-sketch-canvas" data-color="#949119" style="background: #949119;"></a>\
                                            <a href="#jb3-paste-sketch-canvas" data-color="#929292" style="background: #929292;"></a>\
                                            <a href="#jb3-paste-sketch-canvas" data-color="#8E96FC" style="background: #8E96FC;"></a>\
                                            <a href="#jb3-paste-sketch-canvas" data-color="#FF9621" style="background: #FF9621;"></a>\
                                            <a href="#jb3-paste-sketch-canvas" data-color="#FF9794" style="background: #FF9794;"></a>\
                                            <a href="#jb3-paste-sketch-canvas" data-color="#FF9AFD" style="background: #FF9AFD;"></a>\
                                            <a href="#jb3-paste-sketch-canvas" data-color="#00F92C" style="background: #00F92C;"></a>\
                                            <a href="#jb3-paste-sketch-canvas" data-color="#00FA96" style="background: #00FA96;"></a>\
                                            <a href="#jb3-paste-sketch-canvas" data-color="#00FCFE" style="background: #00FCFE;"></a>\
                                            <a href="#jb3-paste-sketch-canvas" data-color="#80FA2E" style="background: #80FA2E;"></a>\
                                            <a href="#jb3-paste-sketch-canvas" data-color="#7EFB96" style="background: #7EFB96;"></a>\
                                            <a href="#jb3-paste-sketch-canvas" data-color="#78FDFE" style="background: #78FDFE;"></a>\
                                            <a href="#jb3-paste-sketch-canvas" data-color="#FFFD33" style="background: #FFFD33;"></a>\
                                            <a href="#jb3-paste-sketch-canvas" data-color="#FFFD98" style="background: #FFFD98;"></a>\
                                            <a href="#jb3-paste-sketch-canvas" data-color="#FFFFFF" style="background: #FFFFFF;"></a>\
                                            <a href="#jb3-paste-sketch-canvas" data-size="1" style="background: #ccc">1</a>\
                                            <a href="#jb3-paste-sketch-canvas" data-size="4" style="background: #ccc">4</a>\
                                            <a href="#jb3-paste-sketch-canvas" data-size="8" style="background: #ccc">8</a>\
                                            <a href="#jb3-paste-sketch-canvas" data-size="16" style="background: #ccc">16</a>\
                                            <a href="#jb3-paste-sketch-canvas" data-tool="marker">Marker</a>\
                                            <a href="#jb3-paste-sketch-canvas" data-tool="eraser">Eraser</a>\
                                        </div>\
                                        <canvas id="jb3-paste-sketch-canvas" width="512" height="384"></canvas>\
                                    </div>\
                                    <button class="jb3-paste-sketch-upload-button c-button c-button--primary"" >Upload</button>\
                                    <progress value="0" max="100"></progress>\
				</div>\
				<div if="{ pastedSketchUrl }" class="c-card  c-card--success">\
				  <div class="c-card__content c-card__content--divider">Pasted!</div>\
				  <div class="c-card__content">\
				    <p class="c-paragraph"><a class="c-link  jb3-pasted-url" href="{ pastedSketchUrl }">{ pastedSketchUrl }</a></p>\
				  </div>\
				</div>\
				<div if="{ pastedSketchError }" class="c-card  c-card--error">\
				  <div class="c-card__content c-card__content--divider">Error :-(</div>\
				  <div class="c-card__content">\
				    <p class="c-paragraph">{ pastedSketchError }</p>\
				  </div>\
				</div>\
			</div>\
			<div class="c-tabs__tab { \'c-tabs__tab--active\': selectedTab == \'file\' }">\\n\
				<form name="pasteFileForm" class="c-fieldset" action="/api/paste/file" method="post">\
					<div class="c-form-element">\
						<input name="pfile" class="c-label__field" type="file"></input>\
					</div>\
					<input type="submit" class="c-button c-button--primary" >\
					<progress value="0" max="100"></progress>\
				</form>\
				<div if="{ pastedFileUrl }" class="c-card  c-card--success">\
				  <div class="c-card__content c-card__content--divider">Pasted!</div>\
				  <div class="c-card__content">\
				    <p class="c-paragraph"><a class="c-link  jb3-pasted-url" href="{ pastedFileUrl }">{ pastedFileUrl }</a></p>\
				  </div>\
				</div>\
				<div if="{ pastedFileError }" class="c-card  c-card--error">\
				  <div class="c-card__content c-card__content--divider">Error :-(</div>\
				  <div class="c-card__content">\
				    <p class="c-paragraph">{ pastedFileError }</p>\
				  </div>\
				</div>\
			</div>\
		</div>\
',
'.jb3-paste-sketch-tools {\
    margin-bottom: 10px;\
}\
.jb3-paste-sketch-tools a {\
    border: 1px solid black;\
    min-width:10px;\
    height: 30px;\
    line-height: 30px;\
    padding: 0 10px;\
    vertical-align: middle;\
    text-align: center;\
    text-decoration: none;\
    display: inline-block;\
    color: black;\
    font-weight: bold;\
}\
#jb3-paste-sketch-canvas {\
    border: 1px solid black;\
}\
',
                function(opts) {
                var self = this;
                        this.selectedTab = 'text';
                        var sketchForm = $(self.pasteSketchForm);
                        sketchForm.find('#jb3-paste-sketch-canvas').sketch();
                        sketchForm.find('.jb3-paste-sketch-upload-button').click(function() {
                        var canvas = sketchForm.find('canvas')[0];
                        canvas.toBlob(function(blob) {
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
                });
                        self.selectTab = function(e) {
                        self.selectedTab = e.target.dataset.tab
                        };
                        self.clear = function() {
                        self.pastedTextError = null;
                                self.pastedTextUrl = null;
                                self.pastedImageError = null;
                                self.pastedImageUrl = null;
                                self.pastedFileError = null;
                                self.pastedFileUrl = null;
                                this.update();
                        }
                $(self.pasteTextForm).ajaxForm({
                success : function(data) {
                self.pastedTextError = null;
                        self.pastedTextUrl = data.url;
                        self.update();
                },
                        error : function() {
                        self.pastedTextError = 'Error during text upload';
                                self.pastedTextUrl = null;
                                self.update();
                        }
                });
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