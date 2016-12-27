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
					self.buttons = [
							{
								type : "info",
								action : function() {
									var pastedUrls = $(self.root).find('.c-tabs__tab--active').find(
											' .jb3-pasted-url').map(
											function(_, e) {
												return e.getAttribute('href');
											}).get();
									self.trigger('pasted', pastedUrls.join(" "));
									self.pasteForm.clear();
									self.modal.hide();
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