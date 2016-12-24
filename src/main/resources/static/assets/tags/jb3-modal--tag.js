riot
        .tag(
                'jb3-modal',
                '\
				<div class="c-overlay" if="{ opts.visible }" onclick="{ hide }"></div>\
				<div class="o-modal o-modal--full"  if="{ opts.visible }">\
                                    <div class="c-card">\
                                        <header class="c-card__header">\
                                            <button type="button" class="c-button c-button--close" onclick="{ hide }">×</button>\
                                            <h3 class="c-heading c-heading--small">{ opts.title }</h3>\
                                        </header>\
                                        <div class="c-card__body o-panel">\
                                              <yield/>\
                                        </div>\
                                        <footer class="c-card__footer  c-card__footer--block">\
                                            <div class="c-input-group">\
                                                <button each="{ opts.buttons }" type="button" class="c-button c-button--block { \'c-button--\' + type }" onclick="{ action }" style="{ style }">\
                                                    { text }\
                                                </button>\
                                            </div>\
				 	</footer>\
                                    </div>\
				</div>\
',
                function (opts) {
                    var self = this;
                    this.show = function () {
                        this.opts.visible = true;
                        this.update();
                    };
                    this.hide = function () {
                        self.opts.visible = false;
                        self.update();
                    }
                });