fromStreams(['$ce-Reservation', '$ce-Invoice']).
    when({
        'ecommerce.sales.ReservationConfirmed' : function(s,e) {
            var metadata = JSON.parse(e.metadataRaw);
            var body = JSON.parse(e.bodyRaw);
            if (body.payload && !metadata.metadata.correlationId) {
                linkTo('invoicing', e);
            }
        },
        'ecommerce.invoicing.PaymentReceived' : function(s,e) {
            var metadata = JSON.parse(e.metadataRaw);
            var body = JSON.parse(e.bodyRaw);
            if (body.payload && !metadata.metadata.correlationId) {
                linkTo('invoicing', e);
            }
        }
    });