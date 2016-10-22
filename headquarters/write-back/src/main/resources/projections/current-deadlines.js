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

                var eventScheduledMetadata = JSON.parse(eventScheduledEnvelope.metadataRaw);
                var eventScheduled = JSON.parse(eventScheduledEnvelope.bodyRaw);

                var deadlineMillis = eventScheduled.payload.metadata.deadlineMillis;
                var currentTimeMillis = JSON.parse(e.bodyRaw).timeMillis;
                var businessUnit = eventScheduled.payload.metadata.businessUnit;
                var target = eventScheduled.payload.metadata.target;

                var deadlineEventPayload = eventScheduled.payload.event;
                var deadlineEventClass = eventScheduled.payload.eventClass;

                var deadlineEvent = eventScheduled;
                deadlineEvent.payload = deadlineEventPayload;
                deadlineEvent.payload.jsonClass = deadlineEventClass;

                var deadlineEventMetadata = eventScheduledMetadata;
                deadlineEventMetadata.content.target = target;

                if (currentTimeMillis >= deadlineMillis) {
                    s.deadlines.splice(i, 1);
                    emit('currentDeadlines-' + businessUnit, deadlineEventClass, deadlineEvent, deadlineEventMetadata);
                }
            }
        }
    });