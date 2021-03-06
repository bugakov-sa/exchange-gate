package trading.engine;

import trading.message.Message;
import trading.message.OhlcData;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StateHolder {

    private final List<StateConfig> configs;

    private State state;

    private boolean stateChanged = false;
    private OhlcData lastMessage = null;

    public StateHolder(State state, List<StateConfig> configs) {
        this.state = state;
        this.configs = configs;
    }

    public State getState() {
        return state;
    }

    public boolean isStateChanged() {
        return stateChanged;
    }

    public void update(List<Message> newMessages) {
        stateChanged = false;
        final State.Builder stateBuilder = new State.Builder();
        for (StateConfig config : configs) {
            final String pair = config.getPair();
            final int bufferSize = config.getBufferSize();
            final List<OhlcData> newPairMessages = filterOhlcMessages(newMessages, pair);
            switch (config.getType()) {
                case LAST_VALUE:
                    OhlcData newLastValue = calcLastValue(pair, newPairMessages);
                    stateBuilder.setLastValue(pair, newLastValue);
                    break;
                case MINUTE_VALUES:
                    List<OhlcData> messages = calcMinutesValues(pair, bufferSize, newPairMessages);
                    stateBuilder.setMinutesValues(pair, messages);
                    break;
            }
        }
        if(stateChanged) {
            state = stateBuilder.build();
        }
    }

    private List<OhlcData> filterOhlcMessages(List<Message> newMessages, String pair) {
        return newMessages.stream()
                .filter(m -> m.getType() == Message.Type.OHLC_DATA)
                .map(m -> (OhlcData)m)
                .filter(m -> m.getPair().equals(pair))
                .collect(Collectors.toList());
    }

    private OhlcData calcLastValue(String pair, List<OhlcData> messages) {
        if(messages.isEmpty()) {
            return state.getLastValue(pair);
        }
        else {
            stateChanged = true;
            return messages.get(messages.size() - 1);
        }
    }

    private List<OhlcData> calcMinutesValues(String pair, int bufferSize, List<OhlcData> messages) {
        List<OhlcData> minuteValues = new ArrayList<>(state.getMinuteValues(pair));
        if(messages.isEmpty()) {
            return minuteValues;
        }
        List<OhlcData> newMinuteValues = new ArrayList<>();
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
