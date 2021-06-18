package trading.entity;

import lombok.Data;

@Data
public class ParamDescriptor {
    private final String name;
    private final Param.Type type;
    private final String defaultValue;

    private ParamDescriptor(String name, Param.Type type, String defaultValue) {
        this.name = name;
        this.type = type;
        this.defaultValue = defaultValue;
    }

    public static ParamDescriptor string(String name) {
        return new ParamDescriptor(name, Param.Type.STRING, null);
    }
}
