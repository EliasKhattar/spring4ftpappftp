package fefo.springframeworkftp.spring4ftpapp.batchreaders;

import fefo.springframeworkftp.spring4ftpapp.model.ItemDetail;
import fefo.springframeworkftp.spring4ftpapp.model.Order;
import fefo.springframeworkftp.spring4ftpapp.model.OrderDetail;
import org.springframework.batch.item.*;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.support.SingleItemPeekableItemReader;
import org.springframework.batch.item.support.builder.SingleItemPeekableItemReaderBuilder;
import org.springframework.lang.Nullable;

import java.util.ArrayList;

public class OrderItemProcessingPeekReader implements ItemReader<Order>, ItemStream {

    private SingleItemPeekableItemReader<Order> peekableItemReader;

    private FlatFileItemReader fieldSetReader;
    private String filePath;

    public OrderItemProcessingPeekReader(FlatFileItemReader fieldSetReader, String filePath) {
        this.fieldSetReader = fieldSetReader;
        this.filePath = filePath;

        peekableItemReader = new SingleItemPeekableItemReaderBuilder<Order>().delegate(this.fieldSetReader).build();
    }


    @Nullable
    @Override
    public Order read() throws Exception{
        Order order = peekableItemReader.peek();
        if(order == null) {
            return null;
        }

        OrderDetail orderDetail = new OrderDetail();

        if(order instanceof OrderDetail) {
            orderDetail = (OrderDetail) peekableItemReader.read();
            orderDetail.setBranchCode(filePath.substring(5,8));
        }

         while(peekableItemReader.peek() != null && peekableItemReader.peek() instanceof ItemDetail){
             if (orderDetail.getItemDetails() == null) {
                 orderDetail.setItemDetails(new ArrayList<>());
             }

             ItemDetail itemDetail = (ItemDetail) peekableItemReader.read();
             itemDetail.setOrderDetails(orderDetail);
             orderDetail.getItemDetails().add(itemDetail);
        }

        return orderDetail;
        }


    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        peekableItemReader.open(executionContext);
        this.fieldSetReader.open(executionContext);
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        peekableItemReader.update(executionContext);
        this.fieldSetReader.update(executionContext);
    }

    @Override
    public void close() throws ItemStreamException {
        peekableItemReader.close();
        this.fieldSetReader.close();
    }
}
