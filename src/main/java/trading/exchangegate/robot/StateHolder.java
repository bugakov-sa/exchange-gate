package trading.exchangegate.robot;

import trading.exchangegate.message.OhlcMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StateHolder {

    private final List<ConfigItem> configs;

    private State state;

    private boolean stateChanged = false;
    private OhlcMessage lastMessage = null;

    public StateHolder(State state, List<ConfigItem> configs) {
        this.state = state;
        this.configs = configs;
    }

    public State getState() {
        return state;
    }

    public boolean isStateChanged() {
        return stateChanged;
    }

    public void update(List<OhlcMessage> newMessages) {
        stateChanged = false;
        final State.Builder stateBuilder = new State.Builder();
        for (ConfigItem config : configs) {
            final String pair = config.getPair();
            final int bufferSize = config.getBufferSize();
            final List<OhlcMessage> newPairMessages = filterMessages(newMessages, pair);
            switch (config.getType()) {
                case LAST_VALUE:
                    OhlcMessage newLastValue = calcLastValue(pair, newPairMessages);
                    stateBuilder.setLastValue(pair, newLastValue);
                    break;
                case MINUTE_VALUES:
                    List<OhlcMessage> messages = calcMinutesValues(pair, bufferSize, newPairMessages);
                    stateBuilder.setMinutesValues(pair, messages);
                    break;
            }
        }
        if(stateChanged) {
            state = stateBuilder.build();
        }
    }

    private List<OhlcMessage> filterMessages(List<OhlcMessage> newMessages, String pair) {
        return newMessages.stream()
                .filter(m -> m.getPair().equals(pair))
                .collect(Collectors.toList());
    }

    private OhlcMessage calcLastValue(String pair, List<OhlcMessage> messages) {
        if(messages.isEmpty()) {
            return state.getLastValue(pair);
        }
        else {
            stateChanged = true;
            return messages.get(messages.size() - 1);
        }
    }

    private List<OhlcMessage> calcMinutesValues(String pair, int bufferSize, List<OhlcMessage> messages) {
        List<OhlcMessage> minuteValues = new ArrayList<>(state.getMinuteValues(pair));
        if(messages.isEmpty()) {
            return minuteValues;
        }
        List<OhlcMessage> newMinuteValues = new ArrayList<>();
        int i = 0;
        if (lastMessage == null) {
            lastMessage = messages.get(0);
            i = 1;
        }
        for (; i < messages.size(); i++) {
            if(messages.get(i).getEtime() != lastMessage.getEtime()) {
                newMinuteValues.add(lastMessage);
                stateChanged = true;
            }
            lastMessage = messages.get(i);
        }
        minuteValues.addAll(newMinuteValues);
        if(minuteValues.size() > bufferSize) {
            minuteValues = minuteValues.subList(minuteValues.size() - bufferSize, minuteValues.size());
        }
        return minuteValues;
    }
}
