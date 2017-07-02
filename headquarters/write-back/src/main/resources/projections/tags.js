fromAll()
    .when({
        $any: function(s,e){
            if (!e.linkMetadataRaw && e.metadataRaw && e.metadataRaw.includes("tags")) {
                var md = JSON.parse(e.metadataRaw);
                if (md.content && md.content.tags) {
                    var tags = md.content.tags;
                    for (var i = 0; i < tags.length; i++) {
                        linkTo(tags[i], e);
                    }
                }
            }
        }
    })