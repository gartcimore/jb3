var jb3PasteFormConstructor = function (opts) {
    var self = this;
    self.selectedTab = null;
    self.pasteFile = self.tags['jb3-paste-file'];
    self.pasteImage = self.tags['jb3-paste-image'];
    self.pasteRecord = self.tags['jb3-paste-record'];
    self.pasteText = self.tags['jb3-paste-text'];
    self.pasteSketch = self.tags['jb3-paste-sketch'];
    self.selectTab = function (e) {
        self.selectedTab = e.target.dataset.tab;
    };
    self.getPasted = function () {
        return self.pasteFile.pastedFileUrl
                || self.pasteImage.pastedImageUrl
                || self.pasteRecord.pastedRecordUrl
                || self.pasteText.pastedTextUrl
                || self.pasteSketch.pastedSketchUrl;
    };
    self.clear = function () {
        self.selectedTab = null;
        self.pasteText.clear();
        self.pasteFile.clear();
        self.pasteImage.clear();
        self.pasteRecord.clear();
        self.pasteSketch.clear();
        this.update();
    };
};

var jb3PasteFormTemplate = '\
<div if="{ !selectedTab}" class="jb3-paste-form-buttons">\
        <button class="c-button u-super c-button--block" data-tab="text" onclick="{ selectTab }">Text</button>\
        <button class="c-button u-super c-button--block" data-tab="image" onclick="{ selectTab }">Image</button>\
        <button class="c-button u-super c-button--block" data-tab="sketch" onclick="{ selectTab }">Sketch</button>\
        <button class="c-button u-super c-button--block" data-tab="record" onclick="{ selectTab }">Record</button>\
        <button class="c-button u-super c-button--block" data-tab="file" onclick="{ selectTab }">File</button>\
 </div>\
<jb3-paste-text if="{ selectedTab == \'text\' }"></jb3-paste-text>\
<jb3-paste-image if="{ selectedTab == \'image\' }"></jb3-paste-image>\
<jb3-paste-sketch if="{ selectedTab == \'sketch\' }"></jb3-paste-sketch>\
<jb3-paste-record if="{ selectedTab == \'record\' }"></jb3-paste-record>\
<jb3-paste-file if="{ selectedTab == \'file\' }"></jb3-paste-file>\
';

var jb3PasteFormStyles = '\
	.jb3-paste-form-buttons .c-button {\
		margin: 5px 0;\
	}\
';

riot.tag('jb3-paste-form', jb3PasteFormTemplate, jb3PasteFormStyles, jb3PasteFormConstructor);