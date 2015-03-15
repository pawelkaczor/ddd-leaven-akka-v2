/* emit(..) creates new event. linkTo should be used for creating pointers*/
fromAll().
    when({
        'ecommerce.sales.ReservationConfirmed' : function(s,e) {
            var metadata = JSON.parse(e.metadataRaw);
            var body = JSON.parse(e.bodyRaw);
            if (body.payload && !metadata.metadata.correlationId) {
                emit('invoicing', e.eventType, body.payload, metadata.metadata);
            }
        },
        'ecommerce.invoicing.PaymentReceived' : function(s,e) {
            var metadata = JSON.parse(e.metadataRaw);
            var body = JSON.parse(e.bodyRaw);
            if (body.payload && !metadata.metadata.correlationId) {
                emit('invoicing', e.eventType, body.payload, metadata.metadata);
            }
        }
    });