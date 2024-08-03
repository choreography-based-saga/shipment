package com.saga.shipment.application.mapper;

import com.saga.shipment.application.controller.api.response.DeliveredShipment;
import com.saga.shipment.application.messaging.api.CreateShipment;
import com.saga.shipment.application.messaging.api.OrderEvent;
import com.saga.shipment.application.messaging.api.SuborderEvent;
import com.saga.shipment.application.messaging.api.SuborderItemEvent;
import com.saga.shipment.application.messaging.api.enums.ShipmentState;
import com.saga.shipment.domain.model.Suborder;
import com.saga.shipment.domain.model.Claim;
import com.saga.shipment.domain.model.DeliveredPackage;
import com.saga.shipment.domain.model.Order;
import com.saga.shipment.domain.model.Shipment;
import com.saga.shipment.domain.model.enums.ClaimStatusDomain;
import com.saga.shipment.domain.model.enums.PackageStatus;
import com.saga.shipment.domain.model.enums.ShipmentDomainStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper
public interface ShipmentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "claim", source = "createShipmentRequest", qualifiedByName = "linkClaim")
    @Mapping(target = "packageId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "senderId", source = "customerId")
    @Mapping(target = "recipientId", ignore = true)
    Shipment fromMessage(CreateShipment createShipmentRequest);

    @Named("linkClaim")
    default Claim linkClaim(CreateShipment shipment) {
        return new Claim(shipment.claimId(), null, ClaimStatusDomain.valueOf(shipment.status().name()));
    }

    ShipmentDomainStatus fromMessageStatus(ShipmentState state);

    @Mapping(target = "delivered", source = "pack.status", qualifiedByName = "isDelivered")
    DeliveredShipment toResponse(DeliveredPackage pack);

    @Named("isDelivered")
    default boolean isDelivered(PackageStatus status) {
        return PackageStatus.DELIVERED.equals(status);
    }

    Order fromMessage(OrderEvent orderEvent);

    Suborder fromMessage(SuborderEvent suborderEvent);

    SuborderItemEvent fromMessage(SuborderItemEvent suborderItem);
}
