package trading.message.handler;

import trading.message.Message;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public interface MessageRouter extends Function<Message, List<Queue<Message>>> {

    static Builder builder() {
        return new Builder();
    }

    class Builder {

        private List<Predicate<Message>> predicates = new ArrayList<>();
        private List<List<Queue<Message>>> queues = new ArrayList<>();

        public Builder when(Predicate<Message> predicate) {
            predicates.add(predicate);
            return this;
        }

        public Builder whenType(Message.Type ... type) {
            predicates.add(message -> Arrays.asList(type).contains(message.getType()));
            return this;
        }

        public Builder then(Queue<Message> ... queue) {
            queues.add(Arrays.asList(queue));
            return this;
        }

        public MessageRouter build() {
            return message -> {
                for(int i = 0; i < predicates.size(); i++) {
                    if(predicates.get(i).test(message)) {
                        return queues.get(i);
                    }
                }
                return Collections.EMPTY_LIST;
            };
        }
    }
}
