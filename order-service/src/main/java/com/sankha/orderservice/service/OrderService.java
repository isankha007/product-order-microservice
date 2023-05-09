package com.sankha.orderservice.service;

import com.sankha.orderservice.dto.InventoryResponse;
import com.sankha.orderservice.dto.OrderLineItemsDto;
import com.sankha.orderservice.dto.OrderRequest;
import com.sankha.orderservice.entity.Order;
import com.sankha.orderservice.entity.OrderLineItems;
import com.sankha.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final WebClient.Builder webClient;

    public String placeOrder(OrderRequest orderRequest){
        Order order=new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(this::mapToDto)
                .toList();


        order.setOrderLineItemList(orderLineItems);

        List<String> skuCodes = order.getOrderLineItemList().stream()
                .map(OrderLineItems::getSkuCode)
                .toList();
        InventoryResponse[] inventoryResponseArray = webClient.build().get()
                .uri("http://inventory-service/api/inventory",uriBuilder -> uriBuilder.queryParam("skuCodes",skuCodes).build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();

        boolean result = Arrays.stream(inventoryResponseArray)
                .allMatch(InventoryResponse::isInStock);
        if(result) {
            orderRepository.save(order);
            return  "Order Placed Successfully";
        }else{
            throw  new IllegalArgumentException("Product is not in stock...");
        }

     }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        return orderLineItems;
    }

}
