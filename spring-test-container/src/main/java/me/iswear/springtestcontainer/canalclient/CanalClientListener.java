package me.iswear.springtestcontainer.canalclient;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import me.iswear.springcanalclient.annotation.CanalListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CanalClientListener {


    @CanalListener
    public void onDataBaseUpdated(Message message) {
        List<CanalEntry.Entry> entries =  message.getEntries();
        for (CanalEntry.Entry entry : entries) {
            System.out.println("hello world");
        }
    }

}
