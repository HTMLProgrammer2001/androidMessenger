package htmlprogrammer.labs.messanger.constants;

public enum MessageTypes {
    TEXT(0);

    private int value;

    private MessageTypes(int val){
        value = val;
    }

    public int getValue(){
        return value;
    }

    public static MessageTypes fromInt(int type){
        MessageTypes[] values = MessageTypes.values();

        for (MessageTypes value1 : values) {
            if (value1.getValue() == type) {
                return value1;
            }
        }

        return null;
    }
}
