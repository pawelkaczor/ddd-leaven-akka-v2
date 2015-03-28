fromStreams(['clock', '$ce-deadlines']).
    when({
        '$init' : function(s,e) {
            return { "deadlines": []}
        },
        'pl.newicom.dddd.scheduling.EventScheduled' : function(s,e) {
            s.deadlines[s.deadlines.length] = e;
        },
        'tick' : function(s,e) {
            for(var i = s.deadlines.length -1; i >= 0; i--) {
                var eventScheduledEnvelope = s.deadlines[i];
                var eventScheduled = JSON.parse(eventScheduledEnvelope.bodyRaw);
                var deadlineMillis = eventScheduled.payload.deadlineMillis;
                var currentTimeMillis = JSON.parse(e.bodyRaw).timeMillis
                var businessUnit = eventScheduled.payload.businessUnit
                if (currentTimeMillis >= deadlineMillis) {
                    s.deadlines.splice(i, 1);
                    linkTo('currentDeadlines-' + businessUnit, eventScheduledEnvelope);
                }
            }
        }
    });