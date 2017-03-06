function jb3RevisionsModalConstructor(opts) {
    var self = this;
    this.on('show', function (revisions) {
        this.opts.revisions = revisions;
        this.opts.visible = true;
        this.update();
    });
    this.hide = function () {
        self.opts.visible = false;
        self.update();
    };
}

var jb3RevisionsModalTemplate = '\
<div class="c-overlay" if="{ opts.visible }" onclick="{ hide }"></div>\
<div class="o-modal"  if="{ opts.visible }" style="height: 80%; overflow: scroll">\
    <header class="o-modal__header">\
        <button type="button" class="c-button c-button--close" onclick="{ hide }">×</button>\
        <h3 class="c-heading c-heading--small">{ opts.title }</h3>\
    </header>\
    <div class="o-modal__body">\
        <jb3-raw content="{ opts.revisions }"></jb3-raw>\
    </div>\
</div>\
';

riot.tag('jb3-revisions-modal', jb3RevisionsModalTemplate, jb3RevisionsModalConstructor);