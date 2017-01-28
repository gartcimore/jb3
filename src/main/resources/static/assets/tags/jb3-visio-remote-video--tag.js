var jb3VisioRemoteVideoTemplate='\
<video name="remoteVideo" id="{ opts.name }" src="{ opts.stream }" width="320" height="240" autoplay controls></video>\
<div>\
    <span class="c-badge">{ opts.nickname }</span>\
</div>\
';
function jb3VisioRemoteVideoConstructor(opts) {
};
riot.tag('jb3-visio-remote-video',
        jb3VisioRemoteVideoTemplate,
        jb3VisioRemoteVideoConstructor);