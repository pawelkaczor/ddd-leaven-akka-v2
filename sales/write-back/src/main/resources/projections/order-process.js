fromStreams(['$ce-Reservation', '$ce-Invoice']).
    when({
        'ecommerce.sales.ReservationConfirmed' : function(s,e) {
            var metadata = JSON.parse(e.metadataRaw);
            var body = JSON.parse(e.bodyRaw);
            if (body.payload && !metadata.metadata.correlationId) {
                linkTo('sales', e);
            }
        },
        'ecommerce.invoicing.OrderBilled' : function(s,e) {
            var metadata = JSON.parse(e.metadataRaw);
            var body = JSON.parse(e.bodyRaw);
            if (body.payload && !metadata.metadata.correlationId) {
                linkTo('sales', e);
            }
        },
        'ecommerce.invoicing.OrderBillingFailed' : function(s,e) {
            var metadata = JSON.parse(e.metadataRaw);
            var body = JSON.parse(e.bodyRaw);
            if (body.payload && !metadata.metadata.correlationId) {
                linkTo('sales', e);
            }
        }
    });