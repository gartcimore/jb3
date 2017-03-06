function jb3ModalConstructor(opts) {
    var self = this;
    this.show = function () {
        this.opts.visible = true;
        this.update();
    };
    this.hide = function () {
        self.opts.visible = false;
        self.update();
    };
}

var jb3ModalTemplate = '\
<div class="c-overlay" if="{ opts.visible }" onclick="{ hide }"></div>\
<div class="o-modal o-modal--full"  if="{ opts.visible }">\
    <div class="c-card">\
        <header class="c-card__header" if="{ opts.showHeader }">\
            <button type="button" class="c-button c-button--close" onclick="{ hide }">×</button>\
            <h3 class="c-heading c-heading--small">{ opts.title }</h3>\
        </header>\
        <div class="{\'jb3-modal-headerless-body\':!opts.showHeader, \'c-card__body\':true, \'o-panel\':true }">\
              <yield from="body"/>\
        </div>\
        <footer class="c-card__footer  c-card__footer--block">\
            <yield from="footer"/>\
        </footer>\
    </div>\
</div>\
';

var jb3ModalStyles = '\
	.jb3-modal-headerless-body {\
		top: 0 !important;\
                bottom: 2.5em !important;\
	}\
';

riot
        .tag(
                'jb3-modal',
                jb3ModalTemplate,
                jb3ModalStyles,
                jb3ModalConstructor);