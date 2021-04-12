package htmlprogrammer.labs.messanger.constants;

public enum DialogTypes {
    PERSONAL(1),
    CHAT(2);

    private int value;

    private DialogTypes(int val){
        value = val;
    }

    public int getValue(){
        return value;
    }

    public static DialogTypes fromInt(int type){
        DialogTypes[] values = DialogTypes.values();

        for (DialogTypes value1 : values) {
            if (value1.getValue() == type) {
                return value1;
            }
        }

        return null;
    }
}
