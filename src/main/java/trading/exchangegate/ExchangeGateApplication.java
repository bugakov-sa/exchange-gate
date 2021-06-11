package trading.exchangegate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import trading.exchangegate.consumer.FilteringMinutesConsumer;
import trading.exchangegate.consumer.LoggingConsumer;
import trading.exchangegate.gate.KrakenExchangeGate;
import trading.exchangegate.message.Event;

@SpringBootApplication
public class ExchangeGateApplication implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(ExchangeGateApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(ExchangeGateApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		log.info("Configuring gate");
		KrakenExchangeGate exchangeGate = new KrakenExchangeGate();
		exchangeGate.init();
		exchangeGate.setEventHandler(m -> {
			if(m.getEvent() == Event.HEARTBEAT) {
				return;
			}
			log.info("Event message: {}", m);
		});
		exchangeGate.subscribeOHLC("XBT/USD", new FilteringMinutesConsumer(new LoggingConsumer()));
		log.info("Gate configured");
	}
}
