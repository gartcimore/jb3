riot
		.tag(
				'jb3-paste-modal',
				'\
				<jb3-modal title="Attach" buttons="{ buttons }">\
                    <yield to="body">\
						<jb3-paste-form></jb3-paste-form>\
                    </yield>\
					<yield to="footer">\
			            <div class="c-input-group">\
			                <button class="c-button c-button--block c-button--info" onclick="{ parent.attach }" disabled="{ !parent.pasteForm.getPasted() }">Attach</button>\
							<button class="c-button c-button--block c-button--warning" onclick="{ parent.cancel }">Cancel</button>\
						</div>\
					</yield>\
				</jb3-modal>\
',
				function(opts) {
					var self = this;
					self.modal = self.tags['jb3-modal'];
					self.pasteForm = self.modal.tags['jb3-paste-form'];
					self.pasteForm.on('paste-content-changed', function() {
						self.trigger('paste-content-changed');
						self.update();
					});
					self.attach = function() {
						var pastedUrl = self.pasteForm.getPasted();
						if (pastedUrl) {
							self.trigger('pasted', pastedUrl);
							self.pasteForm.clear();
							self.modal.hide();
						}
					};
					self.cancel = function() {
						self.pasteForm.clear();
						self.modal.hide();
					};
					this.on('show', function() {
						self.tags['jb3-modal'].show();
					});
				});