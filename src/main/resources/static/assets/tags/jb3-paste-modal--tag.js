riot
		.tag(
				'jb3-paste-modal',
				'\
				<jb3-modal title="Attach" buttons="{ buttons }">\
					<jb3-paste-form></jb3-paste-form>\
				</jb3-modal>\
',
				function(opts) {
					var self = this;
					self.modal = self.tags['jb3-modal'];
					self.pasteForm = self.modal.tags['jb3-paste-form'];
					self.buttons = [ {
						type : "info",
						action : function() {
							var pastedUrl = self.pasteForm.getPasted();
							if (pastedUrl) {
								self.trigger('pasted', pastedUrl);
								self.pasteForm.clear();
								self.modal.hide();
							}
						},
						text : "Attach"
					}, {
						type : "warning",
						action : function() {
							self.pasteForm.clear();
							self.modal.hide();
						},
						text : "Cancel"
					} ];
					this.on('show', function() {
						self.tags['jb3-modal'].show();
					});
				});