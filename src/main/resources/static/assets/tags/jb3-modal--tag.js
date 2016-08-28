riot
		.tag(
				'jb3-modal',
				'\
				<div class="c-overlay" if="{ opts.visible }" onclick="{ hide }"></div>\
				<div class="c-modal"  if="{ opts.visible }" style="height: 80%; overflow: scroll">\
				  <header class="c-modal__header">\
				    <button type="button" class="c-button c-button--close" onclick="{ hide }">×</button>\
				    <h3 class="c-heading c-heading--small">{ opts.title }</h3>\
				  </header>\
				  <div class="c-modal__body">\
				  	<yield/>\
				  </div>\
				  <footer class="c-modal__footer  c-modal__footer--block">\
				 	<button each="{ opts.buttons }" type="button" class="c-button { \'c-button--\' + type }" onclick="{ action }" style="{ style }">\
				 		{ text }\
				 	</button>\
				 	</footer>\
				</div>\
',
				function(opts) {
					var self = this;
					this.show = function() {
						this.opts.visible = true;
						this.update();
					};
					this.hide = function() {
							self.opts.visible = false;
							self.update();
					}
				});