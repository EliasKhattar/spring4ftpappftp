/*
package fefo.springframeworkftp.spring4ftpapp.batchreaders;

import fefo.springframeworkftp.spring4ftpapp.model.ItemDetail;
import fefo.springframeworkftp.spring4ftpapp.model.Order;
import fefo.springframeworkftp.spring4ftpapp.model.OrderDetail;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.batch.item.*;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.support.SingleItemPeekableItemReader;
import org.springframework.batch.item.support.builder.SingleItemPeekableItemReaderBuilder;
import org.springframework.lang.Nullable;

import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

import static org.apache.logging.log4j.LogManager.getLogger;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderItemProcessingItemReader extends SingleItemPeekableItemReader<Order> */
/*implements ItemReader<Order>, ItemStream*//*
{

    private static final Logger LOG = getLogger(OrderItemProcessingItemReader.class);

    //private OrderDetail orderDetail;
    private boolean recordFinished;
    private FlatFileItemReader fieldSetReader;
    private String filePath;

    public OrderItemProcessingItemReader(String filePath) {
        this.filePath = filePath;
    }


    @Nullable
    @Override
    public Order read() throws Exception {
        OrderDetail t = null;
      */
/*  while (!recordFinished){
            if ((Order) fieldSetReader.read() instanceof OrderDetail){
                t = (OrderDetail) fieldSetReader.read();
            }else if((Order) fieldSetReader.read() instanceof ItemDetail){
                if (t.getItemDetails() == null) {
                    t.setItemDetails(new ArrayList<>());
                }

                ItemDetail itemDetail = (ItemDetail) fieldSetReader.read();
                itemDetail.setOrderDetails(t);
                t.getItemDetails().add(itemDetail);
            }else if ((Order) super.peek() instanceof OrderDetail){
                return t;
            }*//*


        while (!recordFinished){
            if (super.read() instanceof OrderDetail){
                t = (OrderDetail) super.read();
            }else if(super.read() instanceof ItemDetail){
                if (t.getItemDetails() == null) {
                    t.setItemDetails(new ArrayList<>());
                }

                ItemDetail itemDetail = (ItemDetail)super.read();
                itemDetail.setOrderDetails(t);
                t.getItemDetails().add(itemDetail);
            }else if (super.peek() instanceof OrderDetail){
                return t;
            }


        }
        return null;
        */
/*recordFinished = false;
        while (!recordFinished){
            process((Order) fieldSetReader.read());

        }
        OrderDetail result = orderDetail;
        orderDetail = null;

        return result;*//*

    }

    */
/**
     * Processing the records coming from batch reading and mapping them correcting before
     * jpa writes them to the DB.
     *
     *//*


    */
/*private void process(Order object) throws Exception{

        // Finish processing if we hit the end of the file.
        if (object == null) {
            LOG.debug("FINISHED");
            recordFinished = true;
            return;
        }

        if (object instanceof OrderDetail){
            orderDetail = (OrderDetail) object;

        }else if (object instanceof ItemDetail) {

            if (orderDetail.getItemDetails() == null) {
                orderDetail.setItemDetails(new ArrayList<>());
            }

            ItemDetail itemDetail = (ItemDetail) object;
            itemDetail.setOrderDetails(orderDetail);
            orderDetail.getItemDetails().add(itemDetail);

        }

        }*//*



    public void setFieldSetReader(FlatFileItemReader<FieldSet> fieldSetReader) {
        this.fieldSetReader = fieldSetReader;
    }

*/
/*    @Override
    public void close() throws ItemStreamException {
        this.fieldSetReader.close();
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        this.fieldSetReader.open(executionContext);
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        this.fieldSetReader.update(executionContext);
    }*//*

    @Override
    public void close() throws ItemStreamException {
        // TODO Auto-generated method stub
        super.close();
    }

    @Override
    public void open(ExecutionContext arg0) throws ItemStreamException {
        // TODO Auto-generated method stub
        super.open(arg0);
    }

    @Override
    public void update(ExecutionContext arg0) throws ItemStreamException {
        // TODO Auto-generated method stub
        super.update(arg0);
    }
*/
/*
    @Override
    public void setResource(Resource arg0) {
        // TODO Auto-generated method stub
        super.setDelegate(delegate);
    }*//*

}
*/
