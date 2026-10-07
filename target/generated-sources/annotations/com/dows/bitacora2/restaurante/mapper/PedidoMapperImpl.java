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
    date = "2026-10-07T17:30:31-0500",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.12.1 (Eclipse Adoptium)"
)
@Component
public class PedidoMapperImpl implements PedidoMapper {

    @Override
    public Pedido toDomain(PedidoRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Pedido pedido = new Pedido();

        pedido.setIdMesa( dto.idMesa() );
        pedido.setItems( itemPedidoRequestDTOListToItemPedidoList( dto.items() ) );

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

        itemPedido.setIdPlato( dto.idPlato() );
        itemPedido.setCantidad( dto.cantidad() );

        return itemPedido;
    }

    @Override
    public PedidoResponseDTO toResponse(Pedido pedido) {
        if ( pedido == null ) {
            return null;
        }

        Long id = null;
        Long idMesa = null;
        String estado = null;
        List<ItemPedidoResponseDTO> items = null;

        id = pedido.getId();
        idMesa = pedido.getIdMesa();
        if ( pedido.getEstado() != null ) {
            estado = pedido.getEstado().name();
        }
        items = itemPedidoListToItemPedidoResponseDTOList( pedido.getItems() );

        Double total = calcularTotalPedido(pedido);

        PedidoResponseDTO pedidoResponseDTO = new PedidoResponseDTO( id, idMesa, estado, total, items );

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

        Long idPlato = null;
        String nombrePlato = null;
        Integer cantidad = null;

        idPlato = item.getIdPlato();
        nombrePlato = item.getNombrePlato();
        cantidad = item.getCantidad();

        Double subtotal = item.subtotal();
        String notas = null;

        ItemPedidoResponseDTO itemPedidoResponseDTO = new ItemPedidoResponseDTO( idPlato, nombrePlato, cantidad, subtotal, notas );

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
