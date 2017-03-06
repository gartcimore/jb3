var jb3PasteFormConstructor = function(opts) {
	var self = this;
	self.selectedTab = null;
	self.pasterList = [ self.tags['jb3-paste-emoji'],
			self.tags['jb3-paste-file'], self.tags['jb3-paste-record'],
			self.tags['jb3-paste-text'], self.tags['jb3-paste-totoz'],
			self.tags['jb3-paste-sketch'] ];
	self.pasterList.forEach(function(paster) {
		paster.on('paste-content-changed', function() {
			self.trigger('paste-content-changed');
		});
	});

	self.selectTab = function(e) {
		self.selectedTab = e.target.dataset.tab;
	};
	self.getPasted = function() {
		var paster = self.pasterList.find(function(paster) {
			return !!paster.pasted;
		});
		return paster && paster.pasted;
	};
	self.clear = function() {
		self.selectedTab = null;
		self.pasterList.forEach(function(paster) {
			paster.clear();
		});
		this.update();
	};
};

var jb3PasteFormTemplate = '\
<div if="{ !selectedTab}" class="jb3-paste-form-buttons">\
        <button class="c-button u-super c-button--block" data-tab="totoz" onclick="{ selectTab }">Totoz</button>\
        <button class="c-button u-super c-button--block" data-tab="emoji" onclick="{ selectTab }">Emoji</button>\
        <button class="c-button u-super c-button--block" data-tab="sketch" onclick="{ selectTab }">Image</button>\
        <button class="c-button u-super c-button--block" data-tab="text" onclick="{ selectTab }">Text</button>\
        <button class="c-button u-super c-button--block" data-tab="record" onclick="{ selectTab }">Record</button>\
        <button class="c-button u-super c-button--block" data-tab="file" onclick="{ selectTab }">File</button>\
</div>\
<jb3-paste-emoji if="{ selectedTab == \'emoji\' }"></jb3-paste-emoji>\
<jb3-paste-totoz if="{ selectedTab == \'totoz\' }"></jb3-paste-totoz>\
<jb3-paste-text if="{ selectedTab == \'text\' }"></jb3-paste-text>\
<jb3-paste-sketch if="{ selectedTab == \'sketch\' }"></jb3-paste-sketch>\
<jb3-paste-record if="{ selectedTab == \'record\' }"></jb3-paste-record>\
<jb3-paste-file if="{ selectedTab == \'file\' }"></jb3-paste-file>\
';

var jb3PasteFormStyles = '\
	.jb3-paste-form-buttons .c-button {\
		margin: 5px 0;\
	}\
';

riot.tag('jb3-paste-form', jb3PasteFormTemplate, jb3PasteFormStyles,
		jb3PasteFormConstructor);