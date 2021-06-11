package trading.exchangegate.consumer;

import trading.exchangegate.message.OhlcMessage;

import java.util.concurrent.atomic.AtomicReference;

public class FilteringMinutesConsumer  implements OhlcConsumer {

    private final AtomicReference<OhlcMessage> lastSentMessage = new AtomicReference<>(null);
    private final OhlcConsumer nextConsumer;
    private volatile OhlcMessage prevMessage = null;

    public FilteringMinutesConsumer(OhlcConsumer nextConsumer) {
        this.nextConsumer = nextConsumer;
    }

    private boolean etimeChanged(OhlcMessage ohlcMessage) {
        return prevMessage != null && prevMessage.getEtime() != ohlcMessage.getEtime();
    }

    @Override
    public void accept(OhlcMessage ohlcMessage) {
        if(etimeChanged(ohlcMessage)) {
            if(!lastSentMessage.compareAndSet(prevMessage, prevMessage)) {
                nextConsumer.accept(prevMessage);
            }
        }
        prevMessage = ohlcMessage;
    }
}
