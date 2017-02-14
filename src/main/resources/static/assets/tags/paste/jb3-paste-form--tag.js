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
				<jb3-paste-image></jb3-paste-image>\
			</div>\
                        <div class="c-tabs__tab { \'c-tabs__tab--active\': selectedTab == \'sketch\' }">\
                            <jb3-paste-sketch></jb3-paste-sketch>\
			</div>\
                        <div class="c-tabs__tab { \'c-tabs__tab--active\': selectedTab == \'record\' }">\
                            <jb3-paste-record></jb3-paste-record>\
                        </div>\
			<div class="c-tabs__tab { \'c-tabs__tab--active\': selectedTab == \'file\' }">\
                            <jb3-paste-file></jb3-paste-file>\
			</div>\
		</div>\
',
                '',
                function(opts) {
                var self = this;
                        self.pasteText = self.tags['jb3-paste-text'];
                        self.pasteFile = self.tags['jb3-paste-file'];
                        self.pasteImage = self.tags['jb3-paste-image'];
                        self.pasteRecord = self.tags['jb3-paste-record'];
                        self.pasteSketch = self.tags['jb3-paste-sketch'];
                        this.selectedTab = 'text';
                        self.selectTab = function(e) {
                        self.selectedTab = e.target.dataset.tab
                        };
                        self.clear = function() {
                        self.pasteText.clear();
                                self.pasteFile.clear();
                                self.pasteImage.clear();
                                self.pasteRecord.clear();
                                self.pasteSketch.clear();
                                this.update();
                        }
                });