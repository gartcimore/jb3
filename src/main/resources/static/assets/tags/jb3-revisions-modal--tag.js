riot
        .tag(
                'jb3-revisions-modal',
                '\
				<div class="c-overlay" if="{ opts.visible }" onclick="{ hide }"></div>\
				<div class="c-modal"  if="{ opts.visible }" style="height: 80%; overflow: scroll">\
				  <header class="c-modal__header">\
				    <button type="button" class="c-button c-button--close" onclick="{ hide }">×</button>\
				    <h3 class="c-heading c-heading--small">{ opts.title }</h3>\
				  </header>\
				  <div class="c-modal__body">\
				  	<jb3-raw content="{ opts.revisions }"></jb3-raw>\
				  </div>\
				</div>\
',
                function (opts) {
                    var self = this;
                    //self.raw = self.tags['jb3-raw'];
                    this.on('show', function (revisions) {
                        this.opts.revisions = revisions;
                        this.opts.visible = true;
                        this.update();
                        //  self.raw.content = revisions;
                        //self.raw.update();
                    });
                    this.hide = function () {
                        self.opts.visible = false;
                        self.update();
                    };
                });