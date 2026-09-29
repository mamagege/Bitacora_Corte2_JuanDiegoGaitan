package com.dows.bitacora2.restaurante.mapper;

import com.dows.bitacora2.restaurante.model.domain.ItemPedido;
import com.dows.bitacora2.restaurante.model.domain.Pedido;
import com.dows.bitacora2.restaurante.model.dto.request.ItemPedidoRequestDTO;
import com.dows.bitacora2.restaurante.model.dto.request.PedidoRequestDTO;
import com.dows.bitacora2.restaurante.model.dto.response.ItemPedidoResponseDTO;
import com.dows.bitacora2.restaurante.model.dto.response.PedidoResponseDTO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-09-29T00:08:49-0500",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 25.0.2 (Oracle Corporation)"
)
@Component
public class PedidoMapperImpl implements PedidoMapper {

    @Override
    public Pedido toDomain(PedidoRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Pedido pedido = new Pedido();

        pedido.setIdMesa( dto.getIdMesa() );
        pedido.setItems( itemPedidoRequestDTOListToItemPedidoList( dto.getItems() ) );

        pedido.setEstado( com.dows.bitacora2.restaurante.model.domain.EstadoPedido.RECIBIDO );
        pedido.setTimestamp( java.time.LocalDateTime.now() );

        return pedido;
    }

    @Override
    public ItemPedido toDomain(ItemPedidoRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        ItemPedido itemPedido = new ItemPedido();

        itemPedido.setIdPlato( dto.getIdPlato() );
        itemPedido.setCantidad( dto.getCantidad() );
        itemPedido.setMasa( dto.getMasa() );
        itemPedido.setSalsa( dto.getSalsa() );
        List<String> list = dto.getToppings();
        if ( list != null ) {
            itemPedido.setToppings( new ArrayList<String>( list ) );
        }

        return itemPedido;
    }

    @Override
    public PedidoResponseDTO toResponse(Pedido pedido) {
        if ( pedido == null ) {
            return null;
        }

        PedidoResponseDTO pedidoResponseDTO = new PedidoResponseDTO();

        pedidoResponseDTO.setId( pedido.getId() );
        pedidoResponseDTO.setIdMesa( pedido.getIdMesa() );
        pedidoResponseDTO.setItems( itemPedidoListToItemPedidoResponseDTOList( pedido.getItems() ) );
        if ( pedido.getEstado() != null ) {
            pedidoResponseDTO.setEstado( pedido.getEstado().name() );
        }
        pedidoResponseDTO.setTimestamp( pedido.getTimestamp() );

        pedidoResponseDTO.setTotalPedido( calcularTotalPedido(pedido) );

        return pedidoResponseDTO;
    }

    @Override
    public List<PedidoResponseDTO> toResponseList(List<Pedido> pedidos) {
        if ( pedidos == null ) {
            return null;
        }

        List<PedidoResponseDTO> list = new ArrayList<PedidoResponseDTO>( pedidos.size() );
        for ( Pedido pedido : pedidos ) {
            list.add( toResponse( pedido ) );
        }

        return list;
    }

    @Override
    public ItemPedidoResponseDTO toResponse(ItemPedido item) {
        if ( item == null ) {
            return null;
        }

        ItemPedidoResponseDTO itemPedidoResponseDTO = new ItemPedidoResponseDTO();

        itemPedidoResponseDTO.setIdPlato( item.getIdPlato() );
        itemPedidoResponseDTO.setNombrePlato( item.getNombrePlato() );
        itemPedidoResponseDTO.setPrecioCongelado( item.getPrecioCongelado() );
        itemPedidoResponseDTO.setCantidad( item.getCantidad() );
        itemPedidoResponseDTO.setMasa( item.getMasa() );
        itemPedidoResponseDTO.setSalsa( item.getSalsa() );
        List<String> list = item.getToppings();
        if ( list != null ) {
            itemPedidoResponseDTO.setToppings( new ArrayList<String>( list ) );
        }

        itemPedidoResponseDTO.setSubtotal( item.subtotal() );

        return itemPedidoResponseDTO;
    }

    protected List<ItemPedido> itemPedidoRequestDTOListToItemPedidoList(List<ItemPedidoRequestDTO> list) {
        if ( list == null ) {
            return null;
        }

        List<ItemPedido> list1 = new ArrayList<ItemPedido>( list.size() );
        for ( ItemPedidoRequestDTO itemPedidoRequestDTO : list ) {
            list1.add( toDomain( itemPedidoRequestDTO ) );
        }

        return list1;
    }

    protected List<ItemPedidoResponseDTO> itemPedidoListToItemPedidoResponseDTOList(List<ItemPedido> list) {
        if ( list == null ) {
            return null;
        }

        List<ItemPedidoResponseDTO> list1 = new ArrayList<ItemPedidoResponseDTO>( list.size() );
        for ( ItemPedido itemPedido : list ) {
            list1.add( toResponse( itemPedido ) );
        }

        return list1;
    }
}
