var jb3VisioRemoteVideoTemplate = '\
<video width="320" height="240" autoplay controls></video>\
<div>\
    <span class="c-badge">{ opts.nickname }</span>\
</div>\
';
function jb3VisioRemoteVideoConstructor(opts) {
    this.on('update', function () {
        var remoteVideo = this.root.querySelector('video');
        if (remoteVideo) {
            remoteVideo.srcObject = this.opts.stream;
        }
    });
}
;
riot.tag('jb3-visio-remote-video',
        jb3VisioRemoteVideoTemplate,
        jb3VisioRemoteVideoConstructor);