riot.tag(
				'jb3-room-editor',
'<fieldset class="c-fieldset">\
		<h2 class="c-heading c-heading--small" if="{ rname.value }"><a class="c-link" href="/?room={ rname.value }">{ rname.value }</a></h2>\
                <form action="/dlfp/connect" class="o-form-element" if="{ rname.value == \'dlfp\' }">\
                        <input type="submit" class="c-button c-button--info" value="Connect & authorize">\
                </form>\
		<div class="c-input-group" if="{ !rname.value }">\
                    <input name="rname" class="c-field" type="text" placeholder="Type new room\'s name">\
                    <button if="{ !rname.value }" class="c-button c-button--info" onclick="{ save }">Create new room</button>\
		</div>\
		<div class="o-form-element" if="{ !rname.value }" >\
                    <button class="c-button c-button--warning" onclick="{ resetAll }">Reset all rooms</button>\
		</div>\
		<button if="{ rname.value }" class="c-button" onclick="{ cancel }">Cancel</button>\
		<button if="{ rname.value }" class="c-button c-button--warning" onclick="{ del }">Delete</button>\
</fieldset>\
',
				function(opts) {
					var self = this;
					this.on('edit-room', function(room) {
						this.rname.value = room.rname;
						this.update();
					});
					this.save = function(e) {
						if(this.rname.value) {
							this.trigger('save-room', {
								rname : this.rname.value
							});
							this.rname.value = '';
							this.update();
						}
					}
					this.cancel = function(e) {
						this.trigger('cancel-edit-room');
						this.rname.value = '';
						this.update();
					}
					this.resetAll = function(e) {
						this.trigger('reset-all-rooms');
						this.rname.value = '';
						this.update();
					}
					this.del = function(e) {
						this.trigger('delete-room', {
							rname : this.rname.value,
						});
						this.rname.value = '';
						this.update();
					}
				});