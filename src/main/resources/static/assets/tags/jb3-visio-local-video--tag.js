var jb3VisioLocalVideoTemplate='\
<video name="localVideo" src="{ opts.stream }" width="320" height="240" autoplay controls muted></video>\
<div>\
    <span class="c-badge">{ opts.nickname }</span>\
</div>\
';
function jb3VisioLocalVideoConstructor(opts) {
};
riot.tag('jb3-visio-local-video',
        jb3VisioLocalVideoTemplate,
        jb3VisioLocalVideoConstructor);