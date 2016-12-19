riot.tag('jb3-raw', '<span></span>', function (opts) {
    this.updateContent = function () {
        this.root.innerHTML = opts.content;
    };

    this.on('update', function () {
        this.updateContent();
    });

    this.updateContent();
});
